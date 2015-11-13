package com.coremedia.livecontext.ecommerce.ibm.inventory;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import java.util.List;
import java.util.Map;

public class AvailabilityByIdsCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private WcAvailabilityWrapperService wrapperService;

  public AvailabilityByIdsCacheKey(String id,
                                   StoreContext storeContext,
                                   WcAvailabilityWrapperService wrapperService,
                                   CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_AVAILABILITY, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.getInventoryAvailability(id, storeContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> wcInventoryAvailabilities) {
    List<Map<String, Object>> inventoryAvailabilityList = DataMapHelper.getValueForKey(wcInventoryAvailabilities, "InventoryAvailability", List.class);
    for (Map<String, Object> wcInventoryAvailability : inventoryAvailabilityList) {
      Cache.dependencyOn(DataMapHelper.getValueForKey(wcInventoryAvailability, "productId", String.class));
    }
  }

}