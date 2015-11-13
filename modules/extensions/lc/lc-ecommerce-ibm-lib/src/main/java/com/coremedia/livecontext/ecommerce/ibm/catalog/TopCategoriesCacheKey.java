package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TopCategoriesCacheKey extends AbstractCommerceCacheKey<List<Map<String, Object>>> {

  private WcCatalogWrapperService wrapperService;

  public TopCategoriesCacheKey(StoreContext storeContext,
                               WcCatalogWrapperService wrapperService,
                               CommerceCache commerceCache) {
    super("root", storeContext, CONFIG_KEY_TOP_CATEGORIES, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public List<Map<String, Object>> computeValue(Cache cache) {
    return wrapperService.findTopCategories(storeContext);
  }

  @Override
  public void addExplicitDependency(List<Map<String, Object>> wcCategories) {
    //TODO
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" +
            storeContext.getWorkspaceId() + ":" + Arrays.toString(storeContext.getContractIds());
  }
}