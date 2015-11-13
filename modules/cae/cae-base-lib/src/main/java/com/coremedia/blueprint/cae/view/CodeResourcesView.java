package com.coremedia.blueprint.cae.view;

import com.coremedia.blueprint.cae.richtext.filter.ScriptFilter;
import com.coremedia.blueprint.cae.view.processing.Minifier;
import com.coremedia.blueprint.common.contentbeans.CMAbstractCode;
import com.coremedia.blueprint.common.contentbeans.CodeResources;
import com.coremedia.cache.Cache;
import com.coremedia.objectserver.view.ServletView;
import com.coremedia.objectserver.view.XmlFilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.XMLFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A programmed view to retrieve merged CSS/JS-Code from a page.
 */
public class CodeResourcesView implements ServletView {

  private static final Logger LOG = LoggerFactory.getLogger(CodeResourcesView.class);

  private Minifier minifier;
  private XmlFilterFactory xmlFilterFactory;
  private Cache cache;
  private String contentType;

  /**
   * Set the xmlFilterFactory from which the main filters can be retrieved.
   *
   * @param xmlFilterFactory the filter factory
   */
  @Required
  public void setXmlFilterFactory(XmlFilterFactory xmlFilterFactory) {
    this.xmlFilterFactory = xmlFilterFactory;
  }

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  @Required
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @Required
  public void setMinifier(Minifier minifier) {
    this.minifier = minifier;
  }

  /**
   * Renders {@link CMAbstractCode#getCode() resources } attached to a page into one merged file.
   * <br/>
   * Before merging, code will be preprocessed.
   * This is necessary because code is stored in a Richtext property, and the Richtext specific XML has to be
   * removed before writing the contents.
   *
   * @param bean     the page containing the resources.
   * @param view     the view
   * @param request  the request
   * @param response the response
   */
  @Override
  public void render(Object bean, String view, HttpServletRequest request, HttpServletResponse response) {

    if (!(bean instanceof CodeResources)) {
      throw new IllegalArgumentException(bean + " is no " + CodeResources.class);
    }

    CodeResources codeResources = (CodeResources) bean;

    try {
      renderResources(request, response, codeResources, response.getWriter());
    }
    catch (IOException e) {
      LOG.error("Error retrieving writer from HttpServletResponse.", e);
    }

  }

  //====================================================================================================================

  /**
   * Merge and render CMS-managed resources. Will also handle device-specific excludes.
   *
   * @param request    the request
   * @param response   the response
   * @param codeResources the codeResources element containing the resources
   * @param out        the writer to render to
   */
  private void renderResources(HttpServletRequest request, HttpServletResponse response, CodeResources codeResources, Writer out) {
    List<? extends CMAbstractCode> codes = codeResources.getMergeableResources();

    //set correct contentType
    response.setContentType(contentType);

    for (CMAbstractCode code : codes) {
      renderResource(request, response, code, out);
    }

  }

  /**
   * Render the given {@link CMAbstractCode resource} with all it's {@link CMAbstractCode#getInclude() includes}.
   *
   * @param request  the request
   * @param response the response
   * @param code     the resource
   * @param out      the writer to render to
   */
  private void renderResource(HttpServletRequest request, HttpServletResponse response, CMAbstractCode code, Writer out) {
      //construct xmlFilters to strip RichText from <div> and <p> tags
      ArrayList<XMLFilter> filters = new ArrayList<>();
      filters.addAll(xmlFilterFactory.createFilters(request, response, code.getCode(), "script"));
      filters.add(new ScriptFilter());

      CodeCacheKey cacheKey = new CodeCacheKey(code.getCode(), filters, code.getContent().getName(), minifier);
      String minifiedString = cache.get(cacheKey);

      try {
        out.write(minifiedString);
        out.append('\n');
      }
      catch (IOException e) {
        LOG.error("Unable to write Script to response.", e);
      }
  }
}