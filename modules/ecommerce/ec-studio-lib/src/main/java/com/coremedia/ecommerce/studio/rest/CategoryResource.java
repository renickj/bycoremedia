package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Catalog;
import com.coremedia.ecommerce.studio.rest.model.ChildRepresentation;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A catalog {@link Category} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/category/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:[^/]+}")
public class CategoryResource extends CommerceBeanResource<Category> {

  @Override
  public CategoryRepresentation getRepresentation() {
    CategoryRepresentation categoryRepresentation = new CategoryRepresentation();
    fillRepresentation(categoryRepresentation);
    return categoryRepresentation;
  }

  protected void fillRepresentation(CategoryRepresentation representation) {
    try {
      super.fillRepresentation(representation);
      Category entity = getEntity();
      representation.setName(entity.getName());
      String shortDescription = entity.getShortDescription().asXml();
      representation.setShortDescription(shortDescription);
      String longDescription = entity.getLongDescription().asXml();
      representation.setLongDescription(longDescription);
      representation.setThumbnailUrl(entity.getThumbnailUrl());
      representation.setParent(entity.getParent());
      representation.setSubCategories(RepresentationHelper.sort(entity.getChildren()));
      representation.setProducts(RepresentationHelper.sort(entity.getProducts()));
      representation.setStore((new Store(entity.getContext())));
      representation.setDisplayName(entity.getDisplayName());

      List<CommerceBean> children = new ArrayList<>();
      children.addAll(representation.getSubCategories());
      children.addAll(representation.getProducts());
      representation.setChildren(children);

      Map<String, ChildRepresentation> result = new LinkedHashMap<>();
      for (CommerceBean child : children) {
        ChildRepresentation childRepresentation = new ChildRepresentation();
        childRepresentation.setChild(child);
        if(child instanceof Category) {
          childRepresentation.setDisplayName(((Category)child).getDisplayName());
        }
        else {
          childRepresentation.setDisplayName(child.getExternalId());
        }

        result.put(child.getId(), childRepresentation);
      }
      representation.setChildrenByName(RepresentationHelper.sortChildren(result));

      representation.setCatalog(new Catalog(entity.getContext()));

    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected Category doGetEntity() {
    CommerceConnection commerceConnection = getConnection();
    String categoryId = commerceConnection.getIdProvider().formatCategoryId(getId());
    return commerceConnection.getCatalogService().findCategoryById(categoryId);
  }

}
