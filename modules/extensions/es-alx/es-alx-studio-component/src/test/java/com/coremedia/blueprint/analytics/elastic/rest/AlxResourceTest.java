package com.coremedia.blueprint.analytics.elastic.rest;

import com.coremedia.blueprint.analytics.elastic.PageViewReportModelService;
import com.coremedia.blueprint.analytics.elastic.PageViewResult;
import com.coremedia.blueprint.analytics.elastic.PublicationReportModelService;
import com.coremedia.blueprint.analytics.elastic.ReportModel;
import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.base.multisite.impl.SiteResolverImpl;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.WebApplicationException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.blueprint.analytics.elastic.ReportModel.REPORT_DATE_FORMAT;
import static com.coremedia.cae.testing.TestInfrastructureBuilder.Infrastructure;
import static com.coremedia.elastic.core.test.Injection.inject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AlxResourceTest {
  public static final String SERVICE_PROVIDER = "service";
  @InjectMocks
  private AlxResource alxResource = new AlxResource();

  @Mock
  private PageViewReportModelService pageViewReportModelService;

  @Mock
  private ReportModel pageViewReportModel;

  @Mock
  private ReportModel publicationReportModel;

  @Mock
  private PageViewResult pageViewResult;

  @Mock
  private Map<String, Long> reportMap;

  @Mock
  private Content content;

  @Mock
  private PublicationReportModelService publicationReportModelService;

  @Before
  public void setup() {
    Infrastructure infrastructure = TestInfrastructureBuilder.create()
            .withContentRepository("classpath:/com/coremedia/testing/contenttest.xml")
            .withBeans("classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml")
            .build();

    inject(alxResource, infrastructure.getContentRepository());
    inject(alxResource, infrastructure.getBean("sitesService", SitesService.class));
    inject(alxResource, publicationReportModelService);
    inject(alxResource, pageViewReportModelService);

    SiteResolver settingsService = infrastructure.getBean("siteResolver", SiteResolverImpl.class);
    inject(settingsService, infrastructure.getContentRepository());
    inject(settingsService, infrastructure.getBean("sitesService", SitesService.class));
    inject(settingsService, infrastructure.getBean("urlPathFormattingHelper", UrlPathFormattingHelper.class));

    when(pageViewReportModelService.getPageViewResult(any(Content.class), eq(SERVICE_PROVIDER))).thenReturn(pageViewResult);
    when(pageViewResult.getReportModel()).thenReturn(pageViewReportModel);
    when(pageViewResult.getTimeStamp()).thenReturn(new Date());

    // when(publicationReportModelService.getReportModel(any(Content.class), eq(SERVICE_PROVIDER))).thenReturn(publicationReportModel);
    when(publicationReportModelService.getReportModel(any(Content.class))).thenReturn(publicationReportModel);
    when(pageViewReportModel.getReportMap()).thenReturn(reportMap);
    when(publicationReportModel.getReportMap()).thenReturn(reportMap);
  }

  @Test
  public void getAlxDataNullValues() {
    when(publicationReportModelService.getReportModel(null, null)).thenReturn(publicationReportModel);
    when(reportMap.get(any(Date.class))).thenReturn(0L);

    ReportResult result = alxResource.getAlxData("4", SERVICE_PROVIDER, null);

    assertNotNull(result.getData());
    assertEquals(7, result.getData().size());
    assertNotNull(result.getTimeStamp());
  }

  @Test
  public void getAlxDataNoTimeStamp() {
    when(reportMap.get(any(Date.class))).thenReturn(0L);
    when(pageViewResult.getTimeStamp()).thenReturn(null);
    ReportResult result = alxResource.getAlxData("4", SERVICE_PROVIDER, null);
    assertNotNull(result.getData());
    assertEquals(0, result.getData().size());
    assertNull(result.getTimeStamp());
  }

  @Test
  public void getAlxData() {
    when(reportMap.get(any(Date.class))).thenReturn(3L);
    ReportResult result = alxResource.getAlxData("4", SERVICE_PROVIDER, null);
    List<AlxData> alxData = result.getData();
    assertNotNull(result.getData());
    assertEquals(7, result.getData().size());
    Date today = new Date();
    for (int i = 0; i < 7; i++) {
      Date date = DateUtils.addDays(today, -(6 - i));
      DateFormat dateFormat = new SimpleDateFormat(REPORT_DATE_FORMAT, Locale.getDefault());
      assertEquals(dateFormat.format(date), alxData.get(i).getKey());
      assertEquals(3, alxData.get(i).getValue());
    }
  }

  @Test
  public void getAlxDataWithInvalidRange() {
    when(reportMap.get(any(Date.class))).thenReturn(3L);
    ReportResult result = alxResource.getAlxData("4", SERVICE_PROVIDER, 0);
    List<AlxData> alxData = result.getData();
    assertNotNull(alxData);
    assertEquals(7, alxData.size());
    Date today = new Date();
    for (int i = 0; i < 7; i++) {
      Date date = DateUtils.addDays(today, -(6 - i));
      DateFormat dateFormat = new SimpleDateFormat(REPORT_DATE_FORMAT, Locale.getDefault());
      assertEquals(dateFormat.format(date), alxData.get(i).getKey());
      assertEquals(3, alxData.get(i).getValue());
    }
  }

  @Test
  public void getAlxDataForTimeRange() {
    when(reportMap.get(any(Date.class))).thenReturn(3L);
    ReportResult result = alxResource.getAlxData("4", SERVICE_PROVIDER, 3);
    List<AlxData> alxData = result.getData();
    assertNotNull(alxData);
    assertEquals(3, alxData.size());
    Date today = new Date();
    for (int i = 0; i < 3; i++) {
      Date date = DateUtils.addDays(today, -(2 - i));
      DateFormat dateFormat = new SimpleDateFormat(REPORT_DATE_FORMAT, Locale.getDefault());
      assertEquals(dateFormat.format(date), alxData.get(i).getKey());
      assertEquals(3, alxData.get(i).getValue());
    }
  }

  @Test(expected = WebApplicationException.class)
  public void getAlxDataNotCMLinkable() {
    alxResource.getAlxData("123456", SERVICE_PROVIDER, null);
  }

  @Test
  public void getRootChannels() {
    Map<String, Collection<Content>> rootChannels = alxResource.getRootChannels();
    assertNotNull(rootChannels);
    Collection<Content> rootChannelsCollection = rootChannels.get("rootChannels");
    assertEquals(4, rootChannelsCollection.size());
  }

  @Test
  public void getPublicationData() {
    Map<String, Long> data = mock(Map.class);
    when(data.get(any(String.class))).thenReturn(3L);
    when(publicationReportModel.getReportMap()).thenReturn(data);
    when(publicationReportModel.getLastSaved()).thenReturn(System.currentTimeMillis());
    ReportResult result = alxResource.getPublicationData("4", 7);

    List<AlxData> alxData = result.getData();
    assertNotNull(result.getData());
    assertEquals(7, result.getData().size());
    Date today = new Date();
    for (int i = 0; i < 7; i++) {
      Date date = DateUtils.addDays(today, -(6 - i));
      DateFormat dateFormat = new SimpleDateFormat(REPORT_DATE_FORMAT, Locale.getDefault());
      assertEquals(dateFormat.format(date), alxData.get(i).getKey());
      assertEquals(3, alxData.get(i).getValue());
    }
  }

  @Test
  public void getNoPublicationData() {
    Map<String, Long> data = mock(Map.class);
    when(data.get(any(String.class))).thenReturn(0L);
    when(publicationReportModel.getReportMap()).thenReturn(data);
    when(publicationReportModel.getLastSaved()).thenReturn(0L);
    ReportResult result = alxResource.getPublicationData("4", 7);

    assertNotNull(result.getData());
    assertEquals(0, result.getData().size());
  }

  @Test
  public void getEmptyPublicationData() {
    Map<String, Long> data = mock(Map.class);
    when(data.get(any(String.class))).thenReturn(0L);
    when(publicationReportModel.getReportMap()).thenReturn(data);
    when(publicationReportModel.getLastSaved()).thenReturn(System.currentTimeMillis());
    ReportResult result = alxResource.getPublicationData("4", 7);

    List<AlxData> alxData = result.getData();
    assertNotNull(result.getData());
    assertEquals(7, result.getData().size());
    Date today = new Date();
    for (int i = 0; i < 7; i++) {
      Date date = DateUtils.addDays(today, -(6 - i));
      DateFormat dateFormat = new SimpleDateFormat(REPORT_DATE_FORMAT, Locale.getDefault());
      assertEquals(dateFormat.format(date), alxData.get(i).getKey());
      assertEquals(0, alxData.get(i).getValue());
    }
  }

  @Test(expected = WebApplicationException.class)
  public void getPublicationDataNotCMLinkable() {
    alxResource.getPublicationData("123456", null);
  }
}
