package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.elastic.social.api.ratings.Like;
import com.coremedia.elastic.social.api.users.CommunityUser;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Provides sort strategies for elastic likes.
 */
public class LikesComparator implements Comparator<Like>, Serializable {
  private static final long serialVersionUID = 42L;

  private CommunityUser user;

  public LikesComparator(CommunityUser user) {
    this.user = user;
  }

  @Override
  public int compare(Like o1, Like o2) {
    if (o1.getAuthor().getId().equals(user.getId())) {
      return -1;
    }
    if (o2.getAuthor().getId().equals(user.getId())) {
      return 1;
    }

    return 0;
  }
}
