package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.cae.util.DefaultSecureHashCodeGeneratorStrategy;
import com.coremedia.blueprint.common.contentbeans.CMMedia;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapBlobRef;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.Version;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.transform.BlobTransformer;
import com.coremedia.transform.TransformedBeanBlob;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests {@link TransformedBlobHandler}
 */
public class TransformedBlobHandlerTest extends HandlerBaseTest {

  private static final String URI = "/resource/image/1234/transformName/100/100/digest/So/london.jpg";
  public static final int CONTENT_ID = 1234;

  final String propertyName = "propertyName";

  private TransformedBeanBlob transformedBlob;
  private CMMedia cmMedia;
  private DataViewFactory dataViewFactory;
  private BlobTransformer blobTransformer;
  private Content content;

  //----
  private static final String DIGEST = "digest";
  private static final String TRANSFORM_NAME = "transformName";
  private static final String WIDTH = "100";
  private static final String HEIGHT = "100";
  private static final String NAME = "london";
  private static final String EXTENSION = "jpg";

  @Override
  public void setUp() throws Exception {

    // 1. --- set up testling.
    String mimeType = "image/jpeg";
    registerMimeTypeWithExtensions(mimeType, EXTENSION);

    when(getUrlPathFormattingHelper().tidyUrlPath("London")).thenReturn(NAME);

    dataViewFactory = mock(DataViewFactory.class);
    blobTransformer = mock(BlobTransformer.class);
    DefaultSecureHashCodeGeneratorStrategy secureHashCodeGeneratorStrategy = new DefaultSecureHashCodeGeneratorStrategy();
    secureHashCodeGeneratorStrategy.setEncoding("UTF-8");

    TransformedBlobHandler testling = new TransformedBlobHandler();
    testling.setMimeTypeService(getMimeTypeService());
    testling.setUrlPathFormattingHelper(getUrlPathFormattingHelper());
    testling.setSecureHashCodeGeneratorStrategy(secureHashCodeGeneratorStrategy);
    testling.setDataViewFactory(dataViewFactory);
    testling.setBlobTransformer(blobTransformer);

    registerHandler(testling);

    // 2. --- mock content
    Version version = mock(Version.class);
    when(version.getId()).thenReturn("coremedia:///cap/version/1234/567");

    CapBlobRef capBlobRef = mock(CapBlobRef.class);

    content = mock(Content.class);
    when(content.getName()).thenReturn("London");
    when(content.getId()).thenReturn("coremedia:///cap/content/1234");
    when(content.isCheckedIn()).thenReturn(true);
    when(content.getCheckedInVersion()).thenReturn(version);
    when(content.isContent()).thenReturn(true);
    when(content.isContentObject()).thenReturn(true);
    when(content.getBlobRef(propertyName)).thenReturn(capBlobRef);

    when(capBlobRef.getCapObject()).thenReturn(content);
    when(capBlobRef.getContentType()).thenReturn(new MimeType(mimeType));
    when(capBlobRef.getPropertyName()).thenReturn(propertyName);

    transformedBlob = mock(TransformedBeanBlob.class);
    when(transformedBlob.getOriginal()).thenReturn(capBlobRef);
    when(transformedBlob.getContentType()).thenReturn(new MimeType(mimeType));
    when(transformedBlob.getETag()).thenReturn(DIGEST);
    when(transformedBlob.getTransformName()).thenReturn(TRANSFORM_NAME);
    when(transformedBlob.getTransform()).thenReturn(TRANSFORM_NAME);

    // 3. --- mock content(-bean) related stuff
    cmMedia = mock(CMMedia.class);
    when(cmMedia.getContent()).thenReturn(content);
    when(cmMedia.getContentId()).thenReturn(CONTENT_ID);
    when(cmMedia.getTransformedData(TRANSFORM_NAME)).thenReturn(transformedBlob);
    when(transformedBlob.getBean()).thenReturn(cmMedia);
    when(blobTransformer.transformBlob(any(Blob.class), any(String.class))).thenReturn(transformedBlob);

    when(getIdContentBeanConverter().convert(String.valueOf(CONTENT_ID))).thenReturn(cmMedia);

    ContentRepository contentRepository = mock(ContentRepository.class);
    when(content.getRepository()).thenReturn(contentRepository);
    when(contentRepository.isContentManagementServer()).thenReturn(false);
  }

  /**
   * Test bean resolution and pattern matching:
   * {@link TransformedBlobHandler#handleRequest}
   */
  @Test
  public void testBean() throws Exception {

    when(dataViewFactory.loadCached(cmMedia, null)).thenReturn(cmMedia);

    assertModel(handleRequest(URI), transformedBlob);
  }

  @Test
  public void acceptUrlWithOriginalExtensionInsteadOfTransformedExtension() throws Exception {
    // in this test case, the original JPEG is transformed to a PNG
    String transformedMimeType = "image/png";
    registerMimeTypeWithExtensions(transformedMimeType, "png");

    when(transformedBlob.getContentType()).thenReturn(new MimeType(transformedMimeType));
    when(dataViewFactory.loadCached(cmMedia, null)).thenReturn(cmMedia);

    assertModel(handleRequest(URI), transformedBlob);
  }

  /**
   * Test bean resolution with a Japanese segment name (BARBUDA-2637).
   * {@link TransformedBlobHandler#handleRequest}
   */
  @Test
  public void testJapaneseSegmentName() throws Exception {
    // Java literals use UTF-16 code points (requiring 2 bytes per character), whereas in the URL,
    // the segment will be encoded in UTF-8, requiring three bytes per character.
    // The UTF-8, URL encoded segment equivalent to these four characters, is "%E8%A9%A6%E9%A8%93%E7%94%BB%E5%83%8F".
    String japaneseName = "\u8A66\u9A13\u753B\u50CF";
    String urlEncodedName = "%E8%A9%A6%E9%A8%93%E7%94%BB%E5%83%8F";

    String urlWithJapaneseSegment = "/resource/image/1234/transformName/100/100/digest/aW/" + urlEncodedName + ".jpg";

    // for now, assume that the path formatting helper does not mangle any of the Japanese characters
    when(getUrlPathFormattingHelper().tidyUrlPath(japaneseName)).thenReturn(japaneseName);
    when(content.getName()).thenReturn(japaneseName);

    when(dataViewFactory.loadCached(cmMedia, null)).thenReturn(cmMedia);

    ModelAndView modelAndView = handleRequest(urlWithJapaneseSegment);
    assertModel(modelAndView, transformedBlob);
  }

  /**
   * Test "not found" when one of the hash-protected URL segments is modified:
   * {@link TransformedBlobHandler#handleRequest}
   */
  @Test
  public void testMessingWithProtectedURLParts() throws Exception {
    when(dataViewFactory.loadCached(cmMedia, null)).thenReturn(cmMedia);
    when(cmMedia.getTransformedData("transformXXX")).thenReturn(transformedBlob);
    when(getIdContentBeanConverter().convert("666")).thenReturn(cmMedia);
    when(cmMedia.getContentId()).thenReturn(666);

    assertNotFound("extension", handleRequest("/resource/image/1234/transformName/100/100/digest/So/london.png"));
    assertNotFound("name", handleRequest("/resource/image/1234/transformName/100/100/digest/So/london-broken.jpg"));
    assertNotFound("digest", handleRequest("/resource/image/1234/transformName/100/100/xxxxx/So/london.jpg"));
    assertNotFound("width", handleRequest("/resource/image/1234/transformName/101/100/digest/So/london.jpg"));
    assertNotFound("height", handleRequest("/resource/image/1234/transformName/100/101/digest/So/london.jpg"));
    assertNotFound("transform", handleRequest("/resource/image/1234/transformXXX/100/100/digest/So/london.jpg"));
    assertNotFound("id", handleRequest("/resource/image/666/transformName/100/100/digest/So/london.jpg"));
    assertNotFound("hash", handleRequest("/resource/image/1234/transformName/100/100/digest/XXX/london.jpg"));

    verify(cmMedia, never()).getTransformedData(anyString());
    verify(blobTransformer, never()).transformBlob(any(Blob.class), anyString());
  }

  /**
   * Test URL generation:
   * {@link TransformedBlobHandler#buildLink(com.coremedia.transform.TransformedBeanBlob, java.util.Map)}
   */
  @Test
  public void testGenerateLink() {
    assertEquals(URI, formatLink(transformedBlob, null, false, ImmutableMap.<String, Object>of(
      TransformedBlobHandler.WIDTH_SEGMENT, WIDTH,
      TransformedBlobHandler.HEIGHT_SEGMENT, HEIGHT
    )));
  }

  /**
   * BARBUDA-2590: to fix this bug, the link generator will use the original extension instead of the
   * correct extension of the transformed blob, because generating the link should not actually require the tranformation
   * to be executed.
   */
  @Test
  public void useOriginalExtensionWhenTransformationChangesExtension() throws MimeTypeParseException {

    // in this test case, the original JPEG is transformed to a PNG
    String transformedMimeType = "image/png";
    registerMimeTypeWithExtensions(transformedMimeType, "png");

    when(transformedBlob.getContentType()).thenReturn(new MimeType(transformedMimeType));

    // expect extension .jpg, even though the transformed blob has a different extension
    assertEquals(URI, formatLink(transformedBlob, null, false, ImmutableMap.<String, Object>of(
      TransformedBlobHandler.WIDTH_SEGMENT, WIDTH,
      TransformedBlobHandler.HEIGHT_SEGMENT, HEIGHT
    )));
  }

  /**
   * BARBUDA-2590: generating a link for a transformed blob must not access its size, contents, or content type,
   * as this would trigger the costly transformation to be performed. Link generation must be inexpensive.
   */
  @Test
  public void linkGenerationDoesNotTriggerTransformation() throws MimeTypeParseException, IOException {

    // generate the link
    assertEquals(URI, formatLink(transformedBlob, null, false, ImmutableMap.<String, Object>of(
      TransformedBlobHandler.WIDTH_SEGMENT, WIDTH,
      TransformedBlobHandler.HEIGHT_SEGMENT, HEIGHT
    )));

    // make sure the "expensive" methods triggering the transformation are never called
    verify(transformedBlob, never()).asBytes();
    verify(transformedBlob, never()).getInputStream();
    verify(transformedBlob, never()).writeOn(any(OutputStream.class));
    verify(transformedBlob, never()).getContentType();
    verify(transformedBlob, never()).getSize();
  }
}
