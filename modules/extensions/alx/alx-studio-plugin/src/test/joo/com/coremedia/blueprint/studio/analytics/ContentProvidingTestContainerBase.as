package com.coremedia.blueprint.studio.analytics {
import com.coremedia.ui.data.util.PropertyChangeEventUtil;

import ext.Container;

import ext.config.container;

public class ContentProvidingTestContainerBase extends Container {

  private var content:Object;
  public function ContentProvidingTestContainerBase(config:container) {
    super(config);
  }

  [ProvideToExtChildren]
  public function getContent():Object {
    return content;
  }

  public function setContent(content:Object):void {
    var oldValue:* = this.content;
    this.content = content;
    PropertyChangeEventUtil.fireEvent(this, 'content', oldValue, content);
  }

}
}