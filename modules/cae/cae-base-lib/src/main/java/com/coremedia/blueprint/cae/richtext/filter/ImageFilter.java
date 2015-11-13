package com.coremedia.blueprint.cae.richtext.filter;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.beans.ContentBeanFactory;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.xml.Filter;
import com.coremedia.xml.Xlink;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Delegates the rendering of embedded pictures to a JSP template
 */
public class ImageFilter extends Filter implements FilterFactory {
  public static final String CLASS = "class";
  public static final String HEIGHT = "height";
  public static final String WIDTH = "width";
  public static final String ALT = "alt";

  public static final String ATT_ALT = "att_alt";
  public static final String ATT_CLASS = "att_class";
  public static final String ATT_HEIGHT = "att_height";
  public static final String ATT_WIDTH = "att_width";
  public static final String ATT_ROLE = "att_role";
  public static final String ATT_TITLE = "att_title";

  private static final String VIEW_NAME = "asRichtextEmbed";

  private DataViewFactory dataViewFactory;
  private ContentRepository contentRepository;
  private ContentBeanFactory contentBeanFactory;
  private HttpServletRequest request;
  private HttpServletResponse response;


  @Override
  public ImageFilter getInstance(HttpServletRequest request, HttpServletResponse response) {
    ImageFilter i = new ImageFilter();
    i.setRequest(request);
    i.setResponse(response);
    i.setDataViewFactory(dataViewFactory);
    i.setContentRepository(contentRepository);
    i.setContentBeanFactory(contentBeanFactory);
    return i;
  }

  /**
   * Filter a start element event.
   *
   * @param uri       The element's Namespace URI, or the empty string.
   * @param localName The element's local name, or the empty string.
   * @param qName     The element's qualified (prefixed) name, or the empty
   *                  string.
   * @param atts      The element's attributes.
   * @throws org.xml.sax.SAXException The client may throw
   *                                  an exception during processing.
   */
  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if (handle(uri, localName, qName)) {
      String contentId = atts.getValue(Xlink.NAMESPACE_URI, Xlink.HREF);
      Content content = contentRepository.getContent(IdHelper.parseContentIdFromBlobId(contentId));
      ContentBean picture = contentBeanFactory.createBeanFor(content);
      picture = dataViewFactory.loadCached(picture, null);

      // Transfer attributes to request
      String title = Xlink.getTitle(atts);
      String role = Xlink.getRole(atts);
      String alt = atts.getValue(ALT);
      String sClass = atts.getValue(CLASS);

      Integer height = null;
      if (StringUtils.isNumeric(atts.getValue(HEIGHT))) {
        height = Integer.decode(atts.getValue(HEIGHT));
      }

      Integer width = null;
      if (StringUtils.isNumeric(atts.getValue(WIDTH))) {
        width = Integer.decode(atts.getValue(WIDTH));
      }

      request.setAttribute(ATT_TITLE, title);
      request.setAttribute(ATT_ROLE, role);
      request.setAttribute(ATT_ALT, alt);
      request.setAttribute(ATT_CLASS, sClass);
      request.setAttribute(ATT_HEIGHT, height);
      request.setAttribute(ATT_WIDTH, width);

      ViewUtils.render(picture, VIEW_NAME, this, request, response);
      return;
    }

    super.startElement(uri, localName, qName, atts);
  }

  boolean handle(String uri, String localName, String qName) {
    return StringUtils.isNotEmpty(qName) && qName.equals("img");
  }

  /**
   * Filter an end element event.
   *
   * @param uri       The element's Namespace URI, or the empty string.
   * @param localName The element's local name, or the empty string.
   * @param qName     The element's qualified (prefixed) name, or the empty
   *                  string.
   * @throws org.xml.sax.SAXException The client may throw
   *                                  an exception during processing.
   */
  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (handle(uri, localName, qName)) {
      return;
    }
    super.endElement(uri, localName, qName);
  }

  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }

  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  public void setContentBeanFactory(ContentBeanFactory contentBeanFactory) {
    this.contentBeanFactory = contentBeanFactory;
  }

  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }
}

