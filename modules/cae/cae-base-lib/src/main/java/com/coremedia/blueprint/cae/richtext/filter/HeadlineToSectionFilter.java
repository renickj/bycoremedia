package com.coremedia.blueprint.cae.richtext.filter;

import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.xml.Filter;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Manipulates markup by adding section tags between all configured #headlines and directly after the beginning and
 * before the end of richtext rendering
 */
public class HeadlineToSectionFilter extends Filter implements FilterFactory {
  private int relatedBoxAfterParagraph = 0;
  private int relatedBoxInSection = 0;
  private int amountOfrenderedParagraphs = 0;
  private int amountOfrenderedSections = 0;
  private static final String PARAGRAPH = "p";
  private static final String SECTION = "section";
  private static final String DIV = "div";
  private List<String> headlines;
  private boolean sectionIsOpen = false;
  private String lastElement = "";
  private boolean relatedBoxRendered = false;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @Override
  public Filter getInstance(HttpServletRequest request, HttpServletResponse response) {
    HeadlineToSectionFilter headlineToSectionFilter = new HeadlineToSectionFilter();
    headlineToSectionFilter.setHeadlines(this.headlines);
    headlineToSectionFilter.setRequest(request);
    headlineToSectionFilter.setResponse(response);
    if (request.getAttribute("relatedBoxAfterParagraph") != null) {
      headlineToSectionFilter.setRelatedBoxAfterParagraph((Integer) request.getAttribute("relatedBoxAfterParagraph"));
    }
    if (request.getAttribute("relatedBoxInSection") != null) {
      headlineToSectionFilter.setRelatedBoxInSection((Integer) request.getAttribute("relatedBoxInSection"));
    }
    return headlineToSectionFilter;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if (qName.equals(PARAGRAPH)) {
      amountOfrenderedParagraphs++;
      if (lastElement.equals(DIV)) {
        openSection(uri);
      }
    }
    if (isHeadline(qName) && sectionIsOpen) {
      closeSection(uri);
    }
    super.startElement(uri, localName, qName, atts);
    lastElement = qName;
  }

  private boolean isHeadline(String qName) {
    for (String headline : headlines) {
      if (qName.equals(headline)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equals(DIV) && sectionIsOpen) {
      closeSection(uri);
    }
    super.endElement(uri, localName, qName);
    if (qName.equals(PARAGRAPH) && !relatedBoxRendered && amountOfrenderedParagraphs >= relatedBoxAfterParagraph && amountOfrenderedSections >= relatedBoxInSection) {
      relatedBoxRendered = true;
      Writer out = new StringWriter();
      ViewUtils.render(request.getAttribute("self"), "related", out, request, response);
      raw(out.toString().toCharArray(), 0, out.toString().toCharArray().length);
    }
    if (isHeadline(qName) && !sectionIsOpen) {
      openSection(uri);
    }

  }

  private void openSection(String uri) throws SAXException {
    super.startElement(uri, SECTION, SECTION, new AttributesImpl());
    amountOfrenderedSections++;
    sectionIsOpen = true;
  }

  private void closeSection(String uri) throws SAXException {
    super.endElement(uri, SECTION, SECTION);
    sectionIsOpen = false;
    amountOfrenderedParagraphs = 0;
  }

  @Required
  public void setHeadlines(List<String> headlines) {
    this.headlines = headlines;
  }

  public void setRelatedBoxAfterParagraph(int relatedBoxAfterParagraph) {
    this.relatedBoxAfterParagraph = relatedBoxAfterParagraph;
  }

  public void setRelatedBoxInSection(int relatedBoxInSection) {
    this.relatedBoxInSection = relatedBoxInSection;
  }

  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }
}
