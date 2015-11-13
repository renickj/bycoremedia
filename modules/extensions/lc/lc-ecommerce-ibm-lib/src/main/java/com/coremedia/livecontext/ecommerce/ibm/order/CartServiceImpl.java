package com.coremedia.livecontext.ecommerce.ibm.order;

import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.ecommerce.order.CartService;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class CartServiceImpl implements CartService {

  private WcCartWrapperService cartWrapperService;
  private CommerceBeanFactory commerceBeanFactory;


  @Override
  public Cart getCart() {
    StoreContext context = StoreContextHelper.getCurrentContext();
    WcCart wcCart = cartWrapperService.getCart(UserContextHelper.getCurrentContext(), context);
    return createCartBeanFor(wcCart, context);
  }

  @Override
  public void deleteCartOrderItem(String orderItemId) {
    updateCartOrderItem(orderItemId, BigDecimal.ZERO);
  }

  @Override
  public void updateCartOrderItem(String orderItemId, BigDecimal newQuantity) {
    updateCart(Collections.singletonList(new OrderItemParam(orderItemId, newQuantity)));
  }

  @Override
  public void updateCart(Iterable<OrderItemParam> orderItems) {
    WcUpdateCartParam wcUpdateCartParam = new WcUpdateCartParam();
    List<WcUpdateCartParam.OrderItem> wcUpdateOrderItems = new ArrayList<>();
    for (OrderItemParam orderItemToUpdate :orderItems) {
      wcUpdateOrderItems.add(new WcUpdateCartParam.OrderItem(orderItemToUpdate.getExternalId(), orderItemToUpdate.getQuantity().toPlainString()));
    }
    wcUpdateCartParam.setOrderItem(wcUpdateOrderItems);
    cartWrapperService.updateCart(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext(), wcUpdateCartParam);
  }

  @Override
  public void addToCart(Iterable<OrderItemParam> orderItems) {
    WcAddToCartParam wcAddToCartParam = new WcAddToCartParam();
    List<WcAddToCartParam.OrderItem> wcAddToOrderItems = new ArrayList<>();
    for (OrderItemParam orderItem :orderItems) {
      wcAddToOrderItems.add(new WcAddToCartParam.OrderItem(orderItem.getExternalId(), orderItem.getQuantity().toPlainString()));
    }
    wcAddToCartParam.setOrderItem(wcAddToOrderItems);
    cartWrapperService.addToCart(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext(), wcAddToCartParam);
  }

  @Override
  public void cancelCart() {
    cartWrapperService.cancelCart(UserContextHelper.getCurrentContext(), StoreContextHelper.getCurrentContext());
  }

  private Cart createCartBeanFor(WcCart cartWrapper, StoreContext context) {
    if (cartWrapper == null) {
      // no wcs cart == empty cart
      return new CartImpl();
    }
    String id = CommerceIdHelper.formatCartId(cartWrapper.getBuyerId());
    Cart cart = (Cart) commerceBeanFactory.createBeanFor(id, context);
    ((AbstractIbmCommerceBean) cart).setDelegate(cartWrapper);
    return cart;
  }

  public WcCartWrapperService getCartWrapperService() {
    return cartWrapperService;
  }

  @Required
  public void setCartWrapperService(WcCartWrapperService cartWrapperService) {
    this.cartWrapperService = cartWrapperService;
  }

  public CommerceBeanFactory getCommerceBeanFactory() {
    return commerceBeanFactory;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  @Nonnull
  @Override
  public CartService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, CartService.class);
  }
}
