package com.coremedia.livecontext.studio.collectionview {

import com.coremedia.cap.content.impl.ContentImpl;
import com.coremedia.cms.editor.sdk.collectionview.CollectionView;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewConstants;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewContainer;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewExtension;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewManagerInternal;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.collectionview.SearchState;
import com.coremedia.cms.editor.sdk.collectionview.search.SearchArea;
import com.coremedia.cms.editor.sdk.collectionview.tree.LibraryTree;
import com.coremedia.cms.editor.sdk.collectionview.tree.TreeViewContextMenu;
import com.coremedia.cms.editor.sdk.config.collectionView;
import com.coremedia.cms.editor.sdk.config.collectionViewContainer;
import com.coremedia.cms.editor.sdk.config.searchArea;
import com.coremedia.cms.editor.sdk.config.treeViewContextMenu;
import com.coremedia.cms.editor.sdk.desktop.sidepanel.sidePanelManager;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.CatalogModel;
import com.coremedia.ecommerce.studio.ECommerceCollectionViewExtension;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryList;
import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryListContainer;
import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryThumbnails;
import com.coremedia.ecommerce.studio.components.search.CatalogSearchContextMenu;
import com.coremedia.ecommerce.studio.components.search.CatalogSearchList;
import com.coremedia.ecommerce.studio.components.search.CatalogSearchListContainer;
import com.coremedia.ecommerce.studio.components.search.CatalogSearchThumbnails;
import com.coremedia.ecommerce.studio.components.thumbnail.CatalogDefaultOverlayBase;
import com.coremedia.ecommerce.studio.components.thumbnail.CatalogThumbDataView;
import com.coremedia.ecommerce.studio.components.tree.impl.CatalogTreeDragDropModel;
import com.coremedia.ecommerce.studio.components.tree.impl.CatalogTreeModel;
import com.coremedia.ecommerce.studio.config.catalogRepositoryListContainer;
import com.coremedia.ecommerce.studio.config.catalogSearchContextMenu;
import com.coremedia.ecommerce.studio.config.catalogSearchListContainer;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.livecontext.studio.AbstractCatalogStudioTest;
import com.coremedia.livecontext.studio.LivecontextCollectionViewActionsPlugin;
import com.coremedia.livecontext.studio.LivecontextCollectionViewExtension;
import com.coremedia.livecontext.studio.config.catalogCollectionViewTestView;
import com.coremedia.livecontext.studio.config.livecontextStudioPlugin;
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.test.Step;
import com.coremedia.ui.store.BeanRecord;

import ext.Button;
import ext.Component;
import ext.ComponentMgr;
import ext.Container;
import ext.Element;
import ext.Ext;
import ext.IEventObject;
import ext.Toolbar;
import ext.form.Label;
import ext.grid.GridPanel;
import ext.grid.RowSelectionModel;
import ext.menu.Item;

import js.HTMLElement;

public class CatalogCollectionViewTest extends AbstractCatalogStudioTest {
  private static const CATALOG_REPOSITORY_CONTAINER:String = catalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID;
  private static const CATALOG_SEARCH_LIST_CONTAINER:String = catalogSearchListContainer.VIEW_CONTAINER_ITEM_ID;

  private var viewport:CatalogCollectionViewTestView;
  private var testling:CollectionView;
  private var catalogTree:LibraryTree;
  private var catalogFooter:SwitchingContainer;
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

    viewport = new CatalogCollectionViewTestView(new catalogCollectionViewTestView());

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
    var searchContainer:CatalogSearchListContainer = CatalogSearchListContainer(searchList.get(CATALOG_SEARCH_LIST_CONTAINER));
    //ensure type cast!!!! there are other list views too
    return searchContainer.get(CollectionViewConstants.LIST_VIEW) as CatalogSearchList;
  }

  private function getRepositoryContainer():CatalogRepositoryList {
    var repositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    var repositorySwitch:SwitchingContainer = SwitchingContainer(Container(repositoryContainer.get("listViewSwitchingContainer")));
    var repositoryListContainer:CatalogRepositoryListContainer = CatalogRepositoryListContainer(repositorySwitch.get(CATALOG_REPOSITORY_CONTAINER));
    //ensure type cast!!!! there are other list views too
    return repositoryListContainer.get(CollectionViewConstants.LIST_VIEW) as CatalogRepositoryList;
  }

  private function getSearchThumbnails():CatalogSearchThumbnails {
    var catalogSearch:Container = Container(getCollectionModesContainer().get(CollectionViewModel.SEARCH_MODE));
    var searchList:SwitchingContainer = SwitchingContainer(Container(catalogSearch.get("searchSwitchingContainer")));
    var catalogSearchList:SwitchingContainer = SwitchingContainer(Container(searchList.get(CATALOG_SEARCH_LIST_CONTAINER)));
    return CatalogSearchThumbnails(catalogSearchList.get(CollectionViewConstants.THUMBNAILS_VIEW)) as CatalogSearchThumbnails;
  }


  private function getRepositorySwitchingContainer():SwitchingContainer {
    var myCatalogRepositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    var listViewSwitchingContainer:Container = Container(myCatalogRepositoryContainer.get("listViewSwitchingContainer"));
    var repositorySwitchingContainer:SwitchingContainer = SwitchingContainer(listViewSwitchingContainer.get(CATALOG_REPOSITORY_CONTAINER));
    return repositorySwitchingContainer;
  }

  private function getRepositoryList():CatalogRepositoryList {
    var repositorySwitchingContainer:SwitchingContainer = getRepositorySwitchingContainer();
    return CatalogRepositoryList(repositorySwitchingContainer.get(CollectionViewConstants.LIST_VIEW)) as CatalogRepositoryList;
  }

  private function getRepositoryThumbnails():CatalogRepositoryThumbnails {
    var myCatalogRepositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    var listViewSwitchingContainer:Container = Container(myCatalogRepositoryContainer.get("listViewSwitchingContainer"));
    var repositorySwitchingContainer:Container = Container(listViewSwitchingContainer.get(CATALOG_REPOSITORY_CONTAINER));
    return CatalogRepositoryThumbnails(repositorySwitchingContainer.get(CollectionViewConstants.THUMBNAILS_VIEW)) as CatalogRepositoryThumbnails;
  }

  private function getSearchThumbDataView():CatalogThumbDataView {
    return Container(getSearchThumbnails().get(CatalogSearchThumbnails.THUMB_DATA_VIEW_PANEL_ITEM_ID))
            .get(CatalogSearchThumbnails.THUMB_DATA_VIEW_ITEM_ID) as CatalogThumbDataView;
  }

  private function getRepositoryThumbDataView():CatalogThumbDataView {
    return Container(getRepositoryThumbnails().get(CatalogRepositoryThumbnails.THUMB_DATA_VIEW_PANEL_ITEM_ID))
            .get(CatalogRepositoryThumbnails.THUMB_DATA_VIEW_ITEM_ID) as CatalogThumbDataView;
  }

  private function getCollectionModesContainer():SwitchingContainer {
    return SwitchingContainer(testling.get(collectionView.COLLECTION_MODES_CONTAINER_ITEM_ID));
  }

  private function getCurrentModeContainer():Container {
    var myCatalogRepositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    var listViewSwitchingContainer:Container = Container(myCatalogRepositoryContainer.get("listViewSwitchingContainer"));
    var repositorySwitchingContainer:SwitchingContainer = SwitchingContainer(listViewSwitchingContainer.get(CATALOG_REPOSITORY_CONTAINER));
    return Container(repositorySwitchingContainer.getActiveItem());
  }

  private function getCurrentViewContainer():SwitchingContainer {
    return SwitchingContainer(Container(getCurrentModeContainer().get(CatalogRepositoryListContainer.VIEW_CONTAINER_ITEM_ID)));
  }

  private function getTree():LibraryTree {
    var myCatalogRepositoryContainer:Container = Container(getCollectionModesContainer().get(CollectionViewModel.REPOSITORY_MODE));
    return myCatalogRepositoryContainer.get(collectionView.TREE_ITEM_ID) as LibraryTree;
  }

  private function getFooter():SwitchingContainer {
    return SwitchingContainer(testling.get(collectionView.FOOTER_INFO_ITEM_ID));
  }

  private function getFooterTotalHitsLabel():Label {
    return Label(getFooter().get("totalHitsLabel"));
  }

  override public function tearDown():void {
    super.tearDown();
    CatalogHelper.getInstance().getPreferredSiteId = getPreferredSite;
    viewport.destroy();
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

  private function selectStore():Step {
    return new Step("Select Store Node",
            function ():Boolean {
              var store:Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
              testling.setOpenPath(store);
              return getRepositoryContainer() &&  getRepositoryContainer().rendered && getRepositoryContainer().getStore();
            }
    );
  }


  public function testCategoryRepositoryThumbnail():void {
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

            waitUntilSwitchToListButtonIsPressed(),
            switchToThumbnailView(),
            waitUntilSwitchToListButtonIsUnpressed(),
            waitUntilThumbnailViewIsActive(),
            waitUntilCategoriesAreLoadedAsThumbnails(),
            triggerMouseEnterOnRepositoryThumbnailNode(0),
            waitUntilFirstRepositoryCategoryThumbnailOverlayIsLoaded(),
            triggerMouseEnterOnRepositoryThumbnailNode(1),
            waitUntilSecondRepositoryCategoryThumbnailOverlayIsLoaded()
    );
  }

  public function testMarketingSpotRepositoryThumbnail():void {
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

            waitUntilSwitchToListButtonIsPressed(),
            switchToThumbnailView(),
            waitUntilSwitchToListButtonIsUnpressed(),
            waitUntilThumbnailViewIsActive(),
            waitUntilMarketingSpotsAreLoadedAsThumbnails(),
            triggerMouseEnterOnRepositoryThumbnailNode(0),
            waitUntilFirstRepositoryMarketingSpotThumbnailOverlayIsLoaded(),
            triggerMouseEnterOnRepositoryThumbnailNode(1),
            waitUntilSecondRepositoryMarketingSpotThumbnailOverlayIsLoaded()
    );
  }


  /**
   * Test the 'search product variants' toolbar button and context menu item
   */
  public function testProductVariantSearchFromProduct():void {
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
            waitUntilProductIsLoadedInRepositoryList(),
            waitUntilSearchProductVariantToolbarButtonIsInvisible(),
            openContextMenuOnFirstItemOfRepositoryList()
            /*todo aga fix this
            waitUntilRepositoryListContextMenuOpened(),
            waitUntilSearchProductVariantToolbarButtonIsEnabled(),
            waitUntilSearchProductVariantContextMenuIsEnabled(),
            searchProductVariantsUsingContextMenu(),
            waitUntilSearchModeIsActive(),
            waitUntilProductVariantIsLoadedInSearchList(),
            waitUntilCatalogSearchListIsLoadedAndNotEmpty(2, HERMITAGE_RUCHED_BODICE_COCKTAIL_DRESS),
            //now test that the variant search is disabled on product variants themselves
            openContextMenuOnFirstItemOfSearchList(),
            waitUntilSearchListContextMenuOpened(),
            waitUntilSearchProductVariantToolbarButtonIsDisabled(),
            waitUntilSearchProductVariantContextMenuIsDisabled()
             */

    );
  }

  public function testProductVariantFooterTotalHits():void {
    chain(
            initStore(),
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            createTestlingStep(),
            selectStore(),
            triggerSearch("Oranges", CatalogModel.TYPE_PRODUCT_VARIANT),
            waitUntilCatalogSearchListAndLabelIsLoadedAndFooterShowsTotalHits(3)
    );
  }


  public function testCatalogSearchListLoaded():void {
    chain(
            initStore(),
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            createTestlingStep(),
            selectStore(),
            triggerSearch("Oranges", CatalogModel.TYPE_PRODUCT),
            waitUntilSearchModeIsActive(),
            waitUntilCatalogSearchListIsLoadedAndNotEmpty(2, ORANGES_NAME)
    );
  }

  public function testProductFooterTotalHits():void {
    chain(
            initStore(),
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            createTestlingStep(),
            selectStore(),
            triggerSearch("Oranges", CatalogModel.TYPE_PRODUCT),
            waitUntilCatalogSearchListAndLabelIsLoadedAndFooterShowsTotalHits(2)
    );
  }

  public function testSimpleProductVariantSearch():void {
    chain(
            initStore(),
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            createTestlingStep(),
            selectStore(),
            triggerSearch("Oranges", CatalogModel.TYPE_PRODUCT_VARIANT),
            waitUntilSearchModeIsActive(),
            waitUntilCatalogSearchListIsLoadedAndNotEmpty(3, ORANGES_SKU_NAME)
    );
  }

  public function testProductSearchThumbnail():void {
    chain(
            initStore(),
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            createTestlingStep(),
            selectStore(),
            triggerSearch("Oranges", CatalogModel.TYPE_PRODUCT),
            waitUntilSearchModeIsActive(),
            waitUntilSwitchToListButtonIsPressed(),
            switchToThumbnailView(),
            waitUntilSwitchToListButtonIsUnpressed(),
            waitUntilThumbnailViewIsActive(),
            waitUntilProductSearchResultIsLoadedAsThumbnails(),
            triggerMouseEnterOnSearchThumbnailNode(0),
            waitUntilFirstProductSearchThumbnailOverlayIsLoaded(),
            triggerMouseEnterOnSearchThumbnailNode(1),
            waitUntilSecondProductSearchThumbnailOverlayIsLoaded()
    );
  }

  public function testProductVariantSearchThumbnail():void {
    chain(
            initStore(),
            loadContentRepository(),
            waitForContentRepositoryLoaded(),
            loadContentTypes(),
            waitForContentTypesLoaded(),
            createTestlingStep(),
            selectStore(),
            triggerSearch("Oranges", CatalogModel.TYPE_PRODUCT_VARIANT),
            waitUntilSearchModeIsActive(),
            waitUntilSwitchToListButtonIsPressed(),
            switchToThumbnailView(),
            waitUntilSwitchToListButtonIsUnpressed(),
            waitUntilThumbnailViewIsActive(),
            waitUntilProductVariantSearchResultIsLoadedAsThumbnails(),
            triggerMouseEnterOnSearchThumbnailNode(0),
            waitUntilFirstProductVariantSearchThumbnailOverlayIsLoaded(),
            triggerMouseEnterOnSearchThumbnailNode(1),
            waitUntilSecondProductVariantSearchThumbnailOverlayIsLoaded(),
            triggerMouseEnterOnSearchThumbnailNode(2),
            waitUntilThirdProductVariantSearchThumbnailOverlayIsLoaded()
    );
  }

  private function waitUntilCatalogSearchListIsLoaded():Step {
    return new Step("catalog search list should be loaded",
            function ():Boolean {
              return getSearchList() && getSearchList().rendered && getSearchList().getStore();
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilStoreIsSelected():Step {
    return new Step("catalog tree should select the store",
            function ():Boolean {
              return catalogTree.getSelectionModel().getSelectedNode() &&
                      "PerfectChefESite" === catalogTree.getSelectionModel().getSelectedNode().text;
            },
            function ():void {
              var button:Button = getSearchArea().find("itemId", searchArea.SWITCH_BUTTON_CONTAINER_ITEM_ID)[0].find("itemId", searchArea.SWITCH_TO_REPOSITORY_BUTTON_ITEM_ID)[0];
              button.initialConfig.handler();
            });
  }

  private function selectRepositoryMode():Step {
    return new Step("selecting repository mode",
            function ():Boolean {
              var button:Button = getSearchArea().find("itemId", searchArea.SWITCH_BUTTON_CONTAINER_ITEM_ID)[0].find("itemId", searchArea.SWITCH_TO_REPOSITORY_BUTTON_ITEM_ID)[0];
              button.initialConfig.handler();
              return true;

            },
            function ():void {

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

  private function waitUntilMarketingSpotsAreSelected():Step {
    return new Step("catalog tree should select the marketing root",
            function ():Boolean {
              return catalogTree.getSelectionModel().getSelectedNode() &&
                     ECommerceStudioPlugin_properties.INSTANCE.StoreTree_marketing_root === catalogTree.getSelectionModel().getSelectedNode().text;

            },
            function ():void {

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

  private function triggerSearch(searchTerm:String, searchType:String):Step {
    return new Step("trigger catalog search",
            function ():Boolean {
              return true;
            },
            function ():void {
              setSearchStateAndTriggerSearch(searchTerm, searchType);
            });
  }

  private function triggerMouseEnterOnSearchThumbnailNode(nodeIndex:Number):Step {
    return new Step("trigger mouse enter on the search thumbnail node " + nodeIndex,
            function ():Boolean {
              return true;
            },
            function ():void {
              getSearchThumbDataView().fireEvent('mouseenter', getSearchThumbDataView(), nodeIndex,
                      getSearchThumbDataView().getNode(nodeIndex));
            });
  }

  private function triggerMouseEnterOnRepositoryThumbnailNode(nodeIndex:Number):Step {
    return new Step("trigger mouse enter on the repository thumbnail node " + nodeIndex,
            function ():Boolean {
              return true;
            },
            function ():void {
              getRepositoryThumbDataView().fireEvent('mouseenter', getRepositoryThumbDataView(), nodeIndex,
                      getRepositoryThumbDataView().getNode(nodeIndex));
            });
  }

  private function waitUntilSwitchToListButtonIsPressed():Step {
    return new Step("Switch to List Button should be pressed",
            function ():Boolean {
              return getSwitchToListViewButton() && getSwitchToListViewButton().pressed;
            });
  }

  private function waitUntilRepositoryListContextMenuOpened():Step {
    return new Step("Wait for the context menu on the repository list to be opened",
            function ():Boolean {
              return findCatalogRepositoryListContextMenu();
            }
    );
  }

  private function waitUntilSearchListContextMenuOpened():Step {
    return new Step("Wait for the context menu on the search list to be opened",
            function ():Boolean {
              return findCatalogSearchListContextMenu();
            }
    );
  }

  private function getSwitchToSearchModeButton():Button {
    return Button(getSearchArea().find("itemId", searchArea.SWITCH_TO_SEARCH_BUTTON_ITEM_ID)[0]);
  }

  private function getSwitchToListViewButton():Button {
    return Button(getActiveToolbarViewSwitch().find("itemId", "list")[0]);
  }

  private function getSwitchToThumbnailViewButton():Button {
    return Button(getActiveToolbarViewSwitch().find("itemId", "thumb")[0]);
  }

  private function getProductVariantSearchButton():Button {
    return Button(getActiveToolbar().find("itemId", livecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_BUTTON_ITEM_ID)[0]);
  }

  private function getActiveToolbarViewSwitch():Container {
    var itemId:String = getCollectionModesContainer().getActiveItem().getItemId();
    var container:Container = undefined;
    if(itemId === "repository") {
      container = testling.find("itemId", "toolbarSwitchingContainer")[0].find("itemId", "catalogRepositoryToolbar")[0];
    }
    else {
      container = testling.find("itemId", "searchToolbar")[0].find("itemId", "searchToolbarSwitchingContainer")[0];
    }

    return container.find("itemId", "switchButtonsContainer")[0];
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

  private function waitUntilSwitchToListButtonIsUnpressed():Step {
    return new Step("Switch to List Button should be unpressed",
            function ():Boolean {
              return !getSwitchToListViewButton().pressed;
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilThumbnailViewIsActive():Step {
    return new Step("Thumbnailview should be active",
            function ():Boolean {
              return getRepositorySwitchingContainer().getActiveItemValue() === CollectionViewConstants.THUMBNAILS_VIEW;
            },
            function ():void {
              //nothing to do
            });
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
              return folder.getNumericId() == 0;
            }
    );
  }

  private function waitUntilProductIsLoadedInRepositoryList():Step {
    return new Step("Wait for the repository list to be loaded with products",
            function ():Boolean {
              return getRepositoryList().getStore().getCount() > 0 &&
                      getRepositoryList().getView().getCell(0,0)['textContent'] === ECommerceStudioPlugin_properties.INSTANCE.Product_label;
            }
    );
  }

  private function waitUntilProductVariantIsLoadedInSearchList():Step {
    return new Step("Wait for the search list to be loaded with product variants",
            function ():Boolean {
              return getSearchList().getStore().getCount() > 0 &&
                      getSearchList().getView().getCell(0,0)['textContent'] === ECommerceStudioPlugin_properties.INSTANCE.ProductVariant_label;
            }
    );
  }

  private function waitUntilProductSearchResultIsLoadedAsThumbnails():Step {
    return new Step("Product search results should be loaded as thumbnails",
            function ():Boolean {
              var html0:String = getSearchThumbDataView().getNodes().length === 2 &&
                      getSearchThumbDataView().getNode(0).innerHTML;
              var html1:String = getSearchThumbDataView().getNodes().length === 2 &&
                      getSearchThumbDataView().getNode(1).innerHTML;
              return html0 && html1 &&
                      html0.indexOf(ORANGES_EXTERNAL_ID) > 0 &&
                //the first node has a image
                      html0.indexOf("src=\"" + ORANGES_IMAGE_URI + "\"") > 0 &&
                      html1.indexOf('BSH016_1605') > 0 &&
                //the second node has no image - a icon defined in css should be shown
                      html1.indexOf("class=\"content-type-l product-icon\"") > 0;
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilProductPicturesAreLoadedInSearchList():Step {
    return new Step("Wait for the search list to be loaded with product pictures",
            function ():Boolean {
              return getSearchList().getStore().getCount() > 0 &&
                      getSearchList().getView().getCell(0,0)['textContent'] === ECommerceStudioPlugin_properties.INSTANCE.ProductVariant_label;
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

  private function waitUntilProductVariantSearchResultIsLoadedAsThumbnails():Step {
    return new Step("Product Variant search results should be loaded as thumbnails",
            function ():Boolean {
              var html0:String = getSearchThumbDataView().getNodes().length === 3 &&
                      getSearchThumbDataView().getNode(0).innerHTML;
              var html1:String = getSearchThumbDataView().getNodes().length === 3 &&
                      getSearchThumbDataView().getNode(1).innerHTML;
              var html2:String = getSearchThumbDataView().getNodes().length === 3 &&
                      getSearchThumbDataView().getNode(2).innerHTML;
              return html0 && html1 && html2 &&
                      html0.indexOf(ORANGES_SKU_EXTERNAL_ID) > 0 &&
                //the first and the second node have a image
                      html0.indexOf("src=\"" + ORANGES_IMAGE_URI + "\"") > 0 &&
                      html1.indexOf(ORANGES_EXTERNAL_ID + "02") > 0 &&
                      html1.indexOf("src=\"" + ORANGES_IMAGE_URI + "\"") > 0 &&
                      html2.indexOf(ORANGES_EXTERNAL_ID + "03") > 0 &&
                //the third node has no image - a icon defined in css should be shown
                      html2.indexOf("class=\"content-type-l sku-icon\"") > 0;
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilCategoriesAreLoadedAsThumbnails():Step {
    return new Step("Categories should be loaded as thumbnails",
            function ():Boolean {
              var html0:String = getRepositoryThumbDataView().getNodes().length === 3 &&
                      getRepositoryThumbDataView().getNode(0).innerHTML;
              var html1:String = getRepositoryThumbDataView().getNodes().length === 3 &&
                      getRepositoryThumbDataView().getNode(1).innerHTML;
              var html2:String = getRepositoryThumbDataView().getNodes().length === 3 &&
                      getRepositoryThumbDataView().getNode(2).innerHTML;
              return html0 && html1 && html2 &&
                      html0.indexOf('Apparel') > 0 && html1.indexOf('Grocery') > 0 &&
                //categories have no images - a icon defined in css should be shown
                      html0.indexOf("class=\"content-type-l category-icon\"") > 0 &&
                      html1.indexOf("class=\"content-type-l category-icon\"") > 0 &&
                      html2.indexOf("class=\"content-type-l category-icon\"") > 0;
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilFirstProductSearchThumbnailOverlayIsLoaded():Step {
    return new Step("First product search thumbnail overlay should be loaded",
            function ():Boolean {
              var activeOverlay:CatalogDefaultOverlayBase = getSearchThumbDataView().getDetailView().getActiveOverlay();
              var html:String = getHtml(activeOverlay);
              return html && html.indexOf(ORANGES_EXTERNAL_ID) > 0 &&
                      html.indexOf(ORANGES_NAME) > 0 &&
                //the first node has a image
                      html.indexOf("src=\"" + ORANGES_IMAGE_URI + "\"") > 0
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilMarketingSpotsAreLoadedAsThumbnails():Step {
    return new Step("Spots should be loaded as thumbnails",
            function ():Boolean {
              var html0:String = getRepositoryThumbDataView().getNodes().length === 3 &&
                      getRepositoryThumbDataView().getNode(0).innerHTML;
              var html1:String = getRepositoryThumbDataView().getNodes().length === 3 &&
                      getRepositoryThumbDataView().getNode(1).innerHTML;
              var html2:String = getRepositoryThumbDataView().getNodes().length === 3 &&
                      getRepositoryThumbDataView().getNode(2).innerHTML;
              return html0 && html1 && html2 &&
                //categories have no images - a icon defined in css should be shown
                      html0.indexOf("class=\"content-type-l marketingspot-icon\"") > 0 &&
                      html1.indexOf("class=\"content-type-l marketingspot-icon\"") > 0 &&
                      html2.indexOf("class=\"content-type-l marketingspot-icon\"") > 0;
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilSecondProductSearchThumbnailOverlayIsLoaded():Step {
    return new Step("Second product search thumbnail overlay should be loaded",
            function ():Boolean {
              var activeOverlay:CatalogDefaultOverlayBase = getSearchThumbDataView().getDetailView().getActiveOverlay();
              return domContainsText(activeOverlay, 'BSH016_1605', 'Borsati Orange') &&
                      domContainsImgWithCls(activeOverlay, 'content-type-xl', 'product-icon');
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilFirstProductVariantSearchThumbnailOverlayIsLoaded():Step {
    return new Step("First product variant search thumbnail overlay should be loaded",
            function ():Boolean {
              var activeOverlay:CatalogDefaultOverlayBase = getSearchThumbDataView().getDetailView().getActiveOverlay();
              var html:String = getHtml(activeOverlay);
              return html && html.indexOf(ORANGES_SKU_EXTERNAL_ID) > 0 &&
                      html.indexOf(ORANGES_NAME) > 0 &&
                      html.indexOf("src=\"" + ORANGES_IMAGE_URI + "\"") > 0
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilSecondProductVariantSearchThumbnailOverlayIsLoaded():Step {
    return new Step("Second product variant search thumbnail overlay should be loaded",
            function ():Boolean {
              var activeOverlay:CatalogDefaultOverlayBase = getSearchThumbDataView().getDetailView().getActiveOverlay();
              var html:String = getHtml(activeOverlay);
              return html && html.indexOf(ORANGES_EXTERNAL_ID + "02") > 0 &&
                      html.indexOf(ORANGES_NAME) > 0 &&
                      html.indexOf("src=\"" + ORANGES_IMAGE_URI + "\"") > 0
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilThirdProductVariantSearchThumbnailOverlayIsLoaded():Step {
    return new Step("Third product variant search thumbnail overlay should be loaded",
            function ():Boolean {
              var activeOverlay:CatalogDefaultOverlayBase = getSearchThumbDataView().getDetailView().getActiveOverlay();
              return domContainsText(activeOverlay, ORANGES_EXTERNAL_ID + "03", ORANGES_NAME) &&
                      domContainsImgWithCls(activeOverlay, 'content-type-xl', 'sku-icon');
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilFirstRepositoryCategoryThumbnailOverlayIsLoaded():Step {
    return new Step("First Repository thumbnail overlay should be loaded",
            function ():Boolean {
              var activeOverlay:CatalogDefaultOverlayBase = getRepositoryThumbDataView().getDetailView().getActiveOverlay();
              return domContainsText(activeOverlay, 'Apparel') &&
                      domContainsImgWithCls(activeOverlay, 'content-type-xl', 'category-icon');
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilFirstRepositoryMarketingSpotThumbnailOverlayIsLoaded():Step {
    return new Step("First Repository thumbnail overlay should be loaded",
            function ():Boolean {
              var activeOverlay:CatalogDefaultOverlayBase = getRepositoryThumbDataView().getDetailView().getActiveOverlay();
              return domContainsImgWithCls(activeOverlay, 'content-type-xl', 'marketingspot-icon');
            },
            function ():void {
              //nothing to do
            });
  }

  private function waitUntilSecondRepositoryCategoryThumbnailOverlayIsLoaded():Step {
    return new Step("Second Repository thumbnail overlay should be loaded",
            function ():Boolean {
              var activeOverlay:CatalogDefaultOverlayBase = getRepositoryThumbDataView().getDetailView().getActiveOverlay();
              return domContainsText(activeOverlay, 'Grocery') &&
                      domContainsImgWithCls(activeOverlay, 'content-type-xl', 'category-icon');
            });
  }

  private function waitUntilSecondRepositoryMarketingSpotThumbnailOverlayIsLoaded():Step {
    return new Step("Second Repository thumbnail overlay should be loaded",
            function ():Boolean {
              var activeOverlay:CatalogDefaultOverlayBase = getRepositoryThumbDataView().getDetailView().getActiveOverlay();
              return domContainsImgWithCls(activeOverlay, 'content-type-xl', 'marketingspot-icon');
            });
  }

  private static function domContainsText(comp:Component, ...text):Boolean {
    var el:Element = comp && comp.getEl();
    if (!el) {
      return false;
    }

    for (var i:int = 0; i < text.length; i++) {
      var currentText:String = text[i];
      if (el.dom['textContent'].indexOf(currentText) === -1) {
        return false;
      }
    }

    return true;
  }

  private static function domContainsImgWithCls(comp:Component, ...cls):Boolean {
    var el:Element = comp && comp.getEl();
    if (!el) {
      return false;
    }

    for (var i:int = 0; i < cls.length; i++) {
      var currentCls:String = cls[i];
      if (!el.child('img').hasClass(currentCls)) {
        return false;
      }
    }

    return true;
  }

  private function waitUntilSearchProductVariantToolbarButtonIsInvisible():Step {
    return new Step("Wait for the product variant search toolbar button is invisible",
            function ():Boolean {
              return getProductVariantSearchButton().hidden;
            }
    )
  }

  private function waitUntilSearchProductVariantToolbarButtonIsEnabled():Step {
    return new Step("Wait for the product variant search toolbar button is enabled",
            function ():Boolean {
              return !getProductVariantSearchButton().disabled;
            }
    )
  }

  private function waitUntilSearchProductVariantContextMenuIsEnabled():Step {
    return new Step("Wait for the product variant search context menu item is enabled",
            function ():Boolean {
              return !searchProductVariantsContextMenuItem.disabled;
            }
    )
  }

  private function waitUntilSearchProductVariantContextMenuIsDisabled():Step {
    return new Step("Wait for the product variant search context menu item is disabled",
            function ():Boolean {
              return searchProductVariantsContextMenuItem.disabled;
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

  private function waitUntilSearchProductPicturesContextMenuIsDisabled():Step {
    return new Step("Wait for the product pictures search context menu item is disabled",
            function ():Boolean {
              return searchProductPicturesContextMenuItem.disabled;
            }
    )
  }

  private function switchToThumbnailView():Step {
    return new Step("Switch to thumbnail view",
            function ():Boolean {
              return true;
            },
            function ():void {
              getSwitchToThumbnailViewButton().initialConfig.handler();
            });
  }

  private function switchToSearchMode():Step {
    return new Step("Switch to search mode",
            function ():Boolean {
              return true;
            },
            function ():void {
              getSwitchToSearchModeButton().initialConfig.handler();
            });
  }

  private function waitUntilCatalogSearchListIsLoadedAndNotEmpty(expectedResultCount:int, firstItemName:String):Step {
    return new Step("Wait for the catalog search list to be loaded and the search items to be " + expectedResultCount +
            " and the first item to be " + firstItemName,
            function ():Boolean {
              if (getSearchList().getStore() && getSearchList().getStore().getCount() <= 0) {
                return false;
              }
              var name:String = CatalogObject(BeanRecord(getSearchList().getStore().getAt(0)).getBean()).getName();
              return firstItemName === name && expectedResultCount === getSearchList().getStore().getCount();
            }
    );
  }

  private function waitUntilCatalogSearchListAndLabelIsLoadedAndFooterShowsTotalHits(expectedResultCount:int):Step {
    return new Step("footer and catalog search list should be loaded and must not be empty",
            function ():Boolean {
              var footerTotalHitsLabel:Label = getFooterTotalHitsLabel();
              var searchList:CatalogSearchList = getSearchList();
              return footerTotalHitsLabel && searchList.getStore() && searchList.getStore().getCount() > 0 &&
                      footerTotalHitsLabel.text && footerTotalHitsLabel.text.indexOf(String(expectedResultCount)) === 0;

            },
            function ():void {
              //nothing to do
            });
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

  private function openContextMenuOnFirstItemOfSearchList():Step {
    return new Step("Open Context Menu on the first item of the searhc list",
            function ():Boolean {
              return true;
            },
            function ():void {
              openContextMenu(getSearchList(), 0);
            }
    );

  }

  private function searchProductVariantsUsingContextMenu():Step {
    return new Step("Search Product Variants using the context menu",
            function ():Boolean {
              return true;
            },
            function ():void {
              searchProductVariantsContextMenuItem.initialConfig.handler();
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
    var event:IEventObject = {
      getXY: function():Array {
        return Ext.fly(event.getTarget()).getXY();
      },
      preventDefault : function():void{
        //do nothing
      },
      getTarget: function():HTMLElement {
        return grid.getView().getCell(row, 1);
      }
    };
    var sm:RowSelectionModel = grid.getSelectionModel() as RowSelectionModel;
    sm.selectRow(row);
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

  private function findCatalogSearchListContextMenu():CatalogSearchContextMenu {
    var contextMenu:CatalogSearchContextMenu = ComponentMgr.all.find(function (component:Component):Boolean {
      return !component.ownerCt && !component.hidden && component.isXType(catalogSearchContextMenu.xtype);
    }) as CatalogSearchContextMenu;
    if (contextMenu) {
      searchProductVariantsContextMenuItem = contextMenu.getComponent(livecontextStudioPlugin.SEARCH_PRODUCT_VARIANTS_MENU_ITEM_ID) as Item;
    }

    return  contextMenu;
  }

  private function setSearchStateAndTriggerSearch(searchTerm:String, searchType:String):void {
    var searchState:SearchState = new SearchState();
    searchState.searchText = searchTerm;
    searchState.contentType = searchType;
    searchState.folder = CatalogHelper.getInstance().getActiveStoreExpression().getValue();

    editorContext.getCollectionViewManager().openSearch(searchState, true, CollectionViewConstants.LIST_VIEW);
  }

  private function getHtml(component:Component):String {
    return component && component.getEl() && component.getEl().dom &&
            component.getEl().dom.innerHTML;

  }
}
}
