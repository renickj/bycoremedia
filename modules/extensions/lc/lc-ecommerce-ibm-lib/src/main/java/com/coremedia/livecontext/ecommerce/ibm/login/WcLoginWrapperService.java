package com.coremedia.livecontext.ecommerce.ibm.login;

import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.InvalidLoginException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCatalogId;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Arrays.asList;

public class WcLoginWrapperService extends AbstractWcWrapperService {

  public static final String ERROR_KEY_AUTHENTICATION_ERROR = "_ERR_AUTHENTICATION_ERROR";

  private static final WcRestConnector.WcRestServiceMethod<WcSession, WcLoginParam>
          LOGIN_IDENTITY = WcRestConnector.createServiceMethod(HttpMethod.POST, "store/{storeId}/loginidentity", true, false, false, WcLoginParam.class, WcSession.class);

  private static final WcRestConnector.WcRestServiceMethod<Void, Void>
          LOGOUT_IDENTITY = WcRestConnector.createServiceMethod(HttpMethod.DELETE, "store/{storeId}/loginidentity/@self", true, true, false, null, Void.class);

  private static final WcRestConnector.WcRestServiceMethod<WcPreviewToken, WcPreviewTokenParam>
          PREVIEW_TOKEN = WcRestConnector.createServiceMethod(HttpMethod.POST, "store/{storeId}/previewToken", true, true, false, WcPreviewTokenParam.class, WcPreviewToken.class);

  private static final WcRestConnector.WcRestServiceMethod<HashMap, Void>
          USER_CONTEXT_DATA = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/usercontext/@self/contextdata", false, false, false, true, null, HashMap.class);

  public WcSession login(String logonId, String password, StoreContext storeContext) throws CommerceException {
    try {
      return getRestConnector().callServiceInternal(
        LOGIN_IDENTITY, asList(getStoreId(storeContext)), null, new WcLoginParam(logonId, password), null, null);

      //if login not successfully a RemoteException is thrown
    } catch (CommerceRemoteException e) {
      if (e.getRemoteError() != null && ERROR_KEY_AUTHENTICATION_ERROR.equals(e.getRemoteError().getErrorKey())) {
        throw new InvalidLoginException("The specified logon ID '" + logonId + "' or the used password is incorrect.");
      } else {
        throw e;
      }
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public boolean isLoggedIn(String logonId, StoreContext storeContext, UserContext userContext) throws CommerceException {
    try {
      Map userContextData = getRestConnector().callServiceInternal(USER_CONTEXT_DATA, asList(getStoreId(storeContext)),
              createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext)),
              null, storeContext, userContext);
      if (userContextData != null) {
        Object value = DataMapHelper.getValueForPath(userContextData, "basicInfo.callerId");
        if (value instanceof Double) {
          return ("" + ((Double) value).intValue()).equals(logonId);
        }
      }
      return false;

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

  public boolean logout(String storeId) throws CommerceException {
    getRestConnector().callServiceInternal(LOGOUT_IDENTITY, asList(storeId), null, null, null, null);
    // Todo: if no exception is thrown we assume that the user was logged out successfully. is that correct?
    return true;
  }

  public WcPreviewToken getPreviewToken(WcPreviewTokenParam bodyData, StoreContext storeContext) {
    try {
      return getRestConnector().callService(
              PREVIEW_TOKEN, asList(getStoreId(storeContext)),
              null, bodyData, storeContext, null);

    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }

}
