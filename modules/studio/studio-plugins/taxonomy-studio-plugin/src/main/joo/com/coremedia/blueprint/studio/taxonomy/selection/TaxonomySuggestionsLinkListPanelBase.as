package com.coremedia.blueprint.studio.taxonomy.selection {
import com.coremedia.blueprint.studio.config.taxonomy.taxonomySuggestionsLinkListPanel;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderFactory;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderer;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.dragdrop.DragDropVisualFeedback;
import com.coremedia.cms.editor.sdk.util.ContentLocalizationUtil;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;
import com.coremedia.ui.util.EventUtil;

import ext.grid.GridPanel;

/**
 *
 */
public class TaxonomySuggestionsLinkListPanelBase extends GridPanel {
  private var selectedPositionsExpression:ValueExpression;
  private var selectedValuesExpression:ValueExpression;

  private var suggestionsExpression:ValueExpression;
  private var bindTo:ValueExpression;

  private var propertyValueExpression:ValueExpression;

  private var taxonomyId:String;
  private var cache:TaxonomyCache;

  /**
   * @param config The configuration options. See the config class for details.
   *
   * @see com.coremedia.cms.editor.sdk.config.linkListPropertyFieldGridPanelBase
   */
  public function TaxonomySuggestionsLinkListPanelBase(config:taxonomySuggestionsLinkListPanel) {
    super(config);

    if (!config.disableSuggestions) {
      bindTo = config.bindTo;

      propertyValueExpression = ValueExpressionFactory.create('properties.' + config.propertyName, bindTo.getValue());
      propertyValueExpression.addChangeListener(propertyChanged);
      taxonomyId = config.taxonomyId;

      cache = new TaxonomyCache(bindTo.getValue() as Content, propertyValueExpression, taxonomyId);
      updateSuggestions(true);
    }

    this.addListener("afterlayout", refreshLinkList);
  }

  /**
   * Fired when the taxonomy property of the content has been changed.
   * We use this event to refresh (not reload) the taxonomy list.
   * @param ve
   */
  private function propertyChanged(ve:ValueExpression):void {
    updateSuggestions(false);
  }

  protected function getSuggestionsExpression():ValueExpression {
    if (!suggestionsExpression) {
      suggestionsExpression = ValueExpressionFactory.create('hits', beanFactory.createLocalBean());
    }
    return suggestionsExpression;
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
    //todo localize that?
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
   * Loads the values into the list.
   */
  private function updateSuggestions(reload:Boolean = false):void {
    setBusy(true);

    var callback:Function = function(list:TaxonomyNodeList):void {
      if (list) {
        convertResultToContentList(list);
      }
    };

    if (reload) {
      cache.invalidate(callback);
    }
    else {
      cache.loadSuggestions(callback);
    }
  }

  /**
   * Updates the empty list label so that loading is indicated.
   * @param b
   */
  private function setBusy(b:Boolean):void {
    if (b) {
      getSuggestionsExpression().setValue([]);
      getView()['emptyText'] = TaxonomyStudioPlugin_properties.INSTANCE.TaxonomySuggestions_loading;
    }
    else {
      getView()['emptyText'] = TaxonomyStudioPlugin_properties.INSTANCE.TaxonomySuggestions_empty_text;
    }
    if (isVisible()) {
      getView().refresh(false);
    }
  }

  private function convertResultToContentList(list:TaxonomyNodeList):void {
    var items:Array = list.getNodes();
    var contents:Array = [];
    var callbackCount:int = items.length;
    for (var i:int = 0; i < items.length; i++) {
      var item:TaxonomyNode = items[i];
      var child:Content = beanFactory.getRemoteBean(item.getRef()) as Content;
      child.load(function(bean:Content):void {
        contents.push(bean);
        callbackCount--;
        if (callbackCount === 0) {
          getSuggestionsExpression().setValue(contents);
          setBusy(false);
        }
      });
    }
    if (items.length === 0) {
      getSuggestionsExpression().setValue([]);
      setBusy(false);
    }
  }

  /**
   * Adds all items of the list to the keyword list.
   */
  protected function addAllKeywordsHandler():void {
    var suggestions:Array = getSuggestionsExpression().getValue();
    var existingEntries:Array = propertyValueExpression.getValue();
    var newEntries:Array = [];
    for (var i:int = 0; i < existingEntries.length; i++) {
      newEntries.push(existingEntries[i]);
    }
    for (var j:int = 0; j < suggestions.length; j++) {
      newEntries.push(suggestions[j]);
    }
    propertyValueExpression.setValue(newEntries);
    EventUtil.invokeLater(function():void {
      getSuggestionsExpression().setValue([]);
      updateSuggestions(false);
    });
  }

  /**
   * Trigger a new evaluation of the content for suggestions.
   */
  protected function reloadKeywordsHandler():void {
    updateSuggestions(true);
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
    TaxonomyUtil.loadTaxonomyPath(record, bindTo.getValue(), taxonomyId, function (updatedRecord:BeanRecord):void {
      var content:Content = record.getBean() as Content;
      var renderer:TaxonomyRenderer = TaxonomyRenderFactory.createSuggestionsRenderer(record.data.nodes, getId(), cache.getWeight(content.getId()));
      renderer.doRender(function (html:String):void {
        if (record.data.html !== html) {
          record.data.html = html;
          record.commit(false);
        }
      })
    });
    if (!record.data.html) {
      return "<div class='loading'>" + TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_status_loading_text + "</div>"
    }
    return record.data.html;
  }

  /**
   * Executes after layout, we have to refresh the HTML too.
   */
  private function refreshLinkList():void {
    for(var i:int = 0; i<getStore().getCount(); i++) {
      getStore().getAt(i).data.html = null;
      getStore().getAt(i).commit(false);
    }
  }

  /**
   * Removes the given taxonomy
   */
  public function plusMinusClicked(nodeRef:String):void {
    TaxonomyUtil.removeNodeFromSelection(propertyValueExpression, nodeRef);
    TaxonomyUtil.addNodeToSelection(propertyValueExpression, nodeRef);
  }

  override protected function onDestroy():void {
    if (propertyValueExpression) {
      propertyValueExpression.removeChangeListener(propertyChanged);
    }
    super.onDestroy();
  }
}
}
