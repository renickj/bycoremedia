package com.coremedia.livecontext.navigation;

import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.CategoryInSite;
import com.coremedia.livecontext.ecommerce.catalog.Category;

import javax.annotation.Nonnull;

import static org.springframework.util.Assert.notNull;

/**
 * Immutable instances of CategoryInSite.
 */
public class CategoryInSiteImpl implements CategoryInSite {
  private final Category category;
  private final Site site;

  public CategoryInSiteImpl(Category category, Site site) {
    notNull(category);
    notNull(site);
    this.category = category;
    this.site = site;
  }

  @Nonnull
  @Override
  public Category getCategory() {
    return category;
  }

  @Nonnull
  @Override
  public Site getSite() {
    return site;
  }
}
