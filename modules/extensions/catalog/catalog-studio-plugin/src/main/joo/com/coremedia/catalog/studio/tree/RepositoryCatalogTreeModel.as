package com.coremedia.catalog.studio.tree {

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentProperties;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructType;
import com.coremedia.catalog.studio.CatalogTreeRelation;
import com.coremedia.catalog.studio.CatalogTreeRelation;
import com.coremedia.cms.editor.sdk.RepositoryContentTreeRelation;
import com.coremedia.cms.editor.sdk.collectionview.tree.ContentTreeModel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.beanFactory;

use namespace beanFactory;

public class RepositoryCatalogTreeModel extends ContentTreeModel {

  override public function getNodeId(model:Object):String {
    var content:Content = model as Content;
    if (!content || content.isFolder()) {
      return null;
    }

    if(!CatalogHelper.getInstance().isActiveCoreMediaStore()) {
      return null;
    }

    var type:ContentType = content.getType();
    if (type) {
      var typeBean:RemoteBean = type as RemoteBean;
      if (typeBean && typeBean.isLoaded() && !type.isSubtypeOf(CatalogTreeRelation.CONTENT_TYPE_CATEGORY)) {
        return null;
      }
    }

    // otherwise, we really don't know if its a CMCategory, but we have to return something here synchronously...
    return super.getNodeId(model);
  }

  override public function getIdPathFromModel(model:Object):Array {
    var content:Content = model as Content;
    if (!content) {
      // No path exists.
      return null;
    }
    if(!content.isLoaded()) {
      return undefined;
    }

    //the current active site has another store type
    if(!CatalogHelper.getInstance().isActiveCoreMediaStore()) {
      return null;
    }

    var type:ContentType = content.getType();
    if (!(type.isSubtypeOf(CatalogTreeRelation.CONTENT_TYPE_PRODUCT) || type.isSubtypeOf(CatalogTreeRelation.CONTENT_TYPE_CATEGORY))) {
      return null;
    }

    // check if the content is part of the active site,
    // otherwise return null so that the content from the global tree is selected
    var siteId:String = editorContext.getSitesService().getSiteIdFor(content);
    if(siteId !== editorContext.getSitesService().getPreferredSiteId()) {
      return null;
    }

    return super.getIdPathFromModel(model);
  }

  override public function getNodeModel(nodeId:String):Object {
    if(!CatalogHelper.getInstance().isActiveCoreMediaStore()) {
      return null;
    }

    var content:Content = beanFactory.getRemoteBean(nodeId) as Content;
    return content && !content.isFolder() ? content : null;
  }

  override public function getRootId():String {
    return getNodeId(getCatalogRoot());
  }

  override public function getText(nodeId:String):String {
    if (nodeId === getNodeId(getCatalogRoot())) {
      return "Corporate-Catalog";
    }
    return super.getText(nodeId);
  }

  override public function getIconCls(nodeId:String):String {
    var nodeModel:Content = getNodeModel(nodeId) as Content;
    if (nodeModel === getCatalogRoot()) {
      return "store-icon";
    }
    return super.getIconCls(nodeId);
  }

  override protected function getVisibleRootModels():Array {
    return [getCatalogRoot()];
  }

  private static function getSiteRootFolder():Content {
    var site:Site = editorContext.getSitesService().getPreferredSite();
    if (!site) {
      return site === null ? null : undefined;
    }
    if (site.getName() === undefined || site.getLocale() === undefined || site.getLocale().getDisplayName() === undefined) {
      return undefined;
    }
    return site.getSiteRootFolder();
  }

  private static function getCatalogRoot():Content {
    var siteRootFolder:Content = getSiteRootFolder();
    if (!siteRootFolder) {
      return siteRootFolder;
    }
    var liveContextSettings:Content = siteRootFolder.getChild("Options/Settings/LiveContext");
    if (liveContextSettings === undefined) {
      return undefined;
    }
    if (liveContextSettings !== null) {
      var liveContextSettingsProperties:ContentProperties = liveContextSettings.getProperties();
      if (liveContextSettingsProperties === undefined) {
        return undefined;
      }
      var liveContextStruct:Struct = liveContextSettingsProperties.get("settings") as Struct;
      if (liveContextStruct) {
        var structType:StructType = liveContextStruct.getType();
        if (structType === undefined) {
          return undefined;
        }
        if (structType.hasProperty("livecontext.rootCategory")) {
          return liveContextStruct.get("livecontext.rootCategory");
        }
      }
    }
    return null;
  }


  public function toString():String {
    return "RepositoryCatalogTreeModel";
  }
}
}