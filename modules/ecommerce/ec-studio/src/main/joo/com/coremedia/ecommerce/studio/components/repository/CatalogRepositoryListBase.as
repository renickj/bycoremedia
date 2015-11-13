package com.coremedia.ecommerce.studio.components.repository {
import com.coremedia.cms.editor.sdk.config.collectionView;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.components.AbstractCatalogList;
import com.coremedia.ecommerce.studio.config.catalogRepositoryList;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.Catalog;
import com.coremedia.ecommerce.studio.model.Marketing;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.RemoteBeanUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.EventUtil;

import ext.grid.GridPanel;

public class CatalogRepositoryListBase extends AbstractCatalogList {

  private var selectedNodeExpression:ValueExpression;
  private var sortInfo:Object;


  public function CatalogRepositoryListBase(config:catalogRepositoryList) {
    super(config);
    mon(this, 'afterrender', bindStoreAndView);
    mon(this, "containerclick", clearSelection);
  }

  private function getSelectedNodeExpression():ValueExpression {
    if (!selectedNodeExpression) {
      selectedNodeExpression = ComponentContextManager.getInstance().getContextExpression(this, collectionView.SELECTED_FOLDER_VARIABLE_NAME);
      selectedNodeExpression.addChangeListener(selectionChanged);
      addListener('destroy', function():void {
        selectedNodeExpression.removeChangeListener(selectionChanged);
      });
    }

    return selectedNodeExpression;
  }

  internal function createSelectedItemsValueExpression():ValueExpression {
    return ComponentContextManager.getInstance().getContextExpression(this, collectionView.SELECTED_REPOSITORY_ITEMS_VARIABLE_NAME);
  }

  protected function clearSelection():void {
    selectedItemsValueExpression.setValue([]);
  }

  private function selectionChanged():void {
    var value:RemoteBean = selectedNodeExpression.getValue();
    if(value is Marketing) {
      getView()['emptyText'] = ECommerceStudioPlugin_properties.INSTANCE.CatalogView_spots_selection_empty_text;
    }
    else if(value is Catalog) {
      getView()['emptyText'] = ECommerceStudioPlugin_properties.INSTANCE.CatalogView_categories_selection_empty_text;
    }
    else {
      getView()['emptyText'] = ECommerceStudioPlugin_properties.INSTANCE.CatalogView_empty_text;
    }
    getView().refresh(false);
  }

  private function bindStoreAndView():void {
    mon(this, 'sortchange', sortChanged);
    getCatalogItemsValueExpression().addChangeListener(catalogItemsChanged);
    getStore().setDefaultSort('id', 'ASC');

  }

  private function sortChanged(grid:GridPanel, sortInfo:Object):void {
    this.sortInfo = sortInfo;
    if (sortInfo.field === 'name'|| sortInfo.direction === "DESC") {
      loadCurrentBeans();
    }
  }

  private function catalogItemsChanged():void {
    if (sortInfo && (sortInfo.field === "name" || sortInfo.direction === "DESC")) {
      loadCurrentBeans();
    }
  }

  private function loadCurrentBeans():void {
    // start loading all RemoteBeans in this view...
    // and afterwards sort the Store...
    var beans:Array = getCatalogItemsValueExpression().getValue();
    if (beans && beans.length > 0) {
      RemoteBeanUtil.loadAll(function():void {
        EventUtil.invokeLater(function():void {
          getStore().sort(sortInfo.field, sortInfo.direction);
        });
      }, beans);
    }
  }

  internal function getCatalogItemsValueExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Array {
      return CatalogHelper.getInstance().getChildren(getSelectedNodeExpression().getValue());
    });
  }

}
}