package com.coremedia.ecommerce.studio.model {
[RestResource(uriTemplate="livecontext/product/{siteId:[^/]+}/{workspaceId:[^/]+}/{externalId:[^/]+}")]
public class ProductImpl extends CatalogObjectImpl implements Product {
  public function ProductImpl(uri:String) {
    super(uri);
  }

  public function getCategory():Category {
    return get(CatalogObjectPropertyNames.CATEGORY);
  }

  public function getThumbnailUrl():String {
    return get(CatalogObjectPropertyNames.THUMBNAIL_URL);

  }

  public function getPreviewUrl():String {
    return get(CatalogObjectPropertyNames.PREVIEW_URL);
  }

  public function getOfferPrice():Number {
    return get(ProductPropertyNames.OFFER_PRICE);
  }

  public function getListPrice():Number {
    return get(ProductPropertyNames.LIST_PRICE);
  }

  public function getCurrency():String {
    return get(ProductPropertyNames.CURRENCY);
  }

  public function getVariants():Array {
    return get(ProductPropertyNames.VARIANTS);
  }

  public function getVisuals():Array {
    return get(ProductPropertyNames.VISUALS);
  }

  public function getPictures():Array {
    return get(ProductPropertyNames.PICTURES);
  }

  public function getDownloads():Array {
    return get(ProductPropertyNames.DOWNLOADS);
  }

  public function getLongDescription():String {
    return get(CatalogObjectPropertyNames.LONG_DESCRIPTION);
  }

  public function getDescribingAttributes():Array {
    return get(ProductPropertyNames.DESCRIBING_ATTRIBUTES);
  }

  override public function invalidate(callback:Function = null):void {
    var thiz:* = this;
    super.invalidate(function():void {
      callback && callback(thiz);
      //all product variants need to be invalidated as well
      var variants:Array = getVariants() || [];
      for each (var variant:ProductVariant in variants) {
        variant.invalidate();
      }
    });
  }
}
}