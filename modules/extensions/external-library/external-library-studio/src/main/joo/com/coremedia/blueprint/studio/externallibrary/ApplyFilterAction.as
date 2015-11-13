package com.coremedia.blueprint.studio.externallibrary {
import com.coremedia.blueprint.studio.config.externallibrary.applyFilterAction;
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Component;
import ext.Ext;
import ext.config.action;

/**
 * Applies the filter string of the filter area, displays all list items afterwards.
 */
public class ApplyFilterAction extends Action {
  private var filterValueExpression:ValueExpression;
  private var dataSourceValueExpression:ValueExpression;
  internal native function get items():Array;

  /**
   * @param config
   */
  public function ApplyFilterAction(config:applyFilterAction) {
    filterValueExpression = config.filterValueExpression;
    dataSourceValueExpression = config.dataSourceValueExpression;
    dataSourceValueExpression.addChangeListener(updateDisabled);
    super(action(Ext.apply({
      handler: function():void {
        var filter:FilterPanel= Ext.getCmp('externalLibraryFilterPanel') as FilterPanel;
        filter.applyFilter();
      }
    }, config)));
  }


  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    updateDisabled();
  }

  private function updateDisabled():void {
    setDisabled(!dataSourceValueExpression.getValue());
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      dataSourceValueExpression && dataSourceValueExpression.removeChangeListener(updateDisabled);
    }
  }
}
}
