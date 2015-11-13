package com.coremedia.livecontext.logictypes;

import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.query.QueryService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.fragment.resolver.SearchTermExternalReferenceResolver;
import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Extension that create custom links on CMChannel documents.
 */
public class SearchLandingPagesExtension extends ExtensionBase {
  private CommercePropertyProvider searchResultRedirectUrlProvider;
  private String keywordsProperty;
  private String segmentPath;
  private TreeRelation<Content> navigationTreeRelation;

  public boolean isSearchLandingPage(CMChannel channel) {
    boolean result = false;

    Site site = getSitesService().getContentSiteAspect(channel.getContent()).getSite();

    if (site != null) {
      Content context = getNavigationContext(site);
      if (context != null) {
        result = context.getLinks(CMChannel.CHILDREN).contains(channel.getContent());
      }
    }

    return result;
  }

  /**
   * Creates a search URL that points to the commerce system, including the search
   * parameters that a read from a content property of the channel.
   */
  public Object createSearchLandingPageURLFor(CMChannel channel) {
    Site site = getSitesService().getContentSiteAspect(channel.getContent()).getSite();
    StoreContext storeContext = getStoreContextProvider().findContextBySite(site);
    String term = channel.getContent().getString(keywordsProperty);

    Map<String, Object> params = new HashMap<>();
    params.put(LiveContextPageHandlerBase.URL_PROVIDER_STORE_CONTEXT, storeContext);
    params.put(LiveContextPageHandlerBase.URL_PROVIDER_IS_STUDIO_PREVIEW, isStudioPreview());
    params.put(LiveContextPageHandlerBase.URL_PROVIDER_SEARCH_TERM, term);

    return searchResultRedirectUrlProvider.provideValue(params);
  }

  // ----------------- Helper -------------------------------

  /**
   * Returns the navigation context configured with {@link #setSegmentPath(String)} relative to the
   * {@link com.coremedia.cap.multisite.Site#getSiteRootDocument() root document} of the given site.
   *
   * @param site site
   * @return navigation, null if not found
   */
  @Nullable
  private Content getNavigationContext(@Nonnull Site site) {
    Preconditions.checkArgument(!segmentPath.startsWith("/"),
            "Segment path must be relative and not start with a slash: " + segmentPath);
    Iterable<String> segments = Splitter.on('/').omitEmptyStrings().split(segmentPath);

    Content context = site.getSiteRootDocument();
    if (context == null) {
      return null;
    }

    QueryService queryService = site.getSiteRootDocument().getRepository().getQueryService();
    Iterator<String> it = segments.iterator();
    while (it.hasNext() && context != null) {
      String segment = it.next();
      Collection<Content> children = navigationTreeRelation.getChildrenOf(context);
      context = queryService.getContentFulfilling(children, SearchTermExternalReferenceResolver.QUERY_NAVIGATION_WITH_SEGMENT, segment);
    }
    return context;
  }

  // ---------------- Config --------------------------------

  @Required
  public void setSearchResultRedirectUrlProvider(CommercePropertyProvider searchResultRedirectUrlProvider) {
    this.searchResultRedirectUrlProvider = searchResultRedirectUrlProvider;
  }

  @Required
  public void setKeywordsProperty(String keywordsProperty) {
    this.keywordsProperty = keywordsProperty;
  }

  @Required
  public void setSegmentPath(String segmentPath) {
    this.segmentPath = segmentPath;
  }

  @Required
  public void setNavigationTreeRelation(TreeRelation<Content> navigationTreeRelation) {
    this.navigationTreeRelation = navigationTreeRelation;
  }
}
