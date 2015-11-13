package com.coremedia.blueprint.personalization.editorplugin {
import com.coremedia.blueprint.personalization.editorplugin.config.searchValidator;
import com.coremedia.cms.editor.sdk.config.premular;
import com.coremedia.cms.editor.sdk.config.previewIFrame;
import com.coremedia.cms.editor.sdk.config.previewPanel;
import com.coremedia.cms.editor.sdk.messageService;
import com.coremedia.cms.editor.sdk.preview.PreviewMessageTypes;
import com.coremedia.ui.data.PropertyChangeEvent;

import ext.Component;
import ext.Container;
import ext.Plugin;
import ext.form.Field;
import ext.util.StringUtil;

import js.Window;

/**
 * The field that uses this plugin retrieves the status of the search from the preview panel and adapts
 * its validation state and tooltip accordingly, thus providing better error feedback to the Studio user.
 */
public class SearchValidator implements Plugin {

  // name of the data attribute used in the preview page to store the search message
  private static const P13N_SEARCHSTATUS_DATA_ATTRIBUTE:String = "cm-personalization-editorplugin-searchstatus";
  private var prevPanel:Container;
  private var field:Field;

  public function SearchValidator(config:searchValidator) {
  }

  public function init(component:Component):void {
    field = component as Field;
    field.addListener("afterrender", onAfterrender);
  }

  /**
   * Performed on the afterrender event. Does stuff that requires a rendered component.
   */
  public function onAfterrender():void {
    this.prevPanel = findPreviewPanel();
    this.prevPanel.addListener('previewUrl', onPreviewUrlChange);
  }

  /**
   * Find the preview panel in the premular this field is placed in. Make sure this is only
   * called on a rendered component.
   *
   * @return the preview panel
   *
   * @throws Error if the preview panel cannot be found
   */
  private function findPreviewPanel():Container {
    if (!field.rendered) {
      throw new Error("findPreviewPanel must only be called on a rendered component");
    }

    const prem:Container = field.findParentByType(premular.xtype) as Container;
    if (prem) {
      const preview:Container = prem.findByType(previewPanel.xtype)[0] as Container;
      if (preview) {
        return preview;
      }
    }
    // didn't found the preview
    throw new Error("unable to locate Preview Panel. Has this component been rendered already?");
  }

  /**
   * Called when the contents of the preview change. Retrieves the search status object from the preview and
   * adapts the state of this field if necessary.
   *
   * @param event the 'previewUrl changed' event
   */
  public function onPreviewUrlChange(event:PropertyChangeEvent):void {
    if (event.newValue) {
      // retrieve the search message from the preview
      var targetWindow:Window = this.prevPanel.findByType(previewIFrame)[0].getContentWindow();
      var messageBody:Object = {dataAttributeName: P13N_SEARCHSTATUS_DATA_ATTRIBUTE};
      messageService.sendMessage(targetWindow, PreviewMessageTypes.RETRIEVE_DATA_ATTRIBUTE, messageBody, function(responseBody:Object):void {
        var searchStatus:Array = responseBody.value as Array;

        if (searchStatus && searchStatus.length > 0) {
          field['validator'] = function (value:*):* {
            return toTooltip(searchStatus[0]);
          };
          field.validate();
        }
        else {
          field['validator'] = function (value:*):* {
            return true;
          };
          field.validate();
        }
      });
    }
  }

  /**
   * Converts the supplied status object into a string to be shown in a tooltip.
   *
   * @param status the object representing the search status
   *
   * @return tooltip representing the search status
   */
  private function toTooltip(status:*):String {
    const code:String = status['code'];
    var msg:String = PersonalizationPlugIn_properties.INSTANCE[code];
    if (!msg) {
      msg = status['msg'];
    }
    switch (code) {
      case "ARGUMENT_VALUE":
      case "ARGUMENT_UNKNOWN":
      case "ARGUMENT_SYNTAX":
      case "ARGUMENT_MISSING":
        return StringUtil.format(msg, status['func'], status['param'], status['msg']);
      case "FUNCTION_EVALUATION":
      case "FUNCTION_UNKNOWN":
        return StringUtil.format(msg, status['func'], status['msg']);
      case "SOLR":
        return StringUtil.format(msg, status['query']);
      case "GENERAL":
      default:
        return StringUtil.format(msg, status['msg']);
    }
  }
}
}