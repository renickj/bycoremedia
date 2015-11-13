package com.coremedia.livecontext.elastic.social.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.elastic.core.api.models.UnresolvableReferenceException;
import com.coremedia.elastic.core.base.serializer.AbstractTypeConverter;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.navigation.ProductInSiteImpl;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

@Named
public class ProductInSiteConverter extends AbstractTypeConverter<ProductInSite> {

  protected static final String ID = "id";
  protected static final String SITE_ID = "site";

  @Inject
  private CommerceConnectionInitializer connectionInitializer;

  @Inject
  private SitesService sitesService;

  @Override
  public Class<ProductInSite> getType() {
    return ProductInSite.class;
  }

  @Override
  public void serialize(ProductInSite productInSite, Map<String, Object> serializedObject) {
    connectionInitializer.init(productInSite.getSite());
    CommerceConnection currentConnection = Commerce.getCurrentConnection();
    if (currentConnection == null) {
      throw new CommerceException("No commerce connection available, cannot serialize " + productInSite);
    }
    serializedObject.put(ID, currentConnection.getIdProvider().formatProductId(productInSite.getProduct().getExternalId()));
    serializedObject.put(SITE_ID, productInSite.getSite().getId());
  }

  @Override
  public ProductInSite deserialize(Map<String, Object> serializedObject) {
    String id = (String) serializedObject.get(ID);
    String siteId = (String) serializedObject.get(SITE_ID);
    Site site = sitesService.getSite(siteId);
    Product product = null;
    try {
      connectionInitializer.init(siteId);
      CommerceConnection currentConnection = Commerce.getCurrentConnection();
      if (currentConnection != null) {
        CatalogService catalogService = currentConnection.getCatalogService();
        if (null != catalogService) {
          product = catalogService.findProductById(id);
        }
      }
    } catch (RuntimeException exception) {
      throwUnresolvable(id, siteId, exception);
    }
    if(null == product || null == site) {
      throwUnresolvable(id, siteId, null);
    }
    return new ProductInSiteImpl(product, site);
  }

  private void throwUnresolvable(String id, String siteId, Throwable exception) {
    throw new UnresolvableReferenceException(String.format("Product with ID %s and site ID %s could not be resolved",
            id, siteId), exception);
  }
}

