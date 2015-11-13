package com.coremedia.ecommerce.studio.rest;

import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.multisite.impl.SitesServiceImpl;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.ecommerce.studio.rest.model.Catalog;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The resource is just used for handling the top level store node "Product Catalog".
 * It is not necessary for the commerce API. This ensures a unified handling
 * of tree nodes in the Studio library window.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext/catalog/{siteId:[^/]+}/{workspaceId:[^/]+}")
public class CatalogResource extends AbstractCatalogResource<Catalog> {

  private SitesService sitesService;

  @Override
  public CatalogRepresentation getRepresentation() {
    CatalogRepresentation representation = new CatalogRepresentation();
    fillRepresentation(representation);
    return representation;
  }

  private void fillRepresentation(CatalogRepresentation representation) {
    try {
      Catalog entity = getEntity();

      if (entity == null) {
        LOG.error("Error loading catalog bean");
        throw new CatalogRestException(Response.Status.NOT_FOUND, CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN, "Could not load catalog bean");
      }

      representation.setStore((new Store(entity.getContext())));
      representation.setId(entity.getId());
      Site site = sitesService.getSite(entity.getContext().getSiteId());
      representation.setTopCategories(getConnection().getCatalogService().findTopCategories(site));

    } catch (CommerceException ex) {
      CommerceStudioErrorHandler.handleCommerceException(ex);
    }
  }

  @Override
  protected Catalog doGetEntity() {
    return new Catalog(getStoreContext());
  }

  @Override
  public void setEntity(Catalog catalog) {
    setSiteId(catalog.getContext().getSiteId());
    setWorkspaceId(catalog.getContext().getWorkspaceId());
  }

  @Required
  public void setSitesService(SitesServiceImpl sitesService) {
    this.sitesService = sitesService;
  }
}
