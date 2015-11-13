package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.common.contentbeans.CMPlaceholder;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.elastic.social.cae.controller.AbstractReviewsResultHandler;
import com.coremedia.blueprint.elastic.social.cae.controller.ReviewsResult;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.social.api.ContributionType;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.livecontext.context.ResolveContextStrategy;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.objectserver.view.substitution.Substitution;
import com.coremedia.objectserver.web.links.Link;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.TARGETVIEW_PARAMETER;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static com.coremedia.blueprint.base.links.UriConstants.Views.VIEW_FRAGMENT;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper.getCurrentCommerceIdProvider;

@RequestMapping
@Link
public class ProductReviewsResultHandler extends AbstractReviewsResultHandler {

  public static final String PLACEHOLDER_ID = "es-reviews-placeholder";
  private static final String PRODUCT_REVIEWS_PREFIX = "product-reviews";
  private static final String PRODUCT_ID = "productId";

  /**
   * URI pattern, for URIs like "/dynamic/fragment/product-reviews/{segment}/{contextId}/{productId}"
   */
  public static final String DYNAMIC_PATTERN_PRODUCT_REVIEWS = "/" + PREFIX_DYNAMIC +
          "/" + SEGMENTS_FRAGMENT +
          "/" + PRODUCT_REVIEWS_PREFIX +
          "/{" + ROOT_SEGMENT + "}" +
          "/{" + CONTEXT_ID + "}" +
          "/{" + PRODUCT_ID + "}";

  @Inject
  @Named("resolveProductFragmentContextStrategy")
  private ResolveContextStrategy contextStrategy;

  @RequestMapping(value = DYNAMIC_PATTERN_PRODUCT_REVIEWS, method = RequestMethod.GET)
  public ModelAndView getReviews(@PathVariable(CONTEXT_ID) String contextId,
                                 @PathVariable(PRODUCT_ID) String productId,
                                 @RequestParam(value = TARGETVIEW_PARAMETER, required = false) String view,
                                 HttpServletRequest request) {
    return handleGetReviews(SiteHelper.getSiteFromRequest(request), contextId, productId, view);
  }

  @RequestMapping(value = DYNAMIC_PATTERN_PRODUCT_REVIEWS, method = RequestMethod.POST)
  public ModelAndView createReview(@PathVariable(CONTEXT_ID) String contextId,
                                   @PathVariable(PRODUCT_ID) String productId,
                                   @RequestParam(value = "text", required = false) String text,
                                   @RequestParam(value = "title", required = false) String title,
                                   @RequestParam(value = "rating", required = false) Integer rating,
                                   HttpServletRequest request) {
    return handleCreateReview(SiteHelper.getSiteFromRequest(request), contextId, productId, text, title, rating);
  }

  // ---------------------- building links ---------------------------------------------------------------------
  @Link(type = ProductReviewsResult.class, view = VIEW_FRAGMENT, uri = DYNAMIC_PATTERN_PRODUCT_REVIEWS)
  public UriComponents buildFragmentLink(ReviewsResult reviewsResult,
                                         UriTemplate uriTemplate,
                                         Map<String, Object> linkParameters,
                                         HttpServletRequest request) {
    return buildFragmentUri(SiteHelper.getSiteFromRequest(request), reviewsResult, uriTemplate, linkParameters);
  }

  @Link(type = ProductReviewsResult.class, uri = DYNAMIC_PATTERN_PRODUCT_REVIEWS)
  public UriComponents buildInfoLink(ReviewsResult reviewsResult, UriTemplate uriTemplate,HttpServletRequest request) {
    return getUriComponentsBuilder(SiteHelper.getSiteFromRequest(request), reviewsResult, uriTemplate).build();
  }

  // ---------------------- private helper methods ---------------------------------------------------------------------
  @Override
  protected UriComponentsBuilder getUriComponentsBuilder(Site site, ReviewsResult result, UriTemplate uriTemplate) {

    Product product = (Product) result.getTarget();
    Navigation navigation = getContextHelper().currentSiteContext();

    if (site != null && contextStrategy != null) {
      navigation = contextStrategy.resolveContext(site, product.getExternalTechId());
    }
    return getUriComponentsBuilder(uriTemplate, navigation, product.getExternalTechId());
  }

  @Substitution(PLACEHOLDER_ID)
  @SuppressWarnings("unused")
  public ProductReviewsResult getReviews(@Nullable CMPlaceholder placeholder, @Nonnull HttpServletRequest request) {
    ProductReviewsResult result = null;
    if (placeholder != null) {
      FragmentParameters params = FragmentContextProvider.getFragmentContext(request).getParameters();
      if (params != null) {
        String productId = params.getProductId();
        if (StringUtils.isNotBlank(productId)) {
          Site site = SiteHelper.getSiteFromRequest(request);
          if (site != null) {
            result = getReviewsResult(getContributionTarget(productId, site));
          }
        }
      }
    }
    if (result == null) {
      LOG.info("unable to find product reviews for page {} and request {}", placeholder, request.getRequestURI());
    }
    return result;
  }

  @Override
  protected Object getContributionTarget(String productId, Site site) {
    Product product = getProduct(productId);
    if (product == null) {
      LOG.warn("Product with ID '{}' for Site '{}' could not be resolved", productId, site);
      throw new NotFoundException("Product with ID " + productId + " for Site with ID " + site.getId() + " + could not be resolved");
    }
    return product;
  }

  private Product getProduct(String productId) {
    String techId = getCurrentCommerceIdProvider().formatProductTechId(productId);
    Product product = getCatalogService().findProductById(techId);
    if (product != null && product instanceof ProductVariant) {
      // we only use products as targets for reviews, no product variants (SKUs)
      // e.g. only store the review for PC_TSHIRT and not for PC_TSHIRT_BLUE_XXL
      product = ((ProductVariant)product).getParent();
      if (product != null && LOG.isDebugEnabled()) {
        LOG.debug("productId {} is a ProductVariant using parent product {} instead", productId, product);
      }
    }
    return product;
  }


  private ProductReviewsResult getReviewsResult(Object target) {
    return  new ProductReviewsResult(target);
  }

  @Override
  protected ProductReviewsResult getReviewsResult(Object target, boolean feedbackEnabled, ContributionType contributionType) {
    CommunityUser user = getElasticSocialUserHelper().getCurrentUser();
    return  new ProductReviewsResult(target, user, getElasticSocialService(), feedbackEnabled, contributionType);
  }

  public CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }
}
