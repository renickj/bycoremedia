package com.coremedia.blueprint.studio.externallibrary {
import com.coremedia.blueprint.studio.config.externallibrary.resetFilterAction;
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Component;
import ext.Ext;
import ext.config.action;
import ext.form.TextField;

/**
 * Resets the filter string of the filter area, displays all list items afterwards.
 * The external content list is re-build afterwards.
 */
public class ResetFilterAction extends Action {
  private var filterValueExpression:ValueExpression;
  private var dataSourceValueExpression:ValueExpression;
  internal native function get items():Array;

  /**
   * @param config
   */
  public function ResetFilterAction(config:resetFilterAction) {
    this.filterValueExpression = config.filterValueExpression;
    this.dataSourceValueExpression = config.dataSourceValueExpression;

    this.filterValueExpression.addChangeListener(updateDisabled);
    this.dataSourceValueExpression.addChangeListener(updateDisabled);

    super(action(Ext.apply({
      handler: function():void {
        filterValueExpression.setValue(null);
        setDisabled(true);
        var filterField:TextField = Ext.getCmp('externalLibrarySearchFilter') as TextField;
        filterField.setValue('');
      }
    }, config)));
  }


  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    updateDisabled();
  }

  private function updateDisabled():void {
    setDisabled(!dataSourceValueExpression.getValue() || !filterValueExpression.getValue());
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      filterValueExpression && filterValueExpression.removeChangeListener(updateDisabled);
      dataSourceValueExpression && dataSourceValueExpression.removeChangeListener(updateDisabled);
    }
  }
}
}
