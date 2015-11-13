package com.coremedia.livecontext.ecommerce.ibm.p13n;

import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;

public class SegmentServiceImpl implements SegmentService {

  private WcSegmentWrapperService segmentWrapperService;
  private CommerceBeanFactory commerceBeanFactory;
  private CommerceCache commerceCache;

  @Required
  public void setSegmentWrapperService(WcSegmentWrapperService segmentWrapperService) {
    this.segmentWrapperService = segmentWrapperService;
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public List<Segment> findAllSegments() throws CommerceException {
    Map<String, Object> segments = (Map<String, Object>) commerceCache.get(
      new SegmentsCacheKey(StoreContextHelper.getCurrentContext(), UserContextHelper.getCurrentContext(),
              segmentWrapperService, commerceCache));
    return createSegmentBeansFor(segments);
  }

  @Nullable
  @Override
  @SuppressWarnings("unchecked")
  public Segment findSegmentById(@Nonnull String id) throws CommerceException {
    Map<String, Object> segment = (Map<String, Object>) commerceCache.get(
      new SegmentCacheKey(CommerceIdHelper.formatSegmentId(id), StoreContextHelper.getCurrentContext(),
              UserContextHelper.getCurrentContext(), segmentWrapperService, commerceCache));
    return createSegmentBeanFor(segment);
  }

  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public List<Segment> findSegmentsForCurrentUser() throws CommerceException {
    Map<String, Object> segments = (Map<String, Object>) commerceCache.get(
          new SegmentsByUserCacheKey(StoreContextHelper.getCurrentContext(), UserContextHelper.getCurrentContext(),
                  segmentWrapperService, commerceCache));
    return createSegmentBeansFor(segments);
  }

  protected Segment createSegmentBeanFor(Map<String, Object> segmentMap) {
    if (segmentMap != null) {
      String id = CommerceIdHelper.formatSegmentId(DataMapHelper.getValueForKey(segmentMap, "id", String.class));
      if (CommerceIdHelper.isSegmentId(id)) {
        Segment segment = (Segment) commerceBeanFactory.createBeanFor(id, StoreContextHelper.getCurrentContext());
        ((AbstractIbmCommerceBean) segment).setDelegate(segmentMap);
        return segment;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  protected List<Segment> createSegmentBeansFor(Map<String, Object> segmentsMap) {
    if (segmentsMap == null || segmentsMap.isEmpty()) {
      return Collections.emptyList();
    }
    List<Segment> result = new ArrayList<>(segmentsMap.size());
    List<Map<String, Object>> memberGroups = DataMapHelper.getValueForPath(segmentsMap, "MemberGroup", List.class);
    for (Map<String, Object> memberGroup : memberGroups) {
      result.add(createSegmentBeanFor(memberGroup));
    }
    return Collections.unmodifiableList(result);
  }

  @Nonnull
  @Override
  public SegmentService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, SegmentService.class);
  }
}
