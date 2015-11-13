package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST resource for accessing configuration values.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("configurations")
public class ConfigurationServiceResource {
  private static final String PARAM_SITE = "site";

  private ConfigurationService configurationService;
  private SitesService sitesService;

  //--- Spring configuration --
  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setConfigurationService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  // -- Impl. ----------

  @GET
  @Path("rawsitepath")
  public PathConfigurationRepresentation getSiteConfigFolder() {
    PathConfigurationRepresentation rep = new PathConfigurationRepresentation();
    rep.setGlobalPath(configurationService.getGlobalConfigFolder());
    return rep;
  }

  @GET
  @Path("rawsitepath/{"+PARAM_SITE+"}")
  public PathConfigurationRepresentation getSiteConfigFolder(@PathParam(PARAM_SITE) String siteId) {
    String siteConfigFolder = configurationService.getRawSiteConfigFolder();
    Site site = sitesService.getSite(siteId);
    if (site == null) {
      throw new WebApplicationException(Response.Status.GONE);
    }
    String sitePath = site.getSiteRootFolder().getChild(siteConfigFolder).getPath();

    PathConfigurationRepresentation rep = new PathConfigurationRepresentation();
    rep.setSite(site.getName());
    rep.setSitePath(sitePath);
    rep.setGlobalPath(configurationService.getGlobalConfigFolder());
    return rep;
  }
}
