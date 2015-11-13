package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.FixValueCacheKey;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class CatalogServiceImpl implements CatalogService {

  private static final Logger LOG = LoggerFactory.getLogger(CatalogServiceImpl.class);

  private WcCatalogWrapperService catalogWrapperService;
  private CommerceBeanFactory commerceBeanFactory;
  private CommerceCache commerceCache;

  private String wcsUrl;
  private String wcsStoreUrl;
  private String wcsAssetsUrl;

  private boolean useExternalIdForBeanCreation;
  private boolean enableAggressiveCaching;

  public WcCatalogWrapperService getCatalogWrapperService() {
    return catalogWrapperService;
  }

  @Required
  public void setCatalogWrapperService(WcCatalogWrapperService catalogWrapperService) {
    this.catalogWrapperService = catalogWrapperService;
  }

  public CommerceBeanFactory getCommerceBeanFactory() {
    return commerceBeanFactory;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  public CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Required
  public void setWcsUrl(String wcsUrl) {
    this.wcsUrl = wcsUrl;
  }

  /**
   * The base url to the commerce system as can bee seen by an end user. This must not be any kind
   * of internal url that may be used as part of the hidden backend communication between CAE and
   * commerce system.
   *
   * @return the publicly known base url to the commerce system
   */
  public String getWcsUrl() {
    return CommercePropertyHelper.replaceTokens(wcsUrl, StoreContextHelper.getCurrentContext());
  }

  @Required
  public void setWcsStoreUrl(String wcsStoreUrl) {
    this.wcsStoreUrl = wcsStoreUrl;
  }

  public String getWcsStoreUrl() {
    return CommercePropertyHelper.replaceTokens(wcsStoreUrl, StoreContextHelper.getCurrentContext());
  }

  @Required
  public void setWcsAssetsUrl(String wcsAssetsUrl) {
    this.wcsAssetsUrl = wcsAssetsUrl;
  }

  public String getWcsAssetsUrl() {
    return CommercePropertyHelper.replaceTokens(wcsAssetsUrl, StoreContextHelper.getCurrentContext());
  }

  public void setUseExternalIdForBeanCreation(boolean useExternalIdForBeanCreation) {
    this.useExternalIdForBeanCreation = useExternalIdForBeanCreation;
  }

  public void setEnableAggressiveCaching(boolean enableAggressiveCaching) {
    this.enableAggressiveCaching = enableAggressiveCaching;
  }

  @PostConstruct
  void initialize(){
    if(null != wcsAssetsUrl) {
      if (!wcsAssetsUrl.endsWith("/")) {
        this.wcsAssetsUrl = wcsAssetsUrl + "/";
      }
      validateUrlString(this.wcsAssetsUrl, "wcsAssetsUrl");
    }

    if(null != wcsStoreUrl) {
      if (!wcsStoreUrl.endsWith("/")) {
        this.wcsStoreUrl = wcsStoreUrl + "/";
      }
      validateUrlString(wcsStoreUrl, "wcsStoreUrl");
    }
  }

  private static void validateUrlString(String string, String urlPropertyName) {
    // validate format of url, e.g. a path part starting with two slashes can lead to broken
    // images in the store front
    try {
      URL url = new URL(string);
      String path = url.getPath();
      if (path.startsWith("//")) {
        LOG.warn("Invalid format of " + urlPropertyName + ": URL's path part starts with '//'. URL is {}", url.toExternalForm());
      }
    } catch (MalformedURLException e) {
      throw new IllegalStateException(urlPropertyName + " is invalid.", e);
    }
  }

  @Override
  @Nullable
  public Product findProductById(@Nonnull final String id) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    Map wcProductMap = (Map) commerceCache.get(
            new ProductCacheKey(id, currentContext, catalogWrapperService, commerceCache));
    return createProductBeanFor(wcProductMap, currentContext);
  }

  @Nullable
  Product findProductByExternalId(@Nonnull final String externalId) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    Map wcProductMap = (Map) commerceCache.get(
            new ProductCacheKey(CommerceIdHelper.formatProductId(externalId), currentContext, catalogWrapperService, commerceCache));
    return createProductBeanFor(wcProductMap, currentContext);
  }

  @Nullable
  public Product findProductByExternalTechId(@Nonnull final String externalTechId) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    Map wcProductMap = (Map) commerceCache.get(
            new ProductCacheKey(CommerceIdHelper.formatProductTechId(externalTechId), currentContext, catalogWrapperService, commerceCache));
    return createProductBeanFor(wcProductMap, currentContext);
  }

  @Override
  @Nullable
  public Product findProductBySeoSegment(@Nonnull final String seoSegment) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    Map wcProductMap = (Map) commerceCache.get(
            new ProductCacheKey(CommerceIdHelper.formatProductSeoId(seoSegment), currentContext, catalogWrapperService, commerceCache));
    return createProductBeanFor(wcProductMap, currentContext);
  }

  @Override
  @Nullable
  public ProductVariant findProductVariantById(@Nonnull final String id) throws CommerceException {
    return (ProductVariant) findProductById(id);
  }

  @Override
  @Nonnull
  public List<Product> findProductsByCategory(@Nonnull final Category category) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    List<Map<String, Object>> wcProductsMap = (List<Map<String, Object>>) commerceCache.get(
            new ProductsByCategoryCacheKey(category.getExternalTechId(), currentContext, catalogWrapperService, commerceCache));
    return (List<Product>) createProductBeansFor(wcProductsMap, currentContext);
  }

  @Override
  @Nullable
  public Category findCategoryById(@Nonnull final String id) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    Map<String, Object> wcCategory = (Map<String, Object>) commerceCache.get(
            new CategoryCacheKey(id, currentContext, catalogWrapperService, commerceCache));
    return createCategoryBeanFor(wcCategory, currentContext);
  }

  @Override
  @Nullable
  public Category findCategoryBySeoSegment(@Nonnull String seoSegment) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    Map<String, Object> wcCategory = (Map<String, Object>) commerceCache.get(
            new CategoryCacheKey(CommerceIdHelper.formatCategorySeoId(seoSegment), currentContext, catalogWrapperService, commerceCache));
    return createCategoryBeanFor(wcCategory, currentContext);
  }

  /**
   * Implementation uses current {@see StoreContext} to resolve some necessary properties
   * for rest call (e.g. site, currency, locale ...)
   */
  @Override
  @Nonnull
  public List<Category> findTopCategories(Site site) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    List<Map<String, Object>> wcCategories = (List<Map<String, Object>>) commerceCache.get(
            new TopCategoriesCacheKey(currentContext, catalogWrapperService, commerceCache));
    return createCategoryBeansFor(wcCategories, currentContext);
  }

  @Override
  @Nonnull
  public List<Category> findSubCategories(@Nonnull final Category parentCategory) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    List<Map<String, Object>> wcCategories = (List<Map<String, Object>>) commerceCache.get(
            new SubCategoriesCacheKey(parentCategory.getExternalTechId(), currentContext, catalogWrapperService, commerceCache));
    return createCategoryBeansFor(wcCategories, currentContext);
  }

  /**
   * Search for Products
   * @param searchTerm   search keywords
   * @param searchParams map of search params:
   *                     <ul>
   *                     <li>pageNumber {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_PAGENUMBER}</li>
   *                     <li>pageSize {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_PAGESIZE}</li>
   *                     <li>categoryId {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_CATEGORYID}</li>
   *                     </ul>
   *                     In addition ibm rest specific parameters may be passed. See ibm rest api of the ProductViewHandler
   *                     for more details. See {@link WcCatalogWrapperService#validSearchParams} for the complete list of allowed parameters
   *                     to pass to the IBM ProductViewHandler.
   * @return SearchResult containing the list of products
   * @throws CommerceException
   */
  @Override
  @Nonnull
  public SearchResult<Product> searchProducts(@Nonnull final String searchTerm,
                                              @Nullable Map<String, String> searchParams) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    SearchResult<Map<String, Object>> wcSearchResult = getCatalogWrapperService().searchProducts(
            searchTerm, searchParams, currentContext, WcCatalogWrapperService.SearchType.SEARCH_TYPE_PRODUCTS);
    SearchResult<Product> result = new SearchResult<>();
    result.setSearchResult((List<Product>) createProductBeansFor(wcSearchResult.getSearchResult(), currentContext));
    result.setTotalCount(wcSearchResult.getTotalCount());
    result.setPageNumber(wcSearchResult.getPageNumber());
    result.setPageSize(wcSearchResult.getPageSize());
    return result;
  }

  /**
   * Search for ProductVariants
   * @param searchTerm   search keywords
   * @param searchParams map of search params:
   *                     <ul>
   *                     <li>pageNumber {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_PAGENUMBER}</li>
   *                     <li>pageSize {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_PAGESIZE}</li>
   *                     <li>categoryId {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService#SEARCH_PARAM_CATEGORYID}</li>
   *                     </ul>
   *                     In addition ibm rest specific parameters may be passed. See ibm rest api of the ProductViewHandler
   *                     for more details. See {@link WcCatalogWrapperService#validSearchParams} for the complete list of allowed parameters
   *                     to pass to the IBM ProductViewHandler.
   * @return SearchResult containing the list of product variants
   * @throws CommerceException
   */
  @Override
  @Nonnull
  public SearchResult<ProductVariant> searchProductVariants(@Nonnull final String searchTerm,
                                                            @Nullable Map<String, String> searchParams) throws CommerceException {
    StoreContext currentContext = StoreContextHelper.getCurrentContext();
    SearchResult<Map<String, Object>> wcSearchResult = getCatalogWrapperService().searchProducts(
            searchTerm, searchParams, currentContext, WcCatalogWrapperService.SearchType.SEARCH_TYPE_PRODUCT_VARIANTS);
    SearchResult<ProductVariant> result = new SearchResult<>();
    result.setSearchResult((List<ProductVariant>) createProductBeansFor(wcSearchResult.getSearchResult(), currentContext));
    result.setTotalCount(wcSearchResult.getTotalCount());
    result.setPageNumber(wcSearchResult.getPageNumber());
    result.setPageSize(wcSearchResult.getPageSize());
    return result;
  }

  public String getLanguageId(Locale locale) {
    return getCatalogWrapperService().getLanguageId(locale);
  }

  protected Product createProductBeanFor(Map<String, Object> productWrapper, StoreContext context) {
    if (productWrapper != null) {
      String id;
      if (DataMapHelper.getValueForKey(productWrapper, "catalogEntryTypeCode", String.class).equals("ItemBean")) {
        id = useExternalIdForBeanCreation ?
                CommerceIdHelper.formatProductVariantId(DataMapHelper.getValueForKey(productWrapper, "partNumber", String.class)) :
                CommerceIdHelper.formatProductVariantTechId(DataMapHelper.getValueForKey(productWrapper, "uniqueID", String.class));
      } else {
        id = useExternalIdForBeanCreation ?
                CommerceIdHelper.formatProductId(DataMapHelper.getValueForKey(productWrapper, "partNumber", String.class)) :
                CommerceIdHelper.formatProductTechId(DataMapHelper.getValueForKey(productWrapper, "uniqueID", String.class));
      }
      if (CommerceIdHelper.isProductId(id) || CommerceIdHelper.isProductVariantId(id)) {
        Product product = (Product) commerceBeanFactory.createBeanFor(id, context);
        ((AbstractIbmCommerceBean) product).setDelegate(productWrapper);
        // register the product wrapper with the cache, it will optimize later accesses (there are good
        // chances that the beans will be called immediately after this call
        // Todo: currently we use it only for studio calls, but check if we can do it for a cae webapp as well
        // (it probably requires that we are able to reload beans dynamically if someone tries to read a property that is not available)
        if (enableAggressiveCaching) {
          commerceCache.put(new FixValueCacheKey(id, context, productWrapper, AbstractCommerceCacheKey.CONFIG_KEY_PRODUCT, commerceCache));
        }
        return product;
      }
    }
    return null;
  }

  protected List<? extends Product> createProductBeansFor(List<Map<String, Object>> productWrappers, StoreContext context) {
    if (productWrappers == null || productWrappers.isEmpty()) {
      return Collections.emptyList();
    }
    List<Product> result = new ArrayList<>(productWrappers.size());
    for (Map<String, Object> productWrapper : productWrappers) {
      result.add(createProductBeanFor(productWrapper, context));
    }
    return Collections.unmodifiableList(result);
  }

  protected Category createCategoryBeanFor(Map<String, Object> categoryWrapper, StoreContext context) {
    if (categoryWrapper != null) {
      String id = useExternalIdForBeanCreation ?
              CommerceIdHelper.formatCategoryId(DataMapHelper.getValueForKey(categoryWrapper, "identifier", String.class)) :
              CommerceIdHelper.formatCategoryTechId(DataMapHelper.getValueForKey(categoryWrapper, "uniqueID", String.class));
      if (CommerceIdHelper.isCategoryId(id)) {
        Category category = (Category) commerceBeanFactory.createBeanFor(id, context);
        ((AbstractIbmCommerceBean) category).setDelegate(categoryWrapper);
        // register the category wrapper with the cache, it will optimize later accesses (there are good
        // chances that the beans will be called immediately after this call
        // Todo: currently we use it only for studio calls, but check if we can do it for a cae webapp as well
        // (it probably requires that we are able to reload beans dynamically if someone tries to read a property that is not available)
        if (enableAggressiveCaching) {
          commerceCache.put(new FixValueCacheKey(id, context, categoryWrapper, AbstractCommerceCacheKey.CONFIG_KEY_CATEGORY, commerceCache));
        }
        return category;
      }
    }
    return null;
  }

  protected List<Category> createCategoryBeansFor(List<Map<String, Object>> categoryWrappers, StoreContext context) {
    if (categoryWrappers == null || categoryWrappers.isEmpty()) {
      return Collections.emptyList();
    }
    List<Category> result = new ArrayList<>(categoryWrappers.size());
    for (Map<String, Object> categoryWrapper : categoryWrappers) {
      result.add(createCategoryBeanFor(categoryWrapper, context));
    }
    return Collections.unmodifiableList(result);
  }

  @Nonnull
  @Override
  public CatalogService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, CatalogService.class);
  }
}
