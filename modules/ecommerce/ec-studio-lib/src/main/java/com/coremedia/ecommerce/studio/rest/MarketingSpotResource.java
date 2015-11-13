package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.ecommerce.studio.rest.model.Store;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A catalog {@link com.coremedia.livecontext.ecommerce.p13n.MarketingSpot} object as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/marketingspot/{siteId:[^/]+}/{workspaceId:[^/]+}/{id:[^/]+}")
public class MarketingSpotResource extends AbstractCatalogResource<MarketingSpot> {

  @Override
  public MarketingSpotRepresentation getRepresentation() {
    MarketingSpotRepresentation representation = new MarketingSpotRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(MarketingSpotRepresentation representation) {
    try {
      MarketingSpot entity = getEntity();

      if (entity == null) {
        LOG.error("Error loading spot bean");
        throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load spot bean");
      }

      representation.setId(entity.getId());
      representation.setName(entity.getName());
      representation.setShortDescription(entity.getDescription());
      representation.setExternalId(entity.getExternalId());
      representation.setExternalTechId(entity.getExternalTechId());
      representation.setStore((new Store(entity.getContext())));

    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }


  @Override
  protected MarketingSpot doGetEntity() {
    return getMarketingSpotService().findMarketingSpotByExternalId(getId());
  }

  @Override
  public void setEntity(MarketingSpot spot) {
    if (spot.getId() != null){
      String extId = getExternalIdFromId(spot.getId());
      setId(extId);
    } else {
      setId(spot.getExternalId());
    }
    setSiteId(spot.getContext().getSiteId());
    setWorkspaceId(spot.getContext().getWorkspaceId());
  }

}
