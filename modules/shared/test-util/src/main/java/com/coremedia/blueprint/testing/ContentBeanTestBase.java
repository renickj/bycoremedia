package com.coremedia.blueprint.testing;

import com.coremedia.cache.Cache;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Base test infrastructure all content bean tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:/spring/test/test-repository.xml",
        "classpath:/framework/spring/blueprint-contentbeans.xml"
})

public abstract class ContentBeanTestBase {

  @Inject
  private ContentRepository contentRepository;
  @Inject
  private ContentBeanFactory contentBeanFactory;
  @Inject
  private DataViewFactory dataViewFactory;
  @Inject
  private Cache cache;

  public Content getContent(int id) {
    return contentRepository.getContent(IdHelper.formatContentId(id));
  }

  /**
   * Returns the ContentBean with the given id.
   *
   * @param id Id of ContentBean to get
   * @return ContentBean
   */
  protected <T> T getContentBean(int id) {
    return (T)contentBeanFactory.createBeanFor(getContent(id));
  }

  public ContentBeanFactory getContentBeanFactory() {
    return contentBeanFactory;
  }

  public DataViewFactory getDataViewFactory() {
    return dataViewFactory;
  }

  public ContentRepository getContentRepository() {
    return contentRepository;
  }

  public Cache getCache() {
    return cache;
  }

  public static void setUpPreviewDate() {
    setUpPreviewDate(2005, Calendar.JANUARY, 1);
  }

  public static void setUpPreviewDate(int year, int month, int day) {
    Calendar now = GregorianCalendar.getInstance();
    now.set(Calendar.YEAR, year);
    now.set(Calendar.MONTH, month);
    now.set(Calendar.DAY_OF_MONTH, day);
    //TODO: Use the constant ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE.
    setRequestAttribute(now, "previewDateObj", ServletRequestAttributes.SCOPE_REQUEST);
  }

  public static void setRequestAttribute(Object value, String attributeName, int scope) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestAttributes requestAttributes = new ServletRequestAttributes(request);
    requestAttributes.setAttribute(attributeName, value, scope);
    RequestContextHolder.setRequestAttributes(requestAttributes);
  }

  @After
  public void teardown() {
    // make sure that tests do not interfere with each other via thread locals!
    RequestContextHolder.resetRequestAttributes();
  }
}


