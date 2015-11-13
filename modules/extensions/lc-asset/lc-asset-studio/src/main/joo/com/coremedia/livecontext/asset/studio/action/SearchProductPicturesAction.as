package com.coremedia.livecontext.asset.studio.action {
import com.coremedia.cms.editor.sdk.actions.ActionConfigUtil;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewConstants;
import com.coremedia.cms.editor.sdk.collectionview.SearchState;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.livecontext.asset.studio.config.searchProductPicturesAction;
import com.coremedia.livecontext.studio.action.LiveContextCatalogObjectAction;

/**
 * This action is intended to be used from within EXML, only.
 *
 */
public class SearchProductPicturesAction extends LiveContextCatalogObjectAction {

  /**
   * @param config the configuration object
   */
  public function SearchProductPicturesAction(config:searchProductPicturesAction) {
    super(searchProductPicturesAction(ActionConfigUtil.extendConfiguration(ECommerceStudioPlugin_properties.INSTANCE, config, 'searchProductPictures', {handler: handler})));
  }

  override protected function isDisabledFor(catalogObjects:Array):Boolean {
    // the action should be enabled only if
    // there is only one catalog object and
    // it is a product and it has an external id
    if (catalogObjects.length !== 1) {
      return true;
    }
    var catalogObject:CatalogObject = catalogObjects[0];
    if (!(catalogObject is Product)) {
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

      //search site-based
      var preferredSite:Site = editorContext.getSitesService().getPreferredSite();

      var searchState:SearchState = new SearchState();
      searchState.searchText = product.getExternalId();
      searchState.contentType = "CMPicture";
      searchState.folder = preferredSite.getSiteRootFolder();

      editorContext.getCollectionViewManager().openSearch(searchState, true, CollectionViewConstants.LIST_VIEW);
    }
  }
}
}
