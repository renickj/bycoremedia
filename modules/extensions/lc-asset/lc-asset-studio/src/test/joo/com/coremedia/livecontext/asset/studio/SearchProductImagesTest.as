package com.coremedia.livecontext.asset.studio {
import com.coremedia.cap.content.impl.ContentImpl;
import com.coremedia.cms.editor.sdk.collectionview.CollectionView;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewConstants;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewContainer;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewExtension;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewManagerInternal;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.collectionview.search.SearchArea;
import com.coremedia.cms.editor.sdk.collectionview.tree.CompoundTreeModel;
import com.coremedia.cms.editor.sdk.collectionview.tree.LibraryTree;
import com.coremedia.cms.editor.sdk.collectionview.tree.TreeViewContextMenu;
import com.coremedia.cms.editor.sdk.config.collectionView;
import com.coremedia.cms.editor.sdk.config.collectionViewContainer;
import com.coremedia.cms.editor.sdk.config.treeViewContextMenu;
import com.coremedia.cms.editor.sdk.desktop.sidepanel.sidePanelManager;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.ECommerceCollectionViewExtension;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryList;
import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryListContainer;
import com.coremedia.ecommerce.studio.components.search.CatalogSearchList;
import com.coremedia.ecommerce.studio.components.search.CatalogSearchListContainer;
import com.coremedia.ecommerce.studio.components.tree.impl.CatalogTreeDragDropModel;
import com.coremedia.ecommerce.studio.components.tree.impl.CatalogTreeModel;
import com.coremedia.ecommerce.studio.config.catalogRepositoryListContainer;
import com.coremedia.ecommerce.studio.config.catalogSearchListContainer;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.livecontext.asset.studio.config.livecontextAssetStudioPlugin;
import com.coremedia.livecontext.asset.studio.config.searchProductImagesTestView;
import com.coremedia.livecontext.studio.LivecontextCollectionViewActionsPlugin;
import com.coremedia.livecontext.studio.LivecontextCollectionViewExtension;
import com.coremedia.livecontext.studio.config.livecontextStudioPlugin;
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.test.Step;

import ext.Button;
import ext.Component;
import ext.ComponentMgr;
import ext.Container;
import ext.Ext;
import ext.Toolbar;
import ext.data.Store;
import ext.grid.GridPanel;
import ext.menu.Item;

import js.HTMLElement;

public class SearchProductImagesTest extends AbstractCatalogAssetTest {
  private static const LC_CATALOG_REPOSITORY_CONTAINER:String = catalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID;
  private static const LC_CATALOG_SEARCH_LIST_CONTAINER:String = catalogSearchListContainer.VIEW_CONTAINER_ITEM_ID;

  private var viewport:SearchProductImagesTestView;
  private var testling:CollectionView;
  private var catalogTree:LibraryTree;
  private var libraryTreeModel:CompoundTreeModel = new CompoundTreeModel();
  private var catalogFooter:SwitchingContainer;
  private var openContextMenuItem:Item;
  private var searchProductVariantsContextMenuItem:Item;
  private var searchProductPicturesContextMenuItem:Item;

  private var getPreferredSite:Function;
  private var preferredSiteExpression:ValueExpression;

  override public function setUp():void {
    super.setUp();
    preferredSiteExpression = ValueExpressionFactory.create('site', beanFactory.createLocalBean({site: 'HeliosSiteId'}));
    getPreferredSite = CatalogHelper.getInstance().getPreferredSiteId;
    CatalogHelper.getInstance().getPreferredSiteId = function ():String {
      return preferredSiteExpression.getValue();
    };
  }

  private function createTestling():void {
    var collectionViewManagerInternal:CollectionViewManagerInternal =
            ((editorContext.getCollectionViewManager()) as CollectionViewManagerInternal);

    var catalogTreeModel:CatalogTreeModel = new CatalogTreeModel();
    collectionViewManagerInternal.addTreeModel(catalogTreeModel, new CatalogTreeDragDropModel(catalogTreeModel));

    var extension:CollectionViewExtension = new ECommerceCollectionViewExtension();
    collectionViewManagerInternal.addExtension(extension);

    var lcExtension:CollectionViewExtension = new LivecontextCollectionViewExtension();
    collectionViewManagerInternal.addExtension(lcExtension);

    viewport = new SearchProductImagesTestView(new searchProductImagesTestView());

    var cvContainer:CollectionViewContainer = viewport.get(collectionViewContainer.ID) as CollectionViewContainer;
    sidePanelManager['items$1'][collectionViewContainer.ID] = cvContainer;
    testling = cvContainer.get(collectionView.COLLECTION_VIEW_ID) as CollectionView;

    new LivecontextCollectionViewActionsPlugin();

    catalogTree = getTree();
    catalogFooter = getFooter();
  }

  private function getSearchArea():SearchArea {
    return SearchArea(testling.get(collectionView.SEARCH_AREA_ITEM_ID));
  }


  private function getSearchList():CatalogSearchList {
    var catalogSearch:Container = Container(getCollectionModesContainer().get(CollectionViewModel.SEARCH_MODE));
    var searchList:SwitchingContainer = SwitchingContainer(Container(catalogSearch.get("searchSwitchingContainer")));
    var searchContainer:CatalogSearchListContainer = CatalogSearchListContainer(searchList.get(LC_CATALOG_SEARCH_LIST_CONTAINER));
    //ensure type cast!!!! there are other list views too
    return searchContainer.get(CollectionViewConstants.LIST_VIEW) as CatalogSearchList;
  }

  private function getRepositoryList():CatalogRepositoryList {
    var repositorySwitchingContainer:SwitchingContainer = getRepositorySwitchingContainer();
    return CatalogRepositoryList(repositorySwitchingContainer.get(CollectionViewConstants.LIST_VIEW)) as CatalogRepositoryList;
  }



  private function getRepositorySwitchingContainer():SwitchingContainer {
    var myCatalogRepositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    var listViewSwitchingContainer:Container = Container(myCatalogRepositoryContainer.get("listViewSwitchingContainer"));
    var repositorySwitchingContainer:SwitchingContainer = SwitchingContainer(listViewSwitchingContainer.get(catalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID));
    return repositorySwitchingContainer;
  }

  private function getCollectionModesContainer():SwitchingContainer {
    return SwitchingContainer(testling.get(collectionView.COLLECTION_MODES_CONTAINER_ITEM_ID));
  }

  private function getCurrentModeContainer():Container {
    var myCatalogRepositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    var listViewSwitchingContainer:Container = Container(myCatalogRepositoryContainer.get("listViewSwitchingContainer"));
    var repositorySwitchingContainer:SwitchingContainer = SwitchingContainer(listViewSwitchingContainer.get(catalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID));
    return Container(repositorySwitchingContainer.getActiveItem());
  }

  private function getActiveToolbar():Toolbar {
    var itemId:String = getCollectionModesContainer().getActiveItem().getItemId();
    if(itemId === "repository") {
      var repoContainer:Container = testling.find("itemId", "toolbarSwitchingContainer")[0].find("itemId", "catalogRepositoryToolbar")[0];
      return repoContainer.find("itemId", "commerceToolbar")[0] as Toolbar;
    }

    var searchContainer:Container = testling.find("itemId", "searchToolbar")[0].find("itemId", "searchToolbarSwitchingContainer")[0];
    return searchContainer.find("itemId", "commerceToolbar")[0] as Toolbar;
  }


  private function getTree():LibraryTree {
    var myCatalogRepositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    return myCatalogRepositoryContainer.get(collectionView.TREE_ITEM_ID) as LibraryTree;
  }

  private function getFooter():SwitchingContainer {
    return SwitchingContainer(testling.get(collectionView.FOOTER_INFO_ITEM_ID));
  }

  override public function tearDown():void {
    super.tearDown();
    CatalogHelper.getInstance().getPreferredSiteId = getPreferredSite;
    viewport.destroy();
  }

  /**
   * Test the product images search
   */
  public function testSearchProductImages():void {
    chain(
            initStore(),
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            createTestlingStep(),
            selectStore(),
            waitUntilStoreIsSelected(),
            selectNextCatalogTreeNode(),
            waitUntilMarketingSpotsAreSelected(),
            selectNextCatalogTreeNode(),
            waitUntilProductCatalogIsSelected(),
            //wait for the product catalog node to be expanded
            waitUntilSelectedTreeNodeIsExpanded(),
            selectNextCatalogTreeNode(),
            //wait for the Apparel node to be expanded
            waitUntilSelectedTreeNodeIsExpanded(),
            selectNextCatalogTreeNode(),
            //wait for the women node to be expanded
            waitUntilSelectedTreeNodeIsExpanded(),
            selectNextCatalogTreeNode(),
            //wait for the women node to be expanded
            waitUntilSelectedTreeNodeIsExpanded(),
            selectNextCatalogTreeNode(),
            //wait for the women node to be expanded
            waitUntilSelectedTreeNodeIsExpanded(),
            selectNextCatalogTreeNode(),

            //now the Dresses node is selected
            waitUntilProductIsLoadedInRepositoryList(),//todo
            waitUntilSearchProductPicturesToolbarButtonIsInvisible(),
            openContextMenuOnFirstItemOfRepositoryList()
            //TODO fix after extension refactoring
            /*
            waitUntilRepositoryListContextMenuOpened(),
            waitUntilSearchProductPicturesToolbarButtonIsEnabled(),
            waitUntilSearchProductPicturesContextMenuIsEnabled(),
            searchProductPicturesUsingContextMenu(),
            waitUntilSearchModeIsActive(),
            waitUntilSearchTextIsPartnumber(),
            waitUntilSearchTypeIsPicture(),
            waitUntilSearchFolderIsRoot()*/
    );
  }

  private function createTestlingStep():Step {
    return new Step("Create the testling",
            function ():Boolean {
              return true;
            },
            createTestling
    );
  }

  private function initStore():Step {
    return new Step("Load Store Data",
            function ():Boolean {
              var store:Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
              return store !== null && store !== undefined;
            },
            CatalogHelper.getInstance().getActiveStoreExpression().getValue()
    );
  }

  private function waitUntilMarketingSpotsAreSelected():Step {
    return new Step("catalog tree should select the marketing root",
            function ():Boolean {
              return catalogTree.getSelectionModel().getSelectedNode() &&
                      ECommerceStudioPlugin_properties.INSTANCE.StoreTree_marketing_root === catalogTree.getSelectionModel().getSelectedNode().text;

            },
            function ():void {

            });
  }

  private function getRepositoryContainer():CatalogRepositoryList {
    var repositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    var repositorySwitch:SwitchingContainer = SwitchingContainer(Container(repositoryContainer.get("listViewSwitchingContainer")));
    var repositoryListContainer:CatalogRepositoryListContainer = CatalogRepositoryListContainer(repositorySwitch.get(catalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID));
    //ensure type cast!!!! there are other list views too
    return repositoryListContainer.get(CollectionViewConstants.LIST_VIEW) as CatalogRepositoryList;
  }

  private function selectStore():Step {
    return new Step("Select Store Node",
            function ():Boolean {
              var store:Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
              testling.setOpenPath(store);
              return getRepositoryContainer() &&  getRepositoryContainer().rendered && getRepositoryContainer().getStore();
            }
    );
  }

  private function waitUntilStoreIsSelected():Step {
    return new Step("catalog tree should select the store",
            function ():Boolean {
              return catalogTree.getSelectionModel().getSelectedNode() &&
                      "PerfectChefESite" === catalogTree.getSelectionModel().getSelectedNode().text;
            },
            function ():void {
              //nothing to do
            });
  }

  private function selectNextCatalogTreeNode():Step {
    return new Step("selecting next catalog tree node",
            function ():Boolean {
              return true;

            },
            function ():void {
              catalogTree.getSelectionModel().selectNext();
            });
  }

  private function waitUntilProductCatalogIsSelected():Step {
    return new Step("catalog tree should select the product catalog",
            function ():Boolean {
              return catalogTree.getSelectionModel().getSelectedNode() &&
                      ECommerceStudioPlugin_properties.INSTANCE.StoreTree_catalog_root === catalogTree.getSelectionModel().getSelectedNode().text;

            },
            function ():void {
              catalogTree.getSelectionModel().selectNext();
            });
  }

  private function waitUntilRepositoryListContextMenuOpened():Step {
    return new Step("Wait for the context menu on the repository list to be opened",
            function ():Boolean {
              return findCatalogRepositoryListContextMenu();
            }
    );
  }

  private function getProductPicturesSearchButton():Button {
    return Button(getActiveToolbar().find("itemId", livecontextAssetStudioPlugin.SEARCH_PRODUCT_PICTURES_BUTTON_ITEM_ID)[0]);
  }

  private function waitUntilSearchModeIsActive():Step {
    return new Step("Search Mode should be active",
            function ():Boolean {
              return getCollectionModesContainer().getActiveItemValue() === CollectionViewModel.SEARCH_MODE;
            }
    );
  }

  private function waitUntilSearchTextIsPartnumber():Step {
    return new Step("Search Text should be the part number of the product",
            function ():Boolean {
              var mainStateBean:Bean = testling.getCollectionViewModel().getMainStateBean();
              return mainStateBean.get(CollectionViewModel.SEARCH_TEXT_PROPERTY) === "AuroraWMDRS-1";
            }
    );
  }

  private function waitUntilSearchTypeIsPicture():Step {
    return new Step("Search Type should be CMPicture",
            function ():Boolean {
              var mainStateBean:Bean = testling.getCollectionViewModel().getMainStateBean();
              return mainStateBean.get(CollectionViewModel.CONTENT_TYPE_PROPERTY) === "CMPicture";
            }
    );
  }

  private function waitUntilSearchFolderIsRoot():Step {
    return new Step("Search Folder should be root",
            function ():Boolean {
              var mainStateBean:Bean = testling.getCollectionViewModel().getMainStateBean();
              var folder:ContentImpl = mainStateBean.get(CollectionViewModel.FOLDER_PROPERTY);
              return folder.getPath() === "/";
            }
    );
  }

  private function waitUntilProductIsLoadedInRepositoryList():Step {
    return new Step("Wait for the repository list to be loaded with products",
            function ():Boolean {
              return getRepositoryList().getStore().getCount() > 0 &&
                      getRepositoryList().getView().getCell(0, 0)['textContent'] === ECommerceStudioPlugin_properties.INSTANCE.Product_label;
            }
    );
  }

  private function waitUntilSelectedTreeNodeIsExpanded():Step {
    return new Step("Wait for the selected node of the catalog tree to be expanded",
            function ():Boolean {
              return catalogTree.getSelectionModel().getSelectedNode().isExpanded();
            }
    );
  }

  private function waitUntilSearchProductPicturesToolbarButtonIsInvisible():Step {
    return new Step("Wait for the product pictures search toolbar button is invisible",
            function ():Boolean {
              return getProductPicturesSearchButton().hidden;
            }
    )
  }

  private function waitUntilSearchProductPicturesToolbarButtonIsEnabled():Step {
    return new Step("Wait for the product pictures search toolbar button is enabled",
            function ():Boolean {
              return !getProductPicturesSearchButton().disabled;
            }
    )
  }

  private function waitUntilSearchProductPicturesContextMenuIsEnabled():Step {
    return new Step("Wait for the product pictures search context menu item is enabled",
            function ():Boolean {
              return !searchProductPicturesContextMenuItem.disabled;
            }
    )
  }


  private function openContextMenuOnFirstItemOfRepositoryList():Step {
    return new Step("Open Context Menu on the first item of the repository list",
            function ():Boolean {
              return true;
            },
            function ():void {
              openContextMenu(getRepositoryList(), 0);
            }
    );

  }

  private function searchProductPicturesUsingContextMenu():Step {
    return new Step("Search Product Pictures using the context menu",
            function ():Boolean {
              return true;
            },
            function ():void {
              searchProductPicturesContextMenuItem.initialConfig.handler();
            }
    );

  }

  private function openContextMenu(grid:GridPanel, row:Number):void {
    var event:Object = {
      getXY: function ():Array {
        return Ext.fly(grid.getView().getCell(row, 1)).getXY();
      },
      preventDefault: function ():void {
        //do nothing
      },
      getTarget: function():HTMLElement {
        return grid.getView().getCell(row, 1);
      }
    };
    grid.fireEvent("rowcontextmenu", grid, row, event);
  }

  private function findCatalogRepositoryListContextMenu():TreeViewContextMenu {
    var contextMenu:TreeViewContextMenu = ComponentMgr.all.find(function (component:Component):Boolean {
              return !component.ownerCt && !component.hidden && component.isXType(treeViewContextMenu.xtype);
            }) as TreeViewContextMenu;
    if (contextMenu) {
      searchProductVariantsContextMenuItem = contextMenu.getComponent(livecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_MENU_ITEM_ID) as Item;
    }

    return  contextMenu;
  }

}
}
