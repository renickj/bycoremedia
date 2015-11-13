package com.coremedia.livecontext.search;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.cae.handlers.PageHandlerBase;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestion;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestions;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.search.SearchService;
import com.coremedia.livecontext.ecommerce.search.SuggestionResult;
import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;
import com.coremedia.objectserver.view.substitution.Substitution;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.UriComponentsHelper;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.ContentTypes.CONTENT_TYPE_JSON;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_DYNAMIC;

/**
 * Handler gets search suggestions from shop search service.
 */
@RequestMapping
@Link
public class CommerceSearchHandler extends PageHandlerBase {

  protected static final String ACTION_NAME = "wcssearch";
  protected static final String ACTION_ID = "wcssearch";

  protected static final String SEGMENT_ROOT = "root";
  protected static final String PARAMETER_QUERY = "query";
  protected static final String PARAMETER_TYPE = "type";

  private LinkFormatter linkFormatter;

  /**
   * e.g.: /dynamic/Helios/search?type=suggest&query=dre
   * e.g.: /dynamic/Helios/search?query=dress
   */
  private static final String   URI_PATTERN =
                  '/' + PREFIX_DYNAMIC +
                  "/{" + SEGMENT_ROOT + "}" +
                  '/' + ACTION_NAME;

  private SitesService sitesService;
  private CommercePropertyProvider searchResultRedirectUrlProvider;

  @Substitution(ACTION_ID)
  @SuppressWarnings("unused")
  public CommerceSearchActionState createActionState(CMAction representative, HttpServletRequest request) {
    return new CommerceSearchActionState(representative);
  }

  /**
   * Performs shop suggestion search and provides a JSON object containing the suggestions.
   */
  @ResponseBody
  @RequestMapping(value = URI_PATTERN, params = {PARAMETER_TYPE, PARAMETER_QUERY}, method = RequestMethod.GET, produces = CONTENT_TYPE_JSON)
  public List<Suggestion> handleAjaxKeywordSuggestion(
          @PathVariable(SEGMENT_ROOT) String context,
          @RequestParam(value = PARAMETER_QUERY) String term) {

// if no context available: return "not found"
//    StoreContext storeContext = storeContextProvider.getCurrentContext();
    Navigation navigation = getNavigation(context);
    if (navigation instanceof CMObject ) {
      StoreContext storeContext = getStoreContextProvider().findContextByContent(((CMObject) navigation).getContent());
      if (storeContext != null) {
        List<SuggestionResult> commerceSuggestions = getSearchService().getAutocompleteSuggestions(term);
        Suggestions suggestions = new Suggestions();
        List<Suggestion> suggestionList = new ArrayList<>();
        for (SuggestionResult commerceSuggestion : commerceSuggestions) {
          suggestionList.add(new Suggestion(commerceSuggestion.getSuggestTerm(), term, (long) commerceSuggestion.getResultCount()));
        }
        // sort suggestions by count and enforce limit
        Collections.sort(suggestionList);
        suggestions.addAll(suggestionList);

        return suggestions.delegate();
      }
    }

    throw new IllegalArgumentException("Could not get suggestions from shop search.");
  }

  /**
   * Redirects to shop search result page.
   */
  @RequestMapping(value = URI_PATTERN, params = {PARAMETER_QUERY}, method = RequestMethod.POST, produces = CONTENT_TYPE_JSON)
  public ModelAndView handleSearchRequest(
          @PathVariable(SEGMENT_ROOT) String context,
          @RequestParam(value = PARAMETER_QUERY) String term, HttpServletRequest request, HttpServletResponse response) throws IOException {

    // if no context available: return "not found"
    Navigation navigation = getNavigation(context);
    if (navigation instanceof CMObject ) {
      StoreContext storeContext = getStoreContextProvider().findContextByContent(((CMObject) navigation).getContent());
      if (storeContext != null) {
        Map<String, Object> params = new HashMap<>();
        params.put(LiveContextPageHandlerBase.URL_PROVIDER_STORE_CONTEXT, storeContext);
        params.put(LiveContextPageHandlerBase.URL_PROVIDER_IS_STUDIO_PREVIEW, LiveContextPageHandlerBase.isStudioPreviewRequest());
        params.put(LiveContextPageHandlerBase.URL_PROVIDER_SEARCH_TERM, term);

        UriComponents baseUrl = (UriComponents) searchResultRedirectUrlProvider.provideValue(params);
        UriComponents redirectUrl = UriComponentsHelper.fromUriComponents(baseUrl).scheme(request.getScheme()).build();
        String urlStr = redirectUrl.toString();
        if (linkFormatter != null) {
          List<LinkTransformer> transformers = linkFormatter.getTransformers();
          for (LinkTransformer transformer : transformers) {
            urlStr = transformer.transform(urlStr, null, null, request, response, true);

          }
        }
        response.sendRedirect(urlStr);
        response.flushBuffer();
        return null;
      }
    }

    return HandlerHelper.notFound();
  }

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @Link(type = CommerceSearchActionState.class, uri = URI_PATTERN)
  public UriComponents buildSearchActionLink(CommerceSearchActionState state, UriComponentsBuilder uri, Map<String, Object> linkParameters, HttpServletRequest request) {
    UriComponentsBuilder result = addLinkParametersAsQueryParameters(uri, linkParameters);
    Site site = sitesService.getContentSiteAspect(state.getAction().getContent()).getSite();
    if(site != null) {
      Navigation context = getContextHelper().currentSiteContext();
      return result.buildAndExpand(ImmutableMap.of(
        SEGMENT_ROOT, getPathSegments(context).get(0)));
    }
    return null;
  }

  public SearchService getSearchService() {
    return Commerce.getCurrentConnection().getSearchService();
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  @Override
  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setSearchResultRedirectUrlProvider(CommercePropertyProvider searchResultRedirectUrlProvider) {
    this.searchResultRedirectUrlProvider = searchResultRedirectUrlProvider;
  }

  public void setLinkFormatter(LinkFormatter linkFormatter) {
    this.linkFormatter = linkFormatter;
  }
}
