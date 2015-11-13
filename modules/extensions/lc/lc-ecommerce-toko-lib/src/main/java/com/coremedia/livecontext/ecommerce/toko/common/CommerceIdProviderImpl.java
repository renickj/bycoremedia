package com.coremedia.livecontext.ecommerce.toko.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;

public class CommerceIdProviderImpl extends BaseCommerceIdProvider {

  @Override
  public String formatProductTechId(String productTechId) {
    return super.formatProductId(productTechId);
  }

  @Override
  public String formatProductVariantTechId(String productTechId) {
    return super.formatProductVariantId(productTechId);
  }

  @Override
  public String formatCategoryTechId(String techId) {
    return super.formatCategoryId(techId);
  }

  @Override
  public String parseExternalTechIdFromId(String id) {
    return super.parseExternalIdFromId(id);
  }
}
