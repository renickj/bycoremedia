package com.coremedia.livecontext.ecommerce.ibm.search;

import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.springframework.http.HttpMethod;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCatalogId;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Arrays.asList;

/**
 * TODO: Description
 * To change this template use File | Settings | File Templates.
 */
public class WcSearchWrapperService extends AbstractWcWrapperService {

  private static final WcRestConnector.WcRestServiceMethod<WcSuggestionViews, Void>
          GET_KEYWORD_SUGGESTIONS = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/sitecontent/keywordSuggestionsByTerm/{term}", false, false, true, WcSuggestionViews.class);


  public List<WcSuggestion> getKeywordSuggestionsByTerm(String term, StoreContext storeContext) {
    try {
      Map<String, String[]> parametersMap = createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext));
      parametersMap.put("catalogId", new String[]{getStoreId(storeContext)});
      WcSuggestionViews suggestionViews = getRestConnector().callService(
              GET_KEYWORD_SUGGESTIONS, asList(getStoreId(storeContext), term), parametersMap, null, storeContext, null);
      if (suggestionViews != null && suggestionViews.getSuggestionView().get(0) != null) {
        return suggestionViews.getSuggestionView().get(0).getEntry();
      }
      return Collections.emptyList();
    } catch (CommerceRemoteException e) {
      if (e.getRemoteError() != null && "TODO".equals(e.getRemoteError().getErrorKey())) {  //TODO
        return Collections.emptyList();
      } else {
        throw e;
      }
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }


}
