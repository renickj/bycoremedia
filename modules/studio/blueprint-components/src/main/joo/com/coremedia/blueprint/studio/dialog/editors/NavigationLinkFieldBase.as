package com.coremedia.blueprint.studio.dialog.editors {

import com.coremedia.blueprint.studio.config.components.navigationLinkField;
import com.coremedia.blueprint.studio.util.StudioConfigurationUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.ui.ckeditor.CKEditor_properties;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;

import ext.Ext;
import ext.grid.GridPanel;
import ext.util.StringUtil;

/**
 * The application logic for the target field displayed in the internal link menu.
 */
public class NavigationLinkFieldBase extends GridPanel {
  private var siteExpression:ValueExpression;
  private var valueExpression:ValueExpression;
  private var listExpression:ValueExpression;

  private var doctype:String;

  /**
   * @param config the config object
   */
  public function NavigationLinkFieldBase(config:navigationLinkField) {
    super(config);
    siteExpression = config.siteExpression;
    valueExpression = config.valueExpression;
    valueExpression.addChangeListener(valueChanged);
    doctype = config.doctype;
  }

  private function valueChanged():void {
    getTopToolbar().find('itemId', 'clearParentList')[0].setDisabled(false);
    if (!listExpression.getValue() || listExpression.getValue().length === 0) {
      getTopToolbar().find('itemId', 'clearParentList')[0].setDisabled(true);
    }
  }

  override protected function afterRender():void {
    super.afterRender();
    valueChanged();
    mon(getEl(), 'click', openCollectionView);
  }

  /**
   * Show the collection view, if this field may be set.
   */
  private function openCollectionView():void {
    if (getStore().data.length == 0) {
      computeBaseFolder(function (baseFolder:Content):void {
        editorContext.getCollectionViewManager().openSearchForType(doctype, null, baseFolder);
      });
    }
  }

  private function computeBaseFolder(callback:Function):void {
    var preferredSite:Site = editorContext.getSitesService().getPreferredSite();
    if (!preferredSite) {
      callback(null);
    }

    var navigationRootFolderVE:ValueExpression = ValueExpressionFactory.createFromFunction(function():Content {
      var folder:Content = StudioConfigurationUtil.getConfiguration("Content Creation", "paths.navigation");
      if (folder === undefined) {
        return undefined;
      }

      return folder;
    });

    navigationRootFolderVE.loadValue(function(value:Content):void {
      if (!value) {
        callback(preferredSite.getSiteRootFolder());
      }
      value.load(function():void {
        callback(value);
      });
    });
  }

  protected function clearList():void {
    listExpression.setValue([]);
  }

  /**
   * Displays the navigation path.
   * @param value
   * @param metaData
   * @param record
   * @return
   */
  protected function pathRenderer(value:*, metaData:*, record:BeanRecord):String {
    var html:String = record.data.html;
    var c:Content = record.getBean() as Content;
    if (!html && !c.isCheckedOutByOther()) {
      ValueExpressionFactory.create(ContentPropertyNames.PATH, c).loadValue(function ():void {
        if (siteExpression) {
          siteExpression.setValue(editorContext.getSitesService().getSiteNameFor(c));
        }

        ValueExpressionFactory.createFromFunction(computeNavigationPath, c).loadValue(function(path:Array):void {
          // all loaded, let's update the records
          html = '<div style="width:1000px;"><span>';
          for (var i:int = 0; i < path.length; i++) {
            html += path[i].getName();
            if (i < path.length - 1) {
              html += '<div class="quickcreate-path-arrow"></div>';
            }
          }
          html += "</div>";
          record.data.html = html;
          record.commit(false);
        });
      });
    }
    return html;
  }

  internal static function computeNavigationPath(c:Content):Array {
    var path:Array = [];
    if(undefined !== computeNavigationPath_(c, path)) {
      return path.reverse();
    }
  }

  internal static function computeNavigationPath_(c:Content, path:Array):Array {
    if(path.indexOf(c) < 0) {
      path.push(c);
      var referrersWithDescriptor:Array = c.getReferrersWithNamedDescriptor("CMChannel", "children");
      if(undefined === referrersWithDescriptor) {
        return undefined;
      }
      if (!Ext.isEmpty(referrersWithDescriptor)) {
        c = referrersWithDescriptor[0] as Content;
        if (c) {
          return computeNavigationPath_(c, path);
        }
      }
    }
    return path;
  }

  protected function getEmptyText():String {
    return '<div style="vertical-align: middle; cursor: pointer;">' +
            '<img style="vertical-align: middle; margin-right: 4px; width: 16px; height: 16px;"' +
            'src="' + Ext.BLANK_IMAGE_URL + '" class="add-item">' +
            '<label style="vertical-align: middle;cursor: pointer;">' + CKEditor_properties.INSTANCE.internalLinkMenuDragText +
            '</label></div>';
  }

  protected function getListExpression(valueExpression:ValueExpression):ValueExpression {
    if (!listExpression) {
      listExpression = ValueExpressionFactory.create('items', beanFactory.createLocalBean());
      if (valueExpression.getValue()) {
        listExpression.setValue([valueExpression.getValue()]);
      }

      listExpression.addChangeListener(listChanged);
    }
    return listExpression;
  }

  private function listChanged():void {
    if (listExpression.getValue() && listExpression.getValue().length > 0) {
      var c:Content = listExpression.getValue()[0];
      c.invalidate(function():void {
        if(!c.getType().isSubtypeOf(doctype)) {
          listExpression.setValue([]);
          return;
        }

        var site:String = editorContext.getSitesService().getSiteNameFor(c);
        if (site !== editorContext.getSitesService().getPreferredSiteName()) {
          showErrorMessageSite(site);
          listExpression.setValue([]);
          return;
        }

        if(c.isCheckedOutByOther()) {
          c.getEditor().load(function():void {
            showErrorMessageCheckedOut(c);
            listExpression.setValue([]);
          });
        }
        else {
          valueExpression.setValue(c);
        }
      });
    }
    else {
      valueExpression.setValue(null);
    }
  }

  internal function showErrorMessageSite(site:String):void {
    var msg:String = StringUtil.format(NavigationLinkField_properties.INSTANCE.site_error_msg, site, editorContext.getSitesService().getPreferredSiteName());
    MessageBoxUtil.showError(NavigationLinkField_properties.INSTANCE.layout_error, msg);
  }

  internal function showErrorMessageCheckedOut(c:Content):void {
    var msg:String = StringUtil.format(NavigationLinkField_properties.INSTANCE.layout_error_msg, c.getEditor().getName());
    MessageBoxUtil.showError(NavigationLinkField_properties.INSTANCE.layout_error, msg);
  }
}
}
