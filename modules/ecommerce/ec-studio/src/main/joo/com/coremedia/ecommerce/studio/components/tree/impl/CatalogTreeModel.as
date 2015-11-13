package com.coremedia.ecommerce.studio.components.tree.impl {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.collectionview.tree.CompoundChildTreeModel;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.Catalog;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Marketing;
import com.coremedia.ecommerce.studio.model.MarketingSpot;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.models.NodeChildren;

import ext.Ext;

use namespace Ext;

public class CatalogTreeModel implements CompoundChildTreeModel {

  private var enabled:Boolean = true;
  public static const ID_PREFIX:String = "livecontext/";

  public function CatalogTreeModel() {
  }


  public function setEnabled(enabled:Boolean):void {
    this.enabled = enabled;
  }

  public function isEnabled():Boolean {
    return enabled;
  }

  public function isEditable(model:Object):Boolean {
    return false;
  }

  public function rename(model:Object, newName:String, oldName:String, callback:Function):void {
  }

  public function isRootVisible():Boolean{
    return true;
  }

  public function getRootId():String {
    if (!getStore()) {
      return null;
    }
    if(CatalogHelper.getInstance().isActiveCoreMediaStore()) {
      return null;
    }
    return getNodeId(getStore());
  }

  public function getText(nodeId:String):String {
    if (!getStore()) {
      return undefined
    }

    if (CatalogHelper.getInstance().isStoreId(nodeId)) {
      return computeStoreText();
    } else {
      var node:RemoteBean = getNodeModel(nodeId) as RemoteBean;
      if (node is Category) {
        return Category(node).getDisplayName();
      }
      else if (node is Product) {
        return Product(node).getName();
      }
      else if (node is Catalog) {
        return ECommerceStudioPlugin_properties.INSTANCE.StoreTree_catalog_root;
      }
      else if (node is Marketing) {
        return ECommerceStudioPlugin_properties.INSTANCE.StoreTree_marketing_root;
      }
      else if (node is MarketingSpot) {
        return MarketingSpot(node).getName();
      }
    }

    return undefined;
  }

  public function getIconCls(nodeId:String):String {
    return computeIconCls(nodeId, undefined);
  }

  public function getChildren(nodeId:String):NodeChildren {
    if (!getStore()) {
      return undefined
    }

    if (CatalogHelper.getInstance().isStoreId(nodeId)) {
      var store:Store = getNodeModel(nodeId) as Store;
      return getChildrenFor(store.getTopLevel(), store.getChildrenByName(), ECommerceStudioPlugin_properties.INSTANCE.Category_icon);
    }
    if (CatalogHelper.isMarketingSpot(nodeId)) {
      return new NodeChildren([], {}, {});
    }
    if (CatalogHelper.isMarketing(nodeId)) {
      return new NodeChildren([], {}, {});
    }
    if (CatalogHelper.isCatalog(nodeId)) {
      var catalog:Catalog = getNodeModel(nodeId) as Catalog;
      var topLevelCategories:Array = catalog.getTopCategories();
      return getChildrenFor(topLevelCategories, catalog.getChildrenByName(), ECommerceStudioPlugin_properties.INSTANCE.Category_icon);
    }
    var category:Category = getNodeModel(nodeId) as Category;
    var subCategories:Array = category.getSubCategories();

    preloadChildren(subCategories);
    return getChildrenFor(subCategories, category.getChildrenByName(), ECommerceStudioPlugin_properties.INSTANCE.Category_icon);
  }

  /**
   * We children are preloaded, this fixes the problem that raises for breadcrumbs:
   * If you select a leaf category the first time in the search mode, the node is not
   * found in the tree since it has been not loaded yet.
   * As a result, the BindTreeSelectionPlugin selected the default node, which is the content root.
   * @param subCategories
   */
  protected function preloadChildren(subCategories:Array):void {
    Ext.each(subCategories, function(subCategory:Category):void {
      subCategory.getChildrenByName();
    });
  }

  protected function getChildrenFor(children:Array, childrenByName:Object, iconCls:String):NodeChildren {
    if (!children) {
      return undefined;
    }
    if (!childrenByName) {
      return undefined;
    }

    var nameByChildId:Object = computeNameByChildId(childrenByName);
    var childIds:Array = [];
    var namesById:Object = {};
    var iconById:Object = {};
    for (var i:uint = 0; i < children.length; i++) {
      var childId:String = getNodeId(children[i]);
      childIds.push(childId);
      namesById[childId] = nameByChildId[childId];
      iconById[childId] = computeIconCls(childId, iconCls);
    }
    return new NodeChildren(childIds, namesById, iconById);
  }

  private function computeIconCls(childId:String, defaultIconCls:String):String {
    if(CatalogHelper.isMarketing(childId)) {
      return ECommerceStudioPlugin_properties.INSTANCE.Marketing_icon;
    }
    if(childId == getRootId()) {
      return ECommerceStudioPlugin_properties.INSTANCE.Store_icon;
    }
    return defaultIconCls;
  }

  private function computeNameByChildId(childrenByIds:Object):Object {
    var nameByUriPath:Object = {};
    for (var childId:String in childrenByIds) {
      var child:CatalogObject = childrenByIds[childId].child as CatalogObject;
      if(child is Marketing) {
        nameByUriPath[getNodeId(child)] = ECommerceStudioPlugin_properties.INSTANCE.StoreTree_marketing_root;
      }
      else if(child is Catalog) {
        nameByUriPath[getNodeId(child)] = ECommerceStudioPlugin_properties.INSTANCE.StoreTree_catalog_root;
      }
      else if (child) {
        nameByUriPath[getNodeId(child)] = childrenByIds[childId].displayName;
      }
    }
    return nameByUriPath;
  }


  /**
   * Creates an array that contains the tree path for the node with the given id.
   * @param nodeId The id to build the path for.
   * @return
   */
  public function getIdPath(nodeId:String):Array {
    if (!getStore()) {
      return undefined
    }
    return getIdPathFromModel(getNodeModel(nodeId));
  }

  public function getIdPathFromModel(model:Object):Array {
    if(!(model is CatalogObject)) {
      return null;
    }
    if (!getStore()) {
      return undefined
    }

    var path:Array = [];
    var node:RemoteBean = model as RemoteBean;
    if (node is Product) {
      node = Product(node).getCategory();
    }
    if (node is Category) {
      var category:Category = node as Category;

      while (category) {
        path.push(getNodeId(category));
        category = category.getParent();
        if (category === undefined) {
          // The path has not yet been loaded.
          return undefined;
        }
      }
      node = (node as Category).getCatalog();
    }
    if (node is MarketingSpot) {
      var m:Marketing = getStore().getMarketing();
      path.push(getNodeId(m));
    }
    if (node is Marketing) {
      var marketing:Marketing = node as Marketing;
      path.push(getNodeId(marketing));
    }
    if (node is Catalog) {
      var catalog:Catalog = node as Catalog;
      path.push(getNodeId(catalog));
    }
    path.push(getNodeId(getStore()));
    path.reverse();
    return path;
  }


  private function getStore():Store {
    return CatalogHelper.getInstance().getActiveStoreExpression().getValue();
  }

  private function computeStoreText():String {
    var workspaceName:String;
    if (getStore().getCurrentWorkspace()) {
      workspaceName = getStore().getCurrentWorkspace().getName();
    }
    return getStore().getName() + (workspaceName ? ' - ' + workspaceName : '');

  }

  public function getNodeId(model:Object):String {
    var bean:RemoteBean = (model as RemoteBean);
    if (!bean || !(bean is CatalogObject) || (bean is Product) || (bean is MarketingSpot)) {
      return null;
    }
    return bean.getUriPath();
  }

  public function getNodeModel(nodeId:String):Object {
    if (nodeId.indexOf(ID_PREFIX) != 0) {
      return null;
    }
    return beanFactory.getRemoteBean(nodeId);
  }


  public function toString():String {
    return ID_PREFIX;
  }
}
}