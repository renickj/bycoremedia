package com.coremedia.blueprint.common.util.pagination;

import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * StAX based Paginator. Empty blocks are ignored in block count but included in
 * Markup.
 */
class StAXBlockElementPaginator extends AbstractPaginator { // NOSONAR  cyclomatic complexity
  private static final Log LOG = LogFactory.getLog(StAXBlockElementPaginator.class);

  private StringBuffer charactersBuffer = new StringBuffer();
  private XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
  private XMLEventFactory eventFactory = XMLEventFactory.newInstance();
  private XMLEvent divStart;
  private DelimitingPagingRule delimitingPagingRule;

  public StAXBlockElementPaginator() {
  }

  @Override
  public int getCharacterCounter() {
    return charactersBuffer.length();
  }

  @Override
  public void setPagingRule(PagingRule pagingRule) {
    super.setPagingRule(pagingRule);
    if (pagingRule instanceof DelimitingPagingRule) {
      this.setDelimitingPagingRule((DelimitingPagingRule) pagingRule);
    }
  }

  public void setDelimitingPagingRule(DelimitingPagingRule rule) {
    this.delimitingPagingRule = rule;
  }

  public DelimitingPagingRule getDelimitingPagingRule() {
    return this.delimitingPagingRule;
  }


  @Override
  public List<Markup> splitInternally(XMLEventReader xmlReader, Markup markup) throws UnsupportedEncodingException, XMLStreamException { // NOSONAR  cyclomatic complexity
    List<Markup> result = new ArrayList<>();
    List<XMLEvent> blockElements = new ArrayList<>();
    int level = 0;
    XMLEvent currentEvent = null;
    XMLEvent previousEvent = null;

    while (xmlReader.hasNext()) {
      if (currentEvent != null && !isIgnorableEvent(currentEvent)) {
        previousEvent = currentEvent;
      }
      currentEvent = xmlReader.nextEvent();
      if (currentEvent.isStartDocument()) {
        continue;
      }
      if (currentEvent.isEndDocument()) {
        continue;
      }
      if (currentEvent.isStartElement()
              && currentEvent.asStartElement().getName().getLocalPart().equals("div")) {
        divStart = currentEvent;
        continue;
      }
      if (currentEvent.isEndElement() && currentEvent.asEndElement().getName().getLocalPart().equals("div")) {
        continue;
      }
      if (currentEvent.isCharacters() && currentEvent.asCharacters().isWhiteSpace()) {
        continue;
      }
      String currentBlockName = null;
      if (currentEvent.isStartElement()) {
        level = level + 1;
        currentBlockName = currentEvent.asStartElement().getName().getLocalPart();
      }
      if (currentEvent.isEndElement()) {
        level = level - 1;
        currentBlockName = currentEvent.asEndElement().getName().getLocalPart();
      }

      if (null != delimitingPagingRule && null != currentBlockName && blockElements.size() > 0 &&
              delimitingPagingRule.matchesDelimiterTags(currentBlockName) && currentEvent.isStartElement()) {

        StartElement startElement = currentEvent.asStartElement();
        Attribute classAttr = startElement.getAttributeByName(new QName("class"));


        String classString = null;

        if (null != classAttr) {
          classString = classAttr.getValue();
        }

        if (delimitingPagingRule.matchesDelimiter(currentBlockName, classString)) {
          Markup extractedMarkup = buildMarkUp(blockElements, markup.getGrammar());
          if (extractedMarkup != null) {
            result.add(extractedMarkup);
          }
        }
      }

      if (currentEvent.isCharacters()) {
        Characters characters = currentEvent.asCharacters();
        charactersBuffer = charactersBuffer.append(characters.getData());
      }

      blockElements.add(currentEvent);
      // check if we should count block
      if (shouldCountBlock(level, currentBlockName, previousEvent)) {
        setBlockCounter(getBlockCounter() + 1);
      }
      if (currentBlockName != null && getPagingRule().match(currentBlockName) && level == 0) {
        Markup extractedMarkup = buildMarkUp(blockElements, markup.getGrammar());
        if (extractedMarkup != null) {
          result.add(extractedMarkup);
        }
      }
    }
    if (containsNonEmptyBlock(blockElements) && blockElements.size() > 0) {
      Markup extractedMarkup = buildMarkUp(blockElements, markup.getGrammar());
      if (extractedMarkup != null) {
        result.add(extractedMarkup);
      }
    }
    return result;

  }

  private boolean isIgnorableEvent(XMLEvent currentEvent) {
    return currentEvent.isCharacters() && currentEvent.asCharacters().toString().trim().length() == 0;
  }

  private boolean containsNonEmptyBlock(List<XMLEvent> blockElements) {
    XMLEvent currentEvent = null;
    XMLEvent previousEvent = null;
    for (XMLEvent blockElement : blockElements) {
      previousEvent = currentEvent;
      currentEvent = blockElement;
      String currentBlockName = null;
      if (currentEvent.isEndElement()) {
        currentBlockName = currentEvent.asEndElement().getName().getLocalPart();
      }
      if (previousEvent == null) {
        continue;
      }
      if ((previousEvent.isStartElement() && !previousEvent.asStartElement().getName().getLocalPart().equals(
              currentBlockName))) {
        return true;
      }
    }
    return false;
  }

  // only count non-empty top level blocks
  private boolean shouldCountBlock(int level, String currentBlockName, XMLEvent previousEvent) {
    if (level != 0) {
      return false;
    }
    if (previousEvent == null) {
      return false;
    }
    boolean isEmptyBlock = previousEvent.isStartElement()
            && previousEvent.asStartElement().getName().getLocalPart().equals(currentBlockName);
    return !isEmptyBlock;
  }

  private Markup buildMarkUp(List<XMLEvent> blockElements, String grammar) {
    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    ByteArrayInputStream bytesInputStream = null;
    XMLEventWriter streamWriter = null;
    try {
      streamWriter = xmlOutputFactory.createXMLEventWriter(byteArrayStream, "UTF-8");
      streamWriter.add(divStart);
      for (XMLEvent xmlEvent : blockElements) {
        streamWriter.add(xmlEvent);
      }
      streamWriter.add(eventFactory.createEndElement(divStart.asStartElement().getName(), divStart
              .asStartElement().getNamespaces()));
      streamWriter.flush();
      byteArrayStream.flush();
      blockElements.clear();
      setBlockCounter(0);
      charactersBuffer.delete(0, charactersBuffer.length());
      bytesInputStream = new ByteArrayInputStream(byteArrayStream.toByteArray());
      Markup result = MarkupFactory.fromInputStream(bytesInputStream);
      result = result.withGrammar(grammar);
      return result;
    } catch (IOException e) {
      LOG.error("Error flushing  streamwriter", e);
      return null;
    } catch (XMLStreamException e) {
      LOG.error("Error streaming xml", e);
      return null;
    } finally {
      if (bytesInputStream != null) {
        try {
          bytesInputStream.close();
        } catch (IOException e1) {
          LOG.error("buildMarUp() - closing stream has failed !");
        }
      }
      if (streamWriter != null) {
        try {
          streamWriter.close();
        } catch (Exception e) {
          LOG.error("buildMarUp() - closing stream has failed !");
        }
      }
      try {
        byteArrayStream.close();
      } catch (Exception e) {
        LOG.error("buildMarUp() - closing stream has failed !");
      }
    }

  }
}
