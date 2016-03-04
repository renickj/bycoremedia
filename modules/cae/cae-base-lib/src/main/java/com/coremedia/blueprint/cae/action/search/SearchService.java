package com.coremedia.blueprint.cae.action.search;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.search.*;
import com.coremedia.blueprint.cae.search.solr.SolrSearchParams;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestion;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestions;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * The Search service used for fulltext and autocomplete search
 */
public class SearchService {

  private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

  private static final int HITS_PER_PAGE_DEFAULT = 10;

  private SearchResultFactory resultFactory;
  private boolean highlightingEnabled = false;

  private ContentBeanFactory contentBeanFactory;
  private ContentRepository contentRepository;
  private SitesService sitesService;
  private SettingsService settingsService;

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Required
  public void setResultFactory(SearchResultFactory resultFactory) {
    this.resultFactory = resultFactory;
  }

  public void setHighlightingEnabled(boolean highlightingEnabled) {
    this.highlightingEnabled = highlightingEnabled;
  }

  /**
   * the fulltext search method
   *
   * @param page       the search result page
   * @param searchForm SearchFormBean
   * @param docTypes the doctypes to use for the search
   * @return the search result
   */
  public SearchResultBean search(Page page, SearchFormBean searchForm, Collection<String> docTypes) {
    return search(page, searchForm, docTypes, null);
  }

  /**
   * the fulltext search method
   *
   * @param page       the search result page
   * @param searchForm SearchFormBean
   * @param docTypes the doctypes to use for the search
   * @param taxonomySearch performs taxonomy search instead of classic search (the query must be comma separated list of taxonomy ids)
   * @return the search result
   */
  public SearchResultBean search(Page page, SearchFormBean searchForm, Collection<String> docTypes, String taxonomySearch) {
    if (StringUtils.isEmpty(searchForm.getQuery())) {
      return null;
    }
    // get max hits settings
    int hitsPerPage = settingsService.settingWithDefault("search.result.hitsPerPage", Integer.class, HITS_PER_PAGE_DEFAULT, page.getContext());
    // build query
    SearchQueryBean searchQuery = new SearchQueryBean();
    searchQuery.setSearchHandler(SearchQueryBean.SEARCH_HANDLER.FULLTEXT);
    // add query string
    if (taxonomySearch!=null) {
      List<String> ids = Arrays.asList(searchForm.getQuery().split(","));
      searchQuery.addFilter(Condition.is(taxonomySearch, Value.anyOf(ids)));
      searchQuery.setQuery("*");
    } else {
      searchQuery.setQuery(searchForm.getQuery());
    }
    searchQuery.setSpellcheckSuggest(true);
    if (highlightingEnabled) {
      searchQuery.setHighlightingEnabled(true);
    }
    int rootChannelId = page.getNavigation().getRootNavigation().getContentId();
    // set channel filter from form
    if (StringUtils.isNotEmpty(searchForm.getChannelId()) && !searchForm.getChannelId().equals("" + rootChannelId)) {
      Content content = contentRepository.getContent(IdHelper.formatContentId(searchForm.getChannelId()));
      CMNavigation channel = contentBeanFactory.createBeanFor(content, CMNavigation.class);
      StringBuilder builder = new StringBuilder();
      for (Linkable aChannel : channel.getNavigationPathList()) {
        if(aChannel instanceof CMNavigation) {
          builder.append("\\/").append(((CMNavigation)aChannel).getContentId());
        }
      }

      Condition channelCondition = Condition.is(SearchConstants.FIELDS.NAVIGATION_PATHS, Value.exactly(builder.toString()));
      searchQuery.addFilter(channelCondition);
    } else {
      // set root channel id filter to limit results to the site the given action's default context belongs to
      searchQuery.addFilter(Condition.is(SearchConstants.FIELDS.NAVIGATION_PATHS, Value.exactly("\\/" + rootChannelId)));
    }
    // set doctypes to filter if applied in form
    if (StringUtils.isNotEmpty(searchForm.getDocTypeEscaped())) {
      Condition docTypeCondition = Condition.is(SearchConstants.FIELDS.DOCUMENTTYPE,
              Value.exactly(searchForm.getDocTypeEscaped()));
      searchQuery.addFilter(docTypeCondition);
    }
    //else apply doctypes that configured for filtering in the settings
    else if(docTypes != null && !docTypes.isEmpty()) {
      Condition docTypeCondition = Condition.is(SearchConstants.FIELDS.DOCUMENTTYPE,
              Value.anyOf(docTypes));
      searchQuery.addFilter(docTypeCondition);
    }

    // add facets
    searchQuery.setFacetFields(Collections.singletonList(SearchConstants.FIELDS.DOCUMENTTYPE.toString()));
    searchQuery.setFacetMinCount(SolrSearchParams.FACET_MIN_COUNT);
    searchQuery.setSortFields(Arrays.asList(SearchConstants.FIELDS.MODIFICATION_DATE.toString()));
    // add limit/offset
    searchQuery.setLimit(hitsPerPage);
    searchQuery.setOffset(searchForm.getPageNum() * hitsPerPage);
    // run query
    return resultFactory.createSearchResultUncached(searchQuery);
  }


  /**
   * The search query executed to find topic pages for the given search term
   * @param searchForm The user inputted search data.
   * @param docTypes The doctypes
   * @return Topic search results.
   */
  public SearchResultBean searchTopics(Navigation navigation, SearchFormBean searchForm, Collection<String> docTypes) {
    if (StringUtils.isEmpty(searchForm.getQuery()) || CollectionUtils.isEmpty(docTypes)) {
      return null;
    }

    SearchQueryBean searchQuery = new SearchQueryBean();
    searchQuery.setSearchHandler(SearchQueryBean.SEARCH_HANDLER.DYNAMICCONTENT);

    Condition docTypesCondition = Condition.is(SearchConstants.FIELDS.DOCUMENTTYPE, Value.anyOf(docTypes));
    searchQuery.addFilter(docTypesCondition);

    // taxonomy value is fed into field teaserTitle
    Condition topicValueMatches = Condition.is(SearchConstants.FIELDS.TEASER_TITLE, Value.exactly('*' + searchForm.getQueryEscaped() + '*'));
    searchQuery.addFilter(topicValueMatches);

    // select only taxonomies being referenced in current site
    Content root = navigation.getRootNavigation().getContent();
    Site site = requireNonNull(sitesService.getContentSiteAspect(root).getSite(), format("Site for root content %s does not exist", root.getPath()));
    String path = site.getSiteRootFolder().getPath();
    Condition sitePath = Condition.is(SearchConstants.FIELDS.TAXONOMY_REFERRERS_IN_SITE, Value.exactly(path));
    searchQuery.addFilter(sitePath);

    searchQuery.setQuery("*:*");
    // order and limit
    searchQuery.setSortFields(Arrays.asList(SearchConstants.FIELDS.MODIFICATION_DATE.toString()));

    // run the query
    return resultFactory.createSearchResultUncached(searchQuery);
  }

  /**
   * the autocomplete search method
   *
   * @param rootNavigationId the page to retrieve the search ahead results for
   * @param term             the term the user is searching for
   * @param docTypes         the document types to restrict searching to
   * @return a list of suggestions
   */
  public Suggestions getAutocompleteSuggestions(String rootNavigationId, String term, Collection<String> docTypes) {
    Suggestions suggestions = new Suggestions();

    try {
      SearchQueryBean searchQuery = new SearchQueryBean();
      searchQuery.setSearchHandler(SearchQueryBean.SEARCH_HANDLER.SUGGEST);
      searchQuery.setQuery(term);
      // restrict to given site
      Condition cond = Condition.is(SearchConstants.FIELDS.NAVIGATION_PATHS, Value.exactly("\\/" + rootNavigationId));
      searchQuery.addFilter(cond);
      if (docTypes != null && !docTypes.isEmpty()) {
        searchQuery.addFilter(Condition.is(SearchConstants.FIELDS.DOCUMENTTYPE, Value.anyOf(docTypes)));
      }
      searchQuery.setLimit(1);

      LOG.debug("Getting suggestions for: {}", searchQuery.getQuery());

      SearchResultBean result = resultFactory.createSearchResultUncached(searchQuery);

      // add result to suggestions list
      List<ValueAndCount> suggestionValues = result.getAutocompleteSuggestions();

      List<Suggestion> suggestionList = new ArrayList<>();
      for (ValueAndCount vc : suggestionValues) {
        suggestionList.add(new Suggestion(vc.getName(), vc.getName(), vc.getCount()));
      }

      // sort suggestions by count and enforce limit
      Collections.sort(suggestionList);
      suggestions.addAll(suggestionList);

    } catch (Exception e) {
      LOG.error("Cannot retrieve suggestion", e);
    }

    return suggestions;
  }
}
