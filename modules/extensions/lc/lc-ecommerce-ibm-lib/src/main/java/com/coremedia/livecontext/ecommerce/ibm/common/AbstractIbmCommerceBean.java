package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceBean;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.cap.common.XmlGrammar;
import com.coremedia.livecontext.ecommerce.asset.AssetUrlProvider;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Locale;

abstract public class AbstractIbmCommerceBean extends AbstractCommerceBean {

  @Override
  public Locale getLocale() {
    return StoreContextHelper.getLocale(getContext());
  }

  public String getStoreId() {
    return StoreContextHelper.getStoreId(getContext());
  }

  public String getStoreName() {
    return StoreContextHelper.getStoreName(getContext());
  }

  /**
   * Sets a delegate as an arbitrarily backing object.
   * Its up to the concrete catalog implementation if a backing object is set from outside or whether the bean impl
   * handles it for itself privately. If a catalog service impl decides to set it from outside then it can use this
   * method. The bean impl must know how to handle (or cast) the given delegate parameter.
   *
   * @param delegate the arbitrarily backing object
   */
  public abstract void setDelegate(Object delegate);

  public AssetUrlProvider getAssetUrlProvider() {
    return Commerce.getCurrentConnection().getAssetUrlProvider();
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
    return MarkupFactory.fromString(sb.toString()).withGrammar(XmlGrammar.RICH_TEXT_1_0_ID);
  }
}
