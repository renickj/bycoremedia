package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.ecommerce.cae.CommerceContextInterceptorHooks;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.fragment.CompositeNameLinkPostProcessor;
import com.coremedia.livecontext.navigation.CompositeNameHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * LiveContext specific store context issues.
 */
class LivecontextCommerceContextInterceptorHooks implements CommerceContextInterceptorHooks {

  @Override
  public void updateStoreContext(HttpServletRequest request) {
  }

  @Override
  public void enhanceStoreContext(Site site, HttpServletRequest request) {
    String compositeName = request.getParameter(CompositeNameLinkPostProcessor.QUERY_PARAMETER_COMPOSITE_NAME);
    CompositeNameHelper.setCurrentCompositeName(compositeName);
  }
}
