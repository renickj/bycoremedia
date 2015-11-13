package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.catalog.AxisFilter;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

public class BaseTestsCatalogServiceImpl extends AbstractServiceTest {

  protected static final String BEAN_NAME_CATALOG_SERVICE = "catalogService";

  //Rest response values
  private static final String PRODUCT_NAME = "Travel Laptop";
  private static final String PRODUCT_CODE = "CLA022_2203";
  private static final String PRODUCT_SEO_SEGMENT = "travel-laptop";

  private static final String PRODUCT_VARIANT_CODE = System.getProperty("lc.test.productVariant.code", "CLA022_220301");

  private static final String PRODUCT2_CODE = "AuroraWMDRS-1";

  CatalogServiceImpl testling;

  public void testFindProductByExternalId() throws Exception {
    Product product = testling.findProductByExternalId(PRODUCT_CODE);
    assertEquals("CLA022_2203", product.getExternalId());
    assertProduct(product);
  }

  public void testFindProductByExternalIdIsNull() throws Exception {
    Product product = testling.findProductByExternalId("blablablub");
    assertNull(product);
  }

  public void testFindProductByExternalTechId() throws Exception {
    Product product1 = testling.findProductByExternalId(PRODUCT_CODE);
    Product product2 = testling.findProductByExternalTechId(product1.getExternalTechId());
    assertProduct(product2);
  }

  public void testFindProductByExternalTechIdIsNull() throws Exception {
    Product product = testling.findProductByExternalTechId("blablablub");
    assertNull(product);
  }

  public void testFindProductByExternalIdReturns502() throws Exception {
    String endpoint = testling.getCatalogWrapperService().getCatalogConnector().getServiceEndpoint();
    testling.getCatalogWrapperService().getCatalogConnector().setServiceEndpoint("http://unknownhost.unknowndomain/wcs/resources");
    Throwable exception = null;
    Product product = null;
    try {
      product = testling.findProductByExternalId("UNCACHED_PRODUCT");
    } catch (Throwable e) {
      exception = e;
    } finally {
      testling.getCatalogWrapperService().getCatalogConnector().setServiceEndpoint(endpoint);
    }
    assertNull(product);
    assertTrue("CommerceException expected", exception instanceof CommerceException);
  }

  public void testFindProduct2ByExternalId() throws Exception {
    Product product = testling.findProductByExternalId(PRODUCT2_CODE);
    assertEquals(PRODUCT2_CODE, product.getExternalId());
    assertProduct2(product);
  }

  public void testFindProductVariantByExternalId() throws Exception {
    ProductVariant productVariant = testling.findProductVariantById(CommerceIdHelper.formatProductVariantId(PRODUCT_VARIANT_CODE));
    assertEquals(PRODUCT_VARIANT_CODE, productVariant.getExternalId());
    assertProductVariant(productVariant);
    Product product = productVariant.getParent();
    assertProduct(product);
  }

  public void testFindProductVariantByExternalTechId() throws Exception {
    ProductVariant productVariant = testling.findProductVariantById(CommerceIdHelper.formatProductVariantId(PRODUCT_VARIANT_CODE));
    assertEquals(PRODUCT_VARIANT_CODE, productVariant.getExternalId());
    String techId = productVariant.getExternalTechId();
    ProductVariant productVariant2 = testling.findProductVariantById(CommerceIdHelper.formatProductVariantTechId(techId));
    assertEquals(productVariant, productVariant2);
    assertProductVariant(productVariant2);
  }

  public void testFindProductBySeoSegment() throws Exception {
    Product product = testling.findProductBySeoSegment(PRODUCT_SEO_SEGMENT);
    assertEquals("travel-laptop", product.getSeoSegment());
    assertProduct(product);
  }

  public void testFindProductBySeoSegmentIsNull() throws Exception {
    Product product = testling.findProductBySeoSegment("blablablub");
    assertNull(product);
  }

  public void testFindProductsByCategory() throws Exception {
    Product product = testling.findProductByExternalId(PRODUCT_CODE);
    assertEquals("Laptops", product.getCategory().getName());
    List<Product> products = testling.findProductsByCategory(product.getCategory());
    assertTrue("Number of products is to small", products.size() >= 3);
    assertTrue("product name is wrong", products.get(0).getName().contains("Laptop"));
    assertTrue("product seo segment is wrong", products.get(0).getSeoSegment().contains("laptop"));
    assertTrue(products.get(1).getDefaultImageUrl().endsWith(".jpg"));
    assertTrue(products.get(1).getThumbnailUrl().endsWith(".jpg"));
    assertNotNull(products.get(1).getVariants());
    assertNotNull(products.get(2).getCategory());
    assertEquals("Computers Laptops", products.get(2).getCategory().getExternalId());
    assertEquals("Electronics", products.get(2).getCategory().getBreadcrumb().get(0).getName());
  }

  public void testFindProductsByCategoryIsEmpty() throws Exception {
    Category categoryMock = Mockito.mock(CategoryImpl.class);
    when(categoryMock.getExternalTechId()).thenReturn("42");
    List<Product> products = testling.findProductsByCategory(categoryMock);
    assertNotNull(products);
    assertTrue(products.isEmpty());
  }

  public void testSearchProducts() throws Exception {
    SearchResult<Product> searchResult = testling.searchProducts("shoe", null);
    assertNotNull(searchResult);
    assertTrue(searchResult.getSearchResult().size() > 0); //47
    //search product below category
    Category category = findAndAssertCategory("Apparel", null);
    Map<String, String> searchParams = new HashMap<>();
    searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category.getExternalTechId());
    SearchResult<Product> searchResultByCategory = testling.searchProducts("shoe", searchParams);
    assertNotNull(searchResultByCategory);
    assertTrue(searchResultByCategory.getSearchResult().size() >= 3);

    //search product with paging
    Map<String, String> pagingParams = new HashMap<>();
    pagingParams.put(CatalogService.SEARCH_PARAM_PAGESIZE, "10");
    pagingParams.put(CatalogService.SEARCH_PARAM_PAGENUMBER, "1");
    SearchResult<Product> searchResultPaging = testling.searchProducts("shoe", pagingParams);
    assertNotNull(searchResultPaging);
    assertTrue(searchResultPaging.getSearchResult().size() >= 3);
    Product product1 = searchResultPaging.getSearchResult().get(9);

    pagingParams.put(CatalogService.SEARCH_PARAM_PAGESIZE, "9");
    pagingParams.put(CatalogService.SEARCH_PARAM_PAGENUMBER, "2");
    searchResultPaging = testling.searchProducts("shoe", pagingParams);
    Product product2 = searchResultPaging.getSearchResult().get(0);
    assertNotNull(searchResultPaging);
    assertTrue(searchResultPaging.getSearchResult().size() >= 3);
    assertEquals(product1.getId(), product2.getId());

    //search product with invalid param
    Map<String, String> ignoredParam = new HashMap<>();
    ignoredParam.put("blub", "10");
    ignoredParam.put(CatalogService.SEARCH_PARAM_PAGESIZE, "10");
    ignoredParam.put(CatalogService.SEARCH_PARAM_PAGENUMBER, "1");
    SearchResult<Product> searchResultIgnoredParam = testling.searchProducts("shoe", ignoredParam);
    assertNotNull(searchResultIgnoredParam);
    assertTrue(searchResultIgnoredParam.getSearchResult().size() >= 3);

    //search product no hits
    SearchResult<Product> searchResultEmpty = testling.searchProducts("schnasndasn", null);
    assertNotNull(searchResultEmpty);
    assertTrue(searchResultEmpty.getSearchResult().size() == 0);

    //search product multiple words
    SearchResult<Product> searchResultMultipleWords = testling.searchProducts("sport shoe", null);
    assertNotNull(searchResultMultipleWords);
    assertTrue(searchResultMultipleWords.getSearchResult().size() > 0);

    //search product multiple words no hit
    SearchResult<Product> searchResultMultipleWords2 = testling.searchProducts("sport schnasndasn", null);
    assertNotNull(searchResultMultipleWords2);
    assertTrue(searchResultMultipleWords2.getSearchResult().size() == 0);
  }

  public void testSearchProductVariants() throws Exception {
    SearchResult<ProductVariant> searchResult = testling.searchProductVariants("Leo Currency Dress Shoe", null);
    assertNotNull(searchResult);
    assertTrue(searchResult.getSearchResult().size() > 0);

    assertTrue(checkIfClassIsContained(searchResult.getSearchResult(), ProductVariantImpl.class));
    assertTrue(!checkIfClassIsContained(searchResult.getSearchResult(), ProductImpl.class));

    //search product variants by parent part number
    SearchResult<ProductVariant> searchResult2 = testling.searchProductVariants(PRODUCT_CODE, null);
    assertNotNull(searchResult2);
    assertTrue("search result must not be empty (search product variants by parent part number)",
            searchResult2.getSearchResult().size() > 0);

    assertTrue(checkIfClassIsContained(searchResult2.getSearchResult(), ProductVariantImpl.class));
    assertTrue(!checkIfClassIsContained(searchResult2.getSearchResult(), ProductImpl.class));
  }

  private boolean checkIfClassIsContained(List list, Class containedClassType) {
    for (Object item : list) {
      if (item.getClass().equals(containedClassType)) {
        return true;
      }
    }
    return false;
  }

  public void testFindTopCategories() throws Exception {
    List<Category> topCategories = testling.findTopCategories(null);
    assertTrue(topCategories.size() >= 5);
    Category category = findAndAssertCategory("Apparel", null);
    assertTrue(category.getShortDescription().asXml().contains("<p>The"));
    assertTrue(CommerceIdHelper.isCategoryId(category.getId()));
    assertEquals(new Locale("en", "US"), category.getLocale());
    assertNull(category.getParent());
    assertEquals("Apparel", category.getBreadcrumb().get(0).getName());
  }

  public void testFindSubCategories() throws Exception {
    Category category = findAndAssertCategory("Apparel", null);
    List<Category> subCategories = testling.findSubCategories(category);
    assertTrue(subCategories.size() >= 3);
    //assert the beans' properties. Select the bean randomly out of the 6.
    assertTrue(CommerceIdHelper.isCategoryId(subCategories.get(0).getId()));
    assertEquals(new Locale("en", "US"), subCategories.get(3).getLocale());
    assertEquals(2, subCategories.get(0).getBreadcrumb().size());
    assertNotNull("Breadcrumb must not be null", subCategories.get(0).getBreadcrumb().get(0).getName());
  }

  public void testFindSubCategoriesWithContract() throws Exception {

    Commerce.getCurrentConnection().getStoreContext().put("storeId", "10303");

    Category category = findAndAssertCategory("Hardware", null);
    assertNotNull(category);

    category = findAndAssertCategory("Fasteners", category);
    assertNotNull(category);

//    Commerce.getCurrentConnection().getStoreContext().setContractId("4000000000000000502");

    List<Category> subCategories = testling.findSubCategories(category);

    assertTrue(subCategories.size() >= 3);
    //assert the beans' properties. Select the bean randomly out of the 6.
    assertTrue(CommerceIdHelper.isCategoryId(subCategories.get(0).getId()));
    assertEquals("Bolts", subCategories.get(0).getName());
    assertEquals("Nuts", subCategories.get(1).getName());
    assertEquals("Screws", subCategories.get(2).getName());

  }

  public void testFindSubCategoriesIsEmpty() throws Exception {
    Category categoryMock = Mockito.mock(CategoryImpl.class);
    when(categoryMock.getExternalTechId()).thenReturn("blablablub");
    List<Category> subCategories = testling.findSubCategories(categoryMock);
    assertNotNull(subCategories);
    assertTrue(subCategories.isEmpty());
  }

  public void testFindCategoryByExternalTechId() throws Exception {
    Category category1 = testling.findCategoryById(CommerceIdHelper.formatCategoryId("Furniture"));
    Category category2 = testling.findCategoryById(CommerceIdHelper.formatCategoryTechId(category1.getExternalTechId()));
    assertEquals("Furniture", category2.getName());
    assertTrue(category2.getShortDescription().asXml().contains("<p>Furniture</p>"));
    //todo dst for search based is null assertEquals("Furniture", category2.getMetaDescription());
    //todo dst for search based is null assertEquals("Furniture", category2.getMetaKeywords());
    //todo dst assertEquals("Furniture | PerfectChefESite", category2.getTitle());
    assertTrue(CommerceIdHelper.isCategoryId(category2.getId()));
    assertEquals(new Locale("en", "US"), category2.getLocale());
    assertEquals("Furniture", category2.getExternalId());
    assertEquals("Home Furnishings", category2.getParent().getExternalId());
    assertEquals(2, category2.getBreadcrumb().size());
    assertEquals("Home & Furnishing", category2.getBreadcrumb().get(0).getName());
    assertEquals("Furniture", category2.getBreadcrumb().get(1).getName());
    assertEquals("furniture", category2.getSeoSegment());
    //TODO assertEquals(category.getThumbnail(), "???");

  }

  public void testFindCategoryByExternalTechIdIsNull() throws Exception {
    Category category = testling.findCategoryById(CommerceIdHelper.formatCategoryTechId("blablablub"));
    assertNull(category);
  }

  public void testFindCategoryBySeoSegment() throws Exception {
    Category category = testling.findCategoryBySeoSegment("blouses");
    assertEquals("Blouses", category.getName());
    assertTrue(category.getShortDescription().asXml().contains("<p>Blouses</p>"));
    //TODO dst ;-) assertEquals("casual, business, light, evening", category.getMetaKeywords());
    //TODO dst ;-) assertEquals("Blouses", category.getMetaDescription());
    //TODO dst ;-) assertEquals("Blouses | PerfectChefESite", category.getTitle());
    assertTrue(CommerceIdHelper.isCategoryId(category.getId()));
    assertEquals(new Locale("en", "US"), category.getLocale());
    assertEquals("Women Shirts Blouses", category.getExternalId());
    assertEquals("Women", category.getParent().getExternalId());
    assertEquals(3, category.getBreadcrumb().size());
    assertEquals("Apparel", category.getBreadcrumb().get(0).getName());
    assertEquals("Women", category.getBreadcrumb().get(1).getName());
    assertEquals("Blouses", category.getBreadcrumb().get(2).getName());
    //TODO assertEquals(category.getThumbnail(), "???");

  }

  public void testWithStoreContext() {
    StoreContext storeContext = Commerce.getCurrentConnection().getStoreContext();
    assertNotEquals(Locale.GERMAN, storeContext.getLocale());

    StoreContext tempStoreContext = StoreContextHelper.cloneContext(storeContext);
    tempStoreContext.put(StoreContextBuilder.LOCALE, Locale.GERMAN);

    CatalogService catalogServiceWithTempStoreContext = testling.withStoreContext(tempStoreContext);
    Product product = catalogServiceWithTempStoreContext.findProductById(CommerceIdHelper.formatProductId(PRODUCT_CODE));

    assertEquals(Locale.GERMAN, product.getLocale());
  }

  public void testWithStoreContextRethrowException() {
    CatalogService catalogServiceWithTempStoreContext = null;
    try {
      StoreContext storeContext = StoreContextHelper.createContext(null, null, null, null, null, null);
      catalogServiceWithTempStoreContext = testling.withStoreContext(storeContext);
    } catch (CommerceException e) {
      e.printStackTrace();
      fail("Exception not expected here, but later...");
    }
    // should fail with commerce exception
    catalogServiceWithTempStoreContext.findProductById(CommerceIdHelper.formatProductId(PRODUCT_CODE));
  }


  public void testFindCategoryBySeoSegmentIsNull() throws Exception {
    Category category = testling.findCategoryBySeoSegment("blablablub");
    assertNull(category);
  }

  public void testFindCategoryByExternalId() {
    Category category = testling.findCategoryById(CommerceIdHelper.formatCategoryId("Apparel"));
    assertNotNull(category);
  }

  public void testFindCategoryByExternalIdIsNull() {
    Category category = testling.findCategoryById(CommerceIdHelper.formatCategoryId("balablablub"));
    assertNull(category);
  }

  private void assertProduct(Product product) throws CommerceException {
    assertNotNull(product);
    assertEquals(PRODUCT_NAME, product.getName());
    assertEquals(PRODUCT_SEO_SEGMENT, product.getSeoSegment());
    assertTrue(product.getDefaultImageUrl().endsWith(".jpg"));
    // TODO dst required in search servers assertFalse("image alt text is wrong", StringUtils.isEmpty(product.getDefaultImageAlt()));
    assertTrue(product.getThumbnailUrl().endsWith(".jpg"));
    assertNotNull(product.getCategory());
    assertEquals("Computers Laptops", product.getCategory().getExternalId());
    assertEquals(2, product.getCategory().getBreadcrumb().size());
    assertEquals("Electronics", product.getCategory().getBreadcrumb().get(0).getName());
    assertEquals("Laptops", product.getCategory().getBreadcrumb().get(1).getName());
    List<ProductAttribute> definingAttributes = product.getDefiningAttributes();
    assertEquals(4, definingAttributes.size());

    List<ProductAttribute> describingAttributes = product.getDescribingAttributes();
    assertEquals(3, describingAttributes.size());

    assertArrayEquals(new String[]{"ComputersLaptopsMemory", "ComputersLaptopsBrand", "ComputersLaptopsWeight", "ComputersLaptopsBattery Life"}, product.getVariantAxisNames().toArray());
    assertArrayEquals(new Object[]{"2 GB"}, product.getVariantAxisValues("ComputersLaptopsMemory", (VariantFilter) null).toArray());
    List<ProductVariant> productVariants = product.getVariants(new AxisFilter("ComputersLaptopsMemory", "2 GB"));
    assertEquals(1, productVariants.size());
    productVariants = product.getVariants(new AxisFilter("ComputersLaptopsBattery Life", "6 hours"));
    assertEquals(1, productVariants.size());
    productVariants = product.getVariants(new AxisFilter("ComputersLaptopsMemory", "*"));
    assertEquals(1, productVariants.size());
    //TODO: assert more properties
  }

  private void assertProduct2(Product product) throws CommerceException {
    assertNotNull(product);
    assertTrue(!product.getName().isEmpty());
    assertTrue(!product.getSeoSegment().isEmpty());
    assertTrue(product.getDefaultImageUrl().endsWith(".jpg"));
    // TODO dst required in search servers assertFalse("image alt text is wrong", StringUtils.isEmpty(product.getDefaultImageAlt()));
    assertTrue(product.getThumbnailUrl().endsWith(".jpg"));
    assertNotNull(product.getCategory());
    assertEquals("Dresses", product.getCategory().getExternalId());
    assertEquals(3, product.getCategory().getBreadcrumb().size());
    assertEquals("Apparel", product.getCategory().getBreadcrumb().get(0).getName());
    assertEquals("Women", product.getCategory().getBreadcrumb().get(1).getName());
    assertEquals("Dresses", product.getCategory().getBreadcrumb().get(2).getName());

    List<ProductAttribute> definingAttributes = product.getDefiningAttributes();
    assertEquals(2, definingAttributes.size());
    assertEquals("swatchcolor", definingAttributes.get(0).getId());
    assertTrue(definingAttributes.get(0).getType() == null || definingAttributes.get(0).getType().equalsIgnoreCase("string"));
    assertEquals(null, definingAttributes.get(0).getUnit());
    assertEquals("Red", definingAttributes.get(0).getValue());
    assertArrayEquals(new String[]{"Red", "Teal", "Blue"}, definingAttributes.get(0).getValues().toArray());
    assertTrue(definingAttributes.get(0).isDefining());

    List<ProductAttribute> describingAttributes = product.getDescribingAttributes();
    assertEquals(4, describingAttributes.size());
    assertFalse(describingAttributes.get(0).isDefining());

    List<String> axis = product.getVariantAxisNames();
    assertArrayEquals(new String[]{"swatchcolor", "swatchSize"}, axis.toArray());
    assertEquals(3, product.getAttributeValues("swatchcolor").size());
    assertEquals(1, product.getAttributeValues("material").size());

    List<ProductVariant> productVariants = product.getVariants((VariantFilter) null);
    assertEquals(13, productVariants.size());
    productVariants = product.getVariants();
    assertEquals(13, productVariants.size());
    productVariants = product.getVariants(new AxisFilter("swatchcolor", "Red"));
    assertEquals(4, productVariants.size());
    productVariants = product.getVariants(new AxisFilter("swatchcolor", "*"));
    assertEquals(13, productVariants.size());
    productVariants = product.getVariants(new AxisFilter("non_existent", "*"));
    assertEquals(0, productVariants.size());
    productVariants = product.getVariants();
    assertEquals(13, productVariants.size());

    productVariants = product.getVariants(new AxisFilter("swatchcolor", "Red"));
    ProductVariant productVariant = productVariants.get(0);
    assertEquals(2, productVariant.getDefiningAttributes().size());
    assertEquals(4, productVariant.getDescribingAttributes().size());
    assertEquals("Red", productVariant.getAttributeValue("swatchcolor"));
    assertEquals("Synthetic", productVariant.getAttributeValue("material"));
    assertArrayEquals(new String[]{"Synthetic"}, productVariant.getAttributeValues("material").toArray());

    //TODO: assert more properties
  }

  private void assertProductVariant(ProductVariant productVariant) throws CommerceException {
    assertNotNull(productVariant);
    assertEquals("Travel Laptop", productVariant.getName());
    assertEquals("travel-laptop-cla022-220301", productVariant.getSeoSegment());
    assertTrue(productVariant.getDefaultImageUrl().endsWith(".jpg"));
    assertTrue(productVariant.getThumbnailUrl().endsWith(".jpg"));
    assertTrue(productVariant.getDefiningAttributes().size() == 4);
    assertEquals("DVR Technics", productVariant.getDefiningAttributes().get(0).getValue());
    assertEquals(3, productVariant.getDescribingAttributes().size());
    assertNotNull(productVariant.getOfferPrice());
  }

  private Category findAndAssertCategory(String name, Category parent) {
    List<Category> topCategories = parent == null ? testling.findTopCategories(null) : testling.findSubCategories(parent);
    assertTrue(topCategories.size() > 0);
    Category category = null;
    for (Category c : topCategories) {
      if (name.equals(c.getExternalId())) {
        category = c;
      }
    }
    assertNotNull("Category \"" + name + "\" not found", category);
    assertEquals(name, category.getName());
    return category;
  }

}
