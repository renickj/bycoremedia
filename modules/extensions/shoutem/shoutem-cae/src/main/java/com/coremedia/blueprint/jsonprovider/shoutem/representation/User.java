package com.coremedia.blueprint.jsonprovider.shoutem.representation;

import com.coremedia.elastic.social.api.ratings.Like;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * A user representation.
 */
public class User {
  private String id;
  private String name;

  public User(Like like) {
    if(like.getAuthor() != null) {
      id = like.getAuthor().getId();
      if(!like.getAuthor().isAnonymous()) {
        name = like.getAuthor().getName();
      }
    }
    else {
      name = "- User has been deleted -"; //NOSONAR
    }
  }
  
  public User(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getName() {
    return name;
  }
}
