package com.coremedia.blueprint.studio.topicpages.administration {

import com.coremedia.blueprint.studio.topicpages.config.topicPagesEditor;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Ext;
import ext.Panel;

/**
 * Base class of the taxonomy administration tab.
 */
public class TopicPagesEditorBase extends Panel {
  protected static const TOPIC_PAGES_EDITOR_ID:String = 'topicPagesEditor';

  private var selectionExpression:ValueExpression;

  public function TopicPagesEditorBase(config:topicPagesEditor) {
    super(config);
  }

  /**
   * Returns the taxonomy editor instance if opened, undefined otherwise.
   * @return
   */
  public static function getInstance():TopicPagesEditor {
    return Ext.getCmp(TOPIC_PAGES_EDITOR_ID) as TopicPagesEditor;
  }

  /**
   * Returns the value expression that contains the active selection.
   * @return
   */
  protected function getSelectionExpression():ValueExpression {
    if(!selectionExpression) {
      selectionExpression = ValueExpressionFactory.create('selection', beanFactory.createLocalBean());
    }
    return selectionExpression;
  }
}
}
