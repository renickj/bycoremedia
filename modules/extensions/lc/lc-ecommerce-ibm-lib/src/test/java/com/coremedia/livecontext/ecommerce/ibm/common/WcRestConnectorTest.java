package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginServiceImpl;
import com.coremedia.livecontext.ecommerce.ibm.login.WcCredentials;
import com.coremedia.livecontext.ecommerce.ibm.login.WcSession;
import com.coremedia.livecontext.ecommerce.ibm.order.WcCart;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WcRestConnectorTest extends AbstractWrapperServiceTest {

  private static final String BEAN_NAME = "restConnector";
  private static final String BEAN_NAME_LOGIN_SERVICE = "userLoginService";
  private static final String PARAM_LANG_ID = "langId";
  private static final String PARAM_CURRENCY = "currency";
  private static final String PARAM_FOR_USER = "forUser";

  private static final String BEAN_NAME_COMMERCE = "commerce";

  private static final WcRestConnector.WcRestServiceMethod<Map, Map>
    FIND_PERSON_BY_SELF = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/person/@self", true, true, Map.class, Map.class);

  private static final WcRestConnector.WcRestServiceMethod<WcCart, Void>
           GET_CART = WcRestConnector.createServiceMethod(HttpMethod.GET, "store/{storeId}/cart/@self", true, true, false, true, null, WcCart.class);

  private static final WcRestConnector.WcRestServiceMethod<Map, Void>
          FIND_SUB_CATEGORIES_SEARCH = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/categoryview/byParentCategory/{parentCategoryId}?profileName=CoreMedia_findSubCategories", false, false, true, true, true, Map.class);

  protected WcRestConnector testling;
  protected LoginService loginService;
  protected Commerce commerce;
  protected CommerceConnection connection;

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean(BEAN_NAME, WcRestConnector.class);
    loginService = infrastructure.getBean(BEAN_NAME_LOGIN_SERVICE, LoginServiceImpl.class);
    commerce = infrastructure.getBean(BEAN_NAME_COMMERCE, Commerce.class);
    connection = commerce.getConnection("wcs1");
    connection.setStoreContext(testConfig.getStoreContext());
    Commerce.setCurrentConnection(connection);
  }

  /**
   * Attention: This test is not beatamax ready. We should only run it when we test against the
   * real backend system (because it takes longer anyway).
   * To achieve this we test if the "betamax.ignoreHost" Java property is set to "*".
   */
  @Test
  public void testReconnectForAuthorizedServiceCalls() throws Exception {

    if (!"*".equals(System.getProperties().get("betamax.ignoreHosts"))) return;

    StoreContext storeContext = testConfig.getStoreContext();
    StoreContextHelper.setCurrentContext(storeContext);
    UserContext userContext = userContextProvider.createContext(TEST_USER);

    WcCredentials credentials = loginService.loginServiceIdentity();
    credentials.getSession().setWCToken("1002%2cOeke6xduXJ%2ba1BTzBtkz1dYInKBdv5WLd5yBKF0NKe1BGCQivNu5r0uNrX5L8q1ibo8sLXxFXrk%2b%0d%0aFVEvfIZzytRYmjwqjAiryXQ8utp5G%2bcA4%2fg0s%2fGVRq7DiPbdBEUcvwhH6Tx3bJg%3d");
    StoreContextHelper.setCredentials(storeContext, credentials);

    WcRestConnector spiedTestling = spy(testling);

    Map<String, String[]> parametersMap = createParametersMap(
      getLocale(testConfig.getStoreContext()), getCurrency(storeContext), UserContextHelper.getForUserName(userContext));

    spiedTestling.callService(FIND_PERSON_BY_SELF, asList(getStoreId(storeContext)), parametersMap, null, storeContext, userContext);

    verify(spiedTestling, times(2)).callServiceInternal(
            any(WcRestConnector.WcRestServiceMethod.class),
            any(List.class),
            any(Map.class),
            any(Object.class),
            any(StoreContext.class),
            any(UserContext.class)
    );
  }

  @Test
  public void testGetRequestCookieHeader() throws Exception {

    String cookieHeader = "myCookieHeader";

    StoreContext storeContext = StoreContextHelper.createContext("configId", "storeId", "storeName", "catalogId", "de", "EUR");
    UserContext userContext = mock(UserContext.class);
    when(userContext.getCookieHeader()).thenReturn(cookieHeader);
    LoginService loginServiceMock = mock(LoginService.class);
    WcCredentials wcCredentialsMock = mock(WcCredentials.class);
    WcSession wcSessionMock = mock(WcSession.class);
    when(wcSessionMock.getWCToken()).thenReturn("WCToken");
    when(wcSessionMock.getWCTrustedToken()).thenReturn("WCTrustedToken");
    when(wcCredentialsMock.getSession()).thenReturn(wcSessionMock);

    try {
      testling.setLoginService(loginServiceMock);
      when(loginServiceMock.loginServiceIdentity()).thenReturn(wcCredentialsMock);

      Map<String, String> requiredHeaders = testling.getRequiredHeaders(GET_CART, true, storeContext, userContext);

      String cookieHeader2 = requiredHeaders.get("Cookie");
      assertNotNull(cookieHeader2);
      assertEquals(cookieHeader, cookieHeader2);
    }
    finally {
      testling.setLoginService(loginService);
    }
  }

  @Test
  public void testGetRequestCookieHeaderForContracts() throws Exception {

    String cookieHeader = "myCookieHeader";

    BaseCommerceConnection commerceConnection = new BaseCommerceConnection();
    Commerce.setCurrentConnection(commerceConnection);

    StoreContext storeContext = StoreContextHelper.createContext("configId", "storeId", "storeName", "catalogId", "de", "EUR");
    storeContext.setContractIds(new String[]{"contractA", "contractB"});
    StoreContextHelper.setWcsVersion(storeContext, "7.8");
    commerceConnection.setStoreContext(storeContext);

    UserContext userContext = mock(UserContext.class);
    when(userContext.getCookieHeader()).thenReturn(cookieHeader);
    commerceConnection.setUserContext(userContext);

    LoginService loginServiceMock = mock(LoginService.class);
    WcCredentials wcCredentialsMock = mock(WcCredentials.class);
    WcSession wcSessionMock = mock(WcSession.class);
    when(wcSessionMock.getWCToken()).thenReturn("WCToken");
    when(wcSessionMock.getWCTrustedToken()).thenReturn("WCTrustedToken");
    when(wcCredentialsMock.getSession()).thenReturn(wcSessionMock);

    try {
      testling.setLoginService(loginServiceMock);
      when(loginServiceMock.loginServiceIdentity()).thenReturn(wcCredentialsMock);

      Map<String, String> requiredHeaders = testling.getRequiredHeaders(FIND_SUB_CATEGORIES_SEARCH, true, storeContext, null);

      String cookieHeader2 = requiredHeaders.get("Cookie");
      assertNotNull(cookieHeader2);
      assertEquals(cookieHeader, cookieHeader2);
    }
    finally {
      testling.setLoginService(loginService);
    }
  }

  @Test
  public void testGetRequestUri() throws Exception {
    Map<String, String[]> parametersMap = createParametersMap(
      getLocale(testConfig.getStoreContext()), Currency.getInstance(Locale.GERMANY), "mu&rk{e}l");
    URI requestUri = wcRestConnector.buildRequestUri("store/{param1}/person/{param2}@self?q={param3}", true, false,
            Lists.newArrayList("param1value", "param & 2 {value}", "param3value"),
            parametersMap);
    String serviceEndpoint = System.getProperty("livecontext.ibm.wcs.secureUrl", "https://shop-ref.ecommerce.coremedia.com");
    assertEquals(serviceEndpoint + "/wcs/resources/store/param1value/person/param%20&%202%20%7Bvalue%7D@self?q=param3value&currency=EUR&forUser=mu%26rk%7Be%7Dl&langId=-1",
            requestUri.toString());
  }

  /**
   * Adds the given values to a parameters map
   */
  public Map<String, String[]> createParametersMap(Locale locale, Currency currency, String userName) {
    Map<String, String[]> parameters = new TreeMap<>();
    if (locale != null) {
      parameters.put(PARAM_LANG_ID, new String[]{"-1"});
    }
    if (currency != null) {
      parameters.put(PARAM_CURRENCY, new String[] {currency.toString()});
    }
    if (userName != null && !userName.isEmpty()) {
      parameters.put(PARAM_FOR_USER, new String[] {userName});
    }
    return parameters;
  }

}
