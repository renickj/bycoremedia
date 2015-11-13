package com.coremedia.ecommerce.studio.components {
import com.coremedia.ecommerce.studio.config.abstractCatalogList;
import com.coremedia.ecommerce.studio.dragdrop.CatalogDragDropVisualFeedback;
import com.coremedia.ui.data.ValueExpression;

import ext.grid.GridPanel;

public class AbstractCatalogListBase extends GridPanel {

  public native function get selectedItemsValueExpression():ValueExpression;

  public function AbstractCatalogListBase(config:abstractCatalogList) {
    super(config);
    addListener('afterrender', addDragZoneToContentLinkDDGroup, { single: true });
  }

  private function addDragZoneToContentLinkDDGroup():void {
    getView().dragZone.addToGroup("ContentLinkDD");
  }

  /**
   * Override GridPanels getDragDropText() method.
   * The default drag'n'drop ui feedback (number of selected rows) is replaced with
   * the one defined in the CatalogDragDropVisualFeedback class.
   *
   * The return value of this method is picked up by the GridDragZone class if
   * enableDragDrop is enabled for this GridPanel.
   *
   * @return HTML fragment to show inside drag'n'drop feedback div
   */
  public override function getDragDropText():String {
    // Do not use selectedXYZExpression to get drag item as the value expression
    // might still reference a previously selected bean (race condition)
    // GridView - GridDragZone - BeanRecord - Bean (CatalogObject)
    return CatalogDragDropVisualFeedback.getHtmlFeedback(getView().dragZone['dragData'].selections);
  }

}
}