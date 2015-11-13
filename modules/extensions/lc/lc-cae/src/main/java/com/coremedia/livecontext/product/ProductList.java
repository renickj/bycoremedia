package com.coremedia.livecontext.product;

import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.livecontext.context.CategoryInSite;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation class for REST calls to display a subset of products for a category.
 */
public class ProductList {
  private List<ProductInSite> loadedProducts = new ArrayList<>();

  private int start;
  private int steps;
  private int totalProductCount;
  private LiveContextNavigation navigation;
  private LiveContextNavigationFactory liveContextNavigationFactory;

  public ProductList(LiveContextNavigation navigation, int start, int steps, int totalProductCount,
                     LiveContextNavigationFactory liveContextNavigationFactory) {
    this.start = start;
    this.steps = steps;
    this.navigation = navigation;
    this.totalProductCount = totalProductCount;
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  public boolean isProductCategory() {
    return totalProductCount > 0;
  }

  public boolean hasCategories() {
    return !CollectionUtils.isEmpty(navigation.getChildren());
  }

  public void setLoadedProducts(List<ProductInSite> products) {
    this.loadedProducts = products;
  }

  public List<ProductInSite> getLoadedProducts() {
    return loadedProducts;
  }

  public int getStart() {
    return start;
  }

  public int getTotalProductCount() {
    return totalProductCount;
  }

  public int getSteps() {
    return steps;
  }

  public LiveContextNavigation getNavigation() {
    return navigation;
  }

  public Category getCategory() {
    return navigation.getCategory();
  }

  public String getCategoryId() {
    return navigation.getCategory().getExternalId();
  }

  public List<CategoryInSite> getSubCategoriesInSite() {
    List<CategoryInSite> result = new ArrayList<>();
    for (Linkable child : getNavigation().getChildren()) {
      if (child instanceof LiveContextNavigation) {
        LiveContextNavigation lcNavigation = (LiveContextNavigation) child;
        Category category = lcNavigation.getCategory();
        result.add(liveContextNavigationFactory.createCategoryInSite(category, lcNavigation.getSite()));
      }
    }
    return result;
  }
}
