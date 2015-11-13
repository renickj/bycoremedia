package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.xml.Filter;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Filters the markup so that it is valid for Shoutem.
 */
public class ShoutemFilter extends Filter {
  private List<ElementMapping> mappings = new ArrayList<>();

  public ShoutemFilter() {
    addMapping("p", "p--heading-1", "h1");
    addMapping("p", "p--heading-2", "h2");
    addMapping("p", "p--heading-3", "h3");
    addMapping("p", "p--heading-4", "h4");
    //according to the sanitizer, only up to h5
    addMapping("p", "p--heading-5", "h5");
    addMapping("p", "p--heading-6", "h5");
    addMapping("p", "p--heading-7", "h5");
    addMapping("p", "p--heading-8", "h5");
    addMapping("p", "p--standard", "p");
    addMapping("p", "p--pre", "pre");
    addMapping("a", null, "em");
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if (StringUtils.isNotEmpty(qName)) {
      ElementMapping mapping = mapQName(qName, atts);
      if(mapping != null) {
        super.startElement(uri, localName, mapping.getReplace(), mapping.map(atts));
        return;
      }
    }

    // If no paragraph is found, do default
    super.startElement(uri, localName, qName, atts);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (StringUtils.isNotEmpty(qName)) {
      ElementMapping mapping = mapQName(qName, null);
      if(mapping != null) {
        super.endElement(uri, localName, mapping.getReplace());
        return;
      }
    }
    super.endElement(uri, localName, qName);
  }

  private void addMapping(String elementName, String cls, String replacedElementName) {
    this.mappings.add(new ElementMapping(elementName, cls, replacedElementName));
  }

  /**
   * Looks up if there is a mapping for the given qname and it's attributes
   * if we have a start element here.
   *
   * @param qname The qname of the element to map.
   * @param atts
   * @return
   */
  private ElementMapping mapQName(String qname, Attributes atts) {
    for (ElementMapping mapping : mappings) {
      if (mapping.matches(qname, atts)) {
        return mapping;
      }
    }
    return null;
  }

  /**
   * Helper class for replacing elements with or without css classes
   * with another element.
   */
  private static final class ElementMapping {
    private String name;
    private String cls;
    private String replace;

    public ElementMapping(String name, String cls, String replace) {
      this.name = name;
      this.cls = cls;
      this.replace = replace;
    }

    public String getReplace() {
      return replace;
    }

    /**
     * Returns true if this mapping is defined for
     * the given qname and css class
     *
     * @param qname The qname of the element.
     * @param atts  The attributes map.
     * @return true if this mapping is defined for the given qname and css class
     */
    public boolean matches(String qname, Attributes atts) {
      if (name.equalsIgnoreCase(qname)) {
        if(atts == null) {
          //we are dealing with the closing element, so ignore class matching.
          return true;
        }
        String css = atts.getValue("class");
        return css==null ? cls==null : css.equalsIgnoreCase(cls);
      }
      return false;
    }

    public Attributes map(Attributes atts) {
      //just overwrite existing attributes
      return new AttributesImpl();
    }
  }
}
