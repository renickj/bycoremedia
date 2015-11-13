package com.coremedia.blueprint.testing;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.objectserver.beans.ContentBeanFactory;

import static com.coremedia.cae.testing.TestInfrastructureBuilder.Infrastructure;

/**
 * @deprecated better use plain SpringJunit4 tests
 */
@Deprecated
public final class ContentTestCaseHelper {

  // static class
  private ContentTestCaseHelper() {}

  /**
   * Returns the Content with the given id.
   *
   * @param id Id of Content to get
   * @return Content
   */
  public static Content getContent(Infrastructure infrastructure, String id) {
    Content content = infrastructure.getContentRepository().getContent(id);
    if (content == null) {
      throw new IllegalArgumentException("No Content found for id " + id);
    }
    return content;
  }

  /**
   * Returns the Content with the given id.
   *
   * @param id Id of Content to get
   * @return Content
   */
  public static Content getContent(Infrastructure infrastructure, int id) {
    return getContent(infrastructure, Integer.toString(id));
  }

  /**
   * Returns a typed ContentBean with the given id.
   *
   * @param id Id of ContentBean to get
   * @return ContentBean
   */
  public static <T> T getContentBean(Infrastructure infrastructure, int id) {
    Content content = getContent(infrastructure, id);
    return (T) infrastructure.getContentBeanFactory().createBeanFor(content);
  }

  /**
   * Returns a typed ContentBean with the given id.
   *
   * @param id Id of ContentBean to get
   * @return ContentBean
   */
  public static <T> T getContentBean(Infrastructure infrastructure, String id) {
    Content content = getContent(infrastructure, id);
    return (T) infrastructure.getContentBeanFactory().createBeanFor(content);
  }

  /**
   * Returns the Content with the given id.
   *
   * @param id Id of Content to get
   * @return Content
   */
  public static Content getContent(ContentRepository contentRepository, String id) {
    Content content = contentRepository.getContent(id);
    if (content == null) {
      throw new IllegalArgumentException("No Content found for id " + id);
    }
    return content;
  }

  /**
   * Returns the Content with the given id.
   *
   * @param id Id of Content to get
   * @return Content
   */
  public static Content getContent(ContentRepository contentRepository, int id) {
    return getContent(contentRepository, Integer.toString(id));
  }

  /**
   * Returns a typed ContentBean with the given id.
   *
   * @param id Id of ContentBean to get
   * @return ContentBean
   */
  public static <T> T getContentBean(ContentRepository contentRepository, ContentBeanFactory contentBeanFactory, int id) {
    Content content = getContent(contentRepository, id);
    return (T) contentBeanFactory.createBeanFor(content);
  }

  /**
   * Returns a typed ContentBean with the given id.
   *
   * @param id Id of ContentBean to get
   * @return ContentBean
   */
  public static <T> T getContentBean(ContentRepository contentRepository, ContentBeanFactory contentBeanFactory, String id) {
    Content content = getContent(contentRepository, id);
    return (T) contentBeanFactory.createBeanFor(content);
  }
}
