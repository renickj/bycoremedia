package com.coremedia.livecontext.sitemap;

import com.coremedia.blueprint.cae.sitemap.SitemapGenerationController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_INTERNAL;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ROOT;

@RequestMapping
public class WcsPcsSitemapGenerationHandler extends SitemapGenerationController {
  private static final String WCS_PCS = "index-wcs-pcs";

  public static final String URI_PATTERN =
          '/' + PREFIX_INTERNAL +
          "/{" + SEGMENT_ROOT + '}' +
          '/' + WCS_PCS;

  @RequestMapping(URI_PATTERN)
  public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
    return handleRequestInternal(request, response);
  }

}