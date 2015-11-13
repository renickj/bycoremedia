package com.coremedia.livecontext.elastic.social.cae;

import com.coremedia.blueprint.elastic.social.cae.user.PasswordExpiryPolicy;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.livecontext.ecommerce.user.User;
import com.coremedia.livecontext.ecommerce.user.UserService;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;

/**
 * A {@link com.coremedia.blueprint.elastic.social.cae.user.PasswordExpiryPolicy password expiry policy} that
 * delegates the question whether a users password is expired or not to the commerce system.
 */
public class LiveContextPasswordExpiryPolicy implements PasswordExpiryPolicy {
  private UserService userService;

  /**
   * Unfortunately the {@link com.coremedia.livecontext.ecommerce.user.UserService person service} does
   * not support the search for a given user directly but only wants to have a
   * {@link com.coremedia.livecontext.ecommerce.user.UserContext user context} instead. Hence this method
   * actually tests whether the current users password is expired, no matter which user is provided to the method.
   *
   * @param user ignored. Instead the current users password will be validated.
   */
  @Override
  public boolean isExpiredFor(@Nonnull CommunityUser user) {
    User person = userService.findCurrentUser();
    return person != null && person.isPasswordExpired();
  }

  @Required
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

}
