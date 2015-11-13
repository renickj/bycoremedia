package com.coremedia.cms.studio.queryeditor.config {

import ext.config.container;

[ExtConfig(target="com.coremedia.cms.studio.queryeditor.components.QueryConditionsFieldBase", xtype)]
public class queryConditionsFieldBase extends container {

  public static native function get xtype():String;

  public function queryConditionsFieldBase(config:Object = null) {
    super(config || {});
  }

  public native function get attribute():String;

  public native function set attribute(value:String):void;
}
}
