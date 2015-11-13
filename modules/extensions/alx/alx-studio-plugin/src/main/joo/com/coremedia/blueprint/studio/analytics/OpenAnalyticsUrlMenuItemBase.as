package com.coremedia.blueprint.studio.analytics {
import com.coremedia.cap.content.Content;
import com.coremedia.ui.data.ValueExpression;

import ext.config.menuitem;
import ext.menu.Item;

internal class OpenAnalyticsUrlMenuItemBase extends Item {

  internal native function get contentExpression():ValueExpression;

  public function OpenAnalyticsUrlMenuItemBase(config:menuitem) {
    super(config);
  }

  public function getContent():Content {
    return contentExpression.getValue();
  }

  [InjectFromExtParent]
  public function setContent(content:Content):void {
    contentExpression.setValue(content);
  }

}
}