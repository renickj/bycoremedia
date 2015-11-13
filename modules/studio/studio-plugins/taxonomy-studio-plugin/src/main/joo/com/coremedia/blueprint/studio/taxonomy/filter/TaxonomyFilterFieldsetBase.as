package com.coremedia.blueprint.studio.taxonomy.filter {

import com.coremedia.blueprint.studio.config.taxonomy.taxonomyFilterFieldset;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderFactory;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderer;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.undoc.content.ContentUtil;
import com.coremedia.cms.editor.sdk.collectionview.search.FilterFieldset;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;

import ext.util.StringUtil;

/**
 * The non-UI part of a filter for the collection view that allows to select
 * the taxonomies of documents to be included in the search result.
 */
public class TaxonomyFilterFieldsetBase extends FilterFieldset {

  /**
   * The filter property storing the keyword.
   */
  public static const TAXONOMIES_PROPERTY:String = "taxonomies";

  /**
   * The taxonomy id to work on.
   */
  private var taxonomyId:String;

  /**
   * Contains the active selection.
   */
  private var selectionExpression:ValueExpression;

  /**
   * Contains the search result.
   */
  private var searchResultExpression:ValueExpression;

  /**
   * The content property name that is used in the SOLR collection.
   */
  private var propertyName:String;

  /**
   * Create a new fieldset.
   *
   * @param config the configuration
   */
  public function TaxonomyFilterFieldsetBase(config:taxonomyFilterFieldset) {
    super(config);

    this.taxonomyId = config.taxonomyId;
    this.propertyName = config.propertyName;

    // Update the UI once and after state changes.
    getStateBean().addValueChangeListener(stateBeanChanged);
    stateBeanChanged();
  }

  /**
   * The model has changed. Update the UI.
   */
  private function stateBeanChanged():void {
    var stateBean:Bean = getStateBean();
    var selection:Array = stateBean.get(TAXONOMIES_PROPERTY) || [];

    var currentTaxonomies:Array = [];
    for (var i:uint = 0; i < selection.length; i++) {
      var content:Content = selection[i];
        currentTaxonomies.push(content);
      }
    getSelectionExpression().setValue(currentTaxonomies);
  }

  /**
   * Called when the user has made a selection.
   */
  private function selectionChanged():void {
    var selection:Array = getSelectionExpression().getValue();
    getStateBean().set(TAXONOMIES_PROPERTY, selection);
  }

  /**
   * Returns the value expression that contains the active selection.
   * @return
   */
  protected function getSelectionExpression():ValueExpression {
    if(!selectionExpression) {
      selectionExpression = ValueExpressionFactory.create('selection', beanFactory.createLocalBean());
      selectionExpression.addChangeListener(selectionChanged);
    }
    return selectionExpression;
  }

  /**
   * Returns the value expression that contains the current search result.
   * @return
   */
  protected function getSearchResultExpression():ValueExpression {
    if(!searchResultExpression) {
      searchResultExpression = ValueExpressionFactory.create('search', beanFactory.createLocalBean());
      searchResultExpression.addChangeListener(function():void {
        var selection:TaxonomyNodeList = searchResultExpression.getValue() as TaxonomyNodeList;
        if (selection) {
          var leafRef:String = selection.getLeafRef();
          var keyword:Content = ContentUtil.getContent(leafRef);
          keyword.load(function ():void {
            var values:Array = selectionExpression.getValue();
            if(values.indexOf(keyword) === -1) {
              values = values.concat(keyword);
            }
            selectionExpression.setValue(values);
          });
        }
      });
    }
    return searchResultExpression;
  }

  /**
   * Removes the given taxonomy. Invoked from the rendered selection.
   */
  public function plusMinusClicked(nodeRef:String):void {
    TaxonomyUtil.removeNodeFromSelection(selectionExpression, nodeRef);
  }

  /**
   * Displays each path item of a taxonomy
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function taxonomyRenderer(value:*, metaData:*, record:BeanRecord):String {
    TaxonomyUtil.loadTaxonomyPath(record, null, taxonomyId, function (updatedRecord:BeanRecord):void {
      var renderer:TaxonomyRenderer = TaxonomyRenderFactory.createSelectedListWithoutPathRenderer(record.data.nodes, getId(), false);
      renderer.doRender(function (html:String):void {
        if (record.data.html !== html) {
          record.data.html = html;
          record.commit(false);
        }
      })
    });
    if (!record.data.html) {
      return "<div class='loading'>" + TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_status_loading_text + "</div>";
    }
    return record.data.html;
  }

  /**
   * @inheritDoc
   */
  override public function buildQuery():String {
    var stateBean:Bean = getStateBean();
    var keywords:Array = stateBean.get(TAXONOMIES_PROPERTY) || [];
    if (keywords.length === 0) {
      // The entire filter can be omitted.
      return null;
    } else {
      var queryTerms:Array = [];
      for (var i:uint = 0; i < keywords.length; i++) {
        var keyword:Content = keywords[i];
        var param:int = IdHelper.parseContentId(keyword);
        queryTerms.push(StringUtil.format(propertyName.toLowerCase()+ ":{0}", param));
      }
      return queryTerms.join(" OR ");
    }
  }

  /**
   * @inheritDoc
   */
  override public function getDefaultState():Object {
    var state:Object = {};
    state[TAXONOMIES_PROPERTY] = [];
    return state;
  }
}
}