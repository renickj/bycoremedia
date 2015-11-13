package com.coremedia.cms.studio.queryeditor.config {

import ext.config.container;

[ExtConfig(target="com.coremedia.cms.studio.queryeditor.components.ConditionDroppableBase", xtype)]
public class conditionDroppableBase extends container {

  public static native function get xtype():String;

  public function conditionDroppableBase(config:Object = null) {
    super(config || {});
  }

  public native function get attribute():String;

  public native function set attribute(value:String):void;

  public native function get conditionEditorXtype():String;

  public native function set conditionEditorXtype(value:String):void;
}
}
