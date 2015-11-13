package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.storeinfo.StoreInfoService;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class StoreContextProviderImpl extends AbstractStoreContextProvider {

  private float wcsVersion = StoreContextHelper.WCS_VERSION_DEFAULT;

  private StoreInfoService storeInfoService;

  public void setWcsVersion(String wcsVersion) {
    this.wcsVersion = Float.parseFloat(wcsVersion);
  }

  public void setStoreInfoService(StoreInfoService storeInfoService) {
    this.storeInfoService = storeInfoService;
  }

  // --- StoreContextProvider -------------------------------------

  @Override
  protected StoreContext internalCreateContext(@Nonnull Site site) {
    StoreContext result = null;
    // only create catalog context if settings were found for current site
    Struct repositoryStoreConfig = getSettingsService().setting(CONFIG_KEY_STORE_CONFIG, Struct.class, site.getSiteRootDocument());
    if (repositoryStoreConfig != null) {
      try {
        Map<String, Object> targetConfig = new HashMap<>();

        String configId = StructUtil.getString(repositoryStoreConfig, CONFIG_KEY_CONFIG_ID);

        readStoreConfigFromSpring(configId, targetConfig);
        updateStoreConfigFromRepository(repositoryStoreConfig, targetConfig);
        updateStoreConfigFromDynamicStoreInfo(site.getName(), targetConfig);

        result = StoreContextHelper.createContext(
                (String) targetConfig.get(CONFIG_KEY_CONFIG_ID),
                (String) targetConfig.get(CONFIG_KEY_STORE_ID),
                (String) targetConfig.get(CONFIG_KEY_STORE_NAME),
                (String) targetConfig.get(CONFIG_KEY_CATALOG_ID),
                site.getLocale().toString(),
                (String) targetConfig.get(CONFIG_KEY_CURRENCY)
        );

        StoreContextHelper.setConfigId(result, configId);
        StoreContextHelper.setSiteId(result, site.getId());
        StoreContextHelper.setWorkspaceId(result, (String) targetConfig.get(CONFIG_KEY_WORKSPACE_ID));
        StoreContextHelper.setWcsVersion(result, Float.toString(wcsVersion));
        StoreContextHelper.setReplacements(result, (Map<String, String>) targetConfig.get(CONFIG_KEY_REPLACEMENTS));
        StoreContextHelper.setWcsTimeZone(result, (Map<String, String>) targetConfig.get("wcsTimeZone"));

      } catch (NoSuchPropertyDescriptorException e) {
        throw new InvalidContextException("Missing properties in store configuration. ", e);
      }
    }
    return result;
  }

  protected void updateStoreConfigFromDynamicStoreInfo(String siteName, @Nonnull Map<String, Object> targetStoreConfig) {
    if (storeInfoService != null && storeInfoService.isAvailable()) {

      String storeName = (String) targetStoreConfig.get(CONFIG_KEY_STORE_NAME);
      if (StringUtils.isBlank(storeName)) {
        throw new InvalidContextException("No store name found in config (site: " + siteName + ").");
      }

      String storeId = (String) targetStoreConfig.get(CONFIG_KEY_STORE_ID);
      if (storeId == null) {
        storeId = storeInfoService.getStoreId(storeName);
        if (StringUtils.isBlank(storeId)) {
          throw new InvalidContextException("No store id found for store '" + storeName + "' in wcs (site: " + siteName + ").");
        }
        targetStoreConfig.put(CONFIG_KEY_STORE_ID, storeId);
      }

      String catalogId = (String) targetStoreConfig.get(CONFIG_KEY_CATALOG_ID);
      if (catalogId == null) {
        String catalogName = (String) targetStoreConfig.get(CONFIG_KEY_CATALOG_NAME);
        if (catalogName != null) {
          catalogId = storeInfoService.getCatalogId(storeName, catalogName);
          if (StringUtils.isBlank(catalogId)) {
            throw new InvalidContextException("No catalog '" + catalogName + "' found in wcs (site: " + siteName + ").");
          }
        }
        else {
          catalogId = storeInfoService.getDefaultCatalogId(storeName);
          if (StringUtils.isBlank(catalogId)) {
            throw new InvalidContextException("No default catalog id found for store '" + storeName + "' in wcs (site: " + siteName + ").");
          }
        }
        targetStoreConfig.put(CONFIG_KEY_CATALOG_ID, catalogId);
      }
      Map<String, String> timeZone = new HashMap<>();
      timeZone.put("id", storeInfoService.getTimeZone().getID());
      targetStoreConfig.put("wcsTimeZone", timeZone);
    }
  }

}
