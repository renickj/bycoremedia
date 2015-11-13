package com.coremedia.ecommerce.studio.components.thumbnail {
import com.coremedia.ecommerce.studio.config.catalogDefaultOverlay;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.components.Image;
import com.coremedia.ui.components.ImageComponent;
import com.coremedia.ui.data.ValueExpression;

import ext.Container;
import ext.Ext;

public class CatalogDefaultOverlayBase extends Container {
  protected static const IMAGE_ITEM_ID:String = 'IMAGE_ITEM_ID';
  protected static const INFO_ITEM_ID:String = 'INFO_ITEM_ID';
  private static const X_THUMBNAIL_OVERLAY_DOCTYPE_ICON:String = 'x-content-type-xl';

  public function CatalogDefaultOverlayBase(config:catalogDefaultOverlay) {
    super(config);
  }

  /**
   * Reset the image and the info labels
   */
  public function reset():void {
    var image:Image = getImage();
    if (!image) {
      throw new Error(xtype + "must have an image component with item id " + IMAGE_ITEM_ID);
    }
    resetImage(image);
    var info:CatalogOverlayInfo = getComponent(INFO_ITEM_ID) as CatalogOverlayInfo;
    if (!info) {
      throw new Error(xtype + "must have an overlayInfo component with item id " + INFO_ITEM_ID);
    }
    info.reset();
  }

  protected function resetImage(image:Image):void {
    image.setSrc(Ext.BLANK_IMAGE_URL);
    if (!image.rendered) {
      image['cls'] = X_THUMBNAIL_OVERLAY_DOCTYPE_ICON;
    } else {
      image.el.dom.className = X_THUMBNAIL_OVERLAY_DOCTYPE_ICON;
    }
  }

  public function getImage():Image {
    return getComponent(IMAGE_ITEM_ID) as Image;
  }

  internal function boundValueChanged(component:ImageComponent, valueExpression:ValueExpression):void {
    var catalogObject:CatalogObject = valueExpression.getValue();
    if (catalogObject) {
      var typeCls:String = CatalogHelper.getInstance().getTypeCls(catalogObject);
      if(!component.rendered) {
        component['cls'] = 'content-type-xl ' + typeCls;
      } else {
        component.el.dom.className = 'content-type-xl '+ typeCls;
      }
    }
  }
}
}