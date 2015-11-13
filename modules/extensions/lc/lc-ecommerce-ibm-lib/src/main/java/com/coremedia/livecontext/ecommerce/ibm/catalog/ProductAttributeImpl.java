package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductAttributeImpl implements ProductAttribute {

  private Map<String, Object> delegate;

  public ProductAttributeImpl(Map<String, Object> delegate) {
    this.delegate = delegate;
  }

  @Override
  public String getId() {
    return DataMapHelper.getValueForKey(delegate, "identifier", String.class);
  }

  @Override
  public String getType() {
    return DataMapHelper.getValueForKey(delegate, "dataType", String.class);
  }

  @Override
  public String getUnit() {
    return DataMapHelper.getValueForKey(delegate, "unit", String.class);
  }

  @Override
  public String getExternalId() {
    return DataMapHelper.getValueForKey(delegate, "uniqueID", String.class);
  }

  @Override
  public String getDisplayName() {
    return DataMapHelper.getValueForKey(delegate, "name", String.class);
  }

  @Override
  public String getDescription() {
    return DataMapHelper.getValueForKey(delegate, "description", String.class);
  }

  @Override
  public Object getValue() {
    Object value = null;
    //noinspection unchecked
    List<Map<String, Object>> valueForKey = DataMapHelper.getValueForKey(delegate, "values", List.class);
    if (valueForKey != null && !valueForKey.isEmpty()) {
      value = DataMapHelper.getValueForKey(valueForKey.get(0), "value");
    }
    return value;
  }

  @Override
  public List<Object> getValues() {
    List<Object> result = new ArrayList<>();
    //noinspection unchecked
    List<Map<String, Object>> valueForKey = DataMapHelper.getValueForKey(delegate, "values", List.class);
    if (valueForKey != null && !valueForKey.isEmpty()) {
      for (Map<String, Object> item : valueForKey) {
        Object value = DataMapHelper.getValueForKey(item, "value");
        if (value != null) {
          result.add(value);
        }
      }
    }
    return result;
  }

  @Override
  public boolean isDefining() {
    return "Defining".equals(DataMapHelper.getValueForKey(delegate, "usage", String.class));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ProductAttributeImpl that = (ProductAttributeImpl) o;

    if (delegate != null) {
      return getId().equals(that.getId());
    }

    return that.delegate == null;
  }

  @Override
  public int hashCode() {
    return delegate != null ? getId().hashCode() : 0;
  }
}
