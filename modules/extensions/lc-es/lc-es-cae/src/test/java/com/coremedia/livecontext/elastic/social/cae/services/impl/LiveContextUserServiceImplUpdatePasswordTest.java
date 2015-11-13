package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.elastic.social.cae.flows.PasswordResetHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.WebflowMessageKeys;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.elastic.social.cae.LiveContextPasswordReset;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LiveContextUserServiceImplUpdatePasswordTest {

  private static final String USERNAME = "anyUsername";
  private static final String EMAIL = "anyEmail";
  private static final String PASSWORD = "password";
  private static final String NEW_PASSWORD = "new_password";
  private static final String NEW_PASSWORD_REPEAT = "new_password";

  @Spy
  private LiveContextUserServiceImpl testling;

  @Mock
  private UserService commerceUserService;

  @Mock
  private PasswordResetHelper passwordResetHelper;

  //initialized before each test as deep stub
  private RequestContext context;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private UserContextProvider userContextProvider;

  @Mock
  private UserContext userContext;

  @Mock
  private StoreContextProvider storeContextProvider;

  @Mock
  private StoreContext storeContext;

  @Mock
  private CommerceConnection commerceConnection;

  @Before
  public void beforeEachTest() {
    initMocks(this);

    context = mock(RequestContext.class, RETURNS_DEEP_STUBS);
    when(context.getExternalContext().getNativeRequest()).thenReturn(request);
    when(context.getExternalContext().getNativeResponse()).thenReturn(response);

    configureUserAndHisContext();

    when(userContextProvider.createContext(USERNAME)).thenReturn(userContext);
    when(storeContextProvider.getCurrentContext()).thenReturn(storeContext);

    Commerce.setCurrentConnection(commerceConnection);
    when(commerceConnection.getUserService()).thenReturn(commerceUserService);
    when(commerceConnection.getUserContextProvider()).thenReturn(userContextProvider);
  }

  private void configureUserAndHisContext() {
    CommunityUser liveContextCommunityUser = mock(CommunityUser.class);
    doReturn(liveContextCommunityUser).when(testling).getCurrentUser();
    when(liveContextCommunityUser.getName()).thenReturn(USERNAME);
  }

  @Test
  public void testUpdatePassword() throws Exception {
    LiveContextPasswordReset reset = configurePasswordReset(context);

    doReturn(false).when(testling).hasErrorMessages(context);

    boolean result = testling.updatePassword(reset, context);
    assertTrue(result);
    verify(commerceUserService, times(1)).updateCurrentUserPassword(PASSWORD, NEW_PASSWORD, NEW_PASSWORD);
  }

  @Test
  public void testUpdatePasswordSecurityExceptionFromCommerce() throws Exception {
    doThrow(CommerceException.class).when(commerceUserService).updateCurrentUserPassword(PASSWORD, NEW_PASSWORD, NEW_PASSWORD_REPEAT);
    LiveContextPasswordReset reset = configurePasswordReset(context);

    doReturn(false).when(testling).hasErrorMessages(context);

    boolean result = testling.updatePassword(reset, context);
    assertFalse(result);

    verify(commerceUserService, times(1)).updateCurrentUserPassword(PASSWORD, NEW_PASSWORD, NEW_PASSWORD_REPEAT);
    verify(testling, times(1)).addErrorMessage(context, WebflowMessageKeys.USER_DETAILS_FORM_ERROR);
  }

  private LiveContextPasswordReset configurePasswordReset(RequestContext context) {
    LiveContextPasswordReset reset = new LiveContextPasswordReset();
    reset.setPasswordPolicy(null);
    reset.setEmailAddress(EMAIL);
    reset.setConfirmPassword(NEW_PASSWORD_REPEAT);
    reset.setPassword(NEW_PASSWORD);
    reset.setCurrentPassword(PASSWORD);
    return reset;
  }
}
