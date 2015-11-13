package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.Locale;

/**
 * We are using a faked commerce bean here since the "Marketing" level
 * in the Studio library is only used there and is not supported by the commerce API.
 * Therefore we implement the the interface "CommerceBean" here and use the Store itself
 * as a delegate since the "Marketing" only provides methods that are available on the store.
 */
public class Marketing implements CommerceObject {

  private StoreContext context;

  public Marketing(StoreContext context) {
    this.context = context;
  }

  public String getId() {
    return "marketing-" + context.getStoreName();
  }

  public StoreContext getContext() {
    return context;
  }

}
