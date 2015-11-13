package com.coremedia.livecontext.ecommerce.ibm.event;

import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidation;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class CommerceCacheInvalidationImplPropagatorImplTest {

  @Mock
  Cache cache;

  CommerceCacheInvalidationPropagatorImpl testling;

  @Before
  public void setup(){
    testling = new CommerceCacheInvalidationPropagatorImpl();
    testling.setCache(cache);
  }

  @Test
  public void testInvalidate(){
    List<CommerceCacheInvalidation> invalidations = new ArrayList<>();
    CommerceCacheInvalidationImpl event = new CommerceCacheInvalidationImpl();
    event.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_PRODUCT);
    event.setTechId("4711");
    invalidations.add(event);
    invalidations.add(event);

    testling.invalidate(invalidations);
    verify(cache, times(2)).invalidate("4711");
  }

  @Test
  public void testInvalidateClearAll(){
    List<CommerceCacheInvalidation> invalidations = new ArrayList<>();
    CommerceCacheInvalidationImpl event = new CommerceCacheInvalidationImpl();
    event.setContentType(CommerceCacheInvalidationListener.EVENT_CLEAR_ALL_EVENT_ID);
    event.setTechId("0");
    invalidations.add(event);

    testling.invalidate(invalidations);
    verify(cache, times(1)).invalidate(AbstractCommerceCacheKey.INVALIDATE_ALL_EVENT);
  }

  @Test
  public void testInvalidateIgnoreErrors() {
    List<CommerceCacheInvalidation> invalidations = new ArrayList<>();
    CommerceCacheInvalidation eventIncomplete = new CommerceCacheInvalidationImpl();
    invalidations.add(eventIncomplete);

    CommerceCacheInvalidationImpl eventValid = new CommerceCacheInvalidationImpl();
    eventValid.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_PRODUCT);
    eventValid.setTechId("4711");
    invalidations.add(eventValid);


    testling.invalidate(invalidations);
    verify(cache, times(1)).invalidate("4711");
  }
}
