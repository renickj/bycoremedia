package com.coremedia.ecommerce.studio.components.thumbnail {
import com.coremedia.cms.editor.sdk.collectionview.CollectionViewModel;
import com.coremedia.cms.editor.sdk.config.thumbnailOverlay;
import com.coremedia.cms.editor.sdk.context.ComponentContextManager;
import com.coremedia.cms.editor.sdk.dragdrop.ContentDragProvider;
import com.coremedia.cms.editor.sdk.dragdrop.DragInfo;
import com.coremedia.ecommerce.studio.config.catalogThumbDataView;
import com.coremedia.ecommerce.studio.config.catalogThumbnailOverlay;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.DispatchingXTemplate;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.plugins.ContextMenuPlugin;
import com.coremedia.ui.store.BeanRecord;

import ext.Component;
import ext.ComponentMgr;
import ext.DataView;
import ext.Element;
import ext.IEventObject;
import ext.XTemplate;
import ext.config.menu;
import ext.util.MixedCollection;

import js.HTMLElement;

public class CatalogThumbDataViewBase extends DataView implements ContentDragProvider {

  {
    thumbnailViewXTemplate = new DispatchingXTemplate({
      "default":new XTemplate('<div class="thumb-wrap default">',
        '<div class="border">',
        '<div class="thumb">',
        '<div class="large">',
        '<img width="100%" height="100%" class="content-type-l {typeCls}" src="{Ext.BLANK_IMAGE_URL}"/>',
        '</div>',
        '</div>',
        '<div class="ellipsis-container"><div class="short-name"><p class="short-name-paragraph catalog-object_id">{id}</p></div></div>',
        '</div>',
        '</div>'),
      "image":new XTemplate('<div class="thumb-wrap">',
        '<div class="border">',
        '<div class="thumb">',
        '<div class="large">',
        '<img class="fit-catalog-thumb-image" src="{imageUri}"/>',
        '</div>',
        '</div>',
        '<div class="ellipsis-container"><div class="short-name"><p class="short-name-paragraph catalog-object_id">{id}</p></div></div>',
        '</div>',
        '</div>')
    },
    dispatchXTemplate);
  }

  private static var thumbnailViewXTemplate:DispatchingXTemplate;

  private var detailView:CatalogThumbnailOverlay;
  private var collectionViewModel:CollectionViewModel;

  public native function get selectedItemsValueExpression():ValueExpression;

  /**
   * the current record of mouse enter event
   */
  private var record:BeanRecord;

  /**
   * @param config the config object
   */
  public function CatalogThumbDataViewBase(config:catalogThumbDataView) {
    config.tpl = thumbnailViewXTemplate;
    super(config);
    mon(this, 'mouseenter', handleMouseEnter);
    mon(this, 'afterrender', afterFirstRender, null, {single:true});
    mon(this, 'contextmenu', handleContextMenu);
//    mon(this, 'afterrender', addSearchListener);
  }

  private function afterFirstRender():void {
    var detailViewConfig:catalogThumbnailOverlay = new catalogThumbnailOverlay({
      defaultOffsets: [0,-20]
    });
    ComponentContextManager.configOwnerCt(detailViewConfig, this);
    detailView = new CatalogThumbnailOverlay(detailViewConfig);
    detailView.setThumbDataView(this);
    addListenerToDetailView();
    mon(ownerCt.el, 'mouseleave', handleMouseLeave);
    this.el.parent().addListener('scroll', function():void{
      detailView.hide();
    });
  }

  [InjectFromExtParent]
  public function setCollectionViewModel(collectionViewModel:CollectionViewModel):void {
    this.collectionViewModel = collectionViewModel;
  }

  /**
   * Function that is used by DispatchingXTemplate to determine
   * which xtemplate to use. (see #thumbnailViewXTemplate)
   *
   * @param data the map of data objects that the templates receive
   * @return the name of the xtemplate to use.
   */
  private static function dispatchXTemplate(data:Object):String {
    //when no image uri has been set, fall back to the default template:
    return data['imageUri'] ? "image" : "default";
  }


  //noinspection JSUnusedLocalSymbols
  private function handleMouseEnter(view:DataView, index:int, node:HTMLElement, e:IEventObject):void {
    record = view.getStore().getAt(index) as BeanRecord;
    //don't show the overlay of the currently being edited node which would otherwise hide the edit mask
    if (record) {
      showOverLay(index, node);
    }
  }

  private function handleContextMenu(view:DataView, index:int, node:HTMLElement, e:IEventObject):void {
    handleMouseEnter(view, index, node, e);
    e.preventDefault();
  }

  /**
   * show the loading... during the search
   */
//  private function addSearchListener():void {
//    var thiz:CatalogThumbDataViewBase = this;
//    collectionViewModel.addListener(CatalogModel.AFTER_SEARCH_EVENT, function():void {
//      //fixes STUDIO-791, data view needs a refresh in order to get rid of the 'loading' div
//      // We need a 'silent' refreshment here as the BindSelectionPlugin relies on it.
//      var oldClearSelections:Function = thiz["clearSelections"];
//      thiz["clearSelections"] = Ext.emptyFn;
//      thiz.refresh();
//      thiz["clearSelections"] = oldClearSelections;
//      getStore().fireEvent("load", getStore(), getStore().getRange(), {unchanged:true});
//      //TODO: Why is this necessary?
//      var selectedItems:Array = ComponentContextManager.getInstance().getContextExpression(thiz,
//              catalogSearchListContainer.SELECTED_SEARCH_ITEMS_VARIABLE_NAME).getValue();
//      if (selectedItems) {
//        thiz.select(thiz.getRecordIndices(selectedItems), false, true);
//      }
//    });
//  }

  private function showOverLay(nodeIndex:Number, node:HTMLElement):void {
    detailView.hide();
    // Do not show overlay if there is a non-overlay-menu visible (like a contextmenu).
    var openMenus:MixedCollection = ComponentMgr.all.filterBy(function(component:Component):Boolean {
      return component.isXType(menu) && !component.isXType(thumbnailOverlay) && component.isVisible();
    });
    if (openMenus.getCount() === 0) {
      detailView.setCatalogObject(BeanRecord(getStore().getAt(nodeIndex)).getBean() as CatalogObject);
      // if a image overlay...
      if (detailView.getActiveOverlay() is CatalogImageOverlay) {
        //... then show the menu after the image is loaded. this fixes STUDIO-783 - again...
        detailView.getActiveOverlay().getImage().el.addListener('load', function():void {
          detailView.showMenu(Element.get(node), 'c-c');
        }, null, {single:true});
      } else {
        // ...otherwise the image is set by CSS
        detailView.showMenu(Element.get(node), 'c-c');
      }

      detailView.setSelected(isSelected(node));
    }
  }

  private function handleMouseLeave(e:IEventObject):void {
    var horizontalShift:Number = e.getPageX() - ownerCt.getPosition()[0];
    var verticalShift:Number = e.getPageY() - ownerCt.getPosition()[1];
    //When the mouse leaves the dataview...
    if (horizontalShift <= 0 || horizontalShift >= ownerCt.getWidth() ||
            verticalShift <= 0 || verticalShift >= ownerCt.getHeight()){
      //..then hide the overlay
      detailView.hide();
    }
  }

  private function addListenerToDetailView():void {
    mon(detailView, 'render', function ():void {
      //the click of the detail view triggers the selection of the data view
      mon(detailView.el, 'mousedown', function (e:IEventObject):void {
        clickOnCurrentRecord(e);
      });

      mon(detailView.el, 'contextmenu', handleDetailClick);
      mon(detailView.el, 'dblclick', handleDetailClick);
    });
  }

  private function handleDetailClick(event:IEventObject):Boolean {
    return ContextMenuPlugin.handleEvent(this, event);
  }

  private function getRecordIndices(beans:Array):Array {
    var indices:Array = [];
    beans.forEach(function (bean:Bean):void {
      var index:Number = findBeanRecordIndex(bean);
      if (index > -1) {
        indices.push(index);
      }
    });
    return indices;
  }

  private function findBeanRecordIndex( bean:Bean):Number {
    if (bean) {
      return getStore().findBy(function (record:BeanRecord):Boolean {
        return record.getBean() === bean;
      });
    }
    return -1;
  }


  private function clickOnCurrentRecord(e:IEventObject):void {
    //preserve the selection. this is necessary to drag multiple items.
    if (!isSelected(record) || e.shiftKey || e.ctrlKey) {
      // delegate to the ext private method to handle the extra keys
      this['onItemClick'](record, getStore().indexOf(record), e);
    }
    if (isSelected(record)) {
      detailView.setSelected(true);
    } else {
      detailView.setSelected(false);
    }
  }

  public function selectCurrentRecord(keepExisting:Boolean = false):void {
    select(record, keepExisting);
  }

  /**
   * Ensures that the hover layer is destroyed too.
   */
  override protected function onDestroy():void {
    super.onDestroy();
    if (detailView) {
      detailView.destroy();
    }
  }

  public function isLinking():Boolean {
    return false;
  }


  public function notifyDropSuccessful(dragDropType:String, dragInfo:DragInfo):void {
    // do nothing
  }

  public function getDetailView():CatalogThumbnailOverlay {
    return detailView;
  }
}
}
