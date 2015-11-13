package com.coremedia.livecontext.ecommerce.ibm.inventory;

import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityService;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class AvailabilityServiceImpl implements AvailabilityService {

    private WcAvailabilityWrapperService availabilityWrapperService;
    private CommerceCache commerceCache;
    private CommerceBeanFactory commerceBeanFactory;

    @Override
    @Nullable
    public AvailabilityInfo getAvailabilityInfo(@Nonnull final ProductVariant productVariant) {
        StoreContext currentContext = StoreContextHelper.getCurrentContext();
        Map<String, Object> inventoryAvailability = (Map<String, Object>) commerceCache.get(
                new AvailabilityByIdsCacheKey(productVariant.getExternalTechId(), currentContext, availabilityWrapperService, commerceCache));

        Map<ProductVariant, AvailabilityInfo> productVariantAvailabilityMap = getProductVariantAvailabilityMap(inventoryAvailability);
        return productVariantAvailabilityMap.get(productVariant);
    }

    @Override
    @Nonnull
    public Map<ProductVariant, AvailabilityInfo> getAvailabilityInfo(@Nonnull final List<ProductVariant> productVariants) {
        StoreContext currentContext = StoreContextHelper.getCurrentContext();
        String skuIds = getListOfCommaSeperatedProductIds(productVariants);
        Map<String, Object> inventoryAvailability = (Map<String, Object>) commerceCache.get(
                new AvailabilityByIdsCacheKey(skuIds, currentContext, availabilityWrapperService, commerceCache));

        return getProductVariantAvailabilityMap(inventoryAvailability);
    }

    private String getListOfCommaSeperatedProductIds(List<ProductVariant> productVariants) {
        List<String> productVariantIds = new ArrayList<>();

        for (Product product : productVariants) {
            productVariantIds.add(product.getExternalTechId());
        }

        Collections.sort(productVariantIds);
        return StringUtils.join(productVariantIds, ",");
    }

    private Map<ProductVariant, AvailabilityInfo> getProductVariantAvailabilityMap(Map<String, Object> wcInventoryAvailabilityList) {
        if (wcInventoryAvailabilityList == null || wcInventoryAvailabilityList.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<ProductVariant, AvailabilityInfo> result = new HashMap<>();
        List<Map<String, Object>> inventoryAvailabilityList = DataMapHelper.getValueForKey(wcInventoryAvailabilityList, "InventoryAvailability", List.class);

        for (Map<String, Object> inventoryAvailability : inventoryAvailabilityList) {
            //TODO: Online Store only??
            if (DataMapHelper.getValueForKey(inventoryAvailability, "onlineStoreId", String.class) != null) {
                StoreContext currentContext = StoreContextHelper.getCurrentContext();
                String id = CommerceIdHelper.formatProductVariantTechId(DataMapHelper.getValueForKey(inventoryAvailability, "productId", String.class));
                ProductVariant sku = (ProductVariant) commerceBeanFactory.createBeanFor(id, currentContext);
                result.put(sku, new AvailabilityInfoImpl(inventoryAvailability));
            }
        }
        return result;
    }

    public CommerceCache getCommerceCache() {
        return commerceCache;
    }

    public void setCommerceCache(CommerceCache commerceCache) {
        this.commerceCache = commerceCache;
    }

    public WcAvailabilityWrapperService getAvailabilityWrapperService() {
        return availabilityWrapperService;
    }

    public void setAvailabilityWrapperService(WcAvailabilityWrapperService availabilityWrapperService) {
        this.availabilityWrapperService = availabilityWrapperService;
    }

    public CommerceBeanFactory getCommerceBeanFactory() {
        return commerceBeanFactory;
    }

    public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
        this.commerceBeanFactory = commerceBeanFactory;
    }

  @Nonnull
  @Override
  public AvailabilityService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, AvailabilityService.class);
  }
}
