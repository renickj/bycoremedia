package com.coremedia.blueprint.cae.constants;

import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.objectserver.beans.ContentBean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

public final class RequestAttributeConstants {

  public static final String ATTR_NAME_PAGE_SITE = "cmpage_site";
  public static final String ATTR_NAME_PAGE_MODEL = "cmpage_model";

  private static final String ATTR_NAME_PAGE = ContextHelper.ATTR_NAME_PAGE;


  /**
   * Hide Utility Class Constructor
   */
  private RequestAttributeConstants() {
  }

  public static void setPage(ModelAndView modelAndView, Page page) {
    modelAndView.addObject(ATTR_NAME_PAGE, page);
  }

  public static void setPageModel(ContentBean bean) {
    ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    sra.setAttribute(ATTR_NAME_PAGE_MODEL, bean, RequestAttributes.SCOPE_REQUEST);
  }

  public static Page getPage(HttpServletRequest request) {
    return (Page) request.getAttribute(ATTR_NAME_PAGE);
  }

  public static Page getPage(ModelAndView modelAndView) {
    return (Page) modelAndView.getModel().get(ATTR_NAME_PAGE);
  }
}
