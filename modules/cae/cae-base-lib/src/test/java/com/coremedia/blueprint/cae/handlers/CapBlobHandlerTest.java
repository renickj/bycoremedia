package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.common.contentbeans.CMDownload;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.cap.common.CapBlobRef;
import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.common.CapPropertyDescriptorType;
import com.coremedia.cap.common.InvalidPropertyValueException;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentType;
import com.coremedia.objectserver.web.HttpError;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.activation.MimeType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link CapBlobHandler}
 */
public class CapBlobHandlerTest extends HandlerBaseTest {

  private static final String URI_JPG = "/resource/blob/1234/567/nae-me-jpg-propertyName.jpg";
  private static final String URI_ANY = "/resource/blob/1234/567/nae-me-jpg-propertyName.any";
  private static final String URI_RAW = "/resource/blob/1234/567/nae-me-jpg-propertyName.raw";
  private static final String URI_PNG = "/resource/blob/1234/567/nae-me-jpg-propertyName.png";
  private static final String URI_PDF = "/resource/blob/1236/569/a-pdf-pdf-propertyName.pdf";
  private static final String CONTENT_ID = "1234";
  private static final String ETAG = "567";

  private static final String propertyName = "propertyName";

  private CapBlobRef capBlobRef, capBlobRefPdf;
  private CMObject cmObject;
  private CMDownload cmDownload, cmDownloadNullBlob;
  private Content content;
  private ContentType contentType;

  @Override
  public void setUp() throws Exception {
    super.setUp();

    // 1. --- set up handler.
    String mimeType = "image/jpeg";
    String mimeTypePdf = "application/pdf";
    registerMimeTypeWithExtensions(mimeType, "jpg");
    registerMimeTypeWithExtensions(mimeTypePdf, "pdf");
    when(getUrlPathFormattingHelper().tidyUrlPath("nä me.jpg")).thenReturn("nae-me-jpg");
    when(getUrlPathFormattingHelper().tidyUrlPath("a-pdf.pdf")).thenReturn("a-pdf-pdf");

    CapBlobHandler testling = new CapBlobHandler();
    testling.setMimeTypeService(getMimeTypeService());
    testling.setUrlPathFormattingHelper(getUrlPathFormattingHelper());

    registerHandler(testling);

    // 2. --- mock content
    capBlobRef = mock(CapBlobRef.class);
    capBlobRefPdf = mock(CapBlobRef.class);

    content = mock(Content.class);
    when(content.getName()).thenReturn("nä me.jpg");
    when(content.getId()).thenReturn("coremedia:///cap/content/1234");
    when(content.isContent()).thenReturn(true);
    when(content.isContentObject()).thenReturn(true);
    when(content.getBlobRef(propertyName)).thenReturn(capBlobRef);

    contentType = mock(ContentType.class);
    CapPropertyDescriptor propertyDescriptor = mock(CapPropertyDescriptor.class);
    when(content.getType()).thenReturn(contentType);
    when(contentType.getDescriptor(propertyName)).thenReturn(propertyDescriptor);
    when(propertyDescriptor.getType()).thenReturn(CapPropertyDescriptorType.BLOB);

    // 3. --- mock content(-bean) related stuff
    cmObject = mock(CMObject.class);
    when(cmObject.getContent()).thenReturn(content);

    when(capBlobRef.getCapObject()).thenReturn(content);
    when(capBlobRef.getContentType()).thenReturn(new MimeType(mimeType));
    when(capBlobRef.getPropertyName()).thenReturn(propertyName);
    when(capBlobRef.getETag()).thenReturn("567");

    // 4. --- mock CMDownload content
    Content contentPdf = mock(Content.class);
    when(contentPdf.getName()).thenReturn("a-pdf.pdf");
    when(contentPdf.getId()).thenReturn("coremedia:///cap/content/1236");
    when(contentPdf.isContent()).thenReturn(true);
    when(contentPdf.isContentObject()).thenReturn(true);
    when(contentPdf.getBlobRef(propertyName)).thenReturn(capBlobRefPdf);

    when(contentPdf.getType()).thenReturn(contentType);

    // 5. --- mock CMDownload object
    cmDownload = mock(CMDownload.class);
    when(cmDownload.getContent()).thenReturn(contentPdf);
    when(cmDownload.getData()).thenReturn(capBlobRefPdf);
    when(capBlobRefPdf.getCapObject()).thenReturn(contentPdf);
    when(capBlobRefPdf.getContentType()).thenReturn(new MimeType(mimeTypePdf));
    when(capBlobRefPdf.getPropertyName()).thenReturn(propertyName);
    when(capBlobRefPdf.getETag()).thenReturn("569");

    cmDownloadNullBlob = mock(CMDownload.class);
    when(cmDownloadNullBlob.getContent()).thenReturn(mock(Content.class));
    when(cmDownloadNullBlob.getData()).thenReturn(null);

  }


  /**
   * Tests link generation
   */
  @Test
  public void testLink() throws Exception {
    assertEquals("uri", URI_JPG, formatLink(capBlobRef, null, false));
  }

  /**
   * Tests link generation for CMDownload
   */
  @Test
  public void testLinkCMDonwload() throws Exception {
    assertEquals("uri", URI_PDF, formatLink(cmDownload, null, false));
  }

  /**
   * Tests link generation for CMDownload w/o blob
   */
  @Test
  public void testLinkCMDonwloadNoBlob() throws Exception {
    assertEquals("#", formatLink(cmDownloadNullBlob, null, false));
  }

  /**
   * Tests link for null ETag.
   */
  @Test
  public void testLinkWithNullETag() throws Exception {
    when(capBlobRef.getETag()).thenReturn(null);

    String expectedUrl = URI_JPG.replace(ETAG, "-");
    assertEquals("uri", expectedUrl, formatLink(capBlobRef, null, false));
  }

  /**
   * Test bean resolution and pattern matching
   */
  @Test
  public void testHandleBlobUrl() throws Exception {
    when(getIdContentBeanConverter().convert(CONTENT_ID)).thenReturn(cmObject);

    assertModel(handleRequest(URI_JPG), capBlobRef);
    Assert.assertTrue(handleRequest(URI_ANY).getModelMap().get("self") instanceof HttpError);
    assertModel(handleRequest(URI_RAW), capBlobRef);
  }

  /**
   * Test bean resolution and pattern matching for a URL including Japanese characters.
   */
  @Test
  public void testHandleBlobUrlWithJapaneseCharacters() throws Exception {
    when(getIdContentBeanConverter().convert(CONTENT_ID)).thenReturn(cmObject);

    // Java literals use UTF-16 code points (requiring 2 bytes per character), whereas in the URL,
    // the segment will be encoded in UTF-8, requiring three bytes per character.
    // The UTF-8, URL encoded segment equivalent to these four characters, is "%E8%A9%A6%E9%A8%93%E7%94%BB%E5%83%8F".
    String japaneseName = "\u8A66\u9A13\u753B\u50CF.jpg";
    String url = "/resource/blob/1234/567/" + japaneseName + "-jpg-propertyName.jpg";

    when(content.getName()).thenReturn(japaneseName);

    assertModel(handleRequest(url), capBlobRef);
  }

  /**
   * Test accepting "-" for a null ETag.
   */
  @Test
  public void testHandleBlobUrlWithNullETag() throws Exception {
    when(getIdContentBeanConverter().convert(CONTENT_ID)).thenReturn(cmObject);
    when(capBlobRef.getETag()).thenReturn(null);

    String requestUrl = URI_JPG.replace(ETAG, "-");
    assertModel(handleRequest(requestUrl), capBlobRef);
  }

  /**
   * Return a "not found" object, if bean is null.
   */
  @Test
  public void testNotFoundIfBeanIsNull() throws Exception {
    when(getIdContentBeanConverter().convert(CONTENT_ID)).thenReturn(null);
    assertNotFound("null bean", handleRequest(URI_JPG));
  }

  /**
   * Return a "not found" object, if wrong extension.
   */
  @Test
  public void testNotFoundIfWrongExtension() throws Exception {
    when(getIdContentBeanConverter().convert(CONTENT_ID)).thenReturn(cmObject);
    assertNotFound("wrong extension", handleRequest(URI_PNG));
  }

  @Test
  public void testRedirectIfWrongETag() throws Exception {
    when(getIdContentBeanConverter().convert(CONTENT_ID)).thenReturn(cmObject);
    when(capBlobRef.getETag()).thenReturn("890");
    ModelAndView mav = handleRequest(URI_JPG);
    assertModel(mav, capBlobRef);
    assertEquals("redirect:DEFAULT", mav.getViewName());
  }

  @Test
  public void testNotFoundForInvalidPropertyName() throws Exception {
    when(getIdContentBeanConverter().convert(CONTENT_ID)).thenReturn(cmObject);

    // invalid property name
    when(content.getBlobRef(propertyName)).thenThrow(new NoSuchPropertyDescriptorException(propertyName));
    when(contentType.getDescriptor(propertyName)).thenReturn(null);
    when(cmObject.getContent()).thenReturn(content);

    assertNotFound("invalid property name", handleRequest(URI_ANY));
  }

  @Test
  public void testNotFoundForInvalidPropertyValue() throws Exception {
    when(getIdContentBeanConverter().convert(CONTENT_ID)).thenReturn(cmObject);

    // accessing non-blob property
    when(content.getBlobRef(propertyName)).thenThrow(
            new InvalidPropertyValueException(null, null, null, null, null, null));
    CapPropertyDescriptor propertyDescriptor = mock(CapPropertyDescriptor.class);
    when(propertyDescriptor.getType()).thenReturn(CapPropertyDescriptorType.STRING);
    when(contentType.getDescriptor(propertyName)).thenReturn(propertyDescriptor);
    when(cmObject.getContent()).thenReturn(content);
    assertNotFound("not a blob property", handleRequest(URI_ANY));
  }

  @Test
  public void testNotFoundForNullBlobRef() throws Exception {
    when(getIdContentBeanConverter().convert(CONTENT_ID)).thenReturn(cmObject);

    // accessing non-blob property
    when(content.getBlobRef(propertyName)).thenReturn(null);
    when(cmObject.getContent()).thenReturn(content);
    assertNotFound("blob ref is null", handleRequest(URI_ANY));
  }
}
