package com.coremedia.ecommerce.studio.model {
[RestResource(uriTemplate="livecontext/catalog/{siteId:[^/]+}/{workspaceId:[^/]+}")]
public class CatalogImpl extends CatalogObjectImpl implements Catalog {
  public function CatalogImpl(uri:String) {
    super(uri);
  }

  public function getChildrenByName():Object {
    return get(CatalogObjectPropertyNames.CHILDREN_BY_NAME);
  }

  public function getTopCategories():Array {
    return get(CatalogObjectPropertyNames.TOP_CATEGORIES);
  }
}
}