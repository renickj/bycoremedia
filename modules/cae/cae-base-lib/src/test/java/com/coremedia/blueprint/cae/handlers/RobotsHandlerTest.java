package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.cae.view.RobotsView;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.blueprint.base.links.impl.PrefixLinkPostProcessor;
import com.coremedia.blueprint.common.robots.RobotsBean;
import com.coremedia.blueprint.common.robots.RobotsEntry;
import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.objectserver.web.HttpError;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for RobotsHandler and RobotsBean to ensure a proper Robots.txt is generated
 */
public class RobotsHandlerTest {

  private static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withContentBeanFactory()
          .withContentRepository("classpath:/com/coremedia/testing/robots-test-content.xml")
          .withLinkFormatter()
          .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
          .withBeans("classpath:/com/coremedia/cae/view-error-services.xml")
          .withBeans("classpath:/spring/test/dummy-views.xml")
          .withBeans("classpath:/framework/spring-test/blueprint-robots-test.xml")
          .withBeans("classpath:/framework/spring/blueprint-handlers.xml")
          .build();

  private static final String segmentMedia = "media";
  private static final String segmentInvalid = "invalid";

  private ModelAndView mavMedia;
  private ModelAndView mavInvalid;
  private SettingsService settingsService;

  @Before
  public void setUp() throws Exception {

    PrefixLinkPostProcessor linkPostProcessor = infrastructure.getBean("prefixLinkPostProcessor", PrefixLinkPostProcessor.class);
    linkPostProcessor.setPrependBaseUri(false);

    RobotsHandler robotsHandler = infrastructure.getBean("robotsHandler", RobotsHandler.class);
    settingsService = infrastructure.getBean("settingsService", SettingsService.class);

    mavMedia = robotsHandler.handleRequest(segmentMedia);
    mavInvalid = robotsHandler.handleRequest(segmentInvalid);
  }

  @Test
  public void testHandleRequest() {

    RobotsBean robotsMedia = getValidRobotsBeanFromModel(mavMedia);
    assertEquals("expecting default view for media", "DEFAULT", mavMedia.getViewName());
    assertEquals("expecting 3 robots nodes for media", 3, robotsMedia.getRobotsEntries().size());

    Object self = mavInvalid.getModel().get("self");
    assertTrue("self object of invalid should be HttpError", self instanceof HttpError);
  }

  @Test
  public void testRobotsBean() {
    // test for root channel "Media":
    RobotsBean robotsMedia = getValidRobotsBeanFromModel(mavMedia);
    assertEquals("expecting 3 robots nodes for media", 3, robotsMedia.getRobotsEntries().size());

    CMChannel channelMedia = ContentTestCaseHelper.getContentBean(infrastructure, 2);
    List<Map> settingsList = settingsService.settingAsList(RobotsBean.SETTINGS_NAME, Map.class, channelMedia);
    assertEquals("test content for robots settings expected to have 3 nodes configured!", 3, settingsList.size());

    RobotsEntry nodeOne = robotsMedia.getRobotsEntries().get(0);
    RobotsEntry nodeTwo = robotsMedia.getRobotsEntries().get(1);

    assertEquals("node one: user-agent", "*", nodeOne.getUserAgent());
    assertEquals("node one: disallow", 2, nodeOne.getDisallowed().size());
    assertEquals("node one: allow", 1, nodeOne.getAllowed().size());
    assertEquals("node one: custom", 2, nodeOne.getCustom().size());

    assertEquals("node two: user-agent", "Googlebot", nodeTwo.getUserAgent());
    assertEquals("node two: disallow", 2, nodeTwo.getDisallowed().size());
    assertEquals("node two: allow", 0, nodeTwo.getAllowed().size());
    assertEquals("node two: custom", 0, nodeTwo.getCustom().size());
  }

  @Test
  public void testRobotsView() {
    RobotsView robotsView = infrastructure.getBean("robotsView", RobotsView.class);

    RobotsBean bean = getValidRobotsBeanFromModel(mavMedia);
    Writer writer = new StringWriter();
    MockHttpServletRequest request = new MockHttpServletRequest();
    CMChannel channel = ContentTestCaseHelper.getContentBean(infrastructure, 2);
    request.setAttribute(NavigationLinkSupport.ATTR_NAME_CMNAVIGATION, channel);
    request.setContextPath("/context");
    request.setServletPath("/servlet");
    MockHttpServletResponse response = new MockHttpServletResponse();

    robotsView.render(bean, null, writer, request, response);
    List<String> lines = new ArrayList<>();

    try {

      BufferedReader bufferedReader = new BufferedReader(new StringReader(writer.toString()));
      String line;

      while ((line = bufferedReader.readLine()) != null) {

        // empty lines do not matter for crawlers so no check on them required:
        if (line.length() > 0) {
          lines.add(line);
        }
      }

    } catch (IOException ex) {
      fail("unexpected exception during result check");
    }

    assertEquals("result size", 12, lines.size());
    int i = -1;
    assertEquals("user agent 1", "User-agent: *", lines.get(++i));
    assertEquals("disallow 1.1", "Disallow: /media/lifestyle", lines.get(++i));
    assertEquals("disallow 1.2", "Disallow: /media/lifestyle/", lines.get(++i));
    assertEquals("disallow 1.3", "Disallow: /media/sports", lines.get(++i));
    assertEquals("disallow 1.4", "Disallow: /media/sports/", lines.get(++i));
    assertEquals("allow 1.1", "Allow: /media/sports/title-beachsoccer-14", lines.get(++i));
    assertEquals("custom 1.1", "Crawl-delay: 10", lines.get(++i));
    assertEquals("custom 1.2", "Disallow: /*.asp$", lines.get(++i));
    assertEquals("user agent 2", "User-agent: Googlebot", lines.get(++i));
    assertEquals("disallow 2.1", "Disallow: /media/africa", lines.get(++i));
    assertEquals("disallow 2.2", "Disallow: /media/africa/", lines.get(++i));
    assertEquals("disallow 2.3", "Disallow: /media/africa/title-sanfrancisco-16", lines.get(++i));
//    assertEquals("sitemap", "Sitemap: http://localhost/sitemap_index.xml", lines.get(++i));
  }

  private RobotsBean getValidRobotsBeanFromModel(ModelAndView modelAndView) {

    assertNotNull("modelAndView must not be null", modelAndView);
    Object self = modelAndView.getModel().get("self");
    assertTrue("self expected to be of type RobotsBean", self instanceof RobotsBean);
    return (RobotsBean) self;
  }

}
