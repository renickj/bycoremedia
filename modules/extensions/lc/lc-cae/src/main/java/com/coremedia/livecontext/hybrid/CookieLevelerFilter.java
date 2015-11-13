package com.coremedia.livecontext.hybrid;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sets the domain for all cookies that are added to the
 * {@link #doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain) given response }
 * to a fixed value which can be {@link #setCookieDomain(String) configured}.
 *
 * It therefor wraps the given HttpServletResponse that will be handed down the filter chain into a
 * custom class that overrides the {@link javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie) add cookie method}
 *
 * This filter must run before any other code may want to add cookies to the response. Furthermore this filter does not
 * handle container managed cookies like the session cookie for example!
 */
public class CookieLevelerFilter implements Filter {
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (response instanceof HttpServletResponse && request instanceof HttpServletRequest) {
      chain.doFilter(request, new HttpServletResponseCookieAware((HttpServletResponse)response));
    }
    else {
      chain.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {
  }

  private class HttpServletResponseCookieAware extends SaveContextOnUpdateOrErrorResponseWrapper {
    public HttpServletResponseCookieAware(HttpServletResponse response) {
      super(response, false);
    }

    @Override
    protected void saveContext(SecurityContext context) {
    }

    @Override
    public void addCookie(Cookie cookie) {
      cookie.setDomain(cookieDomain);
      super.addCookie(cookie);
    }

  }

  @Required
  public void setCookieDomain(String cookieDomain) {
    this.cookieDomain = cookieDomain;
  }

  private String cookieDomain;
}
