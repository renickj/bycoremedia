package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.ecommerce.catalog.AbstractCmsCommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.rest.cap.content.ContentRepositoryResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static java.text.MessageFormat.format;

/**
 * An abstract catalog object as a RESTful resource.
 */
public abstract class CommerceBeanResource<Entity extends CommerceBean> extends AbstractCatalogResource<Entity> {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceBeanResource.class);
  private static final String ID_AND_SITE_PARAM = "{0}&site={1}";

  @Inject
  private ContentRepositoryResource contentRepositoryResource;

  protected void fillRepresentation(CommerceBeanRepresentation representation) {
    CommerceBean entity = getEntity();

    if (entity == null) {
      LOG.error("Error loading commerce bean");
      throw new CatalogRestException(Response.Status.NOT_FOUND,
              CatalogRestErrorCodes.COULD_NOT_FIND_CATALOG_BEAN,
              "Could not load commerce bean with id " + getId());
    }

    representation.setId(entity.getId());
    representation.setExternalId(entity.getExternalId());
    representation.setExternalTechId(entity.getExternalTechId());

    //set preview url
    representation.setPreviewUrl(computePreviewUrl());

    if (entity instanceof AbstractCmsCommerceBean) {
      representation.setContent(((AbstractCmsCommerceBean) entity).getContent());
    }
  }

  String computePreviewUrl() {
    String previewControllerUriPattern = getContentRepositoryResource().getPreviewControllerUrlPattern();
    return formatPreviewUrl(previewControllerUriPattern, getEntity().getId(), getSiteId());
  }

  public static String formatPreviewUrl(String previewControllerUriPattern, String id, String siteId) {
    // position 0 is reserved for formatted IDs, position 1 is reserved for numeric content IDs
    // the site param is appended to the formatted ID
    String idAndSiteParam = format(ID_AND_SITE_PARAM, id, siteId);
    return format(previewControllerUriPattern, idAndSiteParam);
  }

  public ContentRepositoryResource getContentRepositoryResource() {
    return contentRepositoryResource;
  }

  @Override
  public void setEntity(Entity entity) {
    setId(entity.getExternalId());
    StoreContext context = entity.getContext();
    setSiteId(context.getSiteId());
    setWorkspaceId(context.getWorkspaceId());
  }
}
