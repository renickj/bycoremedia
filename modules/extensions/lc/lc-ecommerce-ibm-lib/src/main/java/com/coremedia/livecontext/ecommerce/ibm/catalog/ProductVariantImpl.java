package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.inventory.AvailabilityInfoImpl;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.Assert.notNull;

public class ProductVariantImpl extends ProductBase implements ProductVariant {

  @Override
  protected Map<String, Object> getDelegate() {
    if (delegate == null) {
      delegate = (Map<String, Object>) getCommerceCache().get(
        new ProductCacheKey(getId(), getContext(), getCatalogWrapperService(), getCommerceCache()));
      if (delegate == null) {
        throw new NotFoundException(getId() + " (sku not found in catalog)");
      }
    }
    return delegate;
  }

  /**
   * @throws CommerceException
   */
  @Override
  public void load() {
    getDelegate();
  }

  @Override
  public String getReference() {
    return CommerceIdHelper.formatProductVariantId(getExternalId());
  }

  @Override
  @Nullable
  public Product getParent() {
    String parentProductID = DataMapHelper.getValueForKey(getDelegate(), "parentCatalogEntryID", String.class);
    if (parentProductID != null) {
      return (Product) getCommerceBeanFactory().createBeanFor(
              CommerceIdHelper.formatProductTechId(parentProductID), getContext());
    }
    return null;
  }

  @Override
  @Nullable
  public AvailabilityInfo getAvailabilityInfo() {
    Map<ProductVariant, AvailabilityInfo> availabilityMap = getAvailabilityMap();
    for (final Map.Entry<ProductVariant, AvailabilityInfo> productVariantAvailabilityInfoEntry : availabilityMap.entrySet()) {
      if (productVariantAvailabilityInfoEntry.getKey().getExternalId().equals(getExternalId())) {
        return productVariantAvailabilityInfoEntry.getValue();
      }
    }
    // fallback if no availability information were found.
    Map<String, Object> wcInfo = new HashMap<>(1);
    wcInfo.put("availableQuantity", 0.0f);
    wcInfo.put("unitOfMeasure", "C62");
    wcInfo.put("inventoryStatus", "Unavailable");
    return new AvailabilityInfoImpl(wcInfo);
  }

  @Override
  @Nullable
  public Object getAttributeValue(@Nonnull String attributeId) {

    notNull(attributeId);

    List<ProductAttribute> attributes = getDefiningAttributes();
    for (ProductAttribute attribute : attributes) {
      if (attributeId.equals(attribute.getId())) {
        return attribute.getValue();
      }
    }
    attributes = getDescribingAttributes();
    for (ProductAttribute attribute : attributes) {
      if (attributeId.equals(attribute.getId())) {
        return attribute.getValue();
      }
    }
    return null;
  }

  // Methods that are directed to the parent product (for sake of convenience)
  //...

  @Nonnull
  @Override
  public List<String> getVariantAxisNames() {
    Product parent = getParent();
    return parent != null ? parent.getVariantAxisNames() : Collections.<String>emptyList();
  }

  @Override
  @Nonnull
  public List<ProductVariant> getVariants() {
    Product parent = getParent();
    return parent != null ? parent.getVariants() : Collections.<ProductVariant>emptyList();
  }

  @Override
  @Nonnull
  public List<ProductVariant> getVariants(@Nullable List<VariantFilter> filters) {
    Product parent = getParent();
    return parent != null ? parent.getVariants(filters) : Collections.<ProductVariant>emptyList();
  }

  @Override
  @Nonnull
  public List<ProductVariant> getVariants(@Nullable VariantFilter filter) {
    Product parent = getParent();
    return parent != null ? parent.getVariants(filter) : Collections.<ProductVariant>emptyList();
  }

  @Override
  @Nonnull
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable List<VariantFilter> filters) {
    Product parent = getParent();
    return parent != null ? parent.getVariantAxisValues(axisName, filters) : Collections.emptyList();
  }

  @Override
  @Nonnull
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable VariantFilter filter) {
    Product parent = getParent();
    return parent != null ? parent.getVariantAxisValues(axisName, filter) : Collections.emptyList();
  }

  @Override
  @Nonnull
  public Map<ProductVariant, AvailabilityInfo> getAvailabilityMap() {
    Product parent = getParent();
    return parent != null ? parent.getAvailabilityMap() : Collections.<ProductVariant, AvailabilityInfo>emptyMap();
  }

  @Override
  public float getTotalStockCount() {
    Product parent = getParent();
    return parent != null ? parent.getTotalStockCount() : 0.0f;
  }

  @Override
  public boolean isAvailable() {
    AvailabilityInfo availabilityInfo = getAvailabilityInfo();
    return Boolean.parseBoolean(DataMapHelper.getValueForKey(getDelegate(), "buyable", String.class))
            && availabilityInfo != null && availabilityInfo.getQuantity() > 0;
  }

  @Override
  public String toString() {
    return "[SKU " + getId() + "]";
  }

  @Override
  protected void loadAttributes() {
    super.loadAttributes();
    Product parent = getParent();
    if (parent != null) {
      List<ProductAttribute> myDescribingAttributes = getDescribingAttributes();
      List<ProductAttribute> parentDescribingAttributes = parent.getDescribingAttributes();
      if (myDescribingAttributes.isEmpty()) {
        myDescribingAttributes.addAll(parentDescribingAttributes);
      }
      else {
        for (ProductAttribute attribute : parentDescribingAttributes) {
          if (!myDescribingAttributes.contains(attribute)) {
            myDescribingAttributes.add(attribute);
          }
        }
      }
    }
  }

}
