package com.coremedia.cms.studio.queryeditor.components {

import com.coremedia.cms.studio.queryeditor.config.conditionDroppableBase;
import com.coremedia.ui.components.IFrameMgr;

import ext.Container;
import ext.Ext;
import ext.config.dragsource;
import ext.dd.DragSource;

/**
 * A condition that can be dragged and dropped at the end of the applied
 * conditions list and thus added to the same list.
 */
public class ConditionDroppableBase extends Container {

  private var conditionEditorXtype:String;

  public function ConditionDroppableBase(config:conditionDroppableBase = null) {
    super(conditionDroppableBase(Ext.apply({}, config)));
    conditionEditorXtype = config.conditionEditorXtype;
  }

  override protected function afterRender():void {
    var dragData:Object = {
      conditionEditorXtype: getConditionEditorXtype()
    };
    makeDragSource(el, dragData);
    super.afterRender();
  }

  /**
   * Creates a draggable object from a given component.
   * @param component a future draggable object
   * @param dragData
   */
  private function makeDragSource(component:*, dragData:Object=undefined):void {
    //TODO: refactor, create ConditionDragSource that extends onBeforeDrag
    new DragSource(component, dragsource({
      dragData:dragData,
      ddGroup:"ConditionDD",
      scroll:false,
      onBeforeDrag:function ():Boolean {
        return !disabled;
      },
      onStartDrag:function ():void {
        IFrameMgr.getInstance().showShims();
      },
      onEndDrag:function ():void {
        IFrameMgr.getInstance().hideShims();
      }
    }));
  }

  public function getConditionEditorXtype():String {
    return conditionEditorXtype;
  }

  protected static function getContainerHeight(text:String):int {
    if(text.length >= 14) {
      return 36;
    }
    return 24;
  }

}
}
