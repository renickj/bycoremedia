package com.coremedia.blueprint.elastic.social.cae.controller;


import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.social.api.ContributionType;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.reviews.DuplicateReviewException;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.objectserver.web.HandlerHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractReviewsResultHandler extends ElasticContentHandler<ReviewsResult> {

  /* TODO min_length is currently duplicated translations for labels */
  private static final int REVIEW_TEXT_MIN_LENGTH = 30;

  protected abstract ReviewsResult getReviewsResult(Object target, boolean enabled, ContributionType contributionType);

  protected ModelAndView handleCreateReview(Site site,
                                            String contextId,
                                            String targetId,
                                            String text,
                                            String title,
                                            Integer rating) {
    Navigation navigation = getNavigation(contextId);

    if (site == null) {
      return HandlerHelper.notFound();
    }

    Object contributionTarget = getContributionTarget(targetId, site);
    if (contributionTarget == null) {
      return HandlerHelper.notFound();
    }
    Object[] beans = getBeansForSettings(contributionTarget, navigation).toArray();

    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);

    CommunityUser author = getElasticSocialUserHelper().getCurrentUser();

    HandlerInfo result = new HandlerInfo();
    validateReview(result, author, rating, title, text, beans);

    if (result.isSuccess()) {
      ModerationType moderationType = elasticSocialConfiguration.getReviewModerationType();
      try {
        if (author == null) {
          author = getElasticSocialUserHelper().getAnonymousUser();
        }
        Review newReview = getElasticSocialService().createReview(author, contributionTarget, text, title, rating, moderationType, null, navigation);
        result.setModel(newReview);
        String message;
        if (moderationType.equals(ModerationType.PRE_MODERATION)) {
          message = getMessage(ContributionMessageKeys.REVIEW_FORM_SUCCESS_PREMODERATION, beans);
        } else {
          message = getMessage(ContributionMessageKeys.REVIEW_FORM_SUCCESS, beans);
        }
        result.addMessage(SUCCESS_MESSAGE, null, message);
      } catch (DuplicateReviewException e) {  // NOSONAR no need to log a stacktrace for this
        LOG.info("Could not write a review, the author {} has already written a review for the target {}", e.getAuthor(), e.getTarget());
        addErrorMessage(result, null, ContributionMessageKeys.REVIEW_FORM_ALREADY_REVIEWED, beans);
      } catch (Exception e) {
        LOG.error("Could not write a review", e);
        addErrorMessage(result, null, ContributionMessageKeys.REVIEW_FORM_ERROR, beans);
      }
    }
    return HandlerHelper.createModel(result);
  }

  protected ModelAndView handleGetReviews(Site site, String contextId, String targetId, String view) {
    Navigation navigation = getNavigation(contextId);
    if (site == null) {
      return HandlerHelper.notFound();
    }

    Object contributionTarget = getContributionTarget(targetId, site);
    if (contributionTarget == null) {
      return HandlerHelper.notFound();
    }
    Object[] beans = getBeansForSettings(contributionTarget, navigation).toArray();

    // if elastic social plugin is disabled, go no further
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    if (!elasticSocialConfiguration.isFeedbackEnabled()) {
      return null;
    }

    final ReviewsResult reviewsResult = getReviewsResult(contributionTarget, elasticSocialConfiguration.isFeedbackEnabled(), elasticSocialConfiguration.getReviewType());

    ModelAndView modelWithView = HandlerHelper.createModelWithView(reviewsResult, view);
    NavigationLinkSupport.setNavigation(modelWithView, navigation);

    return modelWithView;
  }

  protected void validateEnabled(HandlerInfo handlerInfo, CommunityUser user, Object... beans) {
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    // user == null was not allowed in previous versions, removed because user filter handling not fix
    if (!elasticSocialConfiguration.isWritingReviewsEnabled()) {
      addErrorMessage(handlerInfo, null, ContributionMessageKeys.REVIEW_FORM_ERROR_NOT_ENABLED, beans);
    } else if ((user == null || user.isAnonymous()) && !elasticSocialConfiguration.isAnonymousReviewingEnabled()) {
      addErrorMessage(handlerInfo, null, ContributionMessageKeys.REVIEW_FORM_NOT_LOGGED_IN, beans);
    }
  }

  protected void validateReview(HandlerInfo handlerInfo, CommunityUser user, Integer rating, String title, String text, Object... beans) {
    // user == null was not allowed in previous versions, removed because user filter handling not fix
    validateEnabled(handlerInfo, user, beans);
    if (rating == null) {
      addErrorMessage(handlerInfo, "rating", ContributionMessageKeys.REVIEW_FORM_ERROR_RATING_BLANK, beans);
    }

    if (StringUtils.isBlank(title)) {
      addErrorMessage(handlerInfo, "title", ContributionMessageKeys.REVIEW_FORM_ERROR_TITLE_BLANK, beans);
    }

    if (StringUtils.isBlank(text)) {
      addErrorMessage(handlerInfo, "text", ContributionMessageKeys.REVIEW_FORM_ERROR_TEXT_BLANK, beans);
    } else if (text.length() < REVIEW_TEXT_MIN_LENGTH) {
      addErrorMessage(handlerInfo, "text", ContributionMessageKeys.REVIEW_FORM_ERROR_TEXT_TOO_SHORT, beans);
    }
  }
}
