package com.coremedia.blueprint.cae.action.search;

import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.cae.search.Condition;
import com.coremedia.blueprint.cae.search.SearchConstants;
import com.coremedia.blueprint.cae.search.SearchQueryBean;
import com.coremedia.blueprint.cae.search.SearchResultBean;
import com.coremedia.blueprint.cae.search.SearchResultFactory;
import com.coremedia.blueprint.cae.search.Value;
import com.coremedia.blueprint.cae.search.ValueAndCount;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestions;
import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.cap.multisite.SitesService;
import com.google.common.collect.ImmutableMap;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.coremedia.blueprint.testing.ContentTestCaseHelper.getContentBean;
import static com.coremedia.cae.testing.TestInfrastructureBuilder.Infrastructure;
import static com.coremedia.cae.testing.TestInfrastructureBuilder.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SearchService}
 */
public class SearchServiceTest {
  private static final String TERM_NAME = "london";
  private static final Long TERM_COUNT = 1L;
  private static final int ROOT_NAVIGATION_ID = 124;
  private static final int ARTICLE_ID = 4;
  private static final String SEARCHFORMBEAN_QUERY = "sfbQuery";
  private static final String DOCUMENT_TYPE = "CMNavigation";

  private final static SearchFormBean searchFormBean = new SearchFormBean();
  private final static Collection<String> docTypes = Arrays.asList(DOCUMENT_TYPE);

  private static SearchService testling;
  private static CMChannel navigation;
  private static Page page;
  private static SearchResultBean searchResultBean;

  @BeforeClass
  public static void setupBeforeClass() {
    //setup test infrastructure once. Make sure to
    Infrastructure infrastructure = create()
            .withBeans("classpath:/framework/spring/blueprint-search.xml")
            .withContentRepository("classpath:/com/coremedia/blueprint/cae/action/search/searchservice/content.xml")
            .withContentBeanFactory()
            .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
            .withCache()
            .withIdProvider()
            .build();

    //make sure to overwrite the SearchResultFactory in every test with a local factory and a local validator.
    testling = infrastructure.getBean("searchActionService", SearchService.class);

    searchFormBean.setChannelId(String.valueOf(ROOT_NAVIGATION_ID));
    searchFormBean.setDocType(DOCUMENT_TYPE);
    searchFormBean.setQuery(SEARCHFORMBEAN_QUERY);

    searchResultBean = new SearchResultBean();
    searchResultBean.setFacets(ImmutableMap.of(SearchConstants.FIELDS.TEXTBODY.toString(),
            Arrays.asList(new ValueAndCount(TERM_NAME, TERM_COUNT))));
    searchResultBean.setAutocompleteSuggestions(Collections.singletonList(new ValueAndCount(TERM_NAME, TERM_COUNT)));

    CMArticle article = getContentBean(infrastructure, ARTICLE_ID);
    navigation = getContentBean(infrastructure, ROOT_NAVIGATION_ID);

    page = new PageImpl(navigation, article, true, infrastructure.getBean("sitesService", SitesService.class), null);
  }

  @Test
  public void testSearch() {

    //make validator for the LocalSearchResultFactory.
    Validator validator = new Validator() {
      @Override
      public void validate(SearchQueryBean searchQueryBean) {
        assertNotNull("searchQueryBean is null", searchQueryBean);

        //test conditions
        Condition channelCondition = Condition.is(SearchConstants.FIELDS.NAVIGATION_PATHS, Value.exactly("\\/" + ROOT_NAVIGATION_ID));
        Condition docTypeCondition = Condition.is(SearchConstants.FIELDS.DOCUMENTTYPE, Value.exactly(searchFormBean.getDocTypeEscaped()));

        List<Condition> expectedConditions = new ArrayList<>();
        expectedConditions.add(channelCondition);
        expectedConditions.add(docTypeCondition);

        for (Condition condition : expectedConditions) {
          assertTrue("condition not found in searchQueryBean: " + condition.toString(), searchQueryBean.getFilters().contains(condition));
        }
      }
    };

    //overwrite SearchResultFactory with local implementation
    testling.setResultFactory(new LocalSearchResultFactory(validator));

    //result does not matter, all assertions are made in the validator.
    testling.search(page, searchFormBean, docTypes);
  }

  @Test
  public void testSearchTopics() {

    //make validator for the LocalSearchResultFactory.
    Validator validator = new Validator() {
      @Override
      public void validate(SearchQueryBean searchQueryBean) {

        assertNotNull("searchQueryBean is null", searchQueryBean);
        assertEquals("query does not match", "*:*", searchQueryBean.getQuery());

        //test conditions
        String path = navigation.getContent().getPath();
        Condition sitePath = Condition.is(SearchConstants.FIELDS.TAXONOMY_REFERRERS_IN_SITE, Value.exactly(path.substring(0, nthIndexOf(path, '/', 3))));
        Condition topicValueMatches = Condition.is(SearchConstants.FIELDS.TEASER_TITLE, Value.exactly("*" + SEARCHFORMBEAN_QUERY + "*"));
        Condition docTypesCondition = Condition.is(SearchConstants.FIELDS.DOCUMENTTYPE, Value.anyOf(Arrays.asList(DOCUMENT_TYPE)));

        List<Condition> expectedConditions = new ArrayList<>();
        expectedConditions.add(sitePath);
        expectedConditions.add(topicValueMatches);
        expectedConditions.add(docTypesCondition);

        for (Condition condition : expectedConditions) {
          assertTrue("condition not found in searchQueryBean: " + condition.toString(), searchQueryBean.getFilters().contains(condition));
        }
      }
    };

    //overwrite SearchResultFactory with local implementation
    testling.setResultFactory(new LocalSearchResultFactory(validator));

    //result does not matter, all assertions are made in the validator.
    testling.searchTopics(navigation, searchFormBean, docTypes);
  }

  @Test
  public void testAutocompleteSuggestions() {

    //make validator for the LocalSearchResultFactory.
    Validator validator = new Validator() {
      @Override
      public void validate(SearchQueryBean searchQueryBean) {
        assertNotNull("searchQueryBean is null", searchQueryBean);
        Condition condition = searchQueryBean.getFilters().get(0);
        assertEquals("values does not match", "\\/" + ROOT_NAVIGATION_ID, condition.getValue().getValue().toArray()[0]);
        assertEquals("op does not match", Value.Operators.AND, condition.getValue().getOp());
        assertEquals(SearchQueryBean.SEARCH_HANDLER.SUGGEST, searchQueryBean.getSearchHandler());
      }
    };
    //overwrite SearchResultFactory with local implementation
    testling.setResultFactory(new LocalSearchResultFactory(validator));

    Suggestions suggestions = testling.getAutocompleteSuggestions(String.valueOf(ROOT_NAVIGATION_ID), TERM_NAME,
            docTypes);

    assertEquals("originalTerm does not match", TERM_NAME, suggestions.get(0).getValue());
    assertEquals("label does not match", TERM_NAME +" (" +TERM_COUNT + ")", suggestions.get(0).getLabel());

  }

  @Test
  public void testEmptySearch() {

    //create bean with empty query
    SearchFormBean localSearchFormBean = new SearchFormBean();
    localSearchFormBean.setQuery("");

    //result does not matter, all assertions are made in the validator.
    assertNull("return null on empty query",testling.searchTopics(navigation, localSearchFormBean, docTypes));
    assertNull("return null on empty query",testling.search(page, localSearchFormBean, docTypes));
  }


  //====================================================================================================================

  /**
   * Implement this class to validate SearchQueryBeans in every test method.
   */
  private abstract class Validator {

    /**
     * Implement assertions here.
     */
    public abstract void validate(SearchQueryBean searchQueryBean);
  }

  //--------------------------------------------------------------------------------------------------------------------

  /**
   * Determines the index of the nth occurrence of a given character of a string.
   * @param str The string to analyze.
   * @param separator The character to look for.
   * @param n The nth occurrence to look for.
   * @return Index of the nth occurrence of the given character in the given string. -1 if there is no such occurrence.
   */
  private static int nthIndexOf(String str, char separator, int n) {
    int pos = str.indexOf(separator, 0);
    for (int i=1; pos!=-1 && i<n; ++i) {
      pos = str.indexOf(separator, pos+1);
    }
    return pos;
  }

  /**
   * A implementation of a SearchResultFactory that allows validation of the SearchQueryBean build in the various SearchService methods.
   */
  private static class LocalSearchResultFactory implements SearchResultFactory {

    private Validator validator;

    public LocalSearchResultFactory(Validator validator) {
      this.validator = validator;
    }

    @Override
    public SearchResultBean createSearchResult(SearchQueryBean searchInput, long cacheForInSeconds) {
      validator.validate(searchInput);

      return searchResultBean;
    }

    @Override
    public SearchResultBean createSearchResultUncached(SearchQueryBean searchInput) {
      validator.validate(searchInput);

      return searchResultBean;
    }
  }

}
