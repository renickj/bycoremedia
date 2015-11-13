package com.coremedia.livecontext.studio {

import com.coremedia.blueprint.base.components.quickcreate.QuickCreate;
import com.coremedia.blueprint.base.components.quickcreate.processing.ProcessingData;
import com.coremedia.blueprint.studio.property.ImageLinkListRenderer;
import com.coremedia.blueprint.studio.util.ContentInitializer;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewManagerInternal;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.cms.editor.sdk.preview.PreviewURI;
import com.coremedia.ecommerce.studio.CatalogModel;
import com.coremedia.ecommerce.studio.components.link.QuickCreateCatalogLink;
import com.coremedia.ecommerce.studio.config.quickCreateCatalogLink;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.livecontext.studio.config.livecontextStudioPlugin;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.util.UrlUtil;

import ext.Component;
import ext.Container;
import ext.config.container;

public class LivecontextStudioPluginBase extends StudioPlugin {

  private static const EXTERNAL_ID_PROPERTY:String = 'externalId';

  public function LivecontextStudioPluginBase(config:livecontextStudioPlugin) {
    if (UrlUtil.getHashParam('livecontext') === 'false') {
      delete config['rules'];
      delete config['configuration'];
    }
    super(config)
  }


  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);

    var collectionViewManagerInternal:CollectionViewManagerInternal =
            ((editorContext.getCollectionViewManager()) as CollectionViewManagerInternal);
    collectionViewManagerInternal.addExtension(new LivecontextCollectionViewExtension());

    //forward the workspaceId (configured by the hash param) to the preview url.
    editorContext.registerPreviewUrlTransformer(function (uri:PreviewURI, callback:Function):void {
      var workspaceId:String = CatalogHelper.getInstance().getExtractedWorkspaceId();
      if (workspaceId && workspaceId !== CatalogHelper.NO_WS) {
        uri.appendParameter("workspaceId", workspaceId);
      }
      callback.call(null);
    });

    /**
     * Apply image link list preview
     */
    editorContext.registerThumbnailUriRenderer(livecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PAGE, renderLiveContextPreview);
    editorContext.registerThumbnailUriRenderer(livecontextStudioPlugin.CONTENT_TYPE_MARKETING_SPOT, renderLiveContextPreview);
    editorContext.registerThumbnailUriRenderer(livecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER, renderLiveContextProductTeaserPreview);

    /**
     * Register Content initializer
     */
    editorContext.registerContentInitializer(livecontextStudioPlugin.CONTENT_TYPE_MARKETING_SPOT, initMarketingSpot);
    editorContext.registerContentInitializer(livecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER, initProductTeaser);
    editorContext.registerContentInitializer(livecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PAGE, initExternalPage);

    /**
     * apply the marketing spot link field to CMMarketingSpot quick create dialog
     */
    QuickCreate.addQuickCreateDialogProperty(livecontextStudioPlugin.CONTENT_TYPE_MARKETING_SPOT, EXTERNAL_ID_PROPERTY,
            function (data:ProcessingData, properties:Object):Component {
              properties.openLinkSources = CatalogHelper.getInstance().openMarketingSpots;
              properties.catalogObjectType = CatalogModel.TYPE_MARKETING_SPOT;
              properties.emptyText = LivecontextStudioPlugin_properties.INSTANCE.MarketingSpot_Link_empty_text;
              var myCatalogLink:QuickCreateCatalogLink = new QuickCreateCatalogLink(quickCreateCatalogLink(properties));
              return new Container(new container({cls: 'link-list-wrapper-hbox-layout',
                fieldLabel: properties.label,
                autoWidth: true,
                items: [myCatalogLink]}));
            });


    /**
     * apply the product link field to CMProductTeaser quick create dialog
     */
    QuickCreate.addQuickCreateDialogProperty(livecontextStudioPlugin.CONTENT_TYPE_PRODUCT_TEASER, EXTERNAL_ID_PROPERTY,
            function (data:ProcessingData, properties:Object):Component {
              properties.openLinkSources = CatalogHelper.getInstance().openCatalog;
              properties.catalogObjectType = CatalogModel.TYPE_PRODUCT;
              properties.emptyText = LivecontextStudioPlugin_properties.INSTANCE.Product_Link_empty_text;
              var myCatalogLink:QuickCreateCatalogLink = new QuickCreateCatalogLink(quickCreateCatalogLink(properties));
              return new Container(new container({cls: 'link-list-wrapper-hbox-layout',
                fieldLabel: properties.label,
                autoWidth: true,
                items: [myCatalogLink]}));
            });

    CMExternalChannelExtension.register();
    /**
     * apply the category link field to CMExternalChannel aka 'External Page' quick create dialog
     */
    QuickCreate.addQuickCreateDialogProperty(livecontextStudioPlugin.CONTENT_TYPE_EXTERNAL_PAGE, EXTERNAL_ID_PROPERTY,
            function (data:ProcessingData, properties:Object):Component {
              properties.openLinkSources = CatalogHelper.getInstance().openCatalog;
              properties.catalogObjectType = CatalogModel.TYPE_CATEGORY;
              properties.emptyText = LivecontextStudioPlugin_properties.INSTANCE.Category_Link_empty_text;
              var myCatalogLink:QuickCreateCatalogLink = new QuickCreateCatalogLink(quickCreateCatalogLink(properties));
              return new Container(new container({cls: 'link-list-wrapper-hbox-layout',
                fieldLabel: properties.label,
                autoWidth: true,
                items: [myCatalogLink]}));
            });
  }

  private static function initProductTeaser(content:Content):void {
    //don't initialize the teaser title for product teasers
    //they will inherit the teaser title form the linked product
    //setProperty(content, 'teaserTitle', content.getName());
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  private static function initMarketingSpot(content:Content):void {
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  private static function initExternalPage(content:Content):void {
    ContentInitializer.initCMLinkable(content);
    ContentInitializer.initCMLocalized(content);
  }

  private function renderLiveContextProductTeaserPreview(content:Content):String {
    var result:String;
    result = ImageLinkListRenderer.renderCMTeasable(content);
    if(!result){
      result = renderLiveContextPreview(content)
    }
    return result;
  }

  /**
   * Since all live context bean use the "externalId" property we can register the same
   * rendering function for all content types.
   * @param content The livecontext content to render.
   * @return The preview url of the catalog object.
   */
  private function renderLiveContextPreview(content:Content):String {
    var url:String = undefined;
    CatalogHelper.getInstance().getStoreForContent(content, function ():void {
      var bindTo:ValueExpression = ValueExpressionFactory.create('', content);
      var externalId:String = bindTo.extendBy('properties.' + livecontextStudioPlugin.EXTERNAL_ID_PROPERTY).getValue();
      var catalogObject:CatalogObject = CatalogHelper.getInstance().getCatalogObject(externalId, bindTo) as CatalogObject;
      url = CatalogHelper.getInstance().getImageUrl(catalogObject);
    });
    return url;
  }

  //noinspection JSUnusedGlobalSymbols
  internal function getShopExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():String {
      var store:Store = Store(CatalogHelper.getInstance().getActiveStoreExpression().getValue());
      return store && store.getName();
    });
  }

  internal function isWorkspaceEnabledExpression():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Boolean {
      var workspaces:Array;
      var activeStore:Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
      if (activeStore) {
        if (activeStore.getWorkspaces()) {
          workspaces = activeStore.getWorkspaces().getWorkspaces();
        }
      }
      return workspaces && workspaces.length > 0;
    });
  }

  static internal function reloadPreview(previewPanel:PreviewPanel):void {
    if (previewPanel.rendered) {
      previewPanel.reloadFrame();
    }
  }
}
}