package com.coremedia.livecontext.preview;


import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PreviewTokenAppendingLinkTransformerTest {

  private PreviewTokenAppendingLinkTransformer testling;

  @Mock
  private CommercePropertyProvider previewTokenProvider;

  @Mock
  private BaseCommerceConnection connection;



  @Before
  public void setup(){
    testling = new PreviewTokenAppendingLinkTransformer();
    testling.setPreview(true);
    testling.setPreviewTokenProvider(previewTokenProvider);
    connection = MockCommerceEnvBuilder.create().setupEnv();

    when(previewTokenProvider.provideValue(anyMap())).thenReturn("aPreviewTokenStr");
  }

  @Test
  public void testLinkTransformerApply() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("p13n_test", "true");

    String link = testling.transform("//url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("//url/to/shop?previewToken=aPreviewTokenStr", link);

    link = testling.transform("http://url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("http://url/to/shop?previewToken=aPreviewTokenStr", link);

    link = testling.transform("https://url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("https://url/to/shop?previewToken=aPreviewTokenStr", link);
  }

  @Test
  public void testLinkTransformerCopyExsiting() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("previewToken")).thenReturn("existingTokenStr");

    String link = testling.transform("//url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("//url/to/shop?previewToken=existingTokenStr", link);

    link = testling.transform("/blueprint/internal/url", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("/blueprint/internal/url?previewToken=existingTokenStr", link);
  }

  @Test
  public void testLinkTransformerMiss() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("p13n_test", "true");

    String link = testling.transform("/blueprint/url", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("/blueprint/url", link);
  }

  @Test
  public void testLinkTransformerNoStoreContextAvailable() {
    //previewTokenProvider returns null if no storeContext available
    when(previewTokenProvider.provideValue(anyMap())).thenReturn(null);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setParameter("p13n_test", "true");

    String link = testling.transform("//url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("//url/to/shop", link);
  }


}