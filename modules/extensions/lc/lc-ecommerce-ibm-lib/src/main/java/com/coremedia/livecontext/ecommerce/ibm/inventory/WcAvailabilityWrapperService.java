package com.coremedia.livecontext.ecommerce.ibm.inventory;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCatalogId;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Arrays.asList;

/**
 * A service that uses the getRestConnector() to get inventory wrappers by certain search queries.
 */
public class WcAvailabilityWrapperService extends AbstractWcWrapperService {

    private static final WcRestConnector.WcRestServiceMethod<Map, Void>
            GET_AVAILABILITY_FOR_PRODUCT_VARIANTS = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/inventoryavailability/{productVariantList}", false, true, Map.class);

  public Map<String, Object> getInventoryAvailability(String skuIds, StoreContext storeContext) {
    if (skuIds == null || skuIds.isEmpty()) {
      return Collections.emptyMap();
    }
    //noinspection unchecked
    Map<String, Object> wcInventoryAvailabilityList = getRestConnector().callService(
            GET_AVAILABILITY_FOR_PRODUCT_VARIANTS, asList(getStoreId(storeContext), skuIds),
            createParametersMap(getCatalogId(storeContext), getLocale(storeContext), null), null, storeContext, null);

    if (wcInventoryAvailabilityList == null) {
      return Collections.emptyMap();
    }

    return wcInventoryAvailabilityList;
  }

}
