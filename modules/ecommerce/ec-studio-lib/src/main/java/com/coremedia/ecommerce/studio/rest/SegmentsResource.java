package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.ecommerce.studio.rest.model.Segments;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The resource handles the top level store node "Segments".
 * It is not showed in the catalog tree but used to invalidate the list of available commerce segments
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/segments/{siteId:[^/]+}/{workspaceId:[^/]+}")
public class SegmentsResource extends AbstractCatalogResource<Segments> {

  @Override
  public SegmentsRepresentation getRepresentation() {
    SegmentsRepresentation representation = new SegmentsRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(SegmentsRepresentation representation) {
    try {
      Segments segments = getEntity();

      if (segments == null) {
        LOG.error("Error loading segments bean");
        throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load segments bean");
      }

      representation.setId(segments.getId());
      if (getConnection().getSegmentService() != null) {
        representation.setSegments(getConnection().getSegmentService().findAllSegments());
      }

    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected Segments doGetEntity() {
    return new Segments(getStoreContext());
  }

  @Override
  public void setEntity(Segments segments) {
    setSiteId(segments.getContext().getSiteId());
    setWorkspaceId(segments.getContext().getWorkspaceId());
  }
}
