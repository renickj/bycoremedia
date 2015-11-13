package com.coremedia.blueprint.studio.topicpages.administration {
import com.coremedia.blueprint.studio.topicpages.config.filterPanel;
import com.coremedia.ui.data.ValueExpression;

import ext.Button;
import ext.Container;
import ext.IEventObject;
import ext.form.Field;
import ext.form.TextField;

/**
 * The super class of the filter search text field.
 */
public class FilterPanelBase extends Container {
  private var filterExpression:ValueExpression;


  public function FilterPanelBase(config:filterPanel) {
    super(config);
    this.filterExpression = config.filterExpression;
    addListener('afterlayout', init);
  }


  override protected function afterRender():void {
    super.afterRender();
    getFilterField().addListener('keyup', searchChanged);
  }

  private function init():void {
    removeListener('afterlayout', init);
    getFilterButton().hide();
  }

  private function searchChanged(field:Field, e:*):void {
    if(field.getValue().length > 0) {
      getFilterButton().show();
    }
    else {
      getFilterButton().hide();
    }
  }

  /**
   * Executed when the user presses the enter key of the search area.
   * @param field The field the event was triggered from.
   * @param e The key event.
   */
  protected function applyFilterInput(field:Field, e:IEventObject):void {
    if (e.getKey() === e.ENTER) {
      filterExpression.setValue(field.getValue());
      e.stopEvent();
    }
  }

  private function getFilterButton():Button {
    return find('itemId', 'resetFilter')[0];
  }


  private function getFilterField():TextField {
    return find('itemId', 'filterTextField')[0];
  }

  /**
   * Handler for the reset filter button, applies an empty string to
   * the filter expression, therefore to the textfield.
   */
  protected function resetFilter():void {
    filterExpression.setValue('');
    getFilterButton().hide();
  }
}
}