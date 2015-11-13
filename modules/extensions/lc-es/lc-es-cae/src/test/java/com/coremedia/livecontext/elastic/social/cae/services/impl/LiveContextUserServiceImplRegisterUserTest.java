package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.elastic.social.cae.flows.LoginHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.RegistrationHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.WebflowMessageKeys;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.elastic.social.cae.LiveContextRegistration;
import com.coremedia.livecontext.elastic.social.cae.UserMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LiveContextUserServiceImplRegisterUserTest {

  @Mock
  private RegistrationHelper registrationHelper;

  @Spy
  private LiveContextUserServiceImpl testling;

  //initialized before each test as deep stub
  private RequestContext requestContext;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private UserService userService;

  @Mock
  private StoreContext storeContext;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private CommerceConnection commerceConnection;

  @Mock
  private UserContextProvider userContextProvider;

  @Mock
  private UserService commerceUserService;

  @Mock
  private UserContext userContext;

  @Mock
  private CommunityUser user;

  @Mock
  private com.coremedia.livecontext.ecommerce.user.User commerceUser;

  @Mock
  private LoginHelper loginHelper;

  @Before
  public void beforeEachTest() {
    initMocks(this);
    testling.setRegistrationHelper(registrationHelper);
    testling.setCommunityUserService(communityUserService);
    testling.setUserMapper(new UserMapper());
    testling.setLoginHelper(loginHelper);

    requestContext = mock(RequestContext.class, RETURNS_DEEP_STUBS);

    when(storeContextProvider.getCurrentContext()).thenReturn(storeContext);
    when(userContextProvider.getCurrentContext()).thenReturn(userContext);
    when(commerceUserService.registerUser(anyString(), anyString(), anyString())).thenReturn(commerceUser);
    when(loginHelper.authenticate(mock(Authentication.class), requestContext)).thenReturn(true);

    when(requestContext.getExternalContext().getNativeRequest()).thenReturn(request);
    when(requestContext.getExternalContext().getNativeResponse()).thenReturn(response);

    doNothing().when(testling).addErrorMessage(any(RequestContext.class), anyString());

    Commerce.setCurrentConnection(commerceConnection);
    when(commerceConnection.getUserService()).thenReturn(commerceUserService);
    when(commerceConnection.getUserContextProvider()).thenReturn(userContextProvider);
  }

  @Test
  public void testRegisterUserSuccessful() throws Exception {
    String username = "anyUser";
    String password = "anyPassword";
    String email = "anyEmail@anydomain.tld";

    LiveContextRegistration registration = createRegistration(username, password, email);
    when(registrationHelper.register(same(registration), same(requestContext), isNull(CommonsMultipartFile.class), isNull(Map.class))).thenReturn(user);

    configureLoginSuccessfull(username, password);
    configureRegistrationInCommerceSuccessfull();
    doReturn(false).when(testling).hasErrorMessages(requestContext);
    when(commerceUserService.registerUser(username, password, email)).thenReturn(mock(User.class));
    boolean result = testling.registerUser(registration, requestContext);

    assertTrue(result);
    //register in es
    verify(registrationHelper, times(1)).register(same(registration), same(requestContext), isNull(CommonsMultipartFile.class), isNull(Map.class));
    //login user after registration
    verify(testling, times(1)).loginUser(username, password, requestContext, request, response);
  }

  @Test
  public void testRegisterUserNotRegisteredInCommerce() throws Exception {
    String username = "anyUser";
    String password = "anyPassword";
    String email = "anyEmail@anydomain.tld";
    LiveContextRegistration registration = createRegistration(username, password, email);

    when(registrationHelper.register(same(registration), same(requestContext), isNull(CommonsMultipartFile.class), isNull(Map.class))).thenReturn(user);
    when(commerceUserService.registerUser(anyString(), anyString(), anyString())).thenThrow(mock(CommerceException.class));

    boolean result = testling.registerUser(registration, requestContext);

    assertFalse(result);
    verify(testling, times(1)).addErrorMessage(requestContext, WebflowMessageKeys.REGISTRATION_FORM_ERROR);
  }

  @Test
  public void testRegisterUserNotRegisteredInCMS() throws Exception {
    String username = "anyUser";
    String password = "anyPassword";
    String email = "anyEmail@anydomain.tld";

    LiveContextRegistration registration = createRegistration(username, password, email);
    when(registrationHelper.register(same(registration), same(requestContext), isNull(CommonsMultipartFile.class), isNull(Map.class))).thenReturn(null);

    boolean result = testling.registerUser(registration, requestContext);
    assertFalse(result);
    verify(testling, times(1)).addErrorMessage(requestContext, WebflowMessageKeys.REGISTRATION_PROVIDER_ERROR);
  }

  private void configureRegistrationInCommerceSuccessfull() {
    User user = mock(User.class);
    when(userService.updateCurrentUser(user)).thenReturn(user);
  }

  private void configureLoginSuccessfull(String username, String password) {
    doReturn(true).when(testling).loginUser(username, password, requestContext, request, response);
  }

  private LiveContextRegistration createRegistration(String username, String password, String email) {
    LiveContextRegistration registration = new LiveContextRegistration();

    registration.setUsername(username);
    registration.setPassword(password);
    registration.setConfirmPassword(password);
    registration.setEmailAddress(email);
    return registration;
  }
}
