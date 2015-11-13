package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapBlobRef;
import com.coremedia.rest.cap.blob.metadata.BlobMetadataResolver;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Tag;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import javax.annotation.Nonnull;
import java.io.BufferedInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.studio.rest.Mp3MetadataResolver.formatKey;

/**
 * Blob resolver that gathers the meta data information of images.
 *
 */
public class ImageMetadataResolver implements BlobMetadataResolver {
  private static final Logger LOG = LoggerFactory.getLogger(ImageMetadataResolver.class);

  private static final String PROPERTY = "property";
  private static final String SECTION = "section";
  private static final String VALUE = "value";
  public static final MimeType IMAGE_MIME_TYPE = MimeTypeUtils.parseMimeType("image/*");

  private String name;

  @Nonnull
  @Override
  public String getName() {
    return name;
  }

  @Required
  public void setName(@Nonnull String name) {
    Preconditions.checkNotNull(name, "name must not be null");
    this.name = name;
  }

  @Override
  public boolean canHandle(@Nonnull Blob blob) {
    final MimeType mimeType = MimeTypeUtils.parseMimeType(blob.getContentType().toString());
    return IMAGE_MIME_TYPE.isCompatibleWith(mimeType);
  }

  /**
   * Reads the ID3 data of an audio file if available.
   *
   * @param blob The audio content to read the id3 data from.
   * @return The representation with the id3 properties.
   */
  @Override
  public Object resolveMetadata(@Nonnull CapBlobRef blob) {
    final List<Map<String, String>> result = new LinkedList<>();
    com.drew.metadata.Metadata metadata = null;
    try {
      metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(blob.getInputStream()), false);
    } catch (Exception e) {
      LOG.warn("Error processing image data of {}: {}", blob, e.getMessage());
    }
    if (metadata != null) {
      for (Directory directory : metadata.getDirectories()) {
        for (Tag tag : directory.getTags()) {
          if (tag.getTagName() != null) {
            Map<String, String> exifDirectoryInfo = new HashMap<>();
            exifDirectoryInfo.put(SECTION, directory.getName());
            exifDirectoryInfo.put(PROPERTY, formatKey(tag.getTagName()));
            exifDirectoryInfo.put(VALUE, tag.getDescription());
            result.add(exifDirectoryInfo);
          }
        }
      }
      Collections.sort(result, new InfoComparator());
    }
    return result;
  }

  private static class InfoComparator implements Comparator<Map<String, String>>, Serializable {
    private static final long serialVersionUID = 42L;
    @Override
    public int compare(Map<String, String> o1, Map<String, String> o2) {
      return o1.get(PROPERTY).compareTo(o2.get(PROPERTY));
    }
  }
}
