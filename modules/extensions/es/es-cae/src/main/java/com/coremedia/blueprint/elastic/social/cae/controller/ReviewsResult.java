package com.coremedia.blueprint.elastic.social.cae.controller;


import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.elastic.social.api.ContributionType;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewsResult extends ListContributionResult<Review> {

  private double averageRating;
  private long numberOfOnlineReviews;
  private Map<Integer, Integer> statistics;

  public ReviewsResult(Object target) {
    super(target);
  }

  public ReviewsResult(Object target,
                       CommunityUser user,
                       ElasticSocialService service,
                       boolean feedbackEnabled,
                       ContributionType contributionType) {
    super(target, user, service, feedbackEnabled, contributionType);
  }

  @Override
  protected void load() {
    List<Review> reviews = getElasticSocialService().getReviews(target, user);
    setContributions(reviews);
    if (reviews != null && !reviews.isEmpty()) {
      averageRating = getElasticSocialService().getAverageReviewRating(target);
      createStatistics(reviews);
      numberOfOnlineReviews = elasticSocialService.getNumberOfReviews(target);
    }
  }

  @Override
  protected List<Review> findRootContributions() {
    throw new IllegalArgumentException("Not yet supported");
  }

  public List<Review> getReviews() {
    return super.getContributions();
  }

  public double getAverageRating() {
    ensureLoaded();
    return averageRating;
  }

  public int getNumberOfOnlineReviewsFor(int rating) {
    ensureLoaded();
    int count = 0;
    if (statistics != null) {
      Integer countInteger = statistics.get(rating);
      count = countInteger == null ? 0 : countInteger;
    }
    return count;
  }

  public long getNumberOfOnlineReviews() {
    ensureLoaded();
    return numberOfOnlineReviews;
  }

  private Map<Integer, Integer> createStatistics(@Nonnull List<Review> reviews) {
    statistics = new HashMap<>();
    for(Review review: reviews) {
      if (showInStatistics(review)) {
        int rating = review.getRating();
        Integer currentCount = statistics.get(rating);
        if (currentCount == null) {
          statistics.put(rating, 1);
        } else {
          statistics.put(rating, currentCount + 1);
        }
      }
    }
    return statistics;
  }

  private boolean showInStatistics(@Nonnull Review review) {
    return Comment.State.NEW_ONLINE.equals(review.getState())
            || Comment.State.APPROVED.equals(review.getState())
            || Comment.State.IGNORED.equals(review.getState());
  }
}
