package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.ecommerce.order.CartService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.objectserver.view.substitution.Substitution;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.ContentTypes.CONTENT_TYPE_JSON;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_SERVICE;
import static com.coremedia.blueprint.base.links.UriConstants.RequestParameters.TARGETVIEW_PARAMETER;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ROOT;
import static com.coremedia.blueprint.base.links.UriConstants.Views.VIEW_FRAGMENT;

/**
 * Handler for Commerce carts.
 */
@Link
@RequestMapping
public class CartHandler extends LiveContextPageHandlerBase {

  protected static final String URI_PREFIX = "cart";

  /**
   * URI pattern, for URIs like "/service/cart/perfectchef"
   */
  public static final String URI_PATTERN = '/' + PREFIX_SERVICE +
          '/' + URI_PREFIX +
          "/{" + SEGMENT_ROOT + '}';

  /**
   * URI pattern, for URIs like "/dynamic/fragment/cart/perfectchef"
   */
  public static final String DYNAMIC_URI_PATTERN = '/' + PREFIX_DYNAMIC +
          '/' + SEGMENTS_FRAGMENT +
          '/' + URI_PREFIX +
          "/{" + SEGMENT_ROOT + '}';

  private static final String PARAM_ACTION = "action";

  @VisibleForTesting
  static final String ACTION_REMOVE_ORDER_ITEM = "removeOrderItem";
  private static final String ORDER_ITEM_ID = "orderItemId";

  @VisibleForTesting
  static final String ACTION_ADD_ORDER_ITEM = "addOrderItem";
  private static final String EXTERNAL_TECH_ID = "externalTechId";

  private CommercePropertyProvider checkoutRedirectUrlProvider;

  @Substitution("cart")
  public Cart getCart() {
    return new LazyCart();
  }

  // --- Handlers ------------------------------------------------------------------------------------------------------

  @RequestMapping(value = URI_PATTERN, method = RequestMethod.GET)
  public View handleRequest(@PathVariable(SEGMENT_ROOT) String context, HttpServletRequest request, HttpServletResponse response) {
    StoreContext storeContext = getStoreContextProvider().getCurrentContext();

    Map<String,Object> params = new HashMap<>();
    params.put(URL_PROVIDER_STORE_CONTEXT, storeContext);
    params.put(URL_PROVIDER_IS_STUDIO_PREVIEW, isStudioPreview());
    UriComponents checkoutUrl = (UriComponents) checkoutRedirectUrlProvider.provideValue(params);
    String redirectUrl = checkoutUrl.toString();
    redirectUrl = applyLinkTransformers(redirectUrl, request, response, true);

    if (redirectUrl.startsWith("//")) {
      String scheme = request.getScheme();
      redirectUrl = scheme + ":" + redirectUrl;
    }

    return new RedirectView(redirectUrl);
  }

  @RequestMapping(value = DYNAMIC_URI_PATTERN, method = RequestMethod.GET)
  public ModelAndView handleFragmentRequest(@PathVariable(SEGMENT_ROOT) String context,
                                            @RequestParam(value = TARGETVIEW_PARAMETER, required = false) String view) {
    // if no context available: return "not found"
    Navigation navigation = getNavigation(context);
    if (navigation != null) {
      Cart cart = resolveCart();
      if (cart != null) {
        // add navigationContext as navigationContext request param
        ModelAndView modelWithView = HandlerHelper.createModelWithView(cart, view);
        NavigationLinkSupport.setNavigation(modelWithView, navigation);
        return modelWithView;
      }
    }

    return HandlerHelper.notFound();
  }

  @RequestMapping(value = DYNAMIC_URI_PATTERN, method = RequestMethod.POST, produces = {CONTENT_TYPE_JSON})
  @ResponseBody
  public Object handleAjaxRequest(@RequestParam(value = PARAM_ACTION, required = true) String action,
                                  HttpServletRequest request, HttpServletResponse response) {
    switch (action) {
      case ACTION_REMOVE_ORDER_ITEM:
        return handleRemoveOrderItem(request);
      case ACTION_ADD_ORDER_ITEM:
        return handleAddOrderItem(request, response);
      default:
        throw new NotFoundException("unsupported action: " + action);
    }
  }

  private Object handleRemoveOrderItem(HttpServletRequest request) {
    Cart cart = resolveCart();
    if (cart != null) {
      String orderItemId = request.getParameter(ORDER_ITEM_ID);
      if (orderItemExist(cart, orderItemId)) {
        deleteCartOrderItem(orderItemId);
        return Collections.emptyMap();
      }
      throw new NotFoundException("Cannot remove " + orderItemId + " from cart");
    }
    return Collections.emptyMap();
  }

  private Object handleAddOrderItem(HttpServletRequest request, HttpServletResponse response) {
    if (getUserSessionService().ensureGuestIdentity(request, response)) {
      String externalTechId = request.getParameter(EXTERNAL_TECH_ID);
      addCartOrderItem(externalTechId);
      return Collections.emptyMap();
    }
    throw new NotFoundException("Cannot switch to guest state");
  }

  private void deleteCartOrderItem(String orderItemId) {
    getCartService().deleteCartOrderItem(orderItemId);
  }

  private void addCartOrderItem(String orderItemId) {
    List<CartService.OrderItemParam> items = Arrays.asList(new CartService.OrderItemParam(orderItemId, BigDecimal.valueOf(1)));
    getCartService().addToCart(items);
  }

  private boolean orderItemExist(Cart cart, String orderItemId) {
    return orderItemId != null && cart.findOrderItemById(orderItemId) != null;
  }

  public UserSessionService getUserSessionService() {
    return Commerce.getCurrentConnection().getUserSessionService();
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  public static class NotFoundException extends RuntimeException {
    public NotFoundException(String msg) {
      super(msg);
    }
  }


  // --- LinkSchemes ---------------------------------------------------------------------------------------------------

  /**
   * Builds a generic action link for a {@link CMAction}.
   */
  @SuppressWarnings({"TypeMayBeWeakened", "UnusedParameters"})
  @Link(type = Cart.class, uri = URI_PATTERN)
  public UriComponents buildLink(Cart cart, UriTemplate uriPattern, Map<String, Object> linkParameters, HttpServletRequest request) {
    return buildLinkInternal(uriPattern, linkParameters);
  }

  @Link(type = Cart.class, view = VIEW_FRAGMENT, uri = DYNAMIC_URI_PATTERN)
  public UriComponents buildFragmentLink(Cart cart, UriTemplate uriPattern, Map<String, Object> linkParameters, HttpServletRequest request) {
    return buildLinkInternal(uriPattern, linkParameters);
  }

  @Link(type = Cart.class, view = "ajax", uri = DYNAMIC_URI_PATTERN)
  public UriComponents buildDeleteCartOderItemLink(Cart cart, UriTemplate uriPattern, Map<String, Object> linkParameters, HttpServletRequest request) {
    return buildLinkInternal(uriPattern, linkParameters);
  }

  private UriComponents buildLinkInternal(UriTemplate uriPattern, Map<String, Object> linkParameters) {
    Navigation context = getContextHelper().currentSiteContext();
    UriComponentsBuilder result = UriComponentsBuilder.fromPath(uriPattern.toString());
    result = addLinkParametersAsQueryParameters(result, linkParameters);
    return result.buildAndExpand(ImmutableMap.of(
            SEGMENT_ROOT, getPathSegments(context).get(0)
    ));
  }

  public CartService getCartService() {
    return Commerce.getCurrentConnection().getCartService();
  }


  @Required
  public void setCheckoutRedirectUrlProvider(CommercePropertyProvider checkoutRedirectUrlProvider) {
    this.checkoutRedirectUrlProvider = checkoutRedirectUrlProvider;
  }

  private Cart resolveCart() {
    CartService cartService = getCartService();
    return cartService != null ? cartService.getCart() : null;
  }

  //====================================================================================================================

  /**
   * This class fetches the actual cart from the cart service only if some methods are actually used. This saves a cart
   * fetch round trip to the commerce backend if the cart is only needed for link building.
   */
  private class LazyCart implements Cart {

    private Cart delegate;

    public Cart getDelegate() {
      if (delegate == null) {
        delegate = resolveCart();
      }
      return delegate;
    }

    @Override
    public String getId() {
      return getDelegate().getId();
    }

    @Override
    public StoreContext getContext() {
      return getDelegate().getContext();
    }

    @Override
    public Locale getLocale() {
      return getDelegate().getLocale();
    }

    @Override
    public String getReference() {
      return getDelegate().getReference();
    }

    @Override
    public List<OrderItem> getOrderItems() {
      return getDelegate().getOrderItems();
    }

    @Override
    public BigDecimal getTotalQuantity() {
      return getDelegate().getTotalQuantity();
    }

    @Override
    public OrderItem findOrderItemById(String orderItemId) {
      return getDelegate().findOrderItemById(orderItemId);
    }

    @Override
    public String getExternalId() {
      return getDelegate().getExternalId();
    }

    @Override
    public String getExternalTechId() {
      return getDelegate().getExternalTechId();
    }

    @Override
    public void load() throws CommerceException {
      getDelegate();
    }
  }
}
