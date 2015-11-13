package com.coremedia.blueprint.studio.taxonomy.selection {

import com.coremedia.blueprint.studio.config.taxonomy.taxonomyLinkListPropertyField;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderFactory;
import com.coremedia.blueprint.studio.taxonomy.rendering.TaxonomyRenderer;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.Editor_properties;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.premular.fields.LinkListPropertyField;
import com.coremedia.cms.editor.sdk.premular.fields.LinkListPropertyFieldBase;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;
import com.coremedia.ui.util.EventUtil;

import ext.Button;
import ext.Ext;
import ext.util.StringUtil;

public class TaxonomyLinkListPropertyFieldBase extends LinkListPropertyField {
  protected static const TAXONOMY_SEARCH_FIELD_WRAPPER_ITEM_ID:String = "taxonomySearchFieldWrapper";
  protected static const TAXONOMY_SEARCH_FIELD_ITEM_ID:String = "taxonomySearchField";
  protected static const OPEN_TAXONOMY_CHOOSER_BUTTON_ITEM_ID:String = "openTaxonomyChooserButton";

  private var searchResultExpression:ValueExpression;
  private var siteSelectionExpression:ValueExpression;
  private var taxonomyId:String;
  private var bindTo:ValueExpression;

  private var searchField:TaxonomySearchField;
  private var forceReadOnlyValueExpression:ValueExpression;

  public function TaxonomyLinkListPropertyFieldBase(config:taxonomyLinkListPropertyField) {
    super(config);
    taxonomyId = config.taxonomyId;
    forceReadOnlyValueExpression = config.forceReadOnlyValueExpression;
    bindTo = config.bindTo;
  }

  override protected function afterRender():void {
    super.afterRender();
    var content:Content = undefined;
    if (bindTo) {
      content = bindTo.getValue();
    }

    //check if the content is real content or a dummy (e.g. if the link list is used in the quick create dialog)
    if(content as Content) {
      TaxonomyUtil.isEditable(taxonomyId, function (editable:Boolean):void {
        if (!editable) {
          setReadOnly(true);
        } else if (forceReadOnlyValueExpression) {
          forceReadOnlyValueExpression.addChangeListener(updateReadOnly);
          updateReadOnly();
        }
      }, content);
    }
  }

  override protected function beforeDestroy():void {
    if (forceReadOnlyValueExpression) {
      forceReadOnlyValueExpression.removeChangeListener(updateReadOnly);
    }
    super.beforeDestroy();
  }

  private function getChooserButton():Button {
    return LinkListPropertyFieldBase.getLinkListToolbar(this).find('itemId', OPEN_TAXONOMY_CHOOSER_BUTTON_ITEM_ID)[0];
  }

  private function updateReadOnly():void {
    var readOnly:* = forceReadOnlyValueExpression.getValue();
    if (readOnly !== undefined) {
      setReadOnly(readOnly === true);
    }
  }

  private function setReadOnly(value:Boolean):void {
    var chooserButton:Button = getChooserButton();
    if (value) {
      addClass("readonly")
    } else {
      removeClass("readonly")
    }
    if(searchField) {
      searchField.setReadOnly(value);
    }
    if(chooserButton) {
      chooserButton.setDisabled(value);
    }
  }

  /**
   * The value expression for the link list title.
   * @return
   */
  protected function getSearchResultExpression():ValueExpression {
    if (!searchResultExpression) {
      searchResultExpression = ValueExpressionFactory.create('search', beanFactory.createLocalBean());
      searchResultExpression.addChangeListener(searchResultChanged);
    }
    return searchResultExpression;
  }

  /**
   * Returns the value expression for the site the taxonomy link list is working on.
   * The site is calculated from the content path. By setting a value, the REST backend looks up
   * if there is a site depending taxonomy for the given taxonomyId, otherwise the global taxonomy is used.
   * @return
   */
  protected function getSiteSelectionExpression(bindTo:ValueExpression):ValueExpression {
    if (!siteSelectionExpression) {
      siteSelectionExpression = ValueExpressionFactory.create('site', beanFactory.createLocalBean());
      var content:Content = bindTo.getValue() as Content;
      var siteId:String = editorContext.getSitesService().getSiteIdFor(content);
      if (content && !content.getPath()) {
        ValueExpressionFactory.create('path', content).loadValue(function ():void {
          siteSelectionExpression.setValue(siteId);
        });
      }
      else {
        siteSelectionExpression.setValue(siteId);
      }
    }
    return siteSelectionExpression;
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
      if (record.data.nodes) {
        var renderer:TaxonomyRenderer = TaxonomyRenderFactory.createLinkListRenderer(record.data.nodes, getId());
        renderer.doRender(function (html:String):void {
          if (record.data.html !== html) {
            record.data.html = html;
            record.commit(false);
          }
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
    });
    if (!record.data.html) {
      return "<div class='loading'>" + TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_status_loading_text + "</div>"
    }
    return record.data.html;
  }

  /**
   * Fired when the field is used inside a property editor
   * and the user has selected an entry that should be added
   * to the taxonomy link list.
   */
  private function searchResultChanged():void {
    var selection:TaxonomyNodeList = searchResultExpression.getValue();
    if (selection) {
      var content:Content = session.getConnection().getContentRepository().getContent(selection.getLeafRef());
      //do create the property expression here! see BARBUDA-1805
      var propertyValueExpression:ValueExpression = ValueExpressionFactory.create('properties.' + propertyName, bindTo.getValue());
      var taxonomies:Array = propertyValueExpression.getValue();
      for (var i:int = 0; i < taxonomies.length; i++) {
        var child:Content = taxonomies[i];
        //check if node has already been added
        if (TaxonomyUtil.parseRestId(child) === selection.getLeafRef()) {
          return;
        }
      }
      var newTaxonomies:Array = [];
      for (var j:int = 0; j < taxonomies.length; j++) {
        newTaxonomies.push(taxonomies[j]);
      }
      newTaxonomies.push(content);
      propertyValueExpression.setValue(newTaxonomies);
      if (searchField) {
        searchField.focus();
      }
    }
  }

  /**
   * Removes the given taxonomy.
   * The method is called from a generated script call, so usage is not indicated here.
   */
  public function plusMinusClicked(nodeRef:String):void {
    if (forceReadOnlyValueExpression){
      forceReadOnlyValueExpression.loadValue(function():void {
        if (!forceReadOnlyValueExpression.getValue()) {
         removeNodeFromSelection(nodeRef);
        }
      });
    } else {
      removeNodeFromSelection(nodeRef);
    }
  }

  private function removeNodeFromSelection(nodeRef:String):void {
    //do create the property expression here! see BARBUDA-1805
    var propertyValueExpression:ValueExpression = ValueExpressionFactory.create('properties.' + propertyName, bindTo.getValue());
    TaxonomyUtil.removeNodeFromSelection(propertyValueExpression, nodeRef);
  }

}
}
