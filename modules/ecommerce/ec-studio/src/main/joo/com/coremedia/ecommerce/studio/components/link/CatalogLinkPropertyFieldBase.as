package com.coremedia.ecommerce.studio.components.link {
import com.coremedia.ecommerce.studio.config.catalogLinkPropertyField;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class CatalogLinkPropertyFieldBase extends CatalogLinkProperty {

  private var readOnlyExpression:ValueExpression;
  private var droppingRowValueExpression:ValueExpression;
  private var multiple:Boolean;

  public function CatalogLinkPropertyFieldBase(config:catalogLinkPropertyField) {
    super(config);
    multiple = config.multiple;
  }


  /**
   * Returns value expression for the row where a item will be dropped
   */
  public function getDroppingRowValueExpression():ValueExpression{
    if (!droppingRowValueExpression){
      droppingRowValueExpression = ValueExpressionFactory.createFromValue();
    }
    return droppingRowValueExpression;
  }

  /**
   * Returns value expression for if the capacity is free or can not be calculated.
   */
  protected function getHasFreeCapacityExpression(config:catalogLinkPropertyField):ValueExpression{
    var hasFreeCapacityExpression:ValueExpression = ValueExpressionFactory.createFromFunction(function ():Boolean {
      if (multiple) {
        return true;
      }
      var productsExpression:ValueExpression = config.bindTo.extendBy('properties').extendBy(config.propertyName);
      var products:Array = productsExpression.getValue();
      return !products || products.length === 0;
    });
    return hasFreeCapacityExpression;
  }

  protected function getReadOnlyExpression(config:*):ValueExpression {
    if (!readOnlyExpression) {
      readOnlyExpression = ValueExpressionFactory.createFromFunction(CatalogLinkFieldBase.getReadOnlyFunction(config));
    }
    return readOnlyExpression;
  }

}

}