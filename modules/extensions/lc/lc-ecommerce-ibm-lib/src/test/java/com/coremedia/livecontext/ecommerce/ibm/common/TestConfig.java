package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TestConfig {

  public static final String STORE_CONFIG_ID = System.getProperty("lc.test.configID","aurora");
  public static final String STORE_ID = System.getProperty("lc.test.storeId","10202");
  public static final String STORE_NAME = System.getProperty("lc.test.storeName","AuroraESite");
  public static final String B2B_STORE_ID = System.getProperty("lc.test.storeId","10303");
  public static final String B2B_STORE_NAME = System.getProperty("lc.test.storeName","AuroraB2BESite");

  public static String CATALOG_ID = System.getProperty("lc.test.catalogId","10051");

  public static String CATALOG_ID_FEP8 = System.getProperty("lc.test.catalogId","10101");
  public static String STORE_ID_FEP8 = System.getProperty("lc.test.storeId","10302");

  public static final String LOCALE = "en_US";
  public static final String CURRENCY = "USD";
  public static final String WORKSPACE_ID = "4711";
  public static final String CONNECTION_ID = "wcs1";

  protected float wcsVersion = StoreContextHelper.WCS_VERSION_DEFAULT;

  public void setWcsVersion(String wcsVersion) {
    this.wcsVersion = Float.parseFloat(wcsVersion);
  }

  public static final StoreContext STORE_CONTEXT_WITH_WORKSPACE = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
  {
    STORE_CONTEXT_WITH_WORKSPACE.setWorkspaceId(WORKSPACE_ID);
  }

  public static final StoreContext STORE_CONTEXT_WITHOUT_CATALOG_ID = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, null, LOCALE, CURRENCY);
  {
    STORE_CONTEXT_WITH_WORKSPACE.setWorkspaceId(WORKSPACE_ID);
  }

  public StoreContext getStoreContext() {
    StoreContext result = null;
    if (StoreContextHelper.WCS_VERSION_7_8 == wcsVersion){
      result = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID_FEP8, STORE_NAME, CATALOG_ID_FEP8, LOCALE, CURRENCY);
    } else {
      result = StoreContextHelper.createContext(STORE_CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    }
    Map replacements = new HashMap<String, String>();
    replacements.put("storeId", result.getStoreId());
    replacements.put("catalogId", result.getCatalogId());
    replacements.put("locale", result.getLocale());
    StoreContextHelper.setReplacements(result, replacements);

    StoreContextHelper.setWcsVersion(result, Float.toString(wcsVersion));
    return result;
  }

  public StoreContext getB2BStoreContext() {
    StoreContext result = StoreContextHelper.createContext(STORE_CONFIG_ID, B2B_STORE_ID, B2B_STORE_NAME, CATALOG_ID_FEP8, LOCALE, CURRENCY);
    StoreContextHelper.setWcsVersion(result, Float.toString(wcsVersion));
    return result;
  }

  public StoreContext getStoreContextWithWorkspace() {
    StoreContext result = getStoreContext();
    result.setWorkspaceId(WORKSPACE_ID);
    StoreContextHelper.setWcsVersion(result, Float.toString(wcsVersion));
    return result;
  }

  public String getStoreName() {
    return StoreContextHelper.getStoreName(getStoreContext());
  }

  public String getStoreId() {
    return StoreContextHelper.getStoreId(getStoreContext());
  }

  public Locale getLocale() {
    return StoreContextHelper.getLocale(getStoreContext());
  }

}
