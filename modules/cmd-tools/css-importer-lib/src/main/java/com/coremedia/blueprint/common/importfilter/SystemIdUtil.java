package com.coremedia.blueprint.common.importfilter;

import static java.util.Locale.ENGLISH;

final class SystemIdUtil {
  // static utility class
  private SystemIdUtil() {
  }

  /**
   * Returns the lowercased {@link #extension(String)} of the systemId.
   */
  public static String type(String systemId) {
    return extension(systemId).toLowerCase(ENGLISH);
  }

  /**
   * Returns the extension of the systemId, possibly "".
   */
  public static String extension(String systemId) {
    int dot = systemId.lastIndexOf('.');
    int slash = systemId.lastIndexOf('/');
    return slash>dot || dot<0 || dot>=systemId.length() ? "" : systemId .substring(dot+1);
  }
}
