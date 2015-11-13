package com.coremedia.blueprint.elastic.social.cae.action;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.action.webflow.WebflowActionState;
import com.coremedia.blueprint.cae.handlers.WebflowHandlerBase;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.elastic.social.cae.user.PasswordExpiryPolicy;
import com.coremedia.blueprint.elastic.social.cae.user.UserContext;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.objectserver.view.substitution.Substitution;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import static com.coremedia.blueprint.base.links.UriConstants.Links.ABSOLUTE_URI_KEY;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_NUMBER;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENTS_FRAGMENT;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ACTION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ID;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ROOT;
import static com.coremedia.blueprint.base.links.UriConstants.Views.VIEW_FRAGMENT;

/**
 * Handles authentication (login/logout) actions. This handler is currently used for rendering the initial form only.
 * The login logic itself is handled by a Webflow
 * <p/>
 * See com.coremedia.blueprint.elastic.social.cae.flows.Login.xml
 * See com.coremedia.blueprint.elastic.social.cae.flows.Logout.xml
 */
@Link
@RequestMapping
public class AuthenticationHandler extends WebflowHandlerBase {

  public static final String LOGIN_ACTION_ID = "com.coremedia.blueprint.elastic.social.cae.flows.Login";
  public static final String PROFILE_ACTION_ID = "com.coremedia.blueprint.elastic.social.cae.flows.UserDetails";
  public static final String REGISTRATION_ACTION_ID = "com.coremedia.blueprint.elastic.social.cae.flows.Registration";

  public static final String EXPIRED_PASSWORD_SETTING_ID = "flow.passwordExpired"; // NOSONAR false positive: Credentials should not be hard-coded

  private static final String URI_PREFIX = "auth";

  /**
   * URI pattern suffix for actions on page resources.
   */
  public static final String URI_PATTERN_SUFFIX =
                  '/' + URI_PREFIX +
                  "/{" + SEGMENT_ROOT + "}" +
                  "/{" + SEGMENT_ID + ":" + PATTERN_NUMBER + "}" +
                  "/{" + SEGMENT_ACTION + "}";

  /**
   * Full action URI pattern, for URIs like "/dynamic/auth/media/4420/login"
   */
  public static final String URI_PATTERN = '/' + PREFIX_DYNAMIC + URI_PATTERN_SUFFIX;

  /**
   * Fragment URI pattern, for URIs like "/dynamic/fragment/auth/media/4420/login"
   */
  public static final String DYNAMIC_URI_PATTERN = '/' + PREFIX_DYNAMIC + '/' + SEGMENTS_FRAGMENT + URI_PATTERN_SUFFIX;

  private SettingsService settingsService;
  private PasswordExpiryPolicy passwordExpiryPolicy;
  private LinkFormatter linkFormatter;

  // --- configure --------------------------------------------------
  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  public SettingsService getSettingsService() {
    return settingsService;
  }

  /**
   * Creates a bean that represents the authentication state
   */
  @Substitution(LOGIN_ACTION_ID)
  public AuthenticationState createLoginActionStateBean(CMAction action) {
    RegistrationDisclaimers disclaimers = settingsService.createProxy(RegistrationDisclaimers.class, action);
    return new AuthenticationState(action, null, AuthenticationState.class.getName(), null, disclaimers, getSettingsService());
  }

  /**
   * Creates a bean that represents the authentication state
   */
  @Substitution(PROFILE_ACTION_ID)
  public Object createProfileActionStateBean(CMAction action) {
    // it's the same bean than for the login action. In fact the profile CMAction holds the logout button and  only
    // a link pointing to the profile. Thus, "login" and "profile" action might be merged.
    return createLoginActionStateBean(action);
  }

  /**
   * Creates a bean that represents the registration state
   */
  @Substitution(REGISTRATION_ACTION_ID)
  public Object createRegistrationActionStateBean(CMAction action) {
    return createLoginActionStateBean(action);
  }

  // --------------- handler --------------

  /**
   * Handle requests to CMAction authentication beans.
   * Fallback: Handles all remaining actions by simply displaying the page
   */
  @RequestMapping(value = URI_PATTERN)
  public ModelAndView handleRequest(@PathVariable(SEGMENT_ID) CMAction action,
                                    @PathVariable(SEGMENT_ROOT) String context,
                                    @PathVariable(SEGMENT_ACTION) String actionName,
                                    HttpServletRequest request, HttpServletResponse response) {
    return handleRequestInternal(action, context, actionName, request, response);
  }

  @Override
  protected WebflowActionState getWebflowActionState(CMAction action, ModelAndView webFlowOutcome, String flowId, String flowViewId) {
    RegistrationDisclaimers disclaimers = settingsService.createProxy(RegistrationDisclaimers.class, action);
    return new AuthenticationState(action, webFlowOutcome.getModelMap(), flowId, flowViewId, disclaimers, getSettingsService());
  }

  // ---------------- links -------------------

  /**
   * Builds a generic action link for an {@link AuthenticationState} form.
   */
  @Link(type = AuthenticationState.class, uri = URI_PATTERN)
  public UriComponents buildLink(AuthenticationState action, UriTemplate uriPattern, Map<String, Object> linkParameters, HttpServletRequest request) {
    String actionName = getVanityName(action.getAction());
    Navigation context = getNavigation(action.getAction());
    UriComponentsBuilder result = UriComponentsBuilder.fromPath(uriPattern.toString());
    result = addLinkParametersAsQueryParameters(result, linkParameters);
    return result.buildAndExpand(ImmutableMap.of(
            SEGMENT_ID, getId(action.getAction()),
            SEGMENT_ROOT, getPathSegments(context).get(0),
            SEGMENT_ACTION, actionName
    ));
  }

  //------------ Dynamic Fragments

  /**
   * Builds a generic action link for an {@link AuthenticationState} dynamic fragment (login button).
   */
  @Link(type = AuthenticationState.class, view = VIEW_FRAGMENT, uri = DYNAMIC_URI_PATTERN)
  public UriComponents buildDynamicFragmentLink(AuthenticationState action, UriTemplate uriPattern, Map<String, Object> linkParameters, HttpServletRequest request) {
    //todo simplify URI pattern
    String actionName = getVanityName(action.getAction());
    Navigation context = getNavigation(action.getAction());
    UriComponentsBuilder result = UriComponentsBuilder.fromPath(uriPattern.toString());
    result = addLinkParametersAsQueryParameters(result, linkParameters);
    return result.buildAndExpand(ImmutableMap.of(
            SEGMENT_ID, getId(action.getAction()),
            SEGMENT_ROOT, getPathSegments(context).get(0),
            SEGMENT_ACTION, actionName
    ));
  }

  @RequestMapping(value = DYNAMIC_URI_PATTERN , method = RequestMethod.GET)
  public ModelAndView handleDynamicFragmentRequest(@PathVariable(SEGMENT_ID) CMAction action,
                                                   @PathVariable(SEGMENT_ROOT) String rootPath,
                                                   @RequestParam(value = "targetView", required = false) String view,
                                                   @RequestParam(value = "webflow", required = false) String webflow,
                                                   HttpServletRequest request,
                                                   HttpServletResponse response) {
    CommunityUser user = UserContext.getUser();

    if (user != null && passwordExpiryPolicy.isExpiredFor(user) && !Boolean.valueOf(String.valueOf(webflow))) {
      return forceNewPassword(action, request, response);
    }

    Navigation navigationContext = getNavigation(Collections.singletonList(rootPath));
    if (navigationContext == null) {
      return HandlerHelper.notFound();
    }
    AuthenticationState bean = createLoginActionStateBean(action);
    // add navigationContext as navigationContext request param
    ModelAndView modelWithView = HandlerHelper.createModelWithView(bean, view);
    NavigationLinkSupport.setNavigation(modelWithView, navigationContext);
    return modelWithView;
  }

  private ModelAndView forceNewPassword(@Nonnull CMAction loginAction, @Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
    response.setContentType("text/plain");
    try (PrintWriter writer = response.getWriter()) {
      // Set standard HTTP/1.1 no-cache headers.
      response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
      // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
      response.addHeader("Cache-Control", "post-check=0, pre-check=0");
      // Set standard HTTP/1.0 no-cache header.
      response.setHeader("Pragma", "no-cache");
      writer.print("<script>coremedia.blueprint.basic.redirectTo('" + createForceNewPasswordUrl(loginAction, request, response) +"')</script>");
      writer.flush();
    } catch (Exception ex) {
      LOG.error("A user needs to set a new password but we could not redirect him to the corresponding view.", ex);
      return HandlerHelper.forbidden();
    }

    return null;
  }

  private String createForceNewPasswordUrl(@Nonnull CMAction loginAction, @Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) {
    CMAction passwordExpiredAction = settingsService.setting(EXPIRED_PASSWORD_SETTING_ID, CMAction.class, loginAction);
    AuthenticationState passwordExpiredState = createLoginActionStateBean(passwordExpiredAction);

    request.setAttribute(ABSOLUTE_URI_KEY, true);
    return linkFormatter.formatLink(passwordExpiredState, null, request, response, true);
  }

  @Required
  public void setPasswordExpiryPolicy(PasswordExpiryPolicy passwordExpiryPolicy) {
    this.passwordExpiryPolicy = passwordExpiryPolicy;
  }

  @Required
  public void setLinkFormatter(LinkFormatter linkFormatter) {
    this.linkFormatter = linkFormatter;
  }
}
