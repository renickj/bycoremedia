package com.coremedia.blueprint.cae.exception.handler;

import com.coremedia.objectserver.web.HttpError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Maps {@link AccessDeniedException} to {@link HttpError} with status code
 * {@link javax.servlet.http.HttpServletResponse#SC_FORBIDDEN}
 */
public class AccessDeniedExceptionHandler extends AbstractErrorAndExceptionHandler<AccessDeniedException, HttpError> {
  private static final Logger LOG = LoggerFactory.getLogger(AccessDeniedExceptionHandler.class);

  @Override
  public HttpError resolveSelf(AccessDeniedException exception) {
    return new HttpError(getStatusCode());
  }

  @Override
  public AccessDeniedException resolveException(Exception exception) {
    if (exception instanceof AccessDeniedException) {
      return (AccessDeniedException) exception;
    }
    // cannot handle other types
    return null;
  }

  @Override
  public void handleExceptionInternal(AccessDeniedException exception, ModelAndView modelAndView, String viewName, HttpServletRequest request) {
    LOG.debug("Access denied to '{}' ({}): {}",
      new Object[] { request.getRequestURI(), request.getMethod(), exception.getMessage() }, exception);
  }

  @Override
  public int getStatusCode() {
    return HttpServletResponse.SC_FORBIDDEN;
  }
}
