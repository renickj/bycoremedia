package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriod;
import com.coremedia.blueprint.common.layout.PageGrid;
import com.coremedia.cae.aspect.Aspect;

import java.util.Locale;
import java.util.Map;

/**
 * An abstraction from full Pages and Page Fragments.
 */
public interface AbstractPage extends ValidityPeriod {

  /**
   * Returns the {@link Navigation navigation} denoted by the uri of the request for this response which
   * is not necessarily a content bean.
   * @return The navigation instance of this page.
   */
  Navigation getNavigation();

  /**
   * Returns the main content object rendered on this page
   */
  Object getContent();

  /**
   * Returns true, if the CAE that rendered this response runs in development mode.
   *
   */
  boolean isDeveloperMode();

  /**
   * Returns a {@link PageGrid}
   */
  PageGrid getPageGrid();

  /**
   * Returns the <code>Locale</code> for this abstract page.
   */
  Locale getLocale();

  /**
   * Returns a {@link java.lang.String} "ltr" or "rtl" depending to the <code>Locale</code> for this page
   */
  String getDirection();

  /**
   * Returns the title for this page
   */
  String getTitle();

  /**
   * Returns the keywords for this page
   */
  String getKeywords();

  /**
   * Returns the description for this page
   *  @deprecated use {@link com.coremedia.blueprint.common.contentbeans.CMLinkable#getHtmlDescription instead}
   */
  @Deprecated
  String getDescription();

  /**
   * A page is considered to be a detail view if the main contend rendered on the page is not a navigation node.
   *
   * @return true if if the {@link #getContent()} main content} of this page is not {@link com.coremedia.blueprint.common.navigation.Navigation} instance.
   */
  boolean isDetailView();

  /**
   * Returns a <code>Map</code> from aspectIDs to <code>Aspects</code>. AspectIDs consists of an aspect name
   * with a prefix which identifies the plugin provider.
   *
   * @return a <code>Map</code> from aspectIDs to <code>Aspect</code>s
   */
  Map<String, ? extends Aspect> getAspectByName();

  /**
   * Returns an id of the main content rendered on this page, e.g. for analytics purposes. May be null.
   */
  String getContentId();

  /**
   * Returns an optional type or classifier name of the main content rendered on this page, e.g. for analytics purposes.
   * May be null.
   */
  String getContentType();
}
