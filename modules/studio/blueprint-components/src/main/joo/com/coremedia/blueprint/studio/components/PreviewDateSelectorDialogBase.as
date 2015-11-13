package com.coremedia.blueprint.studio.components {

import com.coremedia.blueprint.studio.config.components.previewDateSelectorDialog;
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.blueprint.studio.util.ToggleButtonUtil;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.ui.data.Calendar;
import com.coremedia.ui.data.ValueExpression;

import ext.Button;
import ext.Window;

/**
 * Base class of the preview date selection menu.
 */
public class PreviewDateSelectorDialogBase extends Window {
  private var previewPanel:PreviewPanel;
  private var dateValueExpression:ValueExpression;

  public function PreviewDateSelectorDialogBase(config:previewDateSelectorDialog) {
    super(config);
    previewPanel = config.previewPanel;
    dateValueExpression = config.dateValueExpression;
  }

  protected function okPressed():void {
    var button:PreviewDateSelectorButton = previewPanel.getTopToolbar().find('itemId', 'previewDateSelectorButton')[0];
    ToggleButtonUtil.setPressed(button);
    if(!dateValueExpression.getValue()) {
      ToggleButtonUtil.setUnpressed(button);
    }
    dateTimeUiChange();
    StudioUtil.reloadPreview();
    hide();
  }

  /**
   * Update the visual status once the date time has been changed.
   */
  private function dateTimeUiChange():void {
    var value:Calendar = dateValueExpression.getValue();
    var button:Button = previewPanel.getTopToolbar().find('itemId', 'previewDateSelectorButton')[0];
    if(value) {
      ToggleButtonUtil.setPressed(button);
    }
    else {
      ToggleButtonUtil.setUnpressed(button);
    }
  }

  /**
   * Do not close, just hide the dialog.
   */
  override public function close():void {
    hide();
  }
}
}
