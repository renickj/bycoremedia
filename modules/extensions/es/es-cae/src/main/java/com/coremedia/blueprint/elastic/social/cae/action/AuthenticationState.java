package com.coremedia.blueprint.elastic.social.cae.action;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.action.webflow.WebflowActionState;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.elastic.social.api.users.CommunityUser;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A bean that represents the authentication action state (either logged in or logged out)
 */
public class AuthenticationState extends WebflowActionState {

  private final RegistrationDisclaimers disclaimers;
  private final SettingsService settingsService;

  /**
   * @param action   The original action
   * @param model    The action's result as named beans
   * @param flowId   The flow id
   * @param flowView The flow view name, e.g. "success"
   */
  public AuthenticationState(CMAction action, Map<String, Object> model,
                             String flowId, String flowView,
                             RegistrationDisclaimers disclaimers,
                             @Nonnull SettingsService settingsService) {
    super(action, model, flowId, flowView);
    this.disclaimers = disclaimers;
    this.settingsService = settingsService;
  }

  /**
   * @return true, if the current user is currently authenticated ("logged in") or not.
   */
  public boolean isAuthenticated() {
    return getUser() != null;
  }

  /**
   * @return The currently {@link #isAuthenticated() authenticated} user or null if not authenticated
   */
  public CommunityUser getUser() {
    return UserContext.getUser();
  }

  /**
   * @return The disclaimer documents
   */
  public RegistrationDisclaimers getDisclaimers() {
    return disclaimers;
  }

  /**
   * @return The CMAction that represents the "login" action
   */
  public CMAction getLoginAction() {
    return getAction();
  }

  /**
   * @return The CMAction that represents the "logout" action
   */
  public CMAction getLogoutAction() {
    return settingsService.setting("flow.logout", CMAction.class, getAction());
  }

  /**
   * @return The CMAction that represents the "password reset" action
   */
  public CMAction getPasswordResetAction() {
    return settingsService.setting("flow.passwordReset", CMAction.class, getAction());
  }

  /**
   * @return The CMAction that represents the "registration" action
   */
  public CMAction getRegistrationAction() {
    CMAction cmAction = getAction();
    if (AuthenticationHandler.REGISTRATION_ACTION_ID.equals(cmAction.getId())) {
      return cmAction;
    }

    return settingsService.setting("flow.registration", CMAction.class, cmAction);
  }

  /**
   * @return The CMAction that represents the "profile"/"user details" action
   */
  public CMAction getProfileAction() {
    return settingsService.setting("flow.userDetails", CMAction.class, getAction());
  }
}
