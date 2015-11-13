package com.coremedia.blueprint.studio.struct.editor {

import com.coremedia.blueprint.studio.struct.config.structEditor;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Container;

/**
 * Base class of the struct property editor, creates the global value expressions for it
 * and implements the toolbar actions.
 */
public class StructEditorBase extends Container {
  private var bindTo:ValueExpression;
  private var propertyName:String;
  private var selectedNodeExpression:ValueExpression;
  private var structHandler:StructHandler;

  public function StructEditorBase(config:structEditor) {
    super(config);
    this.bindTo = config.bindTo;
    this.propertyName = config.propertyName;
  }

  /**
   * Creates the value expression that contains the active tree selection.
   * @return
   */
  protected function getSelectedNodeExpression():ValueExpression {
    if (!selectedNodeExpression) {
      selectedNodeExpression = ValueExpressionFactory.create('selection', beanFactory.createLocalBean());
    }
    return selectedNodeExpression;
  }


  /**
   * Creates the struct config handler that contains all node data and build the json for the tree.
   * @param config
   * @return
   */
  protected function getStructHandler(bindTo:ValueExpression, propertyName:String):StructHandler {
    if(!structHandler) {
      structHandler = new StructHandler(bindTo, propertyName, getSelectedNodeExpression());
    }
    return structHandler;
  }

  override protected function onDestroy():void {
    super.onDestroy();
    this.structHandler.destroy();
  }

  protected function getForceReadOnlyExpression(bindTo:ValueExpression, forceReadOnlyValueExpression:ValueExpression):ValueExpression {
    return PropertyEditorUtil.createReadOnlyValueExpression(bindTo, forceReadOnlyValueExpression);
  }
}
}