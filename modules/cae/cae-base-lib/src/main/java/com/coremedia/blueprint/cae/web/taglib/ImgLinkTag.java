package com.coremedia.blueprint.cae.web.taglib;

import com.coremedia.blueprint.cae.handlers.TransformedBlobHandler;
import com.coremedia.blueprint.common.contentbeans.CMMedia;
import com.coremedia.objectserver.view.ViewException;
import com.coremedia.objectserver.view.ViewServices;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.taglib.LinkSupport;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * This class defines a static method to create a link for a
 * given {@link com.coremedia.blueprint.common.contentbeans.CMMedia} using these parameters:
 * - transformationName
 * - width
 * - height
 */
@Deprecated //use ImageFunctions instead
public class ImgLinkTag extends LinkSupport {

  private static final long serialVersionUID = -1406573622943466517L;
  private static final String TRANSFORMATION_NAME = "transformationName";
  private static final String WIDTH = "width";
  private static final String HEIGHT = "height";

  //no need to escape the full URL
  private String escape = "false";
  /**
   * Parameter for the imageTransformation
   */
  private Map<String, Object> imageTransformation;

  private String varStatus;

  /**
   * The logger
   */
  private static final Log LOG = LogFactory.getLog(ImgLinkTag.class);

  /**
   * Setter for target
   *
   * @param target the target to be set
   */
  public void setTarget(Object target) {
    setTheTarget(target);
  }

  /**
   * Method to create Links on images
   *
   * @return a valid link or an empty string
   * @throws JspException
   */
  @Override
  public int doEndTag() throws JspException {
    Object blob;
    Map<String, String> params = new HashMap<>();

    HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
    HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
    LinkFormatter linkFormatter = ViewServices.getLinkFormatter(request);

    if (getTheTarget() == null) {
      LOG.warn("Target is null, probably no appropriate image resolution was found");
      return EVAL_PAGE;
    }
    if (getTheTarget() instanceof CMMedia) {
      CMMedia media = (CMMedia) getTheTarget();
      if (imageTransformation != null) {
        blob = media.getTransformedData(String.valueOf(imageTransformation.get(TRANSFORMATION_NAME)));
        saveAndAdd(params, TransformedBlobHandler.WIDTH_SEGMENT, String.valueOf(imageTransformation.get(WIDTH)), request);
        saveAndAdd(params, TransformedBlobHandler.HEIGHT_SEGMENT, String.valueOf(imageTransformation.get(HEIGHT)), request);
        if (blob == null) {
          blob = media.getData();
          LOG.debug("No blob found for transformation name " + imageTransformation.get(TRANSFORMATION_NAME) + " for content with id: " + ((CMMedia) getTheTarget()).getContentId() + ". Falling back to data property");
        }
      } else {
        blob = media.getData();
        LOG.debug("imageTransformation is null, probably no settings have been added to the root channel containing image transformation data.");
      }
    } else {
      blob = getTheTarget();
    }

    saveAttribute(ViewUtils.PARAMETERS);

    request.setAttribute(ViewUtils.PARAMETERS, params);

    String href = linkFormatter.formatLink(blob, getTheView(), request, response, false);

    if ("true".equals(escape)) {
      href = StringEscapeUtils.escapeHtml4(href);
    }
    if (getVar() == null) {
      try {
        pageContext.getOut().write(href);
      } catch (IOException e) {
        throw new ViewException("error writing img tag", e);
      }
    } else {
      pageContext.setAttribute(getVar(), href);
    }
    return EVAL_PAGE;
  }

  private void saveAndAdd(Map<String, String> map, String key, String value, HttpServletRequest request) {
    if (StringUtils.isNotEmpty(value)) {
      saveAttribute(key);
      request.setAttribute(key, value);
      map.put(key, value);
    }
  }

  public void setImageTransformation(Map<String, Object> imageTransformation) {
    this.imageTransformation = imageTransformation;
  }

  public String getVarStatus() {
    return varStatus;
  }

  public void setVarStatus(String varStatus) {
    this.varStatus = varStatus;
  }

}