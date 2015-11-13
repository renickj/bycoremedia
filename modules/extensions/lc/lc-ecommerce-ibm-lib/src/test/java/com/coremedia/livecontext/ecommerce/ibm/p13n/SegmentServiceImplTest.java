package com.coremedia.livecontext.ecommerce.ibm.p13n;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractServiceTest;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SegmentServiceImplTest extends AbstractServiceTest {

  public static final String BEAN_NAME_SEGMENT_SERVICE = "segmentService";

  SegmentServiceImpl testling;

  @Before
  public void setup() {
    super.setup();
    testling = infrastructure.getBean(BEAN_NAME_SEGMENT_SERVICE, SegmentServiceImpl.class);
  }

  @Betamax(tape = "ssi_testFindAllSegments", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindAllSegments() throws Exception {
    if (StoreContextHelper.getWcsVersion(testConfig.getStoreContext()) < StoreContextHelper.WCS_VERSION_7_7) return;
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(null);
    UserContextHelper.setCurrentContext(userContext);
    List<Segment> segments = testling.findAllSegments();
    assertNotNull(segments);
    assertTrue(segments.size() > 0);
    Segment lastSegment = segments.get(segments.size() - 1);
    assertTrue("segment id has wrong format", lastSegment.getId().startsWith("ibm:///catalog/segment/8000"));
    assertEquals("Repeat Customers", lastSegment.getName());
  }

  @Betamax(tape = "ssi_testFindSegmentById", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindSegmentById() throws Exception {
    if (StoreContextHelper.getWcsVersion(testConfig.getStoreContext()) < StoreContextHelper.WCS_VERSION_7_7) return;
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(null);
    UserContextHelper.setCurrentContext(userContext);
    List<Segment> segments = testling.findAllSegments();
    assertNotNull(segments);
    Segment segment = findSegmentByName(segments, "Registered Customers");
    assertNotNull(segment);
    assertNotNull(segment.getExternalTechId());
    Segment segment2 = testling.findSegmentById(segment.getExternalTechId());
    assertNotNull(segment2);
    assertTrue("segment2 id has wrong format", segment.getId().startsWith("ibm:///catalog/segment/8000"));
    assertEquals("both segment names should be equal", segment.getName(), segment2.getName());
    assertEquals("both segments should be equal", segment, segment2);
  }

  @Betamax(tape = "ssi_testFindSegmentsByUser", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testFindSegmentsByUser() throws Exception {
    if (StoreContextHelper.getWcsVersion(testConfig.getStoreContext()) < StoreContextHelper.WCS_VERSION_7_7) return;
    StoreContextHelper.setCurrentContext(testConfig.getStoreContext());
    UserContext userContext = userContextProvider.createContext(USER2_NAME);
    userContext.put("forUserId", System.getProperty("lc.test.user2.id", "4"));
    UserContextHelper.setCurrentContext(userContext);
    List<Segment> segments = testling.findSegmentsForCurrentUser();
    assertNotNull(segments);
    assertEquals(System.getProperty("lc.segments.user2.count", "3"), "" + segments.size());
    Segment segment = findSegmentByName(segments, "Registered Customers");
    assertNotNull("\"Registered Customers\" segment should be there", segment);
    segment = findSegmentByName(segments, "Male Customers");
    assertNotNull("\"Male Customers\" segment should be there", segment);
    segment = findSegmentByName(segments, "Frequent Buyer");
    assertNotNull("\"Frequent Buyer\" segment should be there", segment);
  }

  private Segment findSegmentByName(List<Segment> segments, String segmentName) {
    Segment segment = null;
    for (Segment s : segments) {
      if (s.getName().contains(segmentName)) {
        segment = s;
      }
    }
    return segment;
  }

}
