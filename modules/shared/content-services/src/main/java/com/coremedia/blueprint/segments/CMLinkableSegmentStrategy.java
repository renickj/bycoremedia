package com.coremedia.blueprint.segments;

import com.coremedia.blueprint.base.links.ContentSegmentStrategy;
import com.coremedia.cap.content.Content;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;

/**
 * ContentSegmentStrategy for CMLinkable
 */
public class CMLinkableSegmentStrategy implements ContentSegmentStrategy {
  protected static final String SEGMENT = "segment";
  protected static final String TITLE = "title";

  /**
   * If the linkable has no segment, fallback to its title.
   *
   * @return the segment or the title if the segment is empty.
   */
  @Override
  @Nonnull
  public String segment(@Nonnull Content content) {
    return getSomeString(content, SEGMENT, TITLE);
  }

  /**
   * Desperately try to find some suitable String by fallbacking along a list
   * of String properties.
   * <p>
   * Utility method. May be useful in extending classes. Take it or leave it.
   *
   * @return a property value or "" if nothing has been found.
   */
  @Nonnull
  protected final String getSomeString(Content content, String... properties) {
    for (String property : properties) {
      String result = content.getString(property);
      if (StringUtils.hasText(result)) {
        return result;
      }
    }
    return "";
  }
}
