package com.coremedia.livecontext.navigation;

import com.google.common.annotations.VisibleForTesting;

/**
 * A helper for composite placement names
 * Placement pattern: <pageprefix>-<placementname>, e.g. pdp-banner or cart-banner will be resolved.
 */
public class CompositeNameHelper {

  @VisibleForTesting
  static final String PREFIX_SEPARATOR = "-";
  @VisibleForTesting
  static final String PREFIX_QUALIFIER = "livecontext.relatedpage.";

  private static final ThreadLocal<String> COMPOSITE_NAME_THREAD_LOCAL = new ThreadLocal<>();

  /**
   * Map the compositePlacementName to a page key:
   * "foo" -> null
   * "-foo" -> null
   * "bar-foo" -> "livecontext.relatedpage.bar"
   */
  public static String getPagePrefix(String compositePlacementName) {
    if (compositePlacementName == null) {
      return null;
    }

    int sepIndex = compositePlacementName.indexOf(PREFIX_SEPARATOR);
    return sepIndex>0 ? PREFIX_QUALIFIER+compositePlacementName.substring(0, sepIndex) : null;
  }

  /**
   * Extract the placement name from compositePlacementName:
   * "foo" -> null
   * "-foo" -> null
   * "bar-foo" -> "foo"
   */
  public static String getPlacementName(String compositePlacementName) {
    if (compositePlacementName == null) {
      return null;
    }

    int sepIndex = compositePlacementName.indexOf(PREFIX_SEPARATOR);
    return sepIndex > 0 ? compositePlacementName.substring(sepIndex+1) : null;
  }


  public static boolean isCompositeName(String compositePlacementName) {
    if (compositePlacementName == null || !compositePlacementName.contains(PREFIX_SEPARATOR)) {
      return false;
    }

    // Composite name must not start or end with prefix separator (asserts that both components are not empty)
    return !(compositePlacementName.startsWith(PREFIX_SEPARATOR) || compositePlacementName.endsWith(PREFIX_SEPARATOR));
  }

  public static String getCurrentCompositeName() {
    return COMPOSITE_NAME_THREAD_LOCAL.get();
  }

  public static void setCurrentCompositeName(String compositeName) {
    COMPOSITE_NAME_THREAD_LOCAL.set(compositeName);
  }

}
