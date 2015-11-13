package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

public class Store implements CommerceObject {

  private StoreContext context;

  public Store(StoreContext context) {
    this.context = context;
  }

  public String getId() {
    return "store-" + context.getStoreName();
  }

  public StoreContext getContext() {
    return context;
  }

  /**
   * @return Returns the web URL of the commerce system's management tool
   */
  public String getVendorUrl() {
    CommerceConnection connection = Commerce.getCurrentConnection();
    if (connection != null) {
      return connection.getVendorUrl();
    }
    return null;
  }

  public String getVendorVersion() {
    CommerceConnection connection = Commerce.getCurrentConnection();
    if (connection != null) {
      return connection.getVendorVersion();
    }
    return null;
  }

  public String getVendorName() {
    CommerceConnection connection = Commerce.getCurrentConnection();
    if (connection != null) {
      return connection.getVendorName();
    }
    return null;
  }
}
