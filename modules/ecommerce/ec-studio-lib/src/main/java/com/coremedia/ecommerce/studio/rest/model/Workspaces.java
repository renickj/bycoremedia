package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.Locale;

/**
 * We are using a faked commerce bean here to support the invalidation of the list of available segments
 * Therefore we implement the the interface "CommerceBean" here and use the Store itself
 * as a delegate since the "Workspaces" only provides methods that are available on the store.
 */
public class Workspaces implements CommerceObject {

  private StoreContext context;

  public Workspaces(StoreContext context) {
    this.context = context;
  }

  public String getId() {
    return "workspaces-" + context.getStoreName();
  }

  public StoreContext getContext() {
    return context;
  }

}
