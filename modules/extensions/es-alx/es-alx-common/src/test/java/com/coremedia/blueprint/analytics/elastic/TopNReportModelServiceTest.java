package com.coremedia.blueprint.analytics.elastic;

import org.junit.Test;

import static com.coremedia.blueprint.analytics.elastic.TopNReportModelService.COLLECTION_NAME;
import static org.junit.Assert.assertEquals;

public class TopNReportModelServiceTest {

  private TopNReportModelService topNReportModelService = new TopNReportModelService();

  @Test
  public void getCollectionName() {
    assertEquals(COLLECTION_NAME, topNReportModelService.getCollectionName());
  }
}
