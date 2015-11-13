package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cap.common.Blob;

import java.util.List;

/**
 * A Page is a {@link AbstractPage} that represents a complete page including its own CSS, JavaScript
 * and favicon.
 */
public interface Page extends AbstractPage {
  /**
   * Returns the css contents for this page.
   *
   * @return the css contents for this page
   */
  List<?> getCss();

  /**
   * Returns the JavaScript contents for this page.
   *
   * @return the JavaScript contents for this page
   */
  List<?> getJavaScript();

  /**
   * Returns a favicon for this page. May be null.
   */
  Blob getFavicon();

  /**
   * Return the first navigation context within the navigation hierarchy which is an instance of CMContext
   */
  CMContext getContext();
}
