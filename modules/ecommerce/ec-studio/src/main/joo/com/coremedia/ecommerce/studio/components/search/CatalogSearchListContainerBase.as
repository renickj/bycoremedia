package com.coremedia.ecommerce.studio.components.search {
import com.coremedia.cms.editor.sdk.EditorContextImpl;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.config.catalogSearchListContainer;
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

use namespace editorContext;

public class CatalogSearchListContainerBase extends SwitchingContainer {

  private var activeViewExpression:ValueExpression;

  public function CatalogSearchListContainerBase(config:catalogSearchListContainer) {
    super(config);
  }

  protected function getActiveItemExpression():ValueExpression {
    if(!activeViewExpression) {
      var collectionViewModel :CollectionViewModel = EditorContextImpl(editorContext).getCollectionViewModel();
      activeViewExpression = ValueExpressionFactory.create(CollectionViewModel.VIEW_PROPERTY, collectionViewModel.getMainStateBean());
    }
    return activeViewExpression;
  }
}
}