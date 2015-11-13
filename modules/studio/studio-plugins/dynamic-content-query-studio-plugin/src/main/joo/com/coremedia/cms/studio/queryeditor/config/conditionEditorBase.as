package com.coremedia.cms.studio.queryeditor.config {

import com.coremedia.ui.data.ValueExpression;

import ext.config.container;

[ExtConfig(target="com.coremedia.cms.studio.queryeditor.conditions.ConditionEditorBase", xtype)]
public class conditionEditorBase extends container {

  public static native function get xtype():String;

  public function conditionEditorBase(config:Object = null) {
    super(config || {});
  }

  public native function get attribute():String;

  public native function set attribute(value:String):void;

  public native function get bindTo():ValueExpression;

  public native function set bindTo(value:ValueExpression):void;

  public native function get propertyName():String;

  public native function set propertyName(value:String):void;

  public native function get group():String;

  public native function set group(value:String):void;

  public native function get forceReadOnlyValueExpression():ValueExpression;

  public native function set forceReadOnlyValueExpression(value:ValueExpression):void;

  public native function get documentTypes():Array;

  public native function set documentTypes(value:Array):void;

  public native function get sortable():Boolean;

  public native function set sortable(value:Boolean):void;

  /**
   * If set to true, condition editor to be rendered should set the condition value to default
   */
  public native function get setToDefault():Boolean;

  public native function set setToDefault(value:Boolean):void;

}
}
