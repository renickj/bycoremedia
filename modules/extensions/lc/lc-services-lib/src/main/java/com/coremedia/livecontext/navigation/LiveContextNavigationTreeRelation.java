package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.tree.ChildrenLinkListContentTreeRelation;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.events.ContentDestroyedEvent;
import com.coremedia.cap.content.events.ContentEvent;
import com.coremedia.cap.content.events.ContentRepositoryListenerBase;
import com.coremedia.cap.content.publication.events.PlaceApprovedEvent;
import com.coremedia.cap.content.query.QueryService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Tree relation implementation of the live context.
 * The live context tree relation resolves content beans of type CMExternalLink
 * and applies the matching category subtree as navigation tree to the given navigation.
 * <p/>
 * The live context categories are wrapped into LiveContextNavigation instances that includes
 * this tree relation for resolving additional sub children that are live context categories.
 */
public class LiveContextNavigationTreeRelation implements DisposableBean, InitializingBean {

  private static final Logger LOG = LoggerFactory.getLogger(LiveContextNavigationTreeRelation.class);
  public static final String CATEGORY_OVERVIEW_PAGE_SETTINGS_NAME = "COP";
  public static final String ROOT_PAGE_SETTING_NAME = "root";

  private ContentRepository contentRepository;
  private ContentBeanFactory contentBeanFactory;
  private LiveContextNavigationFactory navigationFactory;
  private ChildrenLinkListContentTreeRelation contentBasedTreeRelation;
  private Cache cache;
  private SettingsService settingsService;

  public Collection<Linkable> getChildrenOf(LiveContextNavigation parent) {
    List<Linkable> navigationList = new ArrayList<>();

    //we deal with a category already, so we simply resolve the categories children
    for (Category child : parent.getCategory().getChildren()) {
      Linkable navigation = navigationFactory.createNavigation(child, parent.getSite());
      navigationList.add(navigation);
    }
    return navigationList;
  }

  public Navigation getParentOf(LiveContextNavigation navigation) {
    Content externalChannel = findExternalChannelContentFor(navigation.getCategory(), navigation.getSite(), false);
    if (externalChannel != null) {
      return getParentOfExternalChannel(externalChannel);
    } else {
      return getParentOfCategory(navigation.getCategory(), navigation.getSite());
    }
  }

  public List<Linkable> pathToRoot(Navigation child, Site site) {
    List<Linkable> path = new ArrayList<>();
    Navigation parent = child;
    while (parent != null) {
      path.add(parent);
      parent = parent.getParentNavigation();
    }

    // if there are only synthetic category nodes are available it is necessary to add
    // the catalog root page (found in settings) and the root channel to complete the
    // path to root
    if (path.size() > 0) {
      Linkable last = path.get(path.size()-1);
      if (!(last instanceof CMNavigation)) {
        Content siteRootDocument = site.getSiteRootDocument();
        Linkable rootChannel = contentBeanFactory.createBeanFor(siteRootDocument, Linkable.class);
        Linkable catalogRootPage = getCatalogRootPage(site);
        if (catalogRootPage != null) {
          path.add(catalogRootPage);
        }
        path.add(rootChannel);
      }
    }

    return path;
  }

  private Linkable getCatalogRootPage(Site site) {
    try {
      Map<String, Object> catalogSettings = settingsService.settingAsMap(CATEGORY_OVERVIEW_PAGE_SETTINGS_NAME, String.class, Object.class, site.getSiteRootDocument());
      Content content = (Content) catalogSettings.get(ROOT_PAGE_SETTING_NAME);
      return contentBeanFactory.createBeanFor(content, CMNavigation.class);
    }
    catch (Exception e) {
      LOG.error("Cannot resolve settings map for page prefix '" + CATEGORY_OVERVIEW_PAGE_SETTINGS_NAME + "'", e);
      return null;
    }
  }

  public CMExternalChannel findExternalChannelFor(Category category, Site site) {
    Content externalNavigation = findExternalChannelContentFor(category, site, false);
    return contentBeanFactory.createBeanFor(externalNavigation, CMExternalChannel.class);
  }

  public CMExternalChannel findExternalChannelForRecursively(Category category, Site site) {
    Content externalNavigation = findExternalChannelContentFor(category, site, true);
    return contentBeanFactory.createBeanFor(externalNavigation, CMExternalChannel.class);
  }

  // --- internal ---------------------------------------------------

  private Navigation getParentOfExternalChannel(@Nonnull Content externalChannel) {
    Content parent = contentBasedTreeRelation.getParentOf(externalChannel);
    return contentBeanFactory.createBeanFor(parent, Navigation.class);
  }

  private Navigation getParentOfCategory(@Nonnull Category category, @Nonnull Site site) {
    return getNavigationOfCategory(site, category.getParent());
  }

  private Navigation getNavigationOfCategory(Site site, Category category) {
    if (category == null) {
      return null;
    }
    CMExternalChannel externalChannel = findExternalChannelFor(category, site);
    return externalChannel != null
            ? externalChannel
            : navigationFactory.createNavigation(category, site);
  }

  private Content findExternalChannelContentFor(Category category, Site site, boolean recursively) {
    Content intendedRootNavigation = site.getSiteRootDocument();
    Collection<Content> contents = findExternalChannelContentsFor(category, site, recursively);
    for (Content externalChannel : contents) {
      Content actualRootNavigation = contentBasedTreeRelation.pathToRoot(externalChannel).get(0);
      if (intendedRootNavigation.equals(actualRootNavigation)) {
        return externalChannel;
      }
    }
    return null;
  }

  /**
   * In a multi language web presence a category may be linked into multiple
   * sites.  In this case we will find multiple CMExternalChannels which
   * represent this category.  This is a legal repository state.
   */
  private Collection<Content> findExternalChannelContentsFor(Category category, Site site, boolean recursively) {
    return cache.get(new ExternalChannelFinder(category, site, recursively));
  }

  @VisibleForTesting
  String categoryIdToExternalChannelRef(Category category) {
    return category.getReference();
  }


  // --- configuration ----------------------------------------------

  @Required
  public void setNavigationFactory(LiveContextNavigationFactory navigationFactory) {
    this.navigationFactory = navigationFactory;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setContentBasedTreeRelation(ChildrenLinkListContentTreeRelation contentBasedTreeRelation) {
    this.contentBasedTreeRelation = contentBasedTreeRelation;
  }

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  private class ExternalChannelFinder extends CacheKey<Collection<Content>> {
    private Category category;
    private Site site;
    private boolean recursively;

    public ExternalChannelFinder(Category category, Site site, boolean recursively) {
      this.category = category;
      this.site = site;
      this.recursively = recursively;
    }

    @Override
    public Collection<Content> evaluate(Cache cache) throws Exception {

      Collection<Content> contents;

      if (!recursively) {

        String query = "TYPE = " + CMExternalChannel.NAME + " : " + CMExternalChannel.EXTERNAL_ID + " = ?0 AND isInProduction AND BELOW ?1 ORDER BY id";
        String externalId = categoryIdToExternalChannelRef(category);
        Content folder = site.getSiteRootFolder();
        QueryService queryService = contentRepository.getQueryService();
        Cache.disableDependencies();
        try {
          contents = queryService.poseContentQuery(query, externalId, folder);
        } finally {
          Cache.enableDependencies();
        }
        // do it once again with detailed dependencies for all existing contents (so we notice when they go away)...
        contents = queryService.getContentsFulfilling(contents, query, externalId, folder);
        // and add another dependency for any newly created channels with that externalId (invalidated by listener)
        Cache.dependencyOn(new ExternalChannelDependency(externalId));

      } else {

        contents = findExternalChannelContentsFor(category, site, false);
        if (contents == null || contents.isEmpty()) {
          Category parent = category.getParent();
          if (parent != null) {
            contents = findExternalChannelContentsFor(parent, site, true);
          }
        }

      }

      return contents;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      ExternalChannelFinder that = (ExternalChannelFinder) o;

      return category.equals(that.category) && recursively == that.recursively && site.getId().equals(that.site.getId());
    }

    @Override
    public int hashCode() {
      return category.hashCode() + (recursively+"").hashCode() + (site.getId()+"").hashCode();
    }
  }

  static class ExternalChannelDependency {
    private String externalId;

    public ExternalChannelDependency(String externalId) {
      this.externalId = externalId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ExternalChannelDependency that = (ExternalChannelDependency) o;

      if (externalId != null ? !externalId.equals(that.externalId) : that.externalId != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return externalId != null ? externalId.hashCode() : 0;
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    listener = new ExternalIdContentRepositoryListener(cache);
    contentRepository.addContentRepositoryListener(listener);
  }

  @Override
  public void destroy() throws Exception {
    if(listener != null) {
      contentRepository.removeContentRepositoryListener(listener);
    }
  }

  ExternalIdContentRepositoryListener listener;

  private static class ExternalIdContentRepositoryListener extends ContentRepositoryListenerBase {
    private Cache cache;

    public ExternalIdContentRepositoryListener(Cache cache) {
      this.cache = cache;
    }

    @Override
    public void contentDestroyed(ContentDestroyedEvent event) {
      // we already know that the content is destroyed and there's nothing to do.
    }

    @Override
    public void placeApproved(PlaceApprovedEvent event) {
      super.placeApproved(event);
    }

    @Override
    protected void handleContentEvent(ContentEvent event) {
      Content content = event.getContent();
      if(!content.isDestroyed() && content.isInstanceOf(CMExternalChannel.NAME)) {
        String externalId = content.getString(CMExternalChannel.EXTERNAL_ID);
        if (!StringUtils.isBlank(externalId)) {
          cache.invalidate(new ExternalChannelDependency(externalId));
        }
      }
    }
  }
}
