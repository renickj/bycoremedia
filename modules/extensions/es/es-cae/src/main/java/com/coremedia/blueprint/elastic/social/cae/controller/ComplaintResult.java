package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.elastic.core.api.models.Model;
import com.coremedia.elastic.social.api.ContributionType;
import com.coremedia.elastic.social.api.users.CommunityUser;

public class ComplaintResult extends ContributionResult {

  private boolean alreadyComplaint;

  public ComplaintResult(Object target) {
    super(target);
  }

  public ComplaintResult(Object target,
                         CommunityUser user,
                         ElasticSocialService elasticSocialService,
                         boolean feedbackEnabled,
                         ContributionType contributionType) {
    super(target, user, elasticSocialService, feedbackEnabled, contributionType);
  }

  public boolean hasAlreadyComplained() {
    ensureLoaded();
    return alreadyComplaint;
  }

  @Override
  public boolean isEnabled() {
    // todo remove
    return true;
  }

  @Override
  protected void load() {
    alreadyComplaint = elasticSocialService.hasComplaint(user, target);
  }
}
