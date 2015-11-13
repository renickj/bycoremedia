package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidation;
import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidationPropagator;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper.getValueForKey;

/**
 * A listener thread that polls the ibm commerce system for cache invalidation events.
 * All invalidation events are propagated to a list of {@link CommerceCacheInvalidationPropagator}
 */
public class CommerceCacheInvalidationListener implements InitializingBean {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceCacheInvalidationListener.class);
  public static final String EVENT_CLEAR_ALL_EVENT_ID = "clearall";
  protected static final String CONTENT_IDENTIFIER_PRODUCT = "ProductDisplay";
  protected static final String CONTENT_IDENTIFIER_CATEGORY = "CategoryDisplay";
  protected static final String CONTENT_IDENTIFIER_MARKETING_SPOT = "espot";
  protected static final String CONTENT_IDENTIFIER_SEGMENT = "segment";
  protected static final String CONTENT_IDENTIFIER_TOP_CATEGORY = "TopCategoryDisplay";

  private List<CommerceCacheInvalidationPropagator> cacheInvalidationPropagators;
  long lastInvalidationTimestamp = -1;
  private WcCacheWrapperService wcCacheWrapperService;
  private boolean enabled = true;

  private ScheduledExecutorService pinger;

  private static long PING_INTERVAL = 500;
  private static long PING_INTERVAL_ERROR = 30000;

  private void initialize() {
    if (isEnabled()) {
      this.pinger = Executors.newSingleThreadScheduledExecutor();
      this.pinger.schedule(new CacheInvalidatorThread(this), PING_INTERVAL, TimeUnit.MILLISECONDS);
      LOG.info("Running CommerceCacheInvalidationListener in longpolling mode");
    } else {
      LOG.info("CommerceCacheInvalidationListener disabled ...");
    }
  }

  protected List<CommerceCacheInvalidation> pollCacheInvalidations() throws CommerceException {
    if (lastInvalidationTimestamp <= 0) {
      lastInvalidationTimestamp = wcCacheWrapperService.getLatestTimestamp();
      return Collections.emptyList();
    }

    Map<String, Object> cacheInvalidations = wcCacheWrapperService.getCacheInvalidations(lastInvalidationTimestamp);
    if (cacheInvalidations != null) {
      lastInvalidationTimestamp = getValueForKey(cacheInvalidations, "lastInvalidation", Double.class).longValue();
      List<Map<String, Object>> invalidations = getValueForKey(cacheInvalidations, "invalidations", List.class);

      // the list to return using API classes
      List<CommerceCacheInvalidation> commerceCacheInvalidations = new ArrayList<>();
      if (invalidations != null && !invalidations.isEmpty()) {
        for (Map<String, Object> invalidation : invalidations) {
          commerceCacheInvalidations.add(convertEvent(invalidation));
        }
        return commerceCacheInvalidations;
      }
    }
    return Collections.emptyList();
  }

  protected void invalidateCacheEntries(List<CommerceCacheInvalidation> remoteInvalidations) {
    for (CommerceCacheInvalidationPropagator propagator : cacheInvalidationPropagators) {
      propagator.invalidate(remoteInvalidations);
    }
  }

  protected CommerceCacheInvalidation convertEvent(Map<String, Object> event) {
    CommerceCacheInvalidationImpl cacheInvalidation = new CommerceCacheInvalidationImpl();
    if (event != null) {
      switch (getValueForKey(event, "contentType", String.class)) {
        case CONTENT_IDENTIFIER_PRODUCT:
          event.put("id", CommerceIdHelper.formatProductTechId(getValueForKey(event, "techId", String.class)));
          event.put("contentType", CommerceCacheInvalidationImpl.CONTENT_IDENTIFIER_PRODUCT);
          break;
        case CONTENT_IDENTIFIER_CATEGORY: // same as top category
        case CONTENT_IDENTIFIER_TOP_CATEGORY:
          event.put("id", CommerceIdHelper.formatCategoryTechId(getValueForKey(event, "techId", String.class)));
          event.put("contentType", CommerceCacheInvalidationImpl.CONTENT_IDENTIFIER_CATEGORY);
          break;
        case CONTENT_IDENTIFIER_MARKETING_SPOT:
          event.put("id", CommerceIdHelper.formatMarketingSpotId(getValueForKey(event, "name", String.class)));
          event.put("contentType", CommerceCacheInvalidationImpl.CONTENT_IDENTIFIER_MARKETING_SPOT);
          break;
        case CONTENT_IDENTIFIER_SEGMENT:
          event.put("id", CommerceIdHelper.formatSegmentId(getValueForKey(event, "techId", String.class)));
          event.put("contentType", CommerceCacheInvalidationImpl.CONTENT_IDENTIFIER_SEGMENT);
          break;
      }

      cacheInvalidation.setDelegate(event);
    }
    return cacheInvalidation;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    initialize();
  }

  @Required
  public void setWcCacheWrapperService(WcCacheWrapperService wcCacheWrapperService) {
    this.wcCacheWrapperService = wcCacheWrapperService;
  }

  @Required
  public void setCacheInvalidationPropagators(List<CommerceCacheInvalidationPropagator> cacheInvalidationPropagators) {
    this.cacheInvalidationPropagators = cacheInvalidationPropagators;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  @SuppressWarnings("unused")
  public List<CommerceCacheInvalidationPropagator> getCacheInvalidationPropagators() {
    return cacheInvalidationPropagators;
  }

  private static class CacheInvalidatorThread implements Runnable {
    CommerceCacheInvalidationListener cacheInvalidator;
    private boolean hasError = false;

    public CacheInvalidatorThread(CommerceCacheInvalidationListener cacheInvalidator) {
      this.cacheInvalidator = cacheInvalidator;
    }

    @Override
    public void run() {
      long delay = PING_INTERVAL;
      try {
        List<CommerceCacheInvalidation> invalidations = cacheInvalidator.pollCacheInvalidations();
        if (hasError) {
          // after former errors we now seem to work again...
          LOG.info("Recovered from error situation, polling for cache invalidation is working again. Invalidating all cached commerce data...");
          hasError = false;
          // now we are re-connected, better to clear cache to remove stale cached errors during unconnected state
          CommerceCacheInvalidationImpl syntheticClearAll = new CommerceCacheInvalidationImpl();
          syntheticClearAll.setContentType(CommerceCacheInvalidationImpl.EVENT_CLEAR_ALL_EVENT_ID);
          syntheticClearAll.setTechId("-1");

          invalidations = Arrays.asList((CommerceCacheInvalidation) syntheticClearAll);
        }
        cacheInvalidator.invalidateCacheEntries(invalidations);

      } catch (Exception e) {
        delay = PING_INTERVAL_ERROR;
        hasError = true;
        LOG.warn("Exception while polling commerce system, delaying next request for " + delay + "ms", e.getMessage());
      }
      // schedule next execution of this thread...
      cacheInvalidator.pinger.schedule(CacheInvalidatorThread.this, delay, TimeUnit.MILLISECONDS);
    }
  }
}
