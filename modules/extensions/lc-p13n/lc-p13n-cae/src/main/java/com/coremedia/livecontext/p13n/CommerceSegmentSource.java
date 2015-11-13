package com.coremedia.livecontext.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.p13n.SegmentService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.MapPropertyMaintainer;
import com.coremedia.personalization.context.collector.AbstractContextSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A {@link com.coremedia.personalization.context.collector.ContextSource} that reads the commerce user id
 * from the current commerce user context and asks the commerce system for memberships in customer
 * segments. Such customer segments in which the user is a member will be provided in the context collection
 * to evaluate personalization rules based on commerce segments.
 */
public class CommerceSegmentSource extends AbstractContextSource {

  private static final String SEGMENT_ID_LIST_CONTEXT_KEY = "usersegments";

  private String contextName = "commerce";

  @SuppressWarnings("unused")
  public void setContextName(String contextName) {
    this.contextName = contextName;
  }

  @Override
  public void preHandle(HttpServletRequest request, HttpServletResponse response, ContextCollection contextCollection) {

    if (Commerce.getCurrentConnection() == null ||
            getStoreContext() == null ||
            getUserContext() == null ||
            getUserContext().getContextNames().isEmpty()) {
      return;
    }

    List<String> segmentIdList = null;
    MapPropertyMaintainer segmentContext = new MapPropertyMaintainer();

    //UserSegments provided by LiveContext Fragment Connector
    String userSegments = getStoreContext().getUserSegments();
    if (userSegments != null && !userSegments.isEmpty()) {
      segmentIdList = Arrays.asList(userSegments.split(","));
    }
    if (segmentIdList == null) {
      segmentIdList = readSegmentIdListFromCommerceSystem();
    }

    if (segmentIdList != null) {
      UserContext userContext = getUserContext();
      userContext.put(SEGMENT_ID_LIST_CONTEXT_KEY, segmentIdList);
      StringBuilder segmentList = new StringBuilder();
      // The following format (comma seperated list if ids) demands that not a id can be part of another id (like
      // 1234 is part of 123456). This is guaranteed if all ids have the same length (as it is the case). If not,
      // the format of ids can be changed to a more robust one.
      for (String segment : segmentIdList) {
        String segmentId = getCommerceIdProvider().formatSegmentId(segment);
        if (segmentId != null){
          segmentList.append(segmentId).append(",");
        }
      }
      segmentContext.setProperty(SEGMENT_ID_LIST_CONTEXT_KEY, segmentList.toString());
      contextCollection.setContext(contextName, segmentContext);
    }
  }

  protected List<String> readSegmentIdListFromCommerceSystem() {
    SegmentService segmentService = getSegmentService();
    if (segmentService != null) {
      List<Segment> segments = segmentService.findSegmentsForCurrentUser();
      List<String> segmentIdList = new ArrayList<>(segments.size());
      for (Segment segment : segments) {
        segmentIdList.add(segment.getId());
      }
      return segmentIdList;
    }
    return Collections.emptyList();
  }

  public StoreContext getStoreContext() {
    return Commerce.getCurrentConnection().getStoreContext();
  }

  public UserContext getUserContext() {
    return Commerce.getCurrentConnection().getUserContext();
  }

  public SegmentService getSegmentService() {
    return Commerce.getCurrentConnection().getSegmentService();
  }

  public CommerceIdProvider getCommerceIdProvider() {
    return Commerce.getCurrentConnection().getIdProvider();
  }
}
