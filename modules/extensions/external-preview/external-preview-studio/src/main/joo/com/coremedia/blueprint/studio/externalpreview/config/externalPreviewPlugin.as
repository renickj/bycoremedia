package com.coremedia.blueprint.studio.externalpreview.config {

import joo.JavaScriptObject;

[ExtConfig(target="com.coremedia.blueprint.studio.externalpreview.ExternalPreviewPlugin", ptype)]
public dynamic class externalPreviewPlugin extends JavaScriptObject {

  public static native function get ptype():String;

  public function externalPreviewPlugin(config:Object = null) {
    super(config || {});
  }

}
}