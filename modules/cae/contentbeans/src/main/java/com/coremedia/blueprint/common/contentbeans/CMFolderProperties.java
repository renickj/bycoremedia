package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import java.util.List;
import java.util.Map;

/**
 * CMFolderProperties documents hold a list of contexts which is inherited by all
 * resources under this document's folder.  If most resources have the same contexts,
 * this is more convenient than assigning the CMHasContexts.contexts property.
 *
 * <p>Represents document type {@link #NAME CMFolderProperties}.</p>
 */
public interface CMFolderProperties extends CMLocalized {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMFolderProperties'.
   */
  String NAME = "CMFolderProperties";

  @Override
  Map<String, ? extends Aspect<? extends CMFolderProperties>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMFolderProperties>> getAspects();

  /**
   * Name of the document property 'contexts'.
   */
  String CONTEXTS = "contexts";

  /**
   * Returns the contexts that documents in and below this folder should belong to
   *
   * @return a list of {@link CMContext} objects
   */
  List<? extends CMContext> getContexts();
}
