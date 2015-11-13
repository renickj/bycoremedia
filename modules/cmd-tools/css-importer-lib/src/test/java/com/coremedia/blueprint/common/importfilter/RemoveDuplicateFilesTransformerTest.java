package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.MultiResult;
import com.coremedia.publisher.importer.MultiSource;
import com.coremedia.publisher.importer.ResultFactory;
import org.junit.Assert;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RemoveDuplicateFilesTransformerTest {

  @Test
  public void testTransform() throws Exception {
    File original = new File(RemoveDuplicateFilesTransformerTest.class.getResource("test.css").toURI());
    File workingDir = new File(FileUtils.getTempDirectoryPath() + "/RemoveDuplicateImagesTransformerTest");
    if (!workingDir.exists()) {
      workingDir.mkdir();
    } else {
      FileUtils.cleanDirectory(workingDir);
    }
    FileUtils.copyDirectory(original.getParentFile(), workingDir);


    FindRequiredFilesTransformerFactory findRequiredFilesTransformerFactory = new FindRequiredFilesTransformerFactory();
    findRequiredFilesTransformerFactory.setExtensions("png, bmp, gif");

    Transformer findRequiredImagesTransformer = findRequiredFilesTransformerFactory.getTransformer("FindRequiredFilesTransformer");
    File cssFile = new File(workingDir + "/test.css");
    StreamSource source = new StreamSource(FileUtils.openInputStream(cssFile), cssFile.toURI().toASCIIString());
    MultiResult multiResult = ResultFactory.getInstance().getMultiResult();
    findRequiredImagesTransformer.transform(source, multiResult);

    List<Source> items = new ArrayList<>();
    for (int i = 0; i < multiResult.size(); i++) {
      items.add(multiResult.getSource(i, StreamSource.FEATURE));
    }
    MultiSource multiSource = new MultiSourceMock(items);
    multiResult = ResultFactory.getInstance().getMultiResult();
    RemoveDuplicateFilesTransformerFactory factory = new RemoveDuplicateFilesTransformerFactory();
    Transformer removeDuplicateImagesTransformer = factory.getTransformer("RemoveDuplicateFilesTransformer");
    removeDuplicateImagesTransformer.transform(multiSource, multiResult);
    Assert.assertEquals(2, multiResult.size());

  }
}
