package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.elastic.social.cae.flows.LoginForm;
import com.coremedia.blueprint.elastic.social.cae.flows.LoginHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.elastic.social.cae.springsecurity.LiveContextUsernamePasswordAuthenticationToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LiveContextUserServiceImplLoginUserTest {

  private static final String USERNAME = "anyUsername";

  private static final String PASSWORD = "anyPass";

  @Spy
  private LiveContextUserServiceImpl testling;

  @Mock
  private LoginHelper loginHelper;

  @Mock
  private UserSessionService commerceUserSessionService;

  //initialized before each test as deep stub
  private RequestContext context;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private CommerceConnection commerceConnection;

  @Before
  public void beforeEachTest() {
    MockitoAnnotations.initMocks(this);

    testling.setLoginHelper(loginHelper);

    context = mock(RequestContext.class, RETURNS_DEEP_STUBS);
    when(context.getExternalContext().getNativeRequest()).thenReturn(request);
    when(context.getExternalContext().getNativeResponse()).thenReturn(response);

    Commerce.setCurrentConnection(commerceConnection);
    when(commerceConnection.getUserSessionService()).thenReturn(commerceUserSessionService);
  }

  @Test
  public void testLoginUserLoginInCaeFailed() throws Exception {
    LoginForm form = mock(LoginForm.class);
    when(loginHelper.login(form, context)).thenReturn(false);

    boolean result = testling.loginUser(USERNAME, PASSWORD, context, request, response);

    assertFalse(result);
    verify(commerceUserSessionService, times(0)).loginUser(same(request), same(response), anyString(), anyString());
  }

  @Test
  public void testLoginUserLoginInWcsFailed() throws Exception {
    LoginForm form = createLoginForm();

    //configure services
    when(loginHelper.login(form, context)).thenReturn(true);
    when(commerceUserSessionService.loginUser(request, response, USERNAME, PASSWORD)).thenReturn(false);

    boolean result = testling.loginUser(USERNAME, PASSWORD, context, request, response);

    assertFalse(result);
  }

  @Test
  public void testLoginUserLoginSuccessful() throws Exception {
    LoginForm form = createLoginForm();

    //configure services
    configureServicesForLogin(USERNAME, PASSWORD, form, context, request, response);
    when(loginHelper.authenticate(any(LiveContextUsernamePasswordAuthenticationToken.class), eq(context))).thenReturn(true);

    boolean result = testling.loginUser(USERNAME, PASSWORD, context, request, response);
    assertTrue(result);
    verify(loginHelper, times(1)).authenticate(any(LiveContextUsernamePasswordAuthenticationToken.class), eq(context));
  }

  private LoginForm createLoginForm() {
    LoginForm form = new LoginForm();
    form.setName(USERNAME);
    form.setPassword(PASSWORD);
    return form;
  }

  private void configureServicesForLogin(String username, String password, LoginForm form, RequestContext context, HttpServletRequest request, HttpServletResponse response) throws GeneralSecurityException {
    when(loginHelper.login(form, context)).thenReturn(true);
    when(commerceUserSessionService.loginUser(request, response, username, password)).thenReturn(true);
  }
}
