package com.coremedia.blueprint.ecommerce.segments;

import com.coremedia.blueprint.segments.CMLinkableSegmentStrategy;
import com.coremedia.cap.content.Content;

import javax.annotation.Nonnull;

/**
 * ContentSegmentStrategy for CMProduct
 */
public class CMProductSegmentStrategy extends CMLinkableSegmentStrategy {
  private static final String PRODUCT_NAME = "productName";

  /**
   * Use segment, productName or title as segment.
   */
  @Nonnull
  @Override
  public String segment(@Nonnull Content content) {
    return getSomeString(content, SEGMENT, PRODUCT_NAME, TITLE);
  }
}
