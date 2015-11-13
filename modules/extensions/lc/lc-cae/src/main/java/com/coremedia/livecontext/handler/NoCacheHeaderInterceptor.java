package com.coremedia.livecontext.handler;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor for setting all cache control headers to "Do not cache this resource. We mean it."
 */
public class NoCacheHeaderInterceptor extends HandlerInterceptorAdapter {

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    response.setHeader("Pragma", "no-cache"); //NOSONAR
    response.setHeader("Expires", "0"); //NOSONAR
    response.setHeader("Cache-Control", "no-cache,no-store,max-age=0"); //NOSONAR
    response.setHeader("Edge-Control", "no-store"); //NOSONAR
  }
}
