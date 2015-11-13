package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.GeneralTransformerFactoryImpl;
import com.coremedia.publisher.importer.MultiResult;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import java.util.Arrays;
import java.util.List;

/**
 * @deprecated Not needed any longer.
 */
@Deprecated
public class FindRequiredFilesTransformerFactory extends GeneralTransformerFactoryImpl {
  private String extensions = "";

  public FindRequiredFilesTransformerFactory() throws ClassNotFoundException {
    super(StreamSource.FEATURE, MultiResult.FEATURE, FindRequiredFilesTransformer.class.getName());
  }

  @Override
  public Transformer getTransformer(String name) {
    try {
      Transformer trf = super.getTransformer(name);
      List<String> imageExtensions = Arrays.asList(extensions.split(","));
      trf.setParameter("extensions", imageExtensions);
      return trf;
    } catch (Exception e) {
      throw new RuntimeException("Cannot get Transformer", e);
    }
  }

  public void setExtensions(String extensions) {
    this.extensions = extensions;
  }
}
