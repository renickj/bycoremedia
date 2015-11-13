package com.coremedia.livecontext.ecommerce.ibm.contract;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;

import java.util.Map;

public class ContractCacheKey extends AbstractCommerceCacheKey<Map<String, Object>> {

  public static final String DEPENDENCY_ALL_CONTRACTS = "invalidate-all-contracts-event";

  private WcContractWrapperService wrapperService;

  public ContractCacheKey(String id,
                          StoreContext storeContext,
                          UserContext userContext,
                          WcContractWrapperService wrapperService,
                          CommerceCache commerceCache) {
    super(id, storeContext, userContext, CONFIG_KEY_CONTRACT, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public Map<String, Object> computeValue(Cache cache) {
    String techId = id;
    if (CommerceIdHelper.isContractId(id)){
      techId = CommerceIdHelper.parseExternalIdFromId(id);
    }

    return wrapperService.findContractByTechId(techId, storeContext, userContext);
  }

  @Override
  public void addExplicitDependency(Map<String, Object> contract) {
    Cache.dependencyOn(DEPENDENCY_ALL_CONTRACTS);
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" + user +":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" +
            storeContext.getWorkspaceId();
  }

}