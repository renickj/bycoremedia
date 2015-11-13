package com.coremedia.blueprint.studio.topicpages.administration {

import com.coremedia.blueprint.studio.topicpages.config.topicPreviewPanel;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.ValueExpression;

import ext.Panel;
import ext.data.Record;
import ext.form.Label;

/**
 * Base class of the topic pages preview panel that access an iframe for applying the preview URL.
 */
public class TopicPreviewPanelBase extends Panel {
  protected static const PREVIEW_FRAME:String = "topicPagesPreviewFrame";

  private var frameLabel:Label;
  private var selectionExpression:ValueExpression;
  private var lastUrl:String;

  public function TopicPreviewPanelBase(config:topicPreviewPanel) {
    super(config);
    frameLabel = this.getComponent(PREVIEW_FRAME) as Label;

    this.selectionExpression = config.selectionExpression;
    selectionExpression.addChangeListener(selectionChanged);
  }

  /**
   * Fired when a new entry has been selected on the topic list.
   * The url is only updated when the selection has not changed for 2 seconds.
   */
  private function selectionChanged():void {
    var record:Record = selectionExpression.getValue();
    if(record) {
      var topic:Content = record.data.topic;
      topic.load(function():void {
        var url:String = topic.getPreviewUrl();
        url = url + '&site=' + editorContext.getSitesService().getPreferredSiteId();
        //recheck URL after 2 seconds
        window.setTimeout(function():void {
          if(lastUrl !== url) {
            lastUrl = url;
            trace('[INFO]', 'Updating topic page preview URL: ' + url);
            frameLabel.setText(getFrameHTML(url), false);
          }
        }, 2000);
      });
    }
    else {
      lastUrl = undefined;
    }
  }

  private static function getFrameHTML(url:String):String {
    return '<iframe frameborder="0" src="' +url + '" height="100%" width="100%"></iframe>';
  }
}
}