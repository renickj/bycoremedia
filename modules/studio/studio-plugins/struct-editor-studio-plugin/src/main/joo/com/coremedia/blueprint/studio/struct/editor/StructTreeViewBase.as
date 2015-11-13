package com.coremedia.blueprint.studio.struct.editor {
import com.coremedia.blueprint.studio.struct.XMLUtil;
import com.coremedia.blueprint.studio.struct.config.structPasteDialog;
import com.coremedia.blueprint.studio.struct.config.structToolbarButton;
import com.coremedia.blueprint.studio.struct.config.structTreeView;
import com.coremedia.cms.editor.sdk.config.bindDisablePlugin;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Button;
import ext.IEventObject;
import ext.menu.Menu;
import ext.tree.TreeNode;
import ext.tree.TreePanel;
import ext.util.Observable;

/**
 * Base implementation of the taxonomy tree.
 */
public class StructTreeViewBase extends TreePanel {
  private var selectedNodeExpression:ValueExpression;
  private var handler:StructHandler;
  private var structPasteExpression:ValueExpression;
  private var bindTo:ValueExpression;
  private var forceReadOnlyValueExpression:ValueExpression;

  public function StructTreeViewBase(config:structTreeView) {
    super(config);
    this.bindTo = config.bindTo;
    this.forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;
    this.handler = config.structHandler;
    this.handler.getPropertyValueExpression().addChangeListener(updateUI);

    this.structPasteExpression = ValueExpressionFactory.create('xml', beanFactory.createLocalBean());
    this.structPasteExpression.addChangeListener(structPasted);

    //init value expression.
    this.selectedNodeExpression = config.selectedNodeExpression;
    this.selectedNodeExpression.addChangeListener(selectionExpressionChanged);
    getSelectionModel().addListener('selectionchange', selectionChanged);

    setRootNode(handler.getRootNode());
    addListener('afterlayout', initTree);
    addListener('contextmenu', showContextMenu);

    config.bindTo.addChangeListener(reload);
  }

  /**
   * Shows the context menu with add entries for the selected node.
   * @param node The selected node to show the menu for.
   * @param e The event capturing the mouse location.
   */
  private function showContextMenu(node:TreeNode, e:IEventObject):void {
    e.preventDefault();


    if (!forceReadOnlyValueExpression.getValue()) {
      node.select();
      var ctxMenu:Menu = ContextMenuFactory.createContextMenu(node, selectedNodeExpression, handler);
      if (ctxMenu) {
        ctxMenu.showAt(e.getXY());
      }
    }
  }

  /**
   * Invoked after the user has pressed the ok button of the paste dialog.
   */
  private function structPasted():void {
    selectedNodeExpression.setValue(null);
    var xml:String = structPasteExpression.getValue();
    updateUI();
    selectedNodeExpression.setValue(handler.getRootNode());
    handler.loadXML(xml, function ():void {
      handler.doSave();
    });
  }


  /**
   * Creates all buttons
   */
  private function initTree():void {
    removeListener('afterlayout', initTree);

    for (var i:int = 0; i < ElementModel.TYPES.length; i++) {
      var type:int = ElementModel.TYPES[i];
      var tButton:Button = new StructToolbarButton(structToolbarButton({
        nodeType:type,
        visible:false,
        structHandler:handler,
        selectedNodeExpression:selectedNodeExpression,
        plugins:[
          new bindDisablePlugin({forceReadOnlyValueExpression:forceReadOnlyValueExpression, bindTo:bindTo})
        ]
      }));
      getTopToolbar().add(tButton);
    }

    hideAll();
    reload();
  }

  /**
   * Reloads the struct property and rebuilds the tree.
   */
  private function reload():void {
    // The struct tree does not (as of 2013-03-11) support reloading
    // when it is not rendered. It will make an invalid reload() call into ExtJS.
    // This causes an error, because a ctNode field of an Ext object is not set.
    if (rendered) {
      handler.reload();
    }
  }


  /**
   * Removes the active node selected from the tree.
   */
  protected function removeNode():void {
    var node:TreeNode = selectedNodeExpression.getValue() as TreeNode;
    var newSelection:* = node.previousSibling || node.parentNode;
    node.parentNode.removeChild(node, true);
    getSelectionModel().select(newSelection);
    selectedNodeExpression.setValue(newSelection);
    handler.doSave();
    updateUI();
  }

  /**
   * Handler implementation for the reload button.
   */
  protected function reloadStruct():void {
    handler.reload();
  }

  /**
   * Executed for a selection change in the tree.
   * @return
   */
  private function selectionChanged(selectionModel:Observable, node:TreeNode):void {
    selectedNodeExpression.setValue(node);
    updateUI();
  }

  /**
   * Handler for the selected node value expression.
   */
  private function selectionExpressionChanged():void {
    var node:TreeNode = selectedNodeExpression.getValue();
    var enable:Boolean = (node != null && node != undefined);
    if (!enable) {
      hideAll();
    }
    else {
      var type:int = handler.getData(node).getType();
      updateButtons4Type(node);
    }
    getSelectionModel().select(node);
  }

  /**
   * Called when the property value expression changed.
   */
  private function updateUI():void {
    if (!handler.isValid()) {
      this.removeClass('struct-tree-panel');
      this.addClass('struct-tree-panel-error');
    }
    else {
      this.addClass('struct-tree-panel');
      this.removeClass('struct-tree-panel-error');
    }
  }

  /**
   * Shows and hides buttons depending on the selected node type.
   * @param type
   */
  private function updateButtons4Type(node:TreeNode):void {
    hideAll();
    var enable:Array = handler.getEnabledTypes(node);
    enable.forEach(function (eType:int):void {
      getButton4Type(eType).setVisible(true);
    });
  }

  /**
   * Enabling/Disabling handler for the up button, triggered by the component changed event plugin.
   * @param value The active value
   * @return
   */
  protected function nodeUpTransformer(node:TreeNode):Boolean {
    if (node && handler.getData(node).getType() !== ElementModel.ELEMENT_ROOT) {
      if (node.previousSibling) {
        return false;
      }
    }
    return true;
  }

  /**
   * Enabling/Disabling handler for the down button, triggered by the component changed event plugin.
   * @param value The active value
   * @return
   */
  protected function nodeDownTransformer(node:TreeNode):Boolean {
    if (node && handler.getData(node).getType() !== ElementModel.ELEMENT_ROOT) {
      if (node.nextSibling) {
        return false;
      }
    }
    return true;
  }


  /**
   * Displays the dialog that allows to paste raw struct XML.
   */
  protected function showPasteDialog():void {
    var xml:String = handler.getXML();
    xml = XMLUtil.formatXml(xml);
    var dialog:StructPasteDialog = new StructPasteDialog(structPasteDialog({xml:xml, structPasteExpression:structPasteExpression}));
    dialog.show();
  }

  /**
   * Sets all element buttons on the toolbar invisible.
   */
  private function hideAll():void {
    for (var i:int = 0; i < ElementModel.TYPES.length; i++) {
      getButton4Type(ElementModel.TYPES[i]).setVisible(false);
    }
  }

  /**
   * Enabling/Disabling handler for the delete button, triggered by the component changed event plugin.
   * @param value The active value
   * @return
   */
  protected function deleteButtonTransformer(value:TreeNode):Boolean {
    if (value &&
            handler.getData(value).getType() !== ElementModel.ELEMENT_ROOT) {
      return false;
    }
    return true;
  }

  /**
   * Enabling/Disabling handler for the copy button, triggered by the component changed event plugin.
   * @param value The active value
   * @return
   */
  protected function copyButtonTransformer(value:TreeNode):Boolean {
    if (value &&
        handler.getData(value).getType() !== ElementModel.ELEMENT_ROOT &&
        handler.getData(value.parentNode as TreeNode).getType() !== ElementModel.ELEMENT_STRUCT_PROPERTY) { //struct has XSD min/max of 0/1
      return false;
    }
    return true;
  }

  /**
   * Returns the button for the given type.
   * @param type The element model type.
   * @return
   */
  private function getButton4Type(type:int):Button {
    return getTopToolbar().find('itemId', 'btn-struct-' + type)[0] as Button;
  }

  /**
   * Event handler for the plain modus value expression.
   */
  protected function togglePlainModus():void {
    handler.toggleModus();
  }

  /**
   * Moves the selected node up
   */
  protected function nodeUp():void {
    moveNode(selectedNodeExpression.getValue(), true);
  }

  /**
   * Moves the selected node down
   */
  protected function nodeDown():void {
    moveNode(selectedNodeExpression.getValue(), false);
  }

  /**
   * Common move method.
   * @param node
   * @param moveUp
   */
  private function moveNode(node:TreeNode, moveUp:Boolean):void {
    if (node) {
      var newSelection:TreeNode = handler.moveNode(node, moveUp);
      getSelectionModel().select(newSelection);
      handler.doSave();
    }
  }

  /**
   * Create a clone of the selected node
   */
  protected function copyNode():void {
    var selection:TreeNode = selectedNodeExpression.getValue();
    if (selection) {
      var newSelection:TreeNode = handler.copyNode(selection);
      updateUI();
      getSelectionModel().select(newSelection);
      selectedNodeExpression.setValue(newSelection);
      handler.doSave();
    }
  }

  /**
   * Remove listeners
   */
  override protected function onDestroy():void {
    super.onDestroy();
    handler.getPropertyValueExpression().removeChangeListener(updateUI);
  }

  protected function makeDisabledExpression(forceReadOnlyValueExpression:ValueExpression, selectedNodeExpression:ValueExpression, transformer:Function):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function():* {
      if (forceReadOnlyValueExpression && forceReadOnlyValueExpression.getValue()) {
        return true;
      }

      var selectedNode:* = selectedNodeExpression.getValue();
      if (selectedNode === undefined) {
        return false;
      }

      return transformer(selectedNode);
    });
  }
}
}