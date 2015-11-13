package com.coremedia.blueprint.common.contentbeans;


import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * CMCSS beans provide static CSS resources with a media attribute and
 * a dependency list with other CSS documents.
 *
 * <p>Represents document type {@link #NAME CMCSS}.</p>
 */
public interface CMCSS extends CMAbstractCode {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMCSS'.
   */
  String NAME = "CMCSS";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMCSS} object
   */
  @Override
  CMCSS getMaster();

  @Override
  Map<Locale, ? extends CMCSS> getVariantsByLocale();

  @Override
  Collection<? extends CMCSS> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMCSS>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMCSS>> getAspects();

  /**
   * Name of the document property 'media'.
   */
  String MEDIA = "media";

  /**
   * Returns the value of the document property {@link #MEDIA}.
   *
   * @return the value of the document property {@link #MEDIA}
   */
  String getMedia();

  /**
   * Returns the value of the document property {@link #INCLUDE}.
   *
   * @return a list of {@link CMCSS} objects
   */
  @Override
  List<? extends CMCSS> getInclude();
}
