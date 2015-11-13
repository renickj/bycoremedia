package com.coremedia.livecontext.studio.components.link {
import com.coremedia.livecontext.studio.config.catalogAssetsProperty;
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;


public class CatalogAssetPropertyBase extends SwitchingContainer {

  protected static const CATALOG_ASSET_PROPERTY_ITEM_ID = 'catalogAssets';
  protected static const CATALOG_EMPTY_LABEL_ITEM_ID = 'emptyLabelText';

  public function CatalogAssetPropertyBase(config:catalogAssetsProperty) {
    super(config);
  }

  internal function getActiveCatalogAssetPropertyValueExpression(config:catalogAssetsProperty):ValueExpression {
    return ValueExpressionFactory.createFromFunction(getActiveCatalogAssetProperty, config);
  }

  private function getActiveCatalogAssetProperty(config:catalogAssetsProperty):String {
    var valueExpression:ValueExpression = config.bindTo.extendBy(config.propertyName);
    var values:Array = valueExpression.getValue();
    if (values && values.length != 0) {
      return CATALOG_ASSET_PROPERTY_ITEM_ID;
    }
    return CATALOG_EMPTY_LABEL_ITEM_ID;
  }
}
}