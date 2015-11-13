package com.coremedia.blueprint.studio.externalpreview {
import com.coremedia.blueprint.studio.util.AjaxUtil;
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.config.workArea;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.premular.Premular;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.impl.RemoteServiceMethod;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;

import ext.Component;
import ext.MessageBox;
import ext.TabPanel;

/**
 * Adds the button to start a workflow.
 */
public class ExternalPreviewPlugin {
  private static var token:String;
  private static var selection:Content;
  private static var dirty:Boolean;
  private static var updating:Boolean = false;
  private static var workarea:Component;

  public function init(component:Component):void {
    workarea = component;
    component.addListener('afterlayout', addTabListener);
  }

  /**
   * Registers the tab change listener after layout of the workarea.
   */
  private function addTabListener():void {
    workarea.removeListener('afterlayout', addTabListener);
    workArea.ACTIVE_CONTENT_VALUE_EXPRESSION.addChangeListener(tabChanged);
  }



  /**
   * Fired when the tab is changed.
   * This will trigger the request so that the external preview data is updated.
   */
  private function tabChanged(ve:ValueExpression):void {
    dirty = true;
    if (!token) { //user hasn't requested the token yet, so there is no need to send a REST update
      return;
    }
    if(selection) {
      unregisterListeners();
    }

    selection = ve.getValue();
    registerListeners();
    fireExternalPreviewUpdate();
  }

  public static function registerListeners():void {
    if(!selection) {
      selection = StudioUtil.getActiveContent();
    }
    if (selection) {
      unregisterListeners();
      selection.addValueChangeListener(fireExternalPreviewUpdate);
    }
  }

  private static function unregisterListeners():void {
    selection.removeValueChangeListener(fireExternalPreviewUpdate);
  }

  /**
   * Updates the preview CAE with the current preview data.
   */
  public static function fireExternalPreviewUpdate():void {
    dirty = true;
    if(!selection) {
      selection = StudioUtil.getActiveContent();
    }

    if(!updating) {
      dirty = false;
      updating = true;
      window.setTimeout(function():void {
        var wrapper:PreviewDataWrapper = new PreviewDataWrapper();
        if (selection) {
          selection.load(function ():void {
            var wa:TabPanel = editorContext.getWorkArea();
            var tab:Premular = wa.getActiveTab() as Premular;
            if (tab) { //maybe we do not have a premular open?
              var activeContent:Content = tab.getContent();
              wrapper.setActiveContent(activeContent);
              wrapper.addContent(activeContent);
              updatePreviewController(wrapper);
            }
            else {
              if(!wrapper.isEmpty()) {
                updatePreviewController(wrapper);
              }
            }
          });
        }
        else {
          if(!wrapper.isEmpty()) {
            updatePreviewController(wrapper);
          }

        }
      }, 2000);
    }
  }

  /**
   * Sends the comma separated content ids to the external preview resource.
   * @param wrapper Model for the data to pass to REST.
   */
  private static function updatePreviewController(wrapper:PreviewDataWrapper):void {
    var remoteServiceMethod:RemoteServiceMethod = new RemoteServiceMethod('externalpreview/update', 'POST');
    var params:Object = wrapper.asRequestParameters();
    remoteServiceMethod.request(params, function (response:RemoteServiceMethodResponse):void {
      updating = false;
      var result:Object = response.response.responseText;
      if (result && result == false) {
        MessageBox.alert(ExternalPreviewStudioPlugin_properties.INSTANCE.ExternalPreview_update_error_title,
                ExternalPreviewStudioPlugin_properties.INSTANCE.ExternalPreview_update_error_text);
      }
      else if(dirty) {
        fireExternalPreviewUpdate();
      }
    }, AjaxUtil.onErrorMethodResponse);
  }


  /**
   * Returns the preview token that identifies the studio instance.
   * @return A 4-digit token.
   */
  public static function getPreviewToken():String {
    if (!token) {
      var minNum:int = 1000;
      var maxNum:int = 9999;
      token = '' + Math.ceil(Math.random() * (maxNum - minNum + 1)) + (minNum - 1);
      while (token.length < 4) {
        token = '0' + token;
      }
      if (token.length > 4) {
        token = token.substr(0, 4);
      }
    }
    return token;
  }
}
}