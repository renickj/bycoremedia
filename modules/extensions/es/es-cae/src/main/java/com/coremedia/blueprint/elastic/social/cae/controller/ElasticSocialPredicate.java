package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.objectserver.view.RenderNode;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import javax.inject.Named;

@Named
public class ElasticSocialPredicate implements Predicate<RenderNode> {
  @Override
  public boolean apply(@Nullable RenderNode input) {
    return input != null &&
            (input.getBean() instanceof CommentsResult
                    || input.getBean() instanceof ReviewsResult
                    || input.getBean() instanceof ComplaintResult
                    || input.getBean() instanceof RatingResult
                    || input.getBean() instanceof ShareResult
                    || input.getBean() instanceof LikeResult);
  }
}
