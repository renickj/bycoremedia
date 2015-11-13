package com.coremedia.ecommerce.studio.components {
import com.coremedia.cms.editor.sdk.config.premular;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.ecommerce.studio.config.commerceObjectSelector;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.form.ComboBox;
import ext.form.Field;

/**
 * The base class of the commerce objects selector combobox
 * It contains mainly the model logic to retrieve the catalog objects from the commerce system and
 * the string conversion acrobatic to ensure that the catalog object id (which looks like a number) is stored as String.
 */
public class CommerceObjectSelectorBase extends ComboBox {

  private var contentExpression:ValueExpression;
  private var selectedCatalogObjectsExpression:ValueExpression;
  private var quote:Boolean;

  public function CommerceObjectSelectorBase(config:commerceObjectSelector) {
    super(config);
    selectedCatalogObjectsExpression = config.selectedCatalogObjectsExpression;
    quote = config.quote;

    // reset the current selection if the store has been modified
    getStore().addListener('add', resetSelection);
    getStore().addListener('update', resetSelection);
    getStore().addListener('datachanged', resetSelection);
  }

  private function resetSelection():void {
    const v:* = getValue();
    if (v && getStore().findExact(valueField, unquote(v)) >= 0) {
      setValue(v);
    }
  }

  internal function getSelectableCatalogObjectsExpression(config:commerceObjectSelector):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function():Array {
      var store:Store = CatalogHelper.getInstance().getStoreForContentExpression(getContentExpression()).getValue();
      if (!store) {
        //no store defined
        markInvalid(config.noStoreMessage);
        return undefined;
      } else {
        clearInvalid();
      }

      var catalogObjectsArray:Array = config.getCommerceObjectsFunction.call(null, store) as Array;
      if (catalogObjectsArray && selectedCatalogObjectsExpression) {
        var selectedCatalogObjects:Array = selectedCatalogObjectsExpression.getValue() as Array;
        if (selectedCatalogObjects){
          catalogObjectsArray = catalogObjectsArray.filter(function(catalogObject:CatalogObject):Boolean {
            return selectedCatalogObjects.indexOf(catalogObject) < 0;
          });
        }
      }
      return catalogObjectsArray || [];
    });
  }

  private function getContentExpression():ValueExpression {
    if (!contentExpression) {
      contentExpression = ComponentContextManager.getInstance().getContextExpression(this, premular.CONTENT_VARIABLE_NAME);
    }
    return contentExpression;
  }

  override public function setValue(value:*, flag:Boolean = false):Field {
    var valueString:String = String(value);
    valueString = unquote(valueString);
    return super.setValue(valueString, flag);
  }

  private function unquote(valueString:String):String {
    if (!quote) return valueString;

    if (valueString) {
      if (valueString.indexOf('"') === 0) {
        valueString = valueString.substr(1);
      }
      if (valueString.lastIndexOf('"') === valueString.length - 1) {
        valueString = valueString.substr(0, valueString.length - 1);
      }
    }
    return valueString;
  }

  public function getUnquotedValue():String {
    return unquote(getValue());
  }
  override public function getValue():* {
    var value:* = super.getValue();
    if (!quote) return value;
    return value ? '"' + value + '"': value;
  }

}
}