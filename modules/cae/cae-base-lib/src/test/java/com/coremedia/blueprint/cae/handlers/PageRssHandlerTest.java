package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.cae.test.BlueprintMockRequestUtil;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.common.services.context.CurrentContextService;
import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.HttpError;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link com.coremedia.blueprint.cae.handlers.PageRssHandler}.
 */
public class PageRssHandlerTest {

  public static final String FEED_VIEW_NAME = "asFeed";
  public static final String FEED_URL = "/service/rss/media/124/feed.rss";
  public static final String FEED_URL_TOPIC = "/service/rss/media/4280/4250/feed.rss";
  public static final String CONTENT_REPOSITORY_URL = "classpath:/com/coremedia/blueprint/cae/handlers/rss/rss.xml";

  private static TestInfrastructureBuilder.Infrastructure infrastructure;

  private MockHttpServletResponse response = new MockHttpServletResponse();


  // --- setup ------------------------------------------------------

  @BeforeClass
  public static void setUpStatic() {
    infrastructure = HandlerTestUtil.setupInfrastructure(CONTENT_REPOSITORY_URL);
  }

  @Before
  public void setUp() throws Exception {
  }


  // --- tests ------------------------------------------------------

  /**
   * Test {@link PageRssHandler#handleRss}.
   */
  @Test
  public void testHandleRequest() throws Exception {
    ModelAndView modelAndView = HandlerTestUtil.request(infrastructure, FEED_URL);
    assertModel(modelAndView, 124);
    assertEquals(FEED_VIEW_NAME, modelAndView.getViewName());
  }

  /**
   * Test topicpage-request
   * {@link PageRssHandler#handleRssTopicPage}
   */
  @Test
  public void testHandleTopicPageRequest() throws Exception {
    ModelAndView modelAndView = HandlerTestUtil.request(infrastructure, FEED_URL_TOPIC);
    assertModel(modelAndView, 4280);
    assertEquals(FEED_VIEW_NAME, modelAndView.getViewName());
  }

  /**
   * Test "not found" for invalid taxonomy id of topicpage.
   * {@link PageRssHandler#handleRssTopicPage}.
   */
  @Test
  public void testNotFoundForInvalidTaxonomyId() throws Exception {
    ModelAndView modelAndView = HandlerTestUtil.request(infrastructure, "/service/rss/media/124/8888/feed.rss");
    assertNotFound("Taxonomy not found", modelAndView);
  }

  /**
   * Test "not found" for non-FeedSource.
   * {@link PageRssHandler#handleRss}.
   */
  @Test
  public void testNotFoundForNonFeedSource() throws Exception {
    ModelAndView modelAndView = HandlerTestUtil.request(infrastructure, "/service/rss/media/666/feed.rss");
    // 666 is not a navigation, but a regular linkable
    assertNotFound("not a FeedSource", modelAndView);
  }

  /**
   * Test "not found" if content does not exist.
   * {@link PageRssHandler#handleRss}.
   */
  @Test
  public void testNotFoundIfContentDoesNotExist() throws Exception {
    ModelAndView modelAndView = HandlerTestUtil.request(infrastructure, "/service/rss/media/668/feed.rss");
    // 668 does not exist
    assertNotFound("null navigation", modelAndView);
  }

  /**
   * Test "not found" if the root segment does not match the root segment of the content found by ID.
   * {@link PageRssHandler#handleRss}.
   */
  @Test
  public void testNotFoundIfRootSegmentDoesNotMatchContent() throws Exception {
    ModelAndView modelAndView = HandlerTestUtil.request(infrastructure, "/service/rss/notmedia/124/feed.rss");
    assertNotFound("wrong root segment", modelAndView);
  }

  /**
   * Test generation of RSS URL: /service/rootSegment/navigationId/asFeed/index.xml
   * <p/>
   * {@link PageRssHandler#buildLink(com.coremedia.blueprint.common.feeds.FeedSource, javax.servlet.http.HttpServletRequest)}
   */
  @Test
  public void testGenerateRssUrl() {
    CMNavigation channel = ContentTestCaseHelper.getContentBean(infrastructure, 124);
    String url = createRelativeShortUrl(channel, channel);
    assertEquals("Bad rss URL for channel", FEED_URL, url);
  }

  /**
   * Test generation of RSS URL: /service/rootSegment/navigationId/taxonomyId/asFeed/index.xml
   * <p/>
   * {@link PageRssHandler#buildTaxonomyLink(com.coremedia.blueprint.common.contentbeans.CMTaxonomy, javax.servlet.http.HttpServletRequest)}
   */
  @Test
  public void testGenerateTopicPageRssUrl() {
    CMNavigation nav = ContentTestCaseHelper.getContentBean(infrastructure, 124);
    CMTaxonomy taxonomy = ContentTestCaseHelper.getContentBean(infrastructure, 4250);


    // Must mock a current context, otherwise topic pages for
    // global taxonomies don't work.
    CurrentContextService ccs = mock(CurrentContextService.class);
    when(ccs.getContext()).thenReturn((CMChannel) nav);
    ContextHelper contextHelper = infrastructure.getBean("contextHelper", ContextHelper.class);
    contextHelper.setCurrentContextService(ccs);

    String url = createRelativeShortUrl(nav, taxonomy);
    // the resulting URL contains the ID of the topic page (4280), not of the root channel (124),
    // although the original context was the root channel
    assertEquals("Bad rss URL for topicchannel", FEED_URL_TOPIC, url);
  }

  // --- internal ---------------------------------------------------

  private String createRelativeShortUrl(CMNavigation navigation, Object bean) {
    MockHttpServletRequest request = BlueprintMockRequestUtil.createRequestWithContext(navigation);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    return infrastructure.getLinkFormatter().formatLink(bean, FEED_VIEW_NAME, request, response, false);
  }


  private void assertModel(ModelAndView modelAndView, int beanId) {
    ContentBean bean = getBean(beanId);
    assertNotNull("Model was not resolved correctly", modelAndView);
    assertEquals("Model does not match mocked content", bean, modelAndView.getModel().get("self"));
  }

  private void assertNotFound(String message, ModelAndView modelAndView) {
    assertHttpError(message, HttpServletResponse.SC_NOT_FOUND, modelAndView);
  }

  private void assertHttpError(String message, int expectedErrorCode, ModelAndView modelAndView) {
    assertEquals(String.format("Unexpected root model (%s):", message), HttpError.class, HandlerHelper.getRootModel(modelAndView).getClass());
    assertEquals(String.format("Unexpected error code (%s):", message), expectedErrorCode, ((HttpError) HandlerHelper.getRootModel(modelAndView)).getErrorCode());
  }

  private ContentBean getBean(int beanId) {
    return infrastructure.getContentBeanFactory().createBeanFor(infrastructure.getContentRepository().getContent(IdHelper.formatContentId(beanId)));
  }
}
