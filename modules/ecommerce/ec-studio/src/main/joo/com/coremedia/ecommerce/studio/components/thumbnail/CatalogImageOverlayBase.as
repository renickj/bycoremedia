package com.coremedia.ecommerce.studio.components.thumbnail {
import com.coremedia.ecommerce.studio.config.catalogDefaultOverlay;
import com.coremedia.ecommerce.studio.config.catalogImageOverlay;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.CatalogObjectPropertyNames;
import com.coremedia.ui.components.Image;
import com.coremedia.ui.components.ImageComponent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Ext;

public class CatalogImageOverlayBase extends CatalogDefaultOverlayBase {

  public function CatalogImageOverlayBase(config:catalogDefaultOverlay) {
    super(config);
  }

  internal function imageBindTo(config:catalogImageOverlay):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function(ve:ValueExpression):* {
      if (!ve.getValue()) {
        return null;
      }

      var src:String =  ve.extendBy(CatalogObjectPropertyNames.THUMBNAIL_URL).getValue();
      var docTypeCls:String;
      if(!src) {
        docTypeCls = CatalogHelper.getInstance().getTypeCls(ve.getValue());
      }
      return {'src': src,  'cls': docTypeCls};

    }, config.bindTo)
  }

  internal function imageBoundValueChanged(cmp:ImageComponent, ve:ValueExpression):void {
    if (!cmp.el) {
      return;
    }
    var obj:Object = ve.getValue();
    if (!obj) return;
    if (obj.src) {
      cmp.setSrc(obj.src);
      cmp.el.dom.className = 'thumbnail-overlay-image fit-catalog-thumb-overlay-image';
    } else if (obj.cls) {
      cmp.setSrc(Ext.BLANK_IMAGE_URL);
      if (!cmp.rendered) {
        cmp['cls'] = 'content-type-xl ' + obj.cls;
      } else {
        cmp.el.dom.className = 'content-type-xl ' + obj.cls;
      }
    }
  }

  override protected function resetImage(image:Image):void {
    image.setSrc(Ext.BLANK_IMAGE_URL);
    if (!image.rendered) {
      image['cls'] = 'thumbnail-overlay-image';
    } else {
      image.el.dom.className = 'thumbnail-overlay-image';
    }

  }

}
}