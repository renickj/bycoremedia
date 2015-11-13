package com.coremedia.livecontext.ecommerce.ibm.search;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.search.SuggestionResult;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.search.SearchService;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

/**
 * TODO: Description
 * To change this template use File | Settings | File Templates.
 */
public class SearchServiceImpl implements SearchService {
  private WcSearchWrapperService searchWrapperService;

  @Override
  public List<SuggestionResult> getAutocompleteSuggestions(String term) throws CommerceException {
    List<SuggestionResult> result = Collections.emptyList();
    List<WcSuggestion> wcSuggestions = searchWrapperService.
            getKeywordSuggestionsByTerm(term, StoreContextHelper.getCurrentContext());

    if (wcSuggestions != null && !wcSuggestions.isEmpty()) {
      result = new ArrayList<>();
      for (WcSuggestion wcSuggestion : wcSuggestions) {
        result.add(new SuggestionResult(wcSuggestion.getTerm(), term, wcSuggestion.getFrequency()));
      }
    }
    return result;
  }


  public WcSearchWrapperService getSearchWrapperService() {
    return searchWrapperService;
  }

  @Required
  public void setSearchWrapperService(WcSearchWrapperService searchWrapperService) {
    this.searchWrapperService = searchWrapperService;
  }

  @Nonnull
  @Override
  public SearchService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, SearchService.class);
  }
}
