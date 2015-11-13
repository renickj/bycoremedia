package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a human readable sitemap for orientation in the website.
 * <p>
 * Not to be confused with sitemap.org compliant sitemaps for search engines.
 *
 * <p>Represents the document type {@link #NAME CMSitemap}.</p>
 */
public interface CMSitemap extends CMTeasable {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMSitemap'.
   */
  String NAME = "CMSitemap";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMSitemap} object
   */
  @Override
  CMSitemap getMaster();

  @Override
  Map<Locale, ? extends CMSitemap> getVariantsByLocale();

  @Override
  Collection<? extends CMSitemap> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMSitemap>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMSitemap>> getAspects();

  /**
   * Name of the document property 'target'.
   */
  String ROOT = "root";

  /**
   * Returns the value of the document property {@link #ROOT}.
   *
   * @return a {@link CMLinkable} object
   */
  CMNavigation getRoot();
}
