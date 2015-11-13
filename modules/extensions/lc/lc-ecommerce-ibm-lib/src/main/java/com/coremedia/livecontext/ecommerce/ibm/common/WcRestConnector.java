package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteError;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.UnknownUserException;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import com.coremedia.livecontext.ecommerce.ibm.login.WcCredentials;
import com.coremedia.livecontext.ecommerce.ibm.login.WcPreviewToken;
import com.coremedia.livecontext.ecommerce.ibm.login.WcSession;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.apache.http.client.utils.HttpClientUtils.closeQuietly;

// make the service call once

public class WcRestConnector {

  private static final String ERROR_KEY_INVALID_COOKIE = "_ERR_INVALID_COOKIE";
  private static final String ERROR_KEY_AUTHENTICATION = "_ERR_AUTHENTICATION_ERROR";
  private static final String ERROR_KEY_ACTIVITY_TOKEN_TERMINATED = "CWXBB1012E";
  private static final String ERROR_KEY_ACTIVITY_TOKEN_INVALID = "CWXBB1010E";

  private static final Logger LOG = LoggerFactory.getLogger(WcRestConnector.class);
  private static final String HEADER_CONTENT_TYPE = "Content-Type";

  public static final String MIME_TYPE_JSON = "application/json";

  private static final String HEADER_WC_TOKEN = "WCToken";
  private static final String HEADER_WC_TRUSTED_TOKEN = "WCTrustedToken";
  private static final String HEADER_WC_PREVIEW_TOKEN = "WCPreviewToken";
  private static final String HEADER_COOKIE = "Cookie";
  private static final String WCS_SECURE_COOKIE_PREFIX = "WC_AUTHENTICATION_";
  private static final String WCS_SECURE_COOKIE_PATTERN_STRING = "(^|;)" + WCS_SECURE_COOKIE_PREFIX + "(;|$)";
  private static final Pattern WCS_SECURE_COOKIE_PATTERN = Pattern.compile(WCS_SECURE_COOKIE_PATTERN_STRING);
  private static final String POSITION_RELATIVE_TEMPLATE_VARIABLE = "{ignored}";

  private String serviceEndpoint;
  private String searchServiceEndpoint;
  private String serviceSslEndpoint;
  private String searchServiceSslEndpoint;
  protected boolean trustAllSslCertificates = false;

  private HttpClient httpClient;
  private int connectionRequestTimeout = -1;
  private int connectionTimeout = -1;
  private int socketTimeout = -1;
  private int connectionPoolSize = 200;

  protected LoginService loginService;

  // BOD based service methods

  public static <T> WcRestServiceMethod<T, Void> createServiceMethod(HttpMethod method, String url, boolean secure, boolean requiresAuthentication, Class<T> returnType) {
    return new WcRestServiceMethod<>(method, url, secure, requiresAuthentication, false, true, false, false, Void.class, returnType);
  }

  public static <T, P> WcRestServiceMethod<T, P> createServiceMethod(HttpMethod method, String url, boolean secure, boolean requiresAuthentication, Class<P> parameterType, Class<T> returnType) {
    return new WcRestServiceMethod<>(method, url, secure, requiresAuthentication, false, true, false, false, parameterType, returnType);
  }

  public static <T, P> WcRestServiceMethod<T, P> createServiceMethod(HttpMethod method, String url, boolean secure, boolean requiresAuthentication, boolean previewSupport, Class<P> parameterType, Class<T> returnType) {
    return new WcRestServiceMethod<>(method, url, secure, requiresAuthentication, false, previewSupport, false, false, parameterType, returnType);
  }

  public static <T, P> WcRestServiceMethod<T, P> createServiceMethod(HttpMethod method, String url, boolean secure, boolean requiresAuthentication, boolean previewSupport, boolean userCookieSupport, Class<P> parameterType, Class<T> returnType) {
    return new WcRestServiceMethod<>(method, url, secure, requiresAuthentication, false, previewSupport, userCookieSupport, false, parameterType, returnType);
  }

  public static <T, P> WcRestServiceMethod<T, P> createServiceMethod(HttpMethod method, String url, boolean secure, boolean requiresAuthentication, boolean previewSupport, boolean userCookieSupport, boolean contractsSupport, Class<P> parameterType, Class<T> returnType) {
    return new WcRestServiceMethod<>(method, url, secure, requiresAuthentication, false, previewSupport, userCookieSupport, contractsSupport, parameterType, returnType);
  }

  // Search bases service methods

  public static <T> WcRestServiceMethod<T, Void> createSearchServiceMethod(HttpMethod method, String url, boolean secure, boolean requiresAuthentication, boolean previewSupport, boolean userCookieSupport, boolean contractsSupport, Class<T> returnType) {
    return new WcRestServiceMethod<>(method, url, secure, requiresAuthentication, true, previewSupport, userCookieSupport, contractsSupport, Void.class, returnType);
  }

  public static <T> WcRestServiceMethod<T, Void> createSearchServiceMethod(HttpMethod method, String url, boolean secure, boolean requiresAuthentication, boolean previewSupport, Class<T> returnType) {
    return new WcRestServiceMethod<>(method, url, secure, requiresAuthentication, true, previewSupport, false, false, Void.class, returnType);
  }

  public static <T> WcRestServiceMethod<T, Void> createSearchServiceMethod(HttpMethod method, String url, boolean secure, boolean requiresAuthentication, Class<T> returnType) {
    return new WcRestServiceMethod<>(method, url, secure, requiresAuthentication, true, true, false, false, Void.class, returnType);
  }

  /**
   * Calls the service and returns the JSON response.
   *
   * @param serviceMethod      the service method to call
   * @param variableValues     variables to replace in the {@link WcRestServiceMethod#uriTemplate URI template} of the serviceMethod
   * @param optionalParameters parameters which are appended as query parameters (no variable replacement will be performed here!)
   * @param bodyData           model that represent body data for post, put etc.
   * @param storeContext       the store context that should be used for this call
   * @param userContext        credentials for services which require authentication
   */
  public <T, P> T callService(WcRestServiceMethod<T, P> serviceMethod,
                              List<String> variableValues,
                              final Map<String, String[]> optionalParameters,
                              P bodyData,
                              StoreContext storeContext,
                              UserContext userContext) throws CommerceException {

    StoreContext myStoreContext = storeContext != null ? storeContext : StoreContextHelper.getCurrentContext();

    try {
      // make the service call once
      return callServiceInternal(serviceMethod, variableValues, optionalParameters, bodyData, myStoreContext, userContext);

    } catch (UnauthorizedException e) {
      LOG.info("Commerce connector responded with 'Unauthorized'. Will renew the session and retry.");
      StoreContextHelper.setCurrentContext(myStoreContext);
      loginService.renewServiceIdentityLogin();
      // make the service call the second time
      return callServiceInternal(serviceMethod, variableValues, optionalParameters, bodyData, myStoreContext, userContext);
    }
  }

  /**
   * Calls the service and returns the JSON response. Attention: This method is for intern use only because
   * no attempt will be made to retry the call if a the current wcs session is outdated. This method will only
   * be used for calls that (re)establish such a session.
   *
   * @param serviceMethod      the service method to call
   * @param variableValues     variables to replace in the URL string
   * @param optionalParameters parameters which are appended as query parameters
   * @param bodyData           model that represent body data for post, put etc.
   * @param storeContext       the store context that should be used for this call
   * @param userContext        credentials for services which require authentication
   */
  public <T, P> T callServiceInternal(WcRestServiceMethod<T, P> serviceMethod,
                                      List<String> variableValues,
                                      final Map<String, String[]> optionalParameters,
                                      P bodyData,
                                      StoreContext storeContext,
                                      UserContext userContext) throws CommerceException {

    // it repairs a shortcomming of betamax that cannot record calls that use https
    if ("READ_WRITE".equals(System.getProperty("betamax.defaultMode"))) {
      serviceMethod.secure = false;
    }
    //TODO: only make service call if storeContext is valid
    /*try {
      if (storeContext != null) {
        StoreContextHelper.validateContext(storeContext);
      }
    } catch (InvalidContextException ex) {
      //store context not valid
      LOG.warn("StoreContext not valid: ", ex);
      return null;
    }*/

    T result = null;

    boolean mustBeSecured = mustBeSecured(serviceMethod, storeContext, userContext);

    Map<String, String> additionalHeaders = getRequiredHeaders(serviceMethod, mustBeSecured, storeContext, userContext);

    URI uri;
    try {
      uri = buildRequestUri(serviceMethod.uriTemplate, mustBeSecured, serviceMethod.search, variableValues, optionalParameters);
      if (!isCommerceAvailable(serviceMethod.method, uri, storeContext)) {
        return null;
      }
    } catch (IllegalArgumentException e) {
      LOG.warn("unable to derive REST URI components for method {} with vars {} and optional params {}", serviceMethod, variableValues, optionalParameters);
      return null;
    }

    HttpUriRequest httpClientRequest = getRequest(uri, serviceMethod, bodyData, additionalHeaders);

    try {
      HttpClient client = getHttpClient();

      long start = 0L;
      if (LOG.isTraceEnabled()) {
        start = System.currentTimeMillis();
      }

      HttpResponse response = client.execute(httpClientRequest);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();

      if (LOG.isTraceEnabled()) {
        long time = System.currentTimeMillis() - start;
        LOG.trace(serviceMethod.method + " " + uri + ": " + statusCode + " took " + time + " ms");
      }

      try {
        HttpEntity entity;
        CommerceRemoteError remoteError = null;

        //Handle success here
        if (statusCode >= 200 && statusCode != 204 && statusCode < 300) {
          entity = response.getEntity();
          if (entity != null) {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            result = gson.fromJson(rd, serviceMethod.returnType);
          } else {
            LOG.trace("response entity is null");
          }
        } else {

          // Parse Remote-Errors
          List<CommerceRemoteError> commerceRemoteErrors = parseServiceErrors(response);
          if (!commerceRemoteErrors.isEmpty()) {
            remoteError = commerceRemoteErrors.get(0);
          }

          // Handle Authentication Error
          if (statusCode == 401 || (statusCode == 400 && serviceMethod.requiresAuthentication && isAuthenticationError(remoteError))) {
            throw new UnauthorizedException(remoteError);
          }

          String user = null;
          if (optionalParameters != null) {
            String[] values = optionalParameters.get("forUser");
            if (values != null && values.length > 0) {
              user = values[0];
            }
            if (user == null) {
              values = optionalParameters.get("forUserId");
              if (values != null && values.length > 0) {
                user = values[0];
              }
            }
          }

          // Handle Unknown User
          if (statusCode == 400 && user != null && isUnknownUserError(remoteError)) {
            throw new UnknownUserException(user, remoteError);
          }

          // Handle not found...and return null
          // most queries with no result return 404
          // and others (like category by unknown seo segment) return a 204 (no content)
          else if (statusCode == 404 || statusCode == 204) {
            LOG.trace("result from " + httpClientRequest.getURI() + " will be interpreted as \"no result found\": " +
                    statusCode + " (" + statusLine.getReasonPhrase() + ")");

          } else if (remoteError != null) {
            throw new CommerceRemoteException(
                    format("Remote Error occurred: %s (Error Key: %s, Error Code: %s",
                            remoteError.getErrorMessage(),
                            remoteError.getErrorKey(),
                            remoteError.getErrorCode()),
                    statusCode,
                    remoteError);
          }

          //all other result codes (e.g. 500, 502)
          else {
            if (LOG.isWarnEnabled()) {
              LOG.warn("call to \"" + httpClientRequest.getURI() + "\" returns " + statusCode + " (" + statusLine.getReasonPhrase() + ")");
            }
            throw new CommerceException("call to \"" + httpClientRequest.getURI() + "\" returns " + statusCode + " (" + statusLine.getReasonPhrase() + ")");
          }
        }
      } finally {
        closeQuietly(response);
      }

    } catch (CommerceException e) {
      throw e;
    } catch (IOException e) {
      LOG.warn("Network error occurred while calling WCS: {} ({})", httpClientRequest.getURI(), e.getMessage());
      LOG.trace("The corresponding stacktrace is...", e);
      StoreContextHelper.setCommerceSystemIsUnavailable(storeContext, true);
      throw new CommerceException(e);
    } catch (Exception e) {
      LOG.warn("Error while calling WCS: {} ({})", httpClientRequest.getURI(), e.getMessage());
      LOG.trace("The corresponding stacktrace is...", e);
      throw new CommerceException(e);
    }
    return result;
  }

  private <T, P> boolean mustBeSecured(WcRestServiceMethod<T, P> serviceMethod, StoreContext storeContext, UserContext userContext) {
    if (serviceMethod.secure) {
      return true;
    }
    if (serviceMethod.previewSupport && storeContext != null && storeContext.hasPreviewContext()) {
      WcPreviewToken previewToken = loginService.getPreviewToken();
      if (previewToken != null) {
        return true;
      }
    }
    String cookieHeader = userContext != null ? userContext.getCookieHeader() : null;
    return cookieHeader != null && WCS_SECURE_COOKIE_PATTERN.matcher(cookieHeader).find();
  }

  /**
   * Parses ibm remote errors from JSON-Response.
   */
  private List<CommerceRemoteError> parseServiceErrors(HttpResponse response) {
    if (response.getEntity() == null) {
      return Collections.emptyList();
    }
    try {
      BufferedReader rd = new BufferedReader(
              new InputStreamReader(response.getEntity().getContent()));
      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();
      WcServiceErrors errors = gson.fromJson(rd, WcServiceErrors.class);
      if (errors == null || errors.getErrors() == null || errors.getErrors().isEmpty()) {
        return Collections.emptyList();
      }
      List<CommerceRemoteError> result = new ArrayList<>(errors.getErrors().size());
      for (WcServiceError wcServiceError : errors.getErrors()) {
        result.add(wcServiceError);
      }
      return Collections.unmodifiableList(result);
    } catch (Exception ex) {
      LOG.debug("Error parsing commerce remote exception", ex);
      return Collections.emptyList();
    }
  }

  /**
   * Returns true if REST request can be executed.
   */
  private boolean isCommerceAvailable(HttpMethod method, URI uriComponents, StoreContext storeContext) {

    if (StoreContextHelper.isCommerceSystemUnavailable(storeContext)) {
      if (LOG.isWarnEnabled()) {
        LOG.warn("Dropped " + method + " " + uriComponents + " (commerce system is unavailable)");
      }
      return false;
    }

    return true;
  }

  private boolean isAuthenticationError(CommerceRemoteError remoteError) {
    return remoteError != null && remoteError.getErrorKey() != null &&
            (ERROR_KEY_AUTHENTICATION.equals(remoteError.getErrorKey()) ||
                    ERROR_KEY_INVALID_COOKIE.equals(remoteError.getErrorKey()) ||
                    ERROR_KEY_ACTIVITY_TOKEN_TERMINATED.equals(remoteError.getErrorKey()) ||
                    ERROR_KEY_ACTIVITY_TOKEN_INVALID.equals(remoteError.getErrorKey()) ||
                    (remoteError.getErrorKey().contains("not authorized")));
  }

  private boolean isUnknownUserError(CommerceRemoteError remoteError) {
    return remoteError != null && remoteError.getErrorKey() != null &&
            (remoteError.getErrorKey().contains("ObjectNotFoundException"));
  }

  Map<String, String> getRequiredHeaders(WcRestServiceMethod serviceMethod, boolean mustBeSecured, StoreContext storeContext, UserContext userContext) {

    Map<String, String> headers = new TreeMap<>();

    if (storeContext == null) {
      return headers;
    }

    if (serviceMethod.previewSupport && storeContext.hasPreviewContext()) {
      WcPreviewToken previewToken = loginService.getPreviewToken();
      if (previewToken != null) {
        headers.put(HEADER_WC_PREVIEW_TOKEN, previewToken.getPreviewToken());
      }
    }

    // use case: personalized info, like prices
    if (serviceMethod.userCookiesSupport &&
            StoreContextHelper.getWcsVersion(storeContext) > StoreContextHelper.WCS_VERSION_7_6) {
      if (userContext != null && userContext.getCookieHeader() != null) {
        headers.put(HEADER_COOKIE, userContext.getCookieHeader());
      }
    }

    // use case: contract based info, like prices and/or the selection of categories
    if (!headers.containsKey(HEADER_COOKIE) && serviceMethod.contractsSupport && storeContext.getContractIds() != null
            && StoreContextHelper.getWcsVersion(storeContext) >= StoreContextHelper.WCS_VERSION_7_8
            && Commerce.getCurrentConnection().getUserContext().getCookieHeader() != null) {

        headers.put(HEADER_COOKIE, Commerce.getCurrentConnection().getUserContext().getCookieHeader());
    }

    if (!headers.containsKey(HEADER_COOKIE)) {
      boolean mustBeAuthenticated = serviceMethod.requiresAuthentication || (userContext != null && userContext.getUserId() != null) || (userContext != null && userContext.getUserName() != null);
      if (mustBeAuthenticated || mustBeSecured) {
        WcCredentials credentials = loginService.loginServiceIdentity();
        if (credentials != null) {
          WcSession session = credentials.getSession();
          if (session != null) {
            if (mustBeAuthenticated) {
              headers.put(HEADER_WC_TOKEN, session.getWCToken());
            }
            if (mustBeSecured) {
              headers.put(HEADER_WC_TRUSTED_TOKEN, session.getWCTrustedToken());
            }
          }
        }
      }
    }

    return headers;
  }

  URI buildRequestUri(String relativeUrl, boolean secure, boolean search, List<String> variableValues, Map<String, String[]> optionalParameters) {
    String uri = relativeUrl;

    String endpoint;
    if (search) {
      endpoint = secure ? getSearchServiceSslEndpoint() : getSearchServiceEndpoint();
    } else {
      endpoint = secure ? getServiceSslEndpoint() : getServiceEndpoint();
    }

    if (!endpoint.endsWith("/")) {
      endpoint += "/";
    }
    uri = endpoint + uri;
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(uri);

    if (optionalParameters != null && !optionalParameters.isEmpty()) {
      // ok, it would have be better to use named uri template variables in the first place, but...
      variableValues = new ArrayList<>(variableValues);
      for (Map.Entry<String, String[]> parameter : optionalParameters.entrySet()) {
        // within the optional parameter values, we do not want any variable replacement, so we need this indirection:
        String[] values = parameter.getValue();
        for (String value : values) {
          uriBuilder.queryParam(parameter.getKey(), POSITION_RELATIVE_TEMPLATE_VARIABLE);
          variableValues.add(value);
        }
      }
    }
    Object[] vars = variableValues.toArray(new Object[variableValues.size()]);
    UriComponents uriComponents = uriBuilder.buildAndExpand(vars);
    return uriComponents.encode().toUri();
  }

  /**
   * Creates the HTTP request with the given attributes.
   *
   * @param uri               The  URI of the request
   * @param serviceMethod     The service method to call
   * @param bodyData          The model which is transmitted as JSON in the request body
   * @param additionalHeaders Additional headers that are required for security and authentication
   * @return http client request object
   */
  HttpUriRequest getRequest(URI uri, WcRestServiceMethod serviceMethod, Object bodyData, Map<String, String> additionalHeaders) {

    HttpUriRequest request = null;

    if (serviceMethod.method == HttpMethod.POST) {
      request = new HttpPost(uri);
    } else if (serviceMethod.method == HttpMethod.GET) {
      request = new HttpGet(uri);
    } else if (serviceMethod.method == HttpMethod.DELETE) {
      request = new HttpDelete(uri);
    } else if (serviceMethod.method == HttpMethod.PUT) {
      request = new HttpPut(uri);
    }

    if (request != null) {

      request.addHeader(HEADER_CONTENT_TYPE, MIME_TYPE_JSON);

      for (Map.Entry<String, String> item : additionalHeaders.entrySet()) {
        request.addHeader(item.getKey(), item.getValue());
      }

      try {
        //apply parameter to body
        if (bodyData != null) {
          String json = toJson(bodyData);
          if (LOG.isTraceEnabled()) {
            LOG.trace("{}\n{}", request, json);
          }
          StringEntity entity = new StringEntity(json);
          ((HttpEntityEnclosingRequest) request).setEntity(entity);
        }
      } catch (IOException e) {
        LOG.warn("Error while encoding body data: {}", e.getMessage(), e);
      }
    }

    return request;
  }

  /**
   * Converts the given model to a json string.
   *
   * @param model service model
   * @return string(JSON) representation of model
   * @throws java.io.IOException
   */
  private String toJson(Object model) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    return mapper.writeValueAsString(model);
  }

  protected HttpClient getHttpClient() {
    if (httpClient == null) {
      httpClient = HttpClientFactory.createHttpClient(trustAllSslCertificates, false,
              connectionPoolSize, socketTimeout, connectionTimeout, connectionRequestTimeout);
    }
    return httpClient;
  }

  @Required
  public void setServiceSslEndpoint(String serviceSslEndpoint) {
    this.serviceSslEndpoint = serviceSslEndpoint;
  }

  @SuppressWarnings("unused")
  public String getServiceSslEndpoint() {
    return CommercePropertyHelper.replaceTokens(serviceSslEndpoint, StoreContextHelper.getCurrentContext());
  }

  @Required
  public void setServiceEndpoint(String serviceEndpoint) {
    this.serviceEndpoint = serviceEndpoint;
  }

  public String getServiceEndpoint() {
    return CommercePropertyHelper.replaceTokens(serviceEndpoint, StoreContextHelper.getCurrentContext());
  }

  @SuppressWarnings("unused")
  public String getSearchServiceEndpoint() {
    return CommercePropertyHelper.replaceTokens(searchServiceEndpoint, StoreContextHelper.getCurrentContext());
  }

  @Required
  public void setSearchServiceEndpoint(String searchServiceEndpoint) {
    this.searchServiceEndpoint = searchServiceEndpoint;
  }

  @SuppressWarnings("unused")
  public String getSearchServiceSslEndpoint() {
    return CommercePropertyHelper.replaceTokens(searchServiceSslEndpoint, StoreContextHelper.getCurrentContext());
  }

  @Required
  public void setSearchServiceSslEndpoint(String searchServiceSslEndpoint) {
    this.searchServiceSslEndpoint = searchServiceSslEndpoint;
  }

  @Required
  public void setTrustAllSslCertificates(boolean trustAllSslCertificates) {
    this.trustAllSslCertificates = trustAllSslCertificates;
  }

  @Required
  public void setLoginService(LoginService loginService) {
    this.loginService = loginService;
  }

  @SuppressWarnings("unused")
  public int getConnectionPoolSize() {
    return connectionPoolSize;
  }

  public void setConnectionPoolSize(int connectionPoolSize) {
    this.connectionPoolSize = connectionPoolSize;
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
  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  @SuppressWarnings("unused")
  public void setConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  @SuppressWarnings("unused")
  public int getConnectionRequestTimeout() {
    return connectionRequestTimeout;
  }

  @SuppressWarnings("unused")
  public void setConnectionRequestTimeout(int connectionRequestTimeout) {
    this.connectionRequestTimeout = connectionRequestTimeout;
  }

  @SuppressWarnings("unused")
  public static class WcRestServiceMethod<T, P> {
    private HttpMethod method;
    private String uriTemplate;
    private boolean secure;
    private boolean requiresAuthentication;
    private boolean search;
    private boolean previewSupport;
    private boolean userCookiesSupport;
    private boolean contractsSupport;
    private Class<P> parameterType;
    private Class<T> returnType;

    private WcRestServiceMethod(HttpMethod method,
                                String uriTemplate,
                                boolean secure,
                                boolean requiresAuthentication,
                                boolean search,
                                boolean previewSupport,
                                boolean userCookiesSupport,
                                boolean contractsSupport,
                                Class<P> parameterType, Class<T> returnType) {
      this.method = method;
      this.uriTemplate = uriTemplate;
      this.secure = secure;
      this.requiresAuthentication = requiresAuthentication;
      this.search = search;
      this.previewSupport = previewSupport;
      this.userCookiesSupport = userCookiesSupport;
      this.contractsSupport = contractsSupport;
      this.parameterType = parameterType;
      this.returnType = returnType;
    }
  }

  public static class UnauthorizedException extends CommerceException {
    CommerceRemoteError remoteError;

    public UnauthorizedException(CommerceRemoteError remoteError) {
      super("401");
      this.remoteError = remoteError;
    }
  }

}
