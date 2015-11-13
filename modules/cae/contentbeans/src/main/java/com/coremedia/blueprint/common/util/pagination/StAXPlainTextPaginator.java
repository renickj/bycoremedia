package com.coremedia.blueprint.common.util.pagination;

import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

class StAXPlainTextPaginator extends AbstractPaginator {

  private static final Log LOG = LogFactory.getLog(StAXPlainTextPaginator.class);

  private StringBuffer charactersBuffer = new StringBuffer();
  private XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
  private XMLEventFactory eventFactory = XMLEventFactory.newInstance();
  private XMLEvent divStart;

  public StAXPlainTextPaginator() {
  }

  @Override
  public int getCharacterCounter() {
    return charactersBuffer.length();
  }


  @Override
  public List<Markup> splitInternally(XMLEventReader xmlReader, Markup markup) throws UnsupportedEncodingException, XMLStreamException {
    List<Markup> result = new ArrayList<>();
    while (xmlReader.hasNext()) {
      XMLEvent event = xmlReader.nextEvent();
      if (event.isCharacters() && event.asCharacters().isWhiteSpace()) {
        continue;
      }

      if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("div")) {
        divStart = event;
        continue;
      }

      if (event.isCharacters()) {
        Characters characters = event.asCharacters();
        charactersBuffer = charactersBuffer.append(characters.getData().trim()).append(" ");
      }
    }
    while (charactersBuffer.length() > 0) {
      Markup markupNode = buildMarkup();
      if (markupNode != null) {
        result.add(markupNode);
      }
    }
    return result;
  }

  private Markup buildMarkup() {
    int index = charactersBuffer.indexOf(" ", getPagingRule().getPagingUnitsNumber());
    if (index == -1) {
      index = charactersBuffer.length();
    }
    String writeCharacters = charactersBuffer.substring(0, index);

    charactersBuffer.delete(0, writeCharacters.length());
    String[] v = writeCharacters.split("\n");
    StringBuilder buffer = new StringBuilder();
    for (String aV : v) {
      buffer.append(aV.trim());
    }
    return buildMarkup(buffer.toString());
  }

  private Markup buildMarkup(String characters) {
    ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
    XMLEventWriter streamWriter = null;
    ByteArrayInputStream bytesInputStream = null;

    try {
      streamWriter = xmlOutputFactory.createXMLEventWriter(byteArrayStream);
      streamWriter.add(divStart);
      streamWriter.add(eventFactory.createStartElement("", "", "p"));
      streamWriter.add(eventFactory.createCharacters(characters));
      streamWriter.add(eventFactory.createEndElement("", "", "p"));
      streamWriter.add(eventFactory.createEndElement(divStart.asStartElement().getName(), divStart
              .asStartElement().getNamespaces()));
      streamWriter.flush();
      byteArrayStream.flush();
      setBlockCounter(0);
      bytesInputStream = new ByteArrayInputStream(byteArrayStream.toByteArray());
      return MarkupFactory.fromInputStream(bytesInputStream);
    } catch (IOException e) {
      LOG.error("Error flushing writer or stream", e);
      return null;
    } catch (XMLStreamException e) {
      LOG.error("Error streaming xml", e);
      return null;
    } finally {
      if (bytesInputStream != null) {
        try {
          bytesInputStream.close();
        } catch (IOException e1) {
          LOG.error("buildMarkup() - closing stream has failed !");
        }
      }
      if (streamWriter != null) {
        try {
          streamWriter.close();
        } catch (Exception e) {
          LOG.error("buildMarkup() - closing stream has failed !");
        }
      }
      try {
        byteArrayStream.close();
      } catch (Exception e) {
        LOG.error("buildMarkup() - closing stream has failed !");
      }
    }

  }
}
