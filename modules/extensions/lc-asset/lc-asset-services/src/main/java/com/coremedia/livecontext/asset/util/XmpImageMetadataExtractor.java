package com.coremedia.livecontext.asset.util;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.coremedia.common.util.Predicate;
import com.coremedia.util.StringUtil;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Extracts product codes of all products shown on the photo as XMP/IPTC "artwork or object in the picture".
 */
public class XmpImageMetadataExtractor {
  private static final Logger LOG = LoggerFactory.getLogger(XmpImageMetadataExtractor.class);
  private static final String IPTC_XMP_EXT_NS = "http://iptc.org/std/Iptc4xmpExt/2008-02-29/";
  private static final String ARTWORK_NODE = "ArtworkOrObject";
  private static final String INVENTORY_INFO = "Iptc4xmpExt:AOSourceInvNo";


  public static Collection<String> extractInventoryInfo(InputStream inputStream) {
    if (inputStream == null) {
      return Collections.emptyList();
    }

    Collection<String> externalIds = new ArrayList<>();
    Metadata metadata = null;
    try {
      metadata = ImageMetadataReader.readMetadata(new BufferedInputStream(inputStream), true);
    } catch (Exception e) {
      LOG.warn("Error processing image data" + e.getMessage());
    }
    if (metadata != null) {
      com.coremedia.image.XmpImageMetadataExtractor extractor = com.coremedia.image.XmpImageMetadataExtractor.builder().atNameSpace(IPTC_XMP_EXT_NS).atProperty(ARTWORK_NODE).filteredBy(new Predicate<XMPPropertyInfo>() {
        @Override
        public boolean include(XMPPropertyInfo o) {
          return !StringUtil.isEmpty(o.getValue()) && o.getPath().endsWith(INVENTORY_INFO);
        }
      }).build();
      externalIds = extractor.apply(metadata).values();
    }
    return new HashSet<>(externalIds);
  }
}
