package com.coremedia.blueprint.studio.taxonomy.action {
import com.coremedia.blueprint.studio.config.taxonomy.addKeywordAction;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Component;

public class AddKeywordAction extends Action {

  private var selectionExpression:ValueExpression;
  private var propertyValueExpression:ValueExpression;
  internal native function get items():Array;

  public function AddKeywordAction(config:addKeywordAction) {
    config.handler = applySelection;
    config.text = TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_add_suggestion_action_text;
    super(config);
    selectionExpression = config.selectionExpression;
    propertyValueExpression = config.bindTo.extendBy("properties." + config.propertyName);
    selectionExpression.addChangeListener(updateDisabled);
  }

  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    updateDisabled();
  }

  private function updateDisabled():void {
    setDisabled(true);
    if(selectionExpression.getValue() && selectionExpression.getValue().length > 0) {
      setDisabled(false);
    }
  }

  public function applySelection():void {
    var selection:Array = selectionExpression.getValue();
    var existingEntries = propertyValueExpression.getValue();
    var newEntries:Array = [];
    for(var i:int = 0; i<existingEntries.length; i++) {
      newEntries.push(existingEntries[i]);
    }
    for(var j:int = 0; j<selection.length; j++) {
      newEntries.push(selection[j]);
    }
    propertyValueExpression.setValue(newEntries);
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      selectionExpression && selectionExpression.removeChangeListener(updateDisabled);
    }
  }
}
}