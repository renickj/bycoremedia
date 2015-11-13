package com.coremedia.livecontext.ecommerce.ibm.user;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommercePropertyHelper;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceUrlPropertyProvider;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreFrontResponse;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreFrontService;
import com.coremedia.livecontext.ecommerce.ibm.login.CommerceUserIsLoggedInCacheKey;
import com.coremedia.livecontext.ecommerce.ibm.login.WcLoginWrapperService;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.google.common.collect.ImmutableMap;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriComponents;

import javax.annotation.Nonnull;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class UserSessionServiceImpl extends StoreFrontService implements UserSessionService {

  private final static Logger LOG = LoggerFactory.getLogger(UserSessionServiceImpl.class);

  protected static final String LOGON_URL = "Logon?reLogonURL=fail&storeId={storeId}&catalogId={catalogId}&logonId={logonId}&logonPassword={logonPassword}&URL=TopCategoriesDisplay"; // NOSONAR false positive: Credentials should not be hard-coded
  protected static final String LOGOUT_URL = "Logoff?storeId={storeId}&catalogId={catalogId}";
  protected static final String GUEST_LOGIN_URL = "OrderCreate?storeId={storeId}&catalogId={catalogId}&URL=TopCategoriesDisplay";
  protected static final String GUEST_LOGIN_URL_2 = "MiniShopCartDisplayView?storeId={storeId}&catalogId={catalogId}";

  protected static final String STORE_ID_URL_VAR = "storeId";
  protected static final String CATALOG_ID_URL_VAR = "catalogId";
  protected static final String LOGON_ID_URL_VAR = "logonId";
  protected static final String LOGON_PASSWORD_URL_VAR = "logonPassword"; // NOSONAR false positive: Credentials should not be hard-coded

  private String wcsStorefrontUrl;

  private UserService userService;
  private WcLoginWrapperService loginWrapperService;
  private CommerceCache commerceCache;

  // ----- methods that use WCS storefront services -----------------------------

  @Override
  public boolean ensureGuestIdentity(final HttpServletRequest request, final HttpServletResponse response) {
    if (isKnownUser(request)) {
      return true;
    }

    try {
      Map<String, String> uriTemplateParameters = ImmutableMap.of(
              STORE_ID_URL_VAR, resolveStoreId(),
              CATALOG_ID_URL_VAR, resolveCatalogId());
      StoreFrontResponse storeFrontResponse = handleStorefrontCall(GUEST_LOGIN_URL, uriTemplateParameters, request, response);

      //apply user id update on the user context, other user context values remain untouched: not relevant
      String newUserId = resolveUserId(response, resolveStoreId(), true);
      UserContext userContext = UserContextHelper.getCurrentContext();
      userContext.put(UserContextHelper.FOR_USER_ID, newUserId);
      String mergedCookies = mergeCookies(userContext.getCookieHeader(), storeFrontResponse.getHttpClientContext());

      if (StoreContextHelper.getWcsVersion(StoreContextHelper.getCurrentContext()) > StoreContextHelper.WCS_VERSION_7_7) {
        //bugfix CMS-4132: call guest login url twice because guest session was broken when called only once
        final List<org.apache.http.cookie.Cookie> cookies = storeFrontResponse.getCookies();
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
          @Override
          public String getHeader(String name) {
            if ("Cookie".equals(name) && cookies != null) {
              StringBuilder sb = new StringBuilder();
              for (org.apache.http.cookie.Cookie cookie : cookies) {
                sb.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
              }
              return sb.toString();
            }
            return super.getHeader(name);
          }

        };
        StoreFrontResponse storeFrontResponse2 = handleStorefrontCall(GUEST_LOGIN_URL_2, uriTemplateParameters, requestWrapper, response);
        mergedCookies = mergeCookies(mergedCookies, storeFrontResponse2.getHttpClientContext());
      }

      userContext.setCookieHeader(WcCookieHelper.rewritePreviewCookies(mergedCookies));

      //return if the guest upgrade was successful.
      return isKnownUser(storeFrontResponse);
    } catch (Exception e) {
      LOG.error("Error executing guest login for user: {}", e.getMessage(), e);
    }
    return false;
  }

  private String mergeCookies(String cookieHeader, HttpClientContext httpClientContext) {
    List<org.apache.http.cookie.Cookie> cookies = httpClientContext.getCookieStore().getCookies();
    if (cookies.isEmpty()) {
      return cookieHeader;
    }
    StringBuilder sb = new StringBuilder(cookieHeader == null ? "" : cookieHeader);
    for (org.apache.http.cookie.Cookie cookie : cookies) {
      String name = cookie.getName();
      String value = cookie.getValue();
      if (sb.length() > 0) {
        sb.append("; ");
      }
      sb.append(name).append('=').append(value);
    }
    return sb.toString();
  }

  @Override
  public void pingCommerce(HttpServletRequest request, HttpServletResponse response) {
    try {
      String baseUrl = getWcsStorefrontUrl();
      StoreContext currentContext = getStoreContextProvider().getCurrentContext();

      Map<String, Object> params = new HashMap<>();
      params.put(CommerceUrlPropertyProvider.STORE_CONTEXT, currentContext);
      params.put(CommerceUrlPropertyProvider.URL_TEMPLATE, baseUrl);

      UriComponents pingUrl = (UriComponents) getUrlProvider().provideValue(params);
      handleStorefrontCall(pingUrl.toUriString(), Collections.<String, String>emptyMap(), request, response);
    } catch (GeneralSecurityException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String resolveUserId(HttpServletRequest request, String currentStoreId, boolean ignoreAnonymous) {
    if (request != null) {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        List<String> cookieStrings = new ArrayList<>();
        for (Cookie cookie : cookies) {
          cookieStrings.add(cookie.getName() + "=" + cookie.getValue());
        }

        return resolveUserId(cookieStrings, currentStoreId, ignoreAnonymous);
      }
    }

    return null;
  }

  private String resolveUserId(HttpServletResponse response, String currentStoreId, boolean ignoreAnonymous) {
    return resolveUserId(response.getHeaders("Set-Cookie"), currentStoreId, ignoreAnonymous);
  }

  private String resolveUserId(HttpResponse httpResponse, String currentStoreId, boolean ignoreAnonymous) {
    if (httpResponse != null) {
      Header[] cookieHeaders = httpResponse.getHeaders("Set-Cookie");
      if (cookieHeaders != null) {
        List<String> cookieStrings = new ArrayList<>();
        for (Header cookieHeader : cookieHeaders) {
          cookieStrings.add(cookieHeader.toString().substring("Set-Cookie:".length()).trim());
        }

        return resolveUserId(cookieStrings, currentStoreId, ignoreAnonymous);
      }
    }

    return null;
  }

  private String resolveUserId(Collection<String> cookieStrings, String currentStoreId, boolean ignoreAnonymous) {
    for (String cookieString : cookieStrings) {
      String value = cookieString.substring(cookieString.indexOf("=") + 1, cookieString.length());
      value = decodeUrl(value);
      if ((cookieString.startsWith(StoreFrontService.IBM_WC_USERACTIVITY_COOKIE_NAME) ||
        cookieString.startsWith(StoreFrontService.IBM_WCP_USERACTIVITY_COOKIE_NAME)) && !value.contains("DEL")) {
        String[] values = value.split(",");
        if (values.length >= 2) {
          if (Integer.parseInt(values[0]) < 0 && ignoreAnonymous) {
            continue;
          }

          String storeId = values[1];
          //extract only the user id of the cookie that matches the store, commerce may have generated several
          //WC_USERACTIVITY_* cookies, one for each store.
          if (currentStoreId.equals(storeId)) {
            return values[0];
          }
        }
      }
    }
    return null;
  }

  @Override
  public boolean loginUser(
          @Nonnull HttpServletRequest request,
          @Nonnull HttpServletResponse response,
          String username,
          String password) {
    if (resolveStoreId() != null) {
      try {
        Map<String, String> uriTemplateParameters = ImmutableMap.of(
                STORE_ID_URL_VAR, resolveStoreId(),
                CATALOG_ID_URL_VAR, resolveCatalogId(),
                LOGON_ID_URL_VAR, username,
                LOGON_PASSWORD_URL_VAR, password);
        StoreFrontResponse storeFrontResponse = handleStorefrontCall(LOGON_URL, uriTemplateParameters, request, response);
        if (storeFrontResponse.isSuccess()) {
          refreshUserContext(storeFrontResponse);
        }
        return isKnownUser(storeFrontResponse);
      } catch (GeneralSecurityException e) {
        LOG.warn("Error executing login for user '{}': {}", username, e.getMessage());
        return false;
      }
    }
    return false;
  }

  /**
   * A user is assumed to be logged into the commerce system, if he is a {@link #isKnownUser() known}
   * and {@link #isRegisteredUser() registered} user. Unfortunately there is no single REST call that
   * answers the question if a user is logged in, hence we have to combine both methods.
   */
  @Override
  public boolean isLoggedIn() {
    return isKnownUser() && isRegisteredUser();
  }

  /**
   * Returns true if the current user is a known user, which is a registered user or a guest
   */
  private boolean isKnownUser() {
    UserContext userContext = UserContextHelper.getCurrentContext();
    if (!isAnonymousUser(userContext)) {
      String userId = userContext.getUserId();
      try {
        return (Boolean) commerceCache.get(
                new CommerceUserIsLoggedInCacheKey(userId, StoreContextHelper.getCurrentContext(), UserContextHelper.getCurrentContext(), loginWrapperService, commerceCache)
        );
      } catch (Exception e) {
        LOG.debug("error while trying to load the current user context data, assume the user is not logged in", e);
      }
    }
    return false;
  }

  /**
   * Returns whether the current user is a registered user. Be aware that this method returns true
   * whether or not the user is logged in.
   */
  private boolean isRegisteredUser() {
    if (isAnonymousUser(UserContextHelper.getCurrentContext())) {
      return false;
    }

    try {
      User user = userService.findCurrentUser();
      return user != null && isNotBlank(user.getLogonId());
    } catch (Exception e) {
      LOG.error("Unknown error while trying to find a person in commerce. Will return false as answer to isLoggedIn.", e);
    }
    return false;
  }

  @Override
  public boolean logoutUser(HttpServletRequest request, HttpServletResponse response) throws GeneralSecurityException {
    if (resolveStoreId() != null) {
      Map<String, String> uriTemplateParameters = ImmutableMap.of(STORE_ID_URL_VAR, resolveStoreId(), CATALOG_ID_URL_VAR, resolveCatalogId());
      StoreFrontResponse storeFrontResponse = handleStorefrontCall(LOGOUT_URL, uriTemplateParameters, request, response);
      return !isKnownUser(storeFrontResponse);
    }

    return true;
  }

  @Override
  public void clearCommerceSession(HttpServletRequest request, HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie.getName().startsWith(StoreFrontService.IBM_WC_USERACTIVITY_COOKIE_NAME)
                || cookie.getName().startsWith(StoreFrontService.IBM_WCP_USERACTIVITY_COOKIE_NAME)) {
          if (Commerce.getCurrentConnection() != null && Commerce.getCurrentConnection().getStoreContext() != null) {
            String storeId = Commerce.getCurrentConnection().getStoreContext().getStoreId();
            if (storeId != null && cookie.getValue() != null && cookie.getValue().contains("%2c" + storeId)) {
              cookie.setValue(null);
              cookie.setMaxAge(0);
              cookie.setPath("/");
              response.addCookie(cookie);
            }
          }
        }
      }
    }
  }

  private void refreshUserContext(StoreFrontResponse storeFrontResponse) {
    UserContext userContext = UserContextHelper.getCurrentContext();
    if (userContext != null) {
      userContext.setUserId(resolveUserId(storeFrontResponse.getOriginalResponse(), resolveStoreId(), false));
    }
  }

  private boolean isAnonymousUser(UserContext userContext) {
    if (userContext == null) {
      return true;
    }

    String userId = userContext.getUserId();
    if (isBlank(userId)) {
      return true;
    }

    try {
      if (Integer.parseInt(userId) < 0) {
        return true;
      }
    } catch (NumberFormatException e) {
      return true;
    }

    return false;
  }

  @Required
  public void setWcsStorefrontUrl(String wcsStorefrontUrl) {
    this.wcsStorefrontUrl = wcsStorefrontUrl;
  }

  public String getWcsStorefrontUrl() {
    return CommercePropertyHelper.replaceTokens(wcsStorefrontUrl, getStoreContextProvider().getCurrentContext());
  }

  @Required
  public void setLoginWrapperService(WcLoginWrapperService loginWrapperService) {
    this.loginWrapperService = loginWrapperService;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  private static String decodeUrl(String encodedUrl) {
    try {
      return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      String msg = "UTF-8 is not supported, must not happen, use an approved JVM.";
      LOG.error(msg, e);
      throw new InternalError(msg);
    }
  }
}
