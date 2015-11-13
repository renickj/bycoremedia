package com.coremedia.ecommerce.studio {

import com.coremedia.cap.undoc.content.Content;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewManagerInternal;
import com.coremedia.cms.editor.sdk.collectionview.tree.RepositoryTreeModel;
import com.coremedia.ecommerce.studio.components.tree.impl.CatalogTreeDragDropModel;
import com.coremedia.ecommerce.studio.components.tree.impl.CatalogTreeModel;
import com.coremedia.ecommerce.studio.config.eCommerceStudioPlugin;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.models.TreeModel;

public class ECommerceStudioPluginBase extends StudioPlugin {

  public function ECommerceStudioPluginBase(config:eCommerceStudioPlugin) {
    super(config)
  }


  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);

    var collectionViewManagerInternal:CollectionViewManagerInternal =
            ((editorContext.getCollectionViewManager()) as CollectionViewManagerInternal);
    collectionViewManagerInternal.addExtension(new ECommerceCollectionViewExtension());

    var catalogTreeModel:CatalogTreeModel = new CatalogTreeModel();
    collectionViewManagerInternal.addTreeModel(catalogTreeModel,
            new CatalogTreeDragDropModel(catalogTreeModel));
  }

  internal function getShopExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():String {
      var store:Store = Store(CatalogHelper.getInstance().getActiveStoreExpression().getValue());
      return store && store.getName();
    });
  }

}
}