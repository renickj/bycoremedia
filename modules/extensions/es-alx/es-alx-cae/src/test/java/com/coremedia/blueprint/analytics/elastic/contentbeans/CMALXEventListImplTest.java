package com.coremedia.blueprint.analytics.elastic.contentbeans;

import com.coremedia.blueprint.analytics.elastic.ReportModel;
import com.coremedia.blueprint.analytics.elastic.TopNReportModelService;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.cap.content.Content;
import com.coremedia.id.IdProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CMALXEventListImplTest {
  @InjectMocks
  private CMALXEventListImpl cmalxEventList = new CMALXEventListImpl();

  @Mock
  private TopNReportModelService cmalxBaseListModelServiceFactory;

  @Mock
  private Content content;

  @Mock
  private ReportModel reportModel;

  @Mock
  private IdProvider idProvider;

  @Mock
  private CMPicture cmPicture;

  @Before
  public void setup() {
  }

  @Test
  public void getItemsUnfiltered() {
    String service = "service";
    when(cmalxBaseListModelServiceFactory.getReportModel(content, service)).thenReturn(reportModel);
    when(content.getString(CMALXBaseList.ANALYTICS_PROVIDER)).thenReturn(service);
    String objectId = "1234";
    String teasableId = "5678";
    List<String> reportData = Arrays.asList(objectId, teasableId);
    when(reportModel.getReportData()).thenReturn(reportData);
    when(idProvider.parseId(objectId)).thenReturn(objectId);
    when(idProvider.parseId(teasableId)).thenReturn(cmPicture);
    List objects = cmalxEventList.getItemsUnfiltered();

    assertEquals(1, objects.size());
    assertEquals(cmPicture, objects.get(0));
  }
}
