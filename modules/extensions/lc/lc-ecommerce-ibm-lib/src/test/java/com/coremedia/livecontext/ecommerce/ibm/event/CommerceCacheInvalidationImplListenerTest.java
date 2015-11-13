package com.coremedia.livecontext.ecommerce.ibm.event;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import co.freeside.betamax.Recorder;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.event.CommerceCacheInvalidation;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/com.coremedia.livecontext.ecommerce.ibm.service/test-commerce-services.xml",
        "classpath:/com/coremedia/cap/common/xml/uapi-xml-services.xml"
})

public class CommerceCacheInvalidationImplListenerTest {

  static long TEST_TIMESTAMP = 1403708220082L;

  @Inject
  CommerceCacheInvalidationListener commerceCacheInvalidationListener;

  @Inject
  WcRestConnector restConnector;

  @Mock
  private CommerceConnection connection;

  /*
  Set betamax default mode to READ_ONLY if not defined by user
   */
  private static final Properties sysProps;

  static {
    sysProps = System.getProperties();
    if (StringUtils.isEmpty(sysProps.getProperty("betamax.defaultMode"))) {
      sysProps.setProperty("betamax.defaultMode", "READ_ONLY");
    }
  }

  @Rule
  public Recorder recorder = new Recorder(sysProps);

  @Before
  public void setup() {

    MockitoAnnotations.initMocks(this);

    commerceCacheInvalidationListener.setEnabled(true);

    Commerce.setCurrentConnection(connection);
    StoreContext storeContext = StoreContextBuilder.create().build();
    when(connection.getStoreContext()).thenReturn(storeContext);
  }


  @Betamax(tape = "commercecache_testPollCacheInvalidations", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testPollCacheInvalidations() throws Exception {
    List<CommerceCacheInvalidation> commerceCacheInvalidations = commerceCacheInvalidationListener.pollCacheInvalidations();
    assertNotNull(commerceCacheInvalidations);
    assertTrue(commerceCacheInvalidations.size() == 0);
  }

  @Betamax(tape = "commercecache_testPollCacheInvalidationsWithEntries", match = {MatchRule.path})
  @Test
  public void testPollCacheInvalidationsWithEntries() throws Exception {
    //if you have to re-record this test, you should change the timestamp to now-1h or something similar
    commerceCacheInvalidationListener.lastInvalidationTimestamp = TEST_TIMESTAMP;
    List<CommerceCacheInvalidation> commerceCacheInvalidations = commerceCacheInvalidationListener.pollCacheInvalidations();

    assertNotNull(commerceCacheInvalidations);
    assertTrue(commerceCacheInvalidations.size() > 0);
    CommerceCacheInvalidation commerceCacheInvalidation = commerceCacheInvalidations.get(0);
    assertNotNull(commerceCacheInvalidation.getContentType());
    assertNotNull(commerceCacheInvalidation.getTechId());
    if (!commerceCacheInvalidation.getContentType().equals(CommerceCacheInvalidationListener.EVENT_CLEAR_ALL_EVENT_ID)) {
      assertNotNull(commerceCacheInvalidation.getId());
    }
    assertTrue(commerceCacheInvalidationListener.lastInvalidationTimestamp > TEST_TIMESTAMP ||
            commerceCacheInvalidationListener.lastInvalidationTimestamp <= 0);
  }


  @Test(expected = CommerceException.class)
  public void testPollCacheInvalidationsError() throws Exception {
    String origServiceEndpoint = restConnector.getServiceEndpoint();
    try {
      restConnector.setServiceEndpoint("http://does.not.exists/blub");
      commerceCacheInvalidationListener.pollCacheInvalidations();
    }finally {
      restConnector.setServiceEndpoint(origServiceEndpoint);
    }
  }

  @Test
  public void testAddCommerceBeanIdValid() {
    CommerceCacheInvalidationImpl productInvalidation = new CommerceCacheInvalidationImpl();
    productInvalidation.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_PRODUCT);
    productInvalidation.setTechId("4711");
    commerceCacheInvalidationListener.convertEvent(productInvalidation.getDelegate());
    assertEquals("ibm:///catalog/product/techId:4711", productInvalidation.getId());

    CommerceCacheInvalidationImpl categoryInvalidation = new CommerceCacheInvalidationImpl();
    categoryInvalidation.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_CATEGORY);
    categoryInvalidation.setTechId("4711");
    commerceCacheInvalidationListener.convertEvent(categoryInvalidation.getDelegate());
    assertEquals("ibm:///catalog/category/techId:4711", categoryInvalidation.getId());

    CommerceCacheInvalidationImpl topCategoryInvalidation = new CommerceCacheInvalidationImpl();
    topCategoryInvalidation.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_TOP_CATEGORY);
    topCategoryInvalidation.setTechId("4711");
    commerceCacheInvalidationListener.convertEvent(topCategoryInvalidation.getDelegate());
    assertEquals("ibm:///catalog/category/techId:4711", topCategoryInvalidation.getId());

    CommerceCacheInvalidationImpl segmentInvalidation = new CommerceCacheInvalidationImpl();
    segmentInvalidation.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_SEGMENT);
    segmentInvalidation.setTechId("4711");
    commerceCacheInvalidationListener.convertEvent(segmentInvalidation.getDelegate());
    assertEquals("ibm:///catalog/segment/4711", segmentInvalidation.getId());

    CommerceCacheInvalidationImpl marketingInvalidation = new CommerceCacheInvalidationImpl();
    marketingInvalidation.setContentType(CommerceCacheInvalidationListener.CONTENT_IDENTIFIER_MARKETING_SPOT);
    marketingInvalidation.setName("name");
    commerceCacheInvalidationListener.convertEvent(marketingInvalidation.getDelegate());
    assertEquals("ibm:///catalog/marketingspot/name", marketingInvalidation.getId());
  }

  @Test
  public void testAddCommerceBeanIdInValid() {
    CommerceCacheInvalidationImpl productInvalidation = new CommerceCacheInvalidationImpl();
    productInvalidation.setContentType("invalidContentType");
    productInvalidation.setTechId("4711");
    commerceCacheInvalidationListener.convertEvent(productInvalidation.getDelegate());
    assertNull(productInvalidation.getId());
  }


}
