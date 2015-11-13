package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.cache.Cache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;

import java.util.Map;

public class FindCommercePersonCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private WcPersonWrapperService wrapperService;

  public FindCommercePersonCacheKey(
          String id,
          StoreContext storeContext,
          UserContext userContext,
          WcPersonWrapperService wrapperService,
          CommerceCache commerceCache) {
    super(id, storeContext, userContext, CONFIG_KEY_FIND_CURRENT_USER, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.findPerson(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext());
  }

  @Override
  public void addExplicitDependency(Map<String, Object> wcPerson) {
    //nothing to do
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" + user + ":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale();
  }

}
