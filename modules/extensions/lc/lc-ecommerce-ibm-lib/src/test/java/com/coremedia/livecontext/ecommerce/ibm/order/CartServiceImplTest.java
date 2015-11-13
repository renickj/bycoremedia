package com.coremedia.livecontext.ecommerce.ibm.order;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.ibm.catalog.CatalogServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserSessionServiceImpl;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.ecommerce.order.CartService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CartServiceImplTest extends AbstractServiceTest {

  public static final String BEAN_NAME_CART_SERVICE = "cartService";
  public static final String BEAN_NAME_CATAGLOG_SERVICE = "catalogServiceBod";
  public static final String BEAN_NAME_USER_SESSION_SERVICE = "commerceUserSessionService";
  private static final String SKU_CODE = "CLA022_220301";

  CartServiceImpl testling;
  CatalogServiceImpl catalogService;
  UserSessionService userSessionService;

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean(BEAN_NAME_CART_SERVICE, CartServiceImpl.class);
    catalogService = infrastructure.getBean(BEAN_NAME_CATAGLOG_SERVICE, CatalogServiceImpl.class);
    userSessionService = infrastructure.getBean(BEAN_NAME_USER_SESSION_SERVICE, UserSessionServiceImpl.class);
  }

  /**
   * Test makes only sense if it is used with betamax and the cart is prefilled while you record the tape.
   * @throws Exception
   */
  @Betamax(tape = "casi_testGetCart", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testGetCart() throws Exception {

    if ("*".equals(System.getProperties().get("betamax.ignoreHosts"))) {
      return;
    }

    if (StoreContextHelper.getWcsVersion(testConfig.getStoreContext()) < StoreContextHelper.WCS_VERSION_7_7) return;
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(USER1_NAME);
    userContext.put(UserContextHelper.FOR_USER_ID, USER1_ID);
    UserContextHelper.setCurrentContext(userContext);
    Cart cart = testling.getCart();
    assertNotNull(cart);
    assertEquals("1.0", cart.getOrderItems().get(0).getQuantity().toPlainString());
  }

  /**
   * Attention: This test is not intended to run with betamax. Technically spoken it succeeds automatically
   * if it detects a betamax proxy mode. Only if betamax.ignoreHost is set to "*" the function is tested.
   * The reason is that it writes state on the server and it is not able to run concurrently.
   */
  @Test
  public void testUpdateCart() throws Exception {
    if (!"*".equals(System.getProperties().get("betamax.ignoreHosts")) ||
            StoreContextHelper.getWcsVersion(testConfig.getStoreContext()) < StoreContextHelper.WCS_VERSION_7_7)
      return;
    prefillCart();
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(USER2_NAME);
    userContext.put(UserContextHelper.FOR_USER_ID, USER2_ID);
    UserContextHelper.setCurrentContext(userContext);
    testling.cancelCart();
    Cart cart = testling.getCart();
    assertNotNull(cart);
    assertTrue(cart.getOrderItems().isEmpty());
    String orderItemId = getOrderItemId(SKU_CODE);
    assertNotNull(orderItemId);
    List<CartService.OrderItemParam> addToParams = new ArrayList<>();
    addToParams.add(new CartService.OrderItemParam(
      orderItemId,
      BigDecimal.ONE));
    testling.addToCart(addToParams);
    cart = testling.getCart();
    assertNotNull(cart);
    Cart.OrderItem orderItem = cart.getOrderItems().get(0);
    assertEquals("1.0", orderItem.getQuantity().toPlainString());
    List<CartService.OrderItemParam> updateParams = new ArrayList<>();
    updateParams.add(new CartService.OrderItemParam(
      orderItem.getExternalId(),
      orderItem.getQuantity().add(BigDecimal.ONE)));
    testling.updateCart(updateParams);
    cart = testling.getCart();
    orderItem = cart.getOrderItems().get(0);
    assertEquals("2.0", orderItem.getQuantity().toPlainString());
    testling.cancelCart();
  }

  @Test
  public void testUpdateCartWithAnonymousUser() throws Exception {
    if (!"*".equals(System.getProperties().get("betamax.ignoreHosts")) ||
            StoreContextHelper.getWcsVersion(testConfig.getStoreContext()) < StoreContextHelper.WCS_VERSION_7_7)
      return;

    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContextHelper.setCurrentContext(UserContextHelper.getCurrentContext());

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean guestIdentitySuccess = userSessionService.ensureGuestIdentity(request, response);
    assertTrue(guestIdentitySuccess);

    testling.cancelCart();
    Cart cart = testling.getCart();
    assertNotNull(cart);
    assertTrue(cart.getOrderItems().isEmpty());
    String orderItemId = getOrderItemId(SKU_CODE);
    assertNotNull(orderItemId);
    List<CartService.OrderItemParam> addToParams = new ArrayList<>();
    addToParams.add(new CartService.OrderItemParam(
            orderItemId,
            BigDecimal.ONE));
    testling.addToCart(addToParams);
    cart = testling.getCart();
    assertNotNull(cart);
    Cart.OrderItem orderItem = cart.getOrderItems().get(0);
    assertEquals("1.0", orderItem.getQuantity().toPlainString());
    List<CartService.OrderItemParam> updateParams = new ArrayList<>();
    updateParams.add(new CartService.OrderItemParam(
            orderItem.getExternalId(),
            orderItem.getQuantity().add(BigDecimal.ONE)));
    testling.updateCart(updateParams);
    cart = testling.getCart();
    orderItem = cart.getOrderItems().get(0);
    assertEquals("2.0", orderItem.getQuantity().toPlainString());
    testling.cancelCart();
  }

  private void prefillCart() throws Exception {
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(USER1_NAME);
    userContext.put(UserContextHelper.FOR_USER_ID, USER1_ID);
    UserContextHelper.setCurrentContext(userContext);
    testling.cancelCart();
    Cart cart = testling.getCart();
    assertNotNull(cart);
    String orderItemId = getOrderItemId(SKU_CODE);
    assertNotNull(orderItemId);
    List<CartService.OrderItemParam> updateParams = new ArrayList<>();
    updateParams.add(new CartService.OrderItemParam(
            orderItemId,
            BigDecimal.ONE));
    testling.addToCart(updateParams);
    cart = testling.getCart();
    assertNotNull(cart);
    assertEquals("1.0", cart.getOrderItems().get(0).getQuantity().toPlainString());
  }

  private String getOrderItemId(String productId) {
    Product product = catalogService.findProductById(CommerceIdHelper.formatProductVariantId(productId));
    if (product != null) return product.getExternalTechId();
    return null;
  }
}
