package com.coremedia.blueprint.personalization.editorplugin.config {

import joo.JavaScriptObject;

/**
 *  A Condition specialized for editing <i>taxonomy conditions</i>. A taxonomy condition consists of a linked taxonomy
 * (as Keyword), a comparison operator, and a value field (percentage).
 *
 * @see com.coremedia.blueprint.studio.taxonomy.condition.AbstractTaxonomyCondition
 */
[ExtConfig(target="com.coremedia.blueprint.personalization.editorplugin.taxonomy.AbstractTaxonomyCondition", xtype)]
public dynamic class abstractTaxonomyCondition extends JavaScriptObject {

  public static native function get xtype():String;

  public function abstractTaxonomyCondition(config:Object = null) {
    super(config || {});
  }

  /**
   * prefix of context properties mapped to this condition instance. The characters following the prefix in a property name are assumed to represent the keyword
   */
  public native function get propertyPrefix():String;

  public native function set propertyPrefix(value:String):void;

  /**
   * the text to be shown in the keyword field if it is empty. Defaults to <i>keyword</i>
   */
  public native function get keywordEmptyText():String;

  public native function set keywordEmptyText(value:String):void;

  public native function get conditionName():String;

  public native function set conditionName(value:String):void;

  /**
   * the text to place into the keyword field. Defaults to <i>null</i>
   */
  public native function get keywordText():String;

  public native function set keywordText(value:String):void;

  /**
   * the text to be shown if no operator is selected. Default to <i>operator</i>
   */
  public native function get operatorEmptyText():String;

  public native function set operatorEmptyText(value:String):void;

  /**
   * user-presentable names of the operators. See below
   */
  public native function get operatorNames():Object;

  public native function set operatorNames(value:Object):void;

  /**
   * the operator to select initially. See below
   */
  public native function get operator():String;

  public native function set operator(value:String):void;

  /**
   * the text to be shown in the value field if it is empty. Defaults to <i>value</i>
   */
  public native function get valueEmptyText():String;

  public native function set valueEmptyText(value:String):void;

  /**
   * the text to place into the value field
   */
  public native function get valueText():String;

  public native function set valueText(value:String):void;

  /**
   * the validation type of the value field. See below
   */
  public native function get valueVType():String;

  public native function set valueVType(value:String):void;

  /**
   * the text to be shown after the value field. Defaults to <i>null</i> The property prefix is used to transform keyword properties to and from a user-presentable form. In a typical scenario, keyword properties in a profile will use a common prefix to identify them as keywords, e.g. 'keyword'. This prefix shouldn't be shown to users of the UI. If the propertyPrefix property is set to the internally used prefix, this condition component will remove the prefix (including the '.' separator) from the keyword property before it is displayed, and add it to the value in the keyword field when it is read via getPropertyName. The default validation types are: <ul><li>For the keyword field: <code>/^[a-zA-Z_][a-zA-Z_0-9\.]*$/</code>.</li><li>For the value field: <code>/^\d+(\.\d+)?$/</code>.</li></ul>The <b>operators</b> offered by this component are: <ul><li>'lt'</li><li style="list-style: none">less than</li><li>'le'</li><li style="list-style: none">less than or equals</li><li>'eq'</li><li style="list-style: none">equals</li><li>'ge'</li><li style="list-style: none">greater than or equals</li><li>'gt'</li><li style="list-style: none">greater than</li></ul>The names used for the available operators can be overridden by a dictionary supplied via the <b>operatorNames</b> property. The available operators and their default names are: <ul><li>'lt': 'less'</li><li>'le': 'less or equal'</li><li>'eq': 'equal'</li><li>'ge': 'greater or equal'</li><li>'gt': 'greater'</li></ul>You may override an arbitrary subset of these values.
   */
  public native function get suffixText():String;

  public native function set suffixText(value:String):void;
}
}