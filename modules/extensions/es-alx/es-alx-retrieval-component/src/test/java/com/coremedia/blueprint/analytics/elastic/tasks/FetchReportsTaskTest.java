package com.coremedia.blueprint.analytics.elastic.tasks;

import com.coremedia.blueprint.analytics.elastic.ReportModel;
import com.coremedia.blueprint.analytics.elastic.TopNReportModelService;
import com.coremedia.blueprint.analytics.elastic.retrieval.AnalyticsServiceProvider;
import com.coremedia.blueprint.analytics.elastic.validation.ResultItemValidationService;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.elastic.core.api.models.Model;
import com.coremedia.elastic.core.api.models.Query;
import com.coremedia.elastic.core.api.tenant.TenantService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchReportsTaskTest {
  public static final String PROVIDER_NAME = "test";

  private FetchReportsTask fetchReportsTask;

  @Mock
  private CMALXBaseListService baseListInstances;

  @Mock
  private TenantService tenantService;

  @Mock
  private Content content;

  @Mock
  private AnalyticsServiceProvider serviceProvider;

  @Mock
  private RootContentProcessingTaskHelper rootContentProcessingTaskHelper;

  @Mock
  private TopNReportModelService modelService;

  @Mock
  private SettingsService settingsService;

  @Mock
  private ReportModel reportModel;

  @Mock
  private SitesService sitesService;

  private Map<String, Object> providerSettings;

  @Before
  public void setup() {
    providerSettings = new HashMap<>();

    when(settingsService.mergedSettingAsMap(PROVIDER_NAME, String.class, Object.class, content)).thenReturn(providerSettings);

    when(serviceProvider.getServiceKey()).thenReturn(PROVIDER_NAME);
    when(modelService.getReportModel(content, serviceProvider.getServiceKey())).thenReturn(reportModel);

    final Query query = mock(Query.class);
    List<Model> modelsToClear = Arrays.asList(mock(Model.class));
    when(modelService.query()).thenReturn(query);
    when(query.limit(10000)).thenReturn(query);
    when(query.fetch()).thenReturn(modelsToClear);
    fetchReportsTask = new FetchReportsTask(baseListInstances, modelService, tenantService, mock(ResultItemValidationService.class),rootContentProcessingTaskHelper, sitesService);
  }

  @Test
  public void testRunWithIntervalZero() throws Exception {
    providerSettings.put("applicationName", "CoreMedia");
    providerSettings.put("interval", 0);

    when(reportModel.getLastSaved()).thenReturn(1000L); // a very long time ago
//    when(reportModel.getSettings()).thenReturn(providerSettings);

    fetchReportsTask.run();
    verify(serviceProvider, never()).fetchDataFor(content, providerSettings);
    verify(reportModel, never()).save();
  }

  @Test
  public void testRunWithoutSettings() throws Exception {
    when(settingsService.mergedSettingAsMap(PROVIDER_NAME, String.class, Object.class, content)).thenReturn(Collections.<String, Object>emptyMap());

    fetchReportsTask.run();
    verify(serviceProvider, never()).fetchDataFor(content, null);
    verify(reportModel, never()).save();
  }

  @Test
  public void testRunOnFreshBeanForTenant() throws Exception {
    providerSettings.put("applicationName", "CoreMedia");
    providerSettings.put("interval", 10);

    when(modelService.getReportModel(content, serviceProvider.getServiceKey())).thenReturn(reportModel);
    when(reportModel.getLastSaved()).thenReturn(System.currentTimeMillis()); // very recently

    fetchReportsTask.run();
    verify(serviceProvider, never()).fetchDataFor(content, providerSettings);
    verify(reportModel, never()).save();
  }

  @Test
  public void testRunWithoutBeanForTenant() throws Exception {
    fetchReportsTask.run();
    verify(serviceProvider, never()).fetchDataFor(eq(content), any(Map.class));
    verify(reportModel, never()).save();
  }
}