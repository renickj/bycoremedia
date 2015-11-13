package com.coremedia.ecommerce.studio.components.thumbnail {
import com.coremedia.cms.editor.sdk.config.collectionView;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.ecommerce.studio.config.catalogThumbnailOverlay;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.Category;
import com.coremedia.ecommerce.studio.model.Product;
import com.coremedia.ui.components.SwitchingContainer;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.util.PropertyChangeEventUtil;

import ext.Component;
import ext.EventObject;
import ext.IEventObject;
import ext.menu.Menu;

public class CatalogThumbnailOverlayBase extends Menu {

  public static const THUMB_DATAVIEW_SET_EVENT:String = "thumbDataViewSet";

  internal static const SWITCHING_CONTAINER_ITEM_ID:String = 'SWITCHING_CONTAINER_ITEM_ID';
  private var switchingContainer:SwitchingContainer;
  private var catalogObject:CatalogObject;
  private var thumbDataView:CatalogThumbDataViewBase;
  private var selected:Boolean;
  private var thumbnailDragZone:CatalogThumbnailDragZone;
  private var selectedNodeExpression:ValueExpression;

  /**
   * @param config the config object
   */
  public function CatalogThumbnailOverlayBase(config:catalogThumbnailOverlay) {
    super(config);

  }

  public static function transformCatalogObjectToItemId(catalogObject:CatalogObject):String {
    if ((catalogObject is Product && Product(catalogObject).getThumbnailUrl()) ||
            (catalogObject is Category && Category(catalogObject).getThumbnailUrl())) {
      return "image";
    } else {
      return "default";
    }
  }

  override protected function afterRender():void {
    super.afterRender();
    switchingContainer = getComponent(SWITCHING_CONTAINER_ITEM_ID) as SwitchingContainer;
    setupDragZones();
    // scrolling should be possible in thumbnail views, so close overlay on "mousewheel"
    mon(getEl(), "mousewheel", function ():void {
      hide();
    });
    mon(getEl(), "mouseleave", function ():void {
      hide();
    });
    mon(getEl(), "keydown", handleKeyDown);

    mon(getEl(), 'dblclick', handleDoubleClick);

  }

  private function setupDragZones():void {
    thumbnailDragZone = new CatalogThumbnailDragZone(this, thumbDataView);
  }

  private function handleKeyDown(e:IEventObject):void {
    if (e.getKey() === EventObject.ENTER) {
      openCategory();
    }
  }

  private function handleDoubleClick(e:IEventObject):void {
    getThumbDataView().selectCurrentRecord(e.ctrlKey);
    openCategory();
    e.preventDefault();
  }

  private function openCategory():void {
    if (catalogObject && catalogObject is Category) {
      getSelectedNodeExpression().setValue(catalogObject);
    }
  }

  public function getThumbDataView():CatalogThumbDataViewBase {
    return thumbDataView;
  }

  public function setThumbDataView(catalogThumbDataView:CatalogThumbDataViewBase):void {
    this.thumbDataView = catalogThumbDataView;
    fireEvent(THUMB_DATAVIEW_SET_EVENT, catalogThumbDataView);
  }

  override public function hide():Component {
    //reset the overlays
    if (switchingContainer) {
      switchingContainer.items.each(function(defaultOverlay:CatalogDefaultOverlayBase):void{
        defaultOverlay.reset();
        defaultOverlay.initialConfig.bindTo.setValue(null);
      });
    }
    return super.hide();
  }

  public function getActiveOverlay():CatalogDefaultOverlayBase {
    return switchingContainer?switchingContainer.getActiveItem() as CatalogDefaultOverlayBase:undefined;
  }

  public function getCatalogObject():CatalogObject {
    return catalogObject;
  }

  public function setCatalogObject(newCatalogObject:CatalogObject):void {
    var oldCatalogObject:CatalogObject = catalogObject;
    catalogObject = newCatalogObject;
    PropertyChangeEventUtil.fireEvent(this, 'catalogObject', oldCatalogObject, newCatalogObject);
  }

  public function setSelected(value:Boolean):void {
    if (value) {
      this.el.addClass('thumbnail-overlay-selected');
    } else {
      this.el.removeClass('thumbnail-overlay-selected');
    }
    selected = value;
  }

  public function isSelected():Boolean {
    return selected;
  }

  private function getSelectedNodeExpression():ValueExpression {
    return ComponentContextManager.getInstance().getContextExpression(this, collectionView.SELECTED_FOLDER_VARIABLE_NAME);
  }

  override protected function beforeDestroy():void {
    thumbnailDragZone && thumbnailDragZone.unreg();
    super.beforeDestroy();
  }
}
}
