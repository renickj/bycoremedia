package com.coremedia.lc.studio.lib.validators;

import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DuplicateExternalIdCacheKeyTest {
  @Test
  public void cacheClass() {
    testling = validator.new DuplicateExternalIdCacheKey(externalChannel, rootChannel);
    assertEquals(ExternalChannelExternalIdValidator.CACHE_CLASS, testling.cacheClass(null, null));
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidContentType() {
    testling = validator.new DuplicateExternalIdCacheKey(invalidContent, null);
  }

  @Test
  public void test() {
    testling = validator.new DuplicateExternalIdCacheKey(externalChannel, rootChannel);
    Content duplicate = cache.get(testling);
    assertNotNull(duplicate);
    assertEquals(1, cache.getNumberOfValues().get(ExternalChannelExternalIdValidator.CACHE_CLASS).intValue());

    cache.get(testling);
    assertEquals(1, cache.getNumberOfValues().get(ExternalChannelExternalIdValidator.CACHE_CLASS).intValue());
  }

  /**
   * An Article may be linked as navigation node. But it has no children. This test assures that
   * the cache key does not throw an exception because of the missing property.
   */
  @Test
  public void validateUniquenessNavigationContainsAnArticle() {
    Content article = mock(Content.class, Mockito.RETURNS_DEEP_STUBS);
    List<Content> children = ImmutableList.of(article);
    when(article.getId()).thenReturn("42");
    when(article.getType().isSubtypeOf(ExternalChannelExternalIdValidator.EXTERNAL_CHANNEL_DOCTYPE)).thenReturn(false);

    Map<String, Object> rootChannelProperties = new HashMap<>();
    rootChannelProperties.put(PROPERTY_CHILDREN, children);
    when(rootChannel.getLinks(PROPERTY_CHILDREN)).thenReturn(children);
    when(rootChannel.getProperties()).thenReturn(rootChannelProperties);

    testling = validator.new DuplicateExternalIdCacheKey(externalChannel, rootChannel);
    Content duplicate = cache.get(testling);
    assertNull(duplicate);
  }

  @Before
  public void defaultSetup() {
    validator = new ExternalChannelExternalIdValidator();
    validator.setPropertyName(PROPERTY_NAME);
    cache = new Cache("test");

    List<Content> children = new ArrayList<>();
    children.add(mockChild(false));
    children.add(mockChild(true));
    children.add(mockChild(false));
    children.add(mockChild(false));

    when(externalChannel.getType().isSubtypeOf(ExternalChannelExternalIdValidator.EXTERNAL_CHANNEL_DOCTYPE)).thenReturn(true);
    when(invalidContent.getType()).thenReturn(invalidType);
    when(invalidType.isSubtypeOf(ExternalChannelExternalIdValidator.EXTERNAL_CHANNEL_DOCTYPE)).thenReturn(false);
    when(externalChannel.getString(PROPERTY_NAME)).thenReturn(EXTERNAL_ID);
    when(externalChannel.getId()).thenReturn(EXTERNAL_CHANNEL_ID);
    when(rootChannel.getLinks("children")).thenReturn(children);
    when(rootChannel.getId()).thenReturn(Long.toString(System.currentTimeMillis()));

    Map<String, Object> rootChannelProperties = new HashMap<>();
    rootChannelProperties.put(PROPERTY_CHILDREN, children);
    when(rootChannel.getProperties()).thenReturn(rootChannelProperties);
  }

  private Content mockChild(boolean isDuplicate) {
    Content child = mock(Content.class, Mockito.RETURNS_DEEP_STUBS);
    Map<String, Object> childrenProperties = new HashMap<>();
    childrenProperties.put(PROPERTY_CHILDREN, "irrelevant");
    when(child.getProperties()).thenReturn(childrenProperties);
    when(child.getType().isSubtypeOf(ExternalChannelExternalIdValidator.EXTERNAL_CHANNEL_DOCTYPE)).thenReturn(true);
    when(child.getId()).thenReturn(Long.toString(System.currentTimeMillis()));
    if (isDuplicate) {
      when(child.getString(PROPERTY_NAME)).thenReturn(EXTERNAL_ID);
    } else {
      when(child.getString(PROPERTY_NAME)).thenReturn(Long.toString(System.currentTimeMillis()));
    }

    return child;
  }

  private ExternalChannelExternalIdValidator.DuplicateExternalIdCacheKey testling;
  private ExternalChannelExternalIdValidator validator;
  private Cache cache;

  @Mock(answer = RETURNS_DEEP_STUBS)
  private Content externalChannel;

  @Mock
  private Content invalidContent;

  @Mock
  private ContentType invalidType;

  @Mock(answer = RETURNS_DEEP_STUBS)
  private Content rootChannel;

  private static final String PROPERTY_NAME = "Hyperspace";
  private static final String EXTERNAL_ID = "By-products of Designer People";
  private static final String EXTERNAL_CHANNEL_ID = "42";
  private static final String PROPERTY_CHILDREN = "children";
}
