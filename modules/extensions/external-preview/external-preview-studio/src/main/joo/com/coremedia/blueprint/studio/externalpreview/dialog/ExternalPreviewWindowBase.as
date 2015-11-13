package com.coremedia.blueprint.studio.externalpreview.dialog {

import com.coremedia.blueprint.studio.externalpreview.ExternalPreviewStudioPluginBase;
import com.coremedia.blueprint.studio.externalpreview.ExternalPreviewStudioPlugin_properties;
import com.coremedia.blueprint.studio.externalpreview.config.externalPreviewWindow;

import ext.Ext;
import ext.Window;
import ext.util.StringUtil;

/**
 * Base class for the external preview help dialog.
 */
public class ExternalPreviewWindowBase extends Window {
  internal static const WINDOW_FEATURES:String = "menubar=yes,resizable=yes,scrollbars=yes,status=yes,location=yes";

  public function ExternalPreviewWindowBase(config:externalPreviewWindow) {
    super(config);
  }

  /**
   * Creates a mail with the preview URL link.
   */
  protected function mailPreviewLink():void {
    //create the subject
    var subject:String = ExternalPreviewStudioPlugin_properties.INSTANCE.ExternalPreview_mail_subject;
    var body:String = StringUtil.format(ExternalPreviewStudioPlugin_properties.INSTANCE.ExternalPreview_mail_body,
            ExternalPreviewStudioPluginBase.PREVIEW_URL);

    //encode whole url
    var params:String = Ext.urlEncode({subject:subject, body:body});
    var url:String = Ext.urlAppend("mailto:", params);

    //regular javascript mail to link.
    window.open(url, 'emailWindow');
  }

  /**
   * Formats the given string to format/cut the link url.
   * @param text
   * @return
   */
  protected function formatText(text:String):String {
    if(text.length > 60) {
      text = text.substring(0,59) + "...";
    }
    return text;
  }

  /**
   * Opens the current URL new browser window.
   */
  public static function openInBrowser():Function {
    return function():void {
        window.open(ExternalPreviewStudioPluginBase.PREVIEW_URL, 'External Preview', WINDOW_FEATURES);
    }
  }

  protected function closeDialog():void {
    hide();
  }

  override public function close():void {
    hide();
  }
}
}