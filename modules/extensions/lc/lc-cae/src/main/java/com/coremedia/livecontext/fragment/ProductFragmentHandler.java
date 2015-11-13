package com.coremedia.livecontext.fragment;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.ResolveContextStrategy;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.objectserver.web.HandlerHelper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

/**
 * A fragment handler that handles all fragment requests that within the context of a given product. It will fall back
 * to the sites root channel if no context could be found for the given category.
 */
public class ProductFragmentHandler extends FragmentHandler {

  private static final String AS_ASSETS_VIEW = "asAssets";
  private static final String ORIENTATION_PARAM_NAME = "orientation";
  private static final String TYPES_PARAM_NAME = "types";

  private ResolveContextStrategy contextStrategy;
  private CommerceBeanFactory commerceBeanFactory;

  /**
   * Renders the complete context (which is a CMChannel) of the given <code>product</code> using the given <code>view</code>.
   * If no context can be found for the product, the <code>view</code> of the root channel will be rendered. The site
   * is determined by the tuple <code>(storeId, locale)</code>, which must be unique across all sites.
   *
   * @return the {@link ModelAndView model and view} containing the {@link com.coremedia.blueprint.common.contentbeans.Page page}
   * as <code>self</code> object, that contains the context (CMChannel) that shall be rendered.
   */
  @Override
  public ModelAndView createModelAndView(FragmentParameters params, HttpServletRequest request) {
    Site site = SiteHelper.getSiteFromRequest(request);
    if (site != null) {
      String externalTechId = params.getProductId();
      String view = params.getView();
      String placement = params.getPlacement();

      Navigation navigation = contextStrategy.resolveContext(site, externalTechId);
      if (navigation != null) {
        Content rootChannelContent = site.getSiteRootDocument();
        CMChannel rootChannel = getContentBeanFactory().createBeanFor(rootChannelContent, CMChannel.class);

        if (StringUtils.isEmpty(placement)) {
          if (view != null && view.equals(AS_ASSETS_VIEW)) {
            String orientation = extractParameterValue(params.getParameter(), ORIENTATION_PARAM_NAME);
            String types = extractParameterValue(params.getParameter(), TYPES_PARAM_NAME);
            return createModelAndViewForProductPage(navigation, externalTechId, view, orientation, types);
          }
          return createFragmentModelAndView(navigation, view, rootChannel);
        }
        return createFragmentModelAndViewForPlacementAndView(navigation, placement, view, rootChannel);
      }
    }

    throw new IllegalStateException("ProductFragmentHandler did not find a navigation for storeId \"" + params.getStoreId() +
            "\", locale \"" + params.getLocale() + "\", category id \"" + params.getCategoryId() + "\"");
  }

  @Nonnull
  protected ModelAndView createModelAndViewForProductPage(Navigation navigation, String productId, String view, String orientation, String types) {

    Product product = null;
    if (!StringUtils.isEmpty(productId)) {
      String externalTechId = Commerce.getCurrentConnection().getIdProvider().formatProductTechId(productId);
      product = (Product) commerceBeanFactory.loadBeanFor(externalTechId, getStoreContextProvider().getCurrentContext());
      if (product.isVariant()) {
        externalTechId = Commerce.getCurrentConnection().getIdProvider().formatProductVariantTechId(productId);
        product = (ProductVariant) commerceBeanFactory.loadBeanFor(externalTechId, getStoreContextProvider().getCurrentContext());
      }
    }

    ModelAndView modelAndView = HandlerHelper.createModelWithView(product, view);
    if (!StringUtils.isEmpty(orientation)) {
      modelAndView.addObject("orientation", orientation);
    }
    if (!StringUtils.isEmpty(types)) {
      modelAndView.addObject("types", types);
    }

    Page page = asPage(navigation, navigation);
    addPageModel(modelAndView, page);

    return modelAndView;
  }

  private String extractParameterValue(String parameters, String parameterName) {
    if (StringUtils.isEmpty(parameters)) {
      return parameters;
    }
    String[] params = parameters.split(",");
    for (String param : params) {
      String[] keyValue = param.split("=");
      if (keyValue.length == 2 && parameterName.equals(keyValue[0])) {
        return keyValue[1];
      }
    }
    return null;
  }

  @Override
  public boolean include(FragmentParameters params) {
    return !StringUtils.isEmpty(params.getProductId()) && (StringUtils.isEmpty(params.getExternalRef()) || !params.getExternalRef().startsWith("cm-"));
  }

  private StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  // ------------ Config --------------------------------------------

  @Required
  public void setContextStrategy(ResolveContextStrategy contextStrategy) {
    this.contextStrategy = contextStrategy;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }
}
