package com.coremedia.blueprint.cae.richtext.filter;

import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.view.TextView;
import com.coremedia.objectserver.view.ViewDispatcher;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.xml.ExtendedContentHandler;
import com.coremedia.xml.Filter;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinkEmbedFilterTest {
  private static final String HEADER = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n";
  private static final String FOOTER = "\n</div>";
  private static final String EMBEDDED_BLOCK = "<div class=\"embedded\"><p>embedded block snippet</p></div>";
  private static final String EMBEDDED_FLOW = "<span class=\"richTextImage\"><p>embedded flow snippet</p></span>";

  private static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withDataViewFactory()
          .withContentBeanFactory()
          .withContentRepository("classpath:/com/coremedia/blueprint/cae/richtext/filter/linkembedfiltertest-content.xml")
          .withBeans("framework/spring/blueprint-richtextfilters.xml")
          .build();

  private List<Filter> newXmlFilters = new ArrayList<>();
  private LinkEmbedFilter testling;


  // --- setup ------------------------------------------------------

  @Before
  public void setUp() {
    ViewDispatcher viewDispatcher = mock(ViewDispatcher.class);
    when(viewDispatcher.getView(any(Content.class), any(String.class))).thenReturn(new DivTextView());

    MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    httpServletRequest.setAttribute(ViewUtils.VIEWDISPATCHER, viewDispatcher);

    LinkEmbedFilter linkEmbedFilter = infrastructure.getBean("linkEmbedFilter", LinkEmbedFilter.class);
    testling = linkEmbedFilter.getInstance(httpServletRequest, new MockHttpServletResponse());
    testling.strictNestedPCheck = true;
    newXmlFilters.add(testling);
  }


  // --- tests ------------------------------------------------------

  @Test
  public void testBlockIntoDiv() {
    String result = getResult(248);
    String expected = HEADER +
            EMBEDDED_BLOCK +
            FOOTER;
    assertEquals("testBlockIntoDiv", expected, result);
  }

  @Test
  public void testBlockIntoOpenP() {
    String result = getResult(240);
    String expected = HEADER +
            "<p class=\"orig\">foo </p>" +
            EMBEDDED_BLOCK +
            "<p class=\"orig\"> bar</p>" +
            FOOTER;
    assertEquals("testBlockIntoOpenP", expected, result);
  }

  @Test
  public void testBlockIntoDelayedP() {
    String result = getResult(246);
    String expected = HEADER +
            EMBEDDED_BLOCK +
            "<p class=\"orig\"> behind</p>" +
            FOOTER;
    assertEquals("testBlockIntoDelayedP", expected, result);
  }

  @Test
  public void testBlockIntoEndOfP() {
    String result = getResult(264);
    String expected = HEADER +
            "<p class=\"orig\">foo </p>" +
            EMBEDDED_BLOCK +
            FOOTER;
    assertEquals("testBlockIntoOpenP", expected, result);
  }

  @Test
  public void testBlockIntoEmptyP() {
    String result = getResult(266);
    String expected = HEADER +
            EMBEDDED_BLOCK +
            FOOTER;
    assertEquals("testBlockIntoEmptyP", expected, result);
  }

  @Test
  public void testBlockIntoOpenSpan() {
    String result = getResult(244);
    String expected = HEADER +
            "<p class=\"orig\">bla <span class=\"spanclass\"> before </span></p>" +
            EMBEDDED_BLOCK +
            "<p class=\"orig\"><span class=\"spanclass\"> behind</span> blub</p>" +
            FOOTER;
    assertEquals("testBlockIntoOpenSpan", expected, result);
  }

  /**
   * Ensure that a p which has become empty by link embedding is omitted.
   */
  @Test
  public void testDropEmptiedP() {
    String result = getResult(258);
    String expected = HEADER +
            EMBEDDED_BLOCK +
            FOOTER;
    assertEquals("testDropEmptyP", expected, result);
  }

  /**
   * Ensure that a empty p which is not affected by link embedding
   * is not omitted by the LinkEmbedFilter.
   */
  @Test
  public void testDontTouchNotAffectedP() {
    String result = getResult(260);
    // If the source xml does not contain a link at all, Markup omits the
    // xlink namespace declaration.
    String expected = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\">\n" +
            "<p class=\"orig\"></p>" +
            FOOTER;
    assertEquals("testDontTouchNotAffectedP", expected, result);
  }


  @Test
  public void testFlowIntoDiv() {
    String result = getResult(252);
    String expected = HEADER +
            EMBEDDED_FLOW +
            FOOTER;
    assertEquals("testFlowIntoDiv", expected, result);
  }

  @Test
  public void testFlowIntoOpenP() {
    String result = getResult(254);
    String expected = HEADER +
            "<p class=\"orig\">foo </p>" +
            EMBEDDED_FLOW +
            "<p class=\"orig\"> bar</p>" +
            FOOTER;
    assertEquals("testBlockIntoOpenP", expected, result);
  }

  @Test
  public void testFlowIntoDelayedP() {
    String result = getResult(256);
    String expected = HEADER +
            EMBEDDED_FLOW +
            "<p class=\"orig\"> behind</p>" +
            FOOTER;
    assertEquals("testBlockIntoDelayedP", expected, result);
  }

  /**
   * Regression test for CMS-343:
   * This shows that the question of short tags is not subject of the LinkEmbedFilter
   * but of the invoking infrastructure.
   */
  @Test
  public void regressionTestCMS343() {
    String result = getResult(262);
    String expected = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\">\n" +
            "  <div id=\"outer\">\n" +
            "    TEXT1\n" +
            "    <div id=\"inner\"></div>\n" +  // Subject of this test: No short tag here!
            "    TEXT2\n" +
            "  </div>\n" +
            "</div>";
    assertEquals("regressionTestCMS-343", expected, result);
  }

  /**
   * Regression test for CMS-1673:
   * Reopening a p after embedding a link corrupted the element stack.
   */
  @Test
  public void regressionTestCMS1673() {
    assertTrue("No strictNestedPCheck, not a bug, but this test is meaningless.", testling.strictNestedPCheck);
    String result = getResult(268);
    // No need for checks, we are happy if no assertion failed so far.
  }


  // --- internal ---------------------------------------------------

  private String getResult(int id) {
    Markup markup = ContentTestCaseHelper.getContent(infrastructure, id).getMarkup("detailText");
    StringWriter stringWriter = new StringWriter();
    // Using an XHTML serializer is responsible for XHTML compliant
    // handling of empty divs and spans.  The LinkEmbedFilter has nothing
    // to do with it.
    ExtendedContentHandler xhtmlWriter = MarkupFactory.newXhtmlSerializer(stringWriter);
    markup.writeOn(newXmlFilters, xhtmlWriter);
    return stringWriter.toString();
  }

  /**
   * @ maintainer:
   * This must always match LinkEmbedFilter#hasBlockLevelView
   */
  private boolean embedAsFlow(Object bean) {
    return "CMPicture".equals(((Content) bean).getType().getName());
  }

  /**
   * This is a dummy view for simulating the rendering process.
   */
  private class DivTextView implements TextView {
    @Override
    public void render(Object bean, String view, Writer out, HttpServletRequest request, HttpServletResponse response) {
      try {
        if (embedAsFlow(bean)) {
          out.write(EMBEDDED_FLOW);
        } else {
          out.write(EMBEDDED_BLOCK);
        }
      } catch (IOException e) {
        throw new RuntimeException("Test is broken, not a product bug", e);
      }
    }
  }

}

