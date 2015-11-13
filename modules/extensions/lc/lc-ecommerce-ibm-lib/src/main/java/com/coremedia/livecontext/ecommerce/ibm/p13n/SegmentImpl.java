package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;

import java.util.Map;

public class SegmentImpl extends AbstractIbmCommerceBean implements Segment {

  private Map<String, Object> delegate;
  private WcSegmentWrapperService segmentWrapperService;

  @SuppressWarnings("unchecked")
  public Map<String, Object> getDelegate() {
    if (delegate == null) {
      delegate = (Map<String, Object>) getCommerceCache().get(new SegmentCacheKey(getId(), getContext(),
              UserContextHelper.getCurrentContext(), getSegmentWrapperService(), getCommerceCache()));
      if (delegate == null) {
        throw new NotFoundException(getId() + " (segment not found in catalog)");
      }
    }
    return delegate;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setDelegate(Object delegate) {
    this.delegate = (Map<String, Object>) delegate;
  }

  @Override
  public String getName() {
    return DataMapHelper.getValueForKey(getDelegate(), "displayName.value", String.class);
  }

  @Override
  public String getDescription() {
    return DataMapHelper.getValueForKey(getDelegate(), "description.value", String.class);
  }

  @Override
  public String getReference() {
    return null;
  }

  @Override
  public String getExternalId() {
    return DataMapHelper.getValueForKey(getDelegate(), "id", String.class);
  }

  @Override
  public String getExternalTechId() {
    return DataMapHelper.getValueForKey(getDelegate(), "id", String.class);
  }

  public void setSegmentWrapperService(WcSegmentWrapperService segmentWrapperService) {
    this.segmentWrapperService = segmentWrapperService;
  }

  public WcSegmentWrapperService getSegmentWrapperService() {
    return segmentWrapperService;
  }
}
