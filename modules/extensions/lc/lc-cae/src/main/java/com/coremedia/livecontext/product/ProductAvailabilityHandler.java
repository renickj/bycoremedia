package com.coremedia.livecontext.product;

import com.coremedia.blueprint.cae.handlers.PageHandlerBase;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.Map;
import java.util.Set;

import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static com.coremedia.blueprint.base.links.UriConstants.Views.VIEW_FRAGMENT;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper.getCurrentCommerceIdProvider;

/**
 * This handler generates and handles URLs to retrieve the availability of {@link Product} and {@link ProductVariant}
 * contained in a {@link ProductInSite}. The availability will be rendered as a fragment.
 */
@Link
@RequestMapping
public class ProductAvailabilityHandler extends PageHandlerBase {

  private static final String PATH_VARIABLE_NAME_SHOP_NAME = "shopName";
  private static final String PATH_VARIABLE_NAME_PRODUCT_TYPE = "productType";
  private static final String PATH_VARIABLE_NAME_EXTERNAL_ID = "externalId";

  protected static final String URI_PATTERN = "/" + PREFIX_DYNAMIC +
          '/' + SEGMENTS_FRAGMENT
          + "/productavailability/{"
          + PATH_VARIABLE_NAME_SHOP_NAME
          + "}/{" + PATH_VARIABLE_NAME_PRODUCT_TYPE
          + "}/{" + PATH_VARIABLE_NAME_EXTERNAL_ID + "}";

  private SitesService siteService;

  @Link(type = ProductInSite.class, uri = URI_PATTERN, view = VIEW_FRAGMENT)
  public UriComponents buildLinkFor(ProductInSite productInSite, UriTemplate uriTemplate, Map<String, Object> linkParameters) {
    UriComponentsBuilder result = UriComponentsBuilder.fromPath(uriTemplate.toString());
    String siteName = getContentLinkBuilder().getVanityName(productInSite.getSite().getSiteRootDocument());

    result = addLinkParametersAsQueryParameters(result, linkParameters);
    return result.buildAndExpand(ImmutableMap.of(
            PATH_VARIABLE_NAME_EXTERNAL_ID, productInSite.getProduct().getExternalId(),
            PATH_VARIABLE_NAME_PRODUCT_TYPE, productInSite.getProduct() instanceof ProductVariant ? "variant" : "product",
            PATH_VARIABLE_NAME_SHOP_NAME, siteName
    ));
  }

  @RequestMapping(value = URI_PATTERN)
  public ModelAndView handleDynamicFragmentRequest(@PathVariable(PATH_VARIABLE_NAME_SHOP_NAME) String shopName,
                                                   @PathVariable(PATH_VARIABLE_NAME_PRODUCT_TYPE) String productType,
                                                   @PathVariable(PATH_VARIABLE_NAME_EXTERNAL_ID) String productId,
                                                   @RequestParam(value = "targetView", required = false) String view) {
    Site currentSite = getSite(shopName);

    Navigation navigationContext = getNavigation(shopName);
    //TODO handle context not found i.e. storeContext == null
    StoreContext storeContext = getStoreContextProvider().findContextBySite(currentSite);

    ModelAndView model;
    if ("variant".equals(productType)) {
      ProductVariant productVariant = getCatalogService().findProductVariantById(getCurrentCommerceIdProvider().formatProductVariantId(productId));
      model = HandlerHelper.createModelWithView(productVariant, view);
    } else {
      Product product = getCatalogService().findProductById(
              getCurrentCommerceIdProvider().formatProductId(productId));
      model = HandlerHelper.createModelWithView(product, view);
    }
    NavigationLinkSupport.setNavigation(model, navigationContext);
    return model;
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  public CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }

  @Required
  public void setSiteService(SitesService siteService) {
    this.siteService = siteService;
  }

  /**
   * Looks up a site by the URL segment
   */
  protected Site getSite(String siteSegment) {
    Set<Site> sites = siteService.getSites();
    for (Site site : sites) {
      Content rootChannel = site.getSiteRootDocument();
      String vanityName = getContentLinkBuilder().getVanityName(rootChannel);
      if (siteSegment.equals(vanityName)) {
        return site;
      }
    }
    return null;
  }
}
