package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;

import java.util.Map;

public class SegmentsCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  public static final String DEPENDENCY_ALL_SEGMENTS = "invalidate-all-segments-event";

  private WcSegmentWrapperService wrapperService;

  public SegmentsCacheKey(StoreContext storeContext,
                          UserContext userContext,
                          WcSegmentWrapperService wrapperService,
                          CommerceCache commerceCache) {
    super("segments", storeContext, userContext, CONFIG_KEY_SEGMENTS, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.findAllSegments(storeContext, userContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> segments) {
    Cache.dependencyOn(DEPENDENCY_ALL_SEGMENTS);
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" + user +":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" +
            storeContext.getWorkspaceId();
  }

}