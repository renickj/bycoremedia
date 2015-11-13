package com.coremedia.livecontext.elastic.social.common;


import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.ProductInSite;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductInSiteTransformerTest {
  @InjectMocks
  private ProductInSiteTransformer productTransformer = new ProductInSiteTransformer();

  @Mock
  private ProductInSite productInSite;

  @Mock
  private Site site;

  @Test
  public void transform() {
    Object transformed = productTransformer.transform(productInSite);
    assertNotNull(transformed);
    ProductInSite result = (ProductInSite) transformed;
    assertSame(productInSite, result);
  }

  @Test
  public void getSite() {
    when(productInSite.getSite()).thenReturn(site);

    Site siteFromProductWrapper = productTransformer.getSite(productInSite);

    assertNotNull(siteFromProductWrapper);
    assertEquals(site, siteFromProductWrapper);
  }

  @Test
  public void getType() {
    assertEquals(ProductInSite.class, productTransformer.getType());
  }
}
