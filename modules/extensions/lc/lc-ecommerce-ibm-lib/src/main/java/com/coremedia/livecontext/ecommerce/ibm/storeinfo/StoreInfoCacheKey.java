package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;

import java.util.Map;

public class StoreInfoCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private WcStoreInfoWrapperService wrapperService;

  public StoreInfoCacheKey(String id,
                           WcStoreInfoWrapperService wrapperService,
                           CommerceCache commerceCache) {
    super(id, null, CONFIG_KEY_STORE_INFO, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.getStoreInfos();
  }

  @Override
  public void addExplicitDependency(Map<String, Object> wcStoreInfos) {
    Cache.dependencyOn(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO);
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey;
  }

}