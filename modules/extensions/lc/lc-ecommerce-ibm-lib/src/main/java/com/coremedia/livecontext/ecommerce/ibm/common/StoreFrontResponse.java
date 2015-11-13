package com.coremedia.livecontext.ecommerce.ibm.common;

import com.google.common.base.Preconditions;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the {@link #getOriginalResponse() HTTP response} of a call to the
 * {@link com.coremedia.livecontext.ecommerce.ibm.common.StoreFrontService store front}.
 *
 * @see com.coremedia.livecontext.ecommerce.ibm.common.StoreFrontService
 */
public class StoreFrontResponse {
  /**
   * A store front response must always contain the original Http response which it encapsulates.
   * The cookies may be null.
   *
   * @param response mandatory, the original HTTP response of the store front call
   * @param httpContext the http client context
   */
  public StoreFrontResponse(@Nonnull HttpResponse response, HttpClientContext httpContext) {
    //noinspection ConstantConditions
    Preconditions.checkArgument(response != null, "Source sourceResponse must be given.");

    this.sourceResponse = response;
    statusCode = response.getStatusLine().getStatusCode();
    this.httpClientContext = httpContext;
  }

  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Returns the list of cookies that were set by the store front. These cookies may be used by the
   * CAE to interpret the status of the user session in the store front.
   *
   * @return the list of cookies that were set by the store front
   */
  public List<Cookie> getCookies() {
    if (httpClientContext == null || httpClientContext.getCookieStore() == null) {
      return Collections.emptyList();
    }
    return httpClientContext.getCookieStore().getCookies();
  }

  /**
   * Returns <code>true</code> if the {@link #getOriginalResponse() original store front request} returned
   * with a HTTP status code bigger or equal to 200 and less than 300 or if it was answered with
   * <code>302</code> - moved temporary.
   *
   * @return <code>true</code> for a HTTP code between 200 and 300 or for a 302
   * @see {@link com.coremedia.livecontext.ecommerce.ibm.common.HttpClientFactory} for
   * why a 302 is interpreted as success.
   */
  public boolean isSuccess() {
    return (statusCode >= 200 && statusCode < 300) || statusCode == HttpStatus.SC_MOVED_TEMPORARILY;
  }

  /**
   * A store front call is a HTTP call to the commerce systems store front in contrast to a call to its
   * REST Api.
   *
   * @return the original HTTP response that was received from the store front.
   */
  public HttpResponse getOriginalResponse() {
    return sourceResponse;
  }

  /**
   * Return the http context used for the store front http request. Contains the cookies set by the server response.
   */
  public HttpClientContext getHttpClientContext() {
    return httpClientContext;
  }

  public String toString() {
    return sourceResponse.toString();
  }

  private int statusCode;
  private HttpResponse sourceResponse;
  private HttpClientContext httpClientContext;

  private static final Logger LOG = LoggerFactory.getLogger(StoreFrontResponse.class);
}
