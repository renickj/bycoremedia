package com.coremedia.blueprint.common.services.context;

import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cotopaxi.common.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class CurrentContextServiceImpl implements CurrentContextService {
  private static final Logger LOG = LoggerFactory.getLogger(CurrentContextServiceImpl.class);

  /**
   * {@inheritDoc}
   * <br/>
   * This implementation evaluates a well known attribute of the thread-local request.<br/>
   * <br/>
   * <i>Sets an uncachable dependency so that a {@link com.coremedia.cache.CacheKey CacheKey} or a
   * {@link com.coremedia.objectserver.dataviews.DataView DataView} that uses this method is invalidated instantly.</i>
   */
  @Override
  public CMContext getContext() {

    CacheUtil.uncacheable(); // ensure that this method is not cached

    ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if(sra != null) {
      final Navigation navigation = NavigationLinkSupport.getNavigation(sra.getRequest());
      if (navigation == null) {
        LOG.debug("Navigation context not found in request: {}", sra.getRequest().getRequestURL());
        return null;
      }
      CMContext context = navigation.getContext();
      if (context == null) {
        LOG.warn("navigation.getContext() returned null, navigation is: {}", navigation);
      }
      return context;
    }

    return null;
  }
}
