package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpMethod;

import java.util.*;

public abstract class AbstractWcWrapperService {

  private WcRestConnector restConnector;
  private Map languageMapping;

  private static final String PARAM_CATALOG_ID = "catalogId";
  private static final String PARAM_CONTRACT_ID = "contractId";
  private static final String PARAM_LANG_ID = "langId";
  private static final String PARAM_CURRENCY = "currency";
  private static final String PARAM_FOR_USER = "forUser";
  private static final String PARAM_FOR_USER_ID = "forUserId";

  private static final WcRestConnector.WcRestServiceMethod<Map, Void>
          GET_LANGUAGE_MAPPING = WcRestConnector.createServiceMethod(HttpMethod.GET, "coremedia/languagemap", false, false, Map.class);

  @Required
  public void setRestConnector(WcRestConnector connector) {
    this.restConnector = connector;
  }

  public WcRestConnector getRestConnector() {
    return restConnector;
  }

  public WcRestConnector getCatalogConnector() {
    return restConnector;
  }

  @VisibleForTesting
  public void clearLanguageMapping() {
    languageMapping = null;
  }

  public Map getLanguageMapping() throws CommerceException {
    if (languageMapping == null) {
      //init language mapping
      languageMapping = restConnector.callServiceInternal(GET_LANGUAGE_MAPPING, Collections.<String>emptyList(), null, null, null, null);
    }
    return languageMapping;
  }

  /**
   * Adds the given parameters to a map.
   */
  public Map<String, String[]> createParametersMap(String catalogId, Locale locale, Currency currency, Integer userId, String userName, String[] contractIds) {
    Map<String, String[]> parameters = new TreeMap<>();
    if (catalogId != null) {
      parameters.put(PARAM_CATALOG_ID, new String[]{catalogId});
    }
    if (locale != null) {
      String languageId = getLanguageId(locale);
      parameters.put(PARAM_LANG_ID, new String[]{languageId});
    }
    if (currency != null) {
      parameters.put(PARAM_CURRENCY, new String[]{currency.toString()});
    }
    if (userId != null) {
      parameters.put(PARAM_FOR_USER_ID, new String[]{String.valueOf(userId)});
    }
    else if (userName != null) {
      parameters.put(PARAM_FOR_USER, new String[]{userName});
    }
    else if (contractIds != null) {
      parameters.put(PARAM_CONTRACT_ID, contractIds);
    }
    return parameters;
  }

  /**
   * Convenience method
   */
  public Map<String, String[]> createParametersMap(String catalogId, Locale locale, Currency currency) {
    return createParametersMap(catalogId, locale, currency, null, null, null);
  }

  /**
   * Convenience method
   */
  public Map<String, String[]> createParametersMap(String catalogId, Locale locale, Currency currency, String[] contractIds) {
    return createParametersMap(catalogId, locale, currency, null, null, contractIds);
  }

  /**
   * Gets IBM specific language Id for a given locale String.
   * If a certain mapping does not exist or locale String is invalid, the default "-1" for "en" is returned.
   *
   * @param locale e.g. "en_US" "en" "de"
   */
  public String getLanguageId(Locale locale) {
    String result = "-1"; //default is english
    if (locale != null) {
      String language = locale.getLanguage();
      Map languageMapping = getLanguageMapping();
      if (languageMapping != null) {
        String langId = languageMapping.containsKey(locale.toString()) ?
                (String) languageMapping.get(locale.toString()) :
                (String) languageMapping.get(language);
        if (langId != null) {
          result = langId;
        }
      }
    }
    return result;
  }
}
