package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.links.UrlPrefixResolver;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.handlers.PageHandlerBase;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.context.ResolveContextStrategy;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.coremedia.objectserver.web.links.UriComponentsHelper;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponents;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Links.ABSOLUTE_URI_KEY;
import static com.coremedia.blueprint.base.links.UriConstants.Links.SCHEME_KEY;

public class LiveContextPageHandlerBase extends PageHandlerBase {
  protected static final String SHOP_NAME_VARIABLE = "shop";
  public static final String URL_PROVIDER_URL_TEMPLATE = "urlTemplate";
  public static final String URL_PROVIDER_STORE_CONTEXT = "storeContext";
  public static final String URL_PROVIDER_QUERY_PARAMS = "queryParams";
  public static final String URL_PROVIDER_SEO_SEGMENT = "seoSegment";
  public static final String URL_PROVIDER_IS_STUDIO_PREVIEW = "isStudioPreview";
  public static final String HAS_PREVIEW_TOKEN = "hasPreviewToken";
  public static final String URL_PROVIDER_SEARCH_TERM = "searchTerm";
  public static final String URL_PROVIDER_COMMERCE_BEAN = "commerceBean";

  private ResolveContextStrategy resolveContextStrategy;
  private LiveContextNavigationFactory liveContextNavigationFactory;
  private UrlPrefixResolver urlPrefixResolver;
  private LiveContextSiteResolver siteResolver;
  private SettingsService settingsService;
  private ContentRepository contentRepository;

  private int wcsStorefrontMaxUrlSegments = 2;
  private CommercePropertyProvider urlProvider;
  private LinkFormatter linkFormatter;

  // --- construct and configure ------------------------------------

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setResolveContextStrategy(ResolveContextStrategy resolveContextStrategy) {
    this.resolveContextStrategy = resolveContextStrategy;
  }

  @Required
  public void setUrlProvider(CommercePropertyProvider urlProvider) {
    this.urlProvider = urlProvider;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Required
  public void setLiveContextNavigationFactory(LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  @Required
  public void setUrlPrefixResolver(UrlPrefixResolver urlPrefixResolver) {
    this.urlPrefixResolver = urlPrefixResolver;
  }

  @Required
  public void setSiteResolver(LiveContextSiteResolver siteResolver) {
    this.siteResolver = siteResolver;
  }

  public void setWcsStorefrontMaxUrlSegments(int wcsStorefrontMaxUrlSegments) {
    this.wcsStorefrontMaxUrlSegments = wcsStorefrontMaxUrlSegments;
  }

  // --- features ---------------------------------------------------

  protected StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  protected SettingsService getSettingsService() {
    return settingsService;
  }

  protected CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }

  protected LiveContextNavigation getNavigationContext(Site site, String seoSegment) {
    try {
      return resolveContextStrategy.resolveContext(site, seoSegment);
    } catch (Exception e) {
      // Do not log, means actually just "not found", does not indicate a problem.
      return null;
    }
  }

  protected LiveContextNavigationFactory getLiveContextNavigationFactory() {
    return liveContextNavigationFactory;
  }

  protected LiveContextSiteResolver getSiteResolver() {
    return siteResolver;
  }

  protected UriComponents absoluteUri(UriComponents originalUri, Object bean, Site site, Map<String,Object> linkParameters, ServletRequest request) {
    if (!isAbsoluteUrlRequested(request)) {
      return originalUri;
    }
    String siteId = site.getId();
    String absoluteUrlPrefix = urlPrefixResolver.getUrlPrefix(siteId, bean, null);
    if (absoluteUrlPrefix == null) {
      throw new IllegalStateException("Cannot calculate an absolute URL for " + bean);
    } else if(!StringUtils.isBlank(absoluteUrlPrefix)) {
      //explicitly set scheme if it is set in link parameters
      String scheme = null;
      if(linkParameters != null) {
        Object schemeAttribute = linkParameters.get(SCHEME_KEY);
        if(schemeAttribute != null) {
          scheme = (String) schemeAttribute;
        }
      }
      return UriComponentsHelper.prefixUri(absoluteUrlPrefix, scheme , originalUri);
    }

    return UriComponentsHelper.prefixUri(absoluteUrlPrefix, null, originalUri);
  }

  protected Object buildCommerceLinkFor(String urlTemplate, String seoSegments, Map<String, ?> queryParams) {
    StoreContext currentContext = Commerce.getCurrentConnection().getStoreContext();
    Map<String, Object> newQueryParams = new HashMap<>(queryParams);

    Map<String,Object> params = new HashMap<>();
    params.put(URL_PROVIDER_URL_TEMPLATE, urlTemplate);
    params.put(URL_PROVIDER_STORE_CONTEXT, currentContext);
    params.put(URL_PROVIDER_QUERY_PARAMS, newQueryParams);
    params.put(URL_PROVIDER_SEO_SEGMENT, seoSegments);
    params.put(URL_PROVIDER_IS_STUDIO_PREVIEW, isStudioPreview());

    return urlProvider.provideValue(params);
  }

  protected Object buildCommerceLinkFor(String urlTemplate,CommerceBean commerceBean, Map<String, ?> queryParams) {
    StoreContext currentContext = Commerce.getCurrentConnection().getStoreContext();
    Map<String, Object> newQueryParams = new HashMap<>(queryParams);

//    String seoSegments = buildSeoSegmentsFor(commerceBean);

    Map<String,Object> params = new HashMap<>();
    params.put(URL_PROVIDER_URL_TEMPLATE, urlTemplate);
    params.put(URL_PROVIDER_STORE_CONTEXT, currentContext);
    params.put(URL_PROVIDER_QUERY_PARAMS, newQueryParams);
//    params.put(URL_PROVIDER_SEO_SEGMENT, seoSegments);
    params.put(URL_PROVIDER_COMMERCE_BEAN, commerceBean);
    params.put(URL_PROVIDER_IS_STUDIO_PREVIEW, isStudioPreview());

    return urlProvider.provideValue(params);
  }





  /**
   * Builds complete, absolute WCS links with query parameters.
   * Do not postprocess.
   */
  protected Object buildCommerceLinkFor(CommerceBean commerceBean, Map<String, ?> queryParams) {
    String seoSegments = buildSeoSegmentsFor(commerceBean);
    return buildCommerceLinkFor(null, seoSegments, queryParams);
  }

  protected boolean isPreview() {
    return contentRepository.isContentManagementServer();
  }

  protected boolean isStudioPreview() {
    if(isPreview()) {
      return isStudioPreviewRequest();
    }

    return false;
  }

  public static boolean isStudioPreviewRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    return isTrue(requestAttributes.getAttribute(PreviewHandler.REQUEST_ATTR_IS_STUDIO_PREVIEW, 0))
            ||
            isTrue(requestAttributes.getAttribute(HAS_PREVIEW_TOKEN, 0));
  }

  private static boolean isTrue(Object attribute) {
    return Boolean.valueOf(attribute + "");
  }

  protected String getSiteSegment(Site site) {
    return getContentLinkBuilder().getVanityName(site.getSiteRootDocument());
  }

  protected String applyLinkTransformers(String source, HttpServletRequest request, HttpServletResponse response, boolean forRedirect) {
    String result = source;
    if (linkFormatter != null && source != null) {
      List<LinkTransformer> transformers = linkFormatter.getTransformers();
      for (LinkTransformer transformer : transformers) {
        result = transformer.transform(result, null, null, request, response, true);
      }
    }
    return result;
  }

  // --- internal ---------------------------------------------------

  /**
   * Return the SEO URL for the given commerce bean.
   */
  private String buildSeoSegmentsFor(CommerceBean commerceBean) {
    StringBuilder segments = new StringBuilder();
    if (commerceBean instanceof Category) {
      Category category = (Category) commerceBean;
      segments.append(buildSeoBreadCrumbs(category));
    } else if (commerceBean instanceof Product) {
      Product product = (Product) commerceBean;
      segments.append(buildSeoBreadCrumbs(product.getCategory()));
      segments.append(product.getSeoSegment());
    }

    return segments.toString();
  }

  /**
   * This method returns the string
   * with the whole category path of the current category starting with the top level category and ending with the
   * current category + '/'.
   */
  private String buildSeoBreadCrumbs(Category category) {
    StringBuilder segments = new StringBuilder();
    List<Category> breadcrumb = category.getBreadcrumb();
    if (breadcrumb.size() > wcsStorefrontMaxUrlSegments) {
      breadcrumb = breadcrumb.subList(breadcrumb.size() - wcsStorefrontMaxUrlSegments, breadcrumb.size());
    }
    for (Category c : breadcrumb) {
      segments.append(c.getSeoSegment());
      segments.append('/');
    }
    return segments.toString();
  }

  @VisibleForTesting
  SecurityContext getSecurityContext() {
    return SecurityContextHolder.getContext();
  }

  //====================================================================================================================

  private static boolean isAbsoluteUrlRequested(ServletRequest request) {
    Object absolute = request.getAttribute(ABSOLUTE_URI_KEY);
    return "true".equals(absolute) || Boolean.TRUE.equals(absolute);
  }

  public void setLinkFormatter(LinkFormatter linkFormatter) {
    this.linkFormatter = linkFormatter;
  }
}
