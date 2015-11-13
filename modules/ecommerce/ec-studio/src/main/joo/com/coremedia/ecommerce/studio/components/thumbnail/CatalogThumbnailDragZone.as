package com.coremedia.ecommerce.studio.components.thumbnail {
import com.coremedia.cms.editor.sdk.collectionview.thumbnail.*;

import com.coremedia.cms.editor.sdk.dragdrop.DragDropVisualFeedback;
import com.coremedia.ecommerce.studio.dragdrop.CatalogDragDropVisualFeedback;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ui.store.BeanRecord;

import ext.IEventObject;
import ext.config.dragzone;
import ext.data.Record;
import ext.dd.DragZone;

public class CatalogThumbnailDragZone extends DragZone {
  private var overlay:CatalogThumbnailOverlayBase;
  private var thumbDataView:CatalogThumbDataViewBase;

  /**
   * @param overlay the overlay component to which this drag source is added
   * @param thumbDataView the data view to attach the drag zone to
   */
  public function CatalogThumbnailDragZone(overlay:CatalogThumbnailOverlayBase, thumbDataView:CatalogThumbDataViewBase) {
    super(overlay.getEl(), dragzone({
      scroll: false,
      ddGroup: "ContentDD"      
    }));
    this.overlay = overlay;
    this.thumbDataView = thumbDataView;
  }

  override public function getDragData(e:IEventObject):Object {
    //drag the selected items plus the current overlay
    var beans:Array = getBeans(thumbDataView.getSelectedRecords());
    if (overlay.getCatalogObject() && beans.indexOf(overlay.getCatalogObject()) < 0) {
      if (e.ctrlKey) {
        beans = getBeans(thumbDataView.getSelectedRecords()).concat(overlay.getCatalogObject());
      } else {
        beans = getBeans([]).concat(overlay.getCatalogObject());
      }
    }

    // create an appropriate drag proxy
    var ddel:* = window.document.createElement("div");
    ddel.className = 'x-grid-dd-wrap';

    ddel.innerHTML = CatalogDragDropVisualFeedback.getHtmlFeedback(beans);

    // generate a return object
    return {
      contentDragProvider: thumbDataView,
      contents: beans,
      ddel: ddel,
      repairXY: e.getXY()
    };
  }

  private function getBeans(records:Array):Array {
    var beans:Array = [];
    records.forEach(function (record:Record):void {
      if (record is BeanRecord) {
        beans.push((record as BeanRecord).getBean());
      }
    });
    return beans;
  }

  override public function getRepairXY(e:IEventObject):Array {
    return this.dragData['repairXY'];
  }

}
}
