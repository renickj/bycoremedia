package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.elastic.social.cae.flows.LoginForm;
import com.coremedia.blueprint.elastic.social.cae.flows.LoginHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.PasswordResetHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.RegistrationHelper;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.elastic.social.cae.LiveContextPasswordReset;
import com.coremedia.livecontext.elastic.social.cae.LiveContextUserDetails;
import com.coremedia.livecontext.elastic.social.cae.UserMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LiveContextUserServiceImplTest {
  private final static String USERNAME = "testuser";
  private final static String PASSWORD = "anyPasswordWithNumber123";
  private final static String EMAIL = "tester@email.com";


  @Mock
  private StoreContext storeContext;

  @Mock
  private UserService commerceUserService;

  @Mock
  private UserSessionService commerceUserSessionService;

  @Mock
  private LoginHelper loginHelper;

  @Mock
  private CommunityUser communityUser;

  @Mock
  private RegistrationHelper registrationHelper;

  @Mock
  private PasswordResetHelper passwordResetHelper;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private UserContextProvider userContextProvider;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private RequestContext context;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private UserContext userContext;

  private LiveContextUserServiceImpl testling;

  @Mock
  private MessageContext messageContext;

  @Mock
  private User user;

  @Mock
  private UserMapper userMapper;

  @Mock
  private ExternalContext externalContext;

  @Mock
  private SecurityContextLogoutHandler securityContextLogoutHandler;

  @Mock
  private CommerceConnection commerceConnection;

  @Before
  public void beforeEachTest() {
    initMocks(this);
    testling = spy(new LiveContextUserServiceImpl());
    testling.setCommunityUserService(communityUserService);
    testling.setLoginHelper(loginHelper);
    testling.setRegistrationHelper(registrationHelper);
    testling.setUserMapper(userMapper);
    testling.setSecurityContextLogoutHandler(securityContextLogoutHandler);

    when(communityUser.getName()).thenReturn(USERNAME);

    doNothing().when(testling).addErrorMessage(any(RequestContext.class), anyString());
    when(context.getMessageContext()).thenReturn(messageContext);

    when(storeContextProvider.findContextBySiteName("Helios")).thenReturn(storeContext);

    when(userContextProvider.createContext(USERNAME)).thenReturn(userContext);
    when(storeContextProvider.getCurrentContext()).thenReturn(storeContext);

    configureExternalContext();

    Commerce.setCurrentConnection(commerceConnection);
    when(commerceConnection.getUserService()).thenReturn(commerceUserService);
    when(commerceConnection.getUserSessionService()).thenReturn(commerceUserSessionService);
    when(commerceConnection.getUserContextProvider()).thenReturn(userContextProvider);
  }

  @Test
  public void testResetPassword() throws Exception {
    when(communityUserService.getUserByEmail(EMAIL)).thenReturn(communityUser);

    LiveContextPasswordReset reset = configurePasswordReset(EMAIL);
    boolean success = testling.resetPassword(reset, context);
    assertTrue(success);
    verify(commerceUserService, times(1)).resetPassword(USERNAME, null);
  }

  @Test
  public void testIsPasswordExpired() throws Exception {
    LoginForm form = new LoginForm();
    form.setName(USERNAME);
    form.setPassword(PASSWORD);

    configureUserService();

    boolean expired = testling.isPasswordExpired(form);
    assertFalse(expired);

    when(user.isPasswordExpired()).thenReturn(true);
    expired = testling.isPasswordExpired(form);
    assertTrue(expired);
  }

  @Test
  public void testLogoutUser() throws Exception {
    when(commerceUserSessionService.logoutUser(request, response)).thenReturn(true);

    boolean result = testling.logoutUser(request, response, mock(RequestContext.class));

    assertTrue(result);
    verify(commerceUserSessionService, times(1)).logoutUser(request, response);
  }

  @Test
  public void testGetUserDetails() throws Exception {
    when(communityUserService.createFrom(communityUser)).thenReturn(communityUser);
    configureUserService();
    LiveContextUserDetails details = testling.getUserDetails(communityUser);
    assertNotNull(details);
    verify(userMapper, times(1)).applyPersonToUserDetails(user, details, communityUser);
  }

  @Test
  public void testSaveUser() throws Exception {
    when(communityUserService.createFrom(communityUser)).thenReturn(communityUser);
    configureUserService();
    LiveContextUserDetails details = testling.getUserDetails(communityUser);

    boolean saved = testling.saveUser(details, context);
    assertTrue(saved);
    verify(userMapper, times(1)).applyUserToPerson(user, details);
  }

  private LiveContextPasswordReset configurePasswordReset(String emailAddress) {
    LiveContextPasswordReset reset = new LiveContextPasswordReset();
    reset.setPasswordPolicy(null);
    reset.setEmailAddress(emailAddress);
    reset.setConfirmPassword(PASSWORD);
    reset.setPassword(PASSWORD);
    reset.setCurrentPassword(PASSWORD);
    return reset;
  }

  private void configureUserService() {
    when(user.getLogonId()).thenReturn(USERNAME);
    when(commerceUserService.findCurrentUser()).thenReturn(user);
    when(testling.getCommerceUser(USERNAME)).thenReturn(user);
  }

  private void configureExternalContext() {
    when(context.getExternalContext()).thenReturn(externalContext);
    when(externalContext.getNativeRequest()).thenReturn(request);
    when(externalContext.getNativeResponse()).thenReturn(response);
  }
}
