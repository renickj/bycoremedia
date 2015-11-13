package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceUrlPropertyProvider;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreFrontResponse;
import com.coremedia.livecontext.ecommerce.ibm.common.WcStorefrontConnector;
import com.coremedia.livecontext.ecommerce.ibm.login.WcLoginWrapperService;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.objectserver.web.links.TokenResolverHelper;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.util.UriComponentsBuilder;

import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserSessionServiceImplTest {

  @Test
  public void loginUserNoStoreId() {
    commerceConnection.getStoreContext().put(StoreContextBuilder.STORE_ID, null);
    assertFalse(testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD));
    verifyNoCookiesAtAll();
  }

  @SuppressWarnings("unchecked")
  @Test(expected = AuthenticationServiceException.class)
  public void loginUserAuthenticationException() throws GeneralSecurityException {
    when(storefrontConnector.executeGet(any(String.class), any(Map.class), any(HttpServletRequest.class))).thenThrow(AuthenticationServiceException.class);
    try {
      testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD);
    } finally {
      verifyNoCookiesAtAll();
    }
  }

  @Test
  public void loginUserNoCookiesFromWCS() throws GeneralSecurityException {
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL).build());
    when(wcsResponse.getHeaders("Set-Cookie")).thenReturn(null);
    when(storeFrontResponse.getCookies()).thenReturn(Collections.<Cookie>emptyList());
    boolean loggedIn = testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD);

    assertFalse(loggedIn);
    verifyCookies(UserSessionServiceImpl.LOGON_URL, 0);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void loginUserGenericSecurityException() throws GeneralSecurityException {
    when(storefrontConnector.executeGet(any(String.class), any(Map.class), any(HttpServletRequest.class))).thenThrow(GeneralSecurityException.class);
    assertFalse(testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD));
    verifyNoCookiesAtAll();
  }

  @Test
  public void loginUserIrrelevantCookies() throws GeneralSecurityException {
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL).build());
    initializeCookies(new String[]{
                    "Happy-Vertical-People-Transporter",
                    "Matter-transference-beams"},
            new String[]{
                    "42",
                    "irrelevant"}
    );

    boolean loggedIn = testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD);
    assertFalse(loggedIn);
    verifyCookies(UserSessionServiceImpl.LOGON_URL, 2);
  }

  @Test
  public void loginUserSuccessfully() throws GeneralSecurityException {
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL).build());
    initializeCookies(new String[]{
                    UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
                    "Matter-transference-beams"},
            new String[]{
                    "42",
                    "irrelevant"}
    );

    boolean loggedIn = testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD);
    assertTrue(loggedIn);
    verifyCookies(UserSessionServiceImpl.LOGON_URL, 2);
  }

  @Test
  public void loginUserSuccessfullyWithDeletedAnonymousCookie() throws GeneralSecurityException {
    initializeCookies(new String[]{
                    UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
                    UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID,
                    "Matter-transference-beams"},
            new String[]{
                    "42",
                    "DEL",
                    "irrelevant"}
    );
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGON_URL).build());

    boolean loggedIn = testling.loginUser(sourceRequest, sourceResponse, USERNAME, PASSWORD);
    assertTrue(loggedIn);
    verifyCookies(UserSessionServiceImpl.LOGON_URL, 3);
  }

  @Test
  public void logoutUserNoStoreId() throws GeneralSecurityException {
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGOUT_URL).build());
    commerceConnection.getStoreContext().put(StoreContextBuilder.STORE_ID, null);
    assertTrue(testling.logoutUser(sourceRequest, sourceResponse));
    verifyNoCookiesAtAll();
  }

  @Test
  public void logoutUserSuccessfully() throws GeneralSecurityException {
    when(urlProvider.provideValue(Matchers.anyMap())).thenReturn(UriComponentsBuilder.fromUriString(STOREFRONT_SECURE_URL + UserSessionServiceImpl.LOGOUT_URL).build());
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID,
    }, new String[]{
            "DEL",
            ANONYMOUS_USER_ID
    });

    boolean loggedOut = testling.logoutUser(sourceRequest, sourceResponse);
    assertTrue(loggedOut);
    verifyCookies(UserSessionServiceImpl.LOGOUT_URL, 2);
  }

  @Test
  public void isLoggedInNoPersonAtAll() throws CredentialExpiredException {
    when(wcLoginWrapperService.isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class))).thenReturn(false);

    assertFalse(testling.isLoggedIn());
    verify(wcLoginWrapperService).isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class));
  }

  @Test
  public void isLoggedInException() throws CredentialExpiredException {
    when(wcLoginWrapperService.isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class))).thenThrow(new CommerceException(""));

    assertFalse(testling.isLoggedIn());
    verify(wcLoginWrapperService).isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class));
  }

  @Test
  public void isLoggedInUserUnknown() throws CredentialExpiredException {
    when(wcLoginWrapperService.isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class))).thenReturn(true);
    when(commerceConnection.getUserService().findCurrentUser()).thenReturn(null);

    assertFalse(testling.isLoggedIn());
  }

  @Test
  public void isLoggedInSuccessfully() throws CredentialExpiredException {
    when(wcLoginWrapperService.isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class))).thenReturn(true);
    when(commerceConnection.getUserService().findCurrentUser()).thenReturn(user);

    assertTrue(testling.isLoggedIn());
    verify(wcLoginWrapperService).isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class));
  }

  @Test
  public void isAnonymousUser() throws CredentialExpiredException {
    UserContext userContext = UserContextHelper.createContext("", "");
    UserContextHelper.setCurrentContext(userContext);

    assertFalse(testling.isLoggedIn());
    verify(wcLoginWrapperService, times(0)).isLoggedIn(anyString(), any(StoreContext.class), any(UserContext.class));
  }

  @SuppressWarnings("unchecked")
  @Before
  public void defaultSetup() throws GeneralSecurityException {

    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
    UserContext context = commerceConnection.getUserContext();
    context.setUserId(USERID);

    testling = new UserSessionServiceImpl();
    testling.setStoreContextProvider(commerceConnection.getStoreContextProvider());
    testling.setUrlProvider(urlProvider);
    testling.setStorefrontConnector(storefrontConnector);
    CommerceCache commerceCache = new CommerceCache();
    commerceCache.setEnabled(false);
    commerceCache.setCacheTimesInSeconds(Collections.EMPTY_MAP);
    testling.setCommerceCache(commerceCache);
    testling.setLoginWrapperService(wcLoginWrapperService);

    when(storefrontConnector.executeGet(Matchers.contains("Logon"), any(Map.class), any(HttpServletRequest.class))).thenReturn(storeFrontResponse);
    when(storefrontConnector.executeGet(Matchers.contains("Logoff"), any(Map.class), any(HttpServletRequest.class))).thenReturn(storeFrontResponse);

    when(storeFrontResponse.getOriginalResponse()).thenReturn(wcsResponse);
    when(userActivityCookie.getName()).thenReturn(UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID);
    when(userActivityCookie.getValue()).thenReturn("irrelevant");

    UserService userService = commerceConnection.getUserService();
    testling.setUserService(userService);
    when(userService.registerUser(USERNAME, PASSWORD, EMAIL)).thenReturn(user);

    user = new UserImpl() {
      @Override
      public String getLogonId() {
        return USERNAME;
      }
      @Override
      public String getUserId() {
        return USERID;
      }
    };

    when(commerceConnection.getCommerceBeanFactory().createBeanFor(anyString(), any(StoreContext.class))).thenReturn(user);

    when(anonymousUser.getLogonId()).thenReturn(null);
    when(registeredUser.getLogonId()).thenReturn("yes");

    wcsResponseCookies = null;
    storeFrontCookies = null;
  }

  private void initializeCookies(String[] names, String[] values) {
    if (names.length > 0) {
      wcsResponseCookies = new Header[names.length];
      storeFrontCookies = new ArrayList<>();
      for (int i = 0; i < names.length; i++) {
        Header header = mock(Header.class);
        when(header.getName()).thenReturn(names[i]);
        when(header.getValue()).thenReturn(values[i]);
        wcsResponseCookies[i] = header;

        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn(names[i]);
        when(cookie.getValue()).thenReturn(values[i]);
        storeFrontCookies.add(cookie);
      }
    }

    when(wcsResponse.getHeaders("Set-Cookie")).thenReturn(wcsResponseCookies);
    when(storeFrontResponse.getCookies()).thenReturn(storeFrontCookies);
  }

  private void verifyCookies(String uri, int countCookies) throws GeneralSecurityException {
    //noinspection unchecked
    verify(storefrontConnector).executeGet(eq(STOREFRONT_SECURE_URL + uri), any(Map.class), eq(sourceRequest));
    verify(storeFrontResponse).getCookies();
    verify(sourceResponse, times(countCookies)).addHeader(any(String.class), any(String.class));
    verify(sourceResponse, never()).setHeader(any(String.class), any(String.class));
  }

  private void verifyNoCookiesAtAll() {
    verify(sourceResponse, never()).addHeader(any(String.class), any(String.class));
    verify(sourceResponse, never()).setHeader(any(String.class), any(String.class));
  }

  @Mock
  private WcStorefrontConnector storefrontConnector;

  @Mock
  private StoreFrontResponse storeFrontResponse;

  @Mock
  private User anonymousUser;

  @Mock
  private User registeredUser;

  @Mock
  private HttpResponse wcsResponse;

  @Mock
  private Cookie userActivityCookie;

  @Mock
  private HttpServletRequest sourceRequest;

  @Mock
  private HttpServletResponse sourceResponse;

  @Mock
  private StoreContext storeContext;

  @Mock
  private WcLoginWrapperService wcLoginWrapperService;

  @Mock
  private CommerceUrlPropertyProvider urlProvider;

  /*@Mock
  private WcPerson wcPerson;*/

  private User user;

  private BaseCommerceConnection commerceConnection;

  private UserSessionServiceImpl testling;

  // The list of all "Set-Cookie" header returned in the wcsResponse
  private Header[] wcsResponseCookies;

  // The list of all store front response cookies matching the wcsResponseCookies
  private List<Cookie> storeFrontCookies;

  private static final String USERNAME = "zaphod";
  private static final String USERID = "42";
  private static final String PASSWORD = "trish";
  private static final String EMAIL = "trish@zaphod.com";
  private static final String STOREFRONT_SECURE_URL = "Doors";
  private static final String GUEST_OR_LOGGEDIN_USER_ID = "38009";
  private static final String ANONYMOUS_USER_ID = "-1002";

}
