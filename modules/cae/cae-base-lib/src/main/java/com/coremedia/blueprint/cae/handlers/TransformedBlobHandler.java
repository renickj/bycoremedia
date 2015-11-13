package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.BlobHelper;
import com.coremedia.blueprint.cae.util.SecureHashCodeGeneratorStrategy;
import com.coremedia.blueprint.common.contentbeans.CMMedia;
import com.coremedia.cap.common.Blob;
import com.coremedia.cap.common.CapBlobRef;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.coremedia.transform.BlobTransformer;
import com.coremedia.transform.TransformedBeanBlob;
import com.coremedia.transform.TransformedBlob;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.activation.MimeType;
import java.io.IOException;
import java.util.Map;

import static com.coremedia.blueprint.base.cae.web.taglib.ImageFunctions.createScaleAndRemoveMetadataTransformationString;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_NUMBER;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_RESOURCE;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ID;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_NAME;

/**
 * Controller and LinkScheme for transformed blobs
 *
 * @see com.coremedia.transform.TransformedBeanBlob
 */
@Link
@RequestMapping
public class TransformedBlobHandler extends HandlerBase {

  private static final Logger LOG = LoggerFactory.getLogger(TransformedBlobHandler.class);

  private static final String URI_PREFIX = "image";
  public static final String TRANSFORMATION_SEGMENT = "transformationName";
  private static final String DIGEST_SEGMENT = "digest";
  private static final String SECHASH_SEGMENT = "secHash";
  public static final String WIDTH_SEGMENT = "width";
  public static final String HEIGHT_SEGMENT = "height";

  private SecureHashCodeGeneratorStrategy secureHashCodeGeneratorStrategy;
  private BlobTransformer blobTransformer;
  private String defaultJpegQuality;

  /**
   * URI Pattern for transformed blobs.
   * e.g. /image/4302/landscape_ratio4x3/590/442/969e0a0b2eb79df86df7ffecd1375115/eg/london.jpg
   */
  public static final String URI_PATTERN =
          '/' + PREFIX_RESOURCE +
          "/" + URI_PREFIX +
                  "/{" + SEGMENT_ID + ":" + PATTERN_NUMBER + "}" +
                  "/{" + TRANSFORMATION_SEGMENT + "}" +
                  "/{" + WIDTH_SEGMENT + ":" + PATTERN_NUMBER + "}" +
                  "/{" + HEIGHT_SEGMENT + ":" + PATTERN_NUMBER + "}" +
                  "/{" + DIGEST_SEGMENT + "}" +
                  "/{" + SECHASH_SEGMENT + "}" +
                  "/{" + SEGMENT_NAME + "}" +
                  ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";

  // --- spring config -------------------------------------------------------------------------------------------------

  @Required
  public void setBlobTransformer(BlobTransformer blobTransformer) {
    this.blobTransformer = blobTransformer;
  }

  @Required
  public void setDefaultJpegQuality(String defaultJpegQuality) {
    this.defaultJpegQuality = defaultJpegQuality;
  }

  public void setSecureHashCodeGeneratorStrategy(SecureHashCodeGeneratorStrategy secureHashCodeGeneratorStrategy) {
    this.secureHashCodeGeneratorStrategy = secureHashCodeGeneratorStrategy;
  }

  // --- Handlers ------------------------------------------------------------------------------------------------------

  @RequestMapping(value = URI_PATTERN)
  public ModelAndView handleRequest(@PathVariable(SEGMENT_ID) ContentBean contentBean,
                                    @PathVariable(TRANSFORMATION_SEGMENT) String transformationName,
                                    @PathVariable(WIDTH_SEGMENT) Integer width,
                                    @PathVariable(HEIGHT_SEGMENT) Integer height,
                                    @PathVariable(DIGEST_SEGMENT) String digest,
                                    @PathVariable(SECHASH_SEGMENT) String secHash,
                                    @PathVariable(SEGMENT_NAME) String name,
                                    @PathVariable(SEGMENT_EXTENSION) String extension,
                                    WebRequest webRequest) {

    if (contentBean instanceof CMMedia) {

      CMMedia media = getDataViewFactory().loadCached((CMMedia) contentBean, null);

      // URL validation: segment must match and hash value must be correct
      String segment = removeSpecialCharacters(media.getContent().getName());
      if (name.equals(segment)) {
        //name matches, make sure that secHash matches given URL
        Map<String, Object> parameters = new ImmutableMap.Builder<String, Object>()
          .put(SEGMENT_ID, ((CMMedia) contentBean).getContentId())
          .put(TRANSFORMATION_SEGMENT, transformationName)
          .put(WIDTH_SEGMENT, width)
          .put(HEIGHT_SEGMENT, height)
          .put(DIGEST_SEGMENT, digest)
          .put(SEGMENT_NAME, name)
          .put(SEGMENT_EXTENSION, extension).build();

        if (secureHashCodeGeneratorStrategy.matches(parameters, secHash)) {

          //request is valid, resolve blob and return model
          Blob transformedBlob = getTransformedBlob(media, transformationName, extension, width, height);

          if (transformedBlob != null) {
            if (webRequest.checkNotModified(transformedBlob.getETag())) {
              // shortcut exit - no further processing necessary
              return null;
            }
            return HandlerHelper.createModel(transformedBlob);
          }
        }
      }
    }

    return HandlerHelper.notFound();
  }

  // --- LinkSchemes ---------------------------------------------------------------------------------------------------

  @Link(type = TransformedBeanBlob.class, parameter = {HEIGHT_SEGMENT, WIDTH_SEGMENT}, uri = URI_PATTERN)
  public Map<String, ?> buildLink(TransformedBeanBlob bean, Map<String, String> linkParameters) {

    if (!(bean.getBean() instanceof CMMedia)) {
      return null;
    }

    CapBlobRef original = (CapBlobRef) bean.getOriginal();
    int contentId = IdHelper.parseContentId(original.getCapObject().getId());

    int height = Integer.valueOf(linkParameters.get(HEIGHT_SEGMENT));
    int width = Integer.valueOf(linkParameters.get(WIDTH_SEGMENT));

    /**
     * create parameters map. This is more flexible than calling URI_TEMPLATE#expand with the parameters
     * since this way the parameter's sequence is not relevant and the URI_PATTERN can be changed easier
     */
    // Use content type of original blob, not of the transformed blob which may be different.
    // Requesting the transformed blob's content type forces the transformation to be performed, which is too
    // costly for link generation.
    MimeType contentType = original.getContentType();
    Map<String, Object> parameters = new ImmutableMap.Builder<String, Object>()
            .put(SEGMENT_ID, contentId)
            .put(TRANSFORMATION_SEGMENT, bean.getTransformName())
            .put(WIDTH_SEGMENT, width)
            .put(HEIGHT_SEGMENT, height)
            .put(DIGEST_SEGMENT, bean.getETag())
            .put(SEGMENT_NAME, getName(original))
            .put(SEGMENT_EXTENSION, getExtension(contentType, BlobHelper.BLOB_DEFAULT_EXTENSION)).build();

    //generate secure hash from all parameters and add to map
    String secHash = secureHashCodeGeneratorStrategy.generateSecureHashCode(parameters);

    return new ImmutableMap.Builder<String, Object>().putAll(parameters).put(SECHASH_SEGMENT, secHash).build();
  }

  /**
   * Return the transformed blob contained in the given CMMedia object, including all additional delivery transformations.
   * The returned blob may then be used to render information of the transformed blob in addition to the link, such as
   * the download size.
   *
   * <p>Note that accessing any of the methods {@link com.coremedia.cap.common.Blob#getContentType()}
   * or {@link com.coremedia.cap.common.Blob#getSize()} will trigger the transformation of the blob. While the
   * transformation result is cached, this is generally much more costly than merely generating a link to the
   * transformed blob, esp. if transformed blobs are cached by a CDN.
   */
  public Blob getTransformedBlob(CMMedia media, String transformName, String extension, Integer width, Integer height) {

    TransformedBlob transformedBlob = (TransformedBlob) media.getTransformedData(transformName);
    if (transformedBlob == null) {
      LOG.info("Requested transformation '{}' of {} but no such transformation exists.",transformName,media);
      return null;
    }

    // Validate against extension of original blob. This may be different from the actual content type of the
    // transformed blob, but the link generator appends the original extension for performance reasons.
    if (!BlobHelper.isValidExtension(extension, transformedBlob.getOriginal(), getMimeTypeService())) {
      return null;
    }

    // Scale the image to the desired dimension and be sure to remove all image metadata before delivery
    final String deliveryTransformation = createScaleAndRemoveMetadataTransformationString(width, height);

    Blob data = (Blob) media.getData();

    try {
      return blobTransformer.transformBlob(data, combineTransform(transformedBlob.getTransform(), deliveryTransformation));
    } catch (IOException e) {
      LOG.info("Error transforming blob.", e);
      return null;
    }
  }

  // === internal ======================================================================================================

  private String getName(CapBlobRef o) {
    if (BlobHelper.hasContentContainer(o)) {
      String contentName = ((Content) o.getCapObject()).getName();
      return removeSpecialCharacters(contentName);
    }
    return null;
  }

  /**
   * Combines two transformation strings into one, and
   * sets the default JPEG image compression quality if one is configured.
   */
  private String combineTransform(String t1, String t2) {
    StringBuilder b = new StringBuilder();
    b.append(t1);
    if (b.length() > 0) {
      b.append('/');
    }
    b.append(t2);
    if (defaultJpegQuality != null && defaultJpegQuality.length() > 0) {
      if (b.length() > 0) {
        b.append('/');
      }
      b.append("djq;q=").append(defaultJpegQuality);
    }
    return b.toString();
  }
}
