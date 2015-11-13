package com.coremedia.ecommerce.studio.dragdrop {
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;

import ext.Ext;
import ext.Template;
import ext.XTemplate;
import ext.util.StringUtil;

/**
 * A helper class to create drag and drop visual feedback HTML
 */
public class CatalogDragDropVisualFeedback {

  private static var simpleDragDropTemplate:Template = new XTemplate(
    '<div>{text:htmlEncode}</div>').compile();

  private static var catalogTypeDragDropTemplate:Template = new XTemplate(
    '<div>'+
    '<img width="16" height="16" class="{cssClass} cm-before-text-icon" src="{imgSrc}" />{catalogObjectName:htmlEncode}'+
    '</div>').compile();

  public static function getHtmlFeedback(items:Array) : String {
    if (!items || items.length === 0) {
      return null;
    }

    if (items.length === 1) {
      //the item can be a CatalogObject or a BeanRecord
      var catalogObject:CatalogObject = (items[0] is CatalogObject)? items[0] : items[0].getBean();
      return catalogTypeDragDropTemplate.apply({
        catalogObjectName : catalogObject.getName(),
        cssClass : "content-type-xs " + CatalogHelper.getInstance().getTypeCls(catalogObject),
        imgSrc : Ext.BLANK_IMAGE_URL
      });
    } else {
      return simpleDragDropTemplate.apply({
        text : StringUtil.format(ECommerceStudioPlugin_properties.INSTANCE.Catalog_DragDrop_multiSelect_text, items.length)
      });
    }
  }
}
}