package com.coremedia.blueprint.studio.externalpreview.config {

import com.coremedia.cms.editor.sdk.config.contentAction;
import com.coremedia.ui.data.ValueExpression;
[ExtConfig(target="com.coremedia.blueprint.studio.externalpreview.ExternalPreviewAction")]
public dynamic class externalPreviewAction extends contentAction {

  public function externalPreviewAction(config:Object = null) {
    super(config || {});
  }
}
}