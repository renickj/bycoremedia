package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.undoc.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link ContextStrategy} that finds a context for an external page identified by its external id.
 * The external page must be part of the navigation. All findings will be cached with full dependency
 * tracking.
 */
public class ExternalPageContextStrategy implements ContextStrategy<String, Navigation> {

  private static final Logger LOG = LoggerFactory.getLogger(ExternalPageContextStrategy.class);

  private Cache cache;
  private SitesService sitesService;
  private ContentBeanFactory contentBeanFactory;
  private TreeRelation<Content> treeRelation;

  @Override
  public Navigation findAndSelectContextFor(String pageId, Navigation rootChannel) {
    return selectContext(rootChannel, findContextsFor(pageId, rootChannel));
  }

  @Override
  public List<Navigation> findContextsFor(@Nonnull String pageId) {
    LOG.warn("method findContextsFor(pageId) is not supported, use findContextFor(pageId, navigation) instead");
    return Collections.emptyList();
  }

  @Override
  public List<Navigation> findContextsFor(@Nonnull final String pageId, @Nullable final Navigation rootChannel) {
    List<Navigation> result = new ArrayList<>();
    if (rootChannel instanceof CMObject) {
      Site site = sitesService.getSiteFor(((CMObject) rootChannel).getContent());
      if (site != null) {
        Content externalChannel = cache.get(new CMExternalChannelCacheKey(pageId, site, treeRelation));
        if (externalChannel != null) {
          ContentBean externalChannelBean = contentBeanFactory.createBeanFor(externalChannel);
          if (externalChannelBean instanceof Navigation) {
            result.add((Navigation) externalChannelBean);
          }
        }
      }
    }
    return result;
  }

  @Override
  public Navigation selectContext(Navigation rootChannel, List<? extends Navigation> candidates) {
    return candidates != null && !candidates.isEmpty() ? candidates.get(0) : null;
  }

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setTreeRelation(TreeRelation<Content> treeRelation) {
    this.treeRelation = treeRelation;
  }

}
