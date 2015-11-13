package com.coremedia.livecontext.handler;


import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.cap.multisite.Site;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.livecontext.ecommerce.ibm.user.UserContextHelper;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.fragment.links.context.Context;
import com.coremedia.livecontext.fragment.links.context.ContextBuilder;
import com.coremedia.livecontext.fragment.links.context.LiveContextContextHelper;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContext;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class FragmentCommerceContextInterceptorTest {

  FragmentCommerceContextInterceptor testling;

  @Mock
  private LiveContextSiteResolver siteLinkHelper;

  @Mock
  private Site site;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private BaseCommerceConnection connection;

  @Mock
  private ContractService contractService;

  @Before
  public void setup() {
    initMocks(this);

    connection = MockCommerceEnvBuilder.create().setupEnv();
    connection.getStoreContext().put(StoreContextBuilder.SITE, "siteId");
    connection.setContractService(contractService);

    testling = new FragmentCommerceContextInterceptor();
    testling.setSiteResolver(siteLinkHelper);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
    testling.setPreview(false);
  }

  @Test
  public void testInitUserContextProvider() {
    testling.afterPropertiesSet();
    MockHttpServletRequest request = new MockHttpServletRequest();
    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.user.id", "userId");
    fragmentContext.put("wc.user.loginid", "loginId");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.initUserContext(request);
    assertTrue("UserContext must have been initialized yet", testling.isUserContextInitialized(request));
    UserContext userContext = UserContextHelper.getCurrentContext();
    assertEquals("userId", userContext.getUserId());
    assertEquals("loginId", userContext.getUserName());
  }

  @Test
  public void testInitStoreContextWithContractIds() {
    testling.setPreview(true);
    testling.afterPropertiesSet();
    Collection<Contract> contracts = new ArrayList<>();
    Contract contract1 = mock(Contract.class);
    Contract contract2 = mock(Contract.class);
    when(contract1.getExternalTechId()).thenReturn("contract1");
    when(contract2.getExternalTechId()).thenReturn("contract2");
    contracts.add(contract1);
    contracts.add(contract2);
    when(contractService.findContractIdsForUser(any(UserContext.class), any(StoreContext.class)))
            .thenReturn(contracts);

    MockHttpServletRequest request = new MockHttpServletRequest();
    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.user.id", "userId");
    fragmentContext.put("wc.user.loginid", "loginId");
    fragmentContext.put("wc.preview.contractIds", "contract1 contract2");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.initStoreContext(site, request);
    testling.initUserContext(request);
    String[] contractIdsInStoreContext = StoreContextHelper.getCurrentContext().getContractIds();
    List storeContextList = Arrays.asList(contractIdsInStoreContext);
    Collections.sort(storeContextList);
    List expected = Arrays.asList("contract1", "contract2");
    Collections.sort(storeContextList);

    assertArrayEquals(expected.toArray(), storeContextList.toArray());
  }

  @Test
  public void testInitStoreContextWithContractIdsButDisabledProcessing() {
    testling.setPreview(true);
    testling.afterPropertiesSet();
    testling.setContractsProcessingEnabled(false);
    Collection<Contract> contracts = new ArrayList<>();
    Contract contract1 = mock(Contract.class);
    Contract contract2 = mock(Contract.class);
    when(contract1.getExternalTechId()).thenReturn("contract1");
    when(contract2.getExternalTechId()).thenReturn("contract2");
    contracts.add(contract1);
    contracts.add(contract2);
    when(contractService.findContractIdsForUser(any(UserContext.class), any(StoreContext.class)))
            .thenReturn(contracts);

    MockHttpServletRequest request = new MockHttpServletRequest();
    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.user.id", "userId");
    fragmentContext.put("wc.user.loginid", "loginId");
    fragmentContext.put("wc.preview.contractIds", "contract1 contract2");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.initStoreContext(site, request);
    testling.initUserContext(request);
    String[] contractIdsInStoreContext = StoreContextHelper.getCurrentContext().getContractIds();
    assertNull(contractIdsInStoreContext);
  }

  @Test
  public void testInitStoreContextProviderInPreview() {
    testling.setPreview(true);
    testling.afterPropertiesSet();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/helios");

    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.preview.memberGroups", "memberGroup1, memberGroup2");
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.timezone", "Europe/Berlin");
    fragmentContext.put("wc.preview.workspaceId", "4711");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.initStoreContext(site, request);

    assertTrue("UserContext must have been initialized yet", testling.isStoreContextInitialized(request));

    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    assertEquals("memberGroup1, memberGroup2", storeContext.getUserSegments());
    assertEquals("4711", storeContext.getWorkspaceId());

    assertEquals("02-07-2014 17:57 Europe/Berlin", storeContext.getPreviewDate());

    Calendar calendar = parsePreviewDateIntoCalendar(storeContext.getPreviewDate());
    SimpleDateFormat sdb = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    assertEquals(sdb.format(calendar.getTime()) + " Europe/Berlin", storeContext.getPreviewDate());
    assertEquals(calendar, request.getAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE));
  }

  @Test
  public void testInitStoreContextProviderWithTimeShift() {
    testling.setPreview(true);
    testling.afterPropertiesSet();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/helios");

    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.timezone", "US/Pacific");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.initStoreContext(site, request);

    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    assertEquals("02-07-2014 17:57 US/Pacific", storeContext.getPreviewDate());

    Calendar calendar = parsePreviewDateIntoCalendar(storeContext.getPreviewDate());
    String requestParam = FragmentCommerceContextInterceptor.convertToPreviewDateRequestParameterFormat(calendar);
    assertEquals(requestParam, storeContext.getPreviewDate());
    assertEquals(calendar, request.getAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE));
  }

  @Test
  public void testConvertPreviewDate() {
    testling.setPreview(true);
    testling.afterPropertiesSet();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/helios");

    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");

    Context fragmentContext = ContextBuilder.create().build();
    fragmentContext.put("wc.preview.memberGroups", "memberGroup1, memberGroup2");
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.timezone", "Europe/Berlin");
    fragmentContext.put("wc.preview.workspaceId", "4711");
    LiveContextContextHelper.setContext(request, fragmentContext);

    testling.initStoreContext(site, request);

    assertTrue("UserContext must have been initialized yet", testling.isStoreContextInitialized(request));

    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    assertEquals("memberGroup1, memberGroup2", storeContext.getUserSegments());
    assertEquals("4711", storeContext.getWorkspaceId());

    SimpleDateFormat sdb = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    assertEquals("02-07-2014 17:57 Europe/Berlin", storeContext.getPreviewDate());

    Calendar calendar = parsePreviewDateIntoCalendar(storeContext.getPreviewDate());
    assertEquals(sdb.format(calendar.getTime()) + " Europe/Berlin", storeContext.getPreviewDate());
  }

  @Test
  public void testInitStoreContextProviderInLive() {
    testling.afterPropertiesSet();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo("/helios");
    Context fragmentContext = ContextBuilder.create().build();
    Timestamp ts = Timestamp.valueOf("2014-07-02 17:57:00.000");
    fragmentContext.put("wc.preview.memberGroups", "memberGroup1, memberGroup2");
    fragmentContext.put("wc.preview.timestamp", ts.toString());
    fragmentContext.put("wc.preview.workspaceId", "4711");

    testling.initStoreContext(site, request);

    assertTrue("UserContext must have been initialized yet", testling.isStoreContextInitialized(request));
    StoreContext storeContext = StoreContextHelper.getCurrentContext();
    assertNull(storeContext.getUserSegments());
    assertNull(storeContext.getPreviewDate());
    assertNull(storeContext.getWorkspaceId());
    assertNull(request.getAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE));
  }

  private static Calendar parsePreviewDateIntoCalendar(String previewDate) {
    Calendar calendar = null;
    if (previewDate != null && previewDate.length() > 0) {
      try {
        calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        calendar.setTime(sdf.parse(previewDate.substring(0, previewDate.lastIndexOf(' '))));
        calendar.setTimeZone(TimeZone.getTimeZone(previewDate.substring(previewDate.lastIndexOf(' ') + 1)));
      } catch (ParseException e) {
        // do nothing
      }
    }
    return calendar;
  }

}
