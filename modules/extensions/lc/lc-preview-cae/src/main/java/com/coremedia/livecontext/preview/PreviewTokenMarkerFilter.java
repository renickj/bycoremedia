package com.coremedia.livecontext.preview;

import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Add request attribute "hasPreviewToken" for the request with query parameter "previewToken"
 */
public class PreviewTokenMarkerFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    if (request.getParameterMap().containsKey(PreviewTokenAppendingLinkTransformer.QUERY_PARAMETER_PREVIEW_TOKEN) || isPreviewFragmentRequest(request)) {
      request.setAttribute(LiveContextPageHandlerBase.HAS_PREVIEW_TOKEN, true);
    }
    chain.doFilter(request, response);
  }

  private boolean isPreviewFragmentRequest(ServletRequest request) {
    return (request instanceof HttpServletRequest) && "true".equals(((HttpServletRequest)request).getHeader("wc.p13n_test"));
  }

  @Override
  public void destroy() {
  }

}
