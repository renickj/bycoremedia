package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.catalog.ProductImpl;
import com.coremedia.objectserver.web.links.TokenResolverHelper;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Default implementation for the CommercePropertyProvider, mainly providing formatted not encoded commerce URLs.
 */
public class CommerceUrlPropertyProvider implements CommercePropertyProvider {

  private static final String PARAM_STORE_ID = "storeId";
  private static final String PARAM_CATALOG_ID = "catalogId";
  private static final String PARAM_LANG_ID = "langId";
  private static final String PARAM_LANGUAGE = "language";
  private static final String PARAM_SEO_SEGMENT = "seoSegment";
  private static final String PARAM_STORE_NAME = "storeName";
  private static final String PARAM_SEARCH_TERM = "searchTerm";
  public static final String URL_TEMPLATE = "urlTemplate";
  public static final String STORE_CONTEXT = "storeContext";
  public static final String SEO_SEGMENT = "seoSegment";
  public static final String SEARCH_TERM = "searchTerm";
  public static final String IS_STUDIO_PREVIEW = "isStudioPreview";
  protected static final String NEW_PREVIEW_SESSION_VARIABLE = "newPreviewSession";
  private static final String REDIRECT_URL = "redirectUrl";
  private static final String PARAM_CONTRACT_ID_FOR_PREVIEW = "contractId";
  private static final String PARAM_COMMERCE_BEAN = "commerceBean";

  private String defaultStoreFrontUrl;
  private String previewStoreFrontUrl;
  private String urlPattern;
  private String nonSeoUrlPattern;
  private String shoppingFlowUrlForContractPreview;



  /**
   * The URL template is not mandatory and may be passed with the
   * parameter array of the "provideValue" method.
   */
  public String getUrlPattern() {
      return urlPattern;
    }

  public String getNonSeoUrlPattern() {
    return nonSeoUrlPattern;
  }

  public String getDefaultStoreFrontUrl() {
    return defaultStoreFrontUrl;
  }

  public void setDefaultStoreFrontUrl(String defaultStoreFrontUrl) {
    this.defaultStoreFrontUrl = defaultStoreFrontUrl;
  }

  public String getPreviewStoreFrontUrl() {
    return previewStoreFrontUrl;
  }

  public void setPreviewStoreFrontUrl(String previewStoreFrontUrl) {
    this.previewStoreFrontUrl = previewStoreFrontUrl;
  }

  public void setUrlPattern(String urlPattern) {
    this.urlPattern = urlPattern;
  }

  public void setNonSeoUrlPattern(String nonSeoUrlPattern) {
    this.nonSeoUrlPattern = nonSeoUrlPattern;
  }

  public void setShoppingFlowUrlForContractPreview(String shoppingFlowUrlForContractPreview) {
    this.shoppingFlowUrlForContractPreview = shoppingFlowUrlForContractPreview;
  }

  /**
   * The method expects at least the store context for the URl formatting.
   * Additional optional values may be passed with the array.
   *
   * @param parameters The parameters that are used to format the URL. The following values may be passed:
   *                   <ol>
   *                   <li>StoreContext (optional)</li>
   *                   <li>URL Template (optional)</li>
   *                   <li>URL Parameters Map (optional)</li>
   *                   <li>SEO Segment (optional)</li>
   *                   <li>Search Term (optional)</li>
   *                   <li>StudioPreview Flag(optional)</li>
   *                   </ol>
   */
  @Override
  @SuppressWarnings("unchecked")
  public Object provideValue(Map<String, Object> parameters) {
    String resultUrl = getUrlPattern();
    if (parameters != null) {
      StoreContext storeContext = (StoreContext) parameters.get(STORE_CONTEXT);
      //optional URL template to overwrite the default Spring property
      resultUrl = parameters.get(URL_TEMPLATE) != null ? (String) parameters.get(URL_TEMPLATE) : resultUrl;

      if (resultUrl == null || resultUrl.isEmpty()){
        return resultUrl;
      }

      CommerceBean commerceBean = (CommerceBean) parameters.get(PARAM_COMMERCE_BEAN);
      if(commerceBean instanceof Category){
        resultUrl = getNonSeoUrlPattern();

        Category category = (Category) commerceBean;

        parameters.put("pageType","CategoryDisplay");
        resultUrl = resultUrl.concat("?storeId=" + storeContext.getStoreId())
        .concat("&categoryId="+ category.getId().replaceAll("ibm:///catalog/category/techId:" , ""))
        .concat("&catalogId=" + storeContext.getCatalogId());
      }

      if(commerceBean instanceof Product){
        resultUrl = getNonSeoUrlPattern();

        Product product = (Product) commerceBean;
        Category category = product.getCategory();

        parameters.put("pageType", "ProductDisplay");
        resultUrl = resultUrl.concat("?storeId=" + storeContext.getStoreId())
                .concat("&categoryId="+ category.getId().replaceAll("ibm:///catalog/category/techId:" , ""))
                .concat("&catalogId=" + storeContext.getCatalogId())
                .concat("&productId=" + product.getExternalTechId());
      }

      //compile shopping flow url, if contract ids for preview are stored in storecontext
      if (isContractPreview(parameters)) {
        String redirectUrl = applyParameters(resultUrl, parameters);
        redirectUrl = redirectUrl.startsWith("/") ? redirectUrl.substring(1) : redirectUrl;
        parameters.put(REDIRECT_URL, redirectUrl);
        resultUrl = applyParameters(shoppingFlowUrlForContractPreview, parameters);
        //add contractIds
        resultUrl = UriComponentsBuilder.fromUriString(resultUrl).queryParam(PARAM_CONTRACT_ID_FOR_PREVIEW, storeContext.getContractIdsForPreview()).build().toUriString();
      } else {
        resultUrl = applyParameters(resultUrl, parameters);
      }
    }

    //TODO maybe better no absolute url if fragmentContext available
    //make url absolute
    resultUrl = makeShopUrlAbsolute(resultUrl, parameters);

    // always append "newPreviewSession=true" if request is initial studio preview request
    if (isStudioPreview(parameters)) {
      resultUrl = UriComponentsBuilder.fromUriString(resultUrl).queryParam(NEW_PREVIEW_SESSION_VARIABLE, Boolean.TRUE.toString()).build().toUriString();
    }

    return UriComponentsBuilder.fromUriString(resultUrl).build();
  }

  private String applyParameters(String url, Map<String, Object> parameters) {
    Map<String, Object> parametersMap = new HashMap<>();
    StoreContext storeContext = (StoreContext) parameters.get(STORE_CONTEXT);

    url = CommercePropertyHelper.replaceTokens(url, storeContext);

    parametersMap.put("pageType",parameters.get("pageType") );

    //optional seo segment
    parametersMap.put(PARAM_SEO_SEGMENT, parameters.get(SEO_SEGMENT));

    //optional search term
    parametersMap.put(PARAM_SEARCH_TERM, parameters.get(SEARCH_TERM));

    //optional redirect url
    parametersMap.put(REDIRECT_URL, parameters.get(REDIRECT_URL));

    if (storeContext != null) {
      String storeId = StoreContextHelper.getStoreId(storeContext);
      String catalogId = StoreContextHelper.getCatalogId(storeContext);
      String storeName = StoreContextHelper.getStoreNameInLowerCase(storeContext);
      String languageId = null;

      parametersMap.put(PARAM_STORE_NAME, storeName);

      //the language ID has to be transformed into the format of the commerce system
      Locale locale = StoreContextHelper.getLocale(storeContext);
      if (locale != null) {
        if (getCatalogService() instanceof CatalogServiceImpl) {
          languageId = ((CatalogServiceImpl) getCatalogService()).getLanguageId(locale);
          parametersMap.put(PARAM_LANG_ID, languageId);
        }
        parametersMap.put(PARAM_LANGUAGE, locale.getLanguage());
      }

      if (storeId != null && languageId != null) {
        //The catalog id may be defaulted to the store id if the store is not an e-store.
        if (catalogId == null) {
          catalogId = storeId;
        }

        parametersMap.put(PARAM_STORE_ID, storeId);
        parametersMap.put(PARAM_CATALOG_ID, catalogId);

      }
    }

    url = TokenResolverHelper.replaceTokens(url, parametersMap, false, false);

    return url;
  }

  private String makeShopUrlAbsolute(String url, Map<String, Object> parameters) {
    if (!url.startsWith("http") && !url.startsWith("//")) {
      String prefix = (isStudioPreview(parameters) && getPreviewStoreFrontUrl() != null) ?
              getPreviewStoreFrontUrl() : getDefaultStoreFrontUrl();
      //avoid "//" in concatenated urls
      if (!prefix.endsWith("/")) {
        prefix += "/";
      }
      if (url.startsWith("/")) {
        url = url.substring(1);
      }

      url = prefix + url;
    }
    return url;
  }

  protected boolean isStudioPreview(Map<String, Object> parameters) {
    return parameters != null && parameters.get(IS_STUDIO_PREVIEW) != null && (boolean) parameters.get(IS_STUDIO_PREVIEW);
  }

  protected boolean isContractPreview(Map<String, Object> parameters) {
    if (parameters == null) {
      return false;
    }
    StoreContext storeContext = (StoreContext) parameters.get(STORE_CONTEXT);
    return storeContext != null && isStudioPreview(parameters) && storeContext.getContractIdsForPreview() != null;
  }

  public CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }
}
