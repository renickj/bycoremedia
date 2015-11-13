package com.coremedia.ecommerce.studio.components.search {

import com.coremedia.cms.editor.sdk.EditorContextImpl;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.config.catalogSearchField;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Ext;
import ext.IEventObject;
import ext.form.ComboBox;

public class CatalogSearchFieldBase extends ComboBox {

  /**
   * @param config the config object
   */
  public function CatalogSearchFieldBase(config:catalogSearchField) {
    super(config);
    mon(this, "beforequery", function ():Boolean {return false;});
    mon(this, "specialkey", handleSpecialKey);
    this['mimicBlur'] = Ext.emptyFn;//TODO: fix this!
  }

  internal function getSearchTextExpression():ValueExpression {
    return ValueExpressionFactory.create(CollectionViewModel.SEARCH_TEXT_PROPERTY,
           getCollectionViewModel().getMainStateBean());
  }
  private function getCollectionViewModel():CollectionViewModel {
    return EditorContextImpl(editorContext).getCollectionViewModel();
  }

  private function focusSearchField():void {
    if (getCollectionViewModel().getMainStateBean().get(CollectionViewModel.MODE_PROPERTY) === CollectionViewModel.SEARCH_MODE) {
      // For some reason, the 'blur' is needed sometimes, even if the field is no longer focused.
      this["blur"]();
      this.focus();
    }
  }

  /**
   * set the previous query. this will reload the store the next time it expands
   */
  private function clearLastQuery():void {
    lastQuery = null;
  }

  private function handleSpecialKey(_:*, e:IEventObject):void {
    if (e.getKey() === e.ENTER) {
      e.stopPropagation();
      e.preventDefault();
      getCollectionViewModel().setMode(CollectionViewModel.SEARCH_MODE);
      getCollectionViewModel().triggerSearch();
    }
  }

  override public function destroy():void {
    getCollectionViewModel().removeListener(CollectionViewModel.UPDATE_EVENT, focusSearchField);
    super.destroy();
  }
}
}
