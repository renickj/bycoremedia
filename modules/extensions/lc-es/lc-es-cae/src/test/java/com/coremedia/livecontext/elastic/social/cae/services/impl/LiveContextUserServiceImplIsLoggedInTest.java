package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.webflow.execution.RequestContext;

import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LiveContextUserServiceImplIsLoggedInTest {

  @Spy
  private LiveContextUserServiceImpl testling;

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
    initMocks(this);

    context = mock(RequestContext.class, RETURNS_DEEP_STUBS);
    when(context.getExternalContext().getNativeRequest()).thenReturn(request);
    when(context.getExternalContext().getNativeResponse()).thenReturn(response);

    Commerce.setCurrentConnection(commerceConnection);
    when(commerceConnection.getUserSessionService()).thenReturn(commerceUserSessionService);
  }

  @Test
  public void testIsLoggedInSuccessful() throws Exception {
    configureCommerceUserService(request, true);

    boolean loggedIn = testling.isLoggedIn(request);

    assertTrue(loggedIn);
    verify(commerceUserSessionService, times(1)).isLoggedIn();
  }

  @Test
  public void testIsLoggedInLoggedOutInCMS() throws Exception {
    configureCommerceUserService(request, false);

    boolean loggedIn = testling.isLoggedIn(request);

    assertFalse(loggedIn);
    verify(commerceUserSessionService, times(1)).isLoggedIn();
  }

  private void configureCommerceUserService(HttpServletRequest request, boolean isLoggedInInCMS) {
    try {
      when(commerceUserSessionService.isLoggedIn()).thenReturn(isLoggedInInCMS);
    } catch (CredentialExpiredException e) {
      e.printStackTrace();
    }
  }
}
