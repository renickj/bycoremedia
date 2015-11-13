package com.coremedia.blueprint.cae.web;

import com.coremedia.blueprint.cae.exception.InvalidContentException;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.objectserver.web.HandlerHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public class ContentValidityInterceptor extends HandlerInterceptorAdapter {

  private static final Log LOG = LogFactory.getLog(ContentValidityInterceptor.class);

  private ValidationService<Object> validationService;

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    if (modelAndView != null) {
      Object self = HandlerHelper.getRootModel(modelAndView);
      if ((self == null) || !(self instanceof Page)) {
        return;
      }

      Page page = (Page) self;

      boolean contentValidity = validationService.validate(page.getContent());
      boolean pageValidity = contentValidity && validationService.validate(page.getNavigation());

      if (!pageValidity) {
        final String msg = "Trying to render invalid content, returning " + SC_NOT_FOUND + ".  Page=" + page;
        LOG.debug(msg);
        throw new InvalidContentException(msg, page);
      }
    }
  }

  @Required
  public void setValidationService(ValidationService<Object> validationService) {
    this.validationService = validationService;
  }
}
