package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;

import java.util.Map;

public class SegmentsByUserCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private WcSegmentWrapperService wrapperService;

  public SegmentsByUserCacheKey(StoreContext storeContext,
                                UserContext userContext,
                                WcSegmentWrapperService wrapperService,
                                CommerceCache commerceCache) {
    super("segmentsByUser", storeContext, userContext, CONFIG_KEY_SEGMENTS_BY_USER, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.findSegmentsByUser(storeContext, userContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> segments) {
    //TODO
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" + user +":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" +
            storeContext.getWorkspaceId() + ":" + storeContext.getPreviewDate();
  }

}
