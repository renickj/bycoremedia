package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.Map;

public class FixValueCacheKey extends AbstractCommerceCacheKey<Object> {

  Object value;

  public FixValueCacheKey(String id,
                          StoreContext storeContext,
                          Object value,
                          String configKey,
                          CommerceCache commerceCache) {
    super(id, storeContext, configKey, commerceCache);
    this.value = value;
  }

  @Override
  public Object computeValue(Cache cache) {
    return value;
  }

  @Override
  public void addExplicitDependency(Object o) {
    //TODO: fix value cache only works for Products and Categories by now
    Map<String, Object> commerceObject = (Map<String, Object>) o;
    String resourceName = DataMapHelper.getValueForPath(commerceObject, "resourceName", String.class);

    switch (configKey) {
      case CONFIG_KEY_PRODUCT: Cache.dependencyOn(DataMapHelper.getValueForPath(commerceObject, "uniqueID", String.class)); break;
      case CONFIG_KEY_CATEGORY: Cache.dependencyOn(DataMapHelper.getValueForPath(commerceObject, "uniqueID", String.class)); break;
      case CONFIG_KEY_MARKETING_SPOT: Cache.dependencyOn(DataMapHelper.getValueForPath(commerceObject,
              "espot".equals(resourceName) ? "MarketingSpotData[0].marketingSpotIdentifier" : "MarketingSpot[0].spotId", String.class)); break;
    }
  }

  @Override
  protected String getCacheIdentifier() {
    if (!(value == null || value instanceof Map)) {
      Map<String, Object> commerceObject = (Map<String, Object>) value;
      Object resourceName = commerceObject.get("resourceName");
      if (!("spot".equals(resourceName) || "espot".equals(resourceName))) {
        return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" +
                storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" + storeContext.getCurrency() + ":" +
                storeContext.getWorkspaceId();
      }
    }
    return super.getCacheIdentifier();
  }
}