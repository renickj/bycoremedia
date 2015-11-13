package com.coremedia.livecontext.elastic.social.common;


import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductTransformerTest {
  @InjectMocks
  private ProductTransformer productTransformer = new ProductTransformer();

  @Mock
  private Product product;

  @Mock
  private ProductInSite productInSite;

  @Mock
  private SitesService sitesService;

  @Mock
  private StoreContext storeContext;

  @Mock
  private Site site;

  @Before
  public void setup() {
    String siteId = "1234";
    when(product.getContext()).thenReturn(storeContext);
    when(storeContext.getSiteId()).thenReturn(siteId);
    when(sitesService.getSite(siteId)).thenReturn(site);
  }


  @Test
  public void transform() {
    Object transformed = productTransformer.transform(product);
    assertNotNull(transformed);
    ProductInSite result = (ProductInSite) transformed;
    assertEquals(product, result.getProduct());
    assertEquals(site, result.getSite());
  }

  @Test
  public void getSite() {
    Site siteFromProductWrapper = productTransformer.getSite(product);

    assertNotNull(siteFromProductWrapper);
    assertEquals(site, siteFromProductWrapper);
  }

  @Test
  public void getType() {
    assertEquals(Product.class, productTransformer.getType());
  }
}
