package com.coremedia.livecontext.context;

import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;

/**
 * A category can occur in several sites.
 * CategoryInSite associates a category with a site for deterministic link building.
 */
public interface CategoryInSite extends CommerceObject {
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
