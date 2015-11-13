package com.coremedia.blueprint.elastic.social.cae.tags;

import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.blueprint.elastic.social.cae.controller.CommentsResult;
import com.coremedia.blueprint.elastic.social.cae.controller.ReviewsResult;
import com.coremedia.blueprint.elastic.social.cae.guid.GuidFilter;
import com.coremedia.blueprint.elastic.social.cae.user.ElasticSocialUserHelper;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialPlugin;
import com.coremedia.elastic.core.api.tenant.TenantService;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * A Facade for utility functions used by FreeMarker templates.
 */
@Named
public class ElasticSocialFreemarkerFacade {

  @Inject
  private ElasticSocialService elasticSocialService;

  @Inject
  private TenantService tenantService;

  @Inject
  private ElasticSocialUserHelper elasticSocialUserHelper;

  @Inject
  private ElasticSocialPlugin elasticSocialPlugin;

  private ElasticSocialMessageKeysFreemarker elasticSocialMessageKeys = new ElasticSocialMessageKeysFreemarker();

  public boolean isLoginAction(Object bean) {
    return ElasticSocialFunctions.isLoginAction(bean);
  }

  public boolean isAnonymousUser() {
    return ElasticSocialFunctions.isAnonymousUser();
  }

  public CommunityUser getCurrentUser() {
    return elasticSocialUserHelper.getCurrentOrAnonymousUser();
  }

  public boolean isAnonymous(CommunityUser communityUser) {
    return ElasticSocialFunctions.isAnonymous(communityUser);
  }

  public boolean hasComplaintForCurrentUser(String id, String collection) {
    return ElasticSocialFunctions.hasComplaintForCurrentUser(id, collection);
  }

  public String getCurrentGuid() {
    return GuidFilter.getCurrentGuid();
  }

  public CommentsResult getCommentsResult(Object target) {
    return new CommentsResult(target);
  }

  public ReviewsResult getReviewsResult(Object target) {
    return new ReviewsResult(target);
  }

  public boolean hasUserWrittenReview(Object target) {
    Review review = elasticSocialService.getReview(elasticSocialUserHelper.getCurrentUser(), target);
    return review != null;
  }

  public boolean hasUserRated(Object target) {
    int rating = elasticSocialService.getRating(elasticSocialUserHelper.getCurrentUser(), target);
    return rating > 0;
  }

  public String getCurrentTenant() {
    return tenantService.getCurrent();
  }

  public ElasticSocialConfiguration getElasticSocialConfiguration(Page page) {
    return elasticSocialPlugin.getElasticSocialConfiguration(page);
  }

  public ElasticSocialMessageKeysFreemarker getElasticSocialMessageKeys() {
    return elasticSocialMessageKeys;
  }
}
