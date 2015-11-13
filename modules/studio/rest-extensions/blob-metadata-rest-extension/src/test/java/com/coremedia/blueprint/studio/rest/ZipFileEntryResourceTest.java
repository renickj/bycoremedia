package com.coremedia.blueprint.studio.rest;

import com.coremedia.cap.content.ContentRepository;
import com.coremedia.mimetype.DefaultMimeTypeService;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = BlobMetadataRestTestConfiguration.class)
public class ZipFileEntryResourceTest {

  private static final String CONTENT_ID = "502";
  private static final String TEST_FILE1_CONTENTS = "var i = 0;\r\n" +
          "window.location.href = \"about:blank\";";

  private ZipFileEntryResource resource;
  private static final String TEST_DIRECTORY_LISTING_CONTENTS =
          "com/coremedia/cms/jmeteranalysis/AnalyzeCommand.class\n" +
                  "com/coremedia/cms/jmeteranalysis/AnalyzeMojo.class\n" +
                  "com/coremedia/cms/jmeteranalysis/parser/\n" +
                  "com/coremedia/cms/jmeteranalysis/ResultRenderHelper.class\n" +
                  "com/coremedia/cms/jmeteranalysis/statistics/\n";

  @Autowired
  private ContentRepository contentRepository;

  @Before
  public void setUp() throws Exception {

    DefaultMimeTypeService mimeTypeService = new DefaultMimeTypeService();
    mimeTypeService.setMappings(Collections.singletonMap("text/javascript", "js"));

    resource = new ZipFileEntryResource();
    resource.setMimeTypeService(mimeTypeService);
    resource.setContentRepository(contentRepository);
    resource.setId(CONTENT_ID);
    resource.setProperty("data");
  }

  @Test
  public void testZipFileContent() throws Exception {
    resource.setPath("com/coremedia/cms/js/test.js");
    Response response = resource.getFile();
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertEquals("text/javascript", response.getMetadata().get("content-type").get(0).toString());

    ZipInputStream entity = (ZipInputStream) response.getEntity();
    assertArrayEquals(TEST_FILE1_CONTENTS.getBytes(), getStreamAsBytes(entity));
  }

  @Test
  public void testZipDirectoryListing() throws Exception {
    testZipDirectoryListing("com/coremedia/cms/jmeteranalysis", TEST_DIRECTORY_LISTING_CONTENTS);
    testZipDirectoryListing("com/coremedia/cms/jmeteranalysis/", TEST_DIRECTORY_LISTING_CONTENTS);
  }

  public void testZipDirectoryListing(String path, String result) throws Exception {
    resource.setPath(path);
    Response response = resource.getFile();
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertEquals("text/plain", response.getMetadata().get("content-type").get(0).toString());
    String entity = (String) response.getEntity();
    assertEquals(result, entity);
  }

  private byte[] getStreamAsBytes(InputStream in) throws IOException {
    return IOUtils.toByteArray(in);
  }
}
