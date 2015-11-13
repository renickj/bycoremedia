package com.coremedia.ecommerce.studio.forms {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.config.documentTabPanel;
import com.coremedia.cms.editor.sdk.config.premular;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.cms.editor.sdk.premular.CollapsibleFormPanel;
import com.coremedia.ecommerce.studio.components.CommerceObjectSelector;
import com.coremedia.ecommerce.studio.config.commerceCatalogObjectsSelectForm;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.dependencies.DependencyTracker;

import ext.Component;
import ext.Container;

public class CommerceCatalogObjectsSelectFormBase extends CollapsibleFormPanel{

  private var storeForContentExpression:ValueExpression;
  private var contentExpression:ValueExpression;
  private var catalogObjectsExpression:ValueExpression;

  public function CommerceCatalogObjectsSelectFormBase(config:commerceCatalogObjectsSelectForm) {
    super(config);
    contentExpression = config.bindTo;
    getStoreForContentExpression().addChangeListener(adjustLabel);
  }

  override protected function onDestroy():void {
    getStoreForContentExpression().removeChangeListener(adjustLabel);
    super.onDestroy();
  }

  override protected function afterRender():void {
    super.afterRender();
    mon(this, "add", adjustLabel);
    mon(this, "remove", adjustLabel);
    adjustLabel();
  }

  internal function getContentExpression():ValueExpression {
    if (!contentExpression) {
      contentExpression = ComponentContextManager.getInstance().getContextExpression(this, premular.CONTENT_VARIABLE_NAME);
    }
    return contentExpression;
  }

  [ProvideToExtChildren]
  public function getContent():Content {
    DependencyTracker.dependOnObservable(this, premular.CONTENT_VARIABLE_NAME);
    return getContentExpression().getValue();
  }

  internal function getCatalogObjectsExpression(config:commerceCatalogObjectsSelectForm):ValueExpression {
    if (!catalogObjectsExpression) {
      catalogObjectsExpression = CatalogHelper.getCatalogObjectsExpression(getContentExpression(),
              config.catalogObjectIdListName,
              config.invalidMessage,
              config.catalogObjectIdsExpression);
    }
    return catalogObjectsExpression;
  }

  internal function getHandleSelectFunction(config:commerceCatalogObjectsSelectForm):Function {
    return function (selector:CommerceObjectSelector, startValue:String, newValue:String):void {
      if(!newValue) return;
      CatalogHelper.addCatalogObject(getContentExpression(), config.catalogObjectIdListName, newValue, config.catalogObjectIdsExpression);
      selector.clearValue();
    };
  }

  internal static function getCatalogObjectKey(item:Bean):String {
    if (item is CatalogObject) {
      return CatalogObject(item).getUriPath();
    } else {
      //error handling: when the id is invalid then catalog object is just a bean with the id containing the invalid id
      return item.get('id');
    }
  }

  private function getStoreForContentExpression():ValueExpression {
    if (!storeForContentExpression) {
      storeForContentExpression =   CatalogHelper.getInstance().
              getStoreForContentExpression(getContentExpression());
    }
    return storeForContentExpression;
  }

  //////////custom functions to remove/add catalog object fields without touching the catalog object selector

  internal function removeCommerceObjectFields(container:Container):void {
    if (!container.items || container.items.length === 0) return;

    container.items.each(function(item:Component):void{
      //don't remove the selector and the error label
      if (item.getItemId() === commerceCatalogObjectsSelectForm.SELECTOR_CONTAINER_ITEM_ID ||
              item.getItemId() === commerceCatalogObjectsSelectForm.NO_STORE_LABEL) {
        return;
      }
      container.remove(item, true);
    });
  }

  internal function addCommerceObjectFields(container:Container, components:Array):void {
    if (!components || components.length === 0) return;
    components.forEach(function(item:Component):void{
      // the index of the selector must be computed every time
      var selectorIndex:Number = container.items.indexOf(container.getComponent(commerceCatalogObjectsSelectForm.SELECTOR_CONTAINER_ITEM_ID));
      // add the item just before the selector
      container.insert(selectorIndex, item);
    });
  }

  private function adjustLabel():void {
    if (getStoreForContentExpression().isLoaded()) {
      doAdjustLabel(getStoreForContentExpression().getValue());
    } else {
      getStoreForContentExpression().loadValue(doAdjustLabel);
    }
  }

  private function doAdjustLabel(store:Store):void {
    //show 'no store' label if no store available
    items.each(function (item:Component, index:Number):void {
      var container:Container = item as Container;
      if (container) {
        container.setVisible(store);
      }
    });
    getComponent(commerceCatalogObjectsSelectForm.NO_STORE_LABEL).setVisible(!store);
  }

}
}