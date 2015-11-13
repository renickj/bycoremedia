package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.UserPrincipal;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.blueprint.base.livecontext.ecommerce.user.UserContextBuilder;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class, UserContext.class})
public class LiveContextUserServiceImplSynchronizeSessionsTest {

  @Test
  public void synchronizeUserSessionNoLocalButCommerceLogin() throws GeneralSecurityException {
    when(securityContext.getAuthentication()).thenReturn(null);
    when(commerceUserSessionService.isLoggedIn()).thenReturn(true);

    when(commerceUserService.findCurrentUser()).thenReturn(commerceUser);
    when(commerceUser.getLogonId()).thenReturn("shopper");
    when(commerceUser.getEmail1()).thenReturn("shopper@mail.com");

    when(communityUserService.getUserByName("shopper")).thenReturn(communityUser);

    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);

    verifyLogin(true);
    verifyLogout(false);
  }

  @Test
  public void synchronizeUserSessionNoLocalButCommerceLoginWithUserCreation() throws GeneralSecurityException {
    when(securityContext.getAuthentication()).thenReturn(null);
    when(commerceUserSessionService.isLoggedIn()).thenReturn(true);
    when(communityUserService.getUserByName("shopper")).thenReturn(null);

    when(commerceUser.getLogonId()).thenReturn("shopper");
    when(commerceUser.getEmail1()).thenReturn("shopper@mail.com");
    when(commerceUserService.findCurrentUser()).thenReturn(commerceUser);

    when(communityUserService.createUser("shopper", null, "shopper@mail.com")).thenReturn(communityUser);

    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);

    verify(communityUserService).createUser("shopper", null, "shopper@mail.com");
    assertEquals(UserContext.getUser(), communityUser);

    verifyLogin(true);
    verifyLogout(false);
  }

  @Test
  public void synchronizeUserSessionNoLocalAndNoCommerceLogin() throws GeneralSecurityException {
    when(securityContext.getAuthentication()).thenReturn(null);
    when(commerceUserSessionService.isLoggedIn()).thenReturn(false);
    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);

    verifyLogin(false);
    verifyLogout(false);
  }

  @Test
  public void synchronizeUserSessionLocalLoginButNoCommerceLogin() throws GeneralSecurityException {
    when(commerceUserSessionService.isLoggedIn()).thenReturn(false);
    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);
    verifyLogin(false);
    verifyLogout(true);
  }

  @Test
  public void synchronizeUserSessionLocalLoginAndCommerceLogin() throws GeneralSecurityException {
    UserContext.setUser(communityUser);
    testling.synchronizeUserSession(httpServletRequest, httpServletResponse);
    verifyLogin(true);
    verifyLogout(false);
  }

  private void verifyLogin(boolean localLogin) throws GeneralSecurityException {
    verify(commerceUserSessionService).isLoggedIn();
    if (localLogin) {
      assertNotNull(UserContext.getUser());
    } else {
      assertNull(UserContext.getUser());
    }
  }

  private void verifyLogout(boolean localLogout) throws GeneralSecurityException {
    VerificationMode localLogoutVM = localLogout ? times(1) : never();
    verify(securityContextLogoutHandler, localLogoutVM).logout(any(HttpServletRequest.class), any(HttpServletResponse.class), eq(authentication));
  }

  // The default setup simulates a correctly logged in user. That is a user who is logged into commerce as well
  // as into elastic.
  @Before
  public void defaultSetup() throws CredentialExpiredException {

    testling = new LiveContextUserServiceImpl();
    testling.setSecurityContextLogoutHandler(securityContextLogoutHandler);
    testling.setCommunityUserService(communityUserService);

    mockStatic(SecurityContextHolder.class);
    when(SecurityContextHolder.getContext()).thenReturn(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(userPrincipal);

    Commerce.setCurrentConnection(commerceConnection);
    when(commerceConnection.getStoreContextProvider()).thenReturn(storeContextProvider);
    when(commerceConnection.getUserContextProvider()).thenReturn(userContextProvider);
    when(commerceConnection.getIdProvider()).thenReturn(new BaseCommerceIdProvider("vendor"));
    when(commerceConnection.getStoreContext()).thenReturn(getStoreContext());
    when(commerceConnection.getUserSessionService()).thenReturn(commerceUserSessionService);
    when(commerceConnection.getUserService()).thenReturn(commerceUserService);

    when(commerceUserSessionService.isLoggedIn()).thenReturn(true);
    when(storeContextProvider.getCurrentContext()).thenReturn(getStoreContext());
    when(userContextProvider.getCurrentContext()).thenReturn(getUserContext());

    UserContext.clear();
  }

  private LiveContextUserServiceImpl testling;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private SecurityContextLogoutHandler securityContextLogoutHandler;

  @Mock
  private Authentication authentication;

  @Mock
  private UserPrincipal userPrincipal;

  @Mock
  private User commerceUser;

  @Mock
  private CommunityUser communityUser;

  @Mock
  private UserService commerceUserService;

  @Mock
  private UserSessionService commerceUserSessionService;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private HttpServletResponse httpServletResponse;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private UserContextProvider userContextProvider;

  @Mock
  private CommerceConnection commerceConnection;

  private StoreContext getStoreContext() {
    StoreContext result = StoreContextHelper.createContext(CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.setWcsVersion(result, Float.toString(7.7f));
    return result;
  }

  private com.coremedia.livecontext.ecommerce.user.UserContext getUserContext() {
    com.coremedia.livecontext.ecommerce.user.UserContext userContext = UserContextBuilder.create().build();
    userContext.put(UserContextHelper.FOR_USER_ID, USER1_ID);
    userContext.put(UserContextHelper.FOR_USER_NAME, USER1_NAME);
    return userContext;
  }

  public static final String CONFIG_ID = System.getProperty("lc.test.configID", "myConfigId");
  public static final String STORE_ID = System.getProperty("lc.test.storeId", "10202");
  public static final String STORE_NAME = System.getProperty("lc.test.storeName", "AuroraESite");
  public static final String CATALOG_ID = System.getProperty("lc.test.catalogId", "10051");

  public static final String LOCALE = "en_US";
  public static final String CURRENCY = "USD";
  public static final String CONNECTION_ID = "test-shop-connection-id";
  public static final String USER1_NAME = "testUser";
  public static final String USER1_ID = "4711";
}
