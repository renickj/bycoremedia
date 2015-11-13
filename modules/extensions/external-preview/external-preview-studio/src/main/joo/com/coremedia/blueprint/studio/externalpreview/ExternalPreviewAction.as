package com.coremedia.blueprint.studio.externalpreview {
import com.coremedia.blueprint.studio.externalpreview.config.externalPreviewAction;
import com.coremedia.blueprint.studio.externalpreview.dialog.ExternalPreviewWindow;

import ext.Action;
import ext.Ext;

/**
 * Action for creating a bookmark using the active document.
 */
public class ExternalPreviewAction extends Action {

  /**
   * @param config
   */
  public function ExternalPreviewAction(config:externalPreviewAction) {
    config['handler'] = showExternalPreviewDialog;
    super(config);
  }
  /**
   * The action handler for this action, checks single and multi-selection.
   */
  private function showExternalPreviewDialog():void {
    ExternalPreviewPlugin.registerListeners();
    ExternalPreviewPlugin.fireExternalPreviewUpdate();
    var window:ExternalPreviewWindow = Ext.getCmp('externalPreviewDialog') as ExternalPreviewWindow;
    if (!window) {
      window = new ExternalPreviewWindow();
    }
    window.show();
  }
}
}