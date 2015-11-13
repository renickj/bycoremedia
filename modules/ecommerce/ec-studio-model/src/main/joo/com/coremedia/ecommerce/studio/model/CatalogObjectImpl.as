package com.coremedia.ecommerce.studio.model {
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentProxy;
import com.coremedia.ui.data.impl.RemoteBeanImpl;

public class CatalogObjectImpl extends RemoteBeanImpl implements CatalogObject, ContentProxy {
  private static const CONTENT_PROPERTY_NAME:String = "content";

  public function CatalogObjectImpl(uri:String) {
    super(uri);
  }

  public function getContent():Content {
    return get(CONTENT_PROPERTY_NAME);
  }

  public function getName():String {
    return get(CatalogObjectPropertyNames.NAME);
  }

  public function getShortDescription():String {
    return get(CatalogObjectPropertyNames.SHORT_DESCRIPTION);
  }

  public function getExternalId():String {
    return get(CatalogObjectPropertyNames.EXTERNAL_ID);
  }

  public function getId():String {
    return get(CatalogObjectPropertyNames.ID);
  }

  public function getExternalTechId():String {
    return get(CatalogObjectPropertyNames.EXTERNAL_TECH_ID);
  }

  public function getStore():Store {
    return get(CatalogObjectPropertyNames.STORE);
  }
}
}
