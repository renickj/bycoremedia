package com.coremedia.blueprint.cae.view;

import com.coremedia.blueprint.common.contentbeans.CMCollection;
import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.RenderNodeDecorator;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decorators are called for each RenderNode (=combination of Bean and View) encountered when rendering a response in the CAE.
 */
public class CollectionUnboxedRenderNodeDecorator implements RenderNodeDecorator {

  private static final Pattern PATTERN_UNBOXEDVIEWTYPE = Pattern.compile("^(.*)\\[unboxed\\]$");
  private static final Pattern VIEW_EXCLUDE_PATTERN = Pattern.compile("^fragmentPreview(\\[[^\\]]*\\]){0,1}$");

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

    if (viewName != null && self instanceof CMCollection) {
      Matcher matcher = PATTERN_UNBOXEDVIEWTYPE.matcher(viewName);
      Matcher fragmentExclude = VIEW_EXCLUDE_PATTERN.matcher(viewName);

      if (matcher.matches() && !fragmentExclude.matches()) {
        String view = matcher.group(1);
        if (view != null && view.isEmpty()) {
          view = null;
        }
        renderNode.setBean(new CollectionUnboxed((CMCollection) self, view));
        renderNode.setView(null);
      }
    }

    return renderNode;
  }
}
