package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.Locale;

/**
 * We are using a faked commerce bean here since the "Catalog" level
 * in the Studio library is only used there and is not supported by the commerce API.
 * Therefore we implement the the interface "CommerceBean" here and use the Store itself
 * as a delegate since the "Catalog" only provides methods that are available on the store.
 */
public class Catalog implements CommerceObject {

  private StoreContext context;

  public Catalog(StoreContext context) {
    this.context = context;
  }

  public String getId() {
    return "catalog-" + context.getStoreName();
  }

  public StoreContext getContext() {
    return context;
  }

}
