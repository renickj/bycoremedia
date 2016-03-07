package com.boots.cms.handlers;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.action.search.SearchActionState;
import com.coremedia.blueprint.cae.action.search.SearchFormBean;
import com.coremedia.blueprint.cae.action.search.SearchService;
import com.coremedia.blueprint.cae.handlers.PageHandlerBase;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestion;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestions;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.view.substitution.Substitution;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_NUMBER;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_SERVICE;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ID;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ROOT;
import static com.coremedia.objectserver.web.HandlerHelper.notFound;
import static java.util.Arrays.asList;

/**
 * Handler responsible for rendering search links and handling search requests
 */
@RequestMapping
@Link
public class JSONSearchActionHandler extends PageHandlerBase {

  protected static final String ACTION_NAME = "jsonsearch";
  protected static final String ACTION_ID = "jsonsearch";
  protected static final String PARAMETER_ROOT_NAVIGATION_ID = "rootNavigationId";
  protected static final String PARAMETER_QUERY = "query";
  protected static final String CONTENT_TYPE_JSON = "application/json";

  //used for filtering doctypes when search is executed
  static final String DOCTYPE_SELECT = "search.doctypeselect";
  static final String TOPICS_DOCTYPE_SELECT = "search.topicsdoctypeselect";

  static final int DEFAULT_MINIMAL_SEARCH_QUERY_LENGTH = 3;

  /**
   * e.g. /service/media/2236/jsonsearch
   */
  private static final String URI_PATTERN =
          '/' + PREFIX_SERVICE +
                  "/"+ACTION_NAME +
                  "/{" + SEGMENT_ROOT + "}" +
                  "/{" + SEGMENT_ID + ":" + PATTERN_NUMBER + "}";
  private static final String SEARCH_CHANNEL_SETTING = "searchChannel";


  private SearchService searchService;
  private SettingsService settingsService;
  private int minimalSearchQueryLength = DEFAULT_MINIMAL_SEARCH_QUERY_LENGTH;

  @Required
  public void setSearchService(SearchService searchService) {
    this.searchService = searchService;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Substitution(ACTION_ID)
  @SuppressWarnings("unused")
  public SearchActionState createActionState(CMAction representative, HttpServletRequest request) {
    return new SearchActionState(representative);
  }

  /**
   * Performs site search
   *
   * @see "SearchActionState.jsp"
   */
  @RequestMapping(value = URI_PATTERN, method = RequestMethod.GET)
  public ModelAndView handleSearchAction( HttpServletRequest request, @PathVariable(SEGMENT_ID) CMAction action,
                                   @PathVariable(SEGMENT_ROOT) String context,
                                   @ModelAttribute() SearchFormBean searchForm) {
    Navigation navigation = getValidNavigation(action, context, ACTION_NAME);
   // CMAction searchAction = (CMAction)navigation.getRootNavigation().getSettingMap("caeSettings").getMap().get("searchActionLink");
    //String id = searchAction.getId();
    if (navigation != null) {
      CMChannel searchChannel = settingsService.setting(SEARCH_CHANNEL_SETTING, CMChannel.class, navigation);
      Page searchResultsPage = asPage(searchChannel, searchChannel);
      //context is always accessed via the page request, so we have to apply it to the
      RequestContextHolder.getRequestAttributes().setAttribute(ContextHelper.ATTR_NAME_PAGE, searchResultsPage, RequestAttributes.SCOPE_REQUEST);


      ModelAndView result;
      SearchActionState actionBean;

      String  taxonomySearch = request.getParameter("taxonomySearch");
      // limit as query param for number of results 
      String  limit = request.getParameter("limit");
      // only search if query is long enough
      if ("*".equals(searchForm.getQuery())||(searchForm.getQuery() != null && searchForm.getQuery().length() >= minimalSearchQueryLength)) {
        //regular search result filtered by doctypes given in the Search Settings document
        Collection<String> docTypes = settingsService.settingAsList(DOCTYPE_SELECT, String.class, navigation);
        /*added for the limit tags for particular type- aravind */
        SearchResultBean searchResult = null;
        if(limit!=null){
        	
            SearchResultBean searchResultTemp = null;
            List<String> ids = asList(searchForm.getQuery().split(","));
            searchForm.setQuery(ids.get(0));
            searchResult = searchService.search(searchResultsPage, searchForm, docTypes,taxonomySearch,limit);
            List<CMTeasable> teaserList = (List<CMTeasable>)searchResult.getHits();
            for(String id:ids){
            	searchForm.setQuery(id);
            	searchResultTemp = searchService.search(searchResultsPage, searchForm, docTypes,taxonomySearch,limit);
            	for (Object aResult : searchResultTemp.getHits()) {
            		if (aResult instanceof CMTeasable) {
            			CMTeasable tresult = (CMTeasable)aResult;
            			if(!containsTeasable(teaserList,tresult)){
            				teaserList.add(tresult);
            			}
            		}
            	}
            }
            searchResult.setHits(teaserList);
            /*end*/
        }else{
        	searchResult = searchService.search(searchResultsPage, searchForm, docTypes,taxonomySearch,limit);
        }
        //topics search result filtered by topics doctypes given in the Search Settings document
        Collection<String> topicDocTypes = settingsService.settingAsList(TOPICS_DOCTYPE_SELECT, String.class, navigation);
        SearchResultBean searchResultTopics = searchService.searchTopics(navigation, searchForm, topicDocTypes);

        actionBean = new SearchActionState(action, searchForm, searchResult, searchResultTopics);
      } else if (searchForm.getQuery() == null) {
        actionBean = new SearchActionState(action, searchForm, null, null);
      } else {
        // if no search was executed, write error into SearchActionState.
        actionBean = new SearchActionState(action, searchForm, SearchActionState.ERROR_QUERY_TOO_SHORT);
      }
      // build model
      result = HandlerHelper.createModel(actionBean);
      result.setViewName("asJSON");

      addPageModel(result, searchResultsPage);
      return result;

    }
    return notFound();
  }
  
  private boolean containsTeasable(List<CMTeasable> teaserList,CMTeasable cmTeasable){
	  boolean isFound = false;
	  int teaserContentId = cmTeasable.getContentId();
	  for(CMTeasable teasable : teaserList){
		  if(teasable.getContentId() == teaserContentId){
			  isFound = true;
			  break;
		  }
	  }
	  return isFound;
  }
   
  /**
   * Performs suggestion search and provides a JSON object containing the suggestions.
   *
   * @see "SearchActionState.asHeaderItem.jsp"
   * @see "SearchActionState.asTeaser.jsp"
   */
  @ResponseBody
  @RequestMapping(value = URI_PATTERN, params = {PARAMETER_ROOT_NAVIGATION_ID, PARAMETER_QUERY}, method = RequestMethod.GET, produces = CONTENT_TYPE_JSON)
  public List<Suggestion> handleSearchSuggestionAction(@PathVariable(SEGMENT_ID) CMAction action,
                                               @PathVariable(SEGMENT_ROOT) String context,
                                               @RequestParam(value = PARAMETER_ROOT_NAVIGATION_ID) String rootNavigationId,
                                               @RequestParam(value = PARAMETER_QUERY) String term) {
    if (getValidNavigation(action, context, ACTION_NAME) != null) {
      Collection<String> docTypes = settingsService.settingAsList(DOCTYPE_SELECT, String.class, action);
      Suggestions suggestions = searchService.getAutocompleteSuggestions(rootNavigationId, term, docTypes);

      return suggestions.delegate();
    }
    throw new IllegalArgumentException("Could not resolve navigation for content "+action.getContent().getId());
  }


  @Link(type = SearchActionState.class, uri = URI_PATTERN)
  public UriComponents buildSearchActionLink(SearchActionState action, UriComponentsBuilder uri, Map<String, Object> linkParameters, HttpServletRequest request) {
    Page page = (Page) linkParameters.get("page");
    if (page == null) {
      throw new IllegalArgumentException("Missing 'page' parameter when building link for "+action);
    }
    UriComponentsBuilder result = addLinkParametersAsQueryParameters(uri, linkParameters);
    return result.buildAndExpand(ImmutableMap.of(
            SEGMENT_ID, getId(action.getAction()),
            SEGMENT_ROOT, getPathSegments(page.getNavigation()).get(0)));
  }


  // ==============

  /**
   * Provides the CMNavigation that belongs to the addressed bean
   * @param bean The action bean
   * @param rootSegmentName The name of the root segment
   * @param actionName The action name
   * @return The navigation or null, if the bean and/or the segment name are invalid. This means that it is
   *  an invalid request
   */
  protected Navigation getValidNavigation(ContentBean bean, String rootSegmentName, String actionName) {
    if( !(bean instanceof CMAction) ) {
      // not an action
      return null;
    }
    else if( !actionName.equals(((CMAction) bean).getId())) {
      // the action name is invalid
      return null;
    }

    return getNavigation(asList(rootSegmentName));
  }

  public void setMinimalSearchQueryLength(int minimalSearchQueryLength) {
    this.minimalSearchQueryLength = minimalSearchQueryLength;
  }

}
