package com.coremedia.blueprint.studio.upload.dialog {

import com.coremedia.blueprint.studio.config.upload.uploadProgressDialog;
import com.coremedia.blueprint.studio.config.upload.uploadProgressPanel;
import com.coremedia.blueprint.studio.upload.FileWrapper;
import com.coremedia.blueprint.studio.upload.UploadSettings;
import com.coremedia.cap.content.Content;
import com.coremedia.ui.util.EventUtil;

import ext.Ext;
import ext.Container;
import ext.Window;
import ext.config.menuseparator;

/**
 * Base class of the upload dialog, contains the current
 * items marked for uploading
 */
public class UploadProgressDialogBase extends Window {

  public static const NORTH_CONTAINER_HEIGHT:Number = 21;
  public static const SOUTH_CONTAINER_HEIGHT:Number = 21;

  private var files:Array;
  private var folder:Content;
  private var uploadPanels:Array = [];
  private var activeUploadIndex:int = 0;
  private var settings:UploadSettings;

  public function UploadProgressDialogBase(config:uploadProgressDialog) {
    super(config);
    this.settings = config.settings;
    this.files = config.files;
    this.folder = config.folder;
    addListener('afterlayout', addPanels);
  }

  /**
   * Add the file panels after render
   */
  private function addPanels():void {
    removeListener('afterlayout', addPanels);
    addUploadItems();
    EventUtil.invokeLater(function():void {
       startPanelUpload();
    });
  }

  /**
   * prevent miscalculating height with many items
   */
  public static function calculateWindowHeight(w:Window):void {
    var workArea:*,calcHeight:Number,windowHeight:Number,offsetY:Number;
    offsetY = 160;
    workArea = Ext.getCmp('workarea');
    calcHeight = workArea.getHeight();
    windowHeight = w.getHeight();
    if (windowHeight >= calcHeight) {
      w.setHeight(calcHeight - offsetY);
    }
  }

  /**
   * Creates the upload status panels and adds
   * then to the dialog.
   */
  private function addUploadItems():void {
    var uploadPanel:Container = find('itemId', 'upload-progress-list')[0];
    for (var i:int = 0; i < files.length; i++) {
      var progressPanel:UploadProgressPanel = new UploadProgressPanel(uploadProgressPanel({
        file:files[i],
        folder:folder,
        settings:settings,
        callback:startPanelUpload
      }));
      uploadPanel.add(progressPanel);
      uploadPanel.doLayout(false, true);
      uploadPanels.push(progressPanel);
      uploadPanel.add(new menuseparator({cls:'upload-progress-dialog-separator'}));
    }
    uploadPanel.doLayout(false, true);
  }

  /**
   * Starts the upload for the current panel.
   * Once the callback is called, the next upload is triggered
   * until the all uploads finished. The dialog is closed then afterwards.
   */
  public function startPanelUpload():void {
    if (uploadPanels.length > activeUploadIndex) {
      var uploadPanel:UploadProgressPanel = uploadPanels[activeUploadIndex];
      uploadPanel.startUpload();
      activeUploadIndex++;
    }
    else {
      //all files processed, check error state afterwards
      var close:Boolean = true;
      for (var i:int = 0; i < files.length; i++) {
        if(files[i].getStatus() === FileWrapper.STATUS_ERROR) {
          close = false;
          break;
        }
      }
      if(close) {
        this.close();
      }
    }
  }


  override public function close():void {
    var closeable:Boolean = true;
    for (var i:int = 0; i < files.length; i++) {
      var file:FileWrapper = files[i];
      if(file.getStatus() === FileWrapper.STATUS_WAITING || file.getStatus() === FileWrapper.STATUS_UPLOADING) {
        closeable = false;
        break;
      }
    }
    if(closeable) {
      super.close();
    }
  }

}
}
