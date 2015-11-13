package com.coremedia.livecontext.ecommerce.ibm.search;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.search.SuggestionResult;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class SearchServiceImplTest extends AbstractServiceTest {
  public static final String BEAN_NAME_SEARCH_SERVICE = "searchService";

  SearchServiceImpl testling;

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean(BEAN_NAME_SEARCH_SERVICE, SearchServiceImpl.class);
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
  }

  @Test
  @Betamax(tape = "ssi_testGetAutocompleteSuggestions", match = {MatchRule.path, MatchRule.query})
  public void testGetAutocompleteSuggestions() {
    if (StoreContextHelper.getWcsVersion(StoreContextHelper.getCurrentContext()) < StoreContextHelper.WCS_VERSION_7_7) return;
    List<SuggestionResult> suggestions = testling.getAutocompleteSuggestions("dres");
    assertTrue(!suggestions.isEmpty());
  }
}
