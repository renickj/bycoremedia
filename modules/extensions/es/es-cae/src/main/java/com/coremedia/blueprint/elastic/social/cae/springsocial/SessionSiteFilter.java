package com.coremedia.blueprint.elastic.social.cae.springsocial;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.cap.multisite.Site;

import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * A filter that copies the current site from the request to the user's session (if available)
 */
@Named
public class SessionSiteFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    // nothing to do here
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request instanceof HttpServletRequest) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      final Site siteFromRequest = SiteHelper.getSiteFromRequest(request);
      if(null != siteFromRequest) {
        final HttpSession session = httpServletRequest.getSession(false);
        if(null != session) {
          session.setAttribute(SiteHelper.SITE_KEY, siteFromRequest.getId());
        }
      }
    }
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    // nothing to do here
  }
}