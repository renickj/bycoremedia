package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SubCategoriesCacheKey extends AbstractCommerceCacheKey<List<Map<String, Object>>> {

  private WcCatalogWrapperService wrapperService;

  public SubCategoriesCacheKey(String id,
                               StoreContext storeContext,
                               WcCatalogWrapperService wrapperService,
                               CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_SUB_CATEGORIES, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public List<Map<String, Object>> computeValue(Cache cache) {
    return wrapperService.findSubCategories(id, storeContext);
  }

  @Override
  public void addExplicitDependency(List<Map<String, Object>> wcCategories) {
    Cache.dependencyOn(this.id);
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" +
            storeContext.getWorkspaceId() + ":" + Arrays.toString(storeContext.getContractIds());
  }
}