package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * A catalog {@link Product} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/product/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:[^/]+}")
public class ProductResource extends CommerceBeanResource<Product> {

  @Override
  protected ProductRepresentation getRepresentation() {
    ProductRepresentation representation = new ProductRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  protected void fillRepresentation(ProductRepresentation representation) {
    try {
      super.fillRepresentation(representation);
      Product entity = getEntity();
      representation.setName(entity.getName());
      String shortDescription = entity.getShortDescription().asXml();
      representation.setShortDescription(shortDescription);
      String longDescription = entity.getLongDescription().asXml();
      representation.setLongDescription(longDescription);
      String thumbnailUrl = entity.getThumbnailUrl();
      representation.setThumbnailUrl(RepresentationHelper.modifyAssetImageUrl(thumbnailUrl, getContentRepositoryResource().getEntity()));
      representation.setCategory(entity.getCategory());
      representation.setStore((new Store(entity.getContext())));
      representation.setOfferPrice(entity.getOfferPrice());
      representation.setListPrice(entity.getListPrice());
      if(entity.getCurrency() != null && entity.getLocale() != null) {
        representation.setCurrency(entity.getCurrency().getSymbol(entity.getLocale()));
      }
      representation.setVariants(entity.getVariants());
      representation.setVisuals(entity.getVisuals());
      representation.setPictures(entity.getPictures());
      representation.setDownloads(entity.getDownloads());
      representation.setDescribingAttributes(entity.getDescribingAttributes());
    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected Product doGetEntity() {
    CommerceConnection commerceConnection = getConnection();
    String productId = commerceConnection.getIdProvider().formatProductId(getId());
    return commerceConnection.getCatalogService().findProductById(productId);
  }

}
