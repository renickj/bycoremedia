package com.coremedia.livecontext.asset.studio.action {

import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.livecontext.asset.studio.config.inheritProductListAction;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.ui.actions.DependencyTrackedToggleAction;
import com.coremedia.ui.data.ValueExpression;

import ext.Ext;

use namespace Ext;

public class InheritProductListAction extends DependencyTrackedToggleAction {

  private static const PROPERTIES:String = 'properties';
  private static const LOCAL_SETTINGS_STRUCT_NAME:String = 'localSettings';
  private static const COMMERCE_STRUCT_NAME:String = 'commerce';
  private static const INHERIT_PROPERTY_NAME:String = 'inherit';
  private static const ORIGIN_PRODUCTS_STRUCT_NAME:String = 'originProducts';
  private static const PRODUCTS_STRUCT_NAME:String = 'products';

  private var bindTo:ValueExpression;
  private var inheritExpression:ValueExpression;
  private var originProductListExpression:ValueExpression;
  private var productListExpression:ValueExpression;
  private var productList:Array = [];
  private var forceReadOnlyValueExpression:ValueExpression;


  public function InheritProductListAction(config:inheritProductListAction) {
    // Copy values before super constructor call for calculateDisable.
    bindTo = config.bindTo;
    inheritExpression = config.inheritExpression || config.bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME, COMMERCE_STRUCT_NAME, INHERIT_PROPERTY_NAME);
    originProductListExpression = config.originProductListExpression || config.bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME, COMMERCE_STRUCT_NAME, ORIGIN_PRODUCTS_STRUCT_NAME);
    productListExpression = config.productListExpression || config.bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME, COMMERCE_STRUCT_NAME, PRODUCTS_STRUCT_NAME);

    forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;

    super(inheritProductListAction(Ext.apply({
      iconCls: LivecontextStudioPlugin_properties.INSTANCE.InheritProductListAction_icon,
      text: LivecontextStudioPlugin_properties.INSTANCE.InheritProductListAction_text,
      tooltip: LivecontextStudioPlugin_properties.INSTANCE.InheritProductListAction_tooltip,
      tooltipPressed:LivecontextStudioPlugin_properties.INSTANCE.InheritProductListAction_tooltipPressed
    }, config)));
  }

  override protected function handleUnpress():void {
    inheritExpression.setValue(false);

    //restore the temporarily stored product list
    //but only if the product list is not empty
    if (productList && productList.length > 0) {
      productListExpression.setValue(productList);
    }
  }


  override protected function handlePress():void {
    inheritExpression.setValue(true);

    //we are going to override the product list with original value
    //we want to restore the product list when the button is unpressed
    //so store the product list before copying the original Product List
    productListExpression.loadValue(function():void {
      productList = productListExpression.getValue() || [];

    });

    //set the product list to the origin product list directly
    //before the value of the originProductListExpression is loaded to a non-undefined value
    productListExpression.loadValue(function():void {
      productListExpression.setValue(originProductListExpression.getValue() || []);
    });

    originProductListExpression.loadValue(function():void {
      //check if we are in inherit mode.
      //when this asynchronous callback is called the inherit could be set to false before.
      if (inheritExpression.getValue()){
        productListExpression.setValue(originProductListExpression.getValue());
      }
    });
  }

  override protected function calculateDisabled():Boolean {
    if (forceReadOnlyValueExpression && forceReadOnlyValueExpression.getValue()) {
      return true;
    }
    var formContent:Content = bindTo.getValue();
    if (formContent === undefined) {
      return undefined;
    }
    var readOnly:Boolean = PropertyEditorUtil.isReadOnly(formContent);
    if (readOnly !== false) {
      return readOnly;
    }

    return false;
  }

  override protected function calculatePressed():Boolean {
    return !!inheritExpression.getValue();
  }
}
}
