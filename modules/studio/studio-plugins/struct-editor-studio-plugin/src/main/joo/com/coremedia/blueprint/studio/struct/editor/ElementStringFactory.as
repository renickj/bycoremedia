package com.coremedia.blueprint.studio.struct.editor {
/**
 * Factory class for creating the string values
 * that are used as node labels.
 */
public class ElementStringFactory {

  public static function createEmptyRoot():String {
    return '<' + ElementModel.NAMES[ElementModel.ELEMENT_ROOT] + '/>';
  }

  /**
   * Formats the given string to indicate that it is a link/symbol.
   * @param value
   * @return
   */
  public static function formatLink(value:String):String {
    return '<u>' + value + '</u>';
  }

  /**
   * Returns the HTML representation that will be shown as node in the tree.
   * @param element
   * @param modus
   * @return
   */
  public static function toNodeString(element:ElementModel, modus:int):String {
    var type:int = element.getType();
    var toString:String = '';
    
    if(element.validate()) {
      toString += '<span class="struct-element-text-error">';
    }
    else {
      toString += '<span class="struct-element-text">';
    }
    
    toString += '<span class="struct-element">&lt;' + ElementModel.NAMES[type] + '</span>';


    if (element.getName()) {
      toString += getAttributeName(modus, ElementModel.NAME_PROPERTY, element.getName());
    }
    if (element.getMin()) {
      toString += getAttributeName(modus, ElementModel.MIN_PROPERTY, element.getMin());
    }
    if (element.getMax()) {
      toString += getAttributeName(modus, ElementModel.MAX_PROPERTY, element.getMax());
    }
    if (element.getHRef()) {
      var hrefValue:String = element.getFormattedContentName();
      if(modus == StructHandler.MODUS_FORMATTED) {
        hrefValue = formatLink(hrefValue);
      }
      toString += getAttributeName(modus, ElementModel.HREF_PROPERTY, hrefValue);
    }
    if (element.getLength()) {
      toString += getAttributeName(modus, ElementModel.LENGTH_PROPERTY, element.getLength());
    }
    if (element.getLinkType()) {
      var linkTypeValue:String = element.getFormattedLinkTypeName();
      if(modus == StructHandler.MODUS_FORMATTED) {
        linkTypeValue = formatLink(linkTypeValue);
      }
      toString += getAttributeName(modus, ElementModel.LINK_TYPE_PROPERTY, linkTypeValue);
    }

    //close open tag
    toString += '<span class="struct-element-close">&gt;</span>';

    //value
    if (element.getValue() !== null && element.getValue() !== undefined) {
      var value:String = element.getValue(true);
      if (value.length > 30 && modus != StructHandler.MODUS_PLAIN) {
        value = value.substr(0, 30);
        if (value.indexOf(' ') != -1) {
          value = value.substr(0, value.lastIndexOf(' '));
        }
        else {
          value = value.substr(0, 30);
        }
        value+=' <i>[...]</i>';
      }
      toString += '<span class="struct-element-value">' + value + '</span>';

    }

    //closing tag only if there is a value
    if (element.getValue() !== null && element.getValue() !== undefined) {
      toString+='<span class="struct-element-closing">';
      //just for nicer formatting we skip the closing struct elements
      if (type !== ElementModel.ELEMENT_STRUCT
              && type !== ElementModel.ELEMENT_ROOT
              && type !== ElementModel.ELEMENT_BOOLEAN_LIST_PROPERTY
              && type !== ElementModel.ELEMENT_INT_LIST_PROPERTY
              && type !== ElementModel.ELEMENT_LINK_LIST_PROPERTY
              && type !== ElementModel.ELEMENT_STRING_LIST_PROPERTY
              && type !== ElementModel.ELEMENT_STRUCT_PROPERTY
              && type !== ElementModel.ELEMENT_STRUCT_LIST_PROPERTY) {
        toString += ('&lt;/' + ElementModel.NAMES[type] + '&gt;');
      }
      toString+='</span>';
    }

    toString+='</span>'; //closing wrapper cls
    return toString;
  } 
  
  private static function getAttributeName(modus:int, name:String, value:String):String {
    var attributeCls:String = 'struct-attribute';
    value = '<span class="struct-attribute-value">' + value + '</span>';
    return ' <span class="struct-attribute">' + name + '=&quot;</span>' + value + '<span class="struct-attribute">&quot;</span>';
  }
}
}