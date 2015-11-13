package com.coremedia.blueprint.studio.externallibrary {
import ext.Ext;
import ext.data.Record;
import ext.form.ComboBox;

/**
 * The command stack of the third party's filter section.
 */
public class CommandStack {
  private var filterPanel:FilterPanelBase;
  private var history:Array;
  private var activeIndex:int;

  public function CommandStack(filterPanel:FilterPanelBase) {
    this.filterPanel = filterPanel;
    this.history = [];
    this.activeIndex = 0;
    this.history.push(new Command(null, null, activeIndex, this));
  }

  /**
   * Adds a new command to the stack. The command contains
   * the active selected data source and the active search string.
   * Both are restored when a command re-executed.
   * @param dataSourceRecord The selected data source combo record.
   * @param filter The active search string.
   */
  public function addCommand(dataSourceRecord:Record, filter:String):void {
    if(activeIndex<(history.length-1)) {
      history = history.slice(0, (activeIndex+1));//remove forward commands.
    }
    activeIndex = history.length;
    this.history.push(new Command(dataSourceRecord, filter, activeIndex, this));
    updateButtons();
  }

  private function updateButtons():void {
    filterPanel.find('itemId', 'back')[0].setDisabled(false);
    filterPanel.find('itemId', 'forward')[0].setDisabled(false);

    if(activeIndex <= 0) {
      filterPanel.find('itemId', 'back')[0].setDisabled(true);
    }

    if(activeIndex>=(history.length-1)) {
      filterPanel.find('itemId', 'forward')[0].setDisabled(true);
    }
  }

  /**
   * Returns the active command index.
   * @return
   */
  public function getActiveIndex():int {
    return activeIndex;
  }

  /**
   * Invoked by a command to restore the commands status.
   * @param index
   */
  public function execute(index:int):void {
    if (index < 0 || index === history.length) {
      return;
    }

    var cmd:Command = this.history[index];
    if(cmd) {
      this.activeIndex = cmd.index;
      var combo:ComboBox = Ext.getCmp('externalDataCombo') as ComboBox;
      var value:* = null;
      if(cmd.record) {
        value = cmd.record.data.name;
      }
      if(value) {
        combo.setValue(value);
        filterPanel.dataSourceValueExpression.setValue(cmd.record);
      }

      filterPanel.filterValueExpression.setValue(cmd.filter);
    }

    updateButtons();
  }

  /**
   * Resets the command stack and the history buttons.
   */
  public function reset():void {
    filterPanel.find('itemId', 'back')[0].setDisabled(true);
    filterPanel.find('itemId', 'forward')[0].setDisabled(true);
    this.activeIndex = 0;
    history = [];
    this.history.push(new Command(null, null, activeIndex, this));
  }
}
}