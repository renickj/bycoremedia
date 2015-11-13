package com.coremedia.livecontext.elastic.social.cae.services.impl;

import com.coremedia.blueprint.elastic.social.cae.flows.LoginForm;
import com.coremedia.blueprint.elastic.social.cae.flows.LoginHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.MessageHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.Registration;
import com.coremedia.blueprint.elastic.social.cae.flows.RegistrationHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.WebflowMessageKeys;
import com.coremedia.elastic.core.api.models.UniqueConstraintViolationException;
import com.coremedia.elastic.core.api.users.User;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.UserPrincipal;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceRemoteException;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.user.UserContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserService;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import com.coremedia.livecontext.elastic.social.cae.LiveContextPasswordReset;
import com.coremedia.livecontext.elastic.social.cae.LiveContextRegistration;
import com.coremedia.livecontext.elastic.social.cae.LiveContextUserDetails;
import com.coremedia.livecontext.elastic.social.cae.UserMapper;
import com.coremedia.livecontext.elastic.social.cae.services.LiveContextUserService;
import com.coremedia.livecontext.elastic.social.cae.springsecurity.LiveContextUsernamePasswordAuthenticationToken;
import com.coremedia.livecontext.services.SessionSynchronizer;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.webflow.execution.RequestContext;

import javax.annotation.Nonnull;
import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.GeneralSecurityException;

import static com.coremedia.blueprint.elastic.social.cae.flows.RegistrationHelper.getRequestAttributes;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implements an UserService to integrate with a commerce system.
 */
public class LiveContextUserServiceImpl implements LiveContextUserService, SessionSynchronizer {

  private static final Logger LOG = LoggerFactory.getLogger(LiveContextUserServiceImpl.class);
  public static final String FAILED_TO_RESET_PASSWORD = "Failed to reset password: {}"; // NOSONAR false positive: Credentials should not be hard-coded

  private SecurityContextLogoutHandler securityContextLogoutHandler;
  private RegistrationHelper registrationHelper;
  private LoginHelper loginHelper;
  private CommunityUserService communityUserService;
  private UserMapper userMapper;

  @Override
  public boolean loginUser(@Nonnull String name,
                           @Nonnull String password,
                           @Nonnull RequestContext context,
                           @Nonnull HttpServletRequest request,
                           @Nonnull HttpServletResponse response) {
    checkNotNull(name);
    checkNotNull(password);
    checkNotNull(context);
    checkNotNull(request);
    checkNotNull(response);
    //TODO: wouldn't it be easier to logon to commerce first?
    Authentication authenticationToken = new LiveContextUsernamePasswordAuthenticationToken(request, response, name, password);
    return loginHelper.authenticate(authenticationToken, context);
  }

  @Override
  public boolean isPasswordExpired(LoginForm form) {
    com.coremedia.livecontext.ecommerce.user.User user = getCommerceUser(form.getName());
    return user.isPasswordExpired();
  }

  @Override
  public boolean resetPassword(LiveContextPasswordReset passwordReset, RequestContext context) {
    try {
      CommunityUser user = communityUserService.getUserByEmail(passwordReset.getEmailAddress());
      if (user != null) {
        getCommerceUserService().resetPassword(user.getName(), null);
        return true;
      }
    } catch (CommerceException e) {
      LOG.error(FAILED_TO_RESET_PASSWORD, e.getMessage(), e);
    }
    return false;
  }

  @Override
  public LiveContextUserDetails getUserDetails(User user) {
    CommunityUser communityUser = communityUserService.createFrom(user);
    final LiveContextUserDetails details = new LiveContextUserDetails();
    details.setUsername(user.getName());

    com.coremedia.livecontext.ecommerce.user.User commerceUser = getCommerceUser(details.getUsername());
    userMapper.applyPersonToUserDetails(commerceUser, details, communityUser);

    return details;
  }

  @Override
  public boolean updatePassword(LiveContextPasswordReset passwordReset, RequestContext context) {
    //apply user context for the current user
    CommunityUser user = getCurrentUser();

    try {
      passwordReset.validateResetForm(context);
      if (hasErrorMessages(context)) {
        return false;
      }

      UserContext userContext = getUserContextProvider().createContext(user.getName());
      getUserContextProvider().setCurrentContext(userContext);

      //update password
      String currentPassword = passwordReset.getCurrentPassword();
      String newPassword = passwordReset.getPassword();
      String confirmNewPassword = passwordReset.getConfirmPassword();
      getCommerceUserService().updateCurrentUserPassword(currentPassword, newPassword, confirmNewPassword);

      return true;
    } catch (CommerceRemoteException e) {
      addErrorMessage(context, LiveContextUserServiceUtil.resolveErrorMessage(e));
      LOG.info("Failed to set password for user {}: {}", user.getName(), e.getMessage());
    } catch (CommerceException e) {
      addErrorMessage(context, WebflowMessageKeys.USER_DETAILS_FORM_ERROR);
      LOG.warn("Failed to set password for user {}: {}", user.getName(), e.getMessage(), e);
    }
    return false;
  }

  /**
   * Stores the person data for the given user input into the commerce system.
   *
   * @param userDetails The user details input from the user.
   * @param context     The request context for adding possible error messages.
   */
  @Override
  public boolean saveUser(LiveContextUserDetails userDetails, RequestContext context) {
    try {
      com.coremedia.livecontext.ecommerce.user.User commerceUser = getCommerceUser(userDetails.getUsername());
      userMapper.applyUserToPerson(commerceUser, userDetails);
      saveCommerceUser(commerceUser);
      return true;
    } catch (CommerceRemoteException e) {
      MessageHelper.addErrorMessageWithSource(context, LiveContextUserServiceUtil.resolveErrorMessage(e), "password");
      LOG.info("Failed to set password for user '" + userDetails.getUsername() + "': " + e.getMessage());
    } catch (CommerceException e) {
      MessageHelper.addErrorMessageWithSource(context, WebflowMessageKeys.USER_DETAILS_FORM_ERROR, "password");
      LOG.warn("Failed to set password for user '" + userDetails.getUsername() + "': " + e.getMessage(), e);
    }
    return false;
  }

  @Override
  public boolean registerUser(@Nonnull LiveContextRegistration registration,
                              @Nonnull RequestContext context) {
    checkNotNull(registration);
    checkNotNull(context);

    //disable automatic activation: the registration is only successful if the commerce registration was successful too
    RequestAttributes requestAttributes = getRequestAttributes(context);
    requestAttributes.setAttribute(RegistrationHelper.ELASTIC_AUTOMATIC_USER_ACTIVATION, "false", RequestAttributes.SCOPE_REQUEST);

    String username = registration.getUsername();

    CommunityUser communityUser = registerInCMS(registration, context);
    if (communityUser != null) {
      boolean registeredInCommerceSuccessful = registerUserInCommerce(context, registration);
      if (registeredInCommerceSuccessful) {
        //registration was successful, so activate user immediately
        registrationHelper.activate(communityUser.getProperty("token", String.class), context);

        //ok, all steps are successful, so login the user immediately!
        login(username, registration.getPassword(), context);
        return true;
      }
    }

    if (!hasErrorMessages(context)) {
      addErrorMessage(context, WebflowMessageKeys.REGISTRATION_PROVIDER_ERROR);
    }
    return false;
  }

  private CommunityUser registerInCMS(LiveContextRegistration registration, RequestContext context) {
    //we are resetting the password here as we don't want to store the user credentials on our site.
    //but we will need it for the registration in commerce.
    String realPassword = registration.getPassword();
    CommunityUser user = registrationHelper.register(registration, context, null, null);
    registration.setPassword(realPassword);
    return user;
  }

  /**
   * Needed for testing purposes to mock this static call.
   *
   * @param context the webflow request context
   * @return true if the context contains an error message.
   */
  @VisibleForTesting
  boolean hasErrorMessages(RequestContext context) {
    return context.getMessageContext().hasErrorMessages();
  }

  @VisibleForTesting
  protected CommunityUser getCurrentUser() {
    return com.coremedia.blueprint.elastic.social.cae.user.UserContext.getUser();
  }

  @Override
  public boolean isLoggedIn(HttpServletRequest request) {
    try {
      return getCommerceUserSessionService().isLoggedIn();
    } catch (CredentialExpiredException e) {
      LOG.trace("Login Credential exipired: {}", e.getMessage());
      return false;
    }
  }


  /**
   * Needed for testing purpose to mock this method.
   *
   * @param context the RequestContext were the message should be added
   * @param key     the resource key of the error message
   */
  protected void addErrorMessage(RequestContext context, String key) {
    addErrorMessage(context, key, null);
  }

  @VisibleForTesting
  protected void addErrorMessage(RequestContext context, String key, String source) {
    MessageHelper.addErrorMessage(context, key, source);
  }


  @Override
  public boolean logoutUser(HttpServletRequest request, HttpServletResponse response, RequestContext context) {
    try {
      securityContextLogoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
      return getCommerceUserSessionService().logoutUser(request, response);
    } catch (GeneralSecurityException e) {
      addErrorMessage(context, WebflowMessageKeys.LOGIN_GENERAL_ERROR);
      LOG.debug("Security Exception caught: " + e.getMessage());
      return false;
    }
  }

  @Override
  public void synchronizeUserSession(HttpServletRequest request, HttpServletResponse response) throws GeneralSecurityException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    boolean authenticatedLocally = authentication != null && authentication.getPrincipal() instanceof UserPrincipal;
    boolean authenticatedOnCommerce = getCommerceUserSessionService().isLoggedIn();

    if (authenticatedLocally) {
      if (!authenticatedOnCommerce) {
        localLogout(request, response, authentication);
        getCommerceUserSessionService().clearCommerceSession(request, response);
      }
      else {
        com.coremedia.livecontext.ecommerce.user.User commerceUser = getCommerceUserService().findCurrentUser();
        if (commerceUser != null && commerceUser.getLogonId() != null && !commerceUser.getLogonId().equals(authentication.getName())) {
          // user has changed login e.g. in AuroraESite and session-cached authentication is different
          if (LOG.isDebugEnabled()) {
            LOG.debug("cached authentication in session ({}) differs from commerce user session ({}), re-login user locally...",
                    authentication.getName(), commerceUser.getLogonId());
          }
          localLogin();
        }
      }
    } else if (authenticatedOnCommerce) {
      localLogin();
    }
  }

  private void localLogout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    securityContextLogoutHandler.logout(request, response, authentication);
    com.coremedia.blueprint.elastic.social.cae.user.UserContext.clear();
  }

  private void localLogin() {
    com.coremedia.livecontext.ecommerce.user.User shopUser = getCommerceUserService().findCurrentUser();

    if (shopUser != null) {
      //get community user
      CommunityUser communityUser;
      try {
        communityUser = getOrCreateCommunityUser(shopUser);
      } catch (UniqueConstraintViolationException ex) {
        communityUser = getOrCreateCommunityUser(shopUser);
      }
      com.coremedia.blueprint.elastic.social.cae.user.UserContext.setUser(communityUser);
    } else {
      LOG.warn("Could not find current user {} in shop system", getUserContextProvider().getCurrentContext().getUserId());
    }
  }

  private synchronized CommunityUser getOrCreateCommunityUser(com.coremedia.livecontext.ecommerce.user.User shopUser) {
    CommunityUser communityUser = communityUserService.getUserByName(shopUser.getLogonId());
    if (communityUser == null) {
      //register communityUser if not existing
      communityUser = communityUserService.createUser(shopUser.getLogonId(), null, shopUser.getEmail1());
      communityUser.setProperty("state", CommunityUser.State.ACTIVATED);
      communityUser.save();
    }
    return communityUser;
  }

  /**
   * Executes the commerce registration.
   *
   * @param context      The request context to apply error messages too.
   * @param registration The registration data.
   * @return True, if the registration was successful.
   */
  private boolean registerUserInCommerce(RequestContext context, Registration registration) {
    try {
      String username = registration.getUsername();
      String password = registration.getPassword();
      String email = registration.getEmailAddress();

      com.coremedia.livecontext.ecommerce.user.User commerceUser = getCommerceUserService().registerUser(username, password, email);
      userMapper.applyRegistrationToPerson(commerceUser, (LiveContextRegistration) registration);
      saveCommerceUser(commerceUser);

      return true;
    } catch (Exception e) {
      LOG.warn("Error registering user: {}", e.getMessage(), e);
      handleRegistrationError(registration, context, e);
    }
    return false;
  }


  /**
   * Updates the given person instance.
   *
   * @param commerceUser the commerce person
   */
  private void saveCommerceUser(com.coremedia.livecontext.ecommerce.user.User commerceUser) {
    // ATTENTIONE, ATTENTIONE, ATTENTIONE: user context is being changed.
    getUserContextProvider().setCurrentContext(getUserContextProvider().createContext(commerceUser.getLogonId()));
    getCommerceUserService().updateCurrentUser(commerceUser);
  }

  /**
   * Returns the commerce user for the given ES user.
   *
   * @param username The user name to retrieve the Commerce person instance for.
   */
  @VisibleForTesting
  protected com.coremedia.livecontext.ecommerce.user.User getCommerceUser(String username) {
    return getCommerceUserService().findCurrentUser();
  }

  /**
   * Executes the error handling if the registration on commerce site fails.
   *
   * @param e The exception thrown by the commerce system.
   */
  private void handleRegistrationError(Registration registration, RequestContext context, Exception e) {
    LOG.info("Failed apply changes for user '" + registration.getUsername() + "': " + e.getMessage());
    if (e instanceof CommerceRemoteException) {
      CommerceRemoteException cre = (CommerceRemoteException) e;
      addErrorMessage(context, LiveContextUserServiceUtil.resolveErrorMessage(cre));
    } else {
      addErrorMessage(context, WebflowMessageKeys.REGISTRATION_FORM_ERROR);
    }

    //delete the user from ES, since the registration has failed.
    CommunityUser user = communityUserService.getUserByName(registration.getUsername());
    if (user != null) {
      communityUserService.removeUser(user);
    }
  }

  /**
   * Method to automatically login the user after a successfull registration.
   */
  private void login(String username, String password, RequestContext context) {
    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getNativeRequest();
    HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getNativeResponse();

    loginUser(username, password, context, request, response);
  }

  // ---------- Config ----------------------

  @Required
  public void setLoginHelper(LoginHelper helper) {
    this.loginHelper = helper;
  }

  @Required
  public void setRegistrationHelper(RegistrationHelper helper) {
    this.registrationHelper = helper;
  }

  @Required
  public void setCommunityUserService(CommunityUserService communityUserService) {
    this.communityUserService = communityUserService;
  }

  @Required
  public void setUserMapper(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Required
  public void setSecurityContextLogoutHandler(SecurityContextLogoutHandler securityContextLogoutHandler) {
    this.securityContextLogoutHandler = securityContextLogoutHandler;
  }

  public UserContextProvider getUserContextProvider() {
    return Commerce.getCurrentConnection().getUserContextProvider();
  }

  public UserSessionService getCommerceUserSessionService() {
    return Commerce.getCurrentConnection().getUserSessionService();
  }

  public UserService getCommerceUserService() {
    return Commerce.getCurrentConnection().getUserService();
  }
}
