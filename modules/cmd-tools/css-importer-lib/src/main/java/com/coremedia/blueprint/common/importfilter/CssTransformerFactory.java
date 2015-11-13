package com.coremedia.blueprint.common.importfilter;

import com.coremedia.mimetype.DefaultMimeTypeService;
import com.coremedia.publisher.importer.GeneralTransformerFactoryImpl;

import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.net.URI;

public class CssTransformerFactory extends GeneralTransformerFactoryImpl {
  private String targetpath;

  public CssTransformerFactory() throws ClassNotFoundException {
    super(StreamSource.FEATURE, StreamResult.FEATURE, CssTransformer.class.getName());
  }

  public void setTargetpath(String targetpath) {
    this.targetpath = normalizePath(targetpath);
  }

  @Override
  public Transformer getTransformer(String name) {
    try {
      Transformer trf = super.getTransformer(name);
      URI targeturi = new URI("coremedia", "", targetpath + "/dummyForResolve", null);
      trf.setParameter("targeturi", targeturi);
      trf.setParameter(CssTransformer.PARAM_MIME_TYPE_SERVICE, new DefaultMimeTypeService(true));
      return trf;
    } catch (Exception e) {
      throw new RuntimeException("Cannot get Transformer", e);
    }
  }

  private static String normalizePath(String path) {
    String result = path.trim();
    if (result.endsWith("/")) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }
}
