package com.coremedia.livecontext.ecommerce.ibm.storeinfo;

import java.util.Map;
import java.util.TimeZone;

/**
 * Defines an interface to retrieve availability information for {@link com.coremedia.livecontext.ecommerce.catalog.ProductVariant}.
 */
public interface StoreInfoService {

  String getStoreId(String storeName);

  String getDefaultCatalogId(String storeName);

  String getCatalogId(String storeName, String catalogName);

  Map<String, String> getCatalogs(String storeName);

  boolean isAvailable();

  TimeZone getTimeZone();
}
