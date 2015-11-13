/*
 * Copyright (c) 2011, CoreMedia AG, Hamburg.
 *
 * All rights reserved. This source file is provided to you for
 * documentation purposes only. No part of this file may be
 * reproduced or copied in any form without the written
 * permission of CoreMedia AG. No liability can be accepted
 * for errors in the program or in the documentation or for damages
 * which arise through using the program. If an error is discovered,
 * CoreMedia AG will endeavour to correct it as quickly as possible.
 * The use of the program occurs exclusively under the conditions
 * of the licence contract with CoreMedia AG.
 */
package com.coremedia.livecontext.elastic.social.cae.springsecurity;

import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.SocialAuthenticationToken;
import com.coremedia.elastic.social.springsecurity.UserPrincipal;
import com.coremedia.livecontext.ecommerce.user.UserSessionService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;

/**
 * Spring security authentication provider that uses the commerce user service to login instead of elastic.
 */
public class LiveContextUserAuthenticationProvider implements AuthenticationProvider {

  private CommunityUserService communityUserService;
  private UserSessionService commerceUserSessionService;

  /**
   * Authenticates a user with the passed authentication by reading the community user from elastic and may
   * log in with commerce afterwards.
   *
   * @param authentication must be an instance of LiveContextUsernamePasswordAuthenticationToken
   * @return authentication after successful authentication
   * @throws org.springframework.security.authentication.BadCredentialsException
   */
  @Override
  public Authentication authenticate(Authentication authentication) {

    if(authentication instanceof LiveContextUsernamePasswordAuthenticationToken) {
      return authenticateWithCommerce(authentication);
    } else if(authentication instanceof SocialAuthenticationToken) {
      return authenticateSilentWithoutCommerce(authentication);
    }

    throw new IllegalArgumentException("Authentication type " + authentication.getClass() + " is not supported.");
  }

  /**
   * Authenticate with ElasticSocial first, and with Commerce afterwards.
   */
  private Authentication authenticateWithCommerce(Authentication authentication) {
    LiveContextUsernamePasswordAuthenticationToken authenticationToken = (LiveContextUsernamePasswordAuthenticationToken) authentication;
    HttpServletRequest request = authenticationToken.getRequest();
    HttpServletResponse response = authenticationToken.getResponse();

    String credentials = (String) authenticationToken.getCredentials();
    CommunityUser communityUser = communityUserService.getUserByName(authenticationToken.getName());
    if (communityUser == null) {
      communityUser = communityUserService.getUserByEmail(authenticationToken.getName());
    }
    if (communityUser == null || !commerceUserSessionService.loginUser(request, response, communityUser.getName(), credentials)) {
      throw new BadCredentialsException(format("Login failed for user %s", authenticationToken.getName()));
    }
    return new UsernamePasswordAuthenticationToken(
            new UserPrincipal(communityUser.getId(), communityUser.getName()),
            credentials,
            authentication.getAuthorities()
    );
  }

  /**
   * Authenticate with ElasticSocial without password. Do not log in with commerce.
   */
  private Authentication authenticateSilentWithoutCommerce(Authentication authentication) {
    SocialAuthenticationToken authenticationToken = (SocialAuthenticationToken) authentication;
    CommunityUser communityUser = communityUserService.getUserByName(authenticationToken.getName());

    if (communityUser == null || communityUser.isAnonymous()) {
      throw new BadCredentialsException(format("Login failed for user %s", authenticationToken.getName()));
    }
    if (communityUser.isBlocked()) {
      throw new LockedException(format("User %s is blocked", authenticationToken.getName()));
    }
    return new UsernamePasswordAuthenticationToken(
            new UserPrincipal(communityUser.getId(), communityUser.getName()),
            "",
            authentication.getAuthorities()
    );
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return LiveContextUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication) || SocialAuthenticationToken.class.isAssignableFrom(authentication);
  }

  //---------- Config ---------------------------

  @Required
  public void setCommunityUserService(CommunityUserService communityUserService) {
    this.communityUserService = communityUserService;
  }

  @Required
  public void setCommerceUserSessionService(UserSessionService commerceUserSessionService) {
    this.commerceUserSessionService = commerceUserSessionService;
  }
}
