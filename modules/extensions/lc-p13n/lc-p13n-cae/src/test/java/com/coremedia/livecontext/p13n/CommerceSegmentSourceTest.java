package com.coremedia.livecontext.p13n;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.p13n.SegmentImpl;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.user.UserContextBuilder;
import com.coremedia.personalization.context.ContextCollection;
import com.coremedia.personalization.context.ContextCollectionImpl;
import com.coremedia.personalization.context.MapPropertyMaintainer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommerceSegmentSourceTest {

  public static final String CONFIG_ID = System.getProperty("lc.test.configID", "myConfigId");
  public static final String STORE_ID = System.getProperty("lc.test.storeId", "10202");
  public static final String STORE_NAME = System.getProperty("lc.test.storeName", "AuroraESite");
  public static final String CATALOG_ID = System.getProperty("lc.test.catalogId", "10051");

  public static final String LOCALE = "en_US";
  public static final String CURRENCY = "USD";
  public static final String USER1_NAME = "testUser";
  public static final String USER1_ID = "4711";

  CommerceSegmentSource testling;

  CommerceConnection commerceConnection;

  @Before
  public void setup() {
    initMocks(this);
    testling = new CommerceSegmentSource();
    testling.setContextName("commerce");

    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
    commerceConnection.getUserContext().setUserId(USER1_ID);
    commerceConnection.getUserContext().setUserName(USER1_NAME);
    Commerce.setCurrentConnection(commerceConnection);

  }

  @Test
  public void testPreHandle() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    ContextCollection contextCollection = new ContextCollectionImpl();

    SegmentImpl seg1 = mock(SegmentImpl.class);
    when(seg1.getName()).thenReturn("name1");
    when(seg1.getExternalId()).thenReturn("extId1");
    when(seg1.getExternalTechId()).thenReturn("techId1");
    when(seg1.getId()).thenReturn("id1");

    SegmentImpl seg2 = mock(SegmentImpl.class);
    when(seg2.getName()).thenReturn("name2");
    when(seg2.getExternalId()).thenReturn("extId2");
    when(seg2.getExternalTechId()).thenReturn("techId2");
    when(seg2.getId()).thenReturn("id2");

    List<Segment> segmentList = new ArrayList<>();
    segmentList.add(seg1);
    segmentList.add(seg2);
    when(commerceConnection.getSegmentService().findSegmentsForCurrentUser()).thenReturn(segmentList);

    testling.preHandle(request, response, contextCollection);
    MapPropertyMaintainer profile = (MapPropertyMaintainer) contextCollection.getContext("commerce");
    assertNotNull(profile);
    assertEquals("vendor:///catalog/segment/id1,vendor:///catalog/segment/id2,", profile.getProperty("usersegments"));
  }

  @Test
  public void testPreHandleFromUserContext() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    ContextCollection contextCollection = new ContextCollectionImpl();

    commerceConnection.getStoreContext().setUserSegments("id1,id2");
    testling.preHandle(request, response, contextCollection);
    MapPropertyMaintainer profile = (MapPropertyMaintainer) contextCollection.getContext("commerce");
    assertNotNull(profile);
    assertEquals("vendor:///catalog/segment/id1,vendor:///catalog/segment/id2,", profile.getProperty("usersegments"));
    verify(commerceConnection.getSegmentService(), times(0)).findSegmentsForCurrentUser();
  }

  private StoreContext getStoreContext() {
    StoreContext result = StoreContextHelper.createContext(CONFIG_ID, STORE_ID, STORE_NAME, CATALOG_ID, LOCALE, CURRENCY);
    StoreContextHelper.setWcsVersion(result, Float.toString(7.7f));
    return result;
  }


  private UserContext getUserContext() {
    UserContext userContext = UserContextBuilder.create().build();
    userContext.put(UserContextHelper.FOR_USER_ID, USER1_ID);
    userContext.put(UserContextHelper.FOR_USER_NAME, USER1_NAME);
    UserContextHelper.setCurrentContext(userContext);
    return userContext;
  }

}
