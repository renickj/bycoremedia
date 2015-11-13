package com.coremedia.livecontext.elastic.social.cae.services;

import com.coremedia.blueprint.elastic.social.cae.flows.LoginForm;
import com.coremedia.elastic.core.api.users.User;
import com.coremedia.livecontext.elastic.social.cae.LiveContextPasswordReset;
import com.coremedia.livecontext.elastic.social.cae.LiveContextRegistration;
import com.coremedia.livecontext.elastic.social.cae.LiveContextUserDetails;
import org.springframework.webflow.execution.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The UserService contains operations to register, log in and log out users in a spring webflow.
 * Furthermore it contains operations to check the login state of the client.
 */
public interface LiveContextUserService {

  /**
   * Logs in the user which credentials are passed in the LoginForm.
   * Also the context of the webflow is needed to pass error messages to the context
   * The HttpServletRequest and HttpServletResponse objects must be passed to handle cookies.
   *
   * @param context The context of the spring webflow
   * @param request the request sent from the client
   * @param response the response for the client
   * @return true if login ended successfully otherwise false.
   */
  boolean loginUser(String name, String password, RequestContext context, HttpServletRequest request, HttpServletResponse response);

  /**
   * Returns if the user is currently logged in.
   *
   * @param request the request sent from the client containing informations that can be used to evaluate login state.
   * @return true if the user is logged in otherwise false.
   */
  boolean isLoggedIn(HttpServletRequest request);

  /**
   * Returns true if the user password is expired.
   * @return true if the user's password is expired in otherwise false.
   */
  boolean isPasswordExpired(LoginForm form);

  /**
   * Registers a user.
   *
   * @param registration the bean which contains user credentials for registration
   * @param context the context of the spring webflow.
   *
   * @return true if the user is registered otherwise false.
   */
  boolean registerUser(LiveContextRegistration registration, RequestContext context);

  /**
   * Logs out the currently logged in user.
   *
   * @param request the request sent from the client containing informations that can be used for logout
   * @param response the repsonse where to write the results to.
   * @return true if the user is successfully logged out otherwise false.
   */
  boolean logoutUser(HttpServletRequest request, HttpServletResponse response, RequestContext context);

  /**
   * Sends the request to reset the password for the user (probably via mail).
   * @param context the context of the spring webflow.
   * @param passwordReset The form data including the user the password should be resetted for.
   * @return true if the user password has been reset successfully.
   */
  public boolean resetPassword(LiveContextPasswordReset passwordReset, RequestContext context);

  /**
   * Updates the user for the authenticated user.
   *
   * @param passwordReset The form data of the password reset form.
   * @param context the context of the spring webflow.
   * @return true if the password reset was successful.
   */
  public boolean updatePassword(LiveContextPasswordReset passwordReset, RequestContext context);

  /**
   * Saves the ES user details to the commerce system.
   * @param userDetails The user details
   */
  public boolean saveUser(LiveContextUserDetails userDetails, RequestContext context);

  /**
   * Creates a user details object with details read from the commerce system.
   * @param user The ES user to retrieve the details for.
   */
  public LiveContextUserDetails getUserDetails(User user);
}
