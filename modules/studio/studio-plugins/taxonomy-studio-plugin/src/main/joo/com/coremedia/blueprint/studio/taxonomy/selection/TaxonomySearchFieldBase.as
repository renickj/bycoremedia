package com.coremedia.blueprint.studio.taxonomy.selection {

import com.coremedia.blueprint.studio.config.taxonomy.taxonomySearchField;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderFactory;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderer;
import com.coremedia.blueprint.studio.util.AjaxUtil;
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.logging.Logger;

import ext.Container;
import ext.Ext;
import ext.Panel;
import ext.QuickTips;
import ext.XTemplate;
import ext.config.httpproxy;
import ext.config.jsonstore;
import ext.data.HttpProxy;
import ext.data.JsonStore;
import ext.data.Record;
import ext.form.ComboBox;

import js.XMLHttpRequest;

public class TaxonomySearchFieldBase extends ComboBox {

  /**
   * Name of the property of search suggestions result containing the search hits.
   * @eventType hits
   */
  private static const NODES:String = "nodes";

  /**
   * Name of the property of search suggestions result item containing the number of appearances of the suggested value.
   */
  private static const SUGGESTION_COUNT:String = "size";

  internal static var autoSuggestResultTpl:XTemplate = new XTemplate(
          '<tpl for="."><div class="taxonomy-search-item">',
          '{' + 'html' + '}</span>',
          '</div></tpl>'
  );

  private var searchResultExpression:ValueExpression;

  private var bindTo:ValueExpression;
  private var propertyName:String;

  private var showSelectionPath:Boolean;
  private var taxonomyId:String;

  // It is assumed that cachedValue always corresponds to a valid tag.
  // Consequently its always originates from onNodeSelection().
  private var cachedValue:*;

  private var siteSelectionExpression:ValueExpression;
  private var resetOnBlur:Boolean;

  private var taxonomySearchFieldDropTarget:TaxonomySearchFieldDropTarget;

  private var valueManuallyChanged:Boolean = false;
  private var forceReadOnlyValueExpression:ValueExpression;

  public function TaxonomySearchFieldBase(config:taxonomySearchField) {
    taxonomyId = config.taxonomyId;
    bindTo = config.bindTo;
    propertyName = config.propertyName;
    siteSelectionExpression = config.siteSelectionExpression;
    if (siteSelectionExpression) {
      siteSelectionExpression.addChangeListener(siteSelectionChanged);
    }
    searchResultExpression = config.searchResultExpression;
    showSelectionPath = config.showSelectionPath;

    if (showSelectionPath === undefined) {
      showSelectionPath = true;
    }

    this.resetOnBlur = config.resetOnBlur;

    if (taxonomyId === undefined) {
      taxonomyId = "";
    }

    forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;

    super(taxonomySearchField(Ext.apply({
      store:new JsonStore(new jsonstore({
        autoLoad:false,
        autoDestroy:true,
        proxy:new HttpProxy(httpproxy({
          url:'api/taxonomies/find?' + getTaxonomyIdParam() + getSiteParam(),
          method:'GET',
          failure:function (response:XMLHttpRequest):void {
            Logger.info('Taxonomy search request failed:' + response.responseText);
          }
        })),
        root:NODES,
        idProperty:'text',
        totalProperty:'size',
        fields:[
          {
            name:SUGGESTION_COUNT,
            type:'int'
          },
          {
            name:TaxonomyNode.PROPERTY_REF
          },
          {
            name:TaxonomyNode.PROPERTY_TAXONOMY_ID
          },
          {
            name:TaxonomyNode.PROPERTY_NAME
          },
          {
            name:TaxonomyNode.PROPERTY_HTML, mapping:renderHTML
          },
          {
            name:TaxonomyNode.PROPERTY_PATH
          }
        ]
      })),
      displayField:TaxonomyNode.PROPERTY_NAME,
      tpl:autoSuggestResultTpl
    }, config)));

    getStore().addListener('datachanged', validate);
    addListener("afterrender", validate);
    addListener('focus', doFocus);
    addListener('blur', doBlur);
    addListener('select', onNodeSelection);
    addListener('keydown', function ():void {
      valueManuallyChanged = true;
      QuickTips.getQuickTip().hide();
    });

    // Get reference to related grid panel component.
    // Reference is required to recognize and handle 'internal' drag and drop operations
    if (bindTo) {
      addListener("afterrender", registerDropTarget);
    }

    StudioUtil.getDocumentTabChangeExpression().addChangeListener(blurTextField);
    StudioUtil.getWorkAreaTabChangeExpression().addChangeListener(blurTextField);
  }

  /**
   * Called when an unexpected blur should be executed, like when the tab
   * of the Studio is changed, but the field has still the focus.
   */
  private function blurTextField(ve:ValueExpression):void {
    if(ve.getValue() && ve.getValue()) {
      var comp:Panel = ve.getValue();
      var fields:Array = comp.findByType(taxonomySearchField.xtype);
      if(fields) {
        fields.forEach(function(combo:*):void {
          combo.blur();
        });
      }
    }
  }

  private function siteSelectionChanged():void {
    reset();
    ((getStore() as JsonStore).proxy as HttpProxy).setUrl('api/taxonomies/find?' + getTaxonomyIdParam() + getSiteParam(), true);
  }

  /**
   * Creates the HTML that is displayed for the search hits.
   * @return
   */
  private function renderHTML(value:*):String {
    var nodes:Array = value.path.nodes;
    var renderer:TaxonomyRenderer = TaxonomyRenderFactory.createSearchComboRenderer(nodes);
    renderer.doRender();
    var html:String = renderer.getHtml();
    return html;
  }


  /*
   * Create drop target for this component.
   */
  private function registerDropTarget():void {
    removeListener("afterrender", registerDropTarget);
    taxonomySearchFieldDropTarget = new TaxonomySearchFieldDropTarget(this.findParentByType(Container), bindTo, propertyName, forceReadOnlyValueExpression);
  }

  private function isValidTagPrefix():Boolean {
    return (!getValue() || getStore().getCount() > 0);
  }

  protected function tagPrefixValidValidator():* {
    if (!valueManuallyChanged
            || getValue() === cachedValue
            || (minChars && getValue() && getValue().length < minChars)
            || isValidTagPrefix()) {
      return true;
    } else {
      return TaxonomyStudioPlugin_properties.INSTANCE.TaxonomySearch_no_hit;
    }
  }

  /**
   * The on blur event handler for the textfield/combo, resets the error status of the field.
   */
  public function doBlur():void {
    if (!resetOnBlur && cachedValue) {
      setValue(cachedValue);
    }
    else if(resetOnBlur) {
      setValue("");
      reset();
      getStore().removeAll(true);
    } else if (getValue() !== cachedValue) {
      setValue(null);
    }
  }

  /**
   * The on focus event handler for the textfield/combo, resets the status of the field.
   */
  public function doFocus():void {
    if(!resetOnBlur) {
      getStore().load({});
      if (getValue()) {
        cachedValue = getValue();
      }
    } else {
      setValue("");
    }
  }

  /**
   * Appends the taxonomy id param to the search query if set.
   * @return
   */
  private function getTaxonomyIdParam():String {
    if (taxonomyId) {
      return 'taxonomyId=' + taxonomyId;
    }
    return '';
  }

  /**
   * Returns the site param if there is a site selected.
   * @return
   */
  private function getSiteParam():String {
    if (siteSelectionExpression && siteSelectionExpression.getValue()) {
      return '&site=' + siteSelectionExpression.getValue();
    }
    return '';
  }

  /**
   * Sets the selected path as string of resets the textfield after selection.
   * @param selection
   */
  private function setSelectionString(selection:*):void {
    if (showSelectionPath) {
      setValue(selection);
    }
    else {
      setValue("");
    }
  }

  public function getEmptyText(ve:ValueExpression):String {
    if (ve) {
      return TaxonomyStudioPlugin_properties.INSTANCE.TaxonomySearch_empty_linklist_text;
    }
    return TaxonomyStudioPlugin_properties.INSTANCE.TaxonomySearch_empty_search_text;
  }

  /**
   * Handler function for node selection.
   * @param combo
   * @param record
   * @param index
   */
  private function onNodeSelection(combo:TaxonomySearchField, record:Record, index:Number):void {
    var content:Content = beanFactory.getRemoteBean(record.data[TaxonomyNode.PROPERTY_REF]) as Content;
    content.load(function (c:Content):void {
      setSelectionString(record.data.name);
      cachedValue = record.data.name;
      var path:TaxonomyNodeList = new TaxonomyNodeList(record.data.path.nodes);
      searchResultExpression.setValue(path);
    });
  }

  override protected function onDestroy():void {
    taxonomySearchFieldDropTarget && taxonomySearchFieldDropTarget.unreg();
    super.onDestroy();
    StudioUtil.getDocumentTabChangeExpression().removeChangeListener(blurTextField);
    StudioUtil.getWorkAreaTabChangeExpression().removeChangeListener(blurTextField);
  }
}
}
