package com.coremedia.livecontext.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.cae.handlers.PreviewHandler;
import com.coremedia.livecontext.context.CategoryInSite;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.handler.ExternalPageHandler;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import com.coremedia.livecontext.product.ProductPageHandler;
import com.coremedia.objectserver.web.links.Link;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Named
@Link
public class CommerceBeanPreviewLinkScheme {

  @Inject
  private ExternalPageHandler externalPageHandler;
  @Inject
  private ProductPageHandler productPageHandler;
  @Inject
  private LiveContextNavigationFactory liveContextNavigationFactory;

  @Link(type = Category.class)
  public Object buildLinkForStudioPreview(Category category, String viewName,
                                          Map<String, Object> linkParameters, HttpServletRequest request) {
    if (isPreviewRequest(request)) {
      CategoryInSite categoryInSite = liveContextNavigationFactory.createCategoryInSite(category, getSiteId());

      return externalPageHandler.buildLinkFor(categoryInSite, viewName, linkParameters, request);
    }
    return null;
  }

  @Link(type = Product.class)
  public Object buildLinkForStudioPreview(Product product, String viewName,
                                          Map<String, Object> linkParameters, HttpServletRequest request) {
    if (isPreviewRequest(request)) {
      ProductInSite productInSite = liveContextNavigationFactory.createProductInSite(product, getSiteId());

      return productPageHandler.buildLinkFor(productInSite, viewName, linkParameters, request);
    }
    return null;
  }

  private static Boolean isPreviewRequest(HttpServletRequest request) {
    return Boolean.valueOf(request.getAttribute(PreviewHandler.REQUEST_ATTR_IS_STUDIO_PREVIEW) + "");
  }

  private static String getSiteId() {
    return Commerce.getCurrentConnection().getStoreContext().getSiteId();
  }

}
