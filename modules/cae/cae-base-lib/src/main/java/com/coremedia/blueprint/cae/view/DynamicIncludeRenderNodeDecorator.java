package com.coremedia.blueprint.cae.view;

import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.RenderNodeDecorator;
import com.google.common.base.Predicate;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Decorators are called for each RenderNode (=combination of Bean and View) encountered when rendering a response in the CAE.
 */
public class DynamicIncludeRenderNodeDecorator implements RenderNodeDecorator {

  private List<Predicate<RenderNode>> predicates;

  public void setPredicates(List<Predicate<RenderNode>> predicates) {
    this.predicates = predicates;
  }

  @Override
  public String decorateViewName(Object self, String viewName) {
    return viewName;
  }

  @Override
  public Object decorateBean(Object self, String viewName) {
    return self;
  }

  @Nonnull
  @Override
  public RenderNode decorateRenderNode(Object self, String viewName) {
    RenderNode renderNode = new RenderNode(self, viewName);

    //make Dynamic Include if any predicate matches
    for (Predicate<RenderNode> predicate : predicates) {
      if (predicate.apply(renderNode)) {
        renderNode.setBean(new DynamicInclude(self, viewName));
        renderNode.setView(null);
        break;
      }
    }

    return renderNode;
  }
}
