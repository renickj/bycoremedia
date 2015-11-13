package com.coremedia.livecontext.studio.desktop {
import com.coremedia.cms.editor.sdk.desktop.WorkAreaTab;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.livecontext.studio.config.commerceWorkAreaTab;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;

public class CommerceWorkAreaTabBase extends WorkAreaTab {

  public function CommerceWorkAreaTabBase(config:commerceWorkAreaTab) {
    config.entity = config.entity || config.object;
    super(config);
    getCatalogObject().addValueChangeListener(reloadPreview);
  }

  public function getCatalogObject():CatalogObject {
    return getEntity() as CatalogObject;
  }

  override protected function calculateTitle():String {
    var catalogObject:CatalogObject = getCatalogObject();
    return catalogObject && CatalogHelper.getInstance().getDecoratedName(catalogObject);
  }

  override protected function calculateIcon():String {
    var catalogObject:CatalogObject = getCatalogObject();
    return catalogObject ? CatalogHelper.getInstance().getTypeCls(catalogObject) : super.calculateIcon();
  }

  private function reloadPreview():void {
    var previewPanel:PreviewPanel = getComponent(commerceWorkAreaTab.PREVIEW_PANEL_ITEM_ID) as PreviewPanel;
    //TODO: the preview panel cannot be found sometimes
    previewPanel && previewPanel.reloadFrame();
  }
}
}