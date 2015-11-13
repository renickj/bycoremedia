package com.coremedia.ecommerce.studio.components.repository {
import com.coremedia.cms.editor.sdk.config.collectionView;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.config.catalogRepositoryThumbnails;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Container;
import ext.Ext;

use namespace editorContext;

public class CatalogRepositoryThumbnailsBase extends Container{
  private var selectedNodeExpression:ValueExpression;
  private var selectedItemsValueExpression:ValueExpression;

  public function CatalogRepositoryThumbnailsBase(config:catalogRepositoryThumbnails) {
    super(config);
  }

  internal function getCatalogItemsValueExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Array {
      return CatalogHelper.getInstance().getChildren(getSelectedNodeExpression().getValue());
    });
  }

  protected function getSelectedItemsValueExpression():ValueExpression {
    if (!selectedItemsValueExpression) {
      selectedItemsValueExpression = ComponentContextManager.getInstance().getContextExpression(this, collectionView.SELECTED_REPOSITORY_ITEMS_VARIABLE_NAME);
    }
    return selectedItemsValueExpression;
  }

  private function getSelectedNodeExpression():ValueExpression {
    if (!selectedNodeExpression) {
      selectedNodeExpression = ComponentContextManager.getInstance().getContextExpression(this, collectionView.SELECTED_FOLDER_VARIABLE_NAME);
    }

    return selectedNodeExpression;
  }

  public function disableBrowserContextMenu():void {
    var thumbViewPanel:* = this.el.down('div.catalog-thumb-data-view-panel');
    thumbViewPanel.on("contextmenu", Ext.emptyFn, null, {
      preventDefault: true
    });
  }

}
}