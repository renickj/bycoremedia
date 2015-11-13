package com.coremedia.livecontext.ecommerce.ibm.order;

import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.springframework.http.HttpMethod;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCatalogId;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Arrays.asList;

/**
 * A service that uses the catalog getRestConnector() to get cart wrappers.
 */
public class WcCartWrapperService extends AbstractWcWrapperService {

  private static final WcRestConnector.WcRestServiceMethod<WcCart, Void>
          GET_CART = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/cart/@self", false, false, false, true, null, WcCart.class);

  private static final WcRestConnector.WcRestServiceMethod<Void, WcUpdateCartParam>
    UPDATE_CART = WcRestConnector.createServiceMethod(HttpMethod.PUT, "store/{storeId}/cart/@self", false, false, false, true, WcUpdateCartParam.class, Void.class);

  private static final WcRestConnector.WcRestServiceMethod<Void, Void>
    CANCEL_CART = WcRestConnector.createServiceMethod(HttpMethod.DELETE, "store/{storeId}/cart/@self", false, false, false, true, null, Void.class);

  private static final WcRestConnector.WcRestServiceMethod<Void, WcAddToCartParam>
    ADD_TO_CART = WcRestConnector.createServiceMethod(HttpMethod.POST, "store/{storeId}/cart", false, false, false, true, WcAddToCartParam.class, Void.class);

  public WcCart getCart(final UserContext userContext, final StoreContext storeContext) throws CommerceException {
    try {
      Integer userId = UserContextHelper.getForUserId(userContext);
      if(!UserContextHelper.isAnonymousId(userId)) {
        return getRestConnector().callService(
                GET_CART, asList(getStoreId(storeContext)),
                createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null),
                null, storeContext, userContext);
      }
      return null;
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  public void updateCart(final UserContext userContext, final StoreContext storeContext, WcUpdateCartParam updateCartParam)
    throws CommerceException {
    try {
      getRestConnector().callService(
        UPDATE_CART, asList(getStoreId(storeContext)),
        createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null),
        updateCartParam, storeContext, userContext);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  public void addToCart(final UserContext userContext, final StoreContext storeContext, WcAddToCartParam addToCartParam)
    throws CommerceException {
    try {
      getRestConnector().callService(
        ADD_TO_CART, asList(getStoreId(storeContext)),
        createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null),
        addToCartParam, storeContext, userContext);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  public void cancelCart(final UserContext userContext, final StoreContext storeContext) throws CommerceException {
    try {
      getRestConnector().callService(
        CANCEL_CART, asList(getStoreId(storeContext)),
        createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null),
        null, storeContext, userContext);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

}
