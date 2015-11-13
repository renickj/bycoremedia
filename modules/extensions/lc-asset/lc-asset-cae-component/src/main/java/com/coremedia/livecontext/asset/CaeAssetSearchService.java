package com.coremedia.livecontext.asset;

import com.coremedia.blueprint.cae.search.Condition;
import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.search.SearchResultFactory;
import com.coremedia.blueprint.cae.search.Value;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.objectserver.beans.ContentBean;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CaeAssetSearchService implements AssetSearchService {

  private SearchResultFactory searchResultFactory;
  private long cacheForSeconds = 300;

  @Nonnull
  @Override
  public List<Content> searchAssets(@Nonnull String contentType, @Nonnull String externalId, @Nonnull Site site) {
    List<ContentBean> contentBeans = poseSolrQuery(contentType, site, externalId);
    List<Content> contents = new ArrayList<>(contentBeans.size());
    for (ContentBean contentBean : contentBeans) {
      contents.add(contentBean.getContent());
    }
    return contents;
  }

  private List<ContentBean> poseSolrQuery(@Nonnull String contentType, Site site, String externalId) {
    SearchQueryBean query = new SearchQueryBean();
    query.setSearchHandler(SearchQueryBean.SEARCH_HANDLER.DYNAMICCONTENT);
    Content siteRootDocument = site.getSiteRootDocument();
    int rootChannelId = IdHelper.parseContentId(siteRootDocument.getId());
    query.addFilter(Condition.is(SearchConstants.FIELDS.NAVIGATION_PATHS, Value.exactly("\\/" + rootChannelId)));
    query.setNotSearchableFlagIgnored(true);
    ContentRepository repository = siteRootDocument.getRepository();
    query.addFilter(Condition.is(SearchConstants.FIELDS.DOCUMENTTYPE, Value.anyOf(AssetServiceImpl.getSubTypesOf(contentType, repository))));
    query.setQuery(SearchConstants.FIELDS.COMMERCE_ITEMS.toString() + ':' + '"' + externalId + '"');
    SearchResultBean searchResult = searchResultFactory.createSearchResult(query, cacheForSeconds);
    //noinspection unchecked
    return (List<ContentBean>) searchResult.getHits();
  }

  @Required
  public void setSearchResultFactory(SearchResultFactory searchResultFactory) {
    this.searchResultFactory = searchResultFactory;
  }

  public void setCacheForSeconds(long cacheForSeconds) {
    this.cacheForSeconds = cacheForSeconds;
  }
}
