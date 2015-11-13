package com.coremedia.blueprint.studio.taxonomy {

import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;
import com.coremedia.ui.util.EventUtil;

import ext.Ext;

/**
 * Common utility methods for taxonomies.
 */
public class TaxonomyUtil {
  private static var latestAdminSelection:TaxonomyNode;


  public static function getLatestSelection():TaxonomyNode {
    return latestAdminSelection;
  }

  public static function setLatestSelection(node:TaxonomyNode):void {
    latestAdminSelection = node;
  }

  public static function escapeHTML(xml:String):String {
    while (xml.indexOf('>') != -1) {
      xml = xml.replace('>', '&gt;');
    }
    while (xml.indexOf('<') != -1) {
      xml = xml.replace('<', '&lt;');
    }
    while (xml.indexOf(' ') != -1) {
      xml = xml.replace(' ', '&nbsp;');
    }
    return xml;
  }

  public static function getTaxonomyName(taxonomy:Content):String {
    if (taxonomy.getProperties() && taxonomy.getProperties().get('value') && taxonomy.getProperties().get('value').length > 0) {
      return taxonomy.getProperties().get('value');
    }
    return taxonomy.getName();
  }

  /**
   * Invokes the callback function with true or false depending on if the taxonomy is editable or not.
   * @param taxonomyId The taxonomy id to check.
   * @param callback The callback handler.
   */
  public static function isEditable(taxonomyId:String, callback:Function, content:Content = undefined):void {
    if(!content) {
      content = StudioUtil.getActiveContent();
    }
    if(!content) {
      callback.call(null, true);
    }
    else if(content.isCheckedOutByOther()) {
      callback.call(null, false);
    }
    else if(!content.getState().readable) {
      callback.call(null, false);
    }
    else {
      ValueExpressionFactory.create(ContentPropertyNames.PATH, content).loadValue(function():void {
        var siteId:String = editorContext.getSitesService().getSiteIdFor(content);
        TaxonomyNodeFactory.loadTaxonomyRoot(siteId, taxonomyId, function(parent:TaxonomyNode):void {
          if(parent) {
            callback.call(null, true);
          }
          else {
            callback.call(null, false);
          }
        });
      });
    }
  }

  /**
   * Loads the settings structs and extracts the list of
   * administration group names.
   * @param callback The callback the group names are passed to.
   */
  public static function loadSettings(callback:Function):void {
    var url:String = 'taxonomies/settings?' + Ext.urlEncode({site: editorContext.getSitesService().getPreferredSiteId()});
    var settingsRemoteBean:RemoteBean = beanFactory.getRemoteBean(url);
    settingsRemoteBean.load(function ():void {
      var groups:Array = settingsRemoteBean.get('adminGroups');
      callback.call(null, groups);
    });
  }

  /**
   * Loads the path nodes for the given bean record (content).
   * @param record The record to load the path for.
   * @param taxonomyId The id of the taxonomy the record is located in.
   * @param callback The callback function the updated record is passed to or null if node does not exist.
   */
  public static function loadTaxonomyPath(record:BeanRecord, content:Content, taxonomyId:String, callback:Function):void {
    var bean:Content = record.getBean() as Content;
    var siteId:String = editorContext.getSitesService().getSiteIdFor(content);
    var url:String = 'taxonomies/path?' + Ext.urlEncode({taxonomyId: taxonomyId, nodeRef: parseRestId(bean), site: siteId});
    var taxRemoteBean:RemoteBean = beanFactory.getRemoteBean(url);
    taxRemoteBean.load(function ():void {
      EventUtil.invokeLater(function ():void {
        if (taxRemoteBean.get('path')) { //maybe not set if the taxonomy does not exist
          var nodes:Array = taxRemoteBean.get('path').nodes;
          var leafNode:TaxonomyNode = new TaxonomyNode(nodes[nodes.length - 1]);
          record.data.leafNode = leafNode;
          record.data.nodes = nodes;
          callback.call(null, record);
        }
        else {
          trace('[INFO]', 'Taxonomy node ' + bean + ' does not exist anymore or is not readable.');
          callback.call(null, record);
        }
      });
    });
  }

  /**
   * Adds the content represented by the given node to the list of the
   * selection expression.
   * @param node The node to add to the selection.
   */
  public static function addNodeToSelection(selectionExpression:ValueExpression, contentId:String):void {
    var newSelection:Array = [];

    var child:Content = beanFactory.getRemoteBean(contentId) as Content;
    child.load(function (bean:Content):void {
      newSelection.push(bean);
      var selection:Array = selectionExpression.getValue();
      if (selection) {
        newSelection = newSelection.concat(selection);
      }
      selectionExpression.setValue(newSelection);
    });
  }


  /**
   * Removes the content represented by the given node from the list of the
   * selection expression.
   * @param node The node to remove from the selection.
   */
  public static function removeNodeFromSelection(selectionExpression:ValueExpression, contentId:String):void {
    var selection:Array = selectionExpression.getValue();
    var newSelection:Array = [];
    if (selection) {
      for (var i:int = 0; i < selection.length; i++) {
        var selectedContent:Content = selection[i];
        var restId:String = parseRestId(selectedContent);
        if (restId === contentId) {
          continue;
        }
        newSelection.push(selectedContent);
      }
    }
    selectionExpression.setValue(newSelection);
  }

  /**
   * Returns the formatted content REST id, formatted using the CAP id.
   * @param ref
   * @return
   */
  public static function getRestIdFromCapId(ref:String):String {
    return 'content/' + ref.substr(ref.lastIndexOf('/') + 1, ref.length);
  }

  /**
   * Returns the content id in REST format.
   * @param bean The content to retrieve the REST id from.
   */
  public static function parseRestId(bean:*):String {
    return 'content/' + bean.getNumericId();
  }
}
}
