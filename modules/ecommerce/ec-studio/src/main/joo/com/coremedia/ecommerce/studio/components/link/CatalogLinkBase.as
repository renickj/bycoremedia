package com.coremedia.ecommerce.studio.components.link {

import com.coremedia.blueprint.studio.property.ImageLinkListRenderer;
import com.coremedia.cap.content.Content;
import com.coremedia.ecommerce.studio.config.catalogLink;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.data.BeanState;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.util.PropertyChangeEventUtil;
import com.coremedia.ui.store.BeanRecord;

import ext.grid.GridPanel;

/**
 * The read-only version of the catalog link field
 */
public class CatalogLinkBase extends GridPanel {

  private var forceReadOnly:Boolean;
  private var selectedItems:Array;
  private var selectedPositions:Array;

  private var catalogObjRemoteBean:RemoteBean;

  private var propertyExpression:ValueExpression;

  /**
   * @param config the config object
   */
  public function CatalogLinkBase(config:catalogLink) {
    super(config);
    if (config.forceReadOnlyValueExpression) {
      config.forceReadOnlyValueExpression.addChangeListener(function(source:ValueExpression):void {
        setForceReadOnly(source.getValue());
      });
      setForceReadOnly(config.forceReadOnlyValueExpression.getValue());
    }

  }

  // fire event as a context provider when context value is changed
  public function setForceReadOnly(readOnly:Boolean):void {
    var oldValue:Boolean = forceReadOnly;
    forceReadOnly = readOnly;

    PropertyChangeEventUtil.fireEvent(this, catalogLink.FORCE_READ_ONLY_VARIABLE_NAME, oldValue, readOnly);
  }

  [ProvideToExtChildren]
  public function getForceReadOnly():Boolean {
    return forceReadOnly;
  }

  [ProvideToExtChildren]
  public function getSelectedItems():Array {
    return selectedItems;
  }

  public function setSelectedItems(value:Array):void {
    var oldValue:* = selectedItems;
    selectedItems = value;
    PropertyChangeEventUtil.fireEvent(this, catalogLink.SELECTED_ITEMS_VARIABLE_NAME, oldValue, value);
  }

  [ProvideToExtChildren]
  public function getSelectedPositions():* {
    return selectedPositions;
  }

  public function setSelectedPositions(value:*):void {
    var oldValue:* = selectedPositions;
    selectedPositions = value;
    PropertyChangeEventUtil.fireEvent(this, catalogLink.SELECTED_POSITIONS_VARIABLE_NAME, oldValue, value);
  }

  internal function getCatalogListFunction(config:catalogLink):Function {
    return function ():Array {
      var valuesArray:Array = [];
      var values:* = getPropertyExpression(config).getValue();
      if (values) {
        //the value can be a string or a catalog object bean
        if (values is String || values is CatalogObject) {
          //this is a single catalog object stored
          valuesArray = [values];
        } else if (values is Array) {
          //this are multiple catalog objects stored in an array
          valuesArray = values;
        }
      }

      return valuesArray.map(function (value:*):CatalogObject {
        //the value can be a string or a catalog object bean
        var catalogObject:CatalogObject;

        if (value is CatalogObject) {
          catalogObject = value;
        } else if (value is String) {
          catalogObject = CatalogHelper.getInstance().getCatalogObject(value, config.bindTo) as CatalogObject;
        } else {
          trace("[ERROR]", "CatalogLink does not accept the value: " + value);
        }

        if (catalogObject === undefined) {
          return undefined;
        }

        if (catalogObject !== catalogObjRemoteBean) {
          if (catalogObjRemoteBean) {
            catalogObjRemoteBean.removePropertyChangeListener(BeanState.PROPERTY_NAME, invalidateIssues);
          }
          catalogObjRemoteBean = catalogObject;
          if (catalogObject) {
            catalogObjRemoteBean.addPropertyChangeListener(BeanState.PROPERTY_NAME, invalidateIssues);
          }
        }
        return catalogObject;
      });
    }
  }

  private function invalidateIssues(event:PropertyChangeEvent):void {
    if (event.newState === BeanState.NON_EXISTENT || event.oldState === BeanState.NON_EXISTENT) {
      var content:Content = initialConfig.bindTo.getValue() as Content;
      if (content) {
        if (content.getIssues()) {
          content.getIssues().invalidate();
        }
      }
    }
  }

  protected function getSelectedValuesExpression():ValueExpression {
    return ValueExpressionFactory.create(catalogLink.SELECTED_ITEMS_VARIABLE_NAME, this);
  }

  protected function getSelectedPositionsExpression():ValueExpression {
    return ValueExpressionFactory.create(catalogLink.SELECTED_POSITIONS_VARIABLE_NAME, this);
  }

  protected function getPropertyExpression(config:catalogLink):ValueExpression {
    if (!propertyExpression) {
      if (config.bindTo.getValue() is Content) {
        propertyExpression = config.bindTo.extendBy('properties').extendBy(config.propertyName);
      } else {
        propertyExpression = config.bindTo.extendBy(config.propertyName);
      }
    }
    return propertyExpression;
  }

  internal static function convertTypeLabel(v:String, catalogObject:CatalogObject):String {
    if (catalogObject is CatalogObject) {
      return CatalogHelper.getInstance().getTypeLabel(catalogObject)
    }
  }

  internal static function convertTypeCls(v:String, catalogObject:CatalogObject):String {
    if (catalogObject is CatalogObject) {
      return CatalogHelper.getInstance().getTypeCls(catalogObject)
    }
  }

  internal function convertIdLabel(v:String, catalogObject:CatalogObject):String {
    if (!catalogObject) return undefined;
    if (catalogObject is CatalogObject) {
      try {
        var extId:String = catalogObject.getExternalId();
        if (extId) {
          return extId;
        }
      } catch(e:Error){
        return CatalogHelper.getInstance().getExternalIdFromId(catalogObject.getUri());
      }
    }
    return CatalogHelper.getInstance().getExternalIdFromId(catalogObject.getUri());
  }

  internal function convertNameLabel(v:String, catalogObject:CatalogObject):String {
    var name:String = undefined;
    if (!catalogObject) return name;
    if (catalogObject is CatalogObject) {
      try {
        name = CatalogHelper.getInstance().getDecoratedName(catalogObject);
      } catch(e:Error){
        //ignore
      }
    }
    if (!name) {
      name = CatalogHelper.getInstance().getExternalIdFromId(catalogObject.getUri());
    }
    return name;
  }

  internal static function thumbColRenderer(value:*, metaData:*, record:BeanRecord):String {
    return ImageLinkListRenderer.renderImage(value, false);
  }

}
}
