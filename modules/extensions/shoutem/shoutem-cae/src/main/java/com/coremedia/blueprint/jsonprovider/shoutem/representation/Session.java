package com.coremedia.blueprint.jsonprovider.shoutem.representation;

/**
 * A authentication result representation.
 */
public class Session {
  private User user;
  private String session_id; //NOSONAR

  public Session(String sessionId, String id) {
    this.user = new User(id);
    this.session_id = sessionId; //NOSONAR
  }
  
  public User getUser() {
    return user;
  }
  
  public String getSession_id(){ //NOSONAR
    return session_id;
  }
}
