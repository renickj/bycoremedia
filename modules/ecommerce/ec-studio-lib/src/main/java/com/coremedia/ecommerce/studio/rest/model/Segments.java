package com.coremedia.ecommerce.studio.rest.model;

import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import java.util.Locale;

/**
 * We are using a faked commerce bean here to support the invalidation of the list of available segments
 * Therefore we implement the the interface "CommerceBean" here and use the Store itself
 * as a delegate since the "Segments" only provides methods that are available on the store.
 */
public class Segments implements CommerceObject {

  private StoreContext context;

  public Segments(StoreContext context) {
    this.context = context;
  }

  public String getId() {
    return "segments-" + context.getStoreName();
  }

  public StoreContext getContext() {
    return context;
  }

}
