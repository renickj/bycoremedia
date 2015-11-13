package com.coremedia.blueprint.studio.struct.editor {
import com.coremedia.blueprint.studio.struct.config.contentTypeEditor;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.util.EventUtil;

import ext.Container;
import ext.tree.TreeNode;

public class ContentTypeEditorBase extends Container {
  private var linkEditor:LinkEditor;
  private var contentTypeValueExpression:ValueExpression;
  private var bindTo:ValueExpression;
  private var node:TreeNode;
  private var structHandler:StructHandler;

  public function ContentTypeEditorBase(config:contentTypeEditor) {
    super(config);
    this.linkEditor = config.linkEditor;
    this.bindTo = config.bindTo;
    this.structHandler = config.structHandler;
    this.node = config.node;
  }

  override protected function afterRender():void {
    super.afterRender();
    getContentTypeValueExpression().setValue(bindTo.getValue());
    EventUtil.invokeLater(function ():void { //grr, seems that a plugins does work on the VE, so register the listener later...
      getContentTypeValueExpression().addChangeListener(contentTypeChanged);
    });
  }

  private function contentTypeChanged():void {
    if (linkEditor) {
      this.linkEditor.reset();
    }
    else {
      //reset all child nodes
      for (var i:int = 0; i<node.childNodes.length; i++) {
        var child:TreeNode = node.childNodes[i];
        var linkModel:ElementModel = structHandler.getData(child);
        linkModel.set(ElementModel.HREF_PROPERTY, null);
        structHandler.refresh(child);
      }
    }
    this.bindTo.setValue(contentTypeValueExpression.getValue());
  }

  protected function getContentTypeValueExpression():ValueExpression {
    if (!contentTypeValueExpression) {
      contentTypeValueExpression = ValueExpressionFactory.create('ct', beanFactory.createLocalBean());
    }
    return contentTypeValueExpression;
  }
}
}