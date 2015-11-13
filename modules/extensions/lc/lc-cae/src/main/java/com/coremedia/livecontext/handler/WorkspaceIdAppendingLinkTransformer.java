package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.ParameterAppendingLinkTransformer;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * LinkTransformer implementation that adds the workspaceId request parameter to page links.
 */
public class WorkspaceIdAppendingLinkTransformer implements LinkTransformer {

  private final ParameterAppendingLinkTransformer parameterAppender;

  private boolean preview;

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection() != null ? Commerce.getCurrentConnection().getStoreContextProvider() : null;
  }

  public boolean isPreview() {
    return preview;
  }

  @Value("${cae.is.preview}")
  public void setPreview(boolean preview) {
    this.preview = preview;
  }

  public WorkspaceIdAppendingLinkTransformer() {
    parameterAppender = new ParameterAppendingLinkTransformer();
    parameterAppender.setParameterName(AbstractCommerceContextInterceptor.QUERY_PARAMETER_WORKSPACE_ID);
  }

  @PostConstruct void initialize() throws Exception {
    parameterAppender.afterPropertiesSet();
  }

  @Override
  public String transform(String source, Object bean, String view, HttpServletRequest request, HttpServletResponse response, boolean forRedirect) {
    if (preview) {
      StoreContextProvider storeContextProvider = getStoreContextProvider();
      if (storeContextProvider != null) {
        StoreContext context = storeContextProvider.getCurrentContext();
        if (context != null) {
          String workspaceId = context.getWorkspaceId();
          if (workspaceId != null && !workspaceId.equals(StoreContextBuilder.NO_WS_MARKER)) {
            parameterAppender.setParameterValue(workspaceId);
            return parameterAppender.transform(source, bean, view, request, response, forRedirect);
          }
        }
      }
    }
    return source;
  }

}
