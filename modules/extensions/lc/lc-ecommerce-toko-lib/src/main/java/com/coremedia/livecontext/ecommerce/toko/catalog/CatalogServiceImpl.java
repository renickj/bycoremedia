package com.coremedia.livecontext.ecommerce.toko.catalog;

import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.livecontext.ecommerce.toko.common.AbstractTokoCommerceBean;
import com.coremedia.livecontext.ecommerce.toko.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.toko.common.StoreContextHelper;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Example {@link com.coremedia.livecontext.ecommerce.catalog.CatalogService implementation.
 */
public class CatalogServiceImpl implements CatalogService{

  private static final Logger LOG = LoggerFactory.getLogger(CatalogServiceImpl.class);

  private CommerceBeanFactory commerceBeanFactory;
  private TokoCatalogMock tokoCatalogMock;

  @Nullable
  @Override
  public Product findProductById(@Nonnull String id) throws CommerceException {
    JsonNode delegate = tokoCatalogMock.getProductById(CommerceIdHelper.parseExternalIdFromId(id));
    return createCommerceBeanFor(id, delegate);
  }

  @Nullable
  @Override
  public ProductVariant findProductVariantById(@Nonnull String id) throws CommerceException {
    JsonNode delegate = tokoCatalogMock.getProductVariantById(CommerceIdHelper.parseExternalIdFromId(id));
    return createCommerceBeanFor(id, delegate);
  }

  @Nullable
  @Override
  public Product findProductBySeoSegment(@Nonnull String seoSegment) throws CommerceException {
    return null;
  }

  @Nonnull
  @Override
  public List<Product> findProductsByCategory(@Nonnull Category category) throws CommerceException {
    List<JsonNode> delegates = tokoCatalogMock.getProductsByCategoryId(category.getExternalId());
    return createCommerceBeansFor(delegates, Product.class);
  }

  @Nonnull
  @Override
  public List<Category> findTopCategories(Site site) throws CommerceException {
    List<JsonNode> delegates = tokoCatalogMock.getTopCategories();
    return createCommerceBeansFor(delegates, Category.class);
  }

  @Nonnull
  @Override
  public List<Category> findSubCategories(@Nonnull Category parentCategory) throws CommerceException {
    List<JsonNode> delegates = tokoCatalogMock.getSubCategories(parentCategory.getExternalId());
    return createCommerceBeansFor(delegates, Category.class);
  }

  @Nullable
  @Override
  public Category findCategoryById(@Nonnull String id) throws CommerceException {
    JsonNode delegate = tokoCatalogMock.getCategoryById(CommerceIdHelper.parseExternalIdFromId(id));
    return createCommerceBeanFor(id, delegate);
  }

  @Nullable
  @Override
  public Category findCategoryBySeoSegment(@Nonnull String seoSegment) throws CommerceException {
    return null;
  }

  @Nonnull
  @Override
  public SearchResult<Product> searchProducts(@Nonnull String searchTerm, @Nullable Map<String, String> searchParams) throws CommerceException {
    SearchResult<Product> result = new SearchResult<>();
    List<JsonNode> delegates = tokoCatalogMock.searchProducts(searchTerm, searchParams);
    List<Product> products = createCommerceBeansFor(delegates, Product.class);
    result.setSearchResult(products);
    result.setTotalCount(products.size());
    return result;
  }

  @Nonnull
  @Override
  public SearchResult<ProductVariant> searchProductVariants(@Nonnull String searchTerm, @Nullable Map<String, String> searchParams) throws CommerceException {
    SearchResult<ProductVariant> result = new SearchResult<>();
    List<JsonNode> delegates = tokoCatalogMock.searchProductsVariants(searchTerm, searchParams);
    List<ProductVariant> productVariants = createCommerceBeansFor(delegates, ProductVariant.class);
    result.setSearchResult(productVariants);
    result.setTotalCount(productVariants.size());
    return result;
  }

  @Nonnull
  @Override
  public CatalogService withStoreContext(StoreContext storeContext) {
    return this;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  @SuppressWarnings("unused")
  public CommerceBeanFactory getCommerceBeanFactory() {
    return commerceBeanFactory;
  }

  @Required
  public void setTokoCatalogMock(TokoCatalogMock tokoCatalogMock) {
    this.tokoCatalogMock = tokoCatalogMock;
  }

  @SuppressWarnings("unchecked")
  private <T> T createCommerceBeanFor(String id, JsonNode delegate) {
    if (delegate != null) {
      T bean = (T) commerceBeanFactory.createBeanFor(id, StoreContextHelper.getCurrentContext());
      ((AbstractTokoCommerceBean) bean).setDelegate(delegate);
      return bean;
    }
    return null;
  }

  private <T> List<T> createCommerceBeansFor(List<JsonNode> delegateList, Class<T> typeOf) {
    List<T> result = new ArrayList<>();
    if (delegateList != null) {
      for (JsonNode delegate : delegateList) {
        String externalId = getExternalId(delegate);
        if (externalId != null) {
          String id = CommerceIdHelper.formatCommerceId(externalId, typeOf);
          T bean = createCommerceBeanFor(id, delegate);
          result.add(bean);
        }
        else {
          LOG.warn("Commerce item of type '" + typeOf + "' is not valid: " + delegate);
        }
      }
    }
    return result;
  }

  private String getExternalId(JsonNode jsonNode) {
    JsonNode idNode = jsonNode.get(AbstractTokoCommerceBean.ID_KEY);
    return idNode != null ? idNode.asText() : null;
  }

}
