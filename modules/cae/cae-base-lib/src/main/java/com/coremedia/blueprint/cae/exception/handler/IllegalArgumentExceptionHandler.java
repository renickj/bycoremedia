package com.coremedia.blueprint.cae.exception.handler;

import com.coremedia.objectserver.web.HttpError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Converts {@link IllegalArgumentException IllegalArgumentExceptions} into a valid {@link HttpError}
 */
public class IllegalArgumentExceptionHandler extends AbstractErrorAndExceptionHandler<IllegalArgumentException, HttpError> {

  private static final Logger LOG = LoggerFactory.getLogger(IllegalArgumentExceptionHandler.class);

  @Override
  public HttpError resolveSelf(IllegalArgumentException exception) {
    return new HttpError(getStatusCode());
  }

  @Override
  public IllegalArgumentException resolveException(Exception exception) {
    if (exception instanceof IllegalArgumentException) {
      return (IllegalArgumentException) exception;
    } else {
      return null;
    }
  }

  @Override
  public void handleExceptionInternal(IllegalArgumentException exception, ModelAndView modelAndView, String viewName, HttpServletRequest request) {
    LOG.debug("Caught Exception: {} for {} with view {}", new Object[]{exception, modelAndView, viewName});
  }

  @Override
  public int getStatusCode() {
    return HttpServletResponse.SC_NOT_FOUND;
  }
}
