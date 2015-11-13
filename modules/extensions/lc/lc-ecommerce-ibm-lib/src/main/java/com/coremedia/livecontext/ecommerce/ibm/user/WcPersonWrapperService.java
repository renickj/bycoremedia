package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.*;
import static java.util.Arrays.asList;

/**
 * Wrapper service for person requests.
 */
public class WcPersonWrapperService extends AbstractWcWrapperService {

  private static final String PARAM_CHALLENGE_ANSWER = "challengeAnswer";
  private static final String PARAM_PASSWORD = "logonPassword";  // NOSONAR false positive: Credentials should not be hard-coded
  private static final String PARAM_PASSWORD_OLD = "logonPasswordOld";  // NOSONAR false positive: Credentials should not be hard-coded
  private static final String PARAM_PASSWORD_VERIFY = "logonPasswordVerify";  // NOSONAR false positive: Credentials should not be hard-coded

  private static final WcRestConnector.WcRestServiceMethod<Map, Map>
          FIND_PERSON_BY_SELF = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/person/@self", true, true, Map.class, Map.class),
          UPDATE_PERSON = WcRestConnector.createServiceMethod(HttpMethod.PUT, "store/{storeId}/person/@self", true, true, Map.class, Map.class);
  private static final WcRestConnector.WcRestServiceMethod<Map, Map>
          //the ibm documentation says that the registerPerson-call needs authentication, which seems to be a documentation bug. it only works without authentication.
          REGISTER_PERSON = WcRestConnector.createServiceMethod(HttpMethod.POST, "store/{storeId}/person/", true, false, Map.class, Map.class);
  private static final WcRestConnector.WcRestServiceMethod<Void, Void>
          RESET_PASSWORD = WcRestConnector.createServiceMethod(HttpMethod.POST, "store/{storeId}/resetpassword/{logonId}", true, false, Void.class, Void.class);
  private static final WcRestConnector.WcRestServiceMethod<Void, Void>
          UPDATE_PASSWORD = WcRestConnector.createServiceMethod(HttpMethod.POST, "store/{storeId}/resetpassword/{logonId}", true, true, true, true, Void.class, Void.class);


  public Map<String, Object> findPerson(UserContext userContext, StoreContext storeContext) {
    Map<String, String[]> parametersMap = createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null);
    //noinspection unchecked
    Map<String, Object> map = getRestConnector().callService(
            FIND_PERSON_BY_SELF, asList(getStoreId(storeContext)), parametersMap, null, storeContext, userContext);
    return map;
  }

  public Map<String, Object> updatePerson(Map<String, Object> userMap, UserContext userContext, StoreContext storeContext) {
    Map<String, String[]> parametersMap = createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null);

    //we ignore the return value here since we only retrieve the user id. Instead...
    getRestConnector().callService(
      UPDATE_PERSON, asList(getStoreId(storeContext)), parametersMap, userMap, storeContext, userContext);

    //...we make a regular person lookup.
    return findPerson(userContext, storeContext);
  }

  public Map<String, Object> registerPerson(String username, String password, String email, StoreContext storeContext) {
    Map<String, String> person = new HashMap<>();
    person.put("logonId", username);
    person.put("logonPassword", password);
    person.put("challengeAnswer", "-");
    person.put("email1", email);

    //regular registration without caring about possible guest session
    Map<String, String[]> parametersMap = createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext));
    getRestConnector().callService(REGISTER_PERSON, asList(getStoreId(storeContext)), parametersMap, person, storeContext, null);

    //retrieve the person for the credentials that have been returned by the registration call.
    UserContext registeredPersonContext = UserContextHelper.createContext(username, null);
    return findPerson(registeredPersonContext, storeContext);
  }

  public void resetPassword(String logonId, String challengeAnswer, StoreContext storeContext) throws CommerceException {
    Map<String, String[]> parametersMap = createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext));
    if (StringUtils.isEmpty(challengeAnswer)) {
      challengeAnswer = "-";
    }
    parametersMap.put(PARAM_CHALLENGE_ANSWER, new String[]{challengeAnswer});

    getRestConnector().callService(RESET_PASSWORD, asList(getStoreId(storeContext), logonId), parametersMap, null, storeContext, null);
  }

  public void updatePassword(String oldPassword, String password, String verifyPassword, UserContext userContext, StoreContext storeContext) throws CommerceException {
    Map<String, String[]> parametersMap = createParametersMap(getCatalogId(storeContext), getLocale(storeContext), getCurrency(storeContext), UserContextHelper.getForUserId(userContext), UserContextHelper.getForUserName(userContext), null);
    parametersMap.put(PARAM_PASSWORD_OLD, new String[]{oldPassword});
    parametersMap.put(PARAM_PASSWORD, new String[]{password});
    parametersMap.put(PARAM_PASSWORD_VERIFY, new String[]{verifyPassword});

    String logonId = UserContextHelper.getForUserName(userContext);
    getRestConnector().callService(UPDATE_PASSWORD, asList(getStoreId(storeContext), logonId), parametersMap, null, storeContext, userContext);
  }

}
