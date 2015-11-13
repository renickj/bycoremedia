package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;
import java.util.TimeZone;

public class StoreInfoServiceImpl implements StoreInfoService {

  private WcStoreInfoWrapperService wrapperService;
  private CommerceCache commerceCache;

  @Override
  public String getStoreId(String storeName) {
    Map<String, Object> storeInfos = (Map<String, Object>) commerceCache.get(new StoreInfoCacheKey(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO, wrapperService, commerceCache));
    return (String) DataMapHelper.getValueForPath(storeInfos, "stores." + storeName + ".storeId");
  }

  @Override
  public String getDefaultCatalogId(String storeName) {
    Map<String, Object> storeInfos = (Map<String, Object>) commerceCache.get(new StoreInfoCacheKey(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO, wrapperService, commerceCache));
    return (String) DataMapHelper.getValueForPath(storeInfos, "stores." + storeName + ".defaultCatalogId");
  }

  @Override
  public String getCatalogId(String storeName, String catalogName) {
    Map<String, Object> storeInfos = (Map<String, Object>) commerceCache.get(new StoreInfoCacheKey(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO, wrapperService, commerceCache));
    return (String) DataMapHelper.getValueForPath(storeInfos, "stores." + storeName + ".catalogs."+catalogName);
  }

  @Override
  public Map<String, String> getCatalogs(String storeName) {
    Map<String, Object> storeInfos = (Map<String, Object>) commerceCache.get(new StoreInfoCacheKey(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO, wrapperService, commerceCache));
    return (Map<String, String>) DataMapHelper.getValueForPath(storeInfos, "stores." + storeName + ".catalogs");
  }

  public TimeZone getTimeZone() {
    Map<String, Object> storeInfos = (Map<String, Object>) commerceCache.get(new StoreInfoCacheKey(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO, wrapperService, commerceCache));
    String sTimeZoneId = DataMapHelper.getValueForPath(storeInfos, "serverTimezoneId", String.class);
    return TimeZone.getTimeZone(sTimeZoneId);
  }

  @Override
  public boolean isAvailable() {
    try {
      Map<String, Object> storeInfos = (Map<String, Object>) commerceCache.get(new StoreInfoCacheKey(AbstractCommerceCacheKey.CONFIG_KEY_STORE_INFO, wrapperService, commerceCache));
      return storeInfos != null && !storeInfos.isEmpty();
    }catch (CommerceException ex){
      return false;
    }
  }

  public WcStoreInfoWrapperService getWrapperService() {
    return wrapperService;
  }

  @Required
  public void setWrapperService(WcStoreInfoWrapperService wrapperService) {
    this.wrapperService = wrapperService;
  }

  public CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

}