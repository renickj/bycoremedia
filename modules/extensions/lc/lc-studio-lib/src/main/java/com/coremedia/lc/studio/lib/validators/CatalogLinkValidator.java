package com.coremedia.lc.studio.lib.validators;

import com.coremedia.cap.content.Content;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;
import com.coremedia.rest.cap.validation.ContentTypeValidatorBase;
import com.coremedia.rest.validation.Issues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.List;

import static com.coremedia.rest.validation.Severity.WARN;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Checks if catalog object can be loaded from catalog link property.
 * see also CatalogLink.as and CatalogLinkPropertyField.as
 */
public abstract class CatalogLinkValidator extends ContentTypeValidatorBase implements InitializingBean {
  private static final Logger LOG = LoggerFactory.getLogger(CatalogLinkValidator.class);
  public static final String CODE_ISSUE_CATALOG_ERROR = "catalogError";

  private String propertyName;

  private CommerceConnectionInitializer commerceConnectionInitializer;

  protected abstract void emptyPropertyValue(Issues issues);
  protected abstract void invalidStoreContext(Issues issues, Object... arguments);
  protected abstract void storeContextNotFound(Issues issues, Object... arguments);
  protected abstract void invalidExternalId(Issues issues, Object... arguments);
  protected abstract void validOnlyInWorkspace(Issues issues, Object... arguments);

  @Override
  public void validate(Content content, Issues issues) {
    if (content != null && content.isInProduction()) {
      String propertyValue = content.getString(propertyName);
      if (isBlank(propertyValue)) {
        emptyPropertyValue(issues);
      } else {
        initConnection(content);
        if (Commerce.getCurrentConnection() == null) {
          LOG.debug("StoreContext not found for content: " + content.getPath());
          storeContextNotFound(issues, propertyValue);
          return;
        }

        String externalId = Commerce.getCurrentConnection().getIdProvider().parseExternalIdFromId(propertyValue);
        CommerceBean commerceBean;
        StoreContext currentContext = null;
        try {
          //clear the workspace id before validating
          currentContext = Commerce.getCurrentConnection().getStoreContext();
          if (currentContext == null) {
            LOG.debug("StoreContext not found for content: " + content.getPath());
            storeContextNotFound(issues, propertyValue);
            return;
          }
          currentContext.setWorkspaceId(null);
          commerceBean = loadOrReturnNull(propertyValue, currentContext);

          if (commerceBean != null) {
            // catalog bean is found in the main catalog
            return;
          }

          //commerce bean still not found. search it in each workspace
          //is workspace available
          List<Workspace> allWorkspaces = Collections.emptyList();
          WorkspaceService workspaceService = Commerce.getCurrentConnection().getWorkspaceService();
          if (workspaceService != null){
            allWorkspaces = workspaceService.findAllWorkspaces();
          }

          for (Workspace workspace : allWorkspaces) {
            currentContext.setWorkspaceId(workspace.getExternalTechId());
            commerceBean = loadOrReturnNull(propertyValue, currentContext);
            if (commerceBean != null) {
              validOnlyInWorkspace(issues, externalId, currentContext.getStoreName(), workspace.getName());
              return;
            }
          }

          //commerce bean not found even in workspaces
          LOG.debug("id: " + propertyValue + " not found in the store " + currentContext.getStoreName());
          invalidExternalId(issues, externalId, currentContext.getStoreName());

        } catch (InvalidContextException e) {
          LOG.debug("StoreContext not found for content: " + content.getPath(), e);
          invalidStoreContext(issues, propertyValue);
        } catch (InvalidIdException e) {
          LOG.debug("Invalid catalog id: " + propertyValue, e);
          invalidExternalId(issues, propertyValue, currentContext != null ? currentContext.getStoreName() : "null");
        } catch (CommerceException e) {
          LOG.debug("Catalog could not be accessed: " + propertyValue, e);
          issues.addIssue(WARN, propertyName, CODE_ISSUE_CATALOG_ERROR, propertyValue);
        }
      }
    }
  }

  private CommerceBean loadOrReturnNull(String id, StoreContext storeContext) {
    CommerceBean result = null;
    try {
      result = Commerce.getCurrentConnection().getCommerceBeanFactory().loadBeanFor(id, storeContext);
    } catch (NotFoundException e) {
      LOG.trace("Exception creating commerce bean for {} with store context {}: {}", id, storeContext, e.getMessage());
    }
    return result;
  }

  protected void initConnection(Content content) {
    commerceConnectionInitializer.init(content);
  }

  @Required
  public void setPropertyName(String propertyName) {
    this.propertyName = propertyName;
  }

  protected String getPropertyName() {
    return propertyName;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }

}
