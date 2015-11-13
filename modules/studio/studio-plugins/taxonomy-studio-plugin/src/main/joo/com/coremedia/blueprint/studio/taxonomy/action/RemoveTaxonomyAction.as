package com.coremedia.blueprint.studio.taxonomy.action {
import com.coremedia.blueprint.studio.config.taxonomy.removeTaxonomyAction;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Component;

/**
 * Opens the taxonomy editor and shows the given taxonomy in the tree.
 */
public class RemoveTaxonomyAction extends Action {

  private var propertyName:String;
  private var bindTo:ValueExpression;
  private var selectedValuesExpression:ValueExpression;
  private var propertyValueExpression:ValueExpression;
  private var selectedPositionsExpression:ValueExpression;
  internal native function get items():Array;

  public function RemoveTaxonomyAction(config:removeTaxonomyAction) {
    config.handler = removeTaxonomy;
    config.text = TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_remove_action_text;
    super(config);
    propertyName = config.propertyName;
    bindTo = config.bindTo;
    selectedPositionsExpression = config.selectedPositionsExpression;
    selectedValuesExpression = config.selectedValuesExpression;
    selectedValuesExpression.addChangeListener(updateDisabled);
    propertyValueExpression = bindTo.extendBy('properties', propertyName);
  }

  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    updateDisabled();
  }

  /**
   * Update enabling/disabling selection depending.
   */
  private function updateDisabled():void {
    setDisabled(true);
    if(selectedValuesExpression && selectedValuesExpression.getValue() && selectedValuesExpression.getValue().length > 0) {
      setDisabled(false);
    }
  }


  public function removeTaxonomy():void {
    var originalValue:Array = propertyValueExpression.getValue();
    if (!originalValue) {
      // Should not happen, but be cautious.
      return;
    }
    var selectedPositions:Array = selectedPositionsExpression.getValue();
    var newValue:Array = originalValue.filter(function(val:*, pos:Number):Boolean {
      return selectedPositions.indexOf(pos) < 0;
    });
    propertyValueExpression.setValue(newValue);
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      selectedValuesExpression && selectedValuesExpression.removeChangeListener(updateDisabled);
    }
  }
}
}