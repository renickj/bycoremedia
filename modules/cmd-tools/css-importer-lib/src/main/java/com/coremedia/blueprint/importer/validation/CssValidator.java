package com.coremedia.blueprint.importer.validation;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public final class CssValidator {
  private static final Logger LOG = LoggerFactory.getLogger(CssValidator.class);
  private static final String UTF_8 = "UTF-8";

  // static utility class
  private CssValidator() {
  }

  /**
   * Validate CSS
   * <p>
   * Currently checks only for @import usages, may be improved later.
   *
   * @param systemId resource name, only for logging
   * @param bytes the JavaScript to be validated
   */
  public static int validateCss(String systemId, InputStream bytes) {
    try {
      if (IOUtils.toString(bytes, UTF_8).contains("@import")) {
        LOG.info("CSS validation warning: File {} uses @import", systemId);
      }
    } catch (IOException e) {
      LOG.error("Cannot validate CSS " + systemId, e);
    }
    return 0;  // CSS validation is only informational.
  }

}
