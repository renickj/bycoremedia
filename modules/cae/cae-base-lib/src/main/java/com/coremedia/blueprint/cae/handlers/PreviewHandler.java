package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.objectserver.web.IdRedirectHandlerBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * A handler used for preview purposes: Takes a "id" request parameter and redirect to the resource that is denoted
 * by this id.
 */
@RequestMapping
public class PreviewHandler extends IdRedirectHandlerBase {

  /**
   * Uri pattern for preview URLs.
   * e.g. /preview?id=123&view=fragmentPreview
   */
  public static final String URI_PATTERN = "/preview";
  public static final String REQUEST_ATTR_IS_STUDIO_PREVIEW = "isStudioPreview";

  // --- Handler ----------------------------------------------------

  @RequestMapping(value = URI_PATTERN)
  public ModelAndView handleId(@RequestParam(value = "id", required = true) String id,
                               @RequestParam(value = "view", required = false) String view,
                               @RequestParam(value = "site", required = false) String siteId,
                               @RequestParam(value = "taxonomyId", required = false) String taxonomyId,
                               HttpServletRequest request) {
    request.setAttribute(REQUEST_ATTR_IS_STUDIO_PREVIEW, true);
    storeSite(request, siteId);
    storeTaxonomy(request, taxonomyId);
    return super.handleId(id, view);
  }

  public static boolean isStudioPreviewRequest(){
    return Boolean.valueOf(RequestContextHolder.getRequestAttributes().getAttribute(
                  PreviewHandler.REQUEST_ATTR_IS_STUDIO_PREVIEW, 0)+"");
  }

  @Override
  protected boolean isPermitted(Object o, String s) {
    return true;
  }

  /**
   * Stores the site parameter into the request.
   * The site parameter is used to resolve the context of a content if it does not belong to a specific site.
   * Therefore the studio default site will be passed as parameter to resolve the context.
   * @param siteId The id of the site
   */
  private void storeSite(HttpServletRequest request, String siteId) {
    if (StringUtils.isNotEmpty(siteId)) {
      request.getSession(true).setAttribute(RequestAttributeConstants.ATTR_NAME_PAGE_SITE, siteId);
    }
  }

  /**
   * Stores the taxonomy content into the request.
   * The taxonomy parameter is used for the custom topic pages.
   * @param taxonomyId The numeric content id of the taxonomy node.
   */
  private void storeTaxonomy(HttpServletRequest request, String taxonomyId) {
    if (StringUtils.isNotEmpty(taxonomyId)) {
      String id = IdHelper.formatContentId(taxonomyId);
      request.getSession(true).setAttribute(RequestAttributeConstants.ATTR_NAME_PAGE_MODEL, id);
    }
  }
}
