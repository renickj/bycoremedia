package com.coremedia.livecontext.elastic.social.cae.springsecurity;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The LiveContextAuthenticationToken that additionally encapsulated the request and response
 * for the cookie updates.
 */
public class LiveContextUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

  private HttpServletRequest request;
  private HttpServletResponse response;

  public LiveContextUsernamePasswordAuthenticationToken(HttpServletRequest request, HttpServletResponse response,
                                                        String login, String password) {
    super(login, password);
    this.request = request;
    this.response = response;
  }

  public HttpServletRequest getRequest() {
    return request;
  }

  public HttpServletResponse getResponse() {
    return response;
  }
}
