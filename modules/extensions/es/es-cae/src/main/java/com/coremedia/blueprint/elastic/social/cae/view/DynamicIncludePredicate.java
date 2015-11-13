package com.coremedia.blueprint.elastic.social.cae.view;

import com.coremedia.blueprint.elastic.social.cae.action.AuthenticationState;
import com.coremedia.objectserver.view.RenderNode;
import com.google.common.base.Predicate;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Return true if {@link AuthenticationState} beans are rendered with one of the following views:
 * "asButton", "asHeader", "asLink",
 */
public class DynamicIncludePredicate implements Predicate<RenderNode> {

  private static final List<String> VIEW_NAMES = Arrays.asList("asButton", "asHeader", "asLink");

  @Override
  public boolean apply(@Nullable RenderNode input) {
    return input != null && input.getBean() instanceof AuthenticationState && VIEW_NAMES.contains(input.getView());
  }
}
