package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.elastic.social.cae.flows.LoginHelper;
import com.coremedia.blueprint.elastic.social.cae.flows.Registration;
import com.coremedia.blueprint.elastic.social.cae.flows.RegistrationHelper;
import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.SocialAuthenticationToken;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.Authentication;
import org.springframework.social.connect.Connection;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.webflow.execution.RequestContext;

import static com.coremedia.blueprint.elastic.social.cae.flows.RegistrationHelper.getRequestAttributes;
import static org.springframework.social.connect.web.ProviderSignInUtils.getConnection;

public class LiveContextRegistrationHelper {

  private RegistrationHelper registrationHelper;

  private LoginHelper loginHelper;
  private CommunityUserService communityUserService;

  /**
   * Redirect a logged in user to the home page instead of the registration page.
   *
   * @param context the executing flow's {@link org.springframework.webflow.execution.RequestContext}
   */
  public void registerAndLoginSilent(RequestContext context) {
    RequestAttributes requestAttributes = getRequestAttributes(context);
    Connection<?> connection = getConnection(requestAttributes);
    //ignore call: no connection data available
    if (connection == null) {
      return;
    }

    Registration registration = new Registration();
    //yes, the user is always registered automatically, he should continue shopping after registration
    requestAttributes.setAttribute(RegistrationHelper.ELASTIC_AUTOMATIC_USER_ACTIVATION, "true", RequestAttributes.SCOPE_REQUEST);
    registrationHelper.preProcess(registration, context);
    CommunityUser user = communityUserService.getUserByName(registration.getUsername(), CommunityUser.class);
    //ignore call when there is an authenticated user
    if (user != null) {
      return;
    }

    //No picture
    registrationHelper.register(registration, context, null);
    Authentication authenticationToken = new SocialAuthenticationToken(registration.getUsername(), "");
    loginHelper.authenticate(authenticationToken, context);
  }

  public void redirectLoggedInUserToHomePage(RequestContext context) {
    if (UserContext.getUser() != null) {
      context.getExternalContext().requestExternalRedirect("contextRelative:");
    }
  }

  @Required
  public void setLoginHelper(LoginHelper loginHelper) {
    this.loginHelper = loginHelper;
  }

  @Required
  public void setRegistrationHelper(RegistrationHelper registrationHelper) {
    this.registrationHelper = registrationHelper;
  }

  @Required
  public void setCommunityUserService(CommunityUserService communityUserService) {
    this.communityUserService = communityUserService;
  }
}
