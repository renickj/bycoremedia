package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapBlobRef;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.common.InvalidPropertyValueException;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.mimetype.MimeTypeService;
import com.coremedia.rest.exception.BadRequestException;
import com.coremedia.rest.exception.NotFoundException;
import com.coremedia.rest.linking.EntityResource;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.activation.MimeType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A REST service for delivering a file within a zip or jar archive.
 */
@Path(ZipFileEntryResource.URI_TEMPLATE)
public class ZipFileEntryResource implements EntityResource<BlobZipFileEntry> {
  private static final Log LOG = LogFactory.getLog(ZipFileEntryResource.class);

  static final String PARAM_ID = "id";
  static final String PARAM_PROPERTY = "property";
  static final String PARAM_PATH = "path";

  public static final String URI_TEMPLATE = "content/{" + ZipFileEntryResource.PARAM_ID +
          ":[0-9]+}/zip/{" + ZipFileEntryResource.PARAM_PROPERTY +
          "}/{" + ZipFileEntryResource.PARAM_PATH + ":[^?]+}";

  private ContentRepository contentRepository;
  private MimeTypeService mimeTypeService;

  private String id;
  private String property;
  private String path;

  public String getId() {
    return id;
  }

  @PathParam(PARAM_ID)
  public void setId(String id) {
    this.id = id;
  }

  public String getProperty() {
    return property;
  }

  @PathParam(PARAM_PROPERTY)
  public void setProperty(String property) {
    this.property = property;
  }

  public String getPath() {
    return path;
  }

  @PathParam(PARAM_PATH)
  public void setPath(String path) {
    this.path = path;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Required
  public void setMimeTypeService(MimeTypeService mimeTypeService) {
    this.mimeTypeService = mimeTypeService;
  }

  @GET
  public Response getFile() throws IOException {
    final String contentId = id;
    try {
      Content content = contentRepository.getContent(contentId);
      if (content == null) {
        throw new NotFoundException(String.format("Content '%s' does not exist", contentId));
      }
      Blob blob = content.getBlob(property);
      if (blob == null) {
        throw new NotFoundException(String.format("Property %s of content %s is null", property, contentId));
      }
      return getFileContent(blob, path);
    } catch (NoSuchPropertyDescriptorException e) {
      throw new NotFoundException(String.format("content %s does not have a property %s", contentId, property), e);
    } catch (InvalidPropertyValueException e) {
      throw new BadRequestException(String.format("Property %s of content %s is not a Blob", property, contentId), e);
    }
  }

  private Response getFileContent(Blob blob, String path) throws IOException {
    final MimeType mimeType = blob.getContentType();
    if (MimeTypeHelper.isZip(mimeType)) {
      InputStream blobStream = blob.getInputStream();
      ZipInputStream zipStream = new ZipInputStream(blobStream);
      ZipEntry entry;
      try {
        while ((entry = zipStream.getNextEntry()) != null) {
          // assuming that entry#name is a relative entryPath
          String entryPath = normalizedPath(entry.getName());
          if (entry.getName().equals(path) || entryPath.equals(path)) {
            if (entry.isDirectory()) {
              return directoryListing(zipStream, entryPath);
            } else {
              return fileContents(zipStream, entryPath);
            }
          }
          zipStream.closeEntry();
        }
        zipStream.close();
        throw new NotFoundException(path);
      } catch (IOException e) {
        // zip file format error? or not a zip file at all...
        LOG.error("Exception while reading zip stream", e);
        blobStream.close();
        throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
      }
    }
    return null;
  }

  private String normalizedPath(String entryPath) {
    // zip file names for directories end with a slash:
    return entryPath.endsWith("/") ? entryPath.substring(0, entryPath.length() - 1) : entryPath;
  }

  private Response directoryListing(ZipInputStream zipStream, String directoryPath) throws IOException {
    // assumption: directory content entries are in-line after the directory entry itself
    ZipEntry entry;
    StringBuilder responseBody = new StringBuilder();
    try {
      while ((entry = zipStream.getNextEntry()) != null) {
        // assuming that entry#name is a relative entryPath
        String entryPath = normalizedPath(entry.getName());
        // zip file names for directories end with a slash:
        if (entryPath.startsWith(directoryPath) && entryPath.lastIndexOf('/') == directoryPath.length()) {
          responseBody.append(entry.getName()).append('\n');
        }
        zipStream.closeEntry();
      }
    } finally {
      zipStream.close();
    }
    return Response.ok(responseBody.toString(), MediaType.TEXT_PLAIN_TYPE).build();
  }


  private Response fileContents(ZipInputStream zipStream, String path) {
    String ext = FilenameUtils.getExtension(path);
    MediaType entryMediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
    if (!ext.isEmpty()) {
      String mimeTypeForExtension = mimeTypeService.getMimeTypeForExtension(ext);
      if (mimeTypeForExtension != null) {
        entryMediaType = MediaType.valueOf(mimeTypeForExtension);
      }
    }
    // Jersey should close the stream after response is written
    return Response.ok(zipStream, entryMediaType).build();
  }

  @Override
  public BlobZipFileEntry getEntity() {
    return new BlobZipFileEntry(contentRepository.getContent(id).getBlobRef(getProperty()), getPath());
  }

  @Override
  public void setEntity(BlobZipFileEntry entity) {
    final CapBlobRef capBlobRef = entity.getCapBlobRef();
    setId(Integer.toString(IdHelper.parseContentId(IdHelper.parseContentIdFromBlobId(capBlobRef.getId()))));
    setProperty(capBlobRef.getPropertyName());
    setPath(entity.getPath());
  }
}

