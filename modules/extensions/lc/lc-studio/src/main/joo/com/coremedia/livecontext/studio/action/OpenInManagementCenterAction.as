package com.coremedia.livecontext.studio.action {
import com.coremedia.cms.editor.sdk.actions.ActionConfigUtil;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.MarketingSpot;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.livecontext.studio.config.openInManagementCenterAction;
import com.coremedia.livecontext.studio.mgmtcenter.ManagementCenterUtil;

/**
 * This action is intended to be used from within EXML, only.
 *
 */
public class OpenInManagementCenterAction extends LiveContextCatalogObjectAction {

  /**
   * @param config the configuration object
   */
  public function OpenInManagementCenterAction(config:openInManagementCenterAction) {
    super(openInManagementCenterAction(ActionConfigUtil.extendConfiguration(LivecontextStudioPlugin_properties.INSTANCE, config, 'openInManagementCenter', {handler: handler})));
  }

  override protected function isDisabledFor(catalogObjects:Array):Boolean {
    if (!ManagementCenterUtil.isSupportedBrowser()) {
      return true;
    }

    //the action should be enabled only if there is only one catalog object and it is a product
    if (catalogObjects.length !== 1) {
      return true;
    }
    var catalogObject:CatalogObject = catalogObjects[0];
    if (catalogObject is Product || catalogObject is Category || catalogObject is MarketingSpot ) {
      if (catalogObject.getState().exists) {
        return false;
      }
    }
    return true;
  }

  private function handler():void {
    if (!ManagementCenterUtil.isSupportedBrowser()) {
      return;
    }
    var catalogObject:CatalogObject = getCatalogObjects()[0];
    //currently we display only products and categories
    if (catalogObject is Product) {
      ManagementCenterUtil.openProduct(Product(catalogObject));
    }  else if (catalogObject is Category) {
      ManagementCenterUtil.openCategory(Category(catalogObject));
    } else if (catalogObject is MarketingSpot) {
      ManagementCenterUtil.openMarketingSpot(MarketingSpot(catalogObject));
    }
  }
}
}
