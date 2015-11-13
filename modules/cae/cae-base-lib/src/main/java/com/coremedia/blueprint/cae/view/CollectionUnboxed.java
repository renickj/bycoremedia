package com.coremedia.blueprint.cae.view;

import com.coremedia.blueprint.common.contentbeans.CMCollection;

/**
 * A wrapper class for bean includes
 */
public class CollectionUnboxed {

  private CMCollection delegate;
  private String view;

  public CollectionUnboxed(CMCollection delegate, String view) {
    this.delegate = delegate;
    this.view = view;
  }

  public String getView() {
    return view;
  }

  public void setView(String view) {
    this.view = view;
  }

  public CMCollection getDelegate() {
    return delegate;
  }

  public void setDelegate(CMCollection delegate) {
    if (this.delegate == null) {
      this.delegate = delegate;
    }
  }

}
