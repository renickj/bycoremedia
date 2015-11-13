package com.coremedia.blueprint.common.util.pagination;

import com.coremedia.xml.Markup;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

abstract class AbstractPaginator implements Paginator {

  private static final Log LOG = LogFactory.getLog(AbstractPaginator.class);

  private XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
  private int blockCounter = 0;
  private PagingRule pagingRule;

  @Override
  public int getBlockCounter() {
    return blockCounter;
  }

  public void setBlockCounter(int blockCounter) {
    this.blockCounter = blockCounter;
  }

  @Override
  public void setPagingRule(PagingRule pagingRule) {
    this.pagingRule = pagingRule;
    this.pagingRule.setPaginator(this);
  }

  @Override
  public PagingRule getPagingRule() {
    return pagingRule;
  }

  @Override
  public List<Markup> split(Markup markup) {
    if (markup == null) {
      return Collections.emptyList();
    }
    XMLEventReader xmlReader = null;
    try {
      xmlReader = xmlInputFactory
              .createXMLEventReader(new ByteArrayInputStream(markup.asXml().getBytes("UTF-8")));
      return splitInternally(xmlReader, markup);
    } catch (UnsupportedEncodingException e) {
      LOG.error("Error getting bytes as UTF-8", e);
      return Collections.emptyList();
    } catch (XMLStreamException e) {
      LOG.error("Error streaming xml", e);
      return Collections.emptyList();
    } finally {
      if (xmlReader != null) {
        try {
          xmlReader.close();
        } catch (XMLStreamException e) {
          LOG.error("Error streaming xml", e);
        }
      }
    }
  }

  public abstract List<Markup> splitInternally(XMLEventReader xmlReader, Markup markup) throws UnsupportedEncodingException, XMLStreamException;
}
