package com.coremedia.ecommerce.studio.model {
import com.coremedia.ecommerce.studio.CatalogModel;

import ext.Ext;

use namespace Ext;

[RestResource(uriTemplate="livecontext/store/{siteId:[^/]+}/{workspaceId:[^/]+}")]
public class StoreImpl extends CatalogObjectImpl implements Store {
  private var siteId:String;
  private var workspaceId:String;

  public function StoreImpl(uri:String, vars:Object) {
    siteId = vars['siteId'];
    workspaceId = vars['workspaceId'];
    super(uri);
  }

  public function getChildrenByName():Object {
    return get(CatalogObjectPropertyNames.CHILDREN_BY_NAME);
  }

  public function getTopCategories():Array {
    return get(CatalogObjectPropertyNames.TOP_CATEGORIES);
  }

  public function getStoreId():String {
    return get(CatalogObjectPropertyNames.STORE_ID);
  }

  public function getSiteId():String {
    return siteId;
  }

  public function getTopLevel():Array {
    return get("topLevel");
  }


  public function getMarketing():Marketing {
    return get("marketing");

  }

  public function getSegments():Segments {
    return get(CatalogObjectPropertyNames.SEGMENTS);
  }

  public function getContracts():Contracts {
    return get(CatalogObjectPropertyNames.CONTRACTS);
  }

  public function getWorkspaces():Workspaces {
    return get(CatalogObjectPropertyNames.WORKSPACES);
  }


  public function getCurrentWorkspace():Workspace {
    if (!workspaceId || workspaceId === CatalogModel.NO_WS) return undefined;
    if (!getWorkspaces()) return undefined;
    if (!getWorkspaces().getWorkspaces()) return undefined;

    var workspaces:Array = getWorkspaces().getWorkspaces();

    var filtered:Array = workspaces.filter(function(workspace:Workspace):Boolean {
      return workspaceId === workspace.getExternalTechId();
    });

    return filtered.length > 0 ? filtered[0] : null;
  }

  public function getCatalog():Catalog {
    return get(CatalogObjectPropertyNames.CATALOG);
  }

  public function getVendorUrl():String {
    return get(CatalogObjectPropertyNames.VENDOR_URL);
  }

  public function getVendorVersion():String {
    return get(CatalogObjectPropertyNames.VENDOR_VERSION);
  }

  public function getWcsTimeZone():Object {
    return get("wcsTimeZone");
  }

  public function getVendorName():String {
    return get(CatalogObjectPropertyNames.VENDOR_NAME);
  }

  override public function getStore():Store {
    return this;
  }
}
}