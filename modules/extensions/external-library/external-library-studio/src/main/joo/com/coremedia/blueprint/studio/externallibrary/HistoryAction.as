package com.coremedia.blueprint.studio.externallibrary {
import com.coremedia.blueprint.studio.config.externallibrary.historyAction;
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Component;
import ext.Ext;

/**
 * Moves forward or backwards in the action history.
 */
public class HistoryAction extends Action {

  private var historyValueExpression:ValueExpression;
  private var direction:String;
  private var cmd:Command;
  internal native function get items():Array;

  public function HistoryAction(config:historyAction) {
    super(config);
    direction = config.direction;
    historyValueExpression = config.historyValueExpression;
    historyValueExpression.addChangeListener(updateDisabled);

    if (!config['handler']) {
      setHandler(updateHistory, this);
    }
  }


  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    this.updateDisabled();
  }

  private function updateDisabled():void {
    this.cmd = null;
    var activeIndex:int = historyValueExpression.getValue();
    var filterPanel:FilterPanel = Ext.getCmp('externalLibraryFilterPanel') as FilterPanel;
    if(!filterPanel.getCommandStack()) {
      //nothing, net rendered yet
    }
    else if(direction === 'forward' && filterPanel) {
      cmd = filterPanel.getCommandStack().getCommand((activeIndex+1));
    }
    else if(direction === 'backward' && filterPanel) {
      cmd = filterPanel.getCommandStack().getCommand((activeIndex-2));
    }

    setDisabled(!cmd);
  }

  private function updateHistory() {
    cmd.execute();
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      historyValueExpression && historyValueExpression.removeChangeListener(updateDisabled);
    }
  }
}
}