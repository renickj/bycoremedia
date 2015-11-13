package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.InvalidIdException;

public class CommerceIdHelper {

  public static final String PRODUCT_TYPE = "product";
  public static final String PRODUCT_VARIANT_TYPE = "sku";
  public static final String CATEGORY_TYPE = "category";
  public static final String MARKETING_SPOT_TYPE = "marketingspot";
  public static final String SEGMENT_TYPE = "segment";
  public static final String CONTRACT_TYPE = "contract";
  public static final String WORKSPACE_TYPE = "workspace";
  public static final String PERSON_TYPE = "person";
  public static final String TECH_ID_PREFIX = "techId:";
  public static final String SEO_ID_PREFIX = "seo:";
  public static final String CART_TYPE = "cart";

  private static final String LIVECONTEXT_SCHEME = "ibm";
  private static final String CATALOG_PREFIX = LIVECONTEXT_SCHEME + ":///catalog/";
  private static final String PRODUCT_ID_PREFIX = CATALOG_PREFIX + PRODUCT_TYPE + "/";
  private static final String PRODUCT_TECH_ID_PREFIX = CATALOG_PREFIX + PRODUCT_TYPE + "/" + TECH_ID_PREFIX;
  private static final String PRODUCT_SEO_ID_PREFIX = CATALOG_PREFIX + PRODUCT_TYPE + "/" + SEO_ID_PREFIX;
  private static final String PRODUCT_VARIANT_ID_PREFIX = CATALOG_PREFIX + PRODUCT_VARIANT_TYPE + "/";
  private static final String PRODUCT_VARIANT_TECH_ID_PREFIX = CATALOG_PREFIX + PRODUCT_VARIANT_TYPE + "/" + TECH_ID_PREFIX;
  private static final String PRODUCT_VARIANT_SEO_ID_PREFIX = CATALOG_PREFIX + PRODUCT_VARIANT_TYPE + "/" + SEO_ID_PREFIX;
  private static final String CATEGORY_ID_PREFIX = CATALOG_PREFIX + CATEGORY_TYPE + "/";
  private static final String CATEGORY_TECH_ID_PREFIX = CATALOG_PREFIX + CATEGORY_TYPE + "/" + TECH_ID_PREFIX;
  private static final String CATEGORY_SEO_ID_PREFIX = CATALOG_PREFIX + CATEGORY_TYPE + "/" + SEO_ID_PREFIX;
  private static final String MARKETING_SPOT_ID_PREFIX = CATALOG_PREFIX + MARKETING_SPOT_TYPE + "/";
  private static final String MARKETING_SPOT_TECH_ID_PREFIX = CATALOG_PREFIX + MARKETING_SPOT_TYPE + "/" + TECH_ID_PREFIX;
  private static final String SEGMENT_ID_PREFIX = CATALOG_PREFIX + SEGMENT_TYPE + "/";
  private static final String CONTRACT_ID_PREFIX = CATALOG_PREFIX + CONTRACT_TYPE + "/";
  private static final String WORKSPACE_ID_PREFIX = CATALOG_PREFIX + WORKSPACE_TYPE + "/";
  private static final String PERSON_ID_PREFIX = CATALOG_PREFIX + PERSON_TYPE + "/";
  private static final String CART_ID_PREFIX = CATALOG_PREFIX + CART_TYPE + "/";

  private CommerceIdHelper() {
  }

  public static boolean isCatalogId(String objectId) {
    return objectId != null && objectId.startsWith(CATALOG_PREFIX);
  }

  public static boolean isProductId(String objectId) {
    return objectId != null && objectId.startsWith(PRODUCT_ID_PREFIX);
  }

  public static boolean isProductTechId(String objectId) {
    return objectId != null && objectId.startsWith(PRODUCT_TECH_ID_PREFIX);
  }

  public static boolean isProductSeoId(String objectId) {
    return objectId != null && objectId.startsWith(PRODUCT_SEO_ID_PREFIX);
  }

  public static boolean isProductVariantId(String objectId) {
    return objectId != null && objectId.startsWith(PRODUCT_VARIANT_ID_PREFIX);
  }

  public static boolean isProductVariantTechId(String objectId) {
    return objectId != null && objectId.startsWith(PRODUCT_VARIANT_TECH_ID_PREFIX);
  }

  public static boolean isProductVariantSeoId(String objectId) {
    return objectId != null && objectId.startsWith(PRODUCT_VARIANT_SEO_ID_PREFIX);
  }

  public static boolean isPersonId(String objectId) {
    return objectId != null && objectId.startsWith(PERSON_ID_PREFIX);
  }

  public static boolean isCategoryId(String objectId) {
    return objectId != null && objectId.startsWith(CATEGORY_ID_PREFIX);
  }

  public static boolean isMarketingSpotId(String objectId) {
    return objectId != null && objectId.startsWith(MARKETING_SPOT_ID_PREFIX);
  }

  public static boolean isMarketingSpotTechId(String objectId) {
    return objectId != null && objectId.startsWith(MARKETING_SPOT_TECH_ID_PREFIX);
  }

  public static boolean isSegmentId(String objectId) {
    return objectId != null && objectId.startsWith(SEGMENT_ID_PREFIX);
  }

  public static boolean isContractId(String objectId) {
    return objectId != null && objectId.startsWith(CONTRACT_ID_PREFIX);
  }

  public static boolean isWorkspaceId(String objectId) {
    return objectId != null && objectId.startsWith(WORKSPACE_ID_PREFIX);
  }

  public static boolean isCategoryTechId(String objectId) {
    return objectId != null && objectId.startsWith(CATEGORY_TECH_ID_PREFIX);
  }

  public static boolean isCategorySeoId(String objectId) {
    return objectId != null && objectId.startsWith(CATEGORY_SEO_ID_PREFIX);
  }

  public static boolean isCartId(String objectId) {
    return objectId != null && objectId.startsWith(CART_ID_PREFIX);
  }

  public static String formatProductId(String code) {
    if (code != null && !code.startsWith(PRODUCT_ID_PREFIX)) {
      return PRODUCT_ID_PREFIX + code;
    }
    return code;
  }

  public static String formatProductTechId(String techId) {
    if (techId != null && !techId.startsWith(PRODUCT_TECH_ID_PREFIX)) {
      return PRODUCT_TECH_ID_PREFIX + techId;
    }
    return techId;
  }

  public static String formatProductSeoId(String seoId) {
    if (seoId != null && !seoId.startsWith(PRODUCT_SEO_ID_PREFIX)) {
      return PRODUCT_SEO_ID_PREFIX + seoId;
    }
    return seoId;
  }

  public static String formatProductVariantId(String code) {
    if (code != null && !code.startsWith(PRODUCT_VARIANT_ID_PREFIX)) {
      return PRODUCT_VARIANT_ID_PREFIX + code;
    }
    return code;
  }

  public static String formatProductVariantTechId(String techId) {
    if (techId != null && !techId.startsWith(PRODUCT_VARIANT_TECH_ID_PREFIX)) {
      return PRODUCT_VARIANT_TECH_ID_PREFIX + techId;
    }
    return techId;
  }

  public static String formatMarketingSpotId(String uniqueId) {
    if (uniqueId != null && !uniqueId.startsWith(MARKETING_SPOT_ID_PREFIX)) {
      return MARKETING_SPOT_ID_PREFIX + uniqueId;
    }
    return uniqueId;
  }

  public static String formatMarketingSpotTechId(String techId) {
    if (techId != null && !techId.startsWith(MARKETING_SPOT_TECH_ID_PREFIX)) {
      return MARKETING_SPOT_TECH_ID_PREFIX + techId;
    }
    return techId;
  }

  public static String formatSegmentId(String uniqueId) {
    if (uniqueId != null && !uniqueId.startsWith(SEGMENT_ID_PREFIX)) {
      return SEGMENT_ID_PREFIX + uniqueId;
    }
    return uniqueId;
  }

  public static String formatContractId(String uniqueId) {
    if (uniqueId != null && !uniqueId.startsWith(CONTRACT_ID_PREFIX)) {
      return CONTRACT_ID_PREFIX + uniqueId;
    }
    return uniqueId;
  }

  public static String formatWorkspaceId(String uniqueId) {
    if (uniqueId != null && !uniqueId.startsWith(WORKSPACE_ID_PREFIX)) {
      return WORKSPACE_ID_PREFIX + uniqueId;
    }
    return uniqueId;
  }

  public static String formatProductVariantSeoId(String seoId) {
    if (seoId != null && !seoId.startsWith(PRODUCT_VARIANT_SEO_ID_PREFIX)) {
      return PRODUCT_VARIANT_SEO_ID_PREFIX + seoId;
    }
    return seoId;
  }

  public static String formatCategoryId(String id) {
    if (id != null && !id.startsWith(CATEGORY_ID_PREFIX)) {
      return CATEGORY_ID_PREFIX + id;
    }
    return id;
  }

  public static String formatCategoryTechId(String techId) {
    if (techId != null && !techId.startsWith(CATEGORY_TECH_ID_PREFIX)) {
      return CATEGORY_TECH_ID_PREFIX + techId;
    }
    return techId;
  }

  public static String formatCategorySeoId(String seoId) {
    if (seoId != null && !seoId.startsWith(CATEGORY_SEO_ID_PREFIX)) {
      return CATEGORY_SEO_ID_PREFIX + seoId;
    }
    return seoId;
  }

  public static String formatCartId(String uniqueId) {
    if (uniqueId != null && !uniqueId.startsWith(CART_ID_PREFIX)) {
      return CART_ID_PREFIX + uniqueId;
    }
    return uniqueId;
  }

  public static String formatPersonId(String id) {
    if (id != null && !id.startsWith(PERSON_ID_PREFIX)) {
      return PERSON_ID_PREFIX + id;
    }
    return id;
  }

  public static String parseExternalIdFromId(String id) {
    if (isProductId(id)) {
      return parseNextTokenFromId(PRODUCT_ID_PREFIX.length(), id);
    }
    if (isProductVariantId(id)) {
      return parseNextTokenFromId(PRODUCT_VARIANT_ID_PREFIX.length(), id);
    }
    if (isCategoryId(id)) {
      return parseNextTokenFromId(CATEGORY_ID_PREFIX.length(), id);
    }
    if (isCartId(id)) {
      return parseNextTokenFromId(CART_ID_PREFIX.length(), id);
    }
    if (isMarketingSpotId(id)) {
      return parseNextTokenFromId(MARKETING_SPOT_ID_PREFIX.length(), id);
    }
    if (isSegmentId(id)) {
      return parseNextTokenFromId(SEGMENT_ID_PREFIX.length(), id);
    }
    if (isContractId(id)) {
      return parseNextTokenFromId(CONTRACT_ID_PREFIX.length(), id);
    }
    if (isWorkspaceId(id)) {
      return parseNextTokenFromId(WORKSPACE_ID_PREFIX.length(), id);
    }
    if (isPersonId(id)) {
      return parseNextTokenFromId(PERSON_ID_PREFIX.length(), id);
    }
    throw new InvalidIdException("invalid id: " + id);
  }

  public static String parseExternalTechIdFromId(String id) {
    if (isProductTechId(id)) {
      return parseNextTokenFromId(PRODUCT_TECH_ID_PREFIX.length(), id);
    }
    if (isProductVariantTechId(id)) {
      return parseNextTokenFromId(PRODUCT_VARIANT_TECH_ID_PREFIX.length(), id);
    }
    if (isCategoryTechId(id)) {
      return parseNextTokenFromId(CATEGORY_TECH_ID_PREFIX.length(), id);
    }
    if (isMarketingSpotTechId(id)) {
      return parseNextTokenFromId(MARKETING_SPOT_TECH_ID_PREFIX.length(), id);
    }
    throw new InvalidIdException("invalid tech id: " + id);
  }

  public static String parseExternalSeoIdFromId(String id) {
    if (isProductSeoId(id)) {
      return parseNextTokenFromId(PRODUCT_SEO_ID_PREFIX.length(), id);
    }
    if (isProductVariantSeoId(id)) {
      return parseNextTokenFromId(PRODUCT_VARIANT_SEO_ID_PREFIX.length(), id);
    }
    if (isCategorySeoId(id)) {
      return parseNextTokenFromId(CATEGORY_SEO_ID_PREFIX.length(), id);
    }
    throw new InvalidIdException("invalid seo id: " + id);
  }

  private static String parseNextTokenFromId(int offset, String id) {
    String externalId = id.substring(offset);
    int indexOfSlash = externalId.indexOf('/');
    if (indexOfSlash > 0) {
      externalId = externalId.substring(0, indexOfSlash);
    }
    if (externalId.trim().isEmpty()) {
      throw new InvalidIdException("The id is empty.");
    }
    return externalId;
  }

  public static String parseTypeFromId(String id) {
    if (isMarketingSpotId(id)) {
      return MARKETING_SPOT_TYPE;
    }
    if (isProductVariantId(id)) {
      return PRODUCT_VARIANT_TYPE;
    }
    if (isProductId(id)) {
      return PRODUCT_TYPE;
    }
    if (isCategoryId(id)) {
      return CATEGORY_TYPE;
    }
    if (isCartId(id)) {
      return CART_TYPE;
    }
    if (isPersonId(id)) {
      return PERSON_TYPE;
    }
    if (isSegmentId(id)) {
      return SEGMENT_TYPE;
    }
    if (isContractId(id)) {
      return CONTRACT_TYPE;
    }
    if (isWorkspaceId(id)) {
      return WORKSPACE_TYPE;
    }
    throw new InvalidIdException(id);
  }
}
