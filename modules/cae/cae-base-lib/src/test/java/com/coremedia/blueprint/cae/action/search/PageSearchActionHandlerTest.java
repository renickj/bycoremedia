package com.coremedia.blueprint.cae.action.search;

import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestion;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestions;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.multisite.SitesService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.cae.action.search.PageSearchActionHandler.ACTION_NAME;
import static com.coremedia.blueprint.cae.action.search.PageSearchActionHandler.PARAMETER_QUERY;
import static com.coremedia.blueprint.cae.action.search.PageSearchActionHandler.PARAMETER_ROOT_NAVIGATION_ID;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.checkError;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.checkPage;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.formatLink;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.handlerInfrastructureBuilder;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.initInfrastructure;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.request;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_SERVICE;
import static com.coremedia.blueprint.testing.ContentTestCaseHelper.getContentBean;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link PageSearchActionHandler}
 */
public class PageSearchActionHandlerTest {

  private static final String URI = '/'+ PREFIX_SERVICE+"/search/root/22";
  private static final String WRONG_URI = '/'+ PREFIX_SERVICE+"/search/wrongSegment/22";
  private static final String QUERY = "testQuery";
  private static final int ROOT_NAVIGATION_ID = 4;
  private static final int SEARCH_PAGE_RESULT_ID = 24;
  private static final int ACTION_ID = 22;
  private static final ImmutableMap<String, String> AUTOCOMPLETE_PARAMS = ImmutableMap.of(
          PARAMETER_ROOT_NAVIGATION_ID, String.valueOf(ROOT_NAVIGATION_ID),
          PARAMETER_QUERY, QUERY
  );
  private static final String LONG_QUERY = "myLongEnoughQuery";
  private static final String SHORT_QUERY = "a";

  private static TestInfrastructureBuilder.Infrastructure infrastructure;
  private static SearchActionState searchActionState;
  private static CMAction action;
  private static Page page;
  private static SearchResultBean resultBean;
  private static Suggestions result;
  private static Suggestion suggestion = new Suggestion("a", "a", 1L);
  private static List<Suggestion> suggestionList = ImmutableList.of(suggestion);

  @BeforeClass
  public static void setupBeforeClass() {

    //replace SearchService with mocked version so that this test really only tests PageSearchActionHandler and can
    //check whether the objects returned by the SearchService are correctly merged into the ModelAndView
    SearchService searchService = mock(SearchService.class);
    resultBean = new SearchResultBean();
    when(searchService.search(any(Page.class), any(SearchFormBean.class), org.mockito.Matchers.<Collection<String>>any()))
            .thenReturn(resultBean);
    result = new Suggestions();
    result.addAll(suggestionList);
    when(searchService.getAutocompleteSuggestions(eq(String.valueOf(ROOT_NAVIGATION_ID)), eq(QUERY),
            org.mockito.Matchers.<Collection<String>>any())).thenReturn(result);

    //setup infrastructure
    infrastructure = handlerInfrastructureBuilder("classpath:/com/coremedia/blueprint/cae/action/search/pagesearchactionhandler/content.xml")
            .withViewResolver()
            .withBeans("classpath:/framework/spring/blueprint-search.xml")
            .withBeans("classpath:/framework/spring/errorhandling.xml")
            .withBean("searchActionService", searchService)
            .build();
    initInfrastructure(infrastructure);

    //create needed ContentBeans
    CMChannel navigation = getContentBean(infrastructure, ROOT_NAVIGATION_ID);
    action = getContentBean(infrastructure, ACTION_ID);
    page = new PageImpl(navigation, action, true, infrastructure.getBean("sitesService", SitesService.class), null);

    //create SearchActionState for linkscheme test
    PageSearchActionHandler testling = infrastructure.getBean("pageSearchActionHandler", PageSearchActionHandler.class);
    searchActionState = testling.createActionState(action, null);
  }

  /**
   * Tests {@link PageSearchActionHandler#handleSearchAction(CMAction, String, SearchFormBean)} with a query that is long enough.
   * Expects a successful search.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSearch() throws Exception {

    //simulate the HTML search form, add to request
    Map<String,String> parameters = ImmutableMap.of(PARAMETER_QUERY, LONG_QUERY);
    ModelAndView modelAndView = request(infrastructure, URI, parameters);

    checkPage(modelAndView, SEARCH_PAGE_RESULT_ID, SEARCH_PAGE_RESULT_ID);

    // check that an action result has been registered
    SearchActionState actionResult = (SearchActionState) modelAndView.getModel().get("substitution." + PageSearchActionHandler.ACTION_ID);
    assertNotNull("form", actionResult.getForm());
    assertEquals("result", resultBean, actionResult.getResult());
    assertEquals("action", action, actionResult.getAction());
  }

  /**
   * Tests {@link PageSearchActionHandler#handleSearchAction(CMAction, String, SearchFormBean)} with a short query.
   * Expects an errormessage in the {@link SearchActionState}
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSearchWithShortQuery() throws Exception {

    //simulate the HTML search form, add to request
    Map<String,String> parameters = ImmutableMap.of(PARAMETER_QUERY, SHORT_QUERY);

    ModelAndView modelAndView = request(infrastructure, URI, parameters);

    checkPage(modelAndView, SEARCH_PAGE_RESULT_ID, SEARCH_PAGE_RESULT_ID);

    // check that an action result has been registered
    SearchActionState actionResult = (SearchActionState) modelAndView.getModel().get("substitution." + PageSearchActionHandler.ACTION_ID);
    assertNotNull("form", actionResult.getForm());
    assertNull("result", actionResult.getResult());
    assertTrue("query too short", actionResult.isQueryTooShort());
    assertEquals("action", action, actionResult.getAction());
  }

  /**
   * Tests {@link PageSearchActionHandler#handleSearchSuggestionAction(com.coremedia.blueprint.common.contentbeans.CMAction, String, String, String)}
   */
  @Test
  public void testSuggestion() throws Exception {
    MockHttpServletResponse response = new MockHttpServletResponse();

    //output is written directly to the response, no MaV is returned.
    request(infrastructure, URI, AUTOCOMPLETE_PARAMS, null, PageSearchActionHandler.CONTENT_TYPE_JSON, response);

    String actual = response.getContentAsString();

    assertThat(actual, org.hamcrest.Matchers.allOf(containsString("label"), containsString("value")));
  }

  /**
   * Test "not found" for a non-existent root segment on both handler methods
   * {@link PageSearchActionHandler#handleSearchAction(CMAction, String, SearchFormBean)}
   * {@link PageSearchActionHandler#handleSearchSuggestionAction(com.coremedia.blueprint.common.contentbeans.CMAction, String, String, String)}
   */
  @Test
  public void testNotFoundForUnknownRootSegment() throws Exception {

    ModelAndView modelAndView = request(infrastructure, WRONG_URI);
    checkError(modelAndView, SC_NOT_FOUND);

    ModelAndView modelAndView2 = request(infrastructure, WRONG_URI, AUTOCOMPLETE_PARAMS, null, PageSearchActionHandler.CONTENT_TYPE_JSON);
    checkError(modelAndView2, SC_NOT_FOUND);
  }

  /**
   * Test generation of specific action URL.
   */
  @Test
  public void testGenerateActionLink() {

    Map<String, Object> parameters = ImmutableMap.of("action", ACTION_NAME, "page", page);

    String url = formatLink(infrastructure, parameters, searchActionState);

    assertEquals("wrong uri", URI, url);
  }
}
