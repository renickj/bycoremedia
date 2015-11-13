package com.coremedia.ecommerce.studio {
import com.coremedia.blueprint.studio.util.AjaxUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentTypeNames;
import com.coremedia.cap.content.search.SearchParameters;
import com.coremedia.cms.editor.sdk.collectionview.*;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryListContainer;
import com.coremedia.ecommerce.studio.components.repository.CatalogRepositoryToolbarContainer;
import com.coremedia.ecommerce.studio.components.search.CatalogSearchListContainer;
import com.coremedia.ecommerce.studio.components.search.CatalogSearchToolbarContainer;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Marketing;
import com.coremedia.ecommerce.studio.model.MarketingSpot;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ecommerce.studio.model.SearchResult;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.impl.RemoteServiceMethod;
import com.coremedia.ui.data.impl.RemoteServiceMethodResponse;
import com.coremedia.ui.util.ObjectUtils;

import ext.Container;

public class ECommerceCollectionViewExtension implements CollectionViewExtension {

  protected static const DEFAULT_TYPE_PRODUCT_RECORD:Object = {
    name: ContentTypeNames.DOCUMENT,
    label: ECommerceStudioPlugin_properties.INSTANCE.Product_label,
    icon: ECommerceStudioPlugin_properties.INSTANCE.Product_icon
  };

  private var toolbar:CatalogRepositoryToolbarContainer;
  private var searchToolbar:CatalogSearchToolbarContainer;
  private var listView:CatalogRepositoryListContainer;
  private var searchList:CatalogSearchListContainer;

  public function search(searchParameters:SearchParameters, callback:Function):void {
    var store:Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();

    if(store) {
      var catalogSearch:RemoteServiceMethod = new RemoteServiceMethod("livecontext/search/" + store.getSiteId(), "GET");

      //to object conversion
      var searchParams:Object = ObjectUtils.getPublicProperties(searchParameters);
      searchParams = ObjectUtils.removeUndefinedOrNullProperties(searchParams);

      catalogSearch.request(searchParams,
              function (response:RemoteServiceMethodResponse):void {
                var searchResult:SearchResult = new SearchResult();
                var responseObject:Object = response.getResponseJSON();
                searchResult.setHits(responseObject['hits']);
                searchResult.setTotal(responseObject['total']);
                callback.call(null, searchResult);
              },
              function (response:RemoteServiceMethodResponse):void {
                AjaxUtil.onErrorMethodResponse(response);
              }
      );
    }
  }

  public function getSearchOrSearchSuggestionsParameters(filters:Object, mainStateBean:Bean):SearchParameters {
    var searchText:String = mainStateBean.get(CollectionViewModel.SEARCH_TEXT_PROPERTY);
    var catalogType:String = mainStateBean.get(CollectionViewModel.CONTENT_TYPE_PROPERTY);

    var searchParameters:SearchParameters = new SearchParameters();
    var catalogObject:CatalogObject = mainStateBean.get(CollectionViewModel.FOLDER_PROPERTY);

    if (catalogObject is Category) {
      searchParameters['category'] = catalogObject.getExternalTechId() || catalogObject.getExternalId();
    }

    searchParameters.query = searchText || "*";


    if (!catalogType || catalogType === ContentTypeNames.DOCUMENT) {
      // Cannot search in 'All' catalog objects, so fall back to guessed type depending on catalogObject type:
      catalogType = (catalogObject is Marketing) ? CatalogModel.TYPE_MARKETING_SPOT : CatalogModel.TYPE_PRODUCT;
    }

    searchParameters['searchType'] = catalogType;
    searchParameters['siteId'] = editorContext.getSitesService().getPreferredSiteId();
    searchParameters['workspaceId'] = CatalogHelper.getInstance().getExtractedWorkspaceId();
    return searchParameters;
  }

  public function getSearchSuggestionsUrl():String {
    return "api/livecontext/suggestions";
  }

  public function getSearchView(searchResultHitsValueExpression:ValueExpression, selectedItemsValueExpression:ValueExpression):Container {
    if(!searchList) {
      searchList = new CatalogSearchListContainer({searchResultHitsValueExpression:searchResultHitsValueExpression,
        selectedItemsValueExpression:selectedItemsValueExpression});
    }
    return searchList;
  }

  public function getAvailableSearchTypes(folder:Object):Array {
    return [DEFAULT_TYPE_PRODUCT_RECORD];
  }

  public function getRepositoryToolbar(collectionViewModel:CollectionViewModel, creationExpression:ValueExpression):Container {
    if (!toolbar) {
      toolbar = new CatalogRepositoryToolbarContainer({
        createdContentValueExpression: creationExpression
      });
    }
    return toolbar;
  }

  public function isSearchable():Boolean {
    return true;
  }

  public function getSearchToolbar():Container {
    if(!searchToolbar) {
      searchToolbar = new CatalogSearchToolbarContainer();
    }
    return searchToolbar;
  }

  public function getSearchFilter():Container {
    return null;
  }

  public function getRepositoryView(selectionExpression:ValueExpression):Container {
    if(!listView) {
      listView = new CatalogRepositoryListContainer();
    }
    return listView;
  }

  /**
   * Since this is a common extension to be extended, it is not applicable, only subclasses of it.
   */
  public function isApplicable(model:Object):Boolean {
    return false;
  }

  public function getPathInfo(model:Object):String {
    var catalogObject:CatalogObject = model as CatalogObject;
    if (!catalogObject) {
      return "";
    }
    var namePath:Array = [];
    var store:Store = catalogObject.getStore();
    while (catalogObject) {
      namePath.push(catalogObject.getName());
      if (catalogObject is Product) {
        catalogObject = (catalogObject as Product).getCategory();
      } else if (catalogObject is Category) {
        catalogObject = (catalogObject as Category).getParent();
      } else if (catalogObject is MarketingSpot) {
        catalogObject = (catalogObject as MarketingSpot).getMarketing();
      } else {
        break;
      }
    }
    namePath.push(store.getName());
    return '/' + namePath.reverse().join('/');
  }

  public function applySearchParameters(folder:Content, filterQueryFragments:Array, searchParameters:SearchParameters):SearchParameters {
    return null;
  }
}
}