package com.coremedia.blueprint.common.importfilter;

import com.coremedia.publisher.importer.GeneralTransformerFactoryImpl;
import com.coremedia.publisher.importer.MultiResult;
import com.coremedia.publisher.importer.MultiSource;

/**
 * @deprecated Not needed any longer.
 */
@Deprecated
public class RemoveDuplicateFilesTransformerFactory extends GeneralTransformerFactoryImpl {

  public RemoveDuplicateFilesTransformerFactory() throws ClassNotFoundException {
    super(MultiSource.FEATURE, MultiResult.FEATURE, RemoveDuplicateFilesTransformer.class.getName());
  }
}