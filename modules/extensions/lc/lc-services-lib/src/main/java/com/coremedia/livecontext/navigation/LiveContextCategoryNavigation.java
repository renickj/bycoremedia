package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cae.aspect.provider.AspectsProvider;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.Assert.notNull;

/**
 * A LiveContextNavigation which is backed by a category at runtime.
 * Not persisted in the CMS repository.
 */
public class LiveContextCategoryNavigation implements LiveContextNavigation {
  private LiveContextNavigationTreeRelation treeRelation;
  private Category category;
  private Site site;


  // --- Construction -----------------------------------------------

  public LiveContextCategoryNavigation(@Nonnull Category category,
                                       @Nonnull Site site,
                                       @Nonnull LiveContextNavigationTreeRelation treeRelation) {
    notNull(category);
    notNull(site);
    notNull(treeRelation);

    this.category = category;
    this.site = site;
    this.treeRelation = treeRelation;
  }


  // --- LiveContextNavigation --------------------------------------

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


  // --- Navigation -------------------------------------------------

  @Override
  public List<? extends Linkable> getChildren() {
    Collection<Linkable> children = treeRelation.getChildrenOf(this);
    if (children != null) {
      return (List<? extends Linkable>) children;
    }
    return Collections.emptyList();
  }

  @Override
  public Navigation getParentNavigation() {
    return treeRelation.getParentOf(this);
  }

  @Override
  public CMNavigation getRootNavigation() {
    List<Linkable> navigationPath = treeRelation.pathToRoot(this, site);
    if (isEmpty(navigationPath)) {
      return null;
    }

    Linkable rootNavigation = navigationPath.get(navigationPath.size() - 1);
    isInstanceOf(CMNavigation.class, rootNavigation);
    return (CMNavigation) rootNavigation;
  }

  @Override
  public CMContext getContext() {
    CMExternalChannel externalChannel = treeRelation.findExternalChannelForRecursively(getCategory(), getSite());
    if (externalChannel != null) {
      return externalChannel;
    }
    // after no adequate external page could be found (not even recursively along the category path)...
    // return the catalog root page, whose position is the second in the navigation path list. Or...
    List<? extends Linkable> pathToRoot = getNavigationPathList();
    if (pathToRoot.size() > 1 && pathToRoot.get(1) instanceof CMContext) {
      return (CMContext) pathToRoot.get(1);
    }
    // after no catalog root page is found take the site root node
    if (pathToRoot.size() > 0 && pathToRoot.get(0) instanceof CMContext) {
      return (CMContext) pathToRoot.get(0);
    }
    return null;
  }

  @Override
  public List<? extends Linkable> getNavigationPathList() {
    List<Linkable> navigations = treeRelation.pathToRoot(this, site);
    List<Linkable> reversed = new ArrayList<>(navigations);
    Collections.reverse(reversed);
    return reversed;
  }

  @Override
  public boolean isHidden() {
    return false;
  }

  @Override
  public List<? extends Linkable> getVisibleChildren() {
    return getChildren();
  }

  @Override
  public boolean isHiddenInSitemap() {
    return false;
  }

  @Override
  public List<? extends Linkable> getSitemapChildren() {
    return getChildren();
  }

  @Override
  public String getTitle() {
    return category.getName();
  }

  @Override
  public String getSegment() {
    String seoSegment = category.getSeoSegment();
    return isEmpty(seoSegment) ? category.getExternalId() : seoSegment;
  }

  @Override
  public String getKeywords() {
    return category.getName() + " " + category.getSeoSegment() + " " + category.getShortDescription();
  }

  @Override
  public boolean isRoot() {
    return getParentNavigation() == null;
  }

  @Override
  public Locale getLocale() {
    return category.getLocale();
  }

  @Override
  public String getViewTypeName() {
    return null;
  }

  @Override
  public AspectsProvider getAspectsProvider() {
    //todo introduced to make Elastic Social Plugin work again - revise or remove aspects
    if (getParentNavigation() != null) {
      return getParentNavigation().getAspectsProvider();
    }
    return null;
  }

  @Override
  public boolean isCatalogPage() {
    return true;
  }

  @Override
  public String getExternalId() {
    return getCategory().getExternalId();
  }

  @Override
  public String getExternalUriPath() {
    return null;
  }

  @Override
  public String toString() {
    return "LiveContextNavigation for '" + category.getName() + "'";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LiveContextCategoryNavigation that = (LiveContextCategoryNavigation) o;
    return category.equals(that.category) && site.equals(that.site);
  }

  @Override
  public int hashCode() {
    int result = category.hashCode();
    result = 31 * result + site.hashCode();
    return result;
  }
}
