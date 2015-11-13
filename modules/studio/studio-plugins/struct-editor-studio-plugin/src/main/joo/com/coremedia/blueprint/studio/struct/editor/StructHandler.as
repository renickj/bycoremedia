package com.coremedia.blueprint.studio.struct.editor {
import com.coremedia.blueprint.studio.struct.XMLUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.Blob;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.util.EventUtil;

import ext.tree.AsyncTreeNode;
import ext.tree.TreeNode;

/**
 * Holds the model of the tree, provides operations for loading
 * and storing the XML.
 */
public class StructHandler {
  private const CLS_ICON_OK:String = 'struct-status-ok';

  public static const MODUS_PLAIN:int = 0;
  public static const MODUS_FORMATTED:int = 1;
  private static var idGen:int = 0;

  private var bindTo:ValueExpression;
  private var propertyName:String;

  private var rootModel:Object;
  private var rootNode:AsyncTreeNode;

  private var elementValues:Bean;
  private var modusExpression:ValueExpression;

  private var propertyValueExpression:ValueExpression;
  private var selectedNodeExpression:ValueExpression;

  public function StructHandler(bindTo:ValueExpression, propertyName:String, selectedNodeExpression:ValueExpression):void {
    this.selectedNodeExpression = selectedNodeExpression;
    this.bindTo = bindTo;
    this.propertyName = propertyName;
    this.propertyValueExpression = bindTo.extendBy('properties', propertyName);
    modusExpression = ValueExpressionFactory.create('modus', beanFactory.createLocalBean());
    modusExpression.setValue(MODUS_FORMATTED);

    elementValues = beanFactory.createLocalBean();
  }

  public function getPropertyValueExpression():ValueExpression {
    return propertyValueExpression;
  }

  public function getModusExpression():ValueExpression {
    return modusExpression;
  }

  /**
   * Returns an array of type ids. These type id's are the subelement
   * types that are allowed to be created for the given type.
   * @param type The type to retrieve the enablements for.
   * @return
   */
  public function getEnabledTypes(node:TreeNode):Array {
    var model:ElementModel = getData(node);
    var type:int = model.getType();
    var types:Array = [];
    if (type == ElementModel.ELEMENT_INT_PROPERTY ||
            type == ElementModel.ELEMENT_STRING_PROPERTY ||
            type == ElementModel.ELEMENT_BOOLEAN_PROPERTY ||
            type == ElementModel.ELEMENT_STRING_PROPERTY) {
    }
    else if (type == ElementModel.ELEMENT_STRUCT_PROPERTY) {
      //allow only one child struct
      if (node.childNodes.length === 0) {
        types.push(ElementModel.ELEMENT_STRUCT);
      }
    }
    else if (type == ElementModel.ELEMENT_STRUCT || type == ElementModel.ELEMENT_ROOT) { //we are working on structs so enable all property elements
      types.push(ElementModel.ELEMENT_BOOLEAN_PROPERTY);
      types.push(ElementModel.ELEMENT_BOOLEAN_LIST_PROPERTY);
      types.push(ElementModel.ELEMENT_INT_PROPERTY);
      types.push(ElementModel.ELEMENT_INT_LIST_PROPERTY);
      types.push(ElementModel.ELEMENT_STRING_PROPERTY);
      types.push(ElementModel.ELEMENT_STRING_LIST_PROPERTY);
      types.push(ElementModel.ELEMENT_LINK_PROPERTY);
      types.push(ElementModel.ELEMENT_LINK_LIST_PROPERTY);
      types.push(ElementModel.ELEMENT_STRUCT_PROPERTY);
      types.push(ElementModel.ELEMENT_STRUCT_LIST_PROPERTY);
    }
    else if (type == ElementModel.ELEMENT_BOOLEAN_LIST_PROPERTY) {
      types.push(ElementModel.ELEMENT_BOOLEAN);
    }
    else if (type == ElementModel.ELEMENT_STRING_LIST_PROPERTY) {
      types.push(ElementModel.ELEMENT_STRING);
    }
    else if (type == ElementModel.ELEMENT_INT_LIST_PROPERTY) {
      types.push(ElementModel.ELEMENT_INT);
    }
    else if (type == ElementModel.ELEMENT_STRUCT_LIST_PROPERTY) {
      types.push(ElementModel.ELEMENT_STRUCT);
    }
    else if (type == ElementModel.ELEMENT_LINK_LIST_PROPERTY) {
      types.push(ElementModel.ELEMENT_LINK);
    }
    return types;
  }

  /**
   * Returns the active display mode for the tree editor.
   * @return
   */
  public function getModus():int {
    return modusExpression.getValue();
  }

  /**
   * Toggles between link resolving mode and plain text mode.
   */
  public function toggleModus():void {
    if (modusExpression.getValue() === MODUS_FORMATTED) {
      modusExpression.setValue(MODUS_PLAIN);
    }
    else {
      modusExpression.setValue(MODUS_FORMATTED);
    }
    updateNodeLinks();
  }

  /**
   * Returns the data model for the given tree node.
   * @param node
   * @return
   */
  public function getData(node:TreeNode):ElementModel {
    if (!node) {
      return undefined;
    }
    return elementValues.get(node.id);
  }

  /**
   * Returns the root node of the tree which is an async tree node
   * to create the JSON out of XML and create the tree afterwards.
   * @return
   */
  public function getRootNode():AsyncTreeNode {
    if (!rootNode) {
      var model:ElementModel = new ElementModel(ElementModel.ELEMENT_ROOT);
      rootModel = createNodeSettings(model);
      elementValues.set(rootModel.id, model);
      rootNode = new AsyncTreeNode(rootModel);
    }
    return rootNode;
  }

  /**
   * Loads the tree model.
   * @param callback Called when finished.
   */
  public function reload(callback:Function = undefined):void {
    var content:Content = bindTo.getValue();
    content.load(function ():void {
      var blob:Blob = ValueExpressionFactory.create(propertyName, content.getProperties()).getValue();
      if (!blob) {
        loadXML(null, callback);
      }
      else {
        blob.loadData(function ():void {
          loadXML(blob.getData(), callback);
        });
      }
    });
  }

  /**
   * Loads the struct XML blob and creates the tree out of it.
   */
  public function loadXML(response:String, callback:Function):void {
    if (!response) { //we must have something here, even if the blob is empty. FF doesn't like null values here.
      response = ElementStringFactory.createEmptyRoot();
    }

    var doc:* = XMLUtil.parseXML(response);

    var json:Array = [];
    buildTree(json, doc.childNodes[0]);

    rootModel.children = json;
    rootNode.reload(function ():void {
      updateNodeLinks();
      if(callback) {
        callback.call(null);
      }
    });
  }


  /**
   * Re-renders the nodes with links.
   */
  public function updateNodeLinks():void {
    updateLinks(rootNode, getModusExpression().getValue());
  }

  /**
   * Recursive call to update the HTML of each tree node.
   * @param node
   * @param modus
   */
  private function updateLinks(node:TreeNode, modus:int):void {
    var model:ElementModel = getData(node);
    model.refresh(node, modus);
    for (var i:int = 0; i < node.childNodes.length; i++) {
      updateLinks(node.item(i) as TreeNode, modus);
    }
  }


  /**
   * Recursive build of the struct tree
   * @param document
   */
  private function buildTree(parentChildren:Array, parent:*):void {
    for (var i:int = 0; i < parent.childNodes.length; i++) {
      var node:* = parent.childNodes.item(i);
      var type:int = getTypeForNode(node);
      if (type > 0) {
        var elementData:ElementModel = new ElementModel(type, node);
        var treeNode:* = createNodeSettings(elementData);
        elementValues.set(treeNode.id, elementData);
        parentChildren.push(treeNode);
        buildTree(treeNode.children, node);
      }
    }
  }

  /**
   * Returns the numeric type of the node.
   * @param node
   * @return
   */
  private function getTypeForNode(node:*):int {
    var name:String = node.nodeName;
    for (var i:int = 0; i < ElementModel.NAMES.length; i++) {
      if (ElementModel.NAMES[i] == name) {
        return i;
      }
    }
    return -1;
  }

  /**
   * The common handler for the toolbar create actions.
   * @param type
   * @param appendAsChild
   */
  public function createNode(type:int):TreeNode {
    var model:ElementModel = new ElementModel(type);
    ElementModelnitializer.initNodeDefaults(model);
    var node:Object = createNodeSettings(model);
    var treeNode:TreeNode = new TreeNode(node);
    elementValues.set(node.id, model);
    return treeNode;
  }

  /**
   * Creates the json for the TreeNode object.
   * @param data
   * @param type
   * @return
   */
  public function createNodeSettings(data:ElementModel):* {
    idGen++;
    var id:String = data.getType() + "-" + idGen;
    return {
      children:[],
      id:id,
      iconCls:CLS_ICON_OK,
      text:data.toNodeString(modusExpression.getValue()),
      expandable:true,
      expanded:true,
      leaf:false
    }
  }

  /**
   * Moves the node on one level inside the tree.
   * @param node The node to move.
   * @param moveUp True to move the node up, false to move down.
   * @return
   */
  public function moveNode(node:TreeNode, moveUp:Boolean):TreeNode {
    var parent:TreeNode = node.parentNode as TreeNode;
    if (moveUp) {
      parent.insertBefore(node, node.previousSibling);
    }
    else {
      if (node.nextSibling.nextSibling) {
        parent.insertBefore(node, node.nextSibling.nextSibling);
      }
      else {
        parent.appendChild(node);
      }
    }
    return node;
  }

  /**
   * Copies the given node and inserts it as sibling for the given node.
   * @param node The node to copy.
   * @return The new node.
   */
  public function copyNode(node:TreeNode):TreeNode {
    var newChild:TreeNode = cloneNode(node);
    copyRecursive(node, newChild);

    //check unique name attribute value
    var clonedModel:ElementModel = getData(newChild);
    var originalModel:ElementModel = getData(node);
    if (clonedModel.getName()) {
      var children:Array = node.childNodes;
      var index:int = 1;
      var name:String = originalModel.getName() + " (" + index + ")";
      while(hasChildWithName(node.parentNode as TreeNode, name)) {
        index++;
        name = originalModel.getName() + " (" + index + ")";
      }
      clonedModel.set(ElementModel.NAME_PROPERTY, name);
    }

    if (node.nextSibling) {
      node.parentNode.insertBefore(newChild, node.nextSibling);
    }
    else {
      node.parentNode.appendChild(newChild);
    }
    refresh(newChild);
    return newChild;
  }

  /**
   * Checks if the given node has a sibling with the given name.
   * @param node The node to check the siblings for.
   * @param name The name to check the unique name for.
   * @return True, if a child node with the name exists.
   */
  private function hasChildWithName(node:TreeNode, name:String):Boolean {
    var children:Array = node.childNodes;
    for (var i:int = 0; i < children.length; i++) {
      var child:TreeNode = children[i];
      var childModel:ElementModel = getData(child);
      if(childModel.getName() === name) {
        return true;
      }
    }
    return false;
  }

  /**
   * Recursive refresh.
   * @param node
   */
  public function refresh(node:TreeNode):void {
    var model:ElementModel = getData(node);
    model.refresh(node, modusExpression.getValue());

    for (var i:int = 0; i < node.childNodes.length; i++) {
      refresh(node.childNodes[i]);
    }
  }

  /**
   * Recursive copy call to copy the sub-tree of the origin node
   * to a new sibling tree that is appended to the same parent node afterwards.
   * @param origin The original tree to copy.
   * @param clone The clones tree.
   */
  private function copyRecursive(origin:TreeNode, clone:TreeNode):void {
    for (var i:int = 0; i < origin.childNodes.length; i++) {
      var originChild:TreeNode = origin.childNodes[i];
      var newChild:TreeNode = cloneNode(originChild);
      clone.appendChild(newChild);
      copyRecursive(originChild, newChild);
    }
  }

  /**
   * Clones the given tree node, does not append to a parent or something.
   * @param origin
   * @return
   */
  private function cloneNode(origin:TreeNode):TreeNode {
    var originModel:ElementModel = getData(origin);
    var newChild:TreeNode = createNode(originModel.getType());
    var newModel:ElementModel = getData(newChild);
    originModel.copyTo(newModel);
    return newChild;
  }

  /**
   * Writes the current XML to the corresponding content property.
   */
  public function doSave():void {
    if (isValid()) {
      var xml:String = getXML();
      propertyValueExpression.removeChangeListener(blobPropertyChanged);
      XMLUtil.serialize(xml, propertyName, bindTo);
      EventUtil.invokeLater(function():void {
        propertyValueExpression.addChangeListener(blobPropertyChanged);
      });
    }
  }

  /**
   * Invoked when the content property is reloaded, e.g. when another version
   * is restored. The struct tree has to be rebuild then.
   */
  private function blobPropertyChanged():void {
    this.reload(); //reload the tree
    selectedNodeExpression.setValue(getRootNode()); //important! re-select a node for this expression
  }

  /**
   * Returns the raw xml that will be serialized.
   * @return
   */
  public function getXML():String {
    var xml:String = ElementStringFactory.createEmptyRoot();
    var document:* = XMLUtil.parseXML(xml);
    createXML(document, document.childNodes[0], rootNode);
    var serializedXML:String = XMLUtil.serializeToString(document);
    return serializedXML;
  }

  /**
   * Returns true if the whole document is valid.
   * @return
   */
  public function isValid():Boolean {
    var xml:String = '<' + ElementModel.NAMES[0] + '/>';
    var document:* = XMLUtil.parseXML(xml);
    if (document) {
      var errorNodes:Array = [];
      createXML(document, document.childNodes[0], rootNode, errorNodes);
      return errorNodes.length === 0;
    }
    return true;
  }


  /**
   * Recursive call to create a javascript dom tree.
   * @param document
   * @param parent
   * @param tNode
   */
  private function createXML(document:*, parent:*, tNode:TreeNode, errorNodes:Array = undefined):void {
    for (var i:int = 0; i < tNode.childNodes.length; i++) {
      var childTNode:TreeNode = tNode.item(i) as TreeNode;
      var id:String = childTNode.id;
      var model:ElementModel = elementValues.get(id) as ElementModel;
      if (model.validate()) {
        //skip node
        if (errorNodes) {
          errorNodes.push(childTNode);
        }
      }
      else {
        var node:* = model.toElement(document);
        parent.appendChild(node);
        createXML(document, node, childTNode, errorNodes);
      }
    }
  }

  /**
   * Invoked when the struct property editor is destroyed.
   */
  public function destroy():void {
    propertyValueExpression.removeChangeListener(blobPropertyChanged);
  }
}
}