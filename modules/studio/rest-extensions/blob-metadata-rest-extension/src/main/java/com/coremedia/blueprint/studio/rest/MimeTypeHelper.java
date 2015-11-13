package com.coremedia.blueprint.studio.rest;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

/**
 * Some convenience
 */
final class MimeTypeHelper {

  private static final MimeType APPLICATION_ZIP = createMimeType("application", "zip");
  private static final MimeType APPLICATION_X_JAR = createMimeType("application", "x-java-archive");
  private static final MimeType APPLICATION_JAR = createMimeType("application", "java-archive");

  static MimeType createMimeType(String major, String minor) {
    try {
      return new MimeType(major, minor);
    } catch (MimeTypeParseException e) {
      throw new IllegalArgumentException("Cannot parse mime type " + major + "/" + minor, e);
    }
  }
  // static utility class
  private MimeTypeHelper() {}

  public static boolean isZip(MimeType mimeType) {
    return mimeType.match(APPLICATION_ZIP) || mimeType.match(APPLICATION_JAR) || mimeType.match(APPLICATION_X_JAR);
  }
}
