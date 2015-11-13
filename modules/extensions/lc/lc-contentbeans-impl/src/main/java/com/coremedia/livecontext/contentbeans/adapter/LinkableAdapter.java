package com.coremedia.livecontext.contentbeans.adapter;

import com.coremedia.blueprint.common.navigation.Linkable;

public abstract class LinkableAdapter<T> implements Linkable {
  private T delegate;

  public LinkableAdapter(T delegate) {
    this.delegate = delegate;
  }

  public T getDelegate() {
    return delegate;
  }
}
