package com.coremedia.blueprint.studio.struct.editor {
import com.coremedia.blueprint.base.components.util.StringHelper;
import com.coremedia.blueprint.studio.struct.StructEditor_properties;
import com.coremedia.blueprint.studio.struct.XMLUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.undoc.content.ContentUtil;
import com.coremedia.cms.editor.ContentTypes_properties;
import com.coremedia.ui.data.impl.BeanImpl;
import com.coremedia.ui.util.EncodingUtil;

import ext.tree.TreeNode;
import ext.util.StringUtil;

import js.Element;
import js.Node;

/**
 * The model that represents each node of the tree.
 */
public class ElementModel extends BeanImpl {

  public static const ELEMENT_ROOT:int = 0;

  public static const ELEMENT_INT_LIST_PROPERTY:int = 1;
  public static const ELEMENT_INT_PROPERTY:int = 2;
  public static const ELEMENT_INT:int = 3;

  public static const ELEMENT_STRING_LIST_PROPERTY:int = 4;
  public static const ELEMENT_STRING_PROPERTY:int = 5;
  public static const ELEMENT_STRING:int = 6;

  public static const ELEMENT_BOOLEAN_LIST_PROPERTY:int = 7;
  public static const ELEMENT_BOOLEAN_PROPERTY:int = 8;
  public static const ELEMENT_BOOLEAN:int = 9;

  public static const ELEMENT_LINK_LIST_PROPERTY:int = 10;
  public static const ELEMENT_LINK_PROPERTY:int = 11;
  public static const ELEMENT_LINK:int = 12;

  public static const ELEMENT_STRUCT_LIST_PROPERTY:int = 13;
  public static const ELEMENT_STRUCT_PROPERTY:int = 14;
  public static const ELEMENT_STRUCT:int = 15;

  public static var NAMES:Array = [];
  {
    NAMES[ELEMENT_ROOT] = 'Struct xmlns="http://www.coremedia.com/2008/struct" xmlns:xlink="http://www.w3.org/1999/xlink"';

    NAMES[ELEMENT_INT_LIST_PROPERTY] = 'IntListProperty';
    NAMES[ELEMENT_INT_PROPERTY] = 'IntProperty';
    NAMES[ELEMENT_INT] = 'Int';

    NAMES[ELEMENT_STRING_LIST_PROPERTY] = 'StringListProperty';
    NAMES[ELEMENT_STRING_PROPERTY] = 'StringProperty';
    NAMES[ELEMENT_STRING] = 'String';

    NAMES[ELEMENT_BOOLEAN_LIST_PROPERTY] = 'BooleanListProperty';
    NAMES[ELEMENT_BOOLEAN_PROPERTY] = 'BooleanProperty';
    NAMES[ELEMENT_BOOLEAN] = 'Boolean';

    NAMES[ELEMENT_LINK_LIST_PROPERTY] = 'LinkListProperty';
    NAMES[ELEMENT_LINK_PROPERTY] = 'LinkProperty';
    NAMES[ELEMENT_LINK] = 'Link';

    NAMES[ELEMENT_STRUCT_LIST_PROPERTY] = 'StructListProperty';
    NAMES[ELEMENT_STRUCT_PROPERTY] = 'StructProperty';
    NAMES[ELEMENT_STRUCT] = 'Struct';
  }

  public static const TYPES:Array = [
    ELEMENT_STRING, ELEMENT_STRING_LIST_PROPERTY, ELEMENT_STRING_PROPERTY,
    ELEMENT_BOOLEAN, ELEMENT_BOOLEAN_LIST_PROPERTY, ELEMENT_BOOLEAN_PROPERTY,
    ELEMENT_INT, ELEMENT_INT_LIST_PROPERTY, ELEMENT_INT_PROPERTY,
    ELEMENT_STRUCT, ELEMENT_STRUCT_LIST_PROPERTY, ELEMENT_STRUCT_PROPERTY,
    ELEMENT_LINK, ELEMENT_LINK_LIST_PROPERTY, ELEMENT_LINK_PROPERTY
  ];

  public static const NAME_PROPERTY:String = 'Name';
  public static const VALUE_PROPERTY:String = 'Value';
  public static const HREF_PROPERTY:String = 'xlink:href';
  public static const MIN_PROPERTY:String = 'Min';
  public static const MAX_PROPERTY:String = 'Max';
  public static const LENGTH_PROPERTY:String = 'Length';
  public static const LINK_TYPE_PROPERTY:String = 'LinkType';

  private var type:int;
  private var formattedLinkTypeName:String;
  private var formattedContentName:String;

  public function ElementModel(type:int, node:Node = undefined) {
    this.type = type;
    if (node) {
      //the first child is the text node, so we set the value property using the first child's node value.
      if (node.firstChild) {
        set(VALUE_PROPERTY, node.firstChild.nodeValue);
      }
      var attributes:* = node['attributes'];
      if (attributes) {
        set(NAME_PROPERTY, XMLUtil.getAttributeValue(attributes, NAME_PROPERTY));
        set(MIN_PROPERTY, XMLUtil.getAttributeValue(attributes, MIN_PROPERTY));
        set(MAX_PROPERTY, XMLUtil.getAttributeValue(attributes, MAX_PROPERTY));
        set(LENGTH_PROPERTY, XMLUtil.getAttributeValue(attributes, LENGTH_PROPERTY));
        set(LINK_TYPE_PROPERTY, XMLUtil.getAttributeValue(attributes, LINK_TYPE_PROPERTY));
        set(HREF_PROPERTY, XMLUtil.getAttributeValue(attributes, HREF_PROPERTY));
      }
    }
  }

  /**
   * Copies all values of this node into the new element model.
   * @param newModel The model to copy the values into.
   */
  public function copyTo(newModel:ElementModel):void {
    newModel.formattedContentName = this.formattedContentName;
    newModel.formattedLinkTypeName = this.formattedLinkTypeName;
    newModel.set(NAME_PROPERTY, getName());
    newModel.set(MIN_PROPERTY, getMin());
    newModel.set(MAX_PROPERTY, getMax());
    newModel.set(LENGTH_PROPERTY, getLength());
    newModel.set(LINK_TYPE_PROPERTY, getLinkType());
    newModel.set(HREF_PROPERTY, getHRef());
    newModel.set(VALUE_PROPERTY, getValue());
  }
  
  public function getFormattedLinkTypeName():String{
    return EncodingUtil.encodeForHTML(formattedLinkTypeName);
  }
  
  public function getFormattedContentName():String {
    return EncodingUtil.encodeForHTML(formattedContentName);
  }

  public function getRawLinkTypeName():String {
    var type:String = getLinkType();
    if(type) {
      return type.substr(type.lastIndexOf('/')+1, type.length);
    }
    return undefined;
  }

  public function getType():int {
    return type;
  }

  public function getName():String {
    return EncodingUtil.encodeForHTML(get(NAME_PROPERTY));
  }

  public function getValue(escaped:Boolean = false):String {
    var value:String = get(VALUE_PROPERTY);
    if (value !== undefined && value !== null) {
      if(escaped) {
        value = ""+value; //int to string
        value = StringHelper.trim(value, StringHelper.stringToCharacter('\t'));
        value = StringHelper.trim(value, StringHelper.stringToCharacter('\r'));
        value = StringHelper.trim(value, StringHelper.stringToCharacter('\n'));
        value = StringHelper.trim(value, StringHelper.stringToCharacter(' '));
        return XMLUtil.escapeXML(value);
      }
      return value;
    }

    if(getType() === ELEMENT_INT) {
      return ''+0;
    }
    if(getType() === ELEMENT_BOOLEAN) {
      return 'false';
    }
    return value;
  }

  public function getHRef():String {
    return EncodingUtil.encodeForHTML(get(HREF_PROPERTY));
  }

  public function getMax():String {
    return EncodingUtil.encodeForHTML(get(MAX_PROPERTY));
  }

  public function getMin():String {
    return EncodingUtil.encodeForHTML(get(MIN_PROPERTY));
  }

  public function getLength():String {
    return EncodingUtil.encodeForHTML(get(LENGTH_PROPERTY));
  }

  public function getLinkType():String {
    return EncodingUtil.encodeForHTML(get(LINK_TYPE_PROPERTY));
  }

  public function toNodeString(modus:int):String {
    return ElementStringFactory.toNodeString(this, modus);
  }

  /**
   * Creates the JS dom element for the given model.
   * @param document The document to create the element for.
   * @return The corresponding element, including attributes of this bean.
   */
  public function toElement(document:*):Element {
    var xml:String = NAMES[type];
    var elem:Element = document.createElement(xml);

    if (getName()) {
      elem.setAttribute(NAME_PROPERTY, getName());
    }
    if (getMin()) {
      elem.setAttribute(MIN_PROPERTY, getMin());
    }
    if (getMax()) {
      elem.setAttribute(MAX_PROPERTY, getMax());
    }
    if (getHRef()) {
      elem.setAttribute(HREF_PROPERTY, getHRef());
    }
    if (getLength()) {
      elem.setAttribute(LENGTH_PROPERTY, getLength());
    }
    if (getLinkType()) {
      elem.setAttribute(LINK_TYPE_PROPERTY, getLinkType());
    }

    if (getValue() !== null && getValue() !== undefined) {
      var textNode:Node = document.createTextNode(getValue());
      elem.appendChild(textNode);
    }
    
    if(getType() !== ELEMENT_ROOT) {
      elem.removeAttribute('xmlns');
    }
    return elem;
  }

  /**
   * Returns true if the given element is valid
   * and contains all mandatory attributes.
   * @return The validation error if there is one or undefined.
   */
  public function validate():String {
    //common property check
    var elementName:String = NAMES[getType()];
    if(elementName.indexOf('Property') != -1) {
      if(!getName()) {
        return StringUtil.format(StructEditor_properties.INSTANCE.Struct_validation_error_missing_attribute, NAME_PROPERTY);
      }
    }
    //link checks
    if(elementName.indexOf('LinkProperty') != -1 || elementName.indexOf('LinkListProperty') != -1) {
      if(!getLinkType()) {
        return StringUtil.format(StructEditor_properties.INSTANCE.Struct_validation_error_missing_attribute, LINK_TYPE_PROPERTY);
      }
    }

    //native values check
    if((getType() === ELEMENT_INT || getType() === ELEMENT_BOOLEAN) && (getValue() === null || getValue() === undefined)) {
      return StringUtil.format(StructEditor_properties.INSTANCE.Struct_validation_error_missing_attribute, VALUE_PROPERTY);
    }
    return undefined;
  }

  /**
   * Refreshs the node with the data of this model.
   * @param activeNode
   */
  public function refresh(activeNode:TreeNode, modus:int):void {
    if (StructHandler.MODUS_FORMATTED == modus) {
      formattedLinkTypeName = getLinkType();
      if(formattedLinkTypeName) {
        formattedLinkTypeName = formattedLinkTypeName.substr(formattedLinkTypeName.lastIndexOf('/')+1, formattedLinkTypeName.length);
        var bundleValue:String = ContentTypes_properties.INSTANCE[formattedLinkTypeName + '_text'];
        if(bundleValue) {
          formattedLinkTypeName = bundleValue;
        }
      }
      else {
        formattedLinkTypeName = undefined;
      }

      if(getHRef()) {
        var linkContent:Content = ContentUtil.getContent(getHRef());
        linkContent.load(function ():void {
          if(linkContent.getState().exists) {
            formattedContentName = linkContent.getName();
          }
          else {
            formattedContentName = getHRef();
          }
          refreshHTML(activeNode, modus);
        });
      }
      else {
        formattedContentName = undefined;
      }
    } //plain mode
    else {
      formattedContentName = getHRef();
      formattedLinkTypeName = getLinkType(); //plain link type formatting
    }
    refreshHTML(activeNode, modus);
  }

  /**
   * Re-creates the HTML for the selected Node.
   */
  private function refreshHTML(activeNode:TreeNode, modus:int):void {
    activeNode.getUI().getEl().childNodes[0].childNodes[3].childNodes[0].innerHTML = toNodeString(modus);
    activeNode.getUI().show();
  }  
}
}