package com.coremedia.livecontext.ecommerce.toko.catalog;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokoCatalogMock {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokoCatalogMock.class);

  private static final String TOKO_CATALOG_JSON = "toko-catalog.json";
  private static final String TOP_CATEGORIES = "topCategories";

  private static final String EXTERNAL_ID = "externalId";
  private static final String PRODUCTS = "products";
  private static final String SUB_CATEGORIES = "subCategories";
  private static final String VARIANTS = "variants";
  private static final String NAME = "name";
  private static final String SHORT_DESCRIPTION = "shortDescription";
  private static final String SEARCH_PARAM_CATEGORYID = "categoryId";

  private List<JsonNode> topCategories;
  private Map<String, JsonNode> idToCategory;
  private Map<String, JsonNode> idToProduct;
  private Map<String, JsonNode> idToProductVariant;
  private Map<String, List<JsonNode>> categoryIdToSubCategories;
  private Map<String, List<JsonNode>> categoryIdToProducts;
  private Map<String, List<JsonNode>> productIdToVariants;

  private Map<String, JsonNode> productVariantIdToParent;
  private Map<String, JsonNode> productIdToCategory;
  private Map<String, JsonNode> categoryIdToParent;

  public TokoCatalogMock() {
    initialize();
  }

  private void initialize() {
    topCategories = new ArrayList<>();
    idToCategory = new HashMap<>();
    idToProduct = new HashMap<>();
    idToProductVariant = new HashMap<>();
    categoryIdToSubCategories = new HashMap<>();
    categoryIdToProducts = new HashMap<>();
    productIdToVariants = new HashMap<>();
    productVariantIdToParent = new HashMap<>();
    productIdToCategory = new HashMap<>();
    categoryIdToParent = new HashMap<>();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      InputStream resourceAsStream = getClass().getResourceAsStream(TOKO_CATALOG_JSON);
      InputStreamReader resourceReader = new InputStreamReader(resourceAsStream);
      JsonNode tokoCatalogNode = objectMapper.readTree(resourceReader);
      ArrayNode topCategories = (ArrayNode) tokoCatalogNode.get(TOP_CATEGORIES);
      for (JsonNode topCategory : topCategories) {
        readCategory(topCategory, null);
      }
    } catch (IOException ex) {
      throw new CommerceException(ex);
    }
  }

  private void readCategory(JsonNode category, JsonNode parent) {

    String categoryId = getId(category);

    idToCategory.put(categoryId, category);
    categoryIdToParent.put(getId(category), parent);

    if (parent == null) {
      topCategories.add(category);
    } else {
      List<JsonNode> categories = categoryIdToSubCategories.get(getId(parent));
      if (categories == null) {
        categories = new ArrayList<>();
      }
      categories.add(category);
      categoryIdToSubCategories.put(getId(parent), categories);
    }

    // create the products
    ArrayNode products = (ArrayNode) category.get(PRODUCTS);
    if (products != null) {
      for (JsonNode product : products) {
        readProduct(product, category);
      }
    }

    //now create children categories
    ArrayNode subCategories = (ArrayNode) category.get(SUB_CATEGORIES);
    if (subCategories != null) {
      for (JsonNode subCategory : subCategories) {
        readCategory(subCategory, category);
      }
    }
  }

  private void readProduct(JsonNode product, JsonNode category) {

    String productId = getId(product);
    String categoryId = getId(category);

    idToProduct.put(productId, product);
    productIdToCategory.put(productId, category);

    List<JsonNode> products = categoryIdToProducts.get(categoryId);
    if (products == null) {
      products = new ArrayList<>();
    }
    products.add(product);
    categoryIdToProducts.put(getId(category), products);

    //create now the product variants
    ArrayNode variants = (ArrayNode) product.get(VARIANTS);
    if (variants != null) {
      for (JsonNode variant : variants) {
        readProductVariant(variant, product);
      }
    }
  }

  private void readProductVariant(JsonNode productVariant, JsonNode parent) {

    String productVariantId = getId(productVariant);
    String parentId = getId(parent);

    idToProductVariant.put(productVariantId, productVariant);
    productVariantIdToParent.put(productVariantId, parent);
    List<JsonNode> productVariants = productIdToVariants.get(parentId);
    if (productVariants == null) {
      productVariants = new ArrayList<>();
    }
    productVariants.add(productVariant);
    productIdToVariants.put(parentId, productVariants);
  }


  /************************************************
   * Now the getters to be used by CatalogService
   ************************************************/

  public List<JsonNode> getTopCategories() {
    return topCategories;
  }

  public List<JsonNode> getSubCategories(String parentCategoryId) {
    List<JsonNode> categories = categoryIdToSubCategories.get(parentCategoryId);
    return categories == null ? Collections.<JsonNode>emptyList() : categories;
  }

  public JsonNode getParentCategory(String categoryId) {
    return categoryIdToParent.get(categoryId);
  }

  public JsonNode getCategoryById(String id) {
    JsonNode category = idToCategory.get(id);
    if (category == null) {
      int modulo = Math.abs(id.hashCode() % (idToCategory.size())) ;
      category = (JsonNode) idToCategory.values().toArray()[modulo];
      LOGGER.warn("The category with the id " + id + " not found. Take " + category.get(EXTERNAL_ID).asText());
    }
    return category;
  }

  public List<JsonNode> getProductsByCategoryId(String categoryId) {
    List<JsonNode> products = categoryIdToProducts.get(categoryId);
    return products == null ? Collections.<JsonNode>emptyList() : products;
  }

  public JsonNode getCategoryByProductId(String productId) {
    JsonNode category = productIdToCategory.get(productId);
    if (category == null) {
      int modulo = Math.abs(productId.hashCode() % (idToCategory.size())) ;
      category = (JsonNode) idToCategory.values().toArray()[modulo];
      LOGGER.warn("No category found for product with the id " + productId + " not found. Take " + category.get(EXTERNAL_ID).asText());
    }
    return category;
  }

  public JsonNode getProductById(String id) {
    JsonNode product = idToProduct.get(id);
    if (product == null) {
      int modulo = Math.abs(id.hashCode() % (idToProduct.size())) ;
      product = (JsonNode) idToProduct.values().toArray()[modulo];
      LOGGER.warn("The product with the id " + id + " not found. Take " + product.get(EXTERNAL_ID).asText());
    }
    return product;
  }

  public JsonNode getParentProduct(String productVariantId) {
    return productVariantIdToParent.get(productVariantId);
  }

  public JsonNode getProductVariantById(String id) {
    JsonNode productVariant = idToProductVariant.get(id);
    if (productVariant == null) {
      int modulo = Math.abs(id.hashCode() % (idToProductVariant.size())) ;
      productVariant = (JsonNode) idToProductVariant.values().toArray()[modulo];
      LOGGER.warn("The product variant with the id " + id + " not found. Take " + productVariant.get(EXTERNAL_ID).asText());
    }
    return productVariant;
  }

  public List<JsonNode> searchProducts(String searchTerm, Map<String, String> searchParams) {
    List<JsonNode> products = new ArrayList<>();
    for (JsonNode product : idToProduct.values()) {
      if (searchTerm.equals("*") ||
          product.get(NAME).asText().contains(searchTerm) ||
          product.get(EXTERNAL_ID).asText().contains(searchTerm) ||
          product.get(SHORT_DESCRIPTION).asText().contains(searchTerm)) {
        if (isParent(getCategoryFromSearchParams(searchParams), product)) {
          products.add(product);
        }
      }
    }
    return products;
  }

  public List<JsonNode> searchProductsVariants(String searchTerm, Map<String, String> searchParams) {
    List<JsonNode> productVariants = new ArrayList<>();
    for (JsonNode productVariant : idToProductVariant.values()) {
      if (searchTerm.equals("*") ||
              productVariant.get(NAME).asText().contains(searchTerm) ||
              productVariant.get(EXTERNAL_ID).asText().contains(searchTerm) ||
              productVariant.get(SHORT_DESCRIPTION).asText().contains(searchTerm) ||
              getId(getParentProduct(getId(productVariant))).contains(searchTerm)) {
        if (isParent(getCategoryFromSearchParams(searchParams), productVariant)) {
          productVariants.add(productVariant);
        }
      }
    }
    return productVariants;
  }

  private JsonNode getCategoryFromSearchParams(Map<String, String> searchParams) {
    if (searchParams != null) {
      String categoryId = searchParams.get(SEARCH_PARAM_CATEGORYID);
      if (categoryId != null) {
        return getCategoryById(categoryId);
      }
    }
    return null;
  }

  private String getId(JsonNode jsonNode) {
    JsonNode idNode = jsonNode.get(EXTERNAL_ID);
    return idNode != null ? idNode.asText() : "undefined";
  }

  private boolean isParent(JsonNode category, JsonNode product) {
    return category == null || getBreadcrumb(product).contains(category);
  }

  private List<JsonNode> getBreadcrumb(JsonNode product) {
    List<JsonNode> breadcrumb = new ArrayList<>();
    String productId = getId(product);
    JsonNode test = productVariantIdToParent.get(productId);
    if (test != null) {
      productId = getId(test);
    }
    JsonNode category = productIdToCategory.get(productId);
    while (category != null) {
      breadcrumb.add(category);
      category = categoryIdToParent.get(getId(category));
    }
    return breadcrumb;
  }

}
