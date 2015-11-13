package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;

/**
 * Provides access to ibm specific {@link CommerceIdHelper} functionality from outside the ibm module.
 * CommerceIdProvider is accessible as spring bean.
 */
public class CommerceIdProviderImpl implements CommerceIdProvider {

  @Override
  public String parseExternalIdFromId(String id) {
    String result = null;
    try {
      result = CommerceIdHelper.parseExternalTechIdFromId(id);
    } catch (InvalidIdException ex) {
      //do nothing
    }
    try {
      if (result == null) {
        result = CommerceIdHelper.parseExternalIdFromId(id);
      }
    } catch (InvalidIdException ex) {
      //do nothing
    }
    return result;
  }

  @Override
  public String formatProductId(String id) {
    return CommerceIdHelper.formatProductId(id);
  }

  @Override
  public String formatProductTechId(String techId) {
    return CommerceIdHelper.formatProductTechId(techId);
  }

  @Override
  public String formatProductSeoSegment(String seoSegment) {
    return CommerceIdHelper.formatProductSeoId(seoSegment);
  }

  @Override
  public String formatProductVariantId(String id) {
    return CommerceIdHelper.formatProductVariantId(id);
  }

  @Override
  public String formatProductVariantTechId(String techId) {
    return CommerceIdHelper.formatProductVariantTechId(techId);
  }

  @Override
  public String formatProductVariantSeoSegment(String seoSegment) {
    return CommerceIdHelper.formatProductVariantSeoId(seoSegment);
  }

  @Override
  public String formatCategoryId(String id) {
    return CommerceIdHelper.formatCategoryId(id);
  }

  @Override
  public String formatCategoryTechId(String techId) {
    return CommerceIdHelper.formatCategoryTechId(techId);
  }

  @Override
  public String formatCategorySeoSegment(String seoSegment) {
    return CommerceIdHelper.formatCategorySeoId(seoSegment);
  }

  @Override
  public String formatSegmentId(String id) {
    return CommerceIdHelper.formatSegmentId(id);
  }

  @Override
  public String parseExternalTechIdFromId(String id) {
    return CommerceIdHelper.parseExternalTechIdFromId(id);
  }

  @Override
  public String parseExternalSeoSegmentFromId(String id) {
    return CommerceIdHelper.parseExternalSeoIdFromId(id);
  }

}
