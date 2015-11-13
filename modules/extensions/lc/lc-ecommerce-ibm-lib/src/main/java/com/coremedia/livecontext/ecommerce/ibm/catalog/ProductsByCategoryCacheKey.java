package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProductsByCategoryCacheKey extends AbstractCommerceCacheKey<List<Map<String, Object>>> {

  private WcCatalogWrapperService wrapperService;

  public ProductsByCategoryCacheKey(String id,
                                    StoreContext storeContext,
                                    WcCatalogWrapperService wrapperService,
                                    CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_PRODUCTS_BY_CATEGORY, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public List<Map<String, Object>> computeValue(Cache cache) {
    return wrapperService.findProductsByCategoryId(id, storeContext);
  }

  @Override
  public void addExplicitDependency(List<Map<String, Object>> wcProducts) {
    if (wcProducts != null &&
            !wcProducts.isEmpty() &&
            DataMapHelper.getValueForKey(wcProducts.get(0), "parentCatalogGroupID[0]") != null) {
      Cache.dependencyOn(DataMapHelper.getValueForKey(wcProducts.get(0), "parentCatalogGroupID[0]", String.class));
    }
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" + storeContext.getCurrency() + ":" +
            storeContext.getWorkspaceId() + ":" + Arrays.toString(storeContext.getContractIds());
  }

}