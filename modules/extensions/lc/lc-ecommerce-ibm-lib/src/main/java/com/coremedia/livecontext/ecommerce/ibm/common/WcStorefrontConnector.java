package com.coremedia.livecontext.ecommerce.ibm.common;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.util.UriComponents;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Connector to execute regular HTTP request (not REST) calls against the WCS system.
 */
public class WcStorefrontConnector {
  private HttpClient httpClient;
  private int connectionRequestTimeout = -1;
  private int connectionTimeout = -1;
  private int socketTimeout = -1;
  private int connectionPoolSize = 200;

  @Nonnull
  public StoreFrontResponse executeGet(
          @Nonnull String requestTemplateUri,
          @Nonnull Map<String, String> parameters,
          @Nonnull HttpServletRequest sourceRequest) throws GeneralSecurityException {
    HttpClient client = getHttpClient();

    CookieStore cookieStore = new BasicCookieStore();
    HttpClientContext localContext = buildRequestContext(cookieStore);
    UriComponents encodedUriComponent = connectorHelper.buildRequestUrl(requestTemplateUri, parameters).encode();
    URI requestUri = encodedUriComponent.toUri();
    HttpResponse response = null;
    HttpGet upgradeRequest = new HttpGet(requestUri);
    copySourceCookies(sourceRequest, upgradeRequest);
    try {
      long start = 0L;
      if (LOG.isTraceEnabled()) {
        start = System.currentTimeMillis();
      }

      response = client.execute(upgradeRequest, localContext);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();

      if (LOG.isTraceEnabled()) {
        long time = System.currentTimeMillis() - start;
        LOG.trace(upgradeRequest.getMethod() + " " + requestUri + ": " + statusCode + " took " + time + " ms");
      }

      StoreFrontResponse storeFrontResponse = new StoreFrontResponse(response, localContext);
      boolean isSuccess = storeFrontResponse.isSuccess();
      if (!isSuccess) {
        throw new GeneralSecurityException(String.format("StoreFront call failed: %s (%s):%n%s",
                formatUrlForLogging(upgradeRequest.getURI()), storeFrontResponse.getStatusCode(), storeFrontResponse));
      }

      return storeFrontResponse;
    } catch (IOException e) {
      String msg = String.format("Upgrade registered user request failed: %s", upgradeRequest.getURI());
      LOG.error(msg, e);
      throw new AuthenticationServiceException(msg, e);
    } finally {
      if (response != null) {
        EntityUtils.consumeQuietly(response.getEntity());
      }
    }
  }

  private HttpClient getHttpClient() {
    if (httpClient == null) {
      httpClient = HttpClientFactory.createHttpClient(true, true,
              connectionPoolSize, socketTimeout, connectionTimeout, connectionRequestTimeout);
    }
    return httpClient;
  }

  /**
   * Ensures that no passwords are logged.
   * @param uri the erroneous URL that should be logged.
   */
  private String formatUrlForLogging(URI uri) {
    if(uri != null) {
      String url = uri.toString();
      return url.replaceAll("logonPassword\\=.*&", "logonPassword=***&"); // NOSONAR false positive: Credentials should not be hard-coded
    }
    return null;
  }

  private void copySourceCookies(
          @Nonnull HttpServletRequest sourceRequest,
          @Nonnull HttpGet storeFrontRequest) {
    String sourceCookiesString = sourceRequest.getHeader("Cookie");
    storeFrontRequest.setHeader("Cookie", sourceCookiesString);
  }

  private HttpClientContext buildRequestContext(CookieStore cookieStore) {
    HttpContext localContext = new BasicHttpContext();
    localContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
    return HttpClientContext.adapt(localContext);
  }

  @Required
  public void setConnectorHelper(ConnectorHelper connectorHelper) {
    this.connectorHelper = connectorHelper;
  }

  private ConnectorHelper connectorHelper;

  private static Logger LOG = LoggerFactory.getLogger(WcStorefrontConnector.class);

  @SuppressWarnings("unused")
  public int getConnectionRequestTimeout() {
    return connectionRequestTimeout;
  }

  @SuppressWarnings("unused")
  public void setConnectionRequestTimeout(int connectionRequestTimeout) {
    this.connectionRequestTimeout = connectionRequestTimeout;
  }

  @SuppressWarnings("unused")
  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  @SuppressWarnings("unused")
  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  @SuppressWarnings("unused")
  public int getSocketTimeout() {
    return socketTimeout;
  }

  @SuppressWarnings("unused")
  public void setSocketTimeout(int socketTimeout) {
    this.socketTimeout = socketTimeout;
  }

  @SuppressWarnings("unused")
  public int getConnectionPoolSize() {
    return connectionPoolSize;
  }

  @SuppressWarnings("unused")
  public void setConnectionPoolSize(int connectionPoolSize) {
    this.connectionPoolSize = connectionPoolSize;
  }
}
