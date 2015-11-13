package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import java.util.Arrays;
import java.util.Map;

public class CategoryCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private WcCatalogWrapperService wrapperService;

  public CategoryCacheKey(String id,
                          StoreContext storeContext,
                          WcCatalogWrapperService wrapperService,
                          CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_CATEGORY, commerceCache);
    this.wrapperService = wrapperService;
    if (!CommerceIdHelper.isCategoryId(id)) {
      throw new InvalidIdException(id + " (is not a category id)");
    }
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.findCategoryById(id, storeContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> wcCategory) {
    if (wcCategory != null){
      Cache.dependencyOn(DataMapHelper.getValueForKey(wcCategory, "uniqueID", String.class));
    }
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" +
            storeContext.getWorkspaceId() + ":" + Arrays.toString(storeContext.getContractIds());
  }
}