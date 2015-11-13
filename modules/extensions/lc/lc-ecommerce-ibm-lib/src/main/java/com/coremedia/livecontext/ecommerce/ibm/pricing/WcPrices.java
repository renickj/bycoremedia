package com.coremedia.livecontext.ecommerce.ibm.pricing;

import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WcPrices {

  protected Map<String, WcPrice> prices;
  protected Map<String, Object> dataMap;

  public void setDataMap(Map<String, Object> data) {
    if (data != null) {
      dataMap = data;

      List priceArr = (List) DataMapHelper.getValueForPath(data, "catalogEntryView.price");
      if (priceArr == null) {
        priceArr = (List) DataMapHelper.getValueForPath(data, "price");
      }
      if (priceArr != null && !priceArr.isEmpty()) {
        prices = new HashMap<>();
        for (Object priceEntry : priceArr) {
          Map priceMap = (Map) priceEntry;
          //{currency=EUR, description=L, usage=Display, value=}
          WcPrice wcPrice = new WcPrice();
          wcPrice.setPriceDescription((String) priceMap.get("description"));
          wcPrice.setPriceUsage((String) priceMap.get("usage"));
          wcPrice.setPriceValue((String) priceMap.get("value"));
          wcPrice.setCurrency((String) priceMap.get("currency"));

          prices.put((String) priceMap.get("usage"), wcPrice);
        }
      }
    }
  }

  public Map<String, WcPrice> getPrices() {
    return prices;
  }
}
