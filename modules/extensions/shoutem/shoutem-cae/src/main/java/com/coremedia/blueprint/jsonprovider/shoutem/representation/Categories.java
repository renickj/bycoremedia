package com.coremedia.blueprint.jsonprovider.shoutem.representation;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the list of taxonomies, used as categories for a post/article.
 */
public class Categories {
  private List<Category> data = null;

  public void addCategory(int id, String name) {
    if(data == null) {
      data = new ArrayList<>();
    }
    data.add(new Category(id, name));
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public List<Category> getData() {
    return data;
  }
  
  private static class Category {
    private int category_id;// NOSONAR
    private String name;
    private boolean allowed = true;

    Category(int id, String name) {
      this.category_id = id;
      this.name = name;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public int getCategory_id() {// NOSONAR
      return category_id;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public String getName() {
      return name;
    }

    public boolean isAllowed() {
      return allowed;
    }
  }
}
