package com.coremedia.blueprint.studio.dialog.editors {
import com.coremedia.blueprint.studio.config.components.pasteLinkAction;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.clipboard.Clipboard;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Action;
import ext.Component;

public class PasteLinkAction extends Action {

  private var listExpression:ValueExpression;
  private var disabledExpression:ValueExpression;

  private var allowedDocTypes:String;
  internal native function get items():Array;

  public function PasteLinkAction(config:pasteLinkAction = null) {
    config.handler = pasteFromClipboard;
    allowedDocTypes = config.allowedDocTypes;
    super(config);
    this.listExpression = config.contentValueExpression;
    disabledExpression = ValueExpressionFactory.createFromFunction(calculateDisabled);
    disabledExpression.addChangeListener(updateDisabledStatus);
    Clipboard.getInstance().addValueChangeListener(updateDisabledStatus);
    updateDisabledStatus();
  }

  private function calculateDisabled():Boolean {
    var clipboard:Clipboard = Clipboard.getInstance();
    var contents:Array = clipboard.getContents();
    return !contents || contents.length === 0 || isDisabledFor(contents);
  }

  /**
   * Return the contents on which this action operates.
   */
  protected function getContents():Array {
    var value:* = listExpression.getValue();
    return value is Content ? [value as Content] : value is Array ? value as Array : [];
  }

  private function updateDisabledStatus():void {
    var value:* = disabledExpression.getValue();
    setDisabled(value === undefined || value);
  }

  private function isDisabledFor(contents:Array):Boolean {
    var clipboard:Clipboard = Clipboard.getInstance();
    var contentsToPaste:Array = clipboard.getContents();
    for(var i:int = 0; i<contentsToPaste.length; i++) {
      var content:Content = contentsToPaste[i];
      if(content.getType().isSubtypeOf(allowedDocTypes) && !content.isDeleted()) {
        return false;
      }
    }
    return true;
  }

  private function pasteFromClipboard():void {
    var clipboard:Clipboard = Clipboard.getInstance();
    var contentsToPaste:Array = clipboard.getContents();
    listExpression.setValue([contentsToPaste[0]]);
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      Clipboard.getInstance().removeValueChangeListener(updateDisabledStatus);
      disabledExpression && disabledExpression.removeChangeListener(updateDisabledStatus);
    }
  }
}
}
