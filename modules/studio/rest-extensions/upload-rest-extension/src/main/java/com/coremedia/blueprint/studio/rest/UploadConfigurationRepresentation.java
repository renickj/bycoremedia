package com.coremedia.blueprint.studio.rest;

import javax.activation.MimeType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains all necessary config params used for the upload plugin.
 */
public class UploadConfigurationRepresentation {
  private static final String TEXT_PLAIN = "text/plain";
  private static final String TEXT_CSS = "text/css";
  private static final String TEXT_JAVASCRIPT = "text/javascript";
  private static final String TEXT_HTML = "text/html";
  private static final String IMAGE_PNG = "image/png";
  private static final String IMAGE_JPEG = "image/jpeg";
  private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
  private static final String AUDIO_MPEG = "audio/mpeg";
  private static final String VIDEO_MPEG = "video/mpeg";

  private List<String> mimeTypes;
  private String defaultFolder;
  private String defaultContentType;
  private String defaultBlobPropertyName;
  private int timeout;
  private Map<String, String> mimeTypeMappings = new HashMap<>();
  private Map<String, String> mimeTypeToBlobPropertyMappings = new HashMap<>();
  private Map<String, String> mimeTypeToMarkupPropertyMappings = new HashMap<>();

  public UploadConfigurationRepresentation() {
    //apply defaults
    mimeTypes = Arrays.asList(TEXT_PLAIN, TEXT_CSS, TEXT_JAVASCRIPT, TEXT_HTML,
            IMAGE_PNG, IMAGE_JPEG,
            APPLICATION_OCTET_STREAM,
            AUDIO_MPEG,
            VIDEO_MPEG); //some defaults

    defaultFolder = "Editorial";
    defaultContentType = "CMDownload";
    defaultBlobPropertyName = "data";

    mimeTypeMappings.put("image", "CMPicture");
    mimeTypeMappings.put("application", "CMDownload");
    mimeTypeMappings.put("audio", "CMAudio");
    mimeTypeMappings.put("video", "CMVideo");
    mimeTypeMappings.put("text", "CMDownload");

    mimeTypeToMarkupPropertyMappings.put(TEXT_CSS, "code");
    mimeTypeToMarkupPropertyMappings.put(TEXT_JAVASCRIPT, "code");
    mimeTypeToMarkupPropertyMappings.put(TEXT_HTML, "data");

  }

  public int getTimeout() {
    return timeout;
  }

  public void setTimeout(int timeout) {
    this.timeout = timeout;
  }

  public String getDefaultContentType() {
    return defaultContentType;
  }

  public void setDefaultContentType(String defaultContentType) {
    this.defaultContentType = defaultContentType;
  }

  public String getDefaultBlobPropertyName() {
    return defaultBlobPropertyName;
  }

  public void setDefaultBlobPropertyName(String defaultBlobPropertyName) {
    this.defaultBlobPropertyName = defaultBlobPropertyName;
  }

  public Map<String, String> getMimeTypeMappings() {
    return mimeTypeMappings;
  }

  public void setMimeTypeMappings(Map<String, String> newMimeTypeMappings) {
    mimeTypeMappings = new HashMap<>(newMimeTypeMappings);

    //set the basetype mapping for Markup document types in case this mapping is not set in the UploadSettings.
    if (!mimeTypeMappings.containsKey(TEXT_CSS)) {
      mimeTypeMappings.put(TEXT_CSS, "CMCSS");
    }

    if (!mimeTypeMappings.containsKey(TEXT_JAVASCRIPT)) {
      mimeTypeMappings.put(TEXT_JAVASCRIPT, "CMJavaScript");
    }

    if (!mimeTypeMappings.containsKey(TEXT_HTML)) {
      mimeTypeMappings.put(TEXT_HTML, "CMHTML");
    }
    mimeTypeMappings = Collections.unmodifiableMap(mimeTypeMappings);
  }

  public void setMimeTypeToBlobPropertyMappings(Map<String, String> mimeTypeToBlobPropertyMappings) {
    this.mimeTypeToBlobPropertyMappings = mimeTypeToBlobPropertyMappings;
  }

  public void setMimeTypeToMarkupPropertyMappings(Map<String, String> mimeTypeToMarkupPropertyMappings) {
    this.mimeTypeToMarkupPropertyMappings = mimeTypeToMarkupPropertyMappings;
  }

  public String getDefaultFolder() {
    return defaultFolder;
  }

  public void setDefaultFolder(String defaultFolder) {
    this.defaultFolder = defaultFolder;
  }

  public List<String> getMimeTypes() {
    return mimeTypes;
  }

  public void setMimeTypes(List<String> mimeTypes) {
    this.mimeTypes = mimeTypes;
  }

  public String getMimeTypeMapping(MimeType mimeType) {
    if (mimeTypeMappings != null && mimeTypeMappings.containsKey(mimeType.getBaseType())) {
      return mimeTypeMappings.get(mimeType.getBaseType());
    } else if (mimeTypeMappings != null && mimeTypeMappings.containsKey(mimeType.getPrimaryType())) {
      return mimeTypeMappings.get(mimeType.getPrimaryType());
    }
    return getDefaultContentType();
  }

  public String getMimeTypeToBlobPropertyMapping(MimeType mimeType) {
    if (mimeTypeToBlobPropertyMappings != null && mimeTypeToBlobPropertyMappings.containsKey(mimeType.getBaseType())) {
      return mimeTypeToBlobPropertyMappings.get(mimeType.getBaseType());
    }
    return getDefaultBlobPropertyName();
  }

  public String getMimeTypeToMarkupPropertyMapping(MimeType mimeType) {
    if (mimeTypeToMarkupPropertyMappings != null && mimeTypeToMarkupPropertyMappings.containsKey(mimeType.getBaseType())) {
      return mimeTypeToMarkupPropertyMappings.get(mimeType.getBaseType());
    }
    return null;
  }

}
