package com.coremedia.blueprint.studio.taxonomy.selection {

import com.coremedia.blueprint.studio.config.taxonomy.taxonomyLinkListPropertyFieldGridPanel;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderFactory;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderer;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.Editor_properties;
import com.coremedia.cms.editor.sdk.dragdrop.ContentDragProvider;
import com.coremedia.cms.editor.sdk.dragdrop.DragDropVisualFeedback;
import com.coremedia.cms.editor.sdk.dragdrop.DragInfo;
import com.coremedia.cms.editor.sdk.util.ContentLocalizationUtil;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;
import com.coremedia.ui.util.EventUtil;

import ext.Element;
import ext.Ext;
import ext.IEventObject;
import ext.config.droptarget;
import ext.dd.DragSource;
import ext.dd.DropTarget;
import ext.dd.ScrollManager;
import ext.grid.GridPanel;
import ext.grid.RowSelectionModel;
import ext.util.StringUtil;

/**
 * @private
 *
 * The application logic for a property field editor that edits
 * link lists. Links can be limited to documents of a given type.
 *
 * @see com.coremedia.cms.editor.sdk.config.linkListPropertyFieldGridPanelBase
 * @see com.coremedia.cms.editor.sdk.premular.fields.LinkListPropertyField
 */
public class TaxonomyLinkListPropertyFieldGridPanelBase extends GridPanel implements ContentDragProvider {

  public static const EMPTY_CLASS:String = "link-list-grid-panel-empty";
  public static const INSERT_GRID_ROW_BELOW:String = "grid-row-insert-below";
  public static const INSERT_GRID_ROW_ABOVE:String = "grid-row-insert-above";

  private var bindTo:ValueExpression;
  private var propertyValueExpression:ValueExpression;
  private var propertyName:String;

  private var selectedPositionsExpression:ValueExpression;
  private var selectedValuesExpression:ValueExpression;
  private var forceReadOnlyValueExpression:ValueExpression;

  private var recordType:Class;
  private var scrollable:Boolean = false;
  private var dropTarget:DropTarget;
  private var currentRowEl:*;

  private var selectionMode:Boolean;
  private var taxonomyId:String;

  /**
   * @param config The configuration options. See the config class for details.
   *
   * @see com.coremedia.cms.editor.sdk.config.linkListPropertyFieldGridPanelBase
   */
  public function TaxonomyLinkListPropertyFieldGridPanelBase(config:taxonomyLinkListPropertyFieldGridPanel) {
    super(config);
    taxonomyId = config.taxonomyId;
    forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;

    if (propertyName) {
      selectionMode = false;
    }
    else {
      addClass('simple-list');
      selectionMode = true;
      getTopToolbar().removeAll(true);
    }


    var columns:Array = config["columns"];
    var fields:Array = [];
    for (var i:Number = 0; i < columns.length; i++) {
      fields.push(columns[i]["dataIndex"]);
    }
    recordType = BeanRecord.create(fields, false);

    this.addListener("render", onRender);
    this.addListener("resize", refreshHandler);
    this.addListener("afterlayout", refreshLinkList);

  }

  /**
   * Creates the bind list plugin value expression, depending on a property name is set or not.
   * If no property name is set, then a temporary selection expression is used and the real assignment
   * to the property value is done outside this list.
   * @param ve
   * @param propName
   * @return
   */
  protected function getBindListValueExpression(ve:ValueExpression, propName:String):ValueExpression {
    if (propName) {
      bindTo = ve;
      propertyName = propName;
      propertyValueExpression = ve.extendBy('properties', propName);
      propertyValueExpression.addChangeListener(toggleEmptyGridClass);
    }
    else {
      propertyValueExpression = ve;
      propertyValueExpression.addChangeListener(addScrollToEndListener);
    }

    return propertyValueExpression;
  }

  protected function toggleEmptyGridClass(source:ValueExpression):void {
    if (!source || !source.getValue() || source.getValue().length == 0) {
      this.addClass(EMPTY_CLASS);
    } else {
      this.removeClass(EMPTY_CLASS);
    }
  }

  protected function formatUnreadableName(record:BeanRecord):String {
    var content:Content = record.getBean() as Content;
    return ContentLocalizationUtil.formatNotReadableName(content);
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Status icon can not be formatted, so we simply return
   * an empty string here and let the icon factory display
   * an empty icon.
   * @param record
   * @return
   */
  protected function formatUnreadableStatus(record:BeanRecord):String {
    return "";
  }

  //noinspection JSUnusedLocalSymbols
  protected function formatDataAccessError(error:*, record:BeanRecord):String {
    return "error accessing data";
  }

  public function getSelectedPositionsExpression():ValueExpression {
    if (!selectedPositionsExpression) {
      var selectedPositionsBean:Bean = beanFactory.createLocalBean({ positions:[] });
      selectedPositionsExpression = ValueExpressionFactory.create("positions", selectedPositionsBean);
    }
    return selectedPositionsExpression;
  }

  public function getSelectedValuesExpression():ValueExpression {
    if (!selectedValuesExpression) {
      var selectedValuesBean:Bean = beanFactory.createLocalBean({ values:[] });
      selectedValuesExpression = ValueExpressionFactory.create("values", selectedValuesBean);
    }
    return selectedValuesExpression;
  }

  /**
   * Always scrolls to the end.
   */
  private function addScrollToEndListener():void {
//    var count:int = getStore().getCount();
//    if(count > 0) {
//      var record:Record = getStore().getAt(count-1);
//      (getSelectionModel() as RowSelectionModel).selectRecords([record]);
//      getView().focusRow(getStore().getCount()-1);
//    }
  }

  private function updateModelsAfterDrag(beansBefore:Array, beansToInsert:Array, beansAfter:Array):void {
    // Clear the selections. The store will be updated eventually
    // and we cannot yet determine the rows that will be present
    // after the update.
    (getSelectionModel() as RowSelectionModel).clearSelections();

    // Concatenate the partial results and update the model.
    propertyValueExpression.setValue(beansBefore.concat(beansToInsert, beansAfter));
  }

  // Needs to be public so it can be accessed from separate drop dropTarget
  public function reorder(positions:Array, rowIndex:Number):void {
    var isSelected:Array = [];
    positions.forEach(function (position:Number):void {
      isSelected[position] = true;
    });

    var originalBeanList:Array = propertyValueExpression.getValue();

    var beansBefore:Array = [];
    var beansToInsert:Array = [];
    var beansAfter:Array = [];
    for (var j:Number = 0; j < originalBeanList.length; j++) {
      var bean:RemoteBean = originalBeanList[j] as RemoteBean;
      if (isSelected[j]) {
        beansToInsert.push(bean);
      } else if (j < rowIndex) {
        beansBefore.push(bean);
      } else {
        beansAfter.push(bean);
      }
    }

    updateModelsAfterDrag(beansBefore, beansToInsert, beansAfter);
  }

  private function insertFromOtherGrid(dragInfo:DragInfo, rowIndex:Number):void {
    //we do not have dnd in selection mode
    if (selectionMode) {
      return;
    }
    // foreign data is about to enter this list; is the data ok?
    if (dragInfo.hasInvalidIds(bindTo, propertyName)) {
      // no. the user should have received appropriate feedback, so just don't do it.
      return;
    }

    // Split the existing model into two halves based on the
    // computed row index.
    var originalBeanList:Array = propertyValueExpression.getValue();
    var beansBefore:Array = [];
    var beansAfter:Array = [];
    for (var j:Number = 0; j < originalBeanList.length; j++) {
      var bean:* = originalBeanList[j];
      if (j < rowIndex) {
        beansBefore.push(bean);
      } else {
        beansAfter.push(bean);
      }
    }

    // Compute the additional beans to insert into the list.
    var beansToInsert:Array = [];
    var otherContents:Array = dragInfo.getContents();
    for (var i:Number = 0; i < otherContents.length; i++) {
      var content:Content = otherContents[i];
      beansToInsert.push(content);
    }

    // Concatenate the partial results and update the model.
    updateModelsAfterDrag(beansBefore, beansToInsert, beansAfter);
  }

  private function notifyDrop(d:DragSource, e:IEventObject, data:Object):Boolean {
    var dragInfo:DragInfo = DragInfo.makeDragInfo(d, data, this);
    if (!dragInfo) {
      return false;
    }

    removeInsertClasses();

    // Is the current content modifiable at all?
    if (!isWritable()) {
      return false;
    }

    // determine the dropTarget row
    var rowIndex:Number = computeRowIndex(e);
    if (rowIndex < 0) {
      return false;
    }

    if (data.grid === this) {
      // reordering drag/drop within the property editor
      reorder(dragInfo.getPositions(), rowIndex);
    } else {
      // drag/drop from another grid
      insertFromOtherGrid(dragInfo, rowIndex);
    }
    return true;
  }

  private function removeInsertClasses():void {
    if (this.currentRowEl) {
      this.currentRowEl.removeClass(INSERT_GRID_ROW_BELOW);
      this.currentRowEl.removeClass(INSERT_GRID_ROW_ABOVE);
    }
  }

  private function isWritable():Boolean {
    //we do not have dnd in selection mode
    if (selectionMode) {
      return false;
    }

    var targetValue:* = bindTo.getValue();
    if (!(targetValue is Content)) {
      return false;
    }
    return !(targetValue as Content).isCheckedOutByOther();
  }

  /**
   * Given an events, compute the index of the row before which the cursor
   * is located. If the cursor is at the end of the list, return the length
   * of the list. If the cursor is outside the drop zone, return -1.
   *
   * @param e the event
   * @return the row index
   */
  private function computeRowIndex(e:IEventObject):Number {
    // Loose the type of getTarget(). Somehow the typing of getTarget()
    // and findRowIndex() is broken. One may definitely pass the dropTarget as
    // shown here.
    var target:* = e.getTarget();

    // Is the dropTarget part of the DOM owned by this component?
    // At times, ExtJS dispatches move and drop events to the
    // wrong drop dropTarget.
    if (!isInDom(target)) {
      // The event does not belong here.
      return -1;
    }

    if (isStoreEmpty()) {
      // Yes. Be generous with respect to the drop position. There cannot
      // be much of a discussion.
      return 0;
    }

    // Drop before this row.
    var rowIndex:Number = this.getView().findRowIndex(target);

    // If the destination lies outside the grid, indicate a rejection by
    // returning -1;
    if (rowIndex < 0 || rowIndex === false) {
      return -1;
    } else {
      // Check cursor position relative to the center of the row.
      // Find position of row relative to page (adjusting for grid's scroll position)
      var currentRow:* = this.getView().getRow(rowIndex);
      var rowTop:Number = new Element(currentRow).getY() - getScrollerDom().scrollTop;
      var rowHeight:Number = currentRow.offsetHeight;
      if (e.getPageY() - rowTop - (rowHeight / 2) > 0) {
        // In lower half.
        return rowIndex + 1;
      }
      return rowIndex;
    }
  }

  private function isInDom(el:*):Boolean {
    var isInDom:Boolean = false;
    for (var loopEl:* = el; loopEl; loopEl = loopEl.parentNode) {
      if (loopEl.id === getId()) {
        isInDom = true;
        break;
      }
    }
    return isInDom;
  }

  private function isStoreEmpty():Boolean {
    return getStore().getCount() == 0;
  }

  private function notifyOver(d:DragSource, e:IEventObject, data:Object):String {
    var dragInfo:DragInfo = DragInfo.makeDragInfo(d, data, this);
    if (!dragInfo) {
      return dropTarget.dropNotAllowed;
    }

    // Indicate that no drop is possible. If it is, we add new
    // style classes later.
    removeInsertClasses();

    // Is the edited content able to receive a new value?
    if (!isWritable()) {
      return dropTarget.dropNotAllowed;
    }

    if (!dragInfo.isLocalDrag()) {
      // Foreign data is about to enter this list; is the data ok?
      if (dragInfo.hasInvalidIds(bindTo, propertyName)) {
        return dropTarget.dropNotAllowed;
      }
    }

    var rowIndex:Number = computeRowIndex(e);

    // Do not allow a drop outside the grid.
    if (rowIndex < 0) {
      return dropTarget.dropNotAllowed;
    }

    if (dragInfo.isLocalDrag()) {
      // One might want to disallow a drop if no reordering would happen.
    }

    try {
      // Clear drag line.
      if (this.currentRowEl) {
        this.currentRowEl.removeClass(INSERT_GRID_ROW_BELOW);
        this.currentRowEl.removeClass(INSERT_GRID_ROW_ABOVE);
      }

      if (rowIndex > 0) {
        var previousRow:* = this.getView().getRow(rowIndex - 1);
        this.currentRowEl = new Element(previousRow);
        this.currentRowEl.addClass(INSERT_GRID_ROW_BELOW);
      } else {
        // If the pointer is on the top half of the first row.
        var firstRow:* = this.getView().getRow(0);
        this.currentRowEl = new Element(firstRow);
        this.currentRowEl.addClass(INSERT_GRID_ROW_ABOVE);
      }
    }
    catch (err:*) {
      return dropTarget.dropNotAllowed;
    }
    return (rowIndex === false) ? dropTarget.dropNotAllowed : dropTarget.dropAllowed;
  }

  //noinspection JSUnusedLocalSymbols
  private function notifyOut(d:*, e:IEventObject, data:Object):void {
    // Remove drag lines when pointer leaves the gridView.
    if (this.currentRowEl) {
      this.currentRowEl.removeClass(INSERT_GRID_ROW_ABOVE);
      this.currentRowEl.removeClass(INSERT_GRID_ROW_BELOW);
    }
  }

  private function onRender():void {
    //we do not have dnd in selection mode
    if (selectionMode) {
      return;
    }

    //noinspection JSUnusedGlobalSymbols
    dropTarget = new DropTarget(this.getEl(),
      new droptarget({
        ddGroup:'ContentLinkDD',
        gridDropTarget:this,
        notifyDrop:notifyDrop,
        notifyOver:notifyOver,
        notifyEnter:notifyOver,
        notifyOut:notifyOut
      }));
    dropTarget.addToGroup("ContentDD");

    if (this.scrollable) {
      ScrollManager.register(getScrollerDom());
      this.addListener("beforedestroy", onBeforeDestroy, this, {single:true});
    }

    // Make sure that empty grid panels are displayed correctly at initial form rendering
    this.toggleEmptyGridClass(propertyValueExpression);
  }

  /**
   * Handler function that refreshes the grid view.
   */
  private function refreshHandler():void {
    if (this.isVisible()) {
      this.getView().refresh();
    }
  }

  private function onBeforeDestroy():void {
    // if we previously registered with the scroll manager, unregister
    // it (if we don't, it will lead to problems in IE)
    ScrollManager.unregister(getScrollerDom());
  }

  /**
   * Return the DOM element associated with the scroller of the grid.
   * This method uses undocumented API.
   *
   * @return the DOM element
   */
  private function getScrollerDom():* {
    return this.getView()['scroller']['dom'];
  }

  public function isLinking():Boolean {
    return true;
  }

  public function notifyDropSuccessful(dragDropType:String, dragInfo:DragInfo):void {
    // do nothing
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
    var selectedContent:Array = this.selectedValuesExpression.getValue();
    return DragDropVisualFeedback.getHtmlFeedback(selectedContent);
  }

  /**
   * Displays each path item of a taxonomy
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function taxonomyRenderer(value:*, metaData:*, record:BeanRecord):String {
    var content:Content = null;
    if(bindTo) {
      content = bindTo.getValue();
    }
    TaxonomyUtil.isEditable(taxonomyId, function (editable:Boolean):void {
      if (editable) {
        TaxonomyUtil.loadTaxonomyPath(record, content, taxonomyId, function (updatedRecord:BeanRecord):void {
          var renderer:TaxonomyRenderer = null;
          if (selectionMode) {
            renderer = TaxonomyRenderFactory.createSelectedListRenderer(record.data.nodes, getId(), propertyValueExpression.getValue().length > 3);
          }
          else {
            renderer = TaxonomyRenderFactory.createLinkListRenderer(record.data.nodes, getId());
          }
          renderer.doRender(function (html:String):void {
            if (record.data.html !== html) {
              record.data.html = html;
              record.commit(false);
            }
          });
        });
      }
      else {
        var msg:String = StringUtil.format(Editor_properties.INSTANCE.Content_notReadable_text, IdHelper.parseContentId(record.getBean()));
        var html:String = '<img width="16" height="16" class="content-type-xs cm-no-rights-name" ' +
                'style="vertical-align:middle;width:16px;height:16px;float:left;margin-top: 2px;" src="'
                + Ext.BLANK_IMAGE_URL + '" ext:qtip="" />'
                + '<div class="x-grid3-cell-inner x-grid3-col-name" unselectable="on">' + msg + '</div>';
        if (record.data.html !== html) {
          record.data.html = html;
          EventUtil.invokeLater(function ():void {
            record.commit(false);
          });
        }
      }
    }, record.getBean() as Content);

    if (!record.data.html) {
      return "<div class='loading'>" + TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_status_loading_text + "</div>";
    }
    return record.data.html;
  }

  /**
   * Executes after layout, we have to refresh the HTML too.
   */
  private function refreshLinkList():void {
    for(var i:int = 0; i<getStore().getCount(); i++) {
      getStore().getAt(i).data.html = null;
    }
    
    if(selectionMode) {
      getTopToolbar().getEl().applyStyles("border-color:#FFF;");
    }
  }

  /**
   * Removes the given taxonomy
   */
  public function plusMinusClicked(nodeRef:String):void {
    if(forceReadOnlyValueExpression) { //is null when used in the taxonomy chooser dialog
      forceReadOnlyValueExpression.loadValue(function():void {
        if (!forceReadOnlyValueExpression.getValue()) {
          var propertyValueExpression:ValueExpression = ValueExpressionFactory.create('properties.' + propertyName, bindTo.getValue());
          TaxonomyUtil.removeNodeFromSelection(propertyValueExpression, nodeRef);
        }
      });
    }
    else { //taxonomy chooser dialog else
      TaxonomyUtil.removeNodeFromSelection(propertyValueExpression, nodeRef);
    }
  }

  override protected function beforeDestroy():void {
    dropTarget && dropTarget.unreg();
    super.beforeDestroy();
  }
}
}
