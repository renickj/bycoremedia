package com.coremedia.blueprint.cae;

import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import com.coremedia.cache.Cache;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.cae.testing.TestInfrastructureBuilder;


/**
 * Base test infrastructure all content bean tests
 */
public abstract class ContentBeanTestBase {

  private static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withContentBeanFactory()
          .withContentRepository("classpath:/com/coremedia/testing/contenttest.xml")
          .withDataViewFactory()
          .withIdProvider()
          .withLinkFormatter()
          .withCache()
          .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
          .build();

  public Content getContent(int id) {
    return ContentTestCaseHelper.getContent(infrastructure, id);
  }

  /**
   * Returns the ContentBean with the given id.
   *
   * @param id Id of ContentBean to get
   * @return ContentBean
   */
  protected <T> T getContentBean(int id) {
    return ContentTestCaseHelper.getContentBean(infrastructure, id);
  }

  public ContentBeanFactory getContentBeanFactory() {
    return infrastructure.getContentBeanFactory();
  }

  public DataViewFactory getDataViewFactory() {
    return infrastructure.getDataViewFactory();
  }

  public ContentRepository getContentRepository() {
    return infrastructure.getContentRepository();
  }

  public Cache getCache() {
    return infrastructure.getCache();
  }

  public static TestInfrastructureBuilder.Infrastructure getInfrastructure() {
    return infrastructure;
  }

  public SitesService getSitesService() {
    return infrastructure.getBean("sitesService", SitesService.class);
  }
}


