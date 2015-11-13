package com.coremedia.livecontext.ecommerce.ibm.pricing;

import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WcPricesV7_6 extends WcPrices {

  @Override
  public void setDataMap(Map<String, Object> data) {
    if (data != null) {
      dataMap = data;

      List priceArr = (List) DataMapHelper.getValueForPath(data, "CatalogEntryView.Price");
      if (priceArr != null && !priceArr.isEmpty()) {
        prices = new HashMap<>();
        for (Object priceEntry : priceArr) {
          Map priceMap = (Map) priceEntry;
          WcPrice wcPrice = new WcPrice();
          wcPrice.setPriceDescription((String) priceMap.get("priceDescription"));
          wcPrice.setPriceUsage((String) priceMap.get("priceUsage"));
          wcPrice.setPriceValue((String) priceMap.get("priceValue"));
          prices.put((String) priceMap.get("priceUsage"), wcPrice);
        }
      }
    }
  }

}
