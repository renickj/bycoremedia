package com.coremedia.blueprint.elastic.social.cae.view;

import com.coremedia.blueprint.common.contentbeans.CMArticle;
import com.coremedia.blueprint.elastic.social.cae.controller.LikeResult;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.events.ViewHookEvent;
import com.coremedia.objectserver.view.events.ViewHookEventListener;

import javax.inject.Named;

/**
 * A {@link com.coremedia.objectserver.view.events.ViewHookEventListener} that
 * is responsible for adding the comments widget to rendered views.
 */
@Named
public class LikeViewHookEventListener implements ViewHookEventListener<CMArticle> {

  @Override
  public RenderNode onViewHook(ViewHookEvent<CMArticle> event) {
    // not yet implemented
    /*
    if(VIEW_HOOK_END.equals(event.getId())) {
      return new RenderNode(getLikeResult(event.getBean()), null);
    }*/
    return null;
  }

  //====================================================================================================================

  private LikeResult getLikeResult(Object target) {
    return new LikeResult(target);
  }

  @Override
  public int getOrder() {
    return DEFAULT_ORDER;
  }
}
