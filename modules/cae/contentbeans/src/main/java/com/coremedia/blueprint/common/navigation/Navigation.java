package com.coremedia.blueprint.common.navigation;

import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.cae.aspect.provider.AspectsProvider;

import java.util.List;

/**
 * Common methods implemented by navigation nodes, like CMNavigation.
 * Navigation objects are Linkable too.
 */
public interface Navigation extends Linkable {

  /**
   * Returns the children of this navigation object.
   * If no child exist, an empty list will be returned.
   *
   * @return a list of {@link Linkable} objects
   */
  List<? extends Linkable> getChildren();

  /**
   * Returns the parent of this navigation instance.
   * @return
   */
  Navigation getParentNavigation();

  /**
   * Returns the root {@link Navigation} object (=Site) for this navigation item.
   * If {@link #isRoot()} is true, getRootNavigation returns this.
   *
   * @return the root {@link Navigation}
   */
  CMNavigation getRootNavigation();

  /**
   * Return the first navigation context within the parent hierarchy which is an instance of CMContext
   */
  CMContext getContext();


  /**
   * Use hidden in order to provide <i>nice</i> URLs for content
   * which should not be reachable by navigation.
   * <p>Semantic: hidden implies hiddenInSitemap.</p>
   */
  boolean isHidden();

  /**
   * Returns the children which are visible in navigational contexts.
   * <p/>
   * I.e. the same list as {@link #getChildren()} except {@link Navigation}
   * documents whose
   * {@link Navigation#isHidden()}
   * flag is true.
   *
   * @return the children which are visible in navigational contexts.
   */
  List<? extends Linkable> getVisibleChildren();

  /**
   * Returns the value of the document is hidden in sidemap.
   * <p/>
   * <p>Do not show this channel in a sitemap. We recommend to use this flag
   * in exceptional cases only, because a sitemap is not very helpful if it
   * differs too much from the actual navigation.</p>
   *
   */
  boolean isHiddenInSitemap();

  /**
   * Returns the children which are visible in sitemaps.
   * <p/>
   * Reasonable implementations will delegate to {@link #getVisibleChildren()}
   * and possibly filter the result.  A list which is unrelated to
   * {@link #getVisibleChildren()} would be confusing.
   *
   * @return the children which are visible in sitemaps.
   */
  List<? extends Linkable> getSitemapChildren();

  /**
   * @return true if this navigation item has no parents.
   */
  boolean isRoot();

  //todo introduced to make Elastic Social Plugin work again - document or remove aspects
  AspectsProvider getAspectsProvider();


  /**
   * Returns the navigation path of this navigation from the {@link #getRootNavigation() root navigation}
   * to this navigation.
   *
   * @return the list of navigations forming the path to this Navigation including this.
   */
  List<? extends Linkable> getNavigationPathList();
}
