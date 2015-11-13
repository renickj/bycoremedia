package com.coremedia.blueprint.studio.upload.dialog {
import com.coremedia.blueprint.studio.config.upload.fileContainer;
import com.coremedia.blueprint.studio.upload.FileWrapper;

import ext.Container;
import ext.Ext;
import ext.form.Field;

/**
 * The file container wraps the preview information for each
 * upload, including the name and mime type of the uploading document.
 */
public class FileContainerBase extends Container {
  protected static const PREVIEW_IMG_WIDTH:Number = 100;
  protected static const PREVIEW_IMG_HEIGHT:Number = 75;

  private var fileWrapper:FileWrapper;

  public function FileContainerBase(config:fileContainer) {
    super(config);
    this.fileWrapper = config.file;
    addListener('afterlayout', layoutPanel);
  }

  /**
   * Returns the file wrapper used for this panel.
   * Getter is used for the global validation.
   * @return
   */
  public function getFile():FileWrapper {
    return fileWrapper;
  }

  /**
   * Removes this container from the upload dialog.
   */
  protected function removeContainer():void {
    var uploadDialog:UploadDialog = Ext.getCmp('fileUploadDialog') as UploadDialog;
    uploadDialog.removeFileContainer(this as FileContainer);
  }

  /**
   * Layout preview after render
   */
  private function layoutPanel():void {
    removeListener('afterlayout', layoutPanel);
    var previewContainer:Container = find('itemId', 'preview')[0];
    setDisabled(true);
    fileWrapper.appendPreviewElement(previewContainer.getEl(), PREVIEW_IMG_WIDTH, PREVIEW_IMG_HEIGHT, function():void {
      if(isVisible()) {
        setDisabled(false);
      }
    });
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Ensures that the file name is set to null so that the validation applies.
   * @param field The name text field.
   * @param e
   */
  protected function filenameChanged(field:Field, e:*):void {
    if(!field.getValue() || field.getValue().length === 0) {
      fileWrapper.set(FileWrapper.NAME_PROPERTY, null);
    } else {
      fileWrapper.set(FileWrapper.NAME_PROPERTY, field.getValue);
    }
  }
}
}
