package com.coremedia.blueprint.studio.upload {
import com.coremedia.blueprint.studio.UploadStudioPlugin_properties;

import ext.QuickTips;

public class UploadHelper {
  public static function isHTML5():Boolean {
    return window.File && window.FileReader && window.FileList && window.Blob;
  }

  public static function resolveTooltip():String {
    if (!isHTML5()) {
      return UploadStudioPlugin_properties.INSTANCE.UploadFileAction_tooltip_disabled;
    }
    return UploadStudioPlugin_properties.INSTANCE.UploadFileAction_tooltip;
  }

  public static function resolveMenuItemTooltip(thisMenuItem:*):void {
    if (!UploadHelper.isHTML5()) {
      QuickTips.register({target:thisMenuItem.getEl().getAttribute('id'), text:UploadHelper.resolveTooltip()});
    }
  }
}
}