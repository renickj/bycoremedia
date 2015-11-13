package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.cae.contentbeans.CMNavigationBase;
import com.coremedia.blueprint.cae.contentbeans.CodeResourcesImpl;
import com.coremedia.blueprint.common.contentbeans.CMAbstractCode;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CodeResources;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.common.Blob;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.xml.Markup;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.activation.MimeType;
import java.io.IOException;
import java.util.Map;

import static com.coremedia.blueprint.cae.handlers.CodeResourceHandler.MARKUP_PROGRAMMED_VIEW_NAME;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.checkError;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.checkModelAndView;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.checkView;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.formatLink;
import static com.coremedia.blueprint.cae.handlers.HandlerTestUtil.request;
import static com.coremedia.blueprint.testing.ContentTestCaseHelper.getContentBean;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link CodeResourceHandler}
 */
public class CodeResourceHandlerTest {

  private static final String CONTENT_REPOSITORY_URL = "classpath:/com/coremedia/blueprint/cae/handlers/coderesource/content.xml";

  // The link format is the same for local and repository resources.
  // These paths are not exactly realistic, because in contrast to this
  // unit test real frontend resources are not arranged in Java-driven
  // package directories.

  private static final String LINK_TO_SINGLE_RESOURCE = "/resource/com/coremedia/blueprint/cae/handlers/coderesource/js/my-custom-40-2.js";
  private static final String LINK_TO_NOT_LOCAL_RESOURCE = "/resource/com/coremedia/blueprint/cae/handlers/coderesource/js/not-local-42-3.js";

  private static final String LINK_TO_WRONG_SINGLE_CONTENT_RESOURCE_1 = "/resource/css/my-custom-40-2.css";
  private static final String LINK_TO_WRONG_SINGLE_CONTENT_RESOURCE_2 = "/resource/js/wrong-name-40-2.js";
  private static final String LINK_TO_SINGLE_CONTENT_RESOURCE_OLD_VERSION = "/resource/js/my-custom-40-1.js";

  private static final String LINK_TO_DELETED_LOCAL_RESOURCE = "/resource/com/coremedia/blueprint/cae/handlers/coderesource/js/deleted-50-2.js";
  private static final String LINK_TO_WRONG_LOCAL_RESOURCE = "/resource/js/does-not-exist.js";

  private static final String LINK_TO_MERGED_CONTENT_RESOURCE = "/resource/js/4/500ed6f52c0117f2c5b4218ce13f4563/media.js";
  private static final String LINK_TO_WRONG_SCRIPTHASH_MERGED_CONTENT_RESOURCE = "/resource/js/4/10/media.js";

  private static final String MERGED_JS_VIEW = "js";
  private static final String REDIRECT_VIEW_PREFIX = "redirect:";
  private static final String REDIRECT_DEFAULT_VIEW = REDIRECT_VIEW_PREFIX+ ViewUtils.DEFAULT_VIEW;
  private static final String REDIRECT_MERGED_JS_VIEW = REDIRECT_VIEW_PREFIX +MERGED_JS_VIEW;

  private static TestInfrastructureBuilder.Infrastructure infrastructure;
  private CodeResourceHandler testling;

  private CMContext contextBean;
  private CMAbstractCode abstractCodeBean;


  // --- Setup ------------------------------------------------------

  @BeforeClass
  public static void setUpStatic() {
    infrastructure = HandlerTestUtil.setupInfrastructure(CONTENT_REPOSITORY_URL);
  }

  @Before
  public void setup() {
    contextBean = getContentBean(infrastructure, 6);
    abstractCodeBean = getContentBean(infrastructure, 40);

    testling = infrastructure.getBean("codeResourceHandler", CodeResourceHandler.class);
    // Reset to default, some of these tests change this.
    testling.setLocalResourcesEnabled(false);
  }

  // === Handler Tests =============================================

  // --- Handling CMS Merged content ---

  /**
   * A request to a merged content resource url expects a CMNavigation to be added to the MAV.
   */
  @Test
  public void testHandleMergedLink() throws Exception {
    ModelAndView mav = request(infrastructure, LINK_TO_MERGED_CONTENT_RESOURCE, null);

    checkCodeResources(mav);
    checkView(mav, MERGED_JS_VIEW);
  }

  /**
   * A request to a merged content resource url expects a redirect to the CMNavigation to be added to the MAV
   * if the scriptHash in the URL does not match the current scriptHash of the CMAbstractCode contents linked in the Navigation.
   */
  @Test
  public void testHandleMergedWrongScriptHashLinkRedirect() throws Exception {
    ModelAndView mav = request(infrastructure, LINK_TO_WRONG_SCRIPTHASH_MERGED_CONTENT_RESOURCE, null);

    checkCodeResources(mav);
    checkView(mav, REDIRECT_MERGED_JS_VIEW);
  }

  // --- Handling CMS single content ---

  /**
   * A request to a merged content resource url expects a CMAbstractCode to be added to the MAV.
   */
  @Test
  public void testHandleSingleContentLink() throws Exception {
    ModelAndView mav = request(infrastructure, LINK_TO_SINGLE_RESOURCE);
    checkContentResourceModel(mav);
  }

  /**
   * A request to a WRONG merged content resource url expects a 404 not found.
   */
  @Test
  public void testHandleWrongSingleContentLinks() throws Exception {
    ModelAndView mav1 = request(infrastructure, LINK_TO_WRONG_SINGLE_CONTENT_RESOURCE_1);
    checkError(mav1, SC_NOT_FOUND);

    ModelAndView mav2 = request(infrastructure, LINK_TO_WRONG_SINGLE_CONTENT_RESOURCE_2);
    checkError(mav2, SC_NOT_FOUND);
  }

  /**
   * A request to a merged content resource url with an old version expects a redirect.
   */
  @Test
  public void testHandleSingleContentLinkWithOldVersion() throws Exception {
    ModelAndView mav = request(infrastructure, LINK_TO_SINGLE_CONTENT_RESOURCE_OLD_VERSION);
    checkView(mav, REDIRECT_DEFAULT_VIEW);
  }

  // --- Handling content from the application context ---

  /**
   * A request to a local resource expects a MAV containing a Blob.
   */
  @Test
  public void testHandleLocalResourcesLink() throws Exception {
    testling.setLocalResourcesEnabled(true);
    ModelAndView mav = request(infrastructure, LINK_TO_SINGLE_RESOURCE);
    checkLocalResourceModel(mav);
  }

  /**
   * A request to a WRONG local resource link expects a 404.
   */
  @Test
  public void testHandleWrongLocalResourcesLink() throws Exception {
    ModelAndView mav = request(infrastructure, LINK_TO_WRONG_LOCAL_RESOURCE);
    checkError(mav, SC_NOT_FOUND);
  }

  /**
   * A request to a non-existing local resource serves a content resource
   * as fallback.
   */
  @Test
  public void testFallbackToContentResource() throws Exception {
    testling.setLocalResourcesEnabled(true);
    ModelAndView mav = request(infrastructure, LINK_TO_NOT_LOCAL_RESOURCE);
    checkContentResourceModel(mav);
  }


  // === LinkScheme Tests =============================================

  @Test
  public void testLinkForMergedContent() {
    Map<String,Object> cmParams = new ImmutableMap.Builder<String,Object>().put("extension","js").build();
    String url = formatLink(infrastructure, cmParams, new CodeResourcesImpl(contextBean, CMNavigationBase.JAVA_SCRIPT, false), null, MERGED_JS_VIEW);
    assertEquals("urls do not match", LINK_TO_MERGED_CONTENT_RESOURCE , url);
  }

  @Test
  public void testLinkForManagedResources() {
    String url = formatLink(infrastructure, abstractCodeBean);
    assertEquals("urls do not match", LINK_TO_SINGLE_RESOURCE , url);
  }

  @Test
  public void testLinkForLocalResources() {
    //we need local resources enabled here.
    testling.setLocalResourcesEnabled(true);
    String url = formatLink(infrastructure, abstractCodeBean);
    assertEquals("urls do not match", LINK_TO_SINGLE_RESOURCE , url);
  }

  @Test
  public void testLinkForDeletedLocalResources() {

    //we need local resources enabled here.
    testling.setLocalResourcesEnabled(true);

    String url = formatLink(infrastructure, getContentBean(infrastructure, 50));

    assertEquals("urls do not match", LINK_TO_DELETED_LOCAL_RESOURCE, url);
  }


  // === internal ======================================================================================================

  private void checkContentResourceModel(ModelAndView mav) {
    checkModelAndView(mav, MARKUP_PROGRAMMED_VIEW_NAME, Markup.class);
  }

  private void checkLocalResourceModel(ModelAndView mav) throws IOException {
    checkModelAndView(mav, null, Blob.class);

    Blob blob = (Blob) HandlerHelper.getRootModel(mav);

    MimeType mimetype = blob.getContentType();
    String expectedMimetype = "text/javascript";
    assertEquals("mimetype does not match", expectedMimetype, mimetype.toString());

    String expected = "//this file is for testing purposes";
    String actual = IOUtils.toString(blob.getInputStream(), "UTF-8");
    assertEquals("file content differs", expected, actual);
  }

  /**
   * Check if the model represents the expected Navigation.
   */
  public static void checkCodeResources(ModelAndView mav) {
    Object self = HandlerHelper.getRootModel(mav);
    assertNotNull("null self", self);
    assertTrue("not a CodeResources", self instanceof CodeResources);
  }
}
