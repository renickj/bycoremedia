package com.coremedia.livecontext.ecommerce.ibm.common;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceIdScheme;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommerceIdSchemeTest extends AbstractServiceTest {

  private static final String PRODUCT = "AuroraWMDRS-1";
  private static final String CATEGORY = "Women";

  private static final String PRODUCT_ID = "ibm:///catalog/product/" + PRODUCT;
  private static final String SKU_ID_PREFIX = "ibm:///catalog/sku/";
  private static final String CATEGORY_ID = "ibm:///catalog/category/" + CATEGORY;

  CommerceIdScheme testling;

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean("catalogIdScheme", CommerceIdScheme.class);
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  public void testInvalidBeanId() throws Exception {
    Object o = testling.parseId("ibm://catalog/bla/blub");
    assertEquals(o, CommerceIdScheme.CANNOT_HANDLE);
  }

  @Betamax(tape = "cis_testProductId", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProductId() throws Exception {
    Product product = (Product) testling.parseId(PRODUCT_ID);
    assertNotNull("bean most not be null", product);
    assertEquals(PRODUCT, product.getExternalId());
    String id = testling.getId(product);
    assertEquals(PRODUCT_ID, id);
  }

  @Betamax(tape = "cis_testProductNotFound", match = {MatchRule.path, MatchRule.query})
  @Test(expected = NotFoundException.class)
  public void testProductNotFound() throws Exception {
    Product product = (Product) testling.parseId("ibm:///catalog/product/blablub");
    assertEquals(PRODUCT, product.getExternalId());
  }

  @Betamax(tape = "cis_testProductVariantId", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProductVariantId() throws Exception {
    Product product = (Product) testling.parseId(PRODUCT_ID);
    List<ProductVariant> variants = product.getVariants();
    ProductVariant sku = variants.get(0);
    String skuTechId = sku.getExternalTechId();
    String skuId = CommerceIdHelper.formatProductVariantTechId(skuTechId);
    ProductVariant sku2 = (ProductVariant) testling.parseId(skuId);
    assertNotNull("bean most not be null", sku2);
    assertEquals(sku, sku2);
    String id = testling.getId(sku);
    assertEquals(skuId, id);
  }

  @Betamax(tape = "cis_testProductVariantNotFound", match = {MatchRule.path, MatchRule.query})
  @Test(expected = NotFoundException.class)
  public void testProductVariantNotFound() throws Exception {
    ProductVariant sku = (ProductVariant) testling.parseId(SKU_ID_PREFIX + "blablub");
    assertEquals("blablub", sku.getExternalId());
  }

  @Betamax(tape = "cis_testCategoryId", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testCategoryId() throws Exception {
    Category category = (Category) testling.parseId(CATEGORY_ID);
    assertNotNull("bean most not be null", category);
    assertEquals(CATEGORY, category.getExternalId());
    String id = testling.getId(category);
    assertEquals(CATEGORY_ID, id);
  }

  @Betamax(tape = "cis_testCategoryNotFound", match = {MatchRule.path, MatchRule.query})
  @Test(expected = NotFoundException.class)
  public void testCategoryNotFound() throws Exception {
    Category category = (Category) testling.parseId("ibm:///catalog/category/blablub");
    assertEquals(CATEGORY, category.getExternalId());
  }

}
