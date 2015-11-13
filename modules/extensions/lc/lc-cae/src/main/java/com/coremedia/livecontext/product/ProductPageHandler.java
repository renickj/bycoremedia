package com.coremedia.livecontext.product;

import com.coremedia.blueprint.base.links.PostProcessorPrecendences;
import com.coremedia.blueprint.cae.contentbeans.PageImpl;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.contentbeans.CMProductTeaser;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.coremedia.objectserver.web.links.LinkPostProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.ContentTypes.CONTENT_TYPE_HTML;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_SEGMENTS;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_SERVICE;
import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.VIEW_PARAMETER;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_REST;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper.getCurrentCommerceIdProvider;
import static java.util.Objects.requireNonNull;

@Link
@RequestMapping
@LinkPostProcessor
public class ProductPageHandler extends LiveContextPageHandlerBase {
  public static final String LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS = "livecontext.policy.commerce-product-links";
  public static final String LIVECONTEXT_POLICY_COMMERCE_SEO_LINKS = "livecontext.policy.commerce-seo-links";


  private static final String SEGMENT_PRODUCT = "product";
  private static final String PRODUCT_PATH_VARIABLE = "productPath";
  private static final String SITE_CHANNEL_ID = "siteChannelID";
  private static final String PRODUCT_SEO_SEGMENT = "productSeoSegment";
  private static final String PRODUCT_QUICKINFO_SEGMENT = "productQuickinfo";
  private static final String QUICKINFO_VIEW = "asQuickinfo";

  public static final String URI_PATTERN =
          "/" + SEGMENT_PRODUCT +
                  "/{" + SHOP_NAME_VARIABLE + "}" +
                  "/{" + PRODUCT_PATH_VARIABLE + ":" + PATTERN_SEGMENTS + "}";

  public static final String REST_URI_PATTERN = '/' + PREFIX_SERVICE +
          '/' + SEGMENT_REST +
          "/{" + SITE_CHANNEL_ID +
          "}/" + PRODUCT_QUICKINFO_SEGMENT +
          "/{" + PRODUCT_SEO_SEGMENT + "}";

  // --- Handler ----------------------------------------------------

  @RequestMapping({URI_PATTERN})
  public ModelAndView handleRequest(@PathVariable(SHOP_NAME_VARIABLE) String shopSegment,
                                    @PathVariable(PRODUCT_PATH_VARIABLE) String seoSegment,
                                    @RequestParam(value = VIEW_PARAMETER, required = false) String view) {
    // This handler is only responsible for CAE product links.
    // If the application runs in wcsProductLinks mode, we render native
    // WCS links, and this kind of link cannot occur.
    Site site = getSiteResolver().findSiteBySegment(shopSegment);
    if (useCommerceProductLinks(site)) {
      return HandlerHelper.notFound("Unsupported link format");
    }
    if (StringUtils.isEmpty(seoSegment)) {
      return HandlerHelper.notFound("No product path found");
    }
    return createLiveContextPage(site, seoSegment, view);
  }

  @RequestMapping(value = REST_URI_PATTERN, produces = CONTENT_TYPE_HTML, method = RequestMethod.GET)
  @ResponseBody
  public ModelAndView getProducts(@PathVariable(SITE_CHANNEL_ID) CMNavigation context,
                                  @PathVariable(PRODUCT_SEO_SEGMENT) String productId) {
    Product product = getCatalogService().findProductBySeoSegment(productId);
    Site site = requireNonNull(getSitesService().getContentSiteAspect(context.getContent()).getSite(), "Site for context does not exist");
    ProductInSite productInSite = getLiveContextNavigationFactory().createProductInSite(product, site.getId());
    ModelAndView modelAndView = HandlerHelper.createModelWithView(productInSite, QUICKINFO_VIEW);

    Page page = asPage(context, context);
    modelAndView.addObject("cmpage", page);
    //we need to apply the navigation here, otherwise the template lookup can't decide which context to use
    NavigationLinkSupport.setNavigation(modelAndView, page.getNavigation().getRootNavigation());

    return modelAndView;
  }


  // --- Linkscheme -------------------------------------------------

  /**
   * In default mode (wcsProductLinks==true) buildLinkFor builds native
   * WCS links which have no handler counterpart in the CAE.
   * In !wcsProductLinks mode buildLinkFor builds CAE links
   * which are handled by {@link #handleRequest(String, String, String)}.
   */
  @Link(type = ProductInSite.class)
  public Object buildLinkFor(ProductInSite productInSite, String viewName, Map<String, Object> linkParameters, HttpServletRequest request) {
    Site site = productInSite.getSite();
    Product product = productInSite.getProduct();
    if (useCommerceProductLinks(site) ){
      if(useCommerceSeoLinks(site)){
        return buildCommerceLinkFor(product, linkParameters);
      }else{
        return buildCommerceLinkFor(null, product, linkParameters);
      }
    }
    else{
      return buildCaeLinkFor(productInSite, viewName, linkParameters);
    }


  }

  /**
   * This link is built when the product teaser is inside a rich text.
   * We use the ProductInPage link building logic here.
   */
  @Link(type = CMProductTeaser.class, view = HandlerHelper.VIEWNAME_DEFAULT)
  public Object buildLinkFor(CMProductTeaser productTeaser, String viewName, Map<String, Object> linkParameters, HttpServletRequest request) {
    ProductInSite productInSite = productTeaser.getProductInSite();
    if (productInSite != null) {
      return buildLinkFor(productInSite, viewName, linkParameters, request);
    }
    return null;
  }

  @LinkPostProcessor(type = ProductInSite.class, order = PostProcessorPrecendences.MAKE_ABSOLUTE)
  public Object makeAbsoluteUri(UriComponents originalUri, ProductInSite product, Map<String,Object> linkParameters, HttpServletRequest request) {
    // Native product links are absolute anyway, nothing more to do here.
    Site site = product.getSite();
    return useCommerceProductLinks(site) ? originalUri : absoluteUri(originalUri, product, product.getSite(), linkParameters, request);
  }


  // --- internal ---------------------------------------------------

  private boolean useCommerceProductLinks(Site site) {
    return getSettingsService().settingWithDefault(LIVECONTEXT_POLICY_COMMERCE_PRODUCT_LINKS, Boolean.class, true, site);
  }

  private ModelAndView createLiveContextPage(@Nonnull Site site, @Nonnull String seoSegment, String view) {
    StoreContext shopContext = getStoreContextProvider().findContextBySite(site);
    Navigation context = getNavigationContext(site, seoSegment);

    Product product = getCatalogService().findProductById(
            getCurrentCommerceIdProvider().formatProductSeoSegment(seoSegment));
    ProductInSite productInSite = getLiveContextNavigationFactory().createProductInSite(product, site.getId());
    PageImpl page = new PageImpl(context, productInSite, true, getSitesService(), getCache());
    page.setTitle(product.getTitle());
    page.setDescription(product.getTitle());
    page.setKeywords(product.getMetaKeywords());
    return createModelAndView(page, view);
  }

  private UriComponents buildCaeLinkFor(ProductInSite productInSite, String viewName, Map<String, Object> linkParameters) {
    String siteSegment = getSiteSegment(productInSite.getSite());
    String productSegment = productInSite.getProduct().getSeoSegment();
    UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .newInstance()
            .pathSegment(SEGMENT_PRODUCT)
            .pathSegment(siteSegment)
            .pathSegment(productSegment);
    addViewAndParameters(uriBuilder, viewName, linkParameters);
    return uriBuilder.build();
  }

  public boolean useCommerceSeoLinks(Site site){
    return getSettingsService().settingWithDefault(LIVECONTEXT_POLICY_COMMERCE_SEO_LINKS, Boolean.class, true, site);
  }

}
