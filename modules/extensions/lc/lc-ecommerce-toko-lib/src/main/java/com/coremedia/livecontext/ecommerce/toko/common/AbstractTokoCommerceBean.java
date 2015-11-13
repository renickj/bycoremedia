package com.coremedia.livecontext.ecommerce.toko.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceBean;
import com.coremedia.livecontext.ecommerce.toko.catalog.TokoCatalogMock;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Required;

abstract public class AbstractTokoCommerceBean extends AbstractCommerceBean {

  public static final String ID_KEY = "externalId";
  protected static final String SHORT_DESCRIPTION_KEY = "shortDescription";
  protected static final String NAME_KEY = "name";
  protected static final String TITLE_KEY = "title";

  protected TokoCatalogMock catalogMock;

  private JsonNode delegate;

  public JsonNode getDelegate() {
    if (delegate == null) {
      load();
    }
    return delegate;
  }

  /**
   * Sets a delegate as an arbitrarily backing object.
   * Its up to the concrete catalog implementation if a backing object is set from outside or whether the bean impl
   * handles it for itself privately. If a catalog service impl decides to set it from outside then it can use this
   * method. The bean impl must know how to handle (or cast) the given delegate parameter.
   *
   * @param delegate the arbitrarily backing object
   */
  public void setDelegate(JsonNode delegate) {
    this.delegate = delegate;
  }

  @Required
  public void setCatalogMock(TokoCatalogMock catalogMock) {
    this.catalogMock = catalogMock;
  }

  protected String getTextValue(String key) {
    JsonNode textNode = getDelegate().get(key);
    return textNode != null ? textNode.asText() : null;
  }

  protected static String getIdFromJsonNode(JsonNode jsonNode) {
    if (jsonNode != null) {
      JsonNode idNode = jsonNode.get(ID_KEY);
      if (idNode != null) {
        return idNode.asText();
      }
    }
    return null;
  }

  protected static Markup toRichtext(String str) {
    StringBuilder sb = new StringBuilder();
    sb.append("<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");  // NOSONAR
    if (str!=null && !str.isEmpty()) {
      sb.append("<p>");  // NOSONAR
      sb.append(StringEscapeUtils.escapeXml(str));
      sb.append("</p>");  // NOSONAR
    }
    sb.append("</div>");  // NOSONAR
    return MarkupFactory.fromString(sb.toString());
  }
}
