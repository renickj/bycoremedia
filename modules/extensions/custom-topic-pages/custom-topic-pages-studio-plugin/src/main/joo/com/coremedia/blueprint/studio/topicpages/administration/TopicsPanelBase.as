package com.coremedia.blueprint.studio.topicpages.administration {

import com.coremedia.blueprint.studio.TopicsHelper;
import com.coremedia.blueprint.studio.topicpages.TopicPages_properties;
import com.coremedia.blueprint.studio.topicpages.config.topicsPanel;
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cap.undoc.content.ContentUtil;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.util.ContentLocalizationUtil;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.util.EventUtil;

import ext.EventManager;
import ext.EventObjectImpl;
import ext.Ext;
import ext.IEventObject;
import ext.MessageBox;
import ext.Panel;
import ext.data.Record;
import ext.grid.Column;
import ext.grid.GridPanel;
import ext.grid.RowSelectionModel;
import ext.util.StringUtil;

/**
 * Base class of the taxonomy administration tab.
 */
public class TopicsPanelBase extends Panel {
  private const COMPONENT_ID:String = "topicsPanel";

  private var topicsExpression:ValueExpression;
  private var filterValueExpression:ValueExpression;
  private var selectionExpression:ValueExpression;
  private var taxonomyExpression:ValueExpression;
  private var isFilteredExpression:ValueExpression;

  private var selectionString:String;

  public function TopicsPanelBase(config:topicsPanel) {
    config.id = COMPONENT_ID;
    super(config);

    this.selectionExpression = config.selectionExpression;
  }

  override protected function afterRender():void {
    super.afterRender();
    getSelectionModel().addListener('rowselect', onSelect);
    getGrid().addListener('afterlayout', addKeyMap);
    editorContext.getSitesService().getPreferredSiteIdExpression().addChangeListener(siteSelectionChanged);
    StudioUtil.getWorkAreaTabChangeExpression().addChangeListener(workAreaTabChanged);
  }

  private function workAreaTabChanged(ve:ValueExpression):void {
    var component:* = ve.getValue();
    if(component as TopicPagesEditor) {
      reload();
    }
  }

  /**
   * Called when the user has changed the site.
   */
  private function siteSelectionChanged():void {
    reload();
  }

  /**
   * Adds the key listener to the grid so that the user can input the topic
   * that should be selected.
   */
  private function addKeyMap():void {
    getGrid().removeListener('afterlayout', addKeyMap);
    EventManager.on(getGrid().getEl(), 'keyup', function (evt:EventObjectImpl, t:*, o:*):void {
      if (!evt.shiftKey && !evt.ctrlKey && !evt.altKey) {
        var code:Number = evt.getCharCode();
        var character:String = String.fromCharCode(code).toLowerCase();
        selectionString += character;
        if (!selectRecordForInput(selectionString)) {
          selectionString = character;
          selectRecordForInput(character);
        }
      }
    });
  }

  private function selectRecordForInput(value:String):Boolean {
    for (var i:int = 0; i < getGrid().getStore().getCount(); i++) {
      var record:Record = getGrid().getStore().getAt(i);
      var name:String = record.data.name;
      if (name.toLowerCase().indexOf(value) === 0) {
        getSelectionModel().selectRow(i, false);
        return true;
      }
    }
    return false;
  }

  private function getSelectionModel():RowSelectionModel {
    return (getGrid().getSelectionModel() as RowSelectionModel);
  }

  /**
   * The selection listener for the grid, will trigger the preview reload for a topic selection.
   */
  private function onSelect():void {
    var record:Record = getSelectionModel().getSelected();
    selectionExpression.setValue(record);
  }

  /**
   * Returns the value expression that contains the list of contents to display as topics.
   * @return
   */
  protected function getTopicsExpression():ValueExpression {
    if (!topicsExpression) {
      topicsExpression = ValueExpressionFactory.create('topics', beanFactory.createLocalBean());
      topicsExpression.setValue([]);
    }
    return topicsExpression;
  }

  /**
   * Returns the value expression that contains the list is filtered cos of length
   * @return
   */
  protected function getIsFilteredExpression():ValueExpression {
    if (!isFilteredExpression) {
      isFilteredExpression = ValueExpressionFactory.create('isFiltered', beanFactory.createLocalBean());
      isFilteredExpression.addChangeListener(function(ve:ValueExpression):void {
        var filtered:Boolean = ve.getValue();
        Ext.getCmp('topicPagesFilteredLabel').setVisible(filtered);
      });
    }
    return isFilteredExpression;
  }


  /**
   * The value expression contains the value of the selected taxonomy.
   * @return
   */
  protected function getTaxonomySelectionExpression():ValueExpression {
    if (!taxonomyExpression) {
      taxonomyExpression = ValueExpressionFactory.create('taxonomy', beanFactory.createLocalBean());
      taxonomyExpression.addChangeListener(reload);
    }
    return taxonomyExpression;
  }

  /**
   * Returns the value expression that contains the active search expression.
   * @return
   */
  protected function getFilterValueExpression():ValueExpression {
    if (!filterValueExpression) {
      filterValueExpression = ValueExpressionFactory.create('topics', beanFactory.createLocalBean());
      filterValueExpression.addChangeListener(reload);
    }
    return filterValueExpression;
  }

  /**
   * Reloads the list of topics, fired after a search or a taxonomy selection.
   */
  protected function reload():void {
    OpenTopicPagesEditorAction.isAdministrationEnabled(function(enabled:Boolean):void {
      if(!enabled) {
        return;
      }

      var taxonomyContent:Content = getTaxonomySelectionExpression().getValue();
      if (!taxonomyContent) {
        return;
      }
      var taxonomy:Number = IdHelper.parseContentId(taxonomyContent);
      var term:String = filterValueExpression.getValue() || '';
      var siteId:String = editorContext.getSitesService().getPreferredSiteId();
      TopicsHelper.loadTopics(taxonomy, siteId, term, function (items:Array, filtered:Boolean):void {
        var initCall:Function = function ():void {
          getGrid().getStore().removeListener('load', initCall);
          getGrid().focus(false, 1000);
          getSelectionModel().selectFirstRow();
          getIsFilteredExpression().setValue(filtered);
        };
        getGrid().getStore().addListener('load', initCall);
        getTopicsExpression().setValue(items);
      });
    });
  }

  /**
   * Returns the instance of the grid panel inside this panel.
   * @return
   */
  private function getGrid():GridPanel {
    return find('itemId', 'topicsGrid')[0];
  }

  /**
   * Displays the name of the topic page.
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected static function nameRenderer(value:*, metaData:*, record:Record):String {
    return record.data.name;
  }

  /**
   * Displays the page the topic page is linked to.
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function pageRenderer(value:*, metaData:*, record:Record):String {
    var id:Number = record.data.topic.getNumericId();
    var pageContent:Content = record.data.page;
    if (pageContent) {
      if (!record.data.rendered) {
        EventUtil.invokeLater(function ():void {//invoke later, otherwise JS error will be thrown that row is undefined.
          pageContent.load(function ():void {
            record.data.rendered = true;
            record.commit(false);
          });
        });
      }
      else {
        pageContent.addPropertyChangeListener(ContentPropertyNames.LIFECYCLE_STATUS, customPageChanged);
        var iconCls:String = ContentLocalizationUtil.getIconStyleClassForContentTypeName(pageContent.getType().getName());
        var html:String = '<div><img width="16" height="16" class="' + iconCls + ' content-type-xs cm-before-text-icon" src="'
                + Ext.BLANK_IMAGE_URL
                + '" />'
                + '<a ext:qtip="'+ pageContent.getName() +'" href="#" data-topic-action="open">'
                + TopicPages_properties.INSTANCE.TopicPages_name + '</a>'
                + '<img id="topicpage-delete-' + id + '" width="16" height="16" class="delete-icon" style="margin-bottom: -3px;margin-left: 5px;" src="'
                + Ext.BLANK_IMAGE_URL
                + '" title="' + TopicPages_properties.INSTANCE.TopicPages_deletion_tooltip + '" data-topic-action="delete"/><br />'
                + '</div>';
        return html;
      }
    }

    if(editorContext.getSitesService().getPreferredSite()) {
      return '<div><a href="#" id="topicpage-create-' + id + '" data-topic-action="create">'
              + TopicPages_properties.INSTANCE.TopicPages_create_link + '</a></div>';
    }
    return TopicPages_properties.INSTANCE.TopicPages_no_preferred_site;
  }

  protected function onPageColumnClick(column:Column, grid:GridPanel, rowIndex:Number, event:IEventObject):void {
    var data:Object = grid.getStore().getAt(rowIndex).data;
    var id:Number = data.topic.getNumericId();
    var pageContent:Content = data.page;
    var action:String = String(event.getTarget().getAttribute('data-topic-action'));
    if (action === "create") {
      updatePage(id, true);
    } else if (action === "open") {
      openPage(IdHelper.parseContentId(pageContent));
    } else if (action === "delete") {
      deletePage(id, IdHelper.parseContentId(pageContent));
    }
    event.preventDefault();
  }

  private function customPageChanged(e:PropertyChangeEvent):void {
    var status:String = e.newValue;
    if (status === 'deleted') {
      reload();
    }
  }

  /**
   * Called from the page rendered.
   * @param id
   */
  public function openPage(id:Number):void {
    var page:Content = ContentUtil.getContent('' + id);
    StudioUtil.openInTab(page);
  }

  /**
   * Called from the page rendered.
   * @param id
   * @param pageId
   */
  public function deletePage(id:Number, pageId:Number):void {
    var page:Content = ContentUtil.getContent('' + pageId);
    MessageBox.show({
      title:TopicPages_properties.INSTANCE.TopicPages_deletion_title,
      msg:StringUtil.format(TopicPages_properties.INSTANCE.TopicPages_deletion_text, page.getName()),
      buttons:MessageBox.OKCANCEL,
      fn:function (btn:*):void {
        if (btn === 'ok') {
          if (page.isCheckedOutByCurrentSession()) {
            page.checkIn(function ():void {
              editorContext.getContentTabManager().closeDocument(page);
            });
          }
          updatePage(id, false);
        }
      }
    });
  }

  /**
   * Called by the link rendered into the page column.
   * @param id The numeric content id to link/unlink the page for
   * @param create True, if the page should be created. False to delete the linked page.
   */
  public function updatePage(id:Number, create:Boolean):void {
    TopicsHelper.loadSettings(function (settings:Bean):void {
      var topicPageChannel:Content = settings.get('topicPageChannel');
      if(!topicPageChannel) {
        var siteName:String = editorContext.getSitesService().getPreferredSiteName();
        var msg:String = StringUtil.format(TopicPages_properties.INSTANCE.TopicPages_no_channel_configured, siteName);
        MessageBox.alert(TopicPages_properties.INSTANCE.TopicPages_no_channel_configured_title, msg);
        return;
      }

      topicPageChannel.invalidate(function ():void {
        if (topicPageChannel.isCheckedOutByOther()) {
          var msg:String = StringUtil.format(TopicPages_properties.INSTANCE.TopicPages_root_channel_checked_out_msg,
                  topicPageChannel.getName());
          MessageBox.alert(TopicPages_properties.INSTANCE.TopicPages_root_channel_checked_out_title, msg);
          return;
        }
        selectionExpression.setValue(null);
        var selectedRecord:Record = getSelectionModel().getSelected() as Record;
        var siteId:String = editorContext.getSitesService().getPreferredSiteId();
        TopicsHelper.updatePage(id, siteId, create, function (result:*):void {
          ValueExpressionFactory.create(ContentPropertyNames.PATH, result.topicPagesFolder).loadValue(function (path:String):void {
            session.getConnection().getContentRepository().getChild(path, function (child:Content):void {
              if (child) {
                child.invalidate();
              }
              selectedRecord.data.rendered = false;
              selectedRecord.data.page = result.page;
              selectedRecord.commit(false);

              selectionExpression.setValue(selectedRecord);

              var root:Content = result.rootChannel;
              if (!root) {
                var msg:String = StringUtil.format(TopicPages_properties.INSTANCE.TopicPages_root_channel_not_found_msg, editorContext.getSitesService().getPreferredSiteName());
                MessageBox.alert(TopicPages_properties.INSTANCE.TopicPages_root_channel_not_found_title, msg);
              }
//              else {
//                root.invalidate(function ():void {
//                  StudioUtil.openInBackground([root]);
//                });
//              }

              if (result.page) {
                editorContext.getContentTabManager().openDocuments([result.page], true);
              }
            });
          });
        });
      });
    });
  }


  /**
   * Remove registered listeners.
   */
  override protected function onDestroy():void {
    super.onDestroy();
    editorContext.getSitesService().getPreferredSiteIdExpression().removeChangeListener(siteSelectionChanged);
    StudioUtil.getWorkAreaTabChangeExpression().removeChangeListener(workAreaTabChanged);
  }
}
}
