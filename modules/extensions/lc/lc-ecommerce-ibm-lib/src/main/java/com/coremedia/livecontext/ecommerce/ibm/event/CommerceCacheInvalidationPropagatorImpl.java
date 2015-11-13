package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidation;
import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidationPropagator;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.livecontext.ecommerce.ibm.p13n.MarketingSpotsCacheKey;
import com.coremedia.livecontext.ecommerce.ibm.p13n.SegmentsCacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Invalidates the uapi cache based on incoming {@link CommerceCacheInvalidationImpl} events.
 * To create custom dependencies for external invalidation see also {@link com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey}.
 */
public class CommerceCacheInvalidationPropagatorImpl implements CommerceCacheInvalidationPropagator {
  private static final Logger LOG = LoggerFactory.getLogger(CommerceCacheInvalidationPropagatorImpl.class);

  private Cache cache;

  @Override
  public void invalidate(List<CommerceCacheInvalidation> invalidations) {
    for (CommerceCacheInvalidation invalidation : invalidations) {
      LOG.trace("Invalidate Cache: " + invalidation.getTechId());
      try {
        if (invalidation.getContentType().equals(CommerceCacheInvalidationListener.EVENT_CLEAR_ALL_EVENT_ID)) {
          cache.invalidate(AbstractCommerceCacheKey.INVALIDATE_ALL_EVENT);
        } else if(invalidation.getContentType().equals(CommerceCacheInvalidationImpl.CONTENT_IDENTIFIER_MARKETING_SPOT)){
          cache.invalidate(invalidation.getTechId());
          //in case of e-MarketingSpot events we also want to invalidate the list of all MarketingSpots.
          //By now we do not create dedicated events for CREATE and DELETE MarketingSpot events.
          cache.invalidate(MarketingSpotsCacheKey.DEPENDENCY_ALL_MARKETING_SPOTS);
        } else if(invalidation.getContentType().equals(CommerceCacheInvalidationImpl.CONTENT_IDENTIFIER_SEGMENT)){
          //in case of segment events we also want to invalidate all segments and the list of all segments
          //By now we do not create dedicated events for CREATE and DELETE segment events.
          cache.invalidate(SegmentsCacheKey.DEPENDENCY_ALL_SEGMENTS);
        } else {
          cache.invalidate(invalidation.getTechId());
        }
      } catch (Exception e) {
        // ignore errors in order to process other pending events
        if (LOG.isDebugEnabled()) {
          LOG.debug("error invalidating " + invalidation.getTechId(), e);
        }
      }
    }
    cache.invalidate(AbstractCommerceCacheKey.DEPENDENCY_COMMERCE_BEAN_NOT_FOUND);
  }

  public void setCache(Cache cache) {
    this.cache = cache;
  }
}
