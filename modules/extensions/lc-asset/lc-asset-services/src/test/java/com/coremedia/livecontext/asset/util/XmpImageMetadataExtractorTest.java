package com.coremedia.livecontext.asset.util;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

public class XmpImageMetadataExtractorTest extends TestCase {

  public void testExtractInventoryInfoWithXmp() throws Exception {
    File imageWithXmpData = new File(XmpImageMetadataExtractorTest.class.getResource("image-with-xmp-product-reference.jpg").toURI());
    Collection<String> externalIds = XmpImageMetadataExtractor.extractInventoryInfo(new FileInputStream(imageWithXmpData));
    assertNotNull(externalIds);
    assertTrue(externalIds.size() == 2);
  }

  public void testExtractInventoryInfoNoData() throws Exception {
    File imageNoXmp = new File(XmpImageMetadataExtractorTest.class.getResource("image-no-xmp.jpg").toURI());
    Collection<String> externalIds = XmpImageMetadataExtractor.extractInventoryInfo(new FileInputStream(imageNoXmp));
    assertNotNull(externalIds);
    assertTrue(externalIds.size() == 0);
  }

  public void testExtractInventoryInfoWrongFormat() throws Exception {
    File noImage = new File(XmpImageMetadataExtractorTest.class.getResource("no-pic.jpg").toURI());
    Collection<String> externalIds = XmpImageMetadataExtractor.extractInventoryInfo(new FileInputStream(noImage));
    assertNotNull(externalIds);
    assertTrue(externalIds.size() == 0);
  }
}
