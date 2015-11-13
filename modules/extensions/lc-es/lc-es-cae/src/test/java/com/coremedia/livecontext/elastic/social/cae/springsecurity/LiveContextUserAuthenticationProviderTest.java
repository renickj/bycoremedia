package com.coremedia.livecontext.elastic.social.cae.springsecurity;

import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.UserPrincipal;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LiveContextUserAuthenticationProviderTest {

  private LiveContextUserAuthenticationProvider testling;

  @Mock
  private CommunityUserService communityUserService;

  @Mock
  private UserSessionService commerceUserSessionService;

  @Before
  public void beforeEachTest() {
    MockitoAnnotations.initMocks(this);
    testling = new LiveContextUserAuthenticationProvider();
    testling.setCommunityUserService(communityUserService);
    testling.setCommerceUserSessionService(commerceUserSessionService);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAuthenticateWrongToken() throws Exception {
    UsernamePasswordAuthenticationToken wrongToken = mock(UsernamePasswordAuthenticationToken.class);
    testling.authenticate(wrongToken);
  }

  @Test
  public void testAuthenticationUserFoundInCMSLoggedInInCommerce() throws Exception {
    CommunityUser communityUser = mock(CommunityUser.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    String username = "anyUserNameInMongo";
    String userId = "anyId";
    String password = "correctPasswordForUserName";

    configureCommunityUserService(communityUser, username, userId);

    configureCommerceUserService(request, response, username, password, true);

    LiveContextUsernamePasswordAuthenticationToken input = new LiveContextUsernamePasswordAuthenticationToken(request, response, username, password);

    Authentication authenticate = testling.authenticate(input);

    Object principal = authenticate.getPrincipal();

    assertEquals(password, authenticate.getCredentials());
    assertTrue(principal instanceof UserPrincipal);
    UserPrincipal userPrincipal = (UserPrincipal) principal;
    assertEquals(userId, userPrincipal.getUserId());
    assertEquals(username, userPrincipal.getName());
    assertTrue(authenticate.isAuthenticated());
  }

  @Test(expected = BadCredentialsException.class)
  public void testAuthenticationUserNotFoundInCMS() throws Exception {
    CommunityUser communityUser = null;
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    String username = "anyUserNameNotInMongo";
    String password = "anyPasswordCorrectOrIncorrect";

    configureCommunityUserService(communityUser, username, null);

    LiveContextUsernamePasswordAuthenticationToken input = new LiveContextUsernamePasswordAuthenticationToken(request, response, username, password);

    testling.authenticate(input);

    verifyCommerceNotAskedForLogin(request, response, username, password);
  }

  @Test(expected = BadCredentialsException.class)
  public void testAuthenticationUserFoundInCMSButNotLoggedInInCommerce() throws Exception {
    CommunityUser communityUser = mock(CommunityUser.class);
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    String username = "anyUserNameNotInMongo";
    String userId = "userId";
    String password = "anyPasswordCorrectOrIncorrect";

    configureCommunityUserService(communityUser, username, userId);

    configureCommerceUserService(request, response, username, password, false);

    LiveContextUsernamePasswordAuthenticationToken input = new LiveContextUsernamePasswordAuthenticationToken(request, response, username, password);

    testling.authenticate(input);

    verifyCommerceAskedForLogin(request, response, username, password);
  }

  @Test
  public void testSupportedParentClassesNotSupported() {
    boolean isSupported = testling.supports(UsernamePasswordAuthenticationToken.class);
    assertFalse(isSupported);
  }

  @Test
  public void testSupportedClass() {
    boolean isSupported = testling.supports(LiveContextUsernamePasswordAuthenticationToken.class);
    assertTrue(isSupported);
  }

  private void verifyCommerceNotAskedForLogin(HttpServletRequest request, HttpServletResponse response, String username, String password) {
    verify(commerceUserSessionService, times(0)).loginUser(request, response, username, password);
  }

  private void verifyCommerceAskedForLogin(HttpServletRequest request, HttpServletResponse response, String username, String password) {
    verify(commerceUserSessionService, times(1)).loginUser(request, response, username, password);
  }

  private void configureCommerceUserService(HttpServletRequest request, HttpServletResponse response, String username, String password, boolean result) {
    when(commerceUserSessionService.loginUser(request, response, username, password)).thenReturn(result);
  }

  private void configureCommunityUserService(CommunityUser communityUser, String username, String userId) {
    when(communityUserService.getUserByName(username)).thenReturn(communityUser);
    if (communityUser != null) {
      when(communityUser.getId()).thenReturn(userId);
      when(communityUser.getName()).thenReturn(username);
    }
  }
}
