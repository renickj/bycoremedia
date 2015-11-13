package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.impl.PrefixLinkPostProcessor;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.objectserver.web.HandlerHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static com.coremedia.cae.testing.TestInfrastructureBuilder.Infrastructure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Utilities for Handler and Linkscheme tests
 */
public final class HandlerTestUtil {

  // static utility class
  private HandlerTestUtil() {
  }


  // --- setup utilities --------------------------------------------

  /**
   * Returns a TestInfrastructureBuilder which contains all features needed
   * for Handler and Linkscheme tests.
   * <p/>
   * Possibly add additional features in your particular test, call build(),
   * and complete the setup with initInfrastructure().
   */
  public static TestInfrastructureBuilder handlerInfrastructureBuilder() {
    return TestInfrastructureBuilder
            .create()
            .withContentBeanFactory()
            .withCache()
            .withIdProvider()
            .withLinkFormatter()
            .withHandlers()
            .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
            .withBeans("classpath:/framework/spring/blueprint-handlers.xml")
            .asWebEnvironment();
  }

  /**
   * S. {@link #handlerInfrastructureBuilder()}, incl. an XML repository.
   */
  public static TestInfrastructureBuilder handlerInfrastructureBuilder(String contentRepository) {
    return handlerInfrastructureBuilder().withContentRepository(contentRepository);
  }

  /**
   * Completes the setup of the test infrastructure by configuring
   * particular features.
   */
  public static void initInfrastructure(Infrastructure infrastructure) {
    PrefixLinkPostProcessor linkPostProcessor = infrastructure.getBean("prefixLinkPostProcessor", PrefixLinkPostProcessor.class);
    linkPostProcessor.setPrependBaseUri(false);
  }

  /**
   * Setup the infrastructure for Handler tests.
   */
  public static Infrastructure setupInfrastructure(String contentRepository) {
    Infrastructure infrastructure = handlerInfrastructureBuilder(contentRepository).build();
    initInfrastructure(infrastructure);
    return infrastructure;
  }


  // --- http utilities ---------------------------------------------

  /**
   * Create a http request.
   */
  public static MockHttpServletRequest createRequest(String shortUrl) {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/context/servlet" + shortUrl);
    request.setContextPath("/context");
    request.setServletPath("/servlet");
    request.setCharacterEncoding("UTF-8");
    return request;
  }


  // --- Handler utilities ------------------------------------------

  /**
   * Resolve the URL.
   */
  public static ModelAndView request(Infrastructure infrastructure, String url) throws Exception {
    return request(infrastructure, url, null);
  }

  /**
   * Resolve the URL.
   */
  public static ModelAndView request(Infrastructure infrastructure, String url,
                                     Map<String, String> params) throws Exception {
    return request(infrastructure, url, params, null);
  }

  /**
   * Resolve the URL.
   */
  public static ModelAndView request(Infrastructure infrastructure, String url,
                                     Map<String, String> params, Map<String, Object> requestAttributes)
          throws Exception {
    return request(infrastructure, url, params, requestAttributes, null);
  }

  /**
   * Resolve the URL.
   */
  public static ModelAndView request(Infrastructure infrastructure, String url,
                                     Map<String, String> params, Map<String, Object> requestAttributes,
                                     String contentType) throws Exception {
    return request(infrastructure, url, params, requestAttributes, contentType, null);
  }

  /**
   * Resolve the URL.
   */
  public static ModelAndView request(Infrastructure infrastructure, String url,
                                     Map<String, String> params, Map<String, Object> requestAttributes,
                                     String contentType, MockHttpServletResponse response)
          throws Exception {

    MockHttpServletRequest req = createRequest(url);

    if (params != null) {
      req.setParameters(params);
    }
    if (requestAttributes != null) {
      for (Map.Entry<String, Object> attribute : requestAttributes.entrySet()) {
        req.setAttribute(attribute.getKey(), attribute.getValue());
      }
    }
    if (contentType != null) {
      req.setContentType(contentType);
    }
    return request(infrastructure, req, response);
  }


  // --- Link formatting utilities ----------------------------------

  /**
   * Format a link with the given parameters.
   */
  public static String formatLink(Infrastructure infrastructure, Object bean) {
    return formatLink(infrastructure, null, bean);
  }

  /**
   * Format a link with the given parameters.
   */
  public static String formatLink(Infrastructure infrastructure, Map<String, Object> cmParams, Object bean) {
    return formatLink(infrastructure, cmParams, bean, null);
  }

  /**
   * Format a link with the given parameters.
   */
  public static String formatLink(Infrastructure infrastructure, Map<String, Object> cmParams,
                                  Object bean, Map<String, Object> requestAttributes) {
    return formatLink(infrastructure, cmParams, bean, requestAttributes, null);
  }

  /**
   * Format a link with the given parameters.
   */
  public static String formatLink(Infrastructure infrastructure, Map<String, Object> cmParams,
                                  Object bean, Map<String, Object> requestAttributes, String view) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    if (cmParams != null) {
      request.setAttribute(ViewUtils.PARAMETERS, cmParams);
    }
    if (requestAttributes != null) {
      for (Map.Entry<String, Object> attribute : requestAttributes.entrySet()) {
        request.setAttribute(attribute.getKey(), attribute.getValue());
      }
    }

    MockHttpServletResponse response = new MockHttpServletResponse();

    return infrastructure.getLinkFormatter().formatLink(bean, view, request, response, false);
  }


  // --- check utilities --------------------------------------------

  /**
   * Check that the mav holds a Page, return the Page.
   */
  public static Page extractPage(ModelAndView mav) {
    assertNotNull("null ModelAndView", mav);
    Object self = HandlerHelper.getRootModel(mav);
    assertNotNull("null self", self);
    assertTrue("not a page", self instanceof Page);
    return (Page) self;
  }

  /**
   * Check if a Page model consists of the expected content and channel.
   * <p>
   * Only applicable for content backed Pages.
   */
  public static void checkPage(ModelAndView mav, int contentId, int channelId) {
    Page page = extractPage(mav);
    Object content = page.getContent();
    assertNotNull("null content", content);
    assertEquals("wrong content", contentId, ((CMLinkable)content).getContentId());
    Navigation navigation = page.getNavigation();
    assertNotNull("null navigation", navigation);
    assertEquals("wrong navigation", channelId, ((CMNavigation)navigation).getContentId());
  }

  /**
   * Check if the model represents the expected Navigation.
   */
  public static void checkNavigation(ModelAndView mav, int channelId) {
    Object self = HandlerHelper.getRootModel(mav);
    assertNotNull("null self", self);
    assertTrue("not a CMNavigation", self instanceof CMNavigation);
    assertEquals("wrong navigation", channelId, ((CMNavigation) self).getContentId());
  }

  /**
   * Check if the model represents the expected class.
   */
  public static void checkModelAndView(ModelAndView mav, String expectedView, Class<?> clazz) {
    Object self = HandlerHelper.getRootModel(mav);
    assertNotNull("null self", self);
    assertTrue("not a " + clazz.getName(), clazz.isInstance(self));
    checkView(mav, expectedView);
  }

  /**
   * Check for the expected view.
   */
  public static void checkView(ModelAndView mav, String expectedView) {
    String view = mav.getViewName();
    if (expectedView==null) {
      // null and DEFAULT are equivalent and normalized to null during view dispatching anyway.
      assertTrue("wrong view: " + view, view==null || HandlerHelper.VIEWNAME_DEFAULT.equals(view));
    } else {
      assertTrue("wrong view: " + view, expectedView.equals(view));
    }
  }

  /**
   * Check if the model represents an HttpError.
   */
  public static void checkError(ModelAndView mav, int errorCode) {
    assertTrue(HandlerHelper.isError(mav, errorCode));
  }


  // --- internal ---------------------------------------------------

  private static ModelAndView request(Infrastructure infrastructure, MockHttpServletRequest request) throws Exception {
    MockHttpServletResponse response = new MockHttpServletResponse();
    return request(infrastructure, request, response);
  }

  private static ModelAndView request(Infrastructure infrastructure, MockHttpServletRequest request, MockHttpServletResponse response) throws Exception {
    if(response == null) {
      response = new MockHttpServletResponse();
    }
    return infrastructure.getRequestHandler().invokeHandler(request, response);
  }

}
