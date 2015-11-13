package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;

import java.util.Map;

public class MarketingSpotsCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {
  public static final String DEPENDENCY_ALL_MARKETING_SPOTS = "invalidate-all-marketing-spots-event";

  private WcMarketingSpotWrapperService wrapperService;

  public MarketingSpotsCacheKey(StoreContext storeContext,
                                UserContext userContext,
                                WcMarketingSpotWrapperService wrapperService,
                                CommerceCache commerceCache) {
    super("marketingSpots", storeContext, userContext, CONFIG_KEY_MARKETING_SPOTS, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.findMarketingSpots(storeContext, userContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> wcMarketingSpots) {
    Cache.dependencyOn(DEPENDENCY_ALL_MARKETING_SPOTS);
  }
}