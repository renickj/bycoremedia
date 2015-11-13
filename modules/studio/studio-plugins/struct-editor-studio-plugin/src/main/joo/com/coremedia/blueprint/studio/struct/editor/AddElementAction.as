package com.coremedia.blueprint.studio.struct.editor {
import com.coremedia.blueprint.studio.struct.config.addElementAction;
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.tree.TreeNode;

public class AddElementAction extends Action {

  private var nodeType:int;
  private var selectedNodeExpression:ValueExpression;
  private var structHandler:StructHandler;

  /**
   * @cfg {Function} callback The callback function to call when finished.
   * @param config
   */
  public function AddElementAction(config:addElementAction) {
    config['handler'] = createNode;
    super(config);
    this.nodeType = config.nodeType as int;
    this.structHandler = config.structHandler;
    this.selectedNodeExpression = config.selectedNodeExpression;
  }

  /**
   * Appends a new node on the tree, applies the new node to the selection expression.
   */
  protected function createNode():void {
    var node:TreeNode = structHandler.createNode(nodeType);
    if (selectedNodeExpression.getValue() && selectedNodeExpression.getValue() != structHandler.getRootNode()) {
      var selectedNode:TreeNode = selectedNodeExpression.getValue();
      selectedNode.appendChild(node);
      selectedNode.expand(true, true);
    }
    else {
      structHandler.getRootNode().appendChild(node);
      structHandler.getRootNode().expand(true, true);
    }
    selectedNodeExpression.setValue(node);
    structHandler.doSave();
  }

}
}