package com.coremedia.cms.studio.queryeditor.components {

import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.cms.studio.queryeditor.config.conditionDropTarget;
import com.coremedia.cms.studio.queryeditor.config.queryConditionsField;
import com.coremedia.ui.data.ValueExpression;

import ext.Container;
import ext.Ext;
import ext.IEventObject;
import ext.config.container;
import ext.config.droptarget;
import ext.dd.DragSource;
import ext.dd.DropTarget;

/**
 * A target for dropping applicable conditions.
 */
public class ConditionDropTarget extends Container {

  private var dcqe:ContentQueryEditor;
  private var readOnlyValueExpression:ValueExpression;
  private var dropTarget:DropTarget;

  public function ConditionDropTarget(config:conditionDropTarget = null) {
    super(container(Ext.apply({}, config)));

    readOnlyValueExpression = PropertyEditorUtil.createReadOnlyValueExpression(config.bindTo,
      config.forceReadOnlyValueExpression);

  }

  override protected function afterRender():void {
    dropTarget = makeDropTarget(el);
    super.afterRender();
  }

  /**
   * Creates a drop target from a given component.
   * @param component a future drop target
   */
  private function makeDropTarget(component:*):DropTarget {
    return new DropTarget(component, droptarget({
      ddGroup: "ConditionDD",
      notifyDrop: onDrop,
      notifyOver: onOver
    }));
  }

  /**
   * Creates the new condition editor and updates the applied conditions list.
   * @param source A dropped source.
   * @param e An event object.
   * @param data Custom data the dropped source carries.
   * @return Is drop valid.
   */
  private function onDrop(source:DragSource, e:IEventObject, data:Object):Boolean {
    if (!isWritable()) {
      return false;
    }
    var conditionsField:QueryConditionsFieldBase =
            QueryConditionsFieldBase(this.findParentByType(queryConditionsField.xtype));
    // Create condition editor.
    conditionsField.applyCondition(data.conditionEditorXtype);
    return true; // Validate the drop.
  }

  private function onOver(source:DragSource, e:IEventObject, data:Object):String {
    if (!isWritable()) {
      return dropTarget.dropNotAllowed;
    }
    return dropTarget.dropAllowed;
  }

  private function getContentQueryEditor():ContentQueryEditor {
    if (!dcqe) {
      dcqe = this.findParentByType(ContentQueryEditor) as ContentQueryEditor;
    }
    return dcqe;
  }

  private function isWritable():Boolean {
    return !(readOnlyValueExpression.getValue() === true);
  }

  override protected function beforeDestroy():void  {
    dropTarget && dropTarget.unreg();
    super.beforeDestroy();
  }
}
}
