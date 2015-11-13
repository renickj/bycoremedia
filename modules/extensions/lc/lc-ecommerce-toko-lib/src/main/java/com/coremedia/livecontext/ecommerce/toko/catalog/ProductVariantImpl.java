package com.coremedia.livecontext.ecommerce.toko.catalog;

import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.livecontext.ecommerce.toko.common.CommerceIdHelper;
import org.codehaus.jackson.JsonNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Todo toko
 */
public class ProductVariantImpl extends ProductImpl implements ProductVariant {

  private Product parent;

  @Nullable
  @Override
  public Product getParent() {
    if (parent == null) {
      JsonNode delegate = catalogMock.getParentProduct(getExternalId());
      if (delegate != null) {
        String productId = getIdFromJsonNode(delegate);
        if (productId != null) {
          parent = (Product) getCommerceBeanFactory().createBeanFor(CommerceIdHelper.formatProductId(productId), getContext());
        }
      }
    }
    return parent;
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDefiningAttributes() {
    //todo toko
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDescribingAttributes() {
    //todo toko
    return Collections.emptyList();
  }

  @Nullable
  @Override
  public Object getAttributeValue(@Nonnull String type) {
    //todo toko
    return null;
  }

  @Nullable
  @Override
  public AvailabilityInfo getAvailabilityInfo() {
    //todo toko
    return null;
  }

  @Override
  public boolean isVariant() {
    return true;
  }

  @Override
  public void load() throws CommerceException {
    JsonNode delegate = catalogMock.getProductVariantById(CommerceIdHelper.parseExternalIdFromId(getId()));
    if (delegate == null) {
      throw new NotFoundException("Commerce object not found with id: " + getId());
    }
    else {
      setDelegate(delegate);
    }
  }
}
