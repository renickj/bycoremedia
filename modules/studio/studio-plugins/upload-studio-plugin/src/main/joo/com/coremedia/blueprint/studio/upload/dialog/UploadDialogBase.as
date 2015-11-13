package com.coremedia.blueprint.studio.upload.dialog {

import com.coremedia.blueprint.base.components.util.ContentCreationUtil;
import com.coremedia.blueprint.studio.UploadStudioPlugin_properties;
import com.coremedia.blueprint.studio.config.upload.fileContainer;
import com.coremedia.blueprint.studio.config.upload.uploadDialog;
import com.coremedia.blueprint.studio.config.upload.uploadProgressDialog;
import com.coremedia.blueprint.studio.upload.FileWrapper;
import com.coremedia.blueprint.studio.upload.UploadSettings;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.components.folderprompt.FolderCreationResultImpl;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.cms.editor.sdk.util.PathFormatter;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.util.EventUtil;

import ext.Container;
import ext.Ext;
import ext.MessageBox;
import ext.Window;
import ext.form.Checkbox;
import ext.form.ComboBox;
import ext.util.StringUtil;

/**
 * Base class of the upload dialog, contains the current
 * items marked for uploading
 */
public class UploadDialogBase extends Window {
  protected static const UPLOAD_AREA_HEIGHT:int = 318;
  protected static const UPLOAD_AREA_COLLAPSED_HEIGHT:int = 25;
  protected static const UPLOAD_WINDOW_HEIGHT:int = 453;
  protected static const UPLOAD_WINDOW_WIDTH:int = 480;

  private static const DROP_ZONE_COLLAPSED_CSS:String = 'dialog-upload-helptext-collapsed';

  private var fileContainers:FileContainersObservable;
  private var dropAreaCollapsed:Boolean = false;
  private var folder:Content;
  private var pathCombo:ComboBox;
  private var settings:UploadSettings;

  public function UploadDialogBase(config:uploadDialog) {
    super(config);
    this.settings = config.settings;
    this.folder = config.content;
  }

  /**
   * Some dialog initializations after setup...
   */
  override protected function afterRender():void {
    super.afterRender();

    //apply the default settings upload folder to the folder combo
    pathCombo = Ext.getCmp('upload-folder-combo') as ComboBox;

    if (folder) {
      folder.load(function ():void {
        var path:String = folder.getPath();
        pathCombo.setValue(path);
      });
    }
    else {
      var path:String = settings.getDefaultUploadPath();
      path = PathFormatter.formatSitePath(path);
      pathCombo.setValue(path);
    }
  }

  /**
   * Removes the given file container from the list of uploading files.
   * @param fileContainer
   */
  public function removeFileContainer(fileContainer:FileContainer):void {
    fileContainers.remove(fileContainer);
    this.doLayout(false, true);
    //expand drop zone again?
    if (fileContainers.isEmpty()) {
      toggleDropZoneStatus();
    }
  }

  /**
   * Fired when a file object has been dropped on the target drop area.
   * The file drop plugin fire an event for each file that is dropped
   * and the corresponding action is handled here.
   */
  protected function handleDrop(files:Array):void {
    MessageBox.show({
      title:UploadStudioPlugin_properties.INSTANCE.Upload_progress_title,
      msg:UploadStudioPlugin_properties.INSTANCE.Upload_progress_msg,
      closable:false,
      width:300
    });
    EventUtil.invokeLater(function ():void {//otherwise the progress bar does not appear :(
      for (var i:int = 0; i < files.length; i++) {
        var file:FileWrapper = files[i];
        var fc:fileContainer = new com.coremedia.blueprint.studio.config.upload.fileContainer({
          file:file,
          uploadSettings:settings});
        var fileContainer:FileContainer = new FileContainer(fc);
        fileContainers.add(fileContainer);
      }
      MessageBox.hide();
      refreshUploadList();
    });
  }

  /**
   * Returns the value expression that enables/disables the upload button.
   * the status of the buttons depends on if all file panels on this dialog are valid.
   * @return
   */
  protected function getUploadButtonDisabledExpression():ValueExpression {
    if (!fileContainers) {
      fileContainers = new FileContainersObservable();
      fileContainers.getValidityExpression().setValue(true);
    }
    return fileContainers.getValidityExpression();
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Stores the openInTab option into the settings bean
   * @param checkbox
   * @param checked
   */
  protected function openInTabHandler(checkbox:Checkbox, checked:Boolean):void {
    settings.set(UploadSettings.OPEN_IN_TAB_PROPERTY, checked);
  }

  /**
   * Rebuilds all panels representing a future upload.
   */
  private function refreshUploadList():void {
    //collapse the drop area if there are upload containers
    if (!fileContainers.isEmpty() && !dropAreaCollapsed) {
      toggleDropZoneStatus();
    }

    //clear and add list of upload containers
    var list:Container = Ext.getCmp('upload-list') as Container;
    for (var i:int = 0; i < fileContainers.size(); i++) {
      var fileContainer:FileContainer = fileContainers.getAt(i);
      list.add(fileContainer);
      doLayout(false, true);
    }

    doLayout(false, true);
  }

  /**
   * Expands or collapses the drop zone status.
   */
  private function toggleDropZoneStatus():void {
    var dropArea:Container = Ext.getCmp('upload-dropBox') as Container;
    if (!dropAreaCollapsed) {
      dropAreaCollapsed = true;
      dropArea.setHeight(UPLOAD_AREA_COLLAPSED_HEIGHT);
      Ext.getCmp('upload-drop-label').addClass(DROP_ZONE_COLLAPSED_CSS);
    }
    else {
      dropAreaCollapsed = false;
      dropArea.setHeight(UPLOAD_AREA_HEIGHT);
      Ext.getCmp('upload-drop-label').removeClass(DROP_ZONE_COLLAPSED_CSS);
    }
  }

  /**
   * Opens the progress upload and passes all the file wrapper and the upload dir to it.
   */
  protected function okPressed():void {

    var needsUpload:Boolean = false;
    var files:Array = fileContainers.getFiles();
    for (var i:int = 0; i < files.length; i++) {
      var fileWrapper:FileWrapper = files[i];
      if (!fileWrapper.isXliff()) {
        needsUpload = true;
      }
    }

    if (!needsUpload) {
      var progressDialog:UploadProgressDialog = new UploadProgressDialog(uploadProgressDialog({files: fileContainers.getFiles(),
        settings: settings,
        folder: null}));
      progressDialog.show();
      close();
    } else {
      var uploadDirectory:String = pathCombo.getValue();
      if (uploadDirectory) {
        session.getConnection().getContentRepository().getChild(uploadDirectory, function (folder:Content):void {
          if (folder) { //ensure loading to display path information
            folder.load(function ():void {
              var progressDialog:UploadProgressDialog = new UploadProgressDialog(uploadProgressDialog({files: fileContainers.getFiles(),
                settings: settings,
                folder: folder}));
              progressDialog.show();
              close();
            });
          }
          else {
            ContentCreationUtil.createRequiredSubfolders(uploadDirectory,
              function (result:FolderCreationResultImpl):void {
                if (result.success) {
                  okPressed();
                }
                else if (result.remoteError) {
                  var msg:String = StringUtil.format(UploadStudioPlugin_properties.INSTANCE.Upload_folder_error,
                          uploadDirectory,
                          result.remoteError.errorName);
                  MessageBoxUtil.showError(UploadStudioPlugin_properties.INSTANCE.Upload_error, msg);
                }
              }, true);
          }
        });
      }
    }
  }

}
}
