package com.coremedia.blueprint.analytics.elastic;


import org.junit.Test;

import static com.coremedia.blueprint.analytics.elastic.PageViewTaskReportModelService.COLLECTION_NAME;
import static org.junit.Assert.assertEquals;

public class PageViewTaskReportModelServiceTest {

  private PageViewTaskReportModelService pageViewTaskReportModelService = new PageViewTaskReportModelService();

  @Test
  public void getCollectionName() {
    assertEquals(COLLECTION_NAME, pageViewTaskReportModelService.getCollectionName());
  }
}
