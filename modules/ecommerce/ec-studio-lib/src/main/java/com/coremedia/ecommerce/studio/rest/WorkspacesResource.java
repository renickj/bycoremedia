package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.ecommerce.studio.rest.model.Workspaces;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

/**
 * The resource handles the top level store node "Segments".
 * It is not showed in the catalog tree but used to invalidate the list of available commerce segments
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/workspaces/{siteId:[^/]+}")
public class WorkspacesResource extends AbstractCatalogResource<Workspaces> {

  @Override
  public WorkspacesRepresentation getRepresentation() {
    WorkspacesRepresentation representation = new WorkspacesRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(WorkspacesRepresentation representation) {
    try {
      Workspaces workspaces = getEntity();

      if (workspaces == null) {
        LOG.error("Error loading workspaces bean");
        throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load workspaces bean");
      }

      representation.setId(workspaces.getId());
      if (getConnection().getWorkspaceService() != null) {
        representation.setWorkspaces(getConnection().getWorkspaceService().findAllWorkspaces());
      }

    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected Workspaces doGetEntity() {
    return new Workspaces(getStoreContext());
  }

  @Override
  public void setEntity(Workspaces workspaces) {
    setSiteId(workspaces.getContext().getSiteId());
  }
}
