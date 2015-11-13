package com.coremedia.ecommerce.studio.helper {

import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.EditorContextImpl;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewManagerInternal;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.ecommerce.studio.CatalogModel;
import com.coremedia.ecommerce.studio.ECommerceStudioPlugin_properties;
import com.coremedia.ecommerce.studio.model.Catalog;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Contracts;
import com.coremedia.ecommerce.studio.model.Marketing;
import com.coremedia.ecommerce.studio.model.MarketingSpot;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ecommerce.studio.model.ProductAttribute;
import com.coremedia.ecommerce.studio.model.ProductVariant;
import com.coremedia.ecommerce.studio.model.Segments;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.data.error.NotExistsError;
import com.coremedia.ui.data.error.RemoteError;
import com.coremedia.ui.data.impl.RemoteErrorHandlerRegistryImpl;

import ext.util.StringUtil;

public class CatalogHelper {

  private static const PROFILE_EXTENSIONS:String = 'profileExtensions';
  private static const PROPERTIES:String = 'properties';
  private static const LOCAL_SETTINGS_STRUCT_NAME:String = 'localSettings';
  private static const COMMERCE_STRUCT_NAME:String = 'commerce';
  private static const PRODUCTS_LIST_NAME:String = 'products';
  public static const USER_SEGMENTS:String = 'usersegments';
  public static const USER_CONTRACTS:String = 'usercontracts';

  public static const NO_WS:String = CatalogModel.NO_WS;

  private static const TYPE_CATEGORY:String = CatalogModel.TYPE_CATEGORY;
  private static const TYPE_CATALOG:String = CatalogModel.TYPE_CATALOG;
  private static const TYPE_MARKETING:String = CatalogModel.TYPE_MARKETING;
  public static const TYPE_PRODUCT:String = CatalogModel.TYPE_PRODUCT;
  public static const TYPE_PRODUCT_VARIANT:String = CatalogModel.TYPE_PRODUCT_VARIANT;
  public static const TYPE_MARKETING_SPOT:String = CatalogModel.TYPE_MARKETING_SPOT;

  private static const PREFERENCES_COMMERCE_STRUCT:String = 'commerce';
  public static const COMMERCE_STRUCT_WORKSPACE:String = 'workspace';

  public static const CATALOG_IMAGE_URI_PREFIX:String = 'catalogimage';

  private var storeExpression:ValueExpression;

  private static var instance:CatalogHelper;

  {
    RemoteErrorHandlerRegistryImpl
            .initRemoteErrorHandlerRegistry()
            .registerErrorHandler(remoteErrorHandler);
  }

  public static function getInstance():CatalogHelper {
    if (!instance) {
      reset();
    }
    return instance;
  }

  public static function reset():void {
    instance = new CatalogHelper();
  }


  public static function getContentForCatalogObject(catalogObject:CatalogObject):Content {
    //TODO use content holder
    var uriPath:String = "content/" + catalogObject.getExternalId();
    var contentRepository:ContentRepository = session.getConnection().getContentRepository();
    return contentRepository.getContent(uriPath);
  }

  public function openCatalog():void {
    var store:Store = Store(getInstance().getActiveStoreExpression().getValue());
    if (store) {
      store.load(function ():void {
        store.getCatalog().load(function ():void {
          var selectedNode:* = getCollectionViewModel().getMainStateBean().get(CollectionViewModel.FOLDER_PROPERTY);
          //if already a category is selected we don't have to change anything.
          if (!(selectedNode is Category)) {
            selectedNode = store.getCatalog();
          }
          var model:CollectionViewModel = CollectionViewManagerInternal(editorContext.getCollectionViewManager()).getCollectionView().getCollectionViewModel();
          model.setMode(CollectionViewModel.REPOSITORY_MODE);

          CollectionViewManagerInternal(editorContext.getCollectionViewManager()).openWithAllState();
          CollectionViewManagerInternal(editorContext.getCollectionViewManager()).getCollectionView().showInRepositoryMode(selectedNode);
        });
      });
    }
  }

  public function openMarketingSpots():void {
    var store:Store = Store(getInstance().getActiveStoreExpression().getValue());
    if (store) {
      store.load(function ():void {
        store.getMarketing().load(function ():void {
          var model:CollectionViewModel = CollectionViewManagerInternal(editorContext.getCollectionViewManager()).getCollectionView().getCollectionViewModel();
          model.setMode(CollectionViewModel.REPOSITORY_MODE);

          var selectedNode:Object = store.getMarketing();
          CollectionViewManagerInternal(editorContext.getCollectionViewManager()).openWithAllState();
          CollectionViewManagerInternal(editorContext.getCollectionViewManager()).getCollectionView().showInRepositoryMode(selectedNode);
        });
      });
    }
  }

  public function getTypeLabel(catalogObject:CatalogObject):String {
    var catalogType:String = getType(catalogObject);
    return ECommerceStudioPlugin_properties.INSTANCE[catalogType + '_label'];
  }

  public function getTypeCls(catalogObject:CatalogObject):String {
    var catalogType:String = getType(catalogObject);
    return ECommerceStudioPlugin_properties.INSTANCE[catalogType + '_icon'];
  }

  public function getImageUrl(catalogObject:CatalogObject):String {
    if (catalogObject is Product) {
      return Product(catalogObject).getThumbnailUrl();
    } else if (catalogObject is Category) {
      return Category(catalogObject).getThumbnailUrl();
    }

    return null;
  }

  public function getType(catalogObject:CatalogObject):String {
    var beanType:String;
    if (catalogObject is Category) {
      beanType = TYPE_CATEGORY;
    } else if (catalogObject is ProductVariant) {
      beanType = TYPE_PRODUCT_VARIANT;
    } else if (catalogObject is Product) {
      beanType = TYPE_PRODUCT;
    } else if (catalogObject is MarketingSpot) {
      beanType = TYPE_MARKETING_SPOT;
    } else if (catalogObject is Marketing) {
      beanType = TYPE_MARKETING;
    } else if (catalogObject is Catalog) {
      beanType = TYPE_CATALOG;
    } else {
      beanType = "UnknownType";
    }

    return beanType;
  }

  public function getTypeFromId(id:String):String {
    var beanType:String;
    if (isCategoryId(id)) {
      beanType = TYPE_CATEGORY;
    } else if (isProductVariantId(id)) {
      beanType = TYPE_PRODUCT_VARIANT;
    } else if (isProductId(id)) {
      beanType = TYPE_PRODUCT;
    } else if (isMarketingSpot(id)) {
      beanType = TYPE_MARKETING_SPOT;
    } else if (isMarketing(id)) {
      beanType = TYPE_MARKETING;
    } else if (isCatalog(id)) {
      beanType = TYPE_CATALOG;
    }
    return beanType;
  }

  public function getExternalIdFromId(id:String):String {
    //we assume that the substring after the last '/' is the external id
    return id.substr(id.lastIndexOf('/') + 1);
  }

  public function isSubType(catalogObject:CatalogObject, catalogObjectType:String):Boolean {
    if (catalogObject is Category) {
      return catalogObjectType === TYPE_CATEGORY;
    } else if (catalogObject is ProductVariant) {
      return catalogObjectType === TYPE_PRODUCT || catalogObjectType === TYPE_PRODUCT_VARIANT;
    } else if (catalogObject is Product) {
      return catalogObjectType === TYPE_PRODUCT;
    } else if (catalogObject is MarketingSpot) {
      return catalogObjectType === TYPE_MARKETING_SPOT;
    } else {
      return false;
    }
  }

  /**
   * Get the catalog object for the given catalog object id.
   * If the content is specified the store of the content will be used
   * Otherwise the active store will be used.
   * @param catalogObjectId
   * @param contentExpression
   */
  public function getCatalogObject(catalogObjectId:String, contentExpression:ValueExpression = undefined):RemoteBean {

    var storeValue:Object = contentExpression ?
            getStoreForContentExpression(contentExpression).getValue() :
            getActiveStoreExpression().getValue();
    if (!storeValue) {
      return null;
    }
    var store:Store = storeValue as Store;

    var siteId:String = store.getSiteId();
    if (!siteId) {
      return null;
    }

    var workspaceId:String = getExtractedWorkspaceId();
    if (!workspaceId) {
      workspaceId = NO_WS;//No workspace selected
    }
    var externalId:String = extractExternalId(catalogObjectId);
    if (isCategoryId(catalogObjectId)) {
      return beanFactory.getRemoteBean("livecontext/category/" + siteId + "/" + workspaceId + "/" + externalId) as CatalogObject;
    } else if (isProductVariantId(catalogObjectId)) {
      return beanFactory.getRemoteBean("livecontext/sku/" + siteId + "/" + workspaceId + "/" + externalId) as CatalogObject;
    } else if (isProductId(catalogObjectId)) {
      return beanFactory.getRemoteBean("livecontext/product/" + siteId + "/" + workspaceId + "/" + externalId) as CatalogObject;
    } else if (isSegmentId(catalogObjectId)) {
      return beanFactory.getRemoteBean("livecontext/segment/" + siteId + "/" + workspaceId + "/" + externalId) as CatalogObject;
    } else if (isContractId(catalogObjectId)) {
      return beanFactory.getRemoteBean("livecontext/contract/" + siteId + "/" + workspaceId + "/" + externalId) as CatalogObject;
    } else if (isMarketingSpot(catalogObjectId)) {
      return beanFactory.getRemoteBean("livecontext/marketingspot/" + siteId + "/" + workspaceId + "/" + externalId) as CatalogObject;
    } else if (isMarketing(catalogObjectId)) {
      return beanFactory.getRemoteBean("livecontext/marketing/" + siteId + "/" + workspaceId + "/") as CatalogObject;
    } else if (isCatalog(catalogObjectId)) {
      return beanFactory.getRemoteBean("livecontext/catalog/" + siteId + "/" + workspaceId + "/") as CatalogObject;
    }

    return undefined;
  }

  /**
   *
   * @param bindTo value expression pointing to a document of which 'externalId' property as the product id
   * @param productPropertyName
   */
  public function getProductPropertyExpression(bindTo:ValueExpression, productPropertyName:String):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():* {
      var product:Product;
      var catalogObjectId:String = bindTo.extendBy('properties').extendBy('externalId').getValue();
      if (!catalogObjectId || catalogObjectId.length === 0) {
        return null;
      } else {
        product = getCatalogObject(catalogObjectId, bindTo) as Product;
        if (product) {
          return product.get(productPropertyName);
        } else {
          return null;
        }
      }
    });
  }

  public function isStoreId(catalogObjectId:String):Boolean {
    return catalogObjectId.indexOf('livecontext/store') >= 0;
  }

  private function isProductVariantId(catalogObjectId:String):Boolean {
    return catalogObjectId.indexOf("//catalog/sku/") !== -1;
  }

  private function isProductId(catalogObjectId:String):Boolean {
    return catalogObjectId.indexOf("//catalog/product/") !== -1;
  }

  private function isCategoryId(catalogObjectId:String):Boolean {
    return catalogObjectId.indexOf("//catalog/category/") !== -1;
  }

  private function isSegmentId(catalogObjectId:String):Boolean {
    return catalogObjectId.indexOf("//catalog/segment/") !== -1;
  }

  private function isContractId(catalogObjectId:String):Boolean {
    return catalogObjectId.indexOf("//catalog/contract/") !== -1;
  }

  public static function isMarketingSpot(catalogObjectId:String):Boolean {
    return catalogObjectId.indexOf("/marketingspot/") !== -1
  }

  public static function isMarketing(catalogObjectId:String):Boolean {
    return catalogObjectId.indexOf("/marketing/") !== -1
  }

  public static function isCatalog(catalogObjectId:String):Boolean {
    return catalogObjectId.indexOf("/catalog/") !== -1
  }

  public function getActiveStoreExpression():ValueExpression {
    if (storeExpression) {
      return storeExpression;
    }
    storeExpression = ValueExpressionFactory.createFromFunction(function ():Store {
      var workspaceId:String = getExtractedWorkspaceId();
      var siteId:String = getPreferredSiteId();
      var store:Store = beanFactory.getRemoteBean("livecontext/store/" + siteId + "/" + workspaceId) as Store;
      //test if the store is valid
      store.load();

      if (store.getId()) {
        return store;
      }
      return null;
    });
    return storeExpression;
  }

  public function isActiveCoreMediaStore():Boolean {
    var store:Store = getActiveStoreExpression().getValue();
    return isCoreMediaStore(store);
  }

  public function belongsToCoreMediaStore(items:Array):Boolean {
    if(items.length === 0) {
      return undefined;
    }

    var store:Store = items[0].getStore();
    return isCoreMediaStore(store);
  }

  private function isCoreMediaStore(store:Store):Boolean {
    if(!store) {
      return undefined;
    }
    return store.getVendorName() && store.getVendorName() === "coremedia";
  }

  public function getExtractedWorkspaceId():String {
    var workspaceFullId:String = getCommerceWorkspaceExpression().getValue();
    if (!workspaceFullId) {
      return NO_WS;//No workspace selected;
    }
    return extractExternalId(workspaceFullId);
  }

  public function getPreferredSiteId():String {
    return editorContext.getSitesService().getPreferredSiteId();
  }

  public function getCommerceWorkspaceExpression():ValueExpression {
    return ValueExpressionFactory.create(null, editorContext.getPreferences()).
            extendBy(PREFERENCES_COMMERCE_STRUCT).extendBy(COMMERCE_STRUCT_WORKSPACE);
  }

  public function getSiteIdForContent(content:Content):String {
    return editorContext.getSitesService().getSiteIdFor(content);
  }

  public function getStoreForContentExpression(contentExpression:ValueExpression):ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Store {
      var workspaceId:String = getExtractedWorkspaceId();
      var siteId:String = getSiteIdForContent(contentExpression.getValue());
      return beanFactory.getRemoteBean("livecontext/store/" + siteId + "/" + workspaceId) as Store;
    });
  }

  public function getStoreForContent(content:Content, callback:Function):void {
    if (!content) {
      callback.call(null, undefined);
    }
    getStoreForContentExpression(ValueExpressionFactory.createFromValue(content)).loadValue(
            function ():void {
              callback.call();
            }
    );
  }

  private static function remoteErrorHandler(error:RemoteError, source:Object):void {
    var catalogObject:CatalogObject = source as CatalogObject;
    if (catalogObject) {
      var status:uint = error.status;
      var errorCode:String = error.errorCode;
      if (status === 503 /*&& errorCode === "LC-01001"*/) {
        MessageBoxUtil.showError(ECommerceStudioPlugin_properties.INSTANCE.commerceConnectionError_title,
                StringUtil.format(ECommerceStudioPlugin_properties.INSTANCE.commerceConnectionError_message, errorCode));
        error.setHandled(true); //dont process the error further
      } else if (status === 500 /*&& errorCode === "LC-01002"*/) {
        MessageBoxUtil.showError(ECommerceStudioPlugin_properties.INSTANCE.commerceCatalogError_title,
                StringUtil.format(ECommerceStudioPlugin_properties.INSTANCE.commerceCatalogError_message, errorCode));
        error.setHandled(true); //don't process the error further
      } else if (status === 404) {
        trace('[WARN]', 'CommerceBean not found: ' + error.errorName + '/' + error.errorCode + ' : ' + catalogObject.getUri());
        error.setHandled(true);
      } else {
        trace('[ERROR]', 'Catalog request failed: ' + error.errorName + '/' + error.errorCode + ' : ' + catalogObject.getUri());
        error.setHandled(true);
      }
    }
  }

  public function getChildren(catalogObject:CatalogObject):Array {
    if (!catalogObject) {
      return [];
    }
    else if (catalogObject is Marketing) {
      return Marketing(catalogObject).getMarketingSpots();
    }
    else if (catalogObject is Catalog) {
      return Catalog(catalogObject).getTopCategories();
    }
    else if (catalogObject is Store) {
      return [];
    }
    else if (catalogObject is Category) {
      return Category(catalogObject).getChildren();
    }
    else {
      return [];
    }
  }

  /**
   *
   * @param productVariant
   * @return the defining attributes as comma-separated list. E.g. (Red, XL)
   */
  private function getDefiningAttributesString(productVariant:ProductVariant):String {
    var attributesStr:String = undefined;
    var definingAttributes:Array = productVariant.getDefiningAttributes();
    if (definingAttributes) {
      for each (var attribute:ProductAttribute in  definingAttributes) {
        if (!attributesStr) {
          attributesStr = '('
        } else {
          attributesStr += ', '
        }
        attributesStr +=attribute.value;
      }
      if (attributesStr) {
        attributesStr += ')';
      }
    }
    return attributesStr;
  }

  /**
   *
   * @param catalogObject
   * @return the name and for variants the defining attributes as comma-separated list. E.g. (Red, XL)
   */
  public function getDecoratedName(catalogObject:CatalogObject):String {
    var name:String = catalogObject.getName();
    if (catalogObject is ProductVariant) {
      var attributes:String = getDefiningAttributesString(catalogObject as ProductVariant);
      if (attributes) {
        name += ' ' + attributes;
      }
    } else if (catalogObject is Category) {
      name = (catalogObject as Category).getDisplayName();
    }
    return name;
  }

  public function getPriceWithCurrencyExpression(bindTo:ValueExpression, priceProperty:String) {
    return ValueExpressionFactory.createFromFunction(function():String {
      var priceWithCurrency:String = undefined;
      var product:Product = bindTo.getValue() as Product;
      if (product && product.get(priceProperty)) {
          priceWithCurrency = product.get(priceProperty) + ' ' + product.getCurrency()
      }
      return priceWithCurrency;
    });

  }

  public function getIsVariantExpression(bindTo:ValueExpression) {
    return ValueExpressionFactory.createFromFunction(function():Boolean {
      return bindTo.getValue() is ProductVariant;
    });
  }

  public function getIsNotVariantExpression(bindTo:ValueExpression) {
    return ValueExpressionFactory.createFromFunction(function():Boolean {
      return !(bindTo.getValue() is ProductVariant);
    });
  }

  public function createOrUpdateProductListStructs(bindTo:ValueExpression, product:Product = undefined):void {
    var localSettingsStructExpression:ValueExpression = bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME);
    localSettingsStructExpression.loadValue(function():void {
      var localSettingsStruct:Struct = localSettingsStructExpression.getValue();
      RemoteBean(localSettingsStruct).load(function():void {
        if (!localSettingsStruct.get(COMMERCE_STRUCT_NAME)) {
          localSettingsStruct.getType().addStructProperty(COMMERCE_STRUCT_NAME);
        }
        var commerceStruct:Struct = localSettingsStruct.get(COMMERCE_STRUCT_NAME);
        if (!commerceStruct.get(PRODUCTS_LIST_NAME)) {
          commerceStruct.getType().addStringListProperty(PRODUCTS_LIST_NAME, 1000000);
        }
        //avoid duplicates
        if (product && (commerceStruct.get(PRODUCTS_LIST_NAME) as Array).indexOf(product.getId()) < 0) {
          commerceStruct.addAt(PRODUCTS_LIST_NAME, -1, product.getId());
        }
      });
    });
  }

  public static function getCatalogObjectsExpression(contentExpression:ValueExpression,
                                                     catalogObjectIdListName:String,
                                                     invalidMessage:String,
                                                     catalogObjectIdsExpression:ValueExpression = undefined):ValueExpression {

    var idsExpression:ValueExpression = catalogObjectIdsExpression || getCatalogObjectIdsExpression(contentExpression, catalogObjectIdListName);
    return ValueExpressionFactory.createFromFunction(function ():Array {
      var catalogObjects:Array = idsExpression.getValue();
      if (!catalogObjects) {
        return undefined;
      }
      catalogObjects = catalogObjects.map(function (id:String):Bean {
                try {
                  var externalId:String = CatalogHelper.getInstance().getExternalIdFromId(id);
                  var catalogObject:CatalogObject = CatalogHelper.getInstance().getCatalogObject(id, contentExpression) as CatalogObject;
                  if (catalogObject && catalogObject.getName()) {
                    return catalogObject;
                  } else {
                    // no catalog object or name found : probably wrong catalog object id
                    //use local bean to display the id instead
                    return beanFactory.createLocalBean({
                      id: id,
                      externalId: externalId,
                      name: StringUtil.format(invalidMessage, externalId)
                    });
                  }
                } catch (e:NotExistsError) {
                  // if remote bean could not be loaded (404) local bean shall be displayed
                  return beanFactory.createLocalBean({
                    id: id,
                    externalId: externalId,
                    name: StringUtil.format(invalidMessage, externalId)
                  });
                }
              }
      );

      //todo: the catalog objects may be null (why?)
      catalogObjects = catalogObjects.filter(function (obj:*):Boolean {
        return obj;
      });
      return  catalogObjects;
    })
  }

  public static function addCatalogObject(contentExpression:ValueExpression, catalogObjectIdListName:String,
                                          catlogObjectId:String, catalogObjectIdsExpression:ValueExpression = undefined):void {
    var idsExpression:ValueExpression = catalogObjectIdsExpression || getCatalogObjectIdsExpression(contentExpression, catalogObjectIdListName);
    if (idsExpression.isLoaded()) {
      doAddCatalogObject(contentExpression, catalogObjectIdListName, catlogObjectId, catalogObjectIdsExpression);
    } else {
      idsExpression.loadValue(function ():void {
        doAddCatalogObject(contentExpression, catalogObjectIdListName, catlogObjectId, catalogObjectIdsExpression);
      });
    }
  }

  private static function doAddCatalogObject(contentExpression:ValueExpression, catalogObjectIdListName:String,
                                             catlogObjectId:String, catalogObjectIdsExpression:ValueExpression = undefined):void {
    var idsExpression:ValueExpression = catalogObjectIdsExpression || getCatalogObjectIdsExpression(contentExpression, catalogObjectIdListName);
    var catalogObjectIds:Array = idsExpression.getValue();
    if (!catalogObjectIds) {
      createStructsIfNecessary(contentExpression, catalogObjectIdListName, catalogObjectIdsExpression);
      addCatalogObject(contentExpression, catalogObjectIdListName, catlogObjectId, catalogObjectIdsExpression);
    } else {
      if (catalogObjectIds.indexOf(catlogObjectId) >= 0 ) return;
      idsExpression.setValue(catalogObjectIds.concat([catlogObjectId]));
    }
  }

  private static function createStructsIfNecessary(contentExpression:ValueExpression,
                                                   catalogObjectIdListName:String,
                                                   catalogObjectIdsExpression:ValueExpression = undefined):void {
    if (catalogObjectIdsExpression) return;
    var structProperty:Struct = contentExpression.extendBy(PROPERTIES, PROFILE_EXTENSIONS).getValue();

    var propertiesStruct:Struct = structProperty.get(PROPERTIES);
    if (!propertiesStruct) {
      structProperty.getType().addStructProperty(PROPERTIES);
      propertiesStruct = structProperty.get(PROPERTIES);
    }

    var commerceStruct:Struct = propertiesStruct.get(COMMERCE_STRUCT_NAME);
    if (!commerceStruct) {
      propertiesStruct.getType().addStructProperty(COMMERCE_STRUCT_NAME);
      commerceStruct = propertiesStruct.get(COMMERCE_STRUCT_NAME);
    }

    if (!commerceStruct.get(catalogObjectIdListName)) {
      commerceStruct.getType().addStringListProperty(catalogObjectIdListName, 1000000, []);
    }
  }

  public static function removeCatalogObject(contentExpression:ValueExpression,
                                             catalogObjectIdListName:String, catlogObjectId:String,
                                             catalogObjectIdsExpression:ValueExpression = undefined):void {
    var idsExpression:ValueExpression = catalogObjectIdsExpression || getCatalogObjectIdsExpression(contentExpression, catalogObjectIdListName);
    idsExpression.loadValue(function(catalogObjectIds:Array):void {
      if (catalogObjectIds) {
        if (catalogObjectIds.indexOf(catlogObjectId) < 0 ) return;

        var newCatalogObjects:Array = catalogObjectIds.filter(function(oldCatalogObjectId:String):Boolean {
          return oldCatalogObjectId !== catlogObjectId;
        });

        idsExpression.setValue(newCatalogObjects);
      }
    })
  }

  private static function getCatalogObjectIdsExpression(contentExpression:ValueExpression, catalogObjectIdListName:String):ValueExpression {
    return contentExpression.extendBy(PROPERTIES, PROFILE_EXTENSIONS, PROPERTIES, COMMERCE_STRUCT_NAME, catalogObjectIdListName);
  }

  public static function getSegments(store:Store):Array {
    var segments:Segments = store.getSegments();
    return segments && segments.getSegments();
  }

  public static function getContracts(store:Store):Array {
    var contracts:Contracts = store.getContracts();
    return contracts && contracts.getContracts();
  }


  public function isCatalogImageUrl(imageUrl:String):Boolean {
    return imageUrl && imageUrl.indexOf(CATALOG_IMAGE_URI_PREFIX) >= 0 ;
  }

  public function isNotCatalogImageUrl(imageUrl:String):Boolean {
    return !imageUrl || imageUrl.indexOf(CATALOG_IMAGE_URI_PREFIX) < 0 ;
  }

  private function extractExternalId(catalogObjectId:String):String {
    return catalogObjectId.substr(catalogObjectId.lastIndexOf('/') + 1, catalogObjectId.length);
  }

  private static function getCollectionViewModel():CollectionViewModel {
    return EditorContextImpl(editorContext).getCollectionViewModel();
  }
}
}
