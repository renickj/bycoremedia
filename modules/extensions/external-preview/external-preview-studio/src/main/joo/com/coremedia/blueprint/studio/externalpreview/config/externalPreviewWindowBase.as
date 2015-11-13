package com.coremedia.blueprint.studio.externalpreview.config {
import ext.config.window;

[ExtConfig(target="com.coremedia.blueprint.studio.externalpreview.dialog.ExternalPreviewWindowBase", xtype)]
public dynamic class externalPreviewWindowBase extends ext.config.window {

  public static native function get xtype():String;

  public function externalPreviewWindowBase(config:Object = null) {
    super(config || {});
  }
}
}