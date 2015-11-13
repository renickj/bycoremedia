package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.ecommerce.studio.rest.model.Marketing;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The resource is just used for handling the top level store node "Marketing Spots".
 * It is not necessary for the commerce API. This ensures a unified handling
 * of tree nodes in the Studio library window.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/marketing/{siteId:[^/]+}/{workspaceId:[^/]+}")
public class MarketingResource extends AbstractCatalogResource<Marketing> {

  @Override
  public MarketingRepresentation getRepresentation() {
    MarketingRepresentation representation = new MarketingRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(MarketingRepresentation representation) {
    try {
      Marketing entity = getEntity();

      if (entity == null) {
        LOG.error("Error loading marketing bean");
        throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load marketing bean");
      }

      representation.setId(entity.getId());
      if (getConnection().getMarketingSpotService() != null) {
        representation.setMarketingSpots(getConnection().getMarketingSpotService().findMarketingSpots());
      }

    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected Marketing doGetEntity() {
    return new Marketing(getStoreContext());
  }

  @Override
  public void setEntity(Marketing marketing) {
    setSiteId(marketing.getContext().getSiteId());
    setWorkspaceId(marketing.getContext().getWorkspaceId());
  }
}
