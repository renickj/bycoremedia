package com.coremedia.blueprint.cae.richtext.filter;

import com.coremedia.blueprint.testing.ContentTestCaseHelper;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.xml.Filter;
import com.coremedia.xml.Markup;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class AppendClassToElementFilterTest {

  private List<Filter> newXmlFilters;
  private Markup markup;
  private static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withContentBeanFactory()
          .withContentRepository("classpath:/com/coremedia/testing/contenttest.xml")
          .withBeans("framework/spring/blueprint-richtextfilters.xml")
          .build();

  @Before
  public void setUp() throws Exception {
    AppendClassToElementFilter appendClassToElementFilter = infrastructure.getBean("appendClassToElementFilter", AppendClassToElementFilter.class);
    markup = ContentTestCaseHelper.getContent(infrastructure, 40).getMarkup("detailText");
    newXmlFilters = new ArrayList<>();
    newXmlFilters.add(appendClassToElementFilter.getInstance(new MockHttpServletRequest(), new MockHttpServletResponse()));
  }

  @Test
  public void testFilter() throws Exception {
    StringWriter stringWriter = new StringWriter();
    markup.writeOn(newXmlFilters, stringWriter);
    org.junit.Assert.assertEquals("<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
            "  <p class=\"p--heading-1\">h1</p>\n" +
            "  <p class=\"p--heading-2\">h2</p>\n" +
            "  <p class=\"p--heading-3\">h3</p>\n" +
            "  <p class=\"p--heading-4\">h4</p>\n" +
            "  <p class=\"p--heading-5\">h5</p>\n" +
            "  <p class=\"p--heading-6\">h6</p>\n" +
            "  <p class=\"p--heading-7\">h7</p>\n" +
            "  <p class=\"p--heading-8\">h8</p>\n" +
            "  <p class=\"p--standard\">p</p>\n" +
            "  <p class=\"p--pre\">pre</p>\n" +
            "  <p>\n" +
            "    <a xlink:href=\"coremedia:///cap/content/16\" xlink:show=\"embed\" xlink:actuate=\"onRequest\">\n" +
            "      LondonBus\n" +
            "    </a>\n" +
            "    <ol class=\"rte--list\">\n" +
            "      <li>one</li>\n" +
            "      <li>two</li>\n" +
            "    </ol>\n" +
            "    London's vast urban area is often described\n" +
            "    using a set of district names (e.g. Bloomsbury,\n" +
            "    Knightsbridge, Mayfair, Whitechapel, Fitzrovia).\n" +
            "    These are either informal designations, or\n" +
            "    reflect the names of superseded villages,\n" +
            "    parishes and city wards. Such names have\n" +
            "    remained in use through tradition, each referring\n" +
            "    to a neighbourhood with its own distinctive\n" +
            "    character, but often with no modern official\n" +
            "    boundaries. Since 1965 Greater London has\n" +
            "    been divided into 32 London boroughs in addition\n" +
            "    to the ancient City of London.\n" +
            "    <ul class=\"rte--list\">\n" +
            "      <li>one</li>\n" +
            "      <li>two</li>\n" +
            "    </ul>\n" +
            "  </p>\n" +
            "  <p>\n" +
            "    This article is licensed under the\n" +
            "    <a xlink:href=\"http://www.gnu.org/copyleft/fdl.html\" xlink:show=\"new\" xlink:actuate=\"onRequest\">\n" +
            "      GNU Free Documentation License\n" +
            "    </a>\n" +
            "    . It uses material from the\n" +
            "    <a xlink:href=\"http://en.wikipedia.org/wiki/London\" xlink:show=\"new\" xlink:actuate=\"onRequest\">\n" +
            "      Wikipedia article \"London\"\n" +
            "    </a>\n" +
            "  </p>\n" +
            "</div>", stringWriter.toString());
  }
}
