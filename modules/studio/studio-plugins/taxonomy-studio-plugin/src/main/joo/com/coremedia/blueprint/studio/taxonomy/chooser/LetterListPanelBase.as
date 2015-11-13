package com.coremedia.blueprint.studio.taxonomy.chooser {
import com.coremedia.blueprint.studio.config.taxonomy.letterListPanel;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderFactory;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderer;
import com.coremedia.cap.content.Content;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;

import ext.Ext;
import ext.config.rowselectionmodel;
import ext.data.Record;
import ext.grid.GridPanel;
import ext.grid.RowSelectionModel;

import js.Element;
import js.Event;

/**
 * Displays the active taxonomy node sorted alphabetically.
 */
public class LetterListPanelBase extends GridPanel {

  private var selectedPositionsExpression:ValueExpression;
  private var selectedValuesExpression:ValueExpression;
  private var listValuesExpression:ValueExpression;
  private var activeLetters:ValueExpression;
  private var selectedLetter:ValueExpression;
  private var selectionExpression:ValueExpression;

  private var selectedNodeId:ValueExpression;
  private var selectedNodeList:ValueExpression;

  private var taxonomyId:String;
  private var activeNodeList:TaxonomyNodeList;

  //used for skipping letter column rendering
  private var letter2NodeMap:Bean;

  private var singleSelection:Boolean;

  public function LetterListPanelBase(config:letterListPanel) {
    config.sm = new RowSelectionModel(rowselectionmodel({singleSelect:true}));
    super(config);
    singleSelection = config.singleSelection;
    activeLetters = config.activeLetters;
    taxonomyId = config.taxonomyId;
    activeLetters = config.activeLetters;

    selectionExpression = config.selectionExpression;
    selectionExpression.addChangeListener(updateAll);

    selectedNodeId = config.selectedNodeId;
    selectedNodeList = config.selectedNodeList;
    selectedNodeList.addChangeListener(updateUI);

    selectedLetter = config.selectedLetter;
    selectedLetter.addChangeListener(updateSelectedLetter);

    addListener('dblclick', onDblClick);
    addListener('afterrender', initList);
  }


  private function initList():void {
    selectedNodeId.setValue(taxonomyId); //lets start with the root level to show

    mon(getEl(), "click", selectedNodeClicked);
  }

  protected function getSelectedPositionsExpression():ValueExpression {
    if (!selectedPositionsExpression) {
      selectedPositionsExpression = ValueExpressionFactory.create("positions", beanFactory.createLocalBean());
    }
    return selectedPositionsExpression;
  }

  protected function getSelectedValuesExpression():ValueExpression {
    if (!selectedValuesExpression) {
      selectedValuesExpression = ValueExpressionFactory.create("values", beanFactory.createLocalBean());
    }
    return selectedValuesExpression;
  }

  protected function getListValuesExpression():ValueExpression {
    if (!listValuesExpression) {
      listValuesExpression = ValueExpressionFactory.create("nodes", beanFactory.createLocalBean());
    }
    return listValuesExpression;
  }

  /**
   * Selects the entry in the list with the active letter
   */
  private function updateSelectedLetter():void {
    var letter:String = selectedLetter.getValue();
    if (letter) {
      for (var i:int = 0; i < getStore().getCount(); i++) {
        var record:Record = getStore().getAt(i);
        var name:String = record.data.name;
        if (name.substring(0, 1).toLowerCase() === letter) {
          var sm:RowSelectionModel = (getSelectionModel() as RowSelectionModel);
          sm.selectRecords([record]);
          getView().focusRow(i);
          break;
        }
      }
    }
  }

  /**
   * Refresh the path and list and button column.
   * @param list
   */
  private function updateUI():void {
    var list:TaxonomyNodeList = selectedNodeList.getValue();
    if (list) {
      getListValuesExpression().setValue(list);
      activeNodeList = list;
      letter2NodeMap = beanFactory.createLocalBean();
      updateLetterList(list);
      convertNodeListToContentList();
    }
  }


  /**
   * Fills the letter value expression with an array of the active letters.
   * @param list
   */
  private function updateLetterList(list:TaxonomyNodeList):void {
    var letters:Array = [];
    var nodes:Array = list.getNodes();
    for (var i:int = 0; i < nodes.length; i++) {
      var name:String = nodes[i].getName();
      letters.push(name.substr(0, 1).toLowerCase());
    }
    activeLetters.setValue(letters);
  }


  /**
   * Fired when the user double clicks a row.
   * The next taxonomy child level of the selected node is entered then.
   */
  private function onDblClick():void {
    var selectedRecord:BeanRecord = (getSelectionModel() as RowSelectionModel).getSelected() as BeanRecord;
    if (selectedRecord) {
      var content:Content = selectedRecord.getBean() as Content;
      var id:String = TaxonomyUtil.getRestIdFromCapId(content.getId());
      if (!activeNodeList.getNode(id).isLeaf()) {
        //fire event for path update
        selectedNodeId.setValue(id);
      }
    }
  }

  private function convertNodeListToContentList():void {
    var contents:Bean = beanFactory.createLocalBean();
    var count:int = activeNodeList.getNodes().length;
    for (var i:int = 0; i < activeNodeList.getNodes().length; i++) {
      var item:TaxonomyNode = activeNodeList.getNodes()[i];
      var child:Content = beanFactory.getRemoteBean(item.getRef()) as Content;
      child.load(function (bean:Content):void {
        var id:String = TaxonomyUtil.parseRestId(bean);
        contents.set(id, bean);
        count--;
        if (count === 0) {
          sortAndApplyContentList(contents);
        }
      });
    }
  }

  /**
   * We do sort by the name of the node, not by the content!!!!!!
   * @param contents
   */
  private function sortAndApplyContentList(contents:Bean):void {
    var sortedContentArray:Array = [];
    var nodes:Array = activeNodeList.getNodes();
    for (var i:int = 0; i < nodes.length; i++) {
      var c:Content = contents.get(nodes[i].getRef());
      sortedContentArray.push(c);
    }
    getListValuesExpression().setValue(sortedContentArray);
  }


  /**
   * Displays each name of a taxonomy
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function taxonomyRenderer(value:*, metaData:*, record:BeanRecord):String {
    var content:Content = record.getBean() as Content;
    var node:TaxonomyNode = activeNodeList.getNode(TaxonomyUtil.getRestIdFromCapId(content.getId()));

    var selected:Boolean = isInSelection(node.getRef());
    var selectionExists:Boolean = selectionExpression.getValue() && selectionExpression.getValue().length === 1;

    var renderer:TaxonomyRenderer = null;
    if(singleSelection) {
      renderer = TaxonomyRenderFactory.createSingleSelectionListRenderer(node, getId(), selected, selectionExists);
    }
    else {
      renderer = TaxonomyRenderFactory.createSelectionListRenderer(node, getId(), selected);
    }

    renderer.doRender();
    var html:String = renderer.getHtml();
    return html;
  }

  /**
   * Displays each letter of a taxonomy
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function letterRenderer(value:*, metaData:*, record:BeanRecord):String {
    var content:Content = record.getBean() as Content;
    var node:TaxonomyNode = activeNodeList.getNode(TaxonomyUtil.getRestIdFromCapId(content.getId()));
    var letter:String = node.getName().substr(0, 1).toUpperCase();

    var html:String = '<span style="display:block;width:8px;"><b>&nbsp;</b></span>';
    if (!letter2NodeMap.get(letter) || letter2NodeMap.get(letter).getRef() === node.getRef()) {
      html = '<span style="display:block;width:8px;"><b>' + letter + '</b></span>';
      letter2NodeMap.set(letter, node);
    }

    return html;
  }

  /**
   * Renders an arrow if the given node record has children to step into.
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function parentRenderer(value:*, metaData:*, record:BeanRecord):String {
    var content:Content = record.getBean() as Content;
    var node:TaxonomyNode = activeNodeList.getNode(TaxonomyUtil.getRestIdFromCapId(content.getId()));
    return node.isLeaf() ? "" : '<a class="arrow-link" href="#">&nbsp;</a>';
  }


  /**
   * Handler executed when the node text is clicked on.
   * @param btn The text link button.
   * @param e
   */
  public function selectedNodeClicked(event:Event):void {
    if(event) {
      var target:Element = event.target;
      if (Ext.fly(target).hasClass('arrow-link') || Ext.fly(target).hasClass('tag-link')) {
        onDblClick(); //has the same behaviour like when double clicking a row.
        event.preventDefault();
        event.stopPropagation();
      }
    }
  }

  /**
   * Handler executed when the plus button is clicked.
   * @param btn
   * @param e
   */
  public function plusMinusClicked(nodeRef:String):void {
    var alreadySelected:Boolean = !isInSelection(nodeRef);
    if (alreadySelected) {
      //add to cache so that after the reload of the current level, the node is marked as not addable.
      TaxonomyUtil.addNodeToSelection(selectionExpression, nodeRef);
    }
    else {
      TaxonomyUtil.removeNodeFromSelection(selectionExpression, nodeRef);
    }
  }

  /**
   * Utility method that checks if the given node is already part of the active selection list.
   * @param node The node to check if in selection.
   * @return
   */
  private function isInSelection(contentId:String):Boolean {
    var selection:Array = selectionExpression.getValue();
    if (selection) {
      for (var i:int = 0; i < selection.length; i++) {
        var selectedContent:Content = selection[i];
        var restId:String = TaxonomyUtil.parseRestId(selectedContent);
        if (restId === contentId) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Executes a commit on the record for the given node.
   * @param node The node to refresh the row for.
   */
  private function updateNode(node:TaxonomyNode):void {
    if (singleSelection) {
      updateAll();
    }
    else {
      for (var i:int = 0; i < getStore().getCount(); i++) {
        var record:Record = getStore().getAt(i);
        var restId:String = TaxonomyUtil.getRestIdFromCapId(record.data.id);
        if (restId === node.getRef()) {
          record.commit(false);
          break;
        }
      }
    }

  }

  /**
   * Executes a commit on all records.
   */
  private function updateAll():void {
    for (var i:int = 0; i < getStore().getCount(); i++) {
      var record:Record = getStore().getAt(i);
      record.commit(false);
    }
  }
}
}
