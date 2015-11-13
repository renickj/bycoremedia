package com.coremedia.livecontext.studio.action {
import com.coremedia.cms.editor.sdk.EditorContextImpl;
import com.coremedia.cms.editor.sdk.actions.ActionConfigUtil;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewConstants;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.collectionview.SearchState;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.CatalogModel;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ecommerce.studio.model.ProductVariant;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.livecontext.studio.config.searchProductVariantsAction;

/**
 * This action is intended to be used from within EXML, only.
 *
 */
public class SearchProductVariantsAction extends LiveContextCatalogObjectAction {

  /**
   * @param config the configuration object
   */
  public function SearchProductVariantsAction(config:searchProductVariantsAction) {
    super(searchProductVariantsAction(ActionConfigUtil.extendConfiguration(LivecontextStudioPlugin_properties.INSTANCE, config, 'searchProductVariants', {handler: handler})));
  }

  override protected function isDisabledFor(catalogObjects:Array):Boolean {
    // the action should be enabled only if
    // there is only one catalog object and
    // it is a product but no product variant and
    // it has an external id
    if (catalogObjects.length !== 1) {
      return true;
    }
    var catalogObject:CatalogObject = catalogObjects[0];
    if (!(catalogObject is Product) || catalogObject is ProductVariant) {
      return true;
    }

    if (!Product(catalogObject).getExternalId()) {
      return true;
    }


    return super.isDisabledFor(catalogObjects);
  }

  override protected function isHiddenFor(catalogObjects:Array):Boolean {
    return super.isHiddenFor(catalogObjects) || isDisabledFor(catalogObjects);
  }

  private function handler():void {
    var catalogObject:CatalogObject = getCatalogObjects()[0];
    if (catalogObject is Product) {
      var product:Product = Product(catalogObject);

      var collectionViewModel:CollectionViewModel = EditorContextImpl(editorContext).getCollectionViewModel();

      var searchState:SearchState = new SearchState();
      searchState.searchText = product.getExternalId();
      searchState.contentType = CatalogModel.TYPE_PRODUCT_VARIANT;

      var selection:Object = collectionViewModel.getMainStateBean().get(CollectionViewModel.FOLDER_PROPERTY);
      collectionViewModel.setSearchState(true, CollectionViewConstants.LIST_VIEW, searchState, selection);

      editorContext.getCollectionViewManager().openSearch(searchState, false, CollectionViewConstants.LIST_VIEW);
    }
  }
}
}
