package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The CMInteractive type adds no extra properties but leaves a pluggable spot where needed
 * properties could be attached via a DocTypeAspect.
 * <p>Represents the document type {@link #NAME CMInteractive}.</p>
 */
public interface CMInteractive extends CMVisual {
  String NAME = "CMInteractive";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMInteractive} object
   */
  @Override
  CMInteractive getMaster();

  @Override
  Map<Locale, ? extends CMInteractive> getVariantsByLocale();

  @Override
  Collection<? extends CMInteractive> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMInteractive>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMInteractive>> getAspects();
}
