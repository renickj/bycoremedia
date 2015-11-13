package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.cae.test.BlueprintMockRequestUtil;
import com.coremedia.blueprint.common.contentbeans.CMCSS;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.common.contentbeans.CMJavaScript;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.blueprint.common.services.context.CurrentContextService;
import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TopicpageHandlerTest  {
  private static final String CONTENT_REPOSITORY_URL = "classpath:/com/coremedia/blueprint/cae/handlers/topicpages/topicpages.xml";
  private static TestInfrastructureBuilder.Infrastructure infrastructure;
  private static String idSeparator;
  private static String hamburgUrl;
  private static String holidayUrl;
  private static String lifestyleUrl;

  private CMChannel media;
  private CMChannel topicpageChannel;


  // --- Setup ------------------------------------------------------

  @BeforeClass
  public static void setUpStatic() {
    infrastructure = HandlerTestUtil.setupInfrastructure(CONTENT_REPOSITORY_URL);
    idSeparator = "-";
    hamburgUrl = "/media/topic/hamburg" + idSeparator + "4250";
    holidayUrl = "/media/topic/holiday" + idSeparator + "304";
    // Even though lifestyle has a custom topic page with segment "600-topic",
    // the URL is built with the segment "topic" of the default topic page.
    lifestyleUrl = "/media/topic/lifestyle" + idSeparator + "306";
  }

  @Before
  public void setUp() throws Exception {
    media = ContentTestCaseHelper.getContentBean(infrastructure, 124);
    topicpageChannel = ContentTestCaseHelper.getContentBean(infrastructure, 4280);
  }

  /**
   * Link building for global taxonomies needs the "current context"
   * of the request.
   * <p>
   * This mock must not be active during request handling tests, because during
   * URL resolving the current context is not yet available.
   */
  private void mockCurrentContext() {
    CurrentContextService ccs = mock(CurrentContextService.class);
    when(ccs.getContext()).thenReturn(media);
    ContextHelper contextHelper = infrastructure.getBean("contextHelper", ContextHelper.class);
    contextHelper.setCurrentContextService(ccs);
  }

  private void resetCurrentContext() {
    // Restore the static infrastructure
    ContextHelper contextHelper = infrastructure.getBean("contextHelper", ContextHelper.class);
    contextHelper.setCurrentContextService(infrastructure.getBean("currentContextService", CurrentContextService.class));
  }


  // --- Link Building ----------------------------------------------

  @Test
  public void testSiteTaxonomyLink() throws Exception {
    CMLinkable siteTaxonomy = ContentTestCaseHelper.getContentBean(infrastructure, 4250);
    String url = createRelativeShortUrl(siteTaxonomy);
    assertEquals("Bad topicpage url for site taxonomy", hamburgUrl, url);
  }

  @Test
  public void testGlobalTaxonomyLink() throws Exception {
    // setup
    mockCurrentContext();

    // test
    CMLinkable globalTaxonomy = ContentTestCaseHelper.getContentBean(infrastructure, 304);
    String url = createRelativeShortUrl(globalTaxonomy);
    assertEquals("Bad topicpage url for global taxonomy", holidayUrl, url);

    // tear down
    resetCurrentContext();
  }

  @Test
  public void testCustomTopicpage() throws Exception {
    // setup
    mockCurrentContext();

    // test
    CMLinkable globalTaxonomy = ContentTestCaseHelper.getContentBean(infrastructure, 306);
    String url = createRelativeShortUrl(globalTaxonomy);
    assertEquals("Bad topicpage url for custom topicpage", lifestyleUrl, url);

    // tear down
    resetCurrentContext();
  }


  // --- Request Handling -------------------------------------------

  @Test
  public void testSiteTopicpageRequest() throws Exception {
    ModelAndView mav = HandlerTestUtil.request(infrastructure, hamburgUrl);
    HandlerTestUtil.checkPage(mav, 4280, 4280); //the topic page channel is rendered for taxonomies
  }

  @Test
  public void testGlobalTopicpageRequest() throws Exception {
    ModelAndView mav = HandlerTestUtil.request(infrastructure, holidayUrl);
    HandlerTestUtil.checkPage(mav, 4280, 4280); //the topic page channel is rendered for taxonomies
  }

  @Test
  public void testCustomTopicpageRequest() throws Exception {
    ModelAndView mav = HandlerTestUtil.request(infrastructure, lifestyleUrl);
    HandlerTestUtil.checkPage(mav, 600, 600);
  }

  @Test
  public void testBadSegmentRequest() throws Exception {
    ModelAndView mav = HandlerTestUtil.request(infrastructure, "/media/topic/badsegment" + idSeparator + "4250");
    HandlerTestUtil.checkError(mav, 404);
  }

  @Test
  public void testBadIdRequest() throws Exception {
    ModelAndView mav = HandlerTestUtil.request(infrastructure, "/media/topic/hamburg" + idSeparator + "4252");
    HandlerTestUtil.checkError(mav, 404);
  }

  @Test
  public void testBadTopicRequest() throws Exception {
    ModelAndView mav = HandlerTestUtil.request(infrastructure, "/media/badtopic/hamburg" + idSeparator + "4250");
    HandlerTestUtil.checkError(mav, 404);
  }


  // --- Channel ----------------------------------------------------

  // This section should rather be part of CMChannelImplTest, but here
  // we have the appropriate content to test the inheritance fallback
  // to the site's root context.

  @Test
  public void testCssInheritance() {
    List<? extends CMCSS> css = topicpageChannel.getCss();
    assertEquals("wrong number of csss", 1, css.size());
    assertEquals("wrong css", 584, css.get(0).getContentId());
  }

  @Test
  public void testJsInheritance() {
    List<? extends CMJavaScript> javaScript = topicpageChannel.getJavaScript();
    assertEquals("wrong number of javascripts", 1, javaScript.size());
    assertEquals("wrong css", 552, javaScript.get(0).getContentId());
  }


  // --- internal ---------------------------------------------------

  private String createRelativeShortUrl(Object bean) {
    MockHttpServletRequest request = BlueprintMockRequestUtil.createRequestWithContext(media);
    MockHttpServletResponse response = new MockHttpServletResponse();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    return infrastructure.getLinkFormatter().formatLink(bean, null, request, response, false);
  }

}
