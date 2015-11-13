package com.coremedia.livecontext.sitemap;

import com.coremedia.blueprint.cae.sitemap.SitemapRenderer;
import com.coremedia.blueprint.cae.sitemap.SitemapRendererFactory;

public class SitemapWcsPcsRendererFactory implements SitemapRendererFactory {
  @Override
  public SitemapRenderer createInstance() {
    return new WcsPcsCrawlSitemapRenderer();
  }

  @Override
  public String getContentType() {
    return "text/html";
  }

  @Override
  public boolean absoluteUrls() {
    return true;
  }
}
