package com.coremedia.blueprint.common.navigation;

import java.util.Locale;

/**
 * Interface to be implemented by content or external content instances, like CMLinkable
 */
public interface Linkable extends HasViewTypeName {

  /**
   * Returns the Locale of this document.
   *
   * @return the Locale of this document.
   */
  Locale getLocale();

  /**
   * Returns the title to be used in the head meta data.
   */
  String getTitle();

  /**
   * Returns the value of the keywords property.
   */
  String getKeywords();

  /**
   * The segment is used to create the nice part of the <i>nice</i> URLs.
   *
   * @return the URL segment for this content, SEO friendly, human readable
   */
  String getSegment();

}
