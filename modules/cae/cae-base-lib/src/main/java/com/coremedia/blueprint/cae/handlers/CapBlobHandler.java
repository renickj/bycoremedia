package com.coremedia.blueprint.cae.handlers;

import com.coremedia.blueprint.base.links.BlobHelper;
import com.coremedia.blueprint.common.contentbeans.CMDownload;
import com.coremedia.cap.common.CapBlobRef;
import com.coremedia.cap.common.CapPropertyDescriptor;
import com.coremedia.cap.common.CapPropertyDescriptorType;
import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.objectserver.beans.ContentBean;
import com.coremedia.objectserver.web.links.Link;
import com.google.common.collect.ImmutableMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriTemplate;

import java.util.Map;
import java.util.Objects;

import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_NUMBER;
import static com.coremedia.blueprint.base.links.UriConstants.Patterns.PATTERN_WORD;
import static com.coremedia.blueprint.base.links.UriConstants.Prefixes.PREFIX_RESOURCE;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ETAG;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_EXTENSION;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_ID;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_NAME;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.SEGMENT_PROPERTY;
import static com.coremedia.objectserver.web.HandlerHelper.createModel;
import static com.coremedia.objectserver.web.HandlerHelper.notFound;
import static com.coremedia.objectserver.web.HandlerHelper.redirectTo;

/**
 * Controller and LinkScheme for
 * {@link com.coremedia.cap.common.CapBlobRef blobs}
 */
@Link
@RequestMapping
public class CapBlobHandler extends HandlerBase {

  private static final String URI_PREFIX = "blob";
  private static final String EMPTY_ETAG = "-";

  //e.g. /resource/blob/126/4fb7741a1080d02953ac7d79c76c955c/media-favicon.ico
  public static final String URI_PATTERN =
                  '/' + PREFIX_RESOURCE +
                  "/" + URI_PREFIX +
                  "/{" + SEGMENT_ID + ":" + PATTERN_NUMBER + "}" +
                  "/{" + SEGMENT_ETAG + "}" +
                  "/{" + SEGMENT_NAME + "}" +
                  "-{" + SEGMENT_PROPERTY + ":" + PATTERN_WORD + "}" +
                  ".{" + SEGMENT_EXTENSION + ":" + PATTERN_EXTENSION + "}";


  // --- Handlers ------------------------------------------------------------------------------------------------------

  @RequestMapping(value = URI_PATTERN)
  public ModelAndView handleRequest(@PathVariable(SEGMENT_ID) ContentBean contentBean,
                                    @PathVariable(SEGMENT_ETAG) String eTag,
                                    @PathVariable(SEGMENT_PROPERTY) String propertyName,
                                    @PathVariable(SEGMENT_EXTENSION) String extension,
                                    WebRequest webRequest) {
    if (contentBean == null) {
      return notFound();
    }

    Content content = contentBean.getContent();
    CapPropertyDescriptor propertyDescriptor = content.getType().getDescriptor(propertyName);
    if (propertyDescriptor == null || !Objects.equals(propertyDescriptor.getType(), CapPropertyDescriptorType.BLOB)) {
      return notFound();
    }

    CapBlobRef blob = contentBean.getContent().getBlobRef(propertyName);
    if (blob == null) {
      return notFound();
    }

    // URL validation: extension must be valid for this blob
    if (BlobHelper.isValidExtension(extension, blob, getMimeTypeService())) {

      // URL validation: redirect to "correct" blob URL, if etag does not match.
      // The client may just have an old version of the URL.
      if (eTagMatches(blob, eTag)) {
        if (webRequest.checkNotModified(blob.getETag())) {
          // shortcut exit - no further processing necessary
          return null;
        }
        return createModel(blob);
      } else {
        return redirectTo(blob);
      }
    }

    return notFound();
  }

  // --- LinkSchemes ---------------------------------------------------------------------------------------------------

  @Link(type = CapBlobRef.class, uri = URI_PATTERN)
  public Map<String, ?> buildLink(CapBlobRef bean) {

    String id = String.valueOf(IdHelper.parseContentId(bean.getCapObject().getId()));
    String etag = bean.getETag();

    /**
     * create parameters map. This is more flexible than calling URI_TEMPLATE#expand with the parameters
     * since this way the parameter's sequence is not relevant and the URI_PATTERN can be changed easier
     */

    return new ImmutableMap.Builder<String, Object>()
            .put(SEGMENT_ID, id)
            .put(SEGMENT_ETAG, etag != null ? etag : EMPTY_ETAG)
            .put(SEGMENT_NAME, getName(bean))
            .put(SEGMENT_PROPERTY, bean.getPropertyName())
            .put(SEGMENT_EXTENSION, getExtension(bean.getContentType(), BlobHelper.BLOB_DEFAULT_EXTENSION)).build();
  }

  // === internal ======================================================================================================

  private boolean eTagMatches(CapBlobRef blob, String eTag) {
    String blobETag = blob.getETag();
    return (blobETag != null ? blobETag.equals(eTag) : EMPTY_ETAG.equals(eTag));
  }

  private String getName(CapBlobRef o) {
    if (BlobHelper.hasContentContainer(o)) {
      String contentName = ((Content) o.getCapObject()).getName();
      return removeSpecialCharacters(contentName);
    }
    return null;
  }

  @Link(type = CMDownload.class, uri = URI_PATTERN)
  @SuppressWarnings("unused")
  public String buildLinkForDownload(
         CMDownload download) {
    CapBlobRef blob = (CapBlobRef) download.getData();
    return blob != null ? new UriTemplate(URI_PATTERN).expand(buildLink(blob)).toString() : "#";
  }


}
