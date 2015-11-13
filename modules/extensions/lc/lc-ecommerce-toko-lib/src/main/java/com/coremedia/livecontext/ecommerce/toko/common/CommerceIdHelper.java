package com.coremedia.livecontext.ecommerce.toko.common;

import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;

public class CommerceIdHelper {

  private static CommerceIdProviderImpl INSTANCE = new CommerceIdProviderImpl();
  static {
    INSTANCE.setVendor("toko");
  }

  public static String formatProductId(String id) {
    return INSTANCE.formatProductId(id);
  }

  public static String formatCategoryId(String id) {
    return INSTANCE.formatCategoryId(id);
  }

  public static String parseExternalIdFromId(String id) {
    return INSTANCE.parseExternalIdFromId(id);
  }

  public static String formatCommerceId(String id, Class typeOf) {
    if (ProductVariant.class.equals(typeOf)) {
      return INSTANCE.formatProductVariantId(id);
    }
    if (Product.class.equals(typeOf)) {
      return INSTANCE.formatProductId(id);
    }
    if (Category.class.equals(typeOf)) {
      return INSTANCE.formatCategoryId(id);
    }
    throw new InvalidIdException("Unsupported bean type");
  }

}
