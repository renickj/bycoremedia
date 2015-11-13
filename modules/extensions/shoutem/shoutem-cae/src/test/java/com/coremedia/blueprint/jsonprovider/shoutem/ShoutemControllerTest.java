package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.elastic.core.mongodb.settings.MongoDbSettings;
import com.mongodb.MongoClientURI;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ShoutemController}.
 */
public class ShoutemControllerTest {

  // create infrastructure for this test
  private static TestInfrastructureBuilder.Infrastructure infrastructure = createInfrastructure();

  private static TestInfrastructureBuilder.Infrastructure createInfrastructure() {
    MongoDbSettings settings = mock(MongoDbSettings.class);
    when(settings.getMongoClientURI()).thenReturn(new MongoClientURI("mongodb://localhost:1234"));
    return TestInfrastructureBuilder
            .create()
            .withContentBeanFactory()
            .withContentRepository("classpath:/com/coremedia/testing/contenttest.xml")
            .withBeans("classpath:/com/coremedia/blueprint/jsonprovider/shoutem/shoutem-beans.xml")
            .withBean("mongoDbSettings", settings)
            .build();
  }

  // the component to test
  private ShoutemController shoutemController = infrastructure.getBean("shoutemController", ShoutemController.class);

  @Test
  public void testNotImplemented() throws Exception {
    // create http request
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter(ShoutemApi.PARAM_METHOD, "method/not_a_method");
    request.setPathInfo("/blueprint/servlet/shoutemapi/media");
    // create http response
    MockHttpServletResponse response = new MockHttpServletResponse();

    // run the controller and verify the result
    ModelAndView modelAndView = shoutemController.handleRequest(request, response);
    assertNull(modelAndView);
    assertEquals(ShoutemController.RESPONSE_MESSAGE_NOT_IMPLEMENTED, response.getContentAsString());
  }

  @Test
  public void testSiteSegmentNotFound() throws Exception {
    // create http request
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter(ShoutemApi.PARAM_METHOD, ShoutemApi.METHOD_GET_SERVICE_INFO);
    request.setPathInfo("/blueprint/servlet/");
    // create http response
    MockHttpServletResponse response = new MockHttpServletResponse();

    // run the controller and verify the result
    ModelAndView modelAndView = shoutemController.handleRequest(request, response);
    assertNull(modelAndView);
    assertTrue(response.getContentAsString().contains("SITE SEGMENT NOT FOUND IN REQUEST URI"));
  }

  @Test
  public void testGetServiceInfo() throws Exception {
    // create http request
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter(ShoutemApi.PARAM_METHOD, ShoutemApi.METHOD_GET_SERVICE_INFO);
    request.setPathInfo("/blueprint/servlet/shoutemapi/media");
    // create http response
    MockHttpServletResponse response = new MockHttpServletResponse();

    // run the controller and verify the result
    ModelAndView modelAndView = shoutemController.handleRequest(request, response);
    assertNull(modelAndView);

    JSONAssert.assertEquals("{'server_type':'coremedia','api_version':1}", response.getContentAsString(), true);
  }

}
