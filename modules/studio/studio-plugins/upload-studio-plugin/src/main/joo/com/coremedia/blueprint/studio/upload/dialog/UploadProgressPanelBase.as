package com.coremedia.blueprint.studio.upload.dialog {

import com.coremedia.blueprint.studio.UploadStudioPlugin_properties;
import com.coremedia.blueprint.studio.config.upload.uploadProgressPanel;
import com.coremedia.blueprint.studio.config.upload.xliffImportResultWindow;
import com.coremedia.blueprint.studio.upload.FileWrapper;
import com.coremedia.blueprint.studio.upload.UploadSettings;
import com.coremedia.blueprint.studio.upload.XliffBulkOperationResult;
import com.coremedia.blueprint.studio.upload.XliffBulkOperationResultItem;
import com.coremedia.blueprint.studio.upload.XliffImportResultCodes;
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.impl.BeanFactoryImpl;
import com.coremedia.ui.util.EventUtil;

import ext.Component;
import ext.Container;
import ext.QuickTips;
import ext.config.quicktip;
import ext.form.Label;
import ext.util.JSON;
import ext.util.StringUtil;

/**
 * Base class for the upload progress panel, implements
 * the actual upload and updates the status of the panel.
 */
public class UploadProgressPanelBase extends Container {
  private var file:FileWrapper;
  private var folder:Content;
  private var callback:Function;
  private var settings:UploadSettings;

  public function UploadProgressPanelBase(config:uploadProgressPanel) {
    super(config);
    this.settings = config.settings;
    this.file = config.file;
    this.folder = config.folder;
    this.callback = config.callback;
  }


  override protected function afterRender():void {
    super.afterRender();
    getUploadErrorIcon().setVisible(false);
  }

  /**
   * Triggers the upload with the given data.
   */
  public function startUpload():void {
    //update the visible status
    setRunning(true);

    //start data transfer
    if (file.isXliff()) {
      file.uploadXliff(settings, xliffUploaded, uploadError);
    } else {
      file.upload(settings, folder, fileUploadedAndContentCreated, uploadError);
    }

    //update ui
    setRunning(false);
  }

  private function xliffUploaded(xliffBulkOperationResult:XliffBulkOperationResult):void {
    var translatedContents:Array = [];
    var results:Array = xliffBulkOperationResult.results;
    var hasError:Boolean = false;

    for (var i:int = 0; i < results.length; i++) {
      var resultItem:XliffBulkOperationResultItem = results[i];
      if (resultItem.resultCode === XliffImportResultCodes.SUCCESS) {
        translatedContents.push(resultItem.content);
      } else {
        hasError = true;
      }
    }

    if (settings.getOpenInTab()) {
      editorContext.getContentTabManager().openDocuments(translatedContents);
    }

    if (hasError) {
      new XliffImportResultWindow(new xliffImportResultWindow({bulkResultItems: results})).show();
    }

    getUploadStatusText().setText(UploadStudioPlugin_properties.INSTANCE.UploadProgressDialog_upload_successful);
    file.setStatus(FileWrapper.STATUS_UPLOADED);
    callback.call(null);
  }

  /**
   * Success handler executed after the file has been uploaded.
   * @param response the response from the server.
   */
  private function fileUploadedAndContentCreated(response:Object):void {
    var content:Content = BeanFactoryImpl.resolveBeans(JSON.decode(response.responseText)) as Content;
    content.load(function (postProcessedContent:Content):void {
      var initializer:Function = editorContext.lookupContentInitializer(postProcessedContent.getType());
      if (initializer) {
        initializer(postProcessedContent);
      }

      if (settings.getCheckIn()) {
        doCheckin(postProcessedContent);
      }

      if (settings.getOpenInTab()) {
        StudioUtil.openInTab(postProcessedContent);
      }
      else {
        if (postProcessedContent.isCheckedOutByCurrentSession()) {
          postProcessedContent.checkIn(function ():void {
          });
        }
      }
      getUploadStatusText().setText(UploadStudioPlugin_properties.INSTANCE.UploadProgressDialog_upload_successful);
      file.setStatus(FileWrapper.STATUS_UPLOADED);
      callback.call(null);
    });
  }

  private function doCheckin(content:Content):void {
    content.invalidate(function ():void {
      if (content.isCheckedOutByCurrentSession()) {
        content.checkIn(null);
      }
    });
  }

  /**
   * Invoked when the import document created failed or when the upload
   * itself failed.
   * @param result The remote service method result.
   */
  private function uploadError(result:String):void {
    file.setStatus(FileWrapper.STATUS_ERROR);
    setRunning(false);
    getUploadErrorIcon().setVisible(true);
    EventUtil.invokeLater(function ():void {
      QuickTips.register(quicktip({
        target:getUploadErrorIcon().getId(),
        id:'uploadErrorQT',
        text:StringUtil.format(UploadStudioPlugin_properties.INSTANCE.Upload_error_tooltip, result),
        trackMouse:false,
        autoHide:true,
        dismissDelay:3000
      }));
      getUploadStatusText().setText(UploadStudioPlugin_properties.INSTANCE.UploadProgressDialog_upload_failed);

      callback.call(null);
    });
  }

  /**
   * Updates the status of the loading  text and the busy indicator.
   * @param running
   */
  private function setRunning(running:Boolean):void {
    EventUtil.invokeLater(function ():void {
      if (running && file.getStatus() != FileWrapper.STATUS_ERROR) {
        file.setStatus(FileWrapper.STATUS_UPLOADING);
        getUploadStatusIcon().setVisible(true);
        getUploadStatusText().setText(UploadStudioPlugin_properties.INSTANCE.UploadProgressDialog_upload_running);
      }
      else {
        getUploadStatusIcon().setVisible(false);
      }
      doLayout(false, true);
    });
  }

  /**
   * Returns the button with the busy indicator.
   * @return
   */
  private function getUploadStatusIcon():Component {
    return find('itemId', 'upload-in-progress-icon')[0];
  }

  /**
   * Returns the button with the error icon.
   * @return
   */
  private function getUploadErrorIcon():Component {
    return find('itemId', 'upload-error-icon')[0];
  }

  /**
   * Returns the label that contains the current upload status
   * of the file.
   * @return
   */
  private function getUploadStatusText():Label {
    return find('itemId', 'progress-status-text')[0] as Label;
  }
}
}
