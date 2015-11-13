package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.services.SessionSynchronizer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionSynchronizationInterceptorTest {
  @Test
  public void optionsRequest() throws GeneralSecurityException, IOException {
    when(request.getScheme()).thenReturn("https");
    when(request.getMethod()).thenReturn(OPTIONS);
    testling.preHandle(request, response, null);

    verify(request).getMethod();
    verify(sessionSynchronizer, never()).synchronizeUserSession(request, response);
  }

  @Test
  public void getRequest() throws GeneralSecurityException, IOException {
    Commerce.setCurrentConnection(mock(CommerceConnection.class));
    testling.preHandle(request, response, null);

    verify(request).getMethod();
    verify(sessionSynchronizer).synchronizeUserSession(request, response);
  }

  @Before
  public void defaultSetup() {
    testling = new SessionSynchronizationInterceptor();
    testling.setSessionSynchronizer(sessionSynchronizer);

    when(request.getMethod()).thenReturn(GET);
    when(request.getScheme()).thenReturn("https");
  }

  private SessionSynchronizationInterceptor testling;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private SessionSynchronizer sessionSynchronizer;

  private static final String OPTIONS = "OPTIONS";
  private static final String GET = "GET";
}
