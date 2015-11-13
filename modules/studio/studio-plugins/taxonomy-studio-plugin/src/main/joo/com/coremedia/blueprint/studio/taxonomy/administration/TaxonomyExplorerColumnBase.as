package com.coremedia.blueprint.studio.taxonomy.administration {

import com.coremedia.blueprint.studio.config.taxonomy.taxonomyExplorerColumn;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeFactory;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.rendering.LinkListRenderer;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.util.EventUtil;

import ext.Ext;
import ext.KeyNav;
import ext.config.keynav;
import ext.config.rowselectionmodel;
import ext.data.Record;
import ext.data.Store;
import ext.grid.GridPanel;
import ext.grid.RowSelectionModel;

public class TaxonomyExplorerColumnBase extends GridPanel {

  private var globalSelectedNodeExpression:ValueExpression;
  private var entriesValueExpression:ValueExpression;
  private var siteSelectionExpression:ValueExpression;

  private var parentNode:TaxonomyNode;
  private var activeNode:TaxonomyNode;

  private var taxonomyExplorerColumnDropTarget:TaxonomyExplorerColumnDropTarget;

  public function TaxonomyExplorerColumnBase(config:taxonomyExplorerColumn) {
    config.sm = new RowSelectionModel(rowselectionmodel({singleSelect:true}));
    super(config);
    siteSelectionExpression = config.siteSelectionExpression;
    parentNode = config.parentNode;
    globalSelectedNodeExpression = config.selectedNodeExpression;
    initColumn();

    getSelectionModel().addListener('selectionchange', selectionChanged);
    addListener("afterrender", registerDropTarget);
    addListener('click', onPanelClick);
  }


  override protected function afterRender():void {
    super.afterRender();
    addKeyNavigation();
  }

  /**
   * Registers the additional key handlers for left/right navigation.
   */
  private function addKeyNavigation():void {
    new KeyNav(getEl(), keynav({
      "left":function (e) {
        if (globalSelectedNodeExpression) {
          activeNode = parentNode;
          globalSelectedNodeExpression.setValue(parentNode);
        }
      },
      "right":function (e) {
        if(activeNode.isExtendable()) {
          activeNode.loadChildren(function(list:TaxonomyNodeList):void {
            if(list.size() > 0) {
              var selectNode:TaxonomyNode = list.getNodes()[0];
              globalSelectedNodeExpression.setValue(selectNode);
              getExplorerPanel().getColumnContainer(selectNode).selectNode(selectNode, true);
            }
          });
        }
      },
      scope:this
    }));
  }

  /**
   * Triggered when the reload button is pressed.
   */
  public function reload():void {
    //mpf, bind list plugin can't handle a regular reload, so force resetting the whole list
    getEntriesValueExpression().setValue(undefined);
    EventUtil.invokeLater(function():void {
      initColumn(true);
    });
  }

  /**
   * Returns the parent node this column has been created for.
   * @return
   */
  public function getParentNode():TaxonomyNode {
    return parentNode;
  }

  public function getEntriesValueExpression():ValueExpression {
    if (!entriesValueExpression) {
      var emptyBean:Bean = beanFactory.createLocalBean();
      entriesValueExpression = ValueExpressionFactory.create("nodes", emptyBean);
    }
    return entriesValueExpression;
  }

  protected function selectionChanged():void {
    var selection:Record = (getSelectionModel() as RowSelectionModel).getSelected();
    var selectedNode:TaxonomyNode = undefined;
    if (selection) {
      selectedNode = TaxonomyNode.forValues(selection.data.name,
              selection.data.type,
              selection.data.ref,
              selection.data.siteId,
              selection.data.level,
              selection.data.root,
              selection.data.leaf,
              selection.data.taxonomyId,
              selection.data.selectable,
              selection.data.extendable);
    }

    if (globalSelectedNodeExpression) {
      activeNode = selectedNode;
      globalSelectedNodeExpression.setValue(selectedNode);
    }
  }

  /**
   * Selects the given node in the list
   * record entry.
   * @param node
   * @param force
   */
  public function selectNode(node:TaxonomyNode, force:Boolean = false):void {
    activeNode = node;
    if (force) {
      doSelect(); //selection on new item works only with this variant :(
    }
    else {
      addListener('viewready', doSelect); //initial selection works with this listener
    }
  }

  /**
   * Selects the active node or clears the selection
   * if the active node is not set.
   */
  private function doSelect():void {
    removeListener('viewready', doSelect);
    if (activeNode) {
      var nodeStore:Store = getStore();
      for (var i:int = 0; i < nodeStore.getCount(); i++) {
        var nodeRecord:Record = nodeStore.getAt(i);
        if (nodeRecord.data.ref === activeNode.getRef()) {
          (getSelectionModel() as RowSelectionModel).selectRecords([nodeRecord]);
          getView().focusRow(i);
        }
      }
      //not sure why I needed this, should be invoked later anyway (test with initial search)
      // getExplorerPanel().updateTaxonomyNodeForm(activeNode);
    }
    else {
      (getSelectionModel() as RowSelectionModel).deselectRange(0, getStore().getCount() - 1);
    }
  }

  /**
   * Searches the list for the given node and updates the
   * record entry.
   * @param node
   */
  public function updateNode(node:TaxonomyNode):Boolean {
    var nodeStore:Store = getStore();
    for (var i:int = 0; i < nodeStore.getCount(); i++) {
      var nodeRecord:Record = nodeStore.getAt(i);
      if (nodeRecord.data.ref === node.getRef()) {
        nodeRecord.data.name = node.getName();
        nodeRecord.data.type = node.getType();
        nodeRecord.data.root = node.isRoot();
        nodeRecord.data.extendable = node.isExtendable();
        nodeRecord.data.selectable = node.isSelectable();
        nodeRecord.data.leaf = node.isLeaf();
        nodeRecord.commit(false);
        return true;
      }
    }
    return false;
  }

  /**
   * Loads the values into the list.
   */
  public function initColumn(reload:Boolean = true):void {
    updateLoadStatus(TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyExplorerColumn_emptyText_loading);
    var callback:Function = function (list:TaxonomyNodeList):void {
      getEntriesValueExpression().setValue(list.toJson());
      if(list.toJson().length === 0) {
        updateLoadStatus(TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyExplorerColumn_emptyText_no_keywords);
      }
    };
    if (parentNode) {
      parentNode.loadChildren(callback);
    } else {
      var site:String = siteSelectionExpression.getValue();
      TaxonomyNodeFactory.loadTaxonomies(site, callback, reload);
    }
  }

  private function updateLoadStatus(text:String):void {
    getView()['emptyText'] = text;
    if (isVisible()) {
      getView().refresh(false);
    }
  }

  /**
   * Override GridPanels getDragDropText() method.
   * The default drag'n'drop ui feedback (number of selected rows) is replaced with
   * the one defined in the DragDropVisualFeedback class.
   *
   * The return value of this method is picked up by the GridDragZone class if
   * enableDragDrop is enabled for this GridPanel.
   *
   * @return HTML fragment to show inside drag'n'drop feedback div
   * @author cwe
   */
  public override function getDragDropText():String {
    var node:TaxonomyNode = globalSelectedNodeExpression.getValue();
    return node.getName();
  }

  /**
   * Displays the image for each link list item.
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function nameColRenderer(value:*, metaData:*, record:Record):String {
    var html:String = '';
    var name:String = TaxonomyUtil.escapeHTML(record.data.name);
    if(name.length === 0) {
      name = '<i>' + TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyExplorerColumn_undefined + '</i>';
    }
    if (record.data.root) {
      var displayName:String = TaxonomyUtil.escapeHTML(record.data.name);
      if (record.data.siteId) {
        var siteName:String = editorContext.getSitesService().getSite(record.data.siteId).getName();
        displayName += ' (' + siteName + ')';
      }
      html = '<div class="column-entry-default">' + displayName + '</div>';
    }
    else if (!record.data.extendable || !record.data.selectable) {
      html = '<div class="column-entry-not-extendable">' + name + '</div>';
    }
    else if (record.data.leaf) {
      html = '<div class="column-entry-leaf">' + name + '</div>';
    }
    else {
      html = '<div class="column-entry-default">' + name + '</div>';
    }

    if (getExplorerPanel().isMarkedForCopying(record.data.ref)) {
      html = '<span class="column-entries-marked-for-copy">' + html + '</span>';
    }
    return html;
  }


  /*
   * Create drop target for this component.
   */
  private function registerDropTarget():void {
    removeListener("afterrender", registerDropTarget);
    taxonomyExplorerColumnDropTarget = new TaxonomyExplorerColumnDropTarget(this);
  }


  override protected function beforeDestroy():void {
    taxonomyExplorerColumnDropTarget && taxonomyExplorerColumnDropTarget.unreg();
    super.beforeDestroy();
  }

  /**
   * Executed for a regular click on the panel, updates
   * backward selections that are on the same selection path.
   */
  private function onPanelClick():void {
    var node:TaxonomyNode = globalSelectedNodeExpression.getValue();
    if (activeNode && node && activeNode.getRef() !== node.getRef()) {
      selectNode(activeNode, true);
    }
  }

  /**
   * Returns the parent taxonomy explorer panel.
   * @return
   */
  private function getExplorerPanel():TaxonomyExplorerPanel {
    return Ext.getCmp('taxonomyExplorerPanel') as TaxonomyExplorerPanel;
  }

  /**
   * The pointer column renderer shows a '>' symbol if the node is not a leaf.
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function pointerColRenderer(value:*, metaData:*, record:Record):String {
    var leaf:Boolean = record.data.leaf;
    if (!leaf) {
      return '<span class="' + LinkListRenderer.ARROW_CLS + '"></span>';
    }
    return '';
  }
}
}
