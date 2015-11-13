package com.coremedia.livecontext.p13n.studio {
import com.coremedia.cms.editor.sdk.config.premular;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.cms.editor.sdk.premular.CollapsibleFormPanel;
import com.coremedia.livecontext.p13n.studio.config.commerceGroupContainer;
import com.coremedia.ui.data.ValueExpression;

public class CommerceGroupContainerBase extends CollapsibleFormPanel{

  private var contentExpression:ValueExpression;

  public function CommerceGroupContainerBase(config:commerceGroupContainer) {
    super(config);
  }

  internal function getContentExpression():ValueExpression {
    if (!contentExpression) {
      contentExpression = ComponentContextManager.getInstance().getContextExpression(this, premular.CONTENT_VARIABLE_NAME);
    }
    return contentExpression;
  }
}
}