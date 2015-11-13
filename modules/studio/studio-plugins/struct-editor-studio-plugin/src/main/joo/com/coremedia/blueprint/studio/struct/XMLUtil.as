package com.coremedia.blueprint.studio.struct {
import com.coremedia.ui.data.Blob;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.impl.BlobImpl;

import ext.Ext;

/**
 * Common XML formatting utilities.
 */
public class XMLUtil {

  public static function escapeXML(xml:String):String {
    while (xml.indexOf('>') != -1) {
      xml = xml.replace('>', '&gt;');
    }
    while (xml.indexOf('<') != -1) {
      xml = xml.replace('<', '&lt;');
    }
//    while(xml.indexOf('&') != -1) {
//      xml = xml.replace('&','&amp;');
//    }
    return xml;
  }

  /**
   * Returns the attribute value for the given mame.
   * @param attributes The attributes map.
   * @param name The name of the value inside the map.
   * @return The attribute's string value or undefined if attribute does not exists for the node.
   */
  public static function getAttributeValue(attributes:*, name:String):String {
    if (attributes && attributes.getNamedItem(name) && attributes.getNamedItem(name).value) {
      return attributes.getNamedItem(name).value;
    }
    return undefined;
  }


  /**
   * Creates a document for the given string
   * @param response
   * @return
   */
  public static function parseXML(response:String):* {
    var xmldoc:* = null;
    if (Ext.isIE) {
      xmldoc = new window['ActiveXObject']("Microsoft.XMLDOM");
      xmldoc.async = "false";
      xmldoc.loadXML(response);
    }
    else {
      xmldoc = (new window.DOMParser)['parseFromString'](response, "text/xml");
    }
    return xmldoc;
  }


  /**
   * Converts the document to a string for serializing into the blob property.
   * @param document
   * @return
   */
  public static function serializeToString(document:*, removeEmptyNS:Boolean = true):String {
    var xmlString:String = '';
    if (Ext.isIE) {
      xmlString = document['xml'];
    }
    else {
      xmlString = new window['XMLSerializer']().serializeToString(document);
    }

    if (removeEmptyNS) {
      //why? couldn't find anything about ns settings for documents
      while (xmlString.indexOf('xmlns=""') != -1) {
        xmlString = xmlString.replace('xmlns=""', '');
      }
    }

    return xmlString;
  }

  /**
   * Stores the given xml string into the value expression as blob.
   * @param xml
   * @param propertyName
   * @param bindTo
   */
  public static function serialize(xml:String, propertyName:String, bindTo:ValueExpression):void {
    var blob:Blob = BlobImpl.create(bindTo.extendBy('properties', propertyName).getValue(), xml, 'text/xml');
    bindTo.extendBy('properties', propertyName).setValue(blob);
  }

  /**
   * Pretty format for the given XML string.
   * @param xml The xml to format.
   * @return The indented xml.
   */
  public static function formatXml(xml:String):String {
    var formatted:String = '';
    var reg = /(>)(<)(\/*)/g;
    xml = xml.replace(reg, '$1\r\n$2$3');
    var pad:int = 0;
    xml.split('\r\n').forEach(function (node) {
      var indent:int = 0;
      if (node.match(/.+<\/\w[^>]*>$/)) {
        indent = 0;
      } else if (node.match(/^<\/\w/)) {
        if (pad != 0) {
          pad -= 1;
        }
      } else if (node.match(/^<\w[^>]*[^\/]>.*$/)) {
        indent = 1;
      } else {
        indent = 0;
      }

      var padding:String = '';
      for (var i:int = 0; i < pad; i++) {
        padding += '  ';
      }

      formatted += padding + node + '\r\n';
      pad += indent;
    });

    return formatted;
  }
}
}