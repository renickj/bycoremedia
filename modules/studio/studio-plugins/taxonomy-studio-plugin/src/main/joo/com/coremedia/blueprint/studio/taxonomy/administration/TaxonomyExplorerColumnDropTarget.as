package com.coremedia.blueprint.studio.taxonomy.administration {

import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;

import ext.Ext;
import ext.IEventObject;
import ext.config.droptarget;
import ext.data.Record;
import ext.dd.DragDropMgr;
import ext.dd.DragSource;
import ext.dd.DropTarget;
import ext.grid.RowSelectionModel;

public class TaxonomyExplorerColumnDropTarget extends DropTarget {
  private var column:TaxonomyExplorerColumnBase;
  private var expander:ColumnExpander;

  public function TaxonomyExplorerColumnDropTarget(component:TaxonomyExplorerColumnBase) {
    super(component.getEl(), droptarget({
      ddGroup:"taxonomies"
    }));
    this.column = component;
  }

  override public function notifyEnter(source:DragSource, e:IEventObject, data:Object):String {
    return notifyOver(source, e, data);
  }

  override public function notifyOver(source:DragSource, e:IEventObject, data:Object):String {
    var json:* = data.selections[0].data;
    var sourceNode:TaxonomyNode = new TaxonomyNode(json);
    var targetNode:TaxonomyNode = isWriteable(data, e);
    if(!targetNode) {
      if(expander) {
        expander.cancel();
        expander = null;
      }
      return dropNotAllowed;
    }

    //display the new columns if a node (not leaf) is hovered long enough
    if(targetNode.getLevel() >= sourceNode.getLevel() && !isAlreadyExpanded(targetNode)) { //=>we can not destroy our drag source, so we can only expand child columns
      expand(targetNode);
    }
    else if(expander) {
      expander.cancel();
    }

    return dropAllowed;
  }

  /**
   * Checks if the current column is already expanded, which means
   * it is already resolved as parent.
   * @param targetNode
   * @return
   */
  private function isAlreadyExpanded(targetNode:TaxonomyNode):Boolean {
    return targetNode.getRef() == column.getParentNode().getRef();
  }

  /**
   * Expand the node if is not a
   * @param activeTargetNode
   */
  private function expand(targetNode:TaxonomyNode):void {
    if(!expander) {
      expander = new ColumnExpander(targetNode);
      expander.expand();
      return;
    }

    //check existing expandler if the hovered not is already expanding.
    if(!expander.expands(targetNode)) {
      expander.cancel();
      expander = new ColumnExpander(targetNode);
      expander.expand();
    }
  }

  override public function notifyOut(source:DragSource, e:IEventObject, data:Object):void {
    //nothing
  }

  override public function notifyDrop(source:DragSource, e:IEventObject, data:Object):Boolean {
    var json:* = data.selections[0].data;
    var sourceNode:TaxonomyNode = new TaxonomyNode(json);

    var targetNode:TaxonomyNode = isWriteable(data, e);
    if(!targetNode) {
      return false;
    }

    var taxonomyExplorer:TaxonomyExplorerPanel = Ext.getCmp('taxonomyExplorerPanel') as TaxonomyExplorerPanel;
    taxonomyExplorer.moveNode(sourceNode, targetNode);
    return true;
  }

  /**
   * Checks if a drop can be performed.
   * @param data
   * @param e
   * @return
   */
  private function isWriteable(data:Object, e:IEventObject):TaxonomyNode {
    var json:* = data.selections[0].data;
    var sourceNode:TaxonomyNode = new TaxonomyNode(json);

    //check if the mouse if over a region with records
    var target:* = e.getTarget();
    var rowIndex:Number = column.getView().findRowIndex(target);
    if(!column.getStore().getAt(rowIndex)) { //we drop an a column, not on a specific node
      //no drop on the root column, only on root nodes!
      if(column.getItemId() === 'taxonomyRootsColumn') {
        return null;
      }

      //check if the dragged node is hovering over a column that is a child of it
      //this could be enabled but an additional check is missing then: if the new parent is the dragged node itself!
      if(parseInt(column.getItemId().split("-")[1]) > parseInt(data.grid.getItemId().split("-")[1])) {
        return null;
      }

      if(column.getItemId() !== data.grid.getItemId()) {
        var parentNode:TaxonomyNode = column.getParentNode();
        return parentNode;
      }
      return null;
    }
    var targetJson:* = column.getStore().getAt(rowIndex).data;
    var targetNode:TaxonomyNode = new TaxonomyNode(targetJson);

    //check if the mouse is still inside the dragged record
    if (sourceNode.getRef() === targetNode.getRef()) {
      return null;
    }

    //check if we are still inside the same taxonomy tree
    if (sourceNode.getTaxonomyId() !== targetNode.getTaxonomyId()) {
      return null;
    }

    //check if the dragged node is a parent of the entered node
    if(targetNode.getLevel() > sourceNode.getLevel()) {
      return null;
    }

    //check if the mouse is on the immediate parent, so dropping makes no sense (and also leads to errors)
    //We using the fact here that the parent must be the selected node of the corresponding column since
    //we can not determine the parent synchronously.
    var taxonomyExplorer:TaxonomyExplorerPanel = Ext.getCmp('taxonomyExplorerPanel') as TaxonomyExplorerPanel;
    var targetColumn:TaxonomyExplorerColumn = taxonomyExplorer.getColumnContainer(targetNode);
    var selected:Record = (targetColumn.getSelectionModel() as RowSelectionModel).getSelected();
    if(targetNode.getLevel() === sourceNode.getLevel()-1 && selected.data.ref === targetNode.getRef()) { //direct parent and selected check
      return null;
    }
    DragDropMgr.refreshCache({taxonomies:true}); //new drop zones are not registered during a drag!!!!!
    return targetNode;
  }
}
}

