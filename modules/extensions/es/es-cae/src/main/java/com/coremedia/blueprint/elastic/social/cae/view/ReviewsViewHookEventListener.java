package com.coremedia.blueprint.elastic.social.cae.view;

import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.elastic.social.cae.controller.ReviewsResult;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.events.ViewHookEvent;
import com.coremedia.objectserver.view.events.ViewHookEventListener;

import javax.inject.Named;

import static com.coremedia.blueprint.base.cae.web.taglib.ViewHookEventNames.VIEW_HOOK_END;


/**
 * A {@link com.coremedia.objectserver.view.events.ViewHookEventListener} that
 * is responsible for adding the review widget to rendered views.
 */
@Named
public class ReviewsViewHookEventListener implements ViewHookEventListener<CMArticle> {

  @Override
  public RenderNode onViewHook(ViewHookEvent<CMArticle> event) {
    if (VIEW_HOOK_END.equals(event.getId())) {
      return new RenderNode(getReviewsResult(event.getBean()), null);
    }

    return null;
  }

  //====================================================================================================================

  private ReviewsResult getReviewsResult(Object target) {
    return  new ReviewsResult(target);
  }

  @Override
  public int getOrder() {
    return DEFAULT_ORDER;
  }
}
