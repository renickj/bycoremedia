package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for BOD REST interface.
 */
public class CatalogServiceImplBodBasedTest extends BaseTestsCatalogServiceImpl {

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean(BEAN_NAME_CATALOG_SERVICE, CatalogServiceImpl.class);
    testling.getCatalogWrapperService().clearLanguageMapping();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @Betamax(tape = "csi_testFindProductByPartNumber", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalId() throws Exception {
    super.testFindProductByExternalId();
  }

  @Betamax(tape = "csi_testFindProductByPartNumberIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalIdIsNull() throws Exception {
    super.testFindProductByExternalIdIsNull();
  }

  @Betamax(tape = "csi_testFindProductByExternalTechId", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalTechId() throws Exception {
    super.testFindProductByExternalTechId();
  }

  @Betamax(tape = "csi_testFindProductByExternalTechIdIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalTechIdIsNull() throws Exception {
    super.testFindProductByExternalTechIdIsNull();
  }

  @Betamax(tape = "csi_testFindProductByExternalIdReturns502", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalIdReturns502() throws Exception {
    super.testFindProductByExternalIdReturns502();
  }

  @Betamax(tape = "csi_testFindProduct2ByExternalId", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProduct2ByExternalId() throws Exception {
    super.testFindProduct2ByExternalId();
  }

  @Betamax(tape = "csi_testFindProductVariantByExternalId", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalId() throws Exception {
    super.testFindProductVariantByExternalId();
  }

  @Betamax(tape = "csi_testFindProductVariantByExternalTechId", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalTechId() throws Exception {
    super.testFindProductVariantByExternalTechId();
  }

  @Betamax(tape = "csi_testFindProductBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductBySeoSegment() throws Exception {
    super.testFindProductBySeoSegment();
  }

  @Betamax(tape = "csi_testFindProductBySeoSegmentIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductBySeoSegmentIsNull() throws Exception {
    super.testFindProductBySeoSegmentIsNull();
  }

  @Betamax(tape = "csi_testFindProductsByCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategory() throws Exception {
    super.testFindProductsByCategory();
  }

  @Betamax(tape = "csi_testFindProductsByCategoryIsEmpty", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategoryIsEmpty() throws Exception {
    super.testFindProductsByCategoryIsEmpty();
  }

  @Betamax(tape = "csi_testSearchProducts", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testSearchProducts() throws Exception {
    super.testSearchProducts();
  }

  @Test
  @Betamax(tape = "csi_testSearchProductVariants", match = {MatchRule.path, MatchRule.query})
  @Override
  public void testSearchProductVariants() throws Exception {
    super.testSearchProductVariants();
  }

  @Betamax(tape = "csi_testFindTopCategories", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindTopCategories() throws Exception {
    super.testFindTopCategories();
  }

  @Betamax(tape = "csi_testFindSubCategories", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategories() throws Exception {
    super.testFindSubCategories();
  }

  @Betamax(tape = "csi_testFindSubCategoriesWithContract", match = {MatchRule.path, MatchRule.query})
  @Test
  @Ignore
  @Override
  public void testFindSubCategoriesWithContract() throws Exception {
    super.testFindSubCategoriesWithContract();
  }

  @Betamax(tape = "csi_testFindSubCategoriesIsEmpty", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategoriesIsEmpty() throws Exception {
    super.testFindSubCategoriesIsEmpty();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalTechId", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalTechId() throws Exception {
    super.testFindCategoryByExternalTechId();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalTechIdIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalTechIdIsNull() throws Exception {
    super.testFindCategoryByExternalTechIdIsNull();
  }

  @Betamax(tape = "csi_testFindCategoryBySeoSegment", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegment() throws Exception {
    super.testFindCategoryBySeoSegment();
  }

  @Betamax(tape = "csi_testFindCategoryBySeoSegmentIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegmentIsNull() throws Exception {
    super.testFindCategoryBySeoSegmentIsNull();
  }

  @Betamax(tape = "csi_testFindCategoryByPartNumber", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalId() {
    super.testFindCategoryByExternalId();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalIdIsNull", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalIdIsNull() {
    super.testFindCategoryByExternalIdIsNull();
  }


  @Betamax(tape = "csi_testWithStoreContext", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testWithStoreContext() {
    super.testWithStoreContext();
  }

  @Override
  @Test(expected = CommerceException.class)
  public void testWithStoreContextRethrowException() {
    super.testWithStoreContextRethrowException();
  }
}
