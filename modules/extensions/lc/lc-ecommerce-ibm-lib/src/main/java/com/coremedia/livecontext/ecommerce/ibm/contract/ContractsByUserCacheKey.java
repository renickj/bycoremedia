package com.coremedia.livecontext.ecommerce.ibm.contract;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;

import java.util.Arrays;
import java.util.Map;

public class ContractsByUserCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  private WcContractWrapperService wrapperService;

  public ContractsByUserCacheKey(UserContext userContext,
                                 StoreContext storeContext,
                                 WcContractWrapperService wrapperService,
                                 CommerceCache commerceCache) {
    super(userContext.getUserId() + "_" + userContext.getUserName(), storeContext, userContext, CONFIG_KEY_CONTRACTS_BY_USER, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    return wrapperService.findContractsForUser(userContext, storeContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> stringObjectMap) {
    Cache.dependencyOn(ContractCacheKey.DEPENDENCY_ALL_CONTRACTS);
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" + storeContext.getCurrency() + ":" +
            storeContext.getWorkspaceId() + ":" + Arrays.toString(storeContext.getContractIds());
  }
}
