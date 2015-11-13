package com.coremedia.blueprint.ecommerce.contentbeans.impl;

import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.base.ecommerce.catalog.CmsCatalogService;
import com.coremedia.blueprint.base.ecommerce.catalog.CmsProduct;
import com.coremedia.blueprint.ecommerce.contentbeans.CMProduct;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CMProductImplTest {
  @Mock
  Content productContent;

  @Mock
  Content contentPic1;
  @Mock
  Content contentPic2;
  @Mock
  Content productPic1;
  @Mock
  Content productPic2;
  
  @Mock
  private CMPicture contentPic1Bean;
  @Mock
  private CMPicture contentPic2Bean;
  @Mock
  private CMPicture productPic1Bean;
  @Mock
  private CMPicture productPic2Bean;

  @Mock
  private CmsCatalogService catalogService;

  @Before
  public void setup() {
    Mockito.when(contentPic1Bean.getContent()).thenReturn(contentPic1);
    Mockito.when(contentPic2Bean.getContent()).thenReturn(contentPic2);
    Mockito.when(productPic1Bean.getContent()).thenReturn(productPic1);
    Mockito.when(productPic2Bean.getContent()).thenReturn(productPic2);

    // May be invoked by logging, not relevant for tests
    Mockito.when(productContent.toString()).thenReturn("mocked content");
  }

  @Test
  public void testGetProductPictureFromContent() {
    CMProduct testling = createTestling();
    contentPictures(testling, contentPic1Bean, contentPic2Bean);

    List<CatalogPicture> productPictures = testling.getProductPictures();
    assertSamePictures(productPictures, contentPic1, contentPic2);

    CatalogPicture productPicture = testling.getProductPicture();
    Assert.assertEquals(contentPic1, productPicture.getPicture());
  }

  @Test
  public void testGetProductPictureFromProduct() {
    CMProduct testling = createTestling();
    contentPictures(testling, productPic1Bean, productPic2Bean);
    when(productPic1Bean.getContent()).thenReturn(productPic1);
    when(productPic2Bean.getContent()).thenReturn(productPic2);

    List<CatalogPicture> productPictures = testling.getProductPictures();
    assertSamePictures(productPictures, productPic1, productPic2);

    CatalogPicture productPicture = testling.getProductPicture();
    Assert.assertEquals(productPic1, productPicture.getPicture());
  }

  @Test
  public void testGetProductPictureNoProductFound() {
    CMProduct testling = createTestling();
    contentPictures(testling);
    Mockito.when(catalogService.findProductByContent(Matchers.any(Content.class))).thenReturn(null);

    List<CatalogPicture> productPictures = testling.getProductPictures();

    Assert.assertNotNull(productPictures);
    Assert.assertTrue(productPictures.isEmpty());
  }

  public CMProduct createTestling() {
    CMProductImpl testling = Mockito.spy(new CMProductImpl());
    testling.setCatalogService(catalogService);
    Mockito.doReturn(productContent).when(testling).getContent();
    return testling;
  }

  private static void assertSamePictures(List<CatalogPicture> actual, Content... expected) {
    Assert.assertEquals(expected.length, actual.size());
    for (int i=0; i<expected.length; ++i) {
      Assert.assertEquals(expected[i], actual.get(i).getPicture());
    }
  }

  private void contentPictures(CMProduct testling, CMPicture... contentPictures) {
    Mockito.doReturn(Arrays.asList(contentPictures)).when(testling).getPictures();  // Teasable.getPictures()
  }

}
