package com.coremedia.cms.studio.queryeditor.config {

import ext.config.container;

[ExtConfig(target="com.coremedia.cms.studio.queryeditor.conditions.ContentDropTarget", xtype)]
public class contentDropTarget extends container {

  public static native function get xtype():String;

  public function contentDropTarget(config:Object = null) {
    super(config || {});
  }

  public native function get attribute():String;

  public native function set attribute(value:String):void;

  public native function get contentType():String;

  public native function set contentType(value:String):void;
}
}
