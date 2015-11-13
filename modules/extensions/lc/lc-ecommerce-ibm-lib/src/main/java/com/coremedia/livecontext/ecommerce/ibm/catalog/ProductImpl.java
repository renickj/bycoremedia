package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.Assert.notNull;

public class ProductImpl extends ProductBase {

  private List<String> variantAxis;
  private List<ProductVariant> variants;

  private AvailabilityService availabilityService;

  @Override
  @SuppressWarnings("unchecked")
  protected Map<String, Object> getDelegate() {
    if (delegate == null) {
      delegate = (Map<String, Object>) getCommerceCache().get(
        new ProductCacheKey(getId(), getContext(), getCatalogWrapperService(), getCommerceCache()));
      if (delegate == null) {
        throw new NotFoundException(getId() + " (product not found in catalog)");
      }
    }
    return delegate;
  }

  @Override
  public void load() throws CommerceException {
    getDelegate();
  }

  @Override
  public String getReference() {
    return CommerceIdHelper.formatProductId(getExternalId());
  }

  @Override
  @Nonnull
  public List<String> getVariantAxisNames() {
    if (variantAxis == null) {
      List<String> newVariantAxis = new ArrayList<>();
      List<ProductAttribute> definingAttributes = getDefiningAttributes();
      for (ProductAttribute definingAttribute : definingAttributes) {
        if (!newVariantAxis.contains(definingAttribute.getId())) {
          newVariantAxis.add(definingAttribute.getId());
        }
      }
      variantAxis = newVariantAxis;
    }
    return variantAxis;
  }

  @Override
  @Nonnull
  public List<ProductVariant> getVariants() {
    if (variants == null) {
      List<ProductVariant> newVariants = new ArrayList<>();
      @SuppressWarnings("unchecked")
      List<HashMap<String, Object>> wcSkus = DataMapHelper.getValueForKey(getDelegate(), "sKUs", List.class);
      if (wcSkus != null && !wcSkus.isEmpty()) {
        for (Map<String, Object> wcSku : wcSkus) {
          String technicalId = DataMapHelper.getValueForKey(wcSku, "uniqueID", String.class);
          if (technicalId != null) {
            ProductVariant pv = (ProductVariant) getCommerceBeanFactory().createBeanFor(
                    CommerceIdHelper.formatProductVariantTechId(technicalId), getContext());
            newVariants.add(pv);
          }
        }
      } else {
        //In some cases the initial load mechanism does not come with containing SKUs (e.g. findProductsByCategory).
        //Therefor the product is loaded again via #findProductById to make sure all product data is loaded.
        @SuppressWarnings("unchecked")
        Map<String, Object> wcProduct = (Map<String, Object>) getCommerceCache().get(
                new ProductCacheKey(CommerceIdHelper.formatProductTechId(getExternalTechId()),
                        Commerce.getCurrentConnection().getStoreContext(), getCatalogWrapperService(), getCommerceCache()));
        if (wcProduct != null && wcProduct.containsKey("sKUs")) {
          setDelegate(wcProduct);
          //reset the fields after a new delegate is set.
          variants = null;
          variantAxis = null;
          return getVariants();
        }
      }

      variants = newVariants;
    }
    return variants;
  }

  @Override
  @Nonnull
  public List<ProductVariant> getVariants(@Nullable List<VariantFilter> filters) {
    List<ProductVariant> result = new ArrayList<>();
    List<ProductVariant> allVariants = getVariants();
    if (filters == null || filters.isEmpty()) {
      return allVariants;
    }
    for (ProductVariant productVariant : allVariants) {
      boolean isIncluded = true;
      for (VariantFilter filter : filters) {
        if (!filter.matches(productVariant)) {
          isIncluded = false;
          break;
        }
      }
      if (isIncluded) {
        result.add(productVariant);
      }
    }
    return result;
  }

  @Override
  @Nonnull
  public List<ProductVariant> getVariants(VariantFilter filter) {
    if (filter == null) {
      return getVariants((List<VariantFilter>) null);
    }
    List<VariantFilter> filters = new ArrayList<>();
    filters.add(filter);
    return getVariants(filters);
  }

  @Override
  @Nonnull
  public Map<ProductVariant, AvailabilityInfo> getAvailabilityMap() {
    return availabilityService.getAvailabilityInfo(this.getVariants());
  }

  @Override
  public float getTotalStockCount() {
    Map<ProductVariant, AvailabilityInfo> availabilityMap = getAvailabilityMap();
    float result = 0;
    for (Map.Entry<ProductVariant, AvailabilityInfo> entry : availabilityMap.entrySet()) {
      result += entry.getValue().getQuantity();
    }

    return result;
  }

  @Override
  public boolean isAvailable() {
    // a product is available if at least one product variant is available
    boolean result = false;

    for (ProductVariant variant : getVariants()) {
      result = result || variant.isAvailable();
    }

    return result;
  }

  @Override
  @Nonnull
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable List<VariantFilter> filters) {

    notNull(axisName);

    List<Object> result = new ArrayList<>();

    List<ProductVariant> availableProducts = getVariants(filters);
    for (ProductVariant productVariant : availableProducts) {
      Object attributeValue = productVariant.getAttributeValue(axisName);
      if (attributeValue != null && !result.contains(attributeValue)) {
        result.add(attributeValue);
      }
    }
    return result;
  }

  @Override
  @Nonnull
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable VariantFilter filter) {

    notNull(axisName);

    if (filter == null) {
      return getVariantAxisValues(axisName, (List<VariantFilter>) null);
    }

    List<VariantFilter> filters = new ArrayList<>();
    filters.add(filter);
    return getVariantAxisValues(axisName, filters);
  }

  @SuppressWarnings("unused")
  public AvailabilityService getAvailabilityService() {
    return availabilityService;
  }

  public void setAvailabilityService(AvailabilityService availabilityService) {
    this.availabilityService = availabilityService;
  }

  @Override
  public String toString() {
    return "[Product " + getId() + "]";
  }

}
