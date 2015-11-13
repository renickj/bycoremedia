package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommerceIdHelperTest {

  // TODO: add tests for marketing spots and segments

  private static final String PRODUCT = "4711";
  private static final String CATEGORY = "0815";
  private static final String SKU = "1234";
  private static final String STORE = "aurora";

  private static final String PRODUCT_ID = "ibm:///catalog/product/" + PRODUCT;
  private static final String PRODUCT_TECH_ID = "ibm:///catalog/product/" + CommerceIdHelper.TECH_ID_PREFIX + PRODUCT;
  private static final String PRODUCT_SEO_ID = "ibm:///catalog/product/" + CommerceIdHelper.SEO_ID_PREFIX + PRODUCT;
  private static final String CATEGORY_ID = "ibm:///catalog/category/" + CATEGORY;
  private static final String CATEGORY_TECH_ID = "ibm:///catalog/category/" + CommerceIdHelper.TECH_ID_PREFIX + CATEGORY;
  private static final String CATEGORY_SEO_ID = "ibm:///catalog/category/" + CommerceIdHelper.SEO_ID_PREFIX + CATEGORY;
  private static final String SKU_ID = "ibm:///catalog/sku/" + SKU;
  private static final String SKU_TECH_ID = "ibm:///catalog/sku/" + CommerceIdHelper.TECH_ID_PREFIX + SKU;
  private static final String SKU_SEO_ID = "ibm:///catalog/sku/" + CommerceIdHelper.SEO_ID_PREFIX + SKU;
  private static final String STORE_ID = "ibm:///catalog/store/" + STORE;
  private static final String STORE_TECH_ID = "ibm:///catalog/store/" + CommerceIdHelper.TECH_ID_PREFIX + STORE;

  @Test
  public void testProductId() {
    String id = CommerceIdHelper.formatProductId(PRODUCT);
    assertEquals(PRODUCT_ID, id);
    assertTrue(CommerceIdHelper.isProductId(id));
    assertEquals(CommerceIdHelper.PRODUCT_TYPE, CommerceIdHelper.parseTypeFromId(id));
    String externalId = CommerceIdHelper.parseExternalIdFromId(PRODUCT_ID);
    assertEquals(PRODUCT, externalId);
  }

  @Test
  public void testProductTechId() {
    String id = CommerceIdHelper.formatProductTechId(PRODUCT);
    assertEquals(PRODUCT_TECH_ID, id);
    assertTrue(CommerceIdHelper.isProductTechId(id));
    assertEquals(CommerceIdHelper.PRODUCT_TYPE, CommerceIdHelper.parseTypeFromId(id));
    String externalTechId = CommerceIdHelper.parseExternalTechIdFromId(PRODUCT_TECH_ID);
    assertEquals(PRODUCT, externalTechId);
  }

  @Test
  public void testProductSeoId() {
    String id = CommerceIdHelper.formatProductSeoId(PRODUCT);
    assertEquals(PRODUCT_SEO_ID, id);
    assertTrue(CommerceIdHelper.isProductSeoId(id));
    assertEquals(CommerceIdHelper.PRODUCT_TYPE, CommerceIdHelper.parseTypeFromId(id));
    String externalSeoId = CommerceIdHelper.parseExternalSeoIdFromId(PRODUCT_SEO_ID);
    assertEquals(PRODUCT, externalSeoId);
  }

  @Test
  public void testCategoryId() {
    String id = CommerceIdHelper.formatCategoryId(CATEGORY);
    assertEquals(CATEGORY_ID, id);
    assertTrue(CommerceIdHelper.isCategoryId(id));
    assertEquals(CommerceIdHelper.CATEGORY_TYPE, CommerceIdHelper.parseTypeFromId(id));
    String externalId = CommerceIdHelper.parseExternalIdFromId(CATEGORY_ID);
    assertEquals(CATEGORY, externalId);
  }

  @Test
  public void testCategoryTechId() {
    String id = CommerceIdHelper.formatCategoryTechId(CATEGORY);
    assertEquals(CATEGORY_TECH_ID, id);
    assertTrue(CommerceIdHelper.isCategoryTechId(id));
    assertEquals(CommerceIdHelper.CATEGORY_TYPE, CommerceIdHelper.parseTypeFromId(id));
    String externalTechId = CommerceIdHelper.parseExternalTechIdFromId(CATEGORY_TECH_ID);
    assertEquals(CATEGORY, externalTechId);
  }

  @Test
  public void testCategorySeoId() {
    String id = CommerceIdHelper.formatCategorySeoId(CATEGORY);
    assertEquals(CATEGORY_SEO_ID, id);
    assertTrue(CommerceIdHelper.isCategorySeoId(id));
    assertEquals(CommerceIdHelper.CATEGORY_TYPE, CommerceIdHelper.parseTypeFromId(id));
    String externalSeoId = CommerceIdHelper.parseExternalSeoIdFromId(CATEGORY_SEO_ID);
    assertEquals(CATEGORY, externalSeoId);
  }

  @Test
  public void testSkuId() {
    String id = CommerceIdHelper.formatProductVariantId(SKU);
    assertEquals(SKU_ID, id);
    assertTrue(CommerceIdHelper.isProductVariantId(id));
    assertEquals(CommerceIdHelper.PRODUCT_VARIANT_TYPE, CommerceIdHelper.parseTypeFromId(id));
    String externalId = CommerceIdHelper.parseExternalIdFromId(SKU_ID);
    assertEquals(SKU, externalId);
  }

  @Test
  public void testSkuTechId() {
    String id = CommerceIdHelper.formatProductVariantTechId(SKU);
    assertEquals(SKU_TECH_ID, id);
    assertTrue(CommerceIdHelper.isProductVariantTechId(id));
    assertEquals(CommerceIdHelper.PRODUCT_VARIANT_TYPE, CommerceIdHelper.parseTypeFromId(id));
    String externalTechId = CommerceIdHelper.parseExternalTechIdFromId(SKU_TECH_ID);
    assertEquals(SKU, externalTechId);
  }

  @Test
  public void testSkuSeoId() {
    String id = CommerceIdHelper.formatProductVariantSeoId(SKU);
    assertEquals(SKU_SEO_ID, id);
    assertTrue(CommerceIdHelper.isProductVariantSeoId(id));
    assertEquals(CommerceIdHelper.PRODUCT_VARIANT_TYPE, CommerceIdHelper.parseTypeFromId(id));
    String externalSeoId = CommerceIdHelper.parseExternalSeoIdFromId(SKU_SEO_ID);
    assertEquals(SKU, externalSeoId);
  }


  @Test(expected = InvalidIdException.class)
  public void testParseExternalIdFromIdUnknownType() {
    String unknownType = "ibm://unknown/4712";
    CommerceIdHelper.parseExternalIdFromId(unknownType);
  }

  @Test(expected = InvalidIdException.class)
  public void testParseExternalIdFromIdEmptyId() {
    String emptyId = "ibm:///catalog/category/";
    CommerceIdHelper.parseExternalIdFromId(emptyId);
  }
}
