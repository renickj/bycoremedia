package com.coremedia.livecontext.ecommerce.ibm.catalog;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;

/**
 * Tests for BOD REST interface.
 */
public class CatalogServiceImplSearchBasedTest extends BaseTestsCatalogServiceImpl {

  protected static final String BEAN_NAME_CATALOG_SERVICE_SEARCH = "catalogService";

  // overwrite to use search based wrapper service
  protected static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withBeans("classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml")
          .withBeans("classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services-search.xml")
          .withHandlers()
          .withSites()
          .asWebEnvironment(new MockServletContext())
          .build();

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean(BEAN_NAME_CATALOG_SERVICE_SEARCH, CatalogServiceImpl.class);
    testling.getCatalogWrapperService().clearLanguageMapping();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @Betamax(tape = "csi_testFindProductByPartNumber_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalId() throws Exception {
    super.testFindProductByExternalId();
  }

  @Betamax(tape = "csi_testFindProductByPartNumberIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalIdIsNull() throws Exception {
    super.testFindProductByExternalIdIsNull();
  }

  @Betamax(tape = "csi_testFindProductByExternalTechId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalTechId() throws Exception {
    super.testFindProductByExternalTechId();
  }

  @Betamax(tape = "csi_testFindProductByExternalTechIdIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalTechIdIsNull() throws Exception {
    super.testFindProductByExternalTechIdIsNull();
  }

  @Betamax(tape = "csi_testFindProductByExternalIdReturns502_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductByExternalIdReturns502() throws Exception {
    super.testFindProductByExternalIdReturns502();
  }

  @Betamax(tape = "csi_testFindProduct2ByExternalId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProduct2ByExternalId() throws Exception {
    super.testFindProduct2ByExternalId();
  }

  @Betamax(tape = "csi_testFindProductVariantByExternalId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalId() throws Exception {
    super.testFindProductVariantByExternalId();
  }

  @Betamax(tape = "csi_testFindProductVariantByExternalTechId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductVariantByExternalTechId() throws Exception {
    super.testFindProductVariantByExternalTechId();
  }

  @Betamax(tape = "csi_testFindProductBySeoSegment_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductBySeoSegment() throws Exception {
    super.testFindProductBySeoSegment();
  }

  @Betamax(tape = "csi_testFindProductBySeoSegmentIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductBySeoSegmentIsNull() throws Exception {
    super.testFindProductBySeoSegmentIsNull();
  }

  @Betamax(tape = "csi_testFindProductsByCategory_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategory() throws Exception {
    super.testFindProductsByCategory();
  }

  @Betamax(tape = "csi_testFindProductsByCategoryIsEmpty_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindProductsByCategoryIsEmpty() throws Exception {
    super.testFindProductsByCategoryIsEmpty();
  }

  @Betamax(tape = "csi_testSearchProducts_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testSearchProducts() throws Exception {
    super.testSearchProducts();
  }

  @Test
  @Betamax(tape = "csi_testSearchProductVariants_search", match = {MatchRule.path, MatchRule.query})
  @Override
  public void testSearchProductVariants() throws Exception {
    super.testSearchProductVariants();
  }

  @Betamax(tape = "csi_testFindTopCategories_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindTopCategories() throws Exception {
    super.testFindTopCategories();
  }

  @Betamax(tape = "csi_testFindSubCategories_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategories() throws Exception {
    super.testFindSubCategories();
  }

  @Betamax(tape = "csi_testFindSubCategoriesWithContract_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Ignore
  @Override
  public void testFindSubCategoriesWithContract() throws Exception {
    super.testFindSubCategoriesWithContract();
  }

  @Betamax(tape = "csi_testFindSubCategoriesIsEmpty_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindSubCategoriesIsEmpty() throws Exception {
    super.testFindSubCategoriesIsEmpty();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalTechId_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalTechId() throws Exception {
    super.testFindCategoryByExternalTechId();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalTechIdIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalTechIdIsNull() throws Exception {
    super.testFindCategoryByExternalTechIdIsNull();
  }

  @Betamax(tape = "csi_testFindCategoryBySeoSegment_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegment() throws Exception {
    super.testFindCategoryBySeoSegment();
  }

  @Betamax(tape = "csi_testFindCategoryBySeoSegmentIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryBySeoSegmentIsNull() throws Exception {
    super.testFindCategoryBySeoSegmentIsNull();
  }

  @Betamax(tape = "csi_testFindCategoryByPartNumber_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalId() {
    super.testFindCategoryByExternalId();
  }

  @Betamax(tape = "csi_testFindCategoryByExternalIdIsNull_search", match = {MatchRule.path, MatchRule.query})
  @Test
  @Override
  public void testFindCategoryByExternalIdIsNull() {
    super.testFindCategoryByExternalIdIsNull();
  }
}
