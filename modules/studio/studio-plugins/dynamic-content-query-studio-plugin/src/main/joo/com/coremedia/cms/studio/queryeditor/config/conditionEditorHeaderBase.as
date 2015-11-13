package com.coremedia.cms.studio.queryeditor.config {

import ext.config.container;

[ExtConfig(target="com.coremedia.cms.studio.queryeditor.conditions.ConditionEditorHeaderBase", xtype)]
public class conditionEditorHeaderBase extends container {

  public static native function get xtype():String;

  public function conditionEditorHeaderBase(config:Object = null) {
    super(config || {});
  }

  public native function get attribute():String;

  public native function set attribute(value:String):void;
}
}
