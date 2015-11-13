package com.coremedia.livecontext.p13n.studio {
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.livecontext.p13n.studio.config.commerceObjectsLabel;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.form.Label;

public class CommerceObjectsLabelBase extends Label{

  private var storeForContentExpression:ValueExpression;
  private var personaContentExpression:ValueExpression;
  private var catalogObjectIdListName:String;
  private var emptyMessage:String;
  private var invalidMessage:String;

  public function CommerceObjectsLabelBase(config:commerceObjectsLabel) {
    super(config);
    catalogObjectIdListName = config.catalogObjectIdListName;
    emptyMessage = config.emptyMessage;
    invalidMessage = config.invalidMessage;

    getPersonaContentExpression().setValue(config.personaContent);
  }

  internal function getCommerceObjectsExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function():String {
      if (!getStoreForContentExpression().getValue()) {
        return LivecontextStudioPlugin_properties.INSTANCE.p13n_commerce_no_store_configured;
      }

      var catalogObjects:Array = CatalogHelper.getCatalogObjectsExpression(getPersonaContentExpression(),
              catalogObjectIdListName,
              invalidMessage).getValue();

      if (!catalogObjects || catalogObjects.length === 0) {
        return emptyMessage;
      }

      var commerceObjectsAsString:Array = catalogObjects.map(function(commerceObject:Bean):String {
        if (commerceObject is CatalogObject) {
          return CatalogObject(commerceObject).getName();
        } else {
          //error handling: when the id is invalid then catalogObject is just a bean with a name containing the error description
          return commerceObject.get("name");
        }
      });

      return commerceObjectsAsString.join(", ");

    });
  }

  private function getStoreForContentExpression():ValueExpression {
    if (!storeForContentExpression) {
      storeForContentExpression = CatalogHelper.getInstance().
              getStoreForContentExpression(getPersonaContentExpression());
    }
    return storeForContentExpression;
  }


  private function getPersonaContentExpression():ValueExpression {
    if (!personaContentExpression) {
      personaContentExpression = ValueExpressionFactory.createFromValue();
    }

    return personaContentExpression;
  }

}
}