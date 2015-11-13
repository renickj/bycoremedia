package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidation;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;

import java.util.Hashtable;
import java.util.Map;

/**
 * A cache invalidation message received form the commerce system.
 */
public class CommerceCacheInvalidationImpl implements CommerceCacheInvalidation {

  private Map<String, Object> delegate;

  @Override
  public long getTimestamp() {
    return Long.parseLong(DataMapHelper.getValueForKey(delegate, "timestamp", String.class));
  }

  @Override
  public String getTechId() {
    return DataMapHelper.getValueForKey(delegate, "techId", String.class);
  }

  void setTechId(String techId) {
    if (delegate == null) {
      setDelegate(new Hashtable<String, Object>());
    }
    delegate.put("techId", techId);
  }

  @Override
  public String getContentType() {
    return DataMapHelper.getValueForKey(delegate, "contentType", String.class);
  }

  void setContentType(String contentType) {
    if (delegate == null) {
      setDelegate(new Hashtable<String, Object>());
    }
    delegate.put("contentType", contentType);
  }

  @Override
  public String getId() {
    return DataMapHelper.getValueForKey(delegate, "id", String.class);
  }

  void setName(String name) {
    if (delegate == null) {
      setDelegate(new Hashtable<String, Object>());
    }
    delegate.put("name", name);
  }

  public void setDelegate(Object delegate) {
    this.delegate = (Map<String, Object>) delegate;
  }

  public Map<String, Object> getDelegate() {
    return delegate;
  }
}