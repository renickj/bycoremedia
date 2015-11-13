package com.coremedia.livecontext.ecommerce.ibm.workspace;

import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.workspace.Workspace;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceService;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class WorkspaceServiceImpl implements WorkspaceService {

  private WcWorkspaceWrapperService workspaceWrapperService;
  private CommerceBeanFactory commerceBeanFactory;
  private CommerceCache commerceCache;

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Required
  public void setWorkspaceWrapperService(WcWorkspaceWrapperService workspaceWrapperService) {
    this.workspaceWrapperService = workspaceWrapperService;
  }

  @Nonnull
  @Override
  public List<Workspace> findAllWorkspaces() throws CommerceException {
    StoreContext currentStoreContext = StoreContextHelper.getCurrentContext();
    Map map = (Map) commerceCache.get(new WorkspacesCacheKey(currentStoreContext,
            UserContextHelper.getCurrentContext(), workspaceWrapperService, commerceCache));
    if (map != null) {
      Object workspacesObj = map.get("workspaces");
      if (workspacesObj instanceof List) {
        return createWorkspaceBeansFor((List<Map>) workspacesObj, currentStoreContext);
      }
    }
    return Collections.emptyList();
  }

  @Nullable
  @Override
  public Workspace findWorkspaceByExternalTechId(@Nonnull String externalId) throws CommerceException {
    List<Workspace> allWorkspaces = findAllWorkspaces();
    for (Workspace workspace : allWorkspaces) {
      if (externalId.equals(workspace.getExternalTechId())) {
        return workspace;
      }
    }
    return null;
  }

  protected Workspace createWorkspaceBeanFor(Map map, StoreContext context) {
    if (map != null) {
      String id = CommerceIdHelper.formatWorkspaceId((String) map.get("id"));
      if (CommerceIdHelper.isWorkspaceId(id)) {
        Workspace workspace = (Workspace) commerceBeanFactory.createBeanFor(id, context);
        ((AbstractIbmCommerceBean) workspace).setDelegate(map);
        return workspace;
      }
    }
    return null;
  }

  protected List<Workspace> createWorkspaceBeansFor(List<Map> list, StoreContext context) {
    if (list == null || list.isEmpty()) {
      return Collections.emptyList();
    }
    List<Workspace> result = new ArrayList<>(list.size());
    for (Map workspaceMap : list) {
      Workspace workspace = createWorkspaceBeanFor(workspaceMap, context);
      if (workspace != null) {
        result.add(createWorkspaceBeanFor(workspaceMap, context));
      }
    }
    return Collections.unmodifiableList(result);
  }

  @Nonnull
  @Override
  public WorkspaceService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, WorkspaceService.class);
  }
}
