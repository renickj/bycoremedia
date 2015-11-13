package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.rest.linking.EntityResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;

/**
 * An abstract catalog object as a RESTful resource.
 */
public abstract class AbstractCatalogResource<Entity extends CommerceObject> implements EntityResource<Entity> {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractCatalogResource.class);
  private static final String ID = "id";
  private static final String SITE_ID = "siteId";
  private static final String WORKSPACE_ID = "workspaceId";

  private CommerceConnectionInitializer commerceConnectionInitializer;

  private String id;
  private String siteId;
  private String workspaceId = StoreContextBuilder.NO_WS_MARKER;

  public String getId() {
    return id;
  }

  @GET
  public AbstractCatalogRepresentation get() {
    return getRepresentation();
  }

  protected abstract AbstractCatalogRepresentation getRepresentation();

  @PathParam(ID)
  public void setId(final String id) {
    this.id = id;
  }

  @PathParam(SITE_ID)
  public void setSiteId(String siteId) {
    this.siteId = siteId;
  }

  @PathParam(WORKSPACE_ID)
  public void setWorkspaceId(String workspaceId) {
    this.workspaceId = workspaceId == null ? StoreContextBuilder.NO_WS_MARKER : workspaceId;
  }

  public String getWorkspaceId() {
    return workspaceId;
  }

  public String getSiteId() {
    return siteId;
  }

  protected StoreContext getStoreContext() {
    CommerceConnection connection = getConnection();
    if (connection != null) {
      StoreContext storeContext = connection.getStoreContext();
      if (storeContext != null) {
        storeContext.setWorkspaceId(workspaceId);
        return storeContext;
      }
    }
    return null;
  }

  protected CommerceConnection getConnection() {
    commerceConnectionInitializer.init(getSiteId());
    return Commerce.getCurrentConnection();
  }

  protected UserContext getUserContext() {
    return getConnection().getUserContext();
  }

  protected String getExternalIdFromId(String remoteBeanId) {
    //we assume that the substring after the last '/' is the external id
    return remoteBeanId.substring(remoteBeanId.lastIndexOf('/') + 1);
  }

  public MarketingSpotService getMarketingSpotService() {
    return getConnection().getMarketingSpotService();
  }

  @Override
  public Entity getEntity() {
    if (getStoreContext() == null) {
      return null;
    } else {
      return doGetEntity();
    }
  }
  abstract protected Entity doGetEntity();

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }
}
