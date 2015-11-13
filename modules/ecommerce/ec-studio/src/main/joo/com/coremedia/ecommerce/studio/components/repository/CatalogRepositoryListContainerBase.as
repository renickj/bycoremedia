package com.coremedia.ecommerce.studio.components.repository {
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.config.collectionView;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.ecommerce.studio.config.catalogRepositoryListContainer;
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

public class CatalogRepositoryListContainerBase extends SwitchingContainer {


  public function CatalogRepositoryListContainerBase(config:catalogRepositoryListContainer) {
    super(config);
  }

  internal function getActiveViewExpression():ValueExpression {
    var collectionViewModelExpression:ValueExpression = ComponentContextManager.getInstance().getContextExpression(this, collectionView.MODEL_VARIABLE_NAME);
    return ValueExpressionFactory.createFromFunction(function():String {
      var model:CollectionViewModel = collectionViewModelExpression.getValue();
      return model ? model.getMainStateBean().get(CollectionViewModel.VIEW_PROPERTY):undefined;
    })
  }
}
}