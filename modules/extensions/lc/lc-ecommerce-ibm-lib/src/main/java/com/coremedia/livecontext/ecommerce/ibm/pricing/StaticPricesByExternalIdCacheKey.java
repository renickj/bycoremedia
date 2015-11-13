package com.coremedia.livecontext.ecommerce.ibm.pricing;

import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.ibm.catalog.WcCatalogWrapperService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.Arrays;

public class StaticPricesByExternalIdCacheKey extends AbstractCommerceCacheKey<WcPrices> {

  private WcCatalogWrapperService wrapperService;

  public StaticPricesByExternalIdCacheKey(String id,
                                          StoreContext storeContext,
                                          WcCatalogWrapperService wrapperService,
                                          CommerceCache commerceCache) {
    super(id, storeContext, CONFIG_KEY_STATIC_PRICES, commerceCache);
    this.wrapperService = wrapperService;
  }

  @Override
  public WcPrices computeValue(Cache cache) {
    return wrapperService.findStaticProductPricesByExternalId(id, storeContext);
  }

  @Override
  public void addExplicitDependency(WcPrices wcProductPrices) {
    //TODO: should be dependent to the product
  }

  @Override
  protected String getCacheIdentifier() {
    return id + ":" + configKey + ":" + storeContext.getSiteId() + ":" +
            storeContext.getStoreId() + ":" + storeContext.getCatalogId() + ":" + storeContext.getLocale() + ":" + storeContext.getCurrency() + ":" +
            storeContext.getWorkspaceId() + ":" + Arrays.toString(storeContext.getContractIds());
  }
}