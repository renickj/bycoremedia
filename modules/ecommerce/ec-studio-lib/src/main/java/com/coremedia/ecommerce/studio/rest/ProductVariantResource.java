package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.rest.cap.content.ContentRepositoryResource;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper.getCurrentCommerceIdProvider;

/**
 * A catalog {@link ProductVariant} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/sku/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:[^/]+}")
public class ProductVariantResource extends CommerceBeanResource<ProductVariant> {

  @Inject
  private ContentRepositoryResource contentRepositoryResource;

  @Override
  public ProductVariantRepresentation getRepresentation() {
    ProductVariantRepresentation productVariantRepresentation = new ProductVariantRepresentation();
    fillRepresentation(productVariantRepresentation);
    return productVariantRepresentation;
  }

  private void fillRepresentation(ProductVariantRepresentation representation) {
    try {
      super.fillRepresentation(representation);
      ProductVariant entity = getEntity();
      if (entity == null) {
        throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load sku bean");
      }
      representation.setId(entity.getId());
      representation.setName(entity.getName());
      representation.setExternalId(entity.getExternalId());
      representation.setExternalTechId(entity.getExternalTechId());
      String shortDescription = entity.getShortDescription().asXml();
      representation.setShortDescription(shortDescription);
      String longDescription = entity.getLongDescription().asXml();
      representation.setLongDescription(longDescription);
      String thumbnailUrl = entity.getThumbnailUrl();
      representation.setThumbnailUrl(RepresentationHelper.modifyAssetImageUrl(thumbnailUrl, contentRepositoryResource.getEntity()));
      representation.setParent(entity.getParent());
      representation.setCategory(entity.getCategory());
      representation.setStore((new Store(entity.getContext())));
      representation.setOfferPrice(entity.getOfferPrice());
      representation.setListPrice(entity.getListPrice());
      representation.setCurrency(entity.getCurrency().getSymbol(entity.getLocale()));
      representation.setVisuals(entity.getVisuals());
      representation.setPictures(entity.getPictures());
      representation.setDownloads(entity.getDownloads());
      representation.setDefiningAttributes(entity.getDefiningAttributes());
      representation.setDescribingAttributes(entity.getDescribingAttributes());
    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected ProductVariant doGetEntity() {
    return getConnection().getCatalogService().findProductVariantById(getCurrentCommerceIdProvider().formatProductVariantId(getId()));
  }

  @Override
  public void setEntity(ProductVariant productVariant) {
    setId(productVariant.getExternalId());
    setSiteId(productVariant.getContext().getSiteId());
    setWorkspaceId(productVariant.getContext().getWorkspaceId());
  }
}
