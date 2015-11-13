package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.elastic.social.cae.flows.UserDetails;
import com.coremedia.elastic.core.api.users.User;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.livecontext.elastic.social.cae.services.LiveContextUserService;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.webflow.execution.RequestContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LiveContextUserDetailsHelperTest {

  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String NEW_PASSWORD = "newPassword";
  private static final String NEW_PASSWORD_REPEAT = "newPassword";

  private LiveContextUserDetailsHelper testling;

  @Mock
  private LiveContextUserService userService;

  @Mock
  private CommunityUser communityUser;

  @Before
  public void beforeEachTest() throws Exception {
    MockitoAnnotations.initMocks(this);
    testling = spy(new LiveContextUserDetailsHelper());
    testling.setUserService(userService);

    doReturn(communityUser).when(testling).getLoggedInUser();
  }

  @Test
  public void testDoSave() throws Exception {
    LiveContextUserDetails userDetails =createUserDetails(USERNAME, PASSWORD, NEW_PASSWORD, NEW_PASSWORD_REPEAT);

    RequestContext context = mock(RequestContext.class, RETURNS_DEEP_STUBS);
    doReturn(communityUser).when(testling).saveChanges(communityUser, context);

    CommonsMultipartFile profileImage = mock(CommonsMultipartFile.class);

    doNotValidate(userDetails, context);
    validationCreatedErrorMessages(context, false);

    when(userService.saveUser(userDetails, context)).thenReturn(true);
    LiveContextPasswordResetMatcher passwordResetMatcher = new LiveContextPasswordResetMatcher(PASSWORD, NEW_PASSWORD, NEW_PASSWORD_REPEAT);
    when(userService.updatePassword(argThat(passwordResetMatcher), same(context))).thenReturn(true);

    boolean result = testling.doSave(userDetails, context, profileImage);

    assertTrue(result);
  }

  @Test
  public void testDoSaveErrorMessagesOnValidation() throws Exception {
    LiveContextUserDetails userDetails =createUserDetails(USERNAME, PASSWORD, NEW_PASSWORD, NEW_PASSWORD_REPEAT);

    RequestContext context = mock(RequestContext.class, RETURNS_DEEP_STUBS);
    CommonsMultipartFile profileImage = mock(CommonsMultipartFile.class);

    doNotValidate(userDetails, context);
    validationCreatedErrorMessages(context, true);

    boolean result = testling.doSave(userDetails, context, profileImage);

    assertFalse(result);
    verifyUserServiceNeverCalled();
  }

  @Test
  public void testDoSaveOldPasswordIsBlank() throws Exception {
    LiveContextUserDetails userDetails =createUserDetails(USERNAME, "", NEW_PASSWORD, NEW_PASSWORD_REPEAT);

    RequestContext context = mock(RequestContext.class, RETURNS_DEEP_STUBS);
    doReturn(communityUser).when(testling).saveChanges(communityUser, context);
    CommonsMultipartFile profileImage = mock(CommonsMultipartFile.class);

    doNotValidate(userDetails, context);
    validationCreatedErrorMessages(context, false);

    when(userService.saveUser(userDetails, context)).thenReturn(true);
    LiveContextPasswordResetMatcher passwordResetMatcher = new LiveContextPasswordResetMatcher(PASSWORD, NEW_PASSWORD, NEW_PASSWORD_REPEAT);

    boolean result = testling.doSave(userDetails, context, profileImage);

    verify(userService, times(0)).updatePassword(argThat(passwordResetMatcher), same(context));
    assertTrue(result);
  }

  @Test
  public void testDoSaveNewPasswordIsBlank() throws Exception {
    LiveContextUserDetails userDetails =createUserDetails(USERNAME, PASSWORD, "", NEW_PASSWORD_REPEAT);

    RequestContext context = mock(RequestContext.class, RETURNS_DEEP_STUBS);
    doReturn(communityUser).when(testling).saveChanges(communityUser, context);
    CommonsMultipartFile profileImage = mock(CommonsMultipartFile.class);

    doNotValidate(userDetails, context);
    validationCreatedErrorMessages(context, false);

    when(userService.saveUser(userDetails, context)).thenReturn(true);
    LiveContextPasswordResetMatcher passwordResetMatcher = new LiveContextPasswordResetMatcher(PASSWORD, "", NEW_PASSWORD_REPEAT);

    boolean result = testling.doSave(userDetails, context, profileImage);

    verify(userService, times(0)).updatePassword(argThat(passwordResetMatcher), same(context));
    assertTrue(result);
  }

  @Test
  public void testDoSaveConfirmPasswordIsBlank() throws Exception {
    LiveContextUserDetails userDetails =createUserDetails(USERNAME, PASSWORD, NEW_PASSWORD, "");

    RequestContext context = mock(RequestContext.class, RETURNS_DEEP_STUBS);
    doReturn(communityUser).when(testling).saveChanges(communityUser, context);
    CommonsMultipartFile profileImage = mock(CommonsMultipartFile.class);

    doNotValidate(userDetails, context);
    validationCreatedErrorMessages(context, false);

    when(userService.saveUser(userDetails, context)).thenReturn(true);
    LiveContextPasswordResetMatcher passwordResetMatcher = new LiveContextPasswordResetMatcher(PASSWORD, NEW_PASSWORD, "");

    boolean result = testling.doSave(userDetails, context, profileImage);

    verify(userService, times(0)).updatePassword(argThat(passwordResetMatcher), same(context));
    assertTrue(result);
  }

  @Test
  public void testDoSaveUserDataNotStored() throws Exception {
    LiveContextUserDetails userDetails =createUserDetails(USERNAME, PASSWORD, NEW_PASSWORD, NEW_PASSWORD_REPEAT);

    RequestContext context = mock(RequestContext.class, RETURNS_DEEP_STUBS);
    CommonsMultipartFile profileImage = mock(CommonsMultipartFile.class);

    doNotValidate(userDetails, context);
    validationCreatedErrorMessages(context, false);

    when(userService.saveUser(userDetails, context)).thenReturn(false);

    boolean result = testling.doSave(userDetails, context, profileImage);

    LiveContextPasswordResetMatcher passwordResetMatcher = new LiveContextPasswordResetMatcher(PASSWORD, NEW_PASSWORD, NEW_PASSWORD_REPEAT);
    verify(userService, times(0)).updatePassword(argThat(passwordResetMatcher), same(context));
    assertFalse(result);
  }

  private void verifyUserServiceNeverCalled() {
    verify(userService, times(0)).saveUser(any(LiveContextUserDetails.class), any(RequestContext.class));
    verify(userService, times(0)).updatePassword(any(LiveContextPasswordReset.class), any(RequestContext.class));
  }

  private void validationCreatedErrorMessages(RequestContext context, boolean hasErrorMessages) {
    when(context.getMessageContext().hasErrorMessages()).thenReturn(hasErrorMessages);
  }

  private void doNotValidate(UserDetails userDetails, RequestContext context) {
    doNothing().when(userDetails).validate(context);
  }

  private LiveContextUserDetails createUserDetails(String username, String password, String newPassword, String newPasswordRepeat) {
    LiveContextUserDetails userDetails = spy(new LiveContextUserDetails());
    userDetails.setUsername(username);
    userDetails.setPassword(password);
    userDetails.setNewPassword(newPassword);
    userDetails.setNewPasswordRepeat(newPasswordRepeat);
    return userDetails;
  }

  @Test
  public void testGetDetails() throws Exception {
    User user = mock(User.class);
    LiveContextUserDetails userDetails = mock(LiveContextUserDetails.class);
    when(userService.getUserDetails(user)).thenReturn(userDetails);

    UserDetails details = testling.getDetails(user, true, null, null, false);

    assertSame(userDetails, details);
    verify(userService, times(1)).getUserDetails(any(User.class));
  }

  private class LiveContextPasswordResetMatcher extends BaseMatcher<LiveContextPasswordReset> {

    private String currentPassword;
    private String password;
    private String confirmPassword;

    public LiveContextPasswordResetMatcher(String currentPassword, String password, String confirmPassword) {
      this.currentPassword = currentPassword;
      this.password = password;
      this.confirmPassword = confirmPassword;
    }

    @Override
    public boolean matches(Object o) {
      if (o instanceof LiveContextPasswordReset) {
        LiveContextPasswordReset passwordReset = (LiveContextPasswordReset) o;
        String currentPassword = passwordReset.getCurrentPassword();
        String confirmPassword = passwordReset.getConfirmPassword();
        String password = passwordReset.getPassword();
        if (StringUtils.equals(this.currentPassword, currentPassword)
                && StringUtils.equals(this.password, password)
                && StringUtils.equals(this.confirmPassword, confirmPassword)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public void describeTo(Description description) {

    }
  }
}
