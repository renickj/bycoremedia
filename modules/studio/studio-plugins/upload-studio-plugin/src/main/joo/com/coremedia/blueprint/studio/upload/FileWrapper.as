package com.coremedia.blueprint.studio.upload {

import com.coremedia.blueprint.base.components.util.StringHelper;
import com.coremedia.blueprint.studio.UploadStudioPlugin_properties;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentErrorCodes;
import com.coremedia.cap.content.results.BulkOperationResult;
import com.coremedia.cms.editor.sdk.components.html5.FileSelector;
import com.coremedia.cms.editor.sdk.components.html5.Uploader;
import com.coremedia.cms.editor.sdk.config.uploader;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.error.RemoteError;
import com.coremedia.ui.data.impl.BeanImpl;
import com.coremedia.ui.data.impl.RemoteService;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponseImpl;

import ext.Element;
import ext.Ext;

import js.XMLHttpRequest;

/**
 * Access wrapper for a HTML 5 file object.
 */
public class FileWrapper extends BeanImpl {
  private static const DEFAULT_UPLOAD_SIZE:int = 67108864;

  public static const FILE_PROPERTY:String = 'file';
  public static const NAME_PROPERTY:String = 'name';
  public static const MIME_TYPE_PROPERTY:String = 'mimeType';
  public static const FILE_TYPE_PROPERTY:String = "extensionType";
  public static const SIZE_PROPERTY:String = 'size';
  public static const UPLOAD_FOLDER_PROPERTY:String = 'uploadFolder';

  private static const XLIFF_MIME_TYPE:String = "application/x-xliff";
  private var XLIFF_MIME_TYPE_EXT:String = XLIFF_MIME_TYPE + '+xml';
  private var MIME_TYPE_TEXT_CLASS:String = "no-mime-type";

  public static const STATUS_ERROR:int = -1;
  public static const STATUS_WAITING:int = 0;
  public static const STATUS_UPLOADING:int = 1;
  public static const STATUS_UPLOADED:int = 2;

  //The HTML file object
  private var file:*;

  private var status:int = STATUS_WAITING;

  public function FileWrapper(file:*) {
    this.file = file;

    var fileName:String = file.name;
    var name:String = fileName;

    if (name.indexOf(".") != -1) {
      name = StringHelper.trim(name.substring(0, name.lastIndexOf(".")), ' ');
    }

    set(NAME_PROPERTY, name);
    set(FILE_PROPERTY, file);

    var mimeType:String = file.type;
    if (!mimeType) {
      mimeType = 'text/plain';
    }
    if (fileName.length > 6 && fileName.substr(fileName.length - 6).toLowerCase() === ".xliff") {
      mimeType = XLIFF_MIME_TYPE;
    }

    var extensionType:String = "";
    if (fileName.indexOf(".") !== -1) {
      extensionType = fileName.substr(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    set(FILE_TYPE_PROPERTY, extensionType);
    set(MIME_TYPE_PROPERTY, mimeType);
    set(SIZE_PROPERTY, file.size);
  }

  public function getName():String {
    return get(NAME_PROPERTY);
  }

  public function getSize():int {
    return get(SIZE_PROPERTY);
  }

  public function getMimeType():String {
    return get(MIME_TYPE_PROPERTY);
  }

  public function getFile():* {
    return get(FILE_PROPERTY);
  }

  public function isImage():Boolean {
    return getMimeType().indexOf("image") !== -1;
  }

  public function isXliff():Boolean {
    return getMimeType() === XLIFF_MIME_TYPE || getMimeType() === XLIFF_MIME_TYPE_EXT;
  }

  public function getStatus():int {
    return status;
  }

  public function setStatus(value:int):void {
    status = value;
  }

  public function uploadXliff(settings:UploadSettings, success:Function, error:Function):void {
    var fileSelector:FileSelector = {};
    fileSelector.getInputFile = function ():* {
    };
    fileSelector.detachInputFile = function ():* {
    };
    fileSelector.getFileCls = function ():String {
      return settings.getDefaultContentType();
    };
    fileSelector.getFileName = function ():String {
      return "";
    };

    var url:String = RemoteService.calculateRequestURI('translate/importXliff');
    var upldr:Uploader = new Uploader(uploader({
      fileSelector: fileSelector,
      timeout: settings.getTimeout(),
      url: url,
      method: 'POST'
    }));

    upldr.addListener('uploadcomplete', function (_uploader:Uploader, response:XMLHttpRequest):void {
      var remoteServiceMethodResponse:RemoteServiceMethodResponse = new RemoteServiceMethodResponseImpl(url, true, response, {});
      var bulkOperationResult:BulkOperationResult = new XliffBulkOperationResultBuilder().convert(remoteServiceMethodResponse);
      success(bulkOperationResult);
    });

    upldr.addListener('uploadfailure', function (_uploader:Uploader, response:XMLHttpRequest):void {
      error(response);
    });

    upldr.addListener('uploadprogress', function (_uploader:Uploader, e:*):void {
      var percent:Number = Math.round(e.loaded / e.total * 100);
      trace('[DEBUG]', ' Upload progress: ' + percent);
    });

    upldr.upload(file);
  }

  /**
   * Starts the actual data transfer. For the file of this file wrapper a Studio Uploader instance is
   * created with the corresponding event listeners. The given result callbacks are call with necessary parameters
   * to show the result of the upload.
   * @param settings The settings that define the max upload size ...
   * @param folder The folder under which to upload the file.
   * @param success The success handler called after a successful upload.
   * @param error The error handler called if the upload failed.
   */
  public function upload(settings:UploadSettings, folder:Content, success:Function, error:Function):void {
    var fileSelector:FileSelector = {};
    fileSelector.getInputFile = function ():* {
    };
    fileSelector.detachInputFile = function ():* {
    };
    fileSelector.getFileCls = function ():String {
      return settings.getDefaultContentType();
    };
    fileSelector.getFileName = function ():String {
      return "";
    };

    var headerParams:Object;

    if (editorContext.getSitesService().getPreferredSite()) {
      headerParams = {
        site: editorContext.getSitesService().getPreferredSiteId(),
        folderUri: folder.getUriPath()
      };
    } else {
      headerParams = {
        folderUri: folder.getUriPath()
      };
    }

    var upldr:Uploader = new Uploader(uploader({
      maxFileSize: DEFAULT_UPLOAD_SIZE,
      fileSelector: fileSelector,
      timeout: settings.getTimeout(),
      url: 'api/upload/create',
      method: 'POST',
      headerParams: headerParams,
      params: {contentName: getName()}
    }));

    upldr.addListener('uploadcomplete', function (_uploader:Uploader, response:XMLHttpRequest):void {
      //Hack for html4 upload.
      if (response.status === 201) {
        success.call(null, response);
      }
      else {
        error.call(null, response.responseText + ' (code ' + response.status + ')');
      }
    });

    upldr.addListener('uploadfailure', function (_uploader:Uploader, response:XMLHttpRequest):void {
      var errorCodeName:String;
      try {
        var remoteError:RemoteError = RemoteService.createRemoteError(response);
        errorCodeName = remoteError.errorName;
      } catch (e:*) {
        if (response.responseText.indexOf(ContentErrorCodes.BLOB_MIME_CONTENT_TYPE_MISMATCH) != -1) {
          errorCodeName = ContentErrorCodes.BLOB_MIME_CONTENT_TYPE_MISMATCH;
        }
      }
      error.call(null, errorCodeName);
    });

    upldr.addListener('uploadprogress', function (_uploader:Uploader, e:*):void {
      var percent:Number = Math.round(e.loaded / e.total * 100);
      trace('[DEBUG]', ' Upload progress: ' + percent);
    });

    upldr.upload(file);
  }

  /**
   * Appends an image tag that contains the preview image.
   * @param preview The element to add the image element for.
   * @param width The width of the previewed image.
   * @param height The height of the previewed image.
   * @param callback The callback to call after the image preview has been set up.
   */
  public function appendPreviewElement(preview:Element, width:int, height:int, callback:Function):void {
    var document:* = preview.dom.ownerDocument;
    var img:* = document.createElement("img");
    var imageThumbWrapper:* = document.createElement("div");
    imageThumbWrapper.appendChild(img);
    imageThumbWrapper.setAttribute("class", "upload-thumbnail");
    var htmlFragment:String = "<div class=\"" + MIME_TYPE_TEXT_CLASS + "\"><span>" + UploadStudioPlugin_properties.INSTANCE.Upload_mimetype_text + "</span></div>";

    img.classList.add("preview-container");
    img.file = getFile();
    preview.appendChild(imageThumbWrapper);
    preview.removeClass("no-preview");
    preview.addClass("preview");

    var reader:* = new window.FileReader();
    reader.onload = (function (aImg:*):Function {
      return function (e:*):void {
        if (e.target && e.target.result && e.target.result.indexOf('data:image') != -1) {
          try {
            aImg.src = e.target.result;
            callback.call(null);
            return;
          }
          catch (error:*) {
            trace('Failed to create preview: ' + error);
          }
        }

        aImg.src = Ext.BLANK_IMAGE_URL;
        preview.removeClass("preview");
        preview.addClass("no-preview");

        var noMimeType:* = document.createElement("div");
        noMimeType.setAttribute("class", MIME_TYPE_TEXT_CLASS);
        var noMimeTypeSpan:* = document.createElement("span");
        noMimeTypeSpan.innerHTML = UploadStudioPlugin_properties.INSTANCE.Upload_mimetype_text;
        noMimeType.appendChild(noMimeTypeSpan);
        imageThumbWrapper.appendChild(noMimeType);
        callback.call(null);
      };
    })(img);
    reader.readAsDataURL(getFile());
  }
}
}
