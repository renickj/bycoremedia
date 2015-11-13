package com.coremedia.blueprint.studio.util.plugins {

import com.coremedia.blueprint.studio.util.StudioPluginUtils_properties;
import com.coremedia.blueprint.studio.util.config.approveAndRetryPlugin;
import com.coremedia.cap.common.CapErrorCodes;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.publication.PublicationService;
import com.coremedia.cap.content.results.PublicationResult;
import com.coremedia.cap.content.results.PublicationResultItem;
import com.coremedia.cms.editor.sdk.actions.AbstractPublishAction;
import com.coremedia.cms.editor.sdk.publication.PublicationResultWindow;
import com.coremedia.cms.editor.sdk.publication.PublisherState;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.ui.data.OperationResult;
import com.coremedia.ui.components.FooterButton;

import ext.Component;
import ext.MessageBox;
import ext.Element;
import ext.Plugin;
import ext.util.StringUtil;

/**
 * Plugin, that approves all contents that caused a publication to fail
 * and retries to publish the set of contents.
 */
public class ApproveAndRetryPlugin implements Plugin {
  private var pubWindow:PublicationResultWindow;
  private var itemsToApprove:Array;

  public function ApproveAndRetryPlugin(config:approveAndRetryPlugin) {
    super(config);
  }

  public function init(component:Component):void {
    if (component is PublicationResultWindow) {
      pubWindow = component as PublicationResultWindow;
      pubWindow.addListener('afterrender',onAfterRender);
      if (pubWindow.initialConfig.publishOperation === AbstractPublishAction.OPERATION_PUBLISH || pubWindow.initialConfig.publishOperation === AbstractPublishAction.OPERATION_APPROVE_PUBLISH) {
        pubWindow.addButton(new FooterButton({
                  text: StudioPluginUtils_properties.INSTANCE.ApproveAllAndRetryAction_text,
                  itemId: 'bulkPublishBtn',
                  ctCls: 'bulkPublishBtnCt'
                }
        ), handler, this);
      }
    }
  }

  private function onAfterRender():void {
    var ElPubWindow:Element, suggestionBtn: Object, bulkPublishBtnCtEl:Element;
    ElPubWindow = pubWindow.getEl();
    suggestionBtn = ElPubWindow.select('div.suggestion');
    suggestionBtn.removeClass('suggestion');
    bulkPublishBtnCtEl = ElPubWindow.select('td.bulkPublishBtnCt .flat-light');
    bulkPublishBtnCtEl.addClass('suggestion');
  }

  private function handler():void {
    itemsToApprove = pubWindow.initialConfig.publicationResultItems;
    approveNext();
  }

  private function approveNext():void {
    var publicationService:PublicationService = session.getConnection().getContentRepository().getPublicationService();

    var next:PublicationResultItem = itemsToApprove.pop();
    if (next) {
      publicationService.approveWithPath(next.content, function (r:OperationResult):void {
        if (r.successful) {
          approveNext();
        }
        else {
          abort(r);
        }
      });
    }
    else {
      pubWindow.close();
      PublisherState.publicationStarted(pubWindow.initialConfig.publishOperation);
      publicationService.publishAll(pubWindow.contents, function (r:PublicationResult):void {
        PublisherState.publicationFinished(pubWindow.contents, r, pubWindow.initialConfig.publishOperation);
      });
    }
  }

  private function abort(response:OperationResult):void {
    response.error.setHandled(true);
    if (response.error.errorCode === CapErrorCodes.NOT_VALID) {
      var errorWindowTitle:String = StudioPluginUtils_properties.INSTANCE.CheckBefore_validationErrorWindowTitle;
      var errorText:String = StudioPluginUtils_properties.INSTANCE.CheckBefore_validationErrorWindowTextSimple;
      MessageBoxUtil.showError(errorWindowTitle, errorText, null, false);
    } else {
      pubWindow.close();
      MessageBox.alert(StudioPluginUtils_properties.INSTANCE.ApproveAllAndRetryAction_failure_title,
              StringUtil.format(StudioPluginUtils_properties.INSTANCE.ApproveAllAndRetryAction_failure_text,
                      response.error.errorName));
    }
  }
}
}
