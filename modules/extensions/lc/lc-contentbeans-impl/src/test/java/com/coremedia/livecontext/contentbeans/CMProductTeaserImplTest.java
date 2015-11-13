package com.coremedia.livecontext.contentbeans;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import com.coremedia.xml.MarkupUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CMProductTeaserImplTest {

  public static final String CONTENT_TEASER_TITLE_PROPERTY = "teaserTitle";
  public static final String CONTENT_TEASER_TITLE = "content teaser title";
  public static final String CATALOG_TEASER_TITLE = "catalog teaser title";

  public static final String CONTENT_TEASER_TEXT_PROPERTY = "teaserText";
  public static final String CONTENT_TEASER_TEXT = "content teaser text";
  public static final String CATALOG_TEASER_TEXT = "catalog teaser text";
  public static final Markup CONTENT_TEASER_TEXT_MARKUP = createMarkup(CONTENT_TEASER_TEXT);
  public static final Markup CATALOG_TEASER_TEXT_MARKUP = createMarkup(CATALOG_TEASER_TEXT);
  public static final Markup EMPTY_MARKUP = createMarkup("");

  public static final String CONTENT_TITLE_PROPERTY = "title";
  public static final String CONTENT_TITLE = "content title";

  public static final String CONTENT_DETAIL_TEXT_PROPERTY = "detailText";
  public static final String CONTENT_DETAIL_TEXT = "content detail text";

  private Content content;
  private Product product;
  private CMProductTeaserImpl testling;

  @Before
  public void setUp() {
    initMocks(this);

    content = mock(Content.class);
    product = mock(Product.class);
    testling = new TestCMProductTeaserImpl(content, product);
  }

  @Test
  public void testGetTeaserValuesFromContent() {
    when(content.getString(anyString())).thenReturn(CONTENT_TEASER_TITLE);
    when(content.getMarkup(anyString())).thenReturn(createMarkup(CONTENT_TEASER_TEXT));

    assertEquals(CONTENT_TEASER_TITLE, testling.getTeaserTitle());
    assertEquals(CONTENT_TEASER_TEXT_MARKUP, testling.getTeaserText());
  }

  @Test
  public void testGetTeaserValuesFromCatalog() {
    when(content.getString(anyString())).thenReturn(null);
    when(content.getMarkup(anyString())).thenReturn(null);

    when(product.getName()).thenReturn(CATALOG_TEASER_TITLE);
    when(product.getShortDescription()).thenReturn(createMarkup(CATALOG_TEASER_TEXT));

    assertEquals(CATALOG_TEASER_TITLE, testling.getTeaserTitle());
    assertEquals(CATALOG_TEASER_TEXT_MARKUP, testling.getTeaserText());
  }

  @Test
  public void testGetTeaserValuesFromCatalogWhenPropertyEmpty() {
    when(content.getString(anyString())).thenReturn("");
    when(content.getMarkup(anyString())).thenReturn(EMPTY_MARKUP);

    when(product.getName()).thenReturn(CATALOG_TEASER_TITLE);
    when(product.getShortDescription()).thenReturn(createMarkup(CATALOG_TEASER_TEXT));

    assertEquals(CATALOG_TEASER_TITLE, testling.getTeaserTitle());
    assertEquals(CATALOG_TEASER_TEXT_MARKUP, testling.getTeaserText());
  }

  @Test
  public void testGetTeaserValuesFromXXIfContentAndCatalogEmpty() {
    when(content.getString(anyString())).thenReturn("");
    when(content.getMarkup(anyString())).thenReturn(EMPTY_MARKUP);

    when(product.getName()).thenReturn(CATALOG_TEASER_TITLE);
    when(product.getShortDescription()).thenReturn(createMarkup(CATALOG_TEASER_TEXT));

    assertEquals(CATALOG_TEASER_TITLE, testling.getTeaserTitle());
    assertEquals(CATALOG_TEASER_TEXT_MARKUP, testling.getTeaserText());
  }

  @Test
  public void testGetTeaserValuesWithCommerceException() {
    when(content.getString(CONTENT_TEASER_TITLE_PROPERTY)).thenReturn("");
    when(content.getMarkup(CONTENT_TEASER_TEXT_PROPERTY)).thenReturn(EMPTY_MARKUP);
    when(content.getString(CONTENT_TITLE_PROPERTY)).thenReturn(CONTENT_TITLE);
    when(content.getMarkup(CONTENT_DETAIL_TEXT_PROPERTY)).thenReturn(createMarkup(CONTENT_DETAIL_TEXT));

    when(product.getName()).thenThrow(CommerceException.class);
    when(product.getShortDescription()).thenReturn(null);

    assertEquals(CONTENT_TITLE, testling.getTeaserTitle());
    assertEquals(CONTENT_DETAIL_TEXT, MarkupUtil.asPlainText(testling.getTeaserText()).trim());
  }

  @Test
  public void testGetTeaserValuesWithNoProduct() {
    when(content.getString(CONTENT_TEASER_TITLE_PROPERTY)).thenReturn("");
    when(content.getMarkup(CONTENT_TEASER_TEXT_PROPERTY)).thenReturn(EMPTY_MARKUP);
    when(content.getString(CONTENT_TITLE_PROPERTY)).thenReturn(CONTENT_TITLE);
    when(content.getMarkup(CONTENT_DETAIL_TEXT_PROPERTY)).thenReturn(createMarkup(CONTENT_DETAIL_TEXT));
    product = null;

    assertEquals(CONTENT_TITLE, testling.getTeaserTitle());
    assertEquals(CONTENT_DETAIL_TEXT, MarkupUtil.asPlainText(testling.getTeaserText()).trim());
  }

  private class TestCMProductTeaserImpl extends CMProductTeaserImpl {
    private Content content;
    private Product product;

    public TestCMProductTeaserImpl(Content content, Product product) {
      this.content = content;
      this.product = product;
    }

    @Override
    public Content getContent() {
      return content;
    }

    @Override
    public Product getProduct() {
      return product==null ? super.getProduct() : product;
    }
  }

  private static Markup createMarkup(String value) {
    StringBuilder markupData = new StringBuilder(value.length());
    markupData.append("<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
    markupData.append("<p>");
    markupData.append(value);
    markupData.append("</p>");
    markupData.append("</div>");
    return MarkupFactory.fromString(markupData.toString());
  }
}
