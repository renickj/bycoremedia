package com.coremedia.cms.studio.queryeditor.config {

import ext.Ext;
import ext.config.checkboxgroup;

[ExtConfig(target="com.coremedia.cms.studio.queryeditor.components.ResizableCheckboxGroup", xtype)]
public class resizableCheckboxGroup extends checkboxgroup {

  public static native function get xtype():String;

  public function resizableCheckboxGroup(config:Object = null) {
    super(config || {});
  }

  public native function get attribute():String;

  public native function set attribute(value:String):void;

  public native function get documentTypesFilterHeight():Number;

  public native function set documentTypesFilterHeight(value:Number):void;
}
}
