package com.coremedia.blueprint.cae.exception.resolver;

import com.coremedia.blueprint.cae.exception.ExceptionRenderDynamicViewDecorator;
import com.coremedia.blueprint.cae.exception.handler.ErrorAndExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * ExceptionResolver that selects a {@link ErrorAndExceptionHandler} and maps an exception to it.
 */
public class ErrorAndExceptionMappingResolver extends SimpleMappingExceptionResolver {

  private static final Logger LOG = LoggerFactory.getLogger(ErrorAndExceptionMappingResolver.class);

  private List<ErrorAndExceptionHandler> errorAndExceptionHandler;

  @Override
  public ModelAndView resolveException(HttpServletRequest request,
                                       HttpServletResponse response,
                                       Object handler,
                                       Exception ex) {

    String matchingViewName = determineViewName(ex, request);

    return getModelAndView(matchingViewName, ex, request, response);

  }

  @Override
  protected String determineViewName(Exception ex, HttpServletRequest request) {
    if (ex instanceof ExceptionRenderDynamicViewDecorator) {
      ExceptionRenderDynamicViewDecorator exDec = (ExceptionRenderDynamicViewDecorator) ex;
      if (exDec.getView() != null) {
        return exDec.getView();
      }
    }

    return super.determineViewName(ex, request);
  }

  protected ModelAndView getModelAndView(String viewName, Exception ex, HttpServletRequest request, HttpServletResponse response) {
    LOG.info("Exception was thrown, trying to find a handler: ", ex);

    ModelAndView modelAndView = null;
    for (ErrorAndExceptionHandler exhandler : errorAndExceptionHandler) {
      modelAndView = exhandler.handleException(viewName, ex, request, response);
      if (modelAndView != null) {
        LOG.debug("Found handler for this exception: {}", exhandler.toString());
        break;
      }
    }

    if (modelAndView == null) {
      LOG.debug("Could not find a handler for this exception.");
    }
    return modelAndView;
  }

  public void setExceptionHandler(List<ErrorAndExceptionHandler> handlers) {
    this.errorAndExceptionHandler = handlers;
  }


}
