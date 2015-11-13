package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.MultiResult;
import com.coremedia.publisher.importer.ResultFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class FindRequiredFilesTransformerTest {
  private Transformer findRequiredFilesTransformer;
  private File workingDir;

  @ClassRule
  public static TemporaryFolder testFolder = new TemporaryFolder();

  @Before
  public void setUp() throws Exception {
    setUpTransformer();
    setUpWorkingDirectory();
  }

  /**
   * Set up working directory with example files from resources.
   *
   * @throws IOException if setting up example files fails
   */
  private void setUpWorkingDirectory() throws IOException, URISyntaxException {
    final Class<? extends FindRequiredFilesTransformerTest> myClass = getClass();
    final File original = new File(FindRequiredFilesTransformer.class.getResource("test.css").toURI());
    workingDir = testFolder.newFolder(myClass.getSimpleName());
    FileUtils.copyDirectory(original.getParentFile(), workingDir);
  }

  private void setUpTransformer() throws Exception {
    final FindRequiredFilesTransformerFactory factory = new FindRequiredFilesTransformerFactory();
    factory.setExtensions("png, bmp, gif");
    findRequiredFilesTransformer = factory.getTransformer("FindRequiredFilesTransformer");
  }

  @Test
  public void testTransform() throws Exception {
    final File cssFile = new File(workingDir + "/test.css");
    final Source source = new StreamSource(FileUtils.openInputStream(cssFile), cssFile.toURI().toASCIIString());
    final MultiResult multiResult = ResultFactory.getInstance().getMultiResult();
    findRequiredFilesTransformer.transform(source, multiResult);
    Assert.assertEquals(5, multiResult.size());
  }
}
