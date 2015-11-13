package com.coremedia.blueprint.elastic.social.cae.springsocial;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomProviderSignInControllerTest {
  @InjectMocks
  private CustomProviderSignInController signInController;

  @Mock
  private ConnectionFactoryLocator connectionFactoryLocator;

  @Mock
  private OAuth2ConnectionFactory<?> connectionFactory;

  @Mock
  private NativeWebRequest webRequest;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private HttpSession httpSession;

  @Mock
  private OAuth2Operations oAuthOperations;

  @SuppressWarnings({"unchecked"})
  @Test
  public void testSignIn() {
    when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(httpServletRequest);
    when(httpServletRequest.getSession()).thenReturn(httpSession);
    when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("theURL"));
    when(connectionFactoryLocator.getConnectionFactory("twitter")).thenReturn((ConnectionFactory) connectionFactory);
    when(connectionFactory.getOAuthOperations()).thenReturn(oAuthOperations);

    RedirectView result = signInController.signIn("twitter", webRequest);

    assertNotNull(result);
    verify(httpSession).setAttribute(eq("nextUrl"), anyString());
    verify(httpSession).setAttribute(eq("registerUrl"), anyString());
    verify(httpSession).setAttribute(eq("loginUrl"), anyString());
  }
}
