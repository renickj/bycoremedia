package com.coremedia.blueprint.cae.web;

import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.Page;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Adds the current navigation context to the {@link ModelAndView}. Some rendering layer code will expect
 * the navigation as a request attribute. Requires the current page to be already exposed as request
 * attribute RequestAttributeConstants#ATTR_NAME_PAGE.
 *
 * @see NavigationLinkSupport#getNavigation(javax.servlet.ServletRequest)
 */
public class ExposeCurrentNavigationInterceptor extends HandlerInterceptorAdapter {

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                         ModelAndView modelAndView) {
    Page page = RequestAttributeConstants.getPage(request);
    if (modelAndView != null && !modelAndView.wasCleared() && page != null) {
      NavigationLinkSupport.setNavigation(modelAndView, page.getNavigation());
    }
  }
}
