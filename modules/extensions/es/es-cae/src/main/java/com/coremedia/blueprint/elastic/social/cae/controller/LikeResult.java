package com.coremedia.blueprint.elastic.social.cae.controller;


import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.elastic.social.api.ContributionType;
import com.coremedia.elastic.social.api.users.CommunityUser;

public class LikeResult extends ContributionResult {

  private boolean alreadyLiked;
  private long numberOfLikes;

  public LikeResult(Object target) {
    super(target);
  }

  public LikeResult(Object target,
                         CommunityUser user,
                         ElasticSocialService elasticSocialService,
                         boolean feedbackEnabled,
                         ContributionType contributionType) {
    super(target, user, elasticSocialService, feedbackEnabled, contributionType);
  }

  public boolean hasLiked() {
    ensureLoaded();
    return alreadyLiked;
  }

  public long getNumberOfLikes() {
    ensureLoaded();
    return numberOfLikes;
  }

  @Override
  protected void load() {
    alreadyLiked = elasticSocialService.hasLiked(user, target);
    numberOfLikes = elasticSocialService.getNumberOfLikes(target);
  }
}
