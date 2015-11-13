package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.cae.handlers.NavigationSegmentsUriHelper;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.objectserver.web.HttpError;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CartHandlerTest {

  private CartHandler testling;

  @Mock
  private NavigationSegmentsUriHelper navigationSegmentsUriHelper;

  @Mock
  private SitesService sitesService;

  @Mock
  private CommercePropertyProvider checkoutRedirectPropertyProvider;

  @Mock
  private ContentRepository contentRepository;

  private BaseCommerceConnection commerceConnection;

  @Before
  public void beforeEachTest() {
    initMocks(this);
    testling = spy(new CartHandler());
    testling.setNavigationSegmentsUriHelper(navigationSegmentsUriHelper);
    //testling.setCartService(cartService);
    testling.setDeveloperModeEnabled(true);
    testling.setSitesService(sitesService);
    testling.setCheckoutRedirectUrlProvider(checkoutRedirectPropertyProvider);
    testling.setContentRepository(contentRepository);
    when(contentRepository.isContentManagementServer()).thenReturn(false);

    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
  }

  @Test
  public void testHandleRequestNoContextFound() {
    String context = "helios";

    UriComponentsBuilder testUrlBuilder = UriComponentsBuilder.fromUriString("checkoutUrl");
    UriComponents testUrl = testUrlBuilder.build();
    when(checkoutRedirectPropertyProvider.provideValue(any(Map.class))).thenReturn(testUrl);

    View modelAndView = testling.handleRequest(context, mock(HttpServletRequest.class), mock(HttpServletResponse.class));
    assertTrue(modelAndView instanceof RedirectView);
    assertTrue(((RedirectView) modelAndView).getUrl().equals(testUrl.toString()));
  }

  @Test
  public void testHandleFragmentRequest() {
    String contextName = "helios";
    Navigation context = mock(Navigation.class);

    configureContext(contextName, context);

    Cart resolvedCart = mock(Cart.class);
    configureResolveCart(resolvedCart);

    String viewName = "viewName";

    ModelAndView modelAndView = testling.handleFragmentRequest(contextName, viewName);

    checkCartServiceIsUsedCorrectly();

    checkModelContainsCartAndNavigation(resolvedCart, context, modelAndView);

    checkViewName(viewName, modelAndView);
  }

  @Test
  public void testHandleFragmentRequestNoContext() {
    String contextName = "helios";
    configureContext(contextName, null);
    String viewName = "viewName";
    ModelAndView modelAndView = testling.handleFragmentRequest(contextName, viewName);
    checkSelfIsHttpError(modelAndView);
  }

  @Test
  public void testHandleAjaxRequestDeleteOrderItem() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    String orderItemId = "12";
    configureRequestParameter(request, "orderItemId", orderItemId);
    Cart cart = mock(Cart.class);
    configureResolveCart(cart);
    Cart.OrderItem orderItem = mock(Cart.OrderItem.class);
    configureCartFindOrderItem(cart, orderItemId, orderItem);

    Object result = testling.handleAjaxRequest(CartHandler.ACTION_REMOVE_ORDER_ITEM, request, response);

    checkCartServiceIsUsedCorrectly();
    verifyCartDeleteOrderItem(orderItemId);
    assertEquals(Collections.emptyMap(), result);
  }

  //Is this really the expected behavior?!
  //Action was found but has invalid parameters...
  @Test(expected = CartHandler.NotFoundException.class)
  public void testHandleAjaxRequestNoOrderItemId() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    String orderItemId = null;
    configureRequestParameter(request, "orderItemId", orderItemId);
    Cart cart = mock(Cart.class);
    configureResolveCart(cart);
    Cart.OrderItem orderItem = mock(Cart.OrderItem.class);
    configureCartFindOrderItem(cart, orderItemId, orderItem);

    testling.handleAjaxRequest(CartHandler.ACTION_REMOVE_ORDER_ITEM, request, response);

    checkCartServiceIsUsedCorrectly();
  }


  //Is this really the expected behavior?!
  //Action was found but has invalid parameters...
  @Test(expected = CartHandler.NotFoundException.class)
  public void testHandleAjaxRequestNoOrderItem() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    String orderItemId = "12";
    configureRequestParameter(request, "orderItemId", orderItemId);
    Cart cart = mock(Cart.class);
    configureResolveCart(cart);
    Cart.OrderItem orderItem = null;
    configureCartFindOrderItem(cart, orderItemId, orderItem);

    testling.handleAjaxRequest(CartHandler.ACTION_REMOVE_ORDER_ITEM, request, response);

    checkCartServiceIsUsedCorrectly();
  }

  @Test(expected = CartHandler.NotFoundException.class)
  public void testHandleAjaxRequestUnsupportedAction() {
    HttpServletResponse response = mock(HttpServletResponse.class);
    testling.handleAjaxRequest("AnyInvalidAction", mock(HttpServletRequest.class), response);
    checkCartServiceIsUsedCorrectly();
  }

  //Mock Configurations

  private void configureCartFindOrderItem(Cart cart, String orderItemId, Cart.OrderItem orderItem) {
    when(cart.findOrderItemById(orderItemId)).thenReturn(orderItem);
  }

  private void configureRequestParameter(HttpServletRequest request, String parameterKey, String parameterValue) {
    when(request.getParameter(parameterKey)).thenReturn(parameterValue);
  }

  private void configureResolveCart(Cart cart) {
    when(commerceConnection.getCartService().getCart()).thenReturn(cart);
  }

  private void configureContext(String context, Navigation navigation) {
    when(navigationSegmentsUriHelper.parsePath(eq(Collections.singletonList(context)))).thenReturn(navigation);
  }

  //Checks and Verifies...

  private void checkViewName(String viewName, ModelAndView modelAndView) {
    String actualViewName = modelAndView.getViewName();
    assertEquals(viewName, actualViewName);
  }

  private void checkModelContainsCartAndNavigation(Cart expectedCart, Navigation context, ModelAndView modelAndView) {
    Map<String, Object> model = modelAndView.getModel();
    Object self = model.get("self");
    assertTrue(self instanceof Cart);
    assertSame(expectedCart, self);
    Object navigation = model.get(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION);
    assertSame(context, navigation);
  }

  private void checkCartServiceIsUsedCorrectly() {
    verify(commerceConnection.getCartService(), times(1)).getCart();
  }

  private void checkSelfIsHttpError(ModelAndView modelAndView) {
    assertTrue(modelAndView.getModel().get("self") instanceof HttpError);
  }

  private void verifyCartDeleteOrderItem(String orderItemId) {
    verify(commerceConnection.getCartService(), times(1)).deleteCartOrderItem(orderItemId);
  }
}
