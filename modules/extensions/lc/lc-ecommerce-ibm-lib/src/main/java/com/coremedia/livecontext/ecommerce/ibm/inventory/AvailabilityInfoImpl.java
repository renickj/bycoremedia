package com.coremedia.livecontext.ecommerce.ibm.inventory;

import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;

import java.util.Map;


public class AvailabilityInfoImpl implements AvailabilityInfo {
  Map<String, Object> delegate;

  public AvailabilityInfoImpl(Map<String, Object> delegate) {
    this.delegate = delegate;
  }

  @Override
  public float getQuantity() {
    return Float.parseFloat(DataMapHelper.getValueForKey(delegate, "availableQuantity", String.class));
  }

  @Override
  public String getInventoryStatus() {
    //TODO: translate and make use of interface constants
    return DataMapHelper.getValueForKey(delegate, "inventoryStatus", String.class);
  }

  @Override
  public String getUnitOfMeasure() {
    //TODO: translate result codes
    return DataMapHelper.getValueForKey(delegate, "unitOfMeasure", String.class);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AvailabilityInfoImpl that = (AvailabilityInfoImpl) o;

    return !(delegate != null ? !DataMapHelper.getValueForKey(delegate, "productId", String.class).
            equals(DataMapHelper.getValueForKey(that.delegate, "productId", String.class)) : that.delegate != null);

  }

  @Override
  public int hashCode() {
    return delegate != null ?DataMapHelper.getValueForKey(delegate, "productId", String.class).hashCode() : 0;
  }
}
