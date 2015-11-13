package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.blueprint.jsonprovider.shoutem.representation.Session;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.elastic.social.springsecurity.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.StringTokenizer;

/**
 * Shoutem API user credentials.
 */
public class ShoutemApiCredentials {
  private static final Logger LOG = LoggerFactory.getLogger(ShoutemApiCredentials.class);

  private static final String SEPARATOR = "#|#"; //NOSONAR

  private CommunityUserService userService;
  private AuthenticationManager authenticationManager;

  private String sessionId;
  private CommunityUser user;
  private String site;
  private String host;

  public Session authenticate(String login, String password) {
    Authentication authenticationToken = new UsernamePasswordAuthenticationToken(login, password);
    try {
      Authentication authentication = authenticationManager.authenticate(authenticationToken);
      if (user == null) {
        user = getUser(authentication.getPrincipal());
        if (user == null) {
          Response r = Response.status(401).entity("INVALID USERNAME OR PASSWORD").build(); //NOSONAR
          throw new WebApplicationException(r);
        }
      }
      String id = user.getId();
      sessionId = createToken(user, password);
      return new Session(sessionId, id);
    } catch (AuthenticationException e) {
      LOG.error("Authentication exception during authentication of user " + login + ".", e);
      Response r = Response.status(401).entity("AUTHENTICATION ERROR: " + e.getMessage()).build(); //NOSONAR
      throw new WebApplicationException(r); //NOSONAR Logging of exception sufficient.
    } catch (UnsupportedEncodingException e) {
      LOG.error("UnsupportedEncodingException exception during authentication of user " + login + ".", e);
      Response r = Response.status(401).entity("UnsupportedEncodingException: " + e.getMessage()).build(); //NOSONAR
      throw new WebApplicationException(r); //NOSONAR Logging of exception sufficient.
    }
  }

  public void setUser(CommunityUser user) {
    this.user = user;
  }

  /**
   * Creates the string that will be used as a session id.
   *
   * @param user     The username of the user.
   * @param password The password of the user.
   * @return The encrypted session id.
   */
  private String createToken(CommunityUser user, String password) throws UnsupportedEncodingException {
    String token = user.getName() + SEPARATOR + user.getId() + SEPARATOR + password;
    return URLEncoder.encode(Des.encrypt(token), "UTF8");
  }

  /**
   * Checks the credentials that are stored in the session id.
   *
   * @param sessionId The encrypted session id.
   * @return True, if the session contains valid user credentials.
   */
  public boolean isValid(String sessionId) {
    this.sessionId = sessionId;

    String token = Des.decrypt(this.sessionId);
    if (token == null) {
      return false;
    }

    StringTokenizer tokenizer = new StringTokenizer(token, SEPARATOR, false);
    if (tokenizer.countTokens() != 3) { //NOSONAR
      Response r = Response.status(401).entity("AUTHENTICATION ERROR: INVALID TOKEN FORMAT").build(); //NOSONAR
      throw new WebApplicationException(r);
    }

    String login = tokenizer.nextToken();
    String userId = tokenizer.nextToken();
    String password = tokenizer.nextToken();

    user = userService.getUserById(userId);
    if (user != null && !user.isAnonymous()) {
      return true;
    } else {
      authenticate(login, password);
      return true;
    }
  }

  private CommunityUser getUser(Object principal) {
    CommunityUser result = null;
    if (principal instanceof String) {
      result = userService.getUserByName((String) principal);
      if (result == null) {
        result = userService.getUserByEmail((String) principal);
      }
    } else if (principal instanceof UserPrincipal) {
      UserPrincipal userPrincipal = (UserPrincipal) principal;
      result = userService.getUserById(userPrincipal.getUserId());
    }
    return result;
  }

  public void setUserService(CommunityUserService userService) {
    this.userService = userService;
  }

  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  public String getSite() {
    return site;
  }

  public String getHost() {
    return StringUtils.isBlank(host) ? "" : host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public CommunityUser getUser() {
    if (user != null) {
      return user;
    }
    user = userService.createAnonymousUser();
    user.save();
    return user;
  }

  public String getSessionId() {
    return sessionId;
  }

  public boolean isAnonymous() {
    return getUser().isAnonymous();
  }
}
