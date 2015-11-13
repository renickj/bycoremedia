package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.rest.linking.EntityResourceLinker;
import com.coremedia.rest.linking.LinkResolver;
import com.coremedia.rest.linking.TypeBasedResourceClassFinder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BlobMetadataRestTestConfiguration.class)
public class BlobMetadataResourceTest {
  private static final String CONTENT_ID = "502";
  private static final String BLOB_PROPERTY = "data";
  private static final String CONTENT_URI = "content/" + CONTENT_ID;
  private static final String BLOB_URI = CONTENT_URI + "/properties/" + BLOB_PROPERTY;
  private ZipMetadataResolver zipMetadataResolver;

  @Autowired
  private ContentRepository contentRepository;

  @Before
  public void setUp() throws Exception {
    zipMetadataResolver = new ZipMetadataResolver();
    zipMetadataResolver.setName("archive");
    final EntityResourceLinker entityResourceLinker = new EntityResourceLinker();
    final TypeBasedResourceClassFinder resourceClassFinder = new TypeBasedResourceClassFinder();
    final Collection resourceClasses = Collections.singleton(ZipFileEntryResource.class);
    resourceClassFinder.setResourceClasses(resourceClasses);
    entityResourceLinker.setResourceClassFinder(resourceClassFinder);
    zipMetadataResolver.setEntityResourceLinker(entityResourceLinker);
    LinkResolver linkResolver = mock(LinkResolver.class);
    when(linkResolver.resolveLink(URI.create(CONTENT_URI))).thenReturn(getContent());
  }

  private Content getContent() {
    return contentRepository.getContent(CONTENT_ID);
  }

  @Test
  public void testZipMetadata() throws Exception {
    Map<String, Object> archive = (Map<String, Object>) zipMetadataResolver.resolveMetadata(getContent().getBlobRef(BLOB_PROPERTY));
    assertNotNull(archive);
    List<ZipFileEntry> files = (List<ZipFileEntry>) archive.get("files");
    assertNotNull(files);
    assertEquals("com.coremedia.cae:jmeter-result-analyser:0.2.4-SNAPSHOT", archive.get("label"));
    ZipFileEntry com = files.get(0);
    ZipFileEntry metaInf = files.get(1);
    ZipFileEntry file1 = files.get(2);
    ZipFileEntry file2 = files.get(3);
    ZipFileEntry file3 = files.get(4);
    assertEquals("META-INF", metaInf.getName());
    assertTrue(metaInf.isDirectory());
    assertEquals("com", com.getName());
    assertTrue(com.isDirectory());
    assertEquals("File.txt", file1.getName());
    assertFalse(file1.isDirectory());
    assertEquals("file.txt", file2.getName());
    assertFalse(file2.isDirectory());
    assertEquals("file3.txt", file3.getName());
    assertFalse(file3.isDirectory());
    ZipFileEntry manifest = metaInf.getChildren().get(2);
    assertFalse(manifest.isDirectory());
    assertEquals("MANIFEST.MF", manifest.getName());
    assertEquals(BLOB_URI.replace("/properties/", "/zip/") + "/META-INF/MANIFEST.MF", manifest.getUrl().toString());
  }

}
