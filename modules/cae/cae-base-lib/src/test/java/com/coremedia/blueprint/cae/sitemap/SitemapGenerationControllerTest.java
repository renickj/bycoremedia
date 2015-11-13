package com.coremedia.blueprint.cae.sitemap;

import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.cae.common.predicates.ValidContentPredicate;
import com.coremedia.cae.testing.TestInfrastructureBuilder;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.common.util.Predicate;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.links.LinkScheme;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SitemapGenerationControllerTest {

  private ContentUrlGenerator urlGenerator;
  private SitemapGenerationController testling;

  private HttpServletRequest request;

  private HttpServletResponse response;

  private SimpleServletOutputStream outputStream;

  private static TestInfrastructureBuilder.Infrastructure infrastructure = TestInfrastructureBuilder
          .create()
          .withContentBeanFactory()
          .withContentRepository("classpath:/com/coremedia/blueprint/cae/controller/testurls/testcontent.xml")
          .withDataViewFactory()
          .withIdProvider()
          .withLinkFormatter()
          .withCache()
          .withBeans("classpath:/framework/spring/blueprint-contentbeans.xml")
          .withBeans("classpath:/com/coremedia/blueprint/base/multisite/bpbase-multisite-services.xml")
          .build();

  @Before
  public void setUp() throws Exception {
    LinkFormatter linkFormatter = new LinkFormatter();
    linkFormatter.setSchemes(Arrays.asList(new GeneralPurposeLinkScheme()));

    ValidContentPredicate validContentPredicate = new ValidContentPredicate();
    validContentPredicate.setContentBeanFactory(infrastructure.getContentBeanFactory());
    List<Predicate<Content>> predicates = new ArrayList<>();
    predicates.add(validContentPredicate);

    urlGenerator = new ContentUrlGenerator();
    urlGenerator.setContentBeanFactory(infrastructure.getContentBeanFactory());
    urlGenerator.setExclusionPaths(new ArrayList<String>());
    urlGenerator.setLinkFormatter(linkFormatter);
    urlGenerator.setPredicates(predicates);

    SitemapSetup sitemapSetup = new SitemapSetup();
    sitemapSetup.setSitemapRendererFactory(new PlainSitemapRendererFactory());
    sitemapSetup.setUrlGenerators(Collections.singletonList((SitemapUrlGenerator) urlGenerator));
    SpringBasedSitemapSetupFactory setupFactory = new SpringBasedSitemapSetupFactory();
    setupFactory.setSitemapSetup(sitemapSetup);

    testling = new SitemapGenerationController();
    testling.setSiteResolver(infrastructure.getBean("siteResolver", SiteResolver.class));
    testling.setSitemapSetupFactory(setupFactory);

    // unfortunately we have to mock the objects manually
    // @RunWith for Mockito AND Spring doesn't work and we decided for Spring here to use the content repository
    request = mock(HttpServletRequest.class);
    when(request.getPathInfo()).thenReturn("/internal/theSiteSegment/sitemap-org");
    response = mock(HttpServletResponse.class);

    outputStream = new SimpleServletOutputStream();
    when(response.getOutputStream()).thenReturn(outputStream);
  }

  @Test
  public void testNoParams() throws Exception {
    when(request.getParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS)).thenReturn(null);

    testling.handleRequestInternal(request, response);

    List<String> urlList = convertToList();
    assertNotNull(urlList);
    assertEquals(7, urlList.size());
  }

  @Test
  public void testGzipParam() throws Exception {
    when(request.getParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS)).thenReturn(null);
    when(request.getParameter(SitemapRequestParams.PARAM_GZIP_COMPRESSION)).thenReturn("true");

    testling.handleRequestInternal(request, response);

    List<String> urlList = convertGzipToList();
    assertNotNull(urlList);
    assertEquals(7, urlList.size());
  }

  @Test
  public void testNoSuchSite() throws Exception {
    when(request.getPathInfo()).thenReturn("/internal/noSuchSite/sitemap-org");
    when(request.getParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS)).thenReturn(null);

    testling.handleRequestInternal(request, response);
    verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), anyString());
  }

  @Test
  public void testParamExcludeFolders() throws Exception {
    when(request.getParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS)).thenReturn("Contact");

    testling.handleRequestInternal(request, response);

    List<String> urlList = convertToList();

    assertNotNull(urlList);
    assertEquals(6, urlList.size());
  }

  @Test
  public void testParamExcludeMultipleFolders() throws Exception {
    when(request.getParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS)).thenReturn("Contact,DepthForSiteIndicator");

    testling.handleRequestInternal(request, response);

    List<String> urlList = convertToList();

    assertNotNull(urlList);
    assertEquals(5, urlList.size());
  }

  @Test
  public void testParamExclusionPaths() throws Exception {
    when(request.getParameter(SitemapRequestParams.PARAM_EXCLUDE_FOLDERS)).thenReturn(null);
    urlGenerator.setExclusionPaths(Arrays.asList("About/Contact"));

    testling.handleRequestInternal(request, response);

    List<String> urlList = convertToList();

    assertNotNull(urlList);
    assertEquals(6, urlList.size());
  }

  /**
   * Link scheme for tests. This link scheme renders links for all content beans with the pattern
   * http://www.coremedia.com/<content type>/<content id>
   */
  class GeneralPurposeLinkScheme implements LinkScheme {

    @Override
    public String formatLink(Object bean, String view, HttpServletRequest request, HttpServletResponse response, boolean forRedirect) throws URISyntaxException {
      ContentBean contentBean = (ContentBean) bean;
      StringBuilder stringBuilder = new StringBuilder("http://www.coremedia.com/");
      stringBuilder.append(contentBean.getContent().getType().getName()).append("/").append(IdHelper.parseContentId(contentBean.getContent().getId()));

      return stringBuilder.toString();
    }
  }

  /**
   * Convert output list to a list object to verify the results.
   *
   * @return A list where each entry contains one line of the print writer.
   */
  private List<String> convertToList() {
    return asList(outputStream.toString());
  }

  /**
   * Converts line separated string to an array.
   *
   * @param value A list of values, separated by linefeed.
   * @return The array of each line.
   */
  private List<String> asList(String value) {
    Scanner scanner = new Scanner(value);
    List<String> result = new ArrayList<>();

    while (scanner.hasNextLine()) {
      result.add(scanner.nextLine());
    }

    return result;
  }

  /**
   * Convert output list to a list object to verify the results.
   *
   * @return A list where each entry contains one line of the print writer.
   */
  private List<String> convertGzipToList() throws IOException {
    GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(outputStream.toByteArray()));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    for (int value = 0; value != -1; ) {
      value = gzipInputStream.read();
      if (value != -1) {
        baos.write(value);
      }
    }
    gzipInputStream.close();
    baos.close();
    return asList(new String(baos.toByteArray(), "UTF-8"));
  }

  /**
   * Delegates the mocked Servlet output stream to a byte array output stream.
   */
  class SimpleServletOutputStream extends ServletOutputStream {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    @Override
    public void write(int b) throws IOException {
      out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
      out.write(b);
    }

    public String toString() {
      return new String(out.toByteArray());
    }

    public byte[] toByteArray() {
      return out.toByteArray();
    }
  }

}
