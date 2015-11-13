package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.ibm.user.UserSessionServiceImpl;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class StoreFrontServiceTest {
  @Test(expected = GeneralSecurityException.class)
  public void testHandleStoreFrontCallGeneralSecurityException() throws GeneralSecurityException {
    //noinspection unchecked
    when(storefrontConnector.executeGet(any(String.class), any(Map.class), any(HttpServletRequest.class))).thenThrow(GeneralSecurityException.class);
    try {
      testling.handleStorefrontCall(STOREFRONT_REQUEST_URL, PARAMETERS, sourceRequest, sourceResponse);
    } finally {
      verifyNoCookiesAtAll();
    }
  }

  @Test
  public void handleStoreFrontCallNoCookies() throws GeneralSecurityException {
    when(storeFrontResponse.getOriginalResponse().getHeaders("Set-Cookie")).thenReturn(null);
    StoreFrontResponse response = testling.handleStorefrontCall(STOREFRONT_REQUEST_URL, PARAMETERS, sourceRequest, sourceResponse);

    assertNotNull(response);
    verifyNoCookiesAtAll();
  }

  @Test
  public void handleStoreFrontCallEmptyCookies() throws GeneralSecurityException {
    when(storeFrontResponse.getOriginalResponse().getHeaders("Set-Cookie")).thenReturn(new Header[]{});
    StoreFrontResponse response = testling.handleStorefrontCall(STOREFRONT_REQUEST_URL, PARAMETERS, sourceRequest, sourceResponse);

    assertNotNull(response);
    verifyNoCookiesAtAll();
  }

  @Test
  public void handleStoreFrontCallMultipleCookies() throws GeneralSecurityException {
    StoreFrontResponse response = testling.handleStorefrontCall(STOREFRONT_REQUEST_URL, PARAMETERS, sourceRequest, sourceResponse);

    assertNotNull(response);
    verify(sourceResponse).addHeader(UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID, GUEST_OR_LOGGEDIN_USER_ID);
    verify(sourceResponse).addHeader(UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID, "DEL");
    verify(sourceResponse, never()).setHeader(any(String.class), any(String.class));
  }

  @Test
  public void isLoggedInStoreFrontNoCookiesAtAll() {
    when(storeFrontResponse.getCookies()).thenReturn(null);
    assertFalse(testling.isKnownUser(storeFrontResponse));
    verify(storeFrontResponse).getCookies();
  }

  @Test
  public void isLoggedInServletRequestNoCookiesAtAll() {
    when(sourceRequest.getCookies()).thenReturn(null);
    assertFalse(testling.isKnownUser(sourceRequest));
    verify(sourceRequest).getCookies();
  }

  @Test
  public void isLoggedInStoreFrontEmptyCookies() {
    when(storeFrontResponse.getCookies()).thenReturn(new ArrayList<Cookie>());
    assertFalse(testling.isKnownUser(storeFrontResponse));
    verify(storeFrontResponse).getCookies();
  }

  @Test
  public void isLoggedInServletRequestEmptyCookies() {
    when(sourceRequest.getCookies()).thenReturn(new javax.servlet.http.Cookie[]{});
    assertFalse(testling.isKnownUser(sourceRequest));
    verify(sourceRequest).getCookies();
  }

  @Test
  public void isLoggedInStoreFrontIrrelevantCookies() {
    initializeCookies(new String[]{
            "By-products of Designer People",
            "Happy Vertical People Transporter"
    }, new String[]{
            "42",
            "42"
    });
    assertFalse(testling.isKnownUser(storeFrontResponse));
    verify(storeFrontResponse).getCookies();
  }

  @Test
  public void isLoggedInServletRequestIrrelevantCookies() {
    initializeCookies(new String[]{
            "By-products of Designer People",
            "Happy Vertical People Transporter"
    }, new String[]{
            "42",
            "42"
    });
    assertFalse(testling.isKnownUser(sourceRequest));
    verify(sourceRequest).getCookies();
  }

  @Test
  public void isLoggedInStoreFrontOnlyDeleteCookies() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_GENERIC_ACTIVITY_COOKIE_NAME
    }, new String[]{
            "DEL",
            "DEL"
    });
    assertFalse(testling.isKnownUser(storeFrontResponse));
    verify(storeFrontResponse).getCookies();
  }

  @Test
  public void isLoggedInServletRequestOnlyDeleteCookies() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_GENERIC_ACTIVITY_COOKIE_NAME
    }, new String[]{
            "DEL",
            "DEL"
    });
    assertFalse(testling.isKnownUser(sourceRequest));
    verify(sourceRequest).getCookies();
  }

  @Test
  public void isLoggedInStoreFrontBlankCookieValues() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_GENERIC_ACTIVITY_COOKIE_NAME
    }, new String[]{
            "",
            ""
    });
    assertFalse(testling.isKnownUser(storeFrontResponse));
    verify(storeFrontResponse).getCookies();
  }

  @Test
  public void isLoggedInServletRequestBlankCookieValues() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_GENERIC_ACTIVITY_COOKIE_NAME
    }, new String[]{
            "",
            ""
    });
    assertFalse(testling.isKnownUser(sourceRequest));
    verify(sourceRequest).getCookies();
  }

  @Test
  public void isLoggedInStoreFrontAnonymousToGuest() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID,
            UserSessionServiceImpl.IBM_GENERIC_ACTIVITY_COOKIE_NAME
    }, new String[]{
            "38009",
            "DEL",
            "DEL"
    });
    assertTrue(testling.isKnownUser(storeFrontResponse));
    verify(storeFrontResponse).getCookies();
  }

  @Test
  public void isLoggedInServletRequestAnonymousToGuest() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID,
            UserSessionServiceImpl.IBM_GENERIC_ACTIVITY_COOKIE_NAME
    }, new String[]{
            "38009",
            "DEL",
            "DEL"
    });
    assertTrue(testling.isKnownUser(sourceRequest));
    verify(sourceRequest).getCookies();
  }

  @Test
  public void isLoggedInStoreFrontGuestToAnonymous() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID,
            UserSessionServiceImpl.IBM_GENERIC_ACTIVITY_COOKIE_NAME
    }, new String[]{
            "DEL",
            "-1002",
            "other-than-DEL"
    });
    assertFalse(testling.isKnownUser(storeFrontResponse));
    verify(storeFrontResponse).getCookies();
  }

  @Test
  public void isLoggedInServletRequestGuestToAnonymous() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID,
            UserSessionServiceImpl.IBM_GENERIC_ACTIVITY_COOKIE_NAME
    }, new String[]{
            "DEL",
            "-1002",
            "other-than-DEL"
    });
    assertFalse(testling.isKnownUser(sourceRequest));
    verify(sourceRequest).getCookies();
  }

  @Test
  public void isLoggedInStoreFrontInvalidGuestAsWellAsAnonymous() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID,
            UserSessionServiceImpl.IBM_GENERIC_ACTIVITY_COOKIE_NAME
    }, new String[]{
            "39002",
            "-1002",
            "other-than-DEL"
    });
    assertFalse(testling.isKnownUser(storeFrontResponse));
    verify(storeFrontResponse).getCookies();
  }

  @Test
  public void isLoggedInServletRequestInvalidGuestAsWellAsAnonymous() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID,
            UserSessionServiceImpl.IBM_GENERIC_ACTIVITY_COOKIE_NAME
    }, new String[]{
            "39002",
            "-1002",
            "other-than-DEL"
    });
    assertFalse(testling.isKnownUser(sourceRequest));
    verify(sourceRequest).getCookies();
  }

  @Test
  public void isLoggedInStoreFrontSuccessfully() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID
    }, new String[]{
            "39002"
    });
    assertTrue(testling.isKnownUser(storeFrontResponse));
    verify(storeFrontResponse).getCookies();
  }

  @Test
  public void isLoggedInServletRequestSuccessfully() {
    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID
    }, new String[]{
            "39002"
    });
    assertTrue(testling.isKnownUser(sourceRequest));
    verify(sourceRequest).getCookies();
  }

  @Test
  public void resolveStoreId() {
    assertEquals(STORE_ID, testling.resolveStoreId());
  }

  @Test
  public void resolveCatalogId() {
    assertEquals(CATALOG_ID, testling.resolveCatalogId());
  }

  @Before
  public void setup() throws GeneralSecurityException {
    testling = new StoreFrontService() {
    };
    testling.setStorefrontConnector(storefrontConnector);
    testling.setStoreContextProvider(storeContextProvider);
    testling.setUrlProvider(urlProvider);

    //noinspection unchecked
    when(storefrontConnector.executeGet(any(String.class), any(Map.class), any(HttpServletRequest.class))).thenReturn(storeFrontResponse);
    when(storeContextProvider.getCurrentContext().getStoreId()).thenReturn(STORE_ID);
    when(storeContextProvider.getCurrentContext().getCatalogId()).thenReturn(CATALOG_ID);

    initializeCookies(new String[]{
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + GUEST_OR_LOGGEDIN_USER_ID,
            UserSessionServiceImpl.IBM_WC_USERACTIVITY_COOKIE_NAME + ANONYMOUS_USER_ID,
    }, new String[]{
            GUEST_OR_LOGGEDIN_USER_ID,
            "DEL"
    });
  }

  private void verifyNoCookiesAtAll() {
    verify(sourceResponse, never()).addHeader(any(String.class), any(String.class));
    verify(sourceResponse, never()).setHeader(any(String.class), any(String.class));
  }

  private void initializeCookies(String[] names, String[] values) {
    if (names.length > 0) {
      wcsResponseCookies = new Header[names.length];
      storeFrontCookies = new ArrayList<>();
      sourceRequestCookies = new javax.servlet.http.Cookie[names.length];
      for (int i = 0; i < names.length; i++) {
        Header header = mock(Header.class);
        when(header.getName()).thenReturn(names[i]);
        when(header.getValue()).thenReturn(values[i]);
        wcsResponseCookies[i] = header;

        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn(names[i]);
        when(cookie.getValue()).thenReturn(values[i]);
        storeFrontCookies.add(cookie);

        javax.servlet.http.Cookie servletCookie = mock(javax.servlet.http.Cookie.class);
        when(servletCookie.getName()).thenReturn(names[i]);
        when(servletCookie.getValue()).thenReturn(values[i]);
        sourceRequestCookies[i] = servletCookie;
      }
    }

    when(storeFrontResponse.getOriginalResponse().getHeaders("Set-Cookie")).thenReturn(wcsResponseCookies);
    when(storeFrontResponse.getCookies()).thenReturn(storeFrontCookies);
    when(sourceRequest.getCookies()).thenReturn(sourceRequestCookies);
  }

  private StoreFrontService testling;

  @Mock
  private WcStorefrontConnector storefrontConnector;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private StoreContextProvider storeContextProvider;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private StoreFrontResponse storeFrontResponse;

  @Mock
  private HttpServletRequest sourceRequest;

  @Mock
  private HttpServletResponse sourceResponse;

  @Mock
  private CommerceUrlPropertyProvider urlProvider;

  private Header[] wcsResponseCookies;
  private List<Cookie> storeFrontCookies;
  private javax.servlet.http.Cookie[] sourceRequestCookies;

  private static final String STORE_ID = "Sirius Cybernetics Corporation";
  private static final String CATALOG_ID = "Total Perspective Vortex";
  private static final String STOREFRONT_SECURE_URL = "billion-year-bunker";
  private static final String STOREFRONT_REQUEST_URL = "crash";
  private static final Map<String, String> PARAMETERS = new HashMap<>();
  private static final String GUEST_OR_LOGGEDIN_USER_ID = "38009";
  private static final String ANONYMOUS_USER_ID = "-1002";
}