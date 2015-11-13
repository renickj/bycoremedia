package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapBlobRef;
import com.coremedia.rest.cap.blob.metadata.BlobMetadataResolver;
import com.coremedia.rest.linking.EntityResourceLinker;
import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class ZipMetadataResolver implements BlobMetadataResolver {
  private static final Log LOG = LogFactory.getLog(ZipMetadataResolver.class);

  private static final String JSON_LABEL_PROPERTY = "label";
  private static final String JSON_FILES_PROPERTY = "files";

  private static final String MAVEN_PROPERTIES_REGEXP = "META-INF/maven/(.+)/(.+)/pom.properties";
  private static final Pattern MAVEN_PROPERTIES_PATTERN = Pattern.compile(MAVEN_PROPERTIES_REGEXP);
  private static final String SLASH = "/";

  private String name;
  private EntityResourceLinker entityResourceLinker;

  @Override
  @Nonnull
  public String getName() {
    return name;
  }

  @Override
  public boolean canHandle(@Nonnull Blob blob) {
    return MimeTypeHelper.isZip(blob.getContentType());
  }

  @Required
  public void setName(String name) {
    Preconditions.checkNotNull(name, "name must not be null");
    this.name = name;
  }

  @Required
  public void setEntityResourceLinker(EntityResourceLinker entityResourceLinker) {
    Preconditions.checkNotNull(entityResourceLinker, "entityResourceLinker must not be null");
    this.entityResourceLinker = entityResourceLinker;
  }

  @Override
  public Object resolveMetadata(@Nonnull CapBlobRef blob) {
    InputStream blobStream = blob.getInputStream();
    try {
      ZipInputStream zipStream = new ZipInputStream(blobStream);
      ZipEntry entry;
      Map<String, Object> result = new LinkedHashMap<>();
      List<ZipFileEntry> files = new ArrayList<>();
      try {
        while ((entry = zipStream.getNextEntry()) != null) {
          // assuming that entry#name is a relative path
          String path = entry.getName();
          if (path.endsWith(SLASH)) {
            path = path.substring(0, path.length() - 1);
          }
          String name = getName(path);

          final ZipFileEntry zipFileEntry = createZipFileEntry(name, path, new Date(entry.getTime()), entry.getSize(), entry.isDirectory(), blob);
          files.add(zipFileEntry);

          Matcher matcher = MAVEN_PROPERTIES_PATTERN.matcher(path);
          if (matcher.matches()) {
            Properties properties = new Properties();
            properties.load(zipStream);
            String version = properties.getProperty("version", "");
            String groupId = properties.getProperty("groupId", "");
            String artifactId = properties.getProperty("artifactId", "");
            result.put(JSON_LABEL_PROPERTY, groupId + ":" + artifactId + ":" + version);
          }
          zipStream.closeEntry();
        }
      } finally {
        try {
          zipStream.close();
        } catch (IOException e) {
          LOG.warn("Error closing zip stream", e);
        }
      }
      result.put(JSON_FILES_PROPERTY, fileHierarchy(files, blob));
      return result;
    } catch (ZipException e) {
      // zip file format error?
      LOG.error("Exception while reading zip stream", e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    } catch (IOException e) {
      LOG.error("Exception while reading blob stream", e);
      throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
    } finally {
      try {
        blobStream.close();
      } catch (IOException e) {
        LOG.warn("Error closing blob stream", e);
      }
    }
  }

  private static String getName(String path) {
    int slash = path.lastIndexOf('/');
    return slash < 0
            ? path
            : path.substring(slash + 1);
  }

  private List<ZipFileEntry> fileHierarchy(List<ZipFileEntry> files, CapBlobRef blobRef) {
    final ZipFileEntry root = createZipFileEntry("", SLASH, null, 0, true, blobRef);
    for (ZipFileEntry file : files) {
      ZipFileEntry parent = getParent(file, root);
      // perform "insert sort" for stable JSON results
      int i = 0;
      while (i < parent.getChildren().size() && compareZipFileEntry(parent.getChildren().get(i), file) <= 0) {
        i++;
      }
      parent.getChildren().add(i, file);
      parent.getChildrenByName().put(file.getName(), file);
    }
    return root.getChildren();
  }

  private ZipFileEntry createZipFileEntry(String name, String path, Date time, long size, boolean directory, CapBlobRef blobRef) {
    final BlobZipFileEntry blobZipFileEntry = new BlobZipFileEntry(blobRef, path);
    // we have to use a slightly different link scheme here (compared to the original blob URI)
    // to avoid a clash with transformed blob properties,
    // therefore, we generate links pointing to blobZipFileEntry instances
    final URI uri = entityResourceLinker.link(blobZipFileEntry);
    return new ZipFileEntry(name, path, time, size, directory, blobZipFileEntry, uri);
  }

  private int compareZipFileEntry(ZipFileEntry file1, ZipFileEntry file2) {
    if (file1.isDirectory() != file2.isDirectory()) {
      return file1.isDirectory() ? -1 : +1;
    }
    int ignoreCaseResult = file1.getName().compareToIgnoreCase(file2.getName());
    return ignoreCaseResult != 0 ? ignoreCaseResult : file1.getName().compareTo(file2.getName());
  }

  private ZipFileEntry getParent(ZipFileEntry file, ZipFileEntry root) {
    String[] pathSegments = file.getPath().split(SLASH);
    ZipFileEntry parent = root;
    for (int i = 0; i < pathSegments.length; i++) {
      String segment = pathSegments[i];
      ZipFileEntry child = parent.getChildrenByName().get(segment);
      if (child == null) {
        if (i == pathSegments.length - 1) {
          return parent;
        }
        throw new IllegalStateException("child occurs before parent in zip directory listing");
      }
      parent = child;
    }
    throw new AssertionError("internal error");
  }

}
