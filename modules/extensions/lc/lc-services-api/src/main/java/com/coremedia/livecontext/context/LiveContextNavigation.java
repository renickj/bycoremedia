package com.coremedia.livecontext.context;

import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.Category;

public interface LiveContextNavigation extends Navigation {

  /**
   * Returns true if the page is part of the catalog hierarchy (category or product page).
   * @return true if it is a catalog page
   */
  boolean isCatalogPage();

  /**
   * Returns the external id.
   * @return the external id
   */
  String getExternalId();

  /**
   * Returns the external uri path.
   * @return the external uri path
   */
  String getExternalUriPath();

  /**
   * Returns the category.
   * @return the category
   */
  Category getCategory();

  /**
   * Returns the site.
   * @return the site.
   */
  Site getSite();
}
