package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The CMVideo adds no extra properties but leaves a pluggable spot where needed properties could be
 * attached via a DocTypeAspect.
 * <p>Represents the document type {@link #NAME CMVideo}.</p>
 */
public interface CMVideo extends CMVisual {

  String NAME = "CMVideo";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMVisual} object
   */
  @Override
  CMVisual getMaster();

  @Override
  Map<Locale, ? extends CMVideo> getVariantsByLocale();

  @Override
  Collection<? extends CMVideo> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMVideo>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMVideo>> getAspects();
}
