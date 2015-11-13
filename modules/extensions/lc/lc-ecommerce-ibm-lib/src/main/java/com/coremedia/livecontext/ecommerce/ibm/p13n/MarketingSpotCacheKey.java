package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class MarketingSpotCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private WcMarketingSpotWrapperService wrapperService;

  public MarketingSpotCacheKey(String id,
                               StoreContext storeContext,
                               UserContext userContext,
                               WcMarketingSpotWrapperService wrapperService,
                               CommerceCache commerceCache) {
    super(id, storeContext, userContext, CONFIG_KEY_MARKETING_SPOT, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.findMarketingSpotById(id, storeContext, userContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> wcMarketingSpot) {
    if (wcMarketingSpot != null) {
      String resourceName = DataMapHelper.getValueForKey(wcMarketingSpot, "resourceName", String.class);
      if (resourceName != null) {
        String dependencyFieldIdentifier = DataMapHelper.getValueForKey(wcMarketingSpot,
                "espot".equals(resourceName) ? "MarketingSpotData[0].marketingSpotIdentifier" : "MarketingSpot[0].spotId", String.class);
        String valueForKey = StringUtils.isEmpty(dependencyFieldIdentifier) ?
                null : DataMapHelper.getValueForPath(wcMarketingSpot, dependencyFieldIdentifier, String.class);
        if (!StringUtils.isEmpty(valueForKey)) {
          Cache.dependencyOn(valueForKey);
        }
      }
    }
  }

}