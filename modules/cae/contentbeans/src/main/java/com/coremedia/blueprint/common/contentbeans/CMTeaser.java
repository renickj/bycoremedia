package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Stand-alone teaser, in case you need multiple teasers to some content,
 * so that the embedded teaser is not sufficient.
 * <p>Represents the document type {@link #NAME CMTeaser}.</p>
 */
public interface CMTeaser extends CMTeasable {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMTeaser'.
   */
  String NAME = "CMTeaser";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMTeaser} object
   */
  @Override
  CMTeaser getMaster();

  @Override
  Map<Locale, ? extends CMTeaser> getVariantsByLocale();

  @Override
  Collection<? extends CMTeaser> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMTeaser>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMTeaser>> getAspects();

  /**
   * Name of the document property 'target'.
   */
  String TARGET = "target";

  /**
   * Returns the value of the document property {@link #TARGET}.
   *
   * @return a {@link CMLinkable} object
   */
  @Override
  @SuppressWarnings({"AbstractMethodOverridesAbstractMethod"})
  CMLinkable getTarget();
}
