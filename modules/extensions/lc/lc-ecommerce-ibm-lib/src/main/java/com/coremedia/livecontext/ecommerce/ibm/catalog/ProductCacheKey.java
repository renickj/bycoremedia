package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import java.util.Arrays;
import java.util.Map;

public class ProductCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private  static final String UNIQUE_ID = "uniqueID";

  private WcCatalogWrapperService wrapperService;

  public ProductCacheKey(String id,
                         StoreContext storeContext,
                         WcCatalogWrapperService wrapperService,
                         CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_PRODUCT, commerceCache);
    this.wrapperService = wrapperService;
    if (!CommerceIdHelper.isProductId(id) && !CommerceIdHelper.isProductVariantId(id)) {
      throw new InvalidIdException(id + " (is neither a product nor sku id)");
    }

  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.findProductById(id, storeContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> wcProduct) {
    if (wcProduct != null && wcProduct.containsKey(UNIQUE_ID)) {
      Cache.dependencyOn(DataMapHelper.getValueForKey(wcProduct, UNIQUE_ID, String.class));
    }
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" + storeContext.getCurrency() + ":" +
            storeContext.getWorkspaceId() + ":" + Arrays.toString(storeContext.getContractIds());
  }

}