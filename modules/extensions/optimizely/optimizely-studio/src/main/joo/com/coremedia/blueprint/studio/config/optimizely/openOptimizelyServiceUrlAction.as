package com.coremedia.blueprint.studio.config.optimizely {

import ext.config.action;

[ExtConfig(target="com.coremedia.blueprint.studio.OpenOptimizelyServiceUrlAction")]
public class openOptimizelyServiceUrlAction extends action {

  public function openOptimizelyServiceUrlAction(config:Object = null) {
    super(config || {});
  }
}
}
