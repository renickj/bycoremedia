package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.ibm.common.StoreFrontResponse;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StoreFrontResponseTest {
  @Test (expected = IllegalArgumentException.class)
  public void createNoOriginalResponse() {
    //noinspection ConstantConditions
    testling = new StoreFrontResponse(null, null);
  }

  @Test
  public void createNoCookies() {
    testling = new StoreFrontResponse(httpResponse, null);

    assertTrue(testling.getCookies().isEmpty());
    assertEquals(HttpStatus.SC_OK, testling.getStatusCode());
    assertTrue(testling.isSuccess());
    assertEquals(httpResponse, testling.getOriginalResponse());
  }

  @Test
  public void create302() {
    when(httpResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_MOVED_TEMPORARILY);
    testling = new StoreFrontResponse(httpResponse, httpClientContext);

    assertEquals(cookies, testling.getCookies());
    assertTrue(testling.isSuccess());
    assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, testling.getStatusCode());
    assertEquals(httpResponse, testling.getOriginalResponse());
  }

  @Test
  public void isSuccess() {
    for (int status = 0; status < 600; status++) {
      when(httpResponse.getStatusLine().getStatusCode()).thenReturn(status);
      testling = new StoreFrontResponse(httpResponse, null);
      if (status >= 200 && status < 300 || status == 302) {
        assertTrue(testling.isSuccess());
      } else {
        assertFalse(testling.isSuccess());
      }
    }
  }

  @Before
  public void defaultSetup() {
    when(httpResponse.getStatusLine().getStatusCode()).thenReturn(HttpStatus.SC_OK);
    cookies = new ArrayList<>();
    Cookie cookie = mock(Cookie.class);
    cookies.add(cookie);
    when(cookie.getName()).thenReturn("cookieName");
    httpClientContext = HttpClientContext.create();
    CookieStore cookieStore = new BasicCookieStore();
    cookieStore.addCookie(cookie);
    httpClientContext.setCookieStore(cookieStore);
  }

  private StoreFrontResponse testling;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private HttpResponse httpResponse;
  private List<Cookie> cookies;
  private HttpClientContext httpClientContext;
}