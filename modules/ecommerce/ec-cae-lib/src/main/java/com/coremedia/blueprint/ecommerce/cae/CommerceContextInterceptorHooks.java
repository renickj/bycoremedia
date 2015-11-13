package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.cap.multisite.Site;

import javax.servlet.http.HttpServletRequest;

/**
 * While the general intialization of the Commerce context is independent of
 * the particular implementation, some implementations may have additional
 * requirements.  For such requirements you can attach
 * CommerceContextInterceptorHooks to CommerceContextInterceptors.
 * <p>
 * CommerceContextInterceptorHooks MUST be thread safe, since interceptors
 * are invoked concurrently.
 * <p>
 * CommerceContextInterceptors are responsible for particular URL patterns,
 * but not for particular sites.  If you use different Commerce systems in
 * your sites, the respective CommerceContextInterceptorHooks must be both,
 * <b>robust</b> and <b>noninvasive</b> wrt. to the other Commerce systems
 * and CommerceContextInterceptorHooks.
 * <p>
 * More concrete, CommerceContextInterceptorHooks are not supposed to do
 * more than setting some additional request parameters.
 * <p>
 * CommerceContextInterceptor is NOT to be mistaken as a general aggregator.
 * In terms of separation of concerns, you should rather use independent
 * interceptors, unless your functionality is really tight-knit to
 * commerce context initialization.
 */
public interface CommerceContextInterceptorHooks {
  /**
   * Invoked by {@link AbstractCommerceContextInterceptor#initStoreContext}
   * after successfully initializing the Commerce connection.
   */
  void enhanceStoreContext(Site site, HttpServletRequest request);

  /**
   * Invoked at the end of {@link AbstractCommerceContextInterceptor#initStoreContext}.
   */
  void updateStoreContext(HttpServletRequest request);
}
