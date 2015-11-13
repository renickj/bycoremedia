package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExcludeFromSearchSitemapPredicateTest {

  @Mock
  private Content content;

  private ExcludeFromSearchSitemapPredicate testling;
  private String DOCTYPE = "CMTeasable";
  private String NOT_SEARCHABLE_FLAG = "notSearchable";

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    testling = new ExcludeFromSearchSitemapPredicate();
    testling.setDoctypeName(DOCTYPE);
    testling.setNotSearchablePropertyName(NOT_SEARCHABLE_FLAG);
  }

  @Test
  public void testTeasableButSearchable() throws Exception {
    Content content1 = getContent(-244, true);

    boolean include = testling.include(content1);
    assertTrue(include);
  }

  @Test
  public void testTeasableNotSearchable() throws Exception {
    Content content1 = getContent(0, true);

    boolean include = testling.include(content1);
    assertFalse(include);
  }

  @Test
  public void testTeasableNotSearchableBigPositive() throws Exception {
    Content content1 = getContent(2000, true);

    boolean include = testling.include(content1);
    assertFalse(include);
  }

  @Test
  public void testOnlyContentIsIncluded() throws Exception {
    boolean include = testling.include(new Object());
    assertFalse(include);
  }

  private Content getContent(int notSearchableReturnValue,
                             boolean isSubTypeOf) {
    Content content = mock(Content.class);
    when(content.getInt(NOT_SEARCHABLE_FLAG)).thenReturn(notSearchableReturnValue);
    ContentType mock = mock(ContentType.class);
    when(content.getType()).thenReturn(mock);
    when(mock.isSubtypeOf(DOCTYPE)).thenReturn(isSubTypeOf);
    return content;
  }
}