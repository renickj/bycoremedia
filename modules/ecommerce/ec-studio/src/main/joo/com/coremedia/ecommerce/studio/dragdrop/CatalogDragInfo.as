package com.coremedia.ecommerce.studio.dragdrop {

import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;

import ext.Component;
import ext.dd.DragSource;
import ext.grid.GridPanel;

public class CatalogDragInfo {
  private var localDrag:Boolean;
  private var catalogObjects:Array;
  private var positions:Array;

  public function CatalogDragInfo(localDrag:Boolean, catalogObjects:Array, positions:Array) {
    this.localDrag = localDrag;
    this.catalogObjects = catalogObjects;
    this.positions = positions;
  }

  /**
   * Create a drag info object for the drag gesture described by the argument.
   *
   * @param source the drag source
   * @param data the drag data provided by the drag source
   * @param target the target
   * @return the drag info
   */
  public static function makeDragInfo(source:DragSource, data:Object, target:Component):CatalogDragInfo {
    var ddGroups:Object = source.groups;

    if (data.contents) {
      // custom drag source
      return new CatalogDragInfo(false, data.contents, []);
    } else if (data.node) {
      // tree
      return new CatalogDragInfo(false, [beanFactory.getRemoteBean(data.node.id)], []);
    } else if (data.grid) {
      // grid
      var gridPanel:GridPanel = data.grid;
      var selections:Array = data.selections as Array;
      var catalogObjects:Array = selections.map(function (selection:BeanRecord):CatalogObject {
        return selection.getBean() as CatalogObject;
      });
      var positions:Array = selections.map(function (selection:BeanRecord):Number {
        return gridPanel.getStore().indexOf(selection);
      });
      return new CatalogDragInfo(data.grid === target, catalogObjects, positions);
    } else {
      return undefined;
    }
  }

  public function isLocalDrag():Boolean {
    return localDrag;
  }

  public function getCatalogObjects():Array {
    return catalogObjects;
  }

  public function getPositions():Array {
    return positions;
  }

}
}
