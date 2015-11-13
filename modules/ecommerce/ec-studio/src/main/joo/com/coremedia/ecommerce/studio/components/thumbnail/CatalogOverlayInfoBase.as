package com.coremedia.ecommerce.studio.components.thumbnail {
import com.coremedia.cms.editor.sdk.config.overlayInfo;
import com.coremedia.ecommerce.studio.config.catalogOverlayInfo;

import ext.Container;
import ext.form.Label;

public class CatalogOverlayInfoBase extends Container {
  protected static const INFO_ID_ITEM_ID:String = 'INFO_ID_ITEM_ID';
  protected static const INFO_NAME_ITEM_ID:String = 'INFO_NAME_ITEM_ID';

  public function CatalogOverlayInfoBase(config:catalogOverlayInfo) {
    super(config);
  }

  /**
   * Reset the info labels
   */
  public function reset():void {
    var id:Label = getComponent(INFO_ID_ITEM_ID) as Label;
    if (!id) {
      throw new Error(xtype + "must have a label component with item id " + INFO_ID_ITEM_ID);
    }
    id.setText('');

    var name:Label = getComponent(INFO_NAME_ITEM_ID) as Label;
    if (!name) {
      throw new Error(xtype + "must have a label component with item id " + INFO_NAME_ITEM_ID);
    }
    name.setText('');
  }
}
}