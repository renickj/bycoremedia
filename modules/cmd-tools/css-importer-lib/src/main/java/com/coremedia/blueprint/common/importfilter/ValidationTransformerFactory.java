package com.coremedia.blueprint.common.importfilter;


import com.coremedia.publisher.importer.GeneralTransformerFactoryImpl;
import com.coremedia.publisher.importer.MultiResult;
import com.coremedia.publisher.importer.MultiSource;

public class ValidationTransformerFactory extends GeneralTransformerFactoryImpl {
  public ValidationTransformerFactory() throws ClassNotFoundException {
    super(MultiSource.FEATURE, MultiResult.FEATURE, ValidationTransformer.class.getName());
  }
}
