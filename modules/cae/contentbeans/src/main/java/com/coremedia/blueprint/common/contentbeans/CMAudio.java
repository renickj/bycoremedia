package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.common.Blob;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The CMAudio type splits the media hierarchy to audio components.
 * <p>
 * It provides the audio data as a blob property of mime type audio/*.
 *
 * <p>Represents the document type {@link #NAME CMAudio}.</p>
 */
public interface CMAudio extends CMMedia {

  String NAME = "CMAudio";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMAudio} object
   */
  @Override
  CMAudio getMaster();

  @Override
  Map<Locale, ? extends CMAudio> getVariantsByLocale();

  @Override
  Collection<? extends CMAudio> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMAudio>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMAudio>> getAspects();

  /**
   * Name of the document property 'dataUrl'.
   */
  String DATA_URL = "dataUrl";

  /**
   * Returns the value of the document property (@link #data}
   *
   * @return the value of the document property (@link #data}
   */
  @Override
  Blob getData();

  /**
   * Returns the value of the document property (@link #dataUrl}
   *
   * @return the value of the document property (@link #dataUrl}
   */
  String getDataUrl();
}
