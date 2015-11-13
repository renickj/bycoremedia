package com.coremedia.blueprint.studio.struct.editor {
import com.coremedia.blueprint.studio.struct.StructEditor_properties;
import com.coremedia.blueprint.studio.struct.config.linkEditor;
import com.coremedia.cap.content.Content;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Component;
import ext.Container;
import ext.MessageBox;

public class LinkEditorBase extends Container {
  private var bindTo:ValueExpression;
  private var linkValueExpression:ValueExpression;
  private var linkTypeValueExpression:ValueExpression;

  public function LinkEditorBase(config:linkEditor) {
    super(config);
    this.bindTo = config.bindTo;
    getLinkValueExpression().setValue(bindTo.getValue());
    this.linkTypeValueExpression = config.linkTypeValueExpression;
    this.linkTypeValueExpression.addChangeListener(linkValueChanged);
  }

  override protected function afterRender():void {
    super.afterRender();
    linkValueChanged();
  }

  private function updateComponents():void {
    var ll:Component = find('itemId', 'target')[0];
    if(ll) {
      if(!linkTypeValueExpression.getValue()) {
        find('itemId', 'target')[0].setDisabled(true);
      }
      else {
        find('itemId', 'target')[0].setDisabled(false);
      }
    }
  }

  private function linkValueChanged():void {
    updateComponents();

    var value:Array = linkValueExpression.getValue();
    if(value && value.length > 0) {
      var content:Content = value[0];
      var type:String = linkTypeValueExpression.getValue();
      if(content.getType().isSubtypeOf(type)) {
        bindTo.setValue([content]);
      }
      else {
        MessageBox.alert(StructEditor_properties.INSTANCE.Struct_errors_type_title ,StructEditor_properties.INSTANCE.Struct_errors_type_msg);
        linkValueExpression.setValue([]);
      }
    }
    else {
      bindTo.setValue(linkValueExpression.getValue());
    }
  }

  public function reset():void {
    bindTo.setValue([]);
    linkValueExpression.setValue([]);
  }

  protected function getLinkValueExpression():ValueExpression {
    if(!linkValueExpression) {
      linkValueExpression = ValueExpressionFactory.create('linkValue', beanFactory.createLocalBean());
      linkValueExpression.addChangeListener(linkValueChanged);
    }
    return linkValueExpression;
  }
}
}