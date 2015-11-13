package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_DYNAMIC;

/**
 * Initializes the StoreContextProvider according to the current request.
 */
public abstract class AbstractCommerceContextInterceptor extends HandlerInterceptorAdapter implements InitializingBean {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractCommerceContextInterceptor.class);

  public static final String QUERY_PARAMETER_WORKSPACE_ID = "workspaceId";

  private static final String DYNAMIC_FRAGMENT = "/" + PREFIX_DYNAMIC + "/";
  private static final String STORE_CONTEXT_INITIALIZED = AbstractCommerceContextInterceptor.class.getName()+"#storeContext.initialized";
  private static final String USER_CONTEXT_INITIALIZED = AbstractCommerceContextInterceptor.class.getName()+"#userContext.initialized";

  private SiteResolver siteResolver;
  private CommerceConnectionInitializer commerceConnectionInitializer;
  private boolean preview;
  private boolean initUserContext = false;

  private List<CommerceContextInterceptorHooks> hooks;


  // --- configure --------------------------------------------------

  /**
   * Default: false
   */
  public void setInitUserContext(boolean initUserContext) {
    this.initUserContext = initUserContext;
  }

  @Value("${cae.is.preview}")
  public void setPreview(boolean preview) {
    this.preview = preview;
  }

  @Required
  public void setSiteResolver(SiteResolver siteResolver) {
    this.siteResolver = siteResolver;
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }

  public void setHooks(List<CommerceContextInterceptorHooks> hooks) {
    this.hooks = hooks;
  }

  @Override
  public void afterPropertiesSet() {
    Objects.requireNonNull(siteResolver);
    Objects.requireNonNull(commerceConnectionInitializer);
    if (hooks==null) {
      hooks = Collections.emptyList();
    }
  }


  // --- HandlerInterceptor -----------------------------------------

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String normalizedPath = normalizePath(request.getPathInfo());
    Site site = getSite(request, normalizedPath);
    // If site is null, we cannot help it here.  Silently do nothing.
    // It is up to the request handler to return 404.
    if (site != null) {
      initStoreContext(site, request);
      if (initUserContext && isCommerceContextAvailable()) {
        initUserContext(request);
      }
    }
    return true;
  }


  // --- abstract ---------------------------------------------------

  /**
   * Calculate a site from the request.
   * <p/>
   *
   * @param request        the request
   * @param normalizedPath is the URL path w/o a dynamic fragment prefix
   * @return a Site or null
   */
  @Nullable
  protected abstract Site getSite(HttpServletRequest request, String normalizedPath);


  // --- hook points and utils for extending classes ----------------

  public SiteResolver getSiteResolver() {
    return siteResolver;
  }

  protected boolean isCommerceContextAvailable() {
    return Commerce.getCurrentConnection() != null && Commerce.getCurrentConnection().getStoreContext() != null;
  }

  protected boolean isPreview() {
    return preview;
  }

  @VisibleForTesting  // assume protected
  public boolean isStoreContextInitialized(HttpServletRequest request) {
    return request.getAttribute(STORE_CONTEXT_INITIALIZED) != null;
  }

  @VisibleForTesting  // assume protected
  public boolean isUserContextInitialized(HttpServletRequest request) {
    return request.getAttribute(USER_CONTEXT_INITIALIZED) != null;
  }


  // --- basics, suitable for most extending classes ----------------

  protected void initStoreContext(Site site, HttpServletRequest request) {
    if (!isStoreContextInitialized(request)) {
      if (site != null) {
        //connection is supposed to be a prototype
        commerceConnectionInitializer.init(site);
        if (isCommerceContextAvailable()) {
          StoreContext storeContext = Commerce.getCurrentConnection().getStoreContext();
          if (preview) {
            // search for an existing workspace param and put it in the store context
            String workspaceId = request.getParameter(QUERY_PARAMETER_WORKSPACE_ID);
            storeContext.setWorkspaceId(workspaceId);
            String previewDate = request.getParameter(ValidityPeriodValidator.REQUEST_PARAMETER_PREVIEW_DATE);
            if (previewDate != null) {
              storeContext.setPreviewDate(previewDate);
            }
          }
          Commerce.getCurrentConnection().setStoreContext(storeContext);
          request.setAttribute(STORE_CONTEXT_INITIALIZED, true);
          enhanceStoreContext(site, request);  // optional hooks
        } else {
          LOG.debug("No commerce connection found for site " + site.getName());
        }
      }
    }
    updateStoreContext(request);  // optional hooks
  }

  /**
   * Sets the user context to the user context provider.
   * You will need this if you want to do a call for a user.
   */
  protected void initUserContext(HttpServletRequest request) {
    if (!isUserContextInitialized(request)) {
      try {
        UserContext userContext = Commerce.getCurrentConnection().getUserContextProvider().createContext(request, null);
        CommerceConnection connection = Commerce.getCurrentConnection();
        if (connection != null) {
          connection.setUserContext(userContext);
          request.setAttribute(USER_CONTEXT_INITIALIZED, true);
        } else {
          LOG.error("Error creating commerce user context. No valid commerce connection found.");
        }
      } catch (CommerceException e) {
        LOG.error("Error creating commerce user context: " + e.getMessage(), e);
      }
    }
  }


  // --- internal ---------------------------------------------------

  private void updateStoreContext(HttpServletRequest request) {
    for (CommerceContextInterceptorHooks hook : hooks) {
      hook.updateStoreContext(request);
    }
  }

  private void enhanceStoreContext(Site site, HttpServletRequest request) {
    for (CommerceContextInterceptorHooks hook : hooks) {
      hook.enhanceStoreContext(site, request);
    }
  }

  /**
   * Cut off a possible dynamic prefix
   */
  @VisibleForTesting
  static String normalizePath(String urlPath) {
    return urlPath != null && urlPath.startsWith(DYNAMIC_FRAGMENT) ? urlPath.substring(DYNAMIC_FRAGMENT.length() - 1) : urlPath;
  }
}
