package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.common.CommerceException;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A store {@link com.coremedia.ecommerce.studio.rest.model.Store} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/store/{siteId:[^/]+}/{workspaceId:[^/]+}")
public class StoreResource extends AbstractCatalogResource<Store> {

  @Override
  public StoreRepresentation getRepresentation() {
    StoreRepresentation storeRepresentation = new StoreRepresentation();
    fillRepresentation(storeRepresentation);
    return storeRepresentation;
  }

  private void fillRepresentation(StoreRepresentation representation) {
    try {
      Store entity = getEntity();

      if (entity == null) {
        LOG.warn("Error loading store bean");
        throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load store bean");
      }

      representation.setMarketingEnabled(getConnection().getMarketingSpotService() != null &&
              !getConnection().getMarketingSpotService().findMarketingSpots().isEmpty());
      representation.setId(entity.getId());
      representation.setVendorUrl(entity.getVendorUrl());
      representation.setVendorName(entity.getVendorName());
      representation.setVendorVersion(entity.getVendorVersion());
      representation.setContext(getStoreContext());

    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected Store doGetEntity() {
    return new Store(getStoreContext());
  }

  @Override
  public void setEntity(Store store) {
    setSiteId(store.getContext().getSiteId());
    setWorkspaceId(store.getContext().getWorkspaceId());
  }
}
