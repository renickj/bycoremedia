package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;

import java.util.Map;

public class SegmentCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private WcSegmentWrapperService wrapperService;

  public SegmentCacheKey(String id,
                         StoreContext storeContext,
                         UserContext userContext,
                         WcSegmentWrapperService wrapperService,
                         CommerceCache commerceCache) {
    super(id, storeContext, userContext, CONFIG_KEY_SEGMENT, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.findSegmentById(id, storeContext, userContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> segment) {
    Cache.dependencyOn(SegmentsCacheKey.DEPENDENCY_ALL_SEGMENTS);
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" + user +":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" +
            storeContext.getWorkspaceId();
  }

}