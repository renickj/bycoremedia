package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.base.navigation.context.finder.ContextFinder;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;

import java.util.Collections;
import java.util.List;

/**
 * Determines that the context of a navigation item is the navigation item itself
 */
public class LiveContextNavigationFinder implements ContextFinder<Content, Content> {

  @Override
  public List<Content> findContextsFor(Content content) {
    if (content.isInProduction() && (content.getType().isSubtypeOf(CMNavigation.NAME) || content.getType().isSubtypeOf(CMExternalChannel.NAME))) {
      return Collections.singletonList(content);
    } else {
      return Collections.emptyList();
    }
  }

  @Override
  public List<Content> findContextsFor(Content linkable, Content currentContext) {
    return findContextsFor(linkable);
  }
}
