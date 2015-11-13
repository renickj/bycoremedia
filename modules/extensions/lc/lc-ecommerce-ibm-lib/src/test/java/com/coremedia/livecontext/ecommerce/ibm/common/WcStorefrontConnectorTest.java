package com.coremedia.livecontext.ecommerce.ibm.common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClientFactory.class, WcStorefrontConnector.class})
public class WcStorefrontConnectorTest {
  @Test
  public void executeGet() throws GeneralSecurityException, IOException {
    StoreFrontResponse response = testling.executeGet(URI_TEMPLATE, PARAMETERS, sourceRequest);

    assertNotNull(response);
    assertEquals(storeFrontHttpResponse, response.getOriginalResponse());
    assertTrue(response.isSuccess());
    verify(storeFrontHttpGet).setHeader("Cookie", SOURCE_COOKIE_HEADER);
    verify(httpClient).execute(eq(storeFrontHttpGet), any(HttpContext.class));
  }

  @Test (expected = GeneralSecurityException.class)
  public void executeGetGeneralSecurityException() throws GeneralSecurityException, IOException {
    when(storeFrontResponseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_BAD_REQUEST);
    try {
      testling.executeGet(URI_TEMPLATE, PARAMETERS, sourceRequest);
    } finally {
      verify(storeFrontHttpGet).setHeader("Cookie", SOURCE_COOKIE_HEADER);
      verify(httpClient).execute(eq(storeFrontHttpGet), any(HttpContext.class));
    }
  }

  @Test (expected = AuthenticationServiceException.class)
  public void executeGetIOException() throws GeneralSecurityException, IOException {
    when(httpClient.execute(eq(storeFrontHttpGet), any(HttpContext.class))).thenThrow(IOException.class);
    try {
      testling.executeGet(URI_TEMPLATE, PARAMETERS, sourceRequest);
    } finally {
      verify(storeFrontHttpGet).setHeader("Cookie", SOURCE_COOKIE_HEADER);
      verify(httpClient).execute(eq(storeFrontHttpGet), any(HttpContext.class));
    }
  }

  @Before
  public void defaultSetup() throws Exception {
    testling = new WcStorefrontConnector();
    testling.setConnectorHelper(connectorHelper);

    mockStatic(HttpClientFactory.class);
    PowerMockito.when(HttpClientFactory.createHttpClient(anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(httpClient);
    when(httpClient.execute(eq(storeFrontHttpGet), any(HttpContext.class))).thenReturn(storeFrontHttpResponse);
    PowerMockito.whenNew(HttpGet.class).withAnyArguments().thenReturn(storeFrontHttpGet);

    when(connectorHelper.buildRequestUrl(URI_TEMPLATE, PARAMETERS)).thenReturn(storeFrontUriComponents);
    when(storeFrontUriComponents.encode()).thenReturn(storeFrontUriComponents);
    when(storeFrontUriComponents.toUri()).thenReturn(storeFrontUri);
    when(sourceRequest.getHeader("Cookie")).thenReturn(SOURCE_COOKIE_HEADER);
    when(storeFrontHttpResponse.getStatusLine()).thenReturn(storeFrontResponseStatusLine);
    when(storeFrontResponseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
    when(storeFrontHttpResponse.getEntity()).thenReturn(mock(HttpEntity.class));
  }

  private WcStorefrontConnector testling;

  @Mock
  private HttpClient httpClient;

  @Mock
  private ConnectorHelper connectorHelper;

  @Mock
  private UriComponents storeFrontUriComponents;

  @Mock
  private URI storeFrontUri;

  @Mock
  private HttpServletRequest sourceRequest;

  @Mock
  private HttpGet storeFrontHttpGet;

  @Mock
  private HttpResponse storeFrontHttpResponse;

  @Mock
  private StatusLine storeFrontResponseStatusLine;

  private static final Map<String, String> PARAMETERS = new HashMap<>();
  private static final String URI_TEMPLATE = "/{place-a}/{place-b}/Kill-o-Zap-blaster-pistol";
  private static final String SOURCE_COOKIE_HEADER = "Crisis Inducer, Towels";
}