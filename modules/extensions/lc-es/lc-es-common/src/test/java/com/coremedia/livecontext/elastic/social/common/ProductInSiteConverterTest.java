package com.coremedia.livecontext.elastic.social.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.elastic.core.api.models.UnresolvableReferenceException;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.coremedia.livecontext.elastic.social.common.ProductInSiteConverter.ID;
import static com.coremedia.livecontext.elastic.social.common.ProductInSiteConverter.SITE_ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductInSiteConverterTest {
  private String productId = "1234";
  private String productReferenceId = "vendor:///catalog/product/" + productId;
  private String siteId = "5678";

  @InjectMocks
  private ProductInSiteConverter converter = new ProductInSiteConverter();

  @Mock
  private Product product;

  @Mock
  private ProductInSite productInSite;

  @Mock
  private CommerceConnectionInitializer connectionInitializer;

  @Mock
  private Site site;

  @Mock
  private SitesService sitesService;

  private BaseCommerceConnection commerceConnection;

  @Before
  public void setup(){

    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
    commerceConnection.getStoreContext().put("site", siteId);
    when(commerceConnection.getCatalogService().findProductById(anyString())).thenReturn(product);

    when(product.getExternalId()).thenReturn(productId);
    when(product.getContext()).thenReturn(commerceConnection.getStoreContext());
    when(commerceConnection.getStoreContextProvider().createContext(site)).thenReturn(commerceConnection.getStoreContext());

    when(productInSite.getProduct()).thenReturn(product);
    when(productInSite.getSite()).thenReturn(site);

    when(sitesService.getSite(siteId)).thenReturn(site);

    when(site.getId()).thenReturn(siteId);
  }

  @Test
  public void getType() {
    assertEquals(ProductInSite.class, converter.getType());
  }

  @Test
  public void serializeWithProductReferenceId() {
    Map<String, Object> serializedObject = new HashMap<>();

    converter.serialize(productInSite, serializedObject);

    assertEquals(2, serializedObject.entrySet().size());
    assertEquals(productReferenceId, serializedObject.get(ID));
    assertEquals(siteId, serializedObject.get(SITE_ID));

    verify(product).getExternalId();
  }

  @Test
  public void deserialize() {
    Map<String, Object> serializedObject = new HashMap<>();
    serializedObject.put("id", productReferenceId);
    serializedObject.put("site", siteId);

    ProductInSite result = converter.deserialize(serializedObject);

    Assert.assertSame(product, result.getProduct());
    Assert.assertSame(site, result.getSite());
  }

  @Test(expected = UnresolvableReferenceException.class)
  public void deserializeUnresolvable() {
    Map<String, Object> serializedObject = new HashMap<>();
    serializedObject.put("id", "id");
    serializedObject.put("site", "site");

    when(commerceConnection.getCatalogService().findProductById(anyString())).thenReturn(null);

    converter.deserialize(serializedObject);
  }
}
