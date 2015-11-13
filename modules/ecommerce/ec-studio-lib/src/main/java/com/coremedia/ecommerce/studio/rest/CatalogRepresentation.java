package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.ChildRepresentation;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Catalog representation for JSON.
 */
public class CatalogRepresentation extends AbstractCatalogRepresentation {

  private List<Category> topCategories;
  private Store store;

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public List<Category> getTopCategories() {
    return topCategories;
  }

  public void setTopCategories(List<Category> topCategories) {
    this.topCategories = RepresentationHelper.sort(topCategories);
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Map<String, ChildRepresentation> getChildrenByName() {
    Map<String, ChildRepresentation> result = new LinkedHashMap<>();
    List<Category> subCategories = new ArrayList<>(topCategories);
    for (Category child : subCategories) {
      ChildRepresentation childRepresentation = new ChildRepresentation();
      childRepresentation.setChild(child);
      childRepresentation.setDisplayName(child.getDisplayName());
      result.put(child.getId(), childRepresentation);
    }
    return RepresentationHelper.sortChildren(result);
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }
}
