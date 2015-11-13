package com.coremedia.livecontext.studio.action {
import com.coremedia.ecommerce.studio.action.CatalogObjectAction;
import com.coremedia.ecommerce.studio.config.catalogObjectAction;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;

public class LiveContextCatalogObjectAction extends CatalogObjectAction {

  public function LiveContextCatalogObjectAction(config:catalogObjectAction ) {
    super(config);
  }

  override protected function isHiddenFor(catalogObjects:Array):Boolean {
    return CatalogHelper.getInstance().belongsToCoreMediaStore(catalogObjects) || super.isHiddenFor(catalogObjects);
  }

}
}
