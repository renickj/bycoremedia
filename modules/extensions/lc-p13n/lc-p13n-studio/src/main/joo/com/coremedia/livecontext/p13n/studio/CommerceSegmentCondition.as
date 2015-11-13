package com.coremedia.livecontext.p13n.studio {
import com.coremedia.ecommerce.studio.components.CommerceObjectSelector;
import com.coremedia.ecommerce.studio.config.commerceObjectSelector;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.livecontext.p13n.studio.config.commerceSegmentCondition;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.personalization.ui.condition.AbstractCondition;
import com.coremedia.personalization.ui.util.SelectionRuleHelper;

import ext.Ext;
import ext.data.Record;
import ext.form.ComboBox;

public class CommerceSegmentCondition extends AbstractCondition{

  private static const OPERATORS:Array = [SelectionRuleHelper.OP_CONTAINS];

  private var segmentSelector:CommerceObjectSelector;

  // the prefix of the properties rendered by this component
  private var propertyPrefix:String = "";

  public function CommerceSegmentCondition(config:commerceSegmentCondition) {

    initSegmentSelector(config.segmentsEmptyText, config.paths, config.docType);

    config.layout = "hbox";
    config.height = 25;
    config.layoutConfig = Ext.apply(config.layoutConfig || {}, {flex:100});
    super(config);

    addEvents({
      /**
       * @event modified
       * Fires after the data represented by this component was modified. Intended to be used for
       * automatically saving changes.
       * @param {Component} this
       */
      modified:true});

    // check the supplied configuration
    if (config.propertyPrefix == null) {
      throw new Error("config.propertyPrefix must not be null");
    }
    propertyPrefix = config['propertyPrefix'].length > 0 ? config['propertyPrefix'] + '.' : "";

    // create operator combo
    initOpSelector(null, config['operatorNames'], config['operatorEmptyText'], config['operator'],
            OPERATORS, DEFAULT_OPERATOR_DISPLAY_NAMES);

    // init the segment selector
    add(segmentSelector);

  }

  private function initSegmentSelector(emptyText:String, paths:Array, docType:String):void {
    segmentSelector = new CommerceObjectSelector(new commerceObjectSelector({
      itemId: "segmentSelector",
      flex: 30,
      emptyText: LivecontextStudioPlugin_properties.INSTANCE.p13n_commerce_user_segments_selector_emptyText,
      getCommerceObjectsFunction: CatalogHelper.getSegments,
      minListWidth: 200,
      margins: "0 5 0 5",
      forceSelection: true,
      selectOnFocus: true,
      typeAhead: true,
      allowBlank: false,
      triggerAction: "all",
      mode: 'local',
      quote: true
    }));

    segmentSelector.addListener('change', function ():void {
      fireEvent('modified');
    });

    segmentSelector.addListener('select', function (combo:ComboBox, record:Record, index:Number):void {
      fireEvent('modified');
    });

    // if data changed (e.g. segment deleted), validate again
    segmentSelector.getStore().addListener('datachanged', validateStore);

    // validate again, if focus is lost (e.g. open dropdown, and click anywhere else)
    segmentSelector.addListener('blur', validateStore);

    // validate again on afterrender
    segmentSelector.addListener('afterrender', validateStore);

    // validate again on move
    segmentSelector.addListener('move', validateStore);

  }

  /**
   * Validates the comboBox entry. The comboBox will be marked as invalid if the comboBox store doesn't
   * contain the value of the comboBox.
   */
  private function validateStore():void {
    //the segmentId must be unquoted otherwise the store will not find even the valid id.
    var segmentId:String = segmentSelector.getUnquotedValue();
    if (segmentId) {
      // check if value is not in store
      if (segmentSelector.getStore().find(segmentSelector.valueField, segmentId) == -1) {
        // set segment name as rawValue to avoid checking out the document
        // mark as "invalid"
        segmentSelector.markInvalid(LivecontextStudioPlugin_properties.INSTANCE.p13n_context_commerce_segment_invalid);
      } else {
        segmentSelector.clearInvalid();
      }
    }
  }

  /* ------------------------------------------

   Condition interface

   ------------------------------------------ */

  public override function getPropertyName():String {
    return propertyPrefix + 'usersegments';
  }

  public override function setPropertyName(name:String):void {
    //
  }

  public override function getPropertyValue():String {
    return segmentSelector.getValue();
  }

  public override function setPropertyValue(v:String):void {
    if (v !== null) {
      segmentSelector.setValue(v);
    }
    else {
      segmentSelector.clearValue();
    }
  }

}
}