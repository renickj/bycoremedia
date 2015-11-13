package com.coremedia.cms.studio.queryeditor.conditions {

import ext.Ext;
import ext.Button;
import ext.Container;
import ext.Component;

import com.coremedia.cms.studio.queryeditor.config.conditionEditorHeaderBase;
import com.coremedia.cms.studio.queryeditor.QueryEditor_properties;
import com.coremedia.cms.studio.queryeditor.components.QueryConditionsField;
import com.coremedia.ui.data.ValueExpression;

/**
 * A common header of all condition editors. It triggers the condition editor's deletion.
 */
public class ConditionEditorHeaderBase extends Container {

  public function ConditionEditorHeaderBase(config:conditionEditorHeaderBase = null) {
    super(conditionEditorHeaderBase(Ext.apply({}, config)));
  }

  override protected function afterRender():void {
    setUpDeleteButton();
    super.afterRender();
  }

  /**
   * Set the click handler and the tooltip.
   */
  private function setUpDeleteButton():void {
    var deleteButton:Button = this.getComponent("delete-button") as Button;
    deleteButton.setHandler(onDeleteButtonClick);
//    deleteButton.setTooltip(QueryEditor_properties.INSTANCE.DCQE_tooltip_deletethis);
  }

  private function onDeleteButtonClick():void {
    var conditionsField:QueryConditionsField =
            this.findParentByType(QueryConditionsField) as QueryConditionsField;
    // An owner container of this header is a condition editor.
    conditionsField.removeCondition(this.ownerCt.xtype);
  }
}
}
