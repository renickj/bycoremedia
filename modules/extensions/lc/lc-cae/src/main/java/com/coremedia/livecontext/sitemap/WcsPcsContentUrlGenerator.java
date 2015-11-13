package com.coremedia.livecontext.sitemap;

import com.coremedia.blueprint.cae.sitemap.ContentUrlGenerator;
import com.coremedia.blueprint.cae.sitemap.UrlCollector;
import com.coremedia.cap.multisite.Site;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Url generator for Perfect Chef. This generator considers the <code>secure</code>
 * request attribute and builds https links.
 */
public class WcsPcsContentUrlGenerator extends ContentUrlGenerator {

  @Override
  public void generateUrls(HttpServletRequest request, HttpServletResponse response, Site site, boolean absoluteUrls, String protocol, UrlCollector sitemapRenderer) {
    String secureParameter = request.getParameter(SECURE_PARAM_NAME);
    super.generateUrls(request, response, site, absoluteUrls,
            Boolean.parseBoolean(secureParameter) ? "https" : "http", sitemapRenderer);
  }
}
