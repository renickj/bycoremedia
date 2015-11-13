package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CMPlaceholder is the base type for each document that can be used as
 * a placeholder in order to use a dedicated JSP for rendering. The
 * {@link #getId() id} will be used as a viewName. (CMPlaceholder.&lt;id&gt;.jsp)
 *
 * <p>Usecases:  RSS, Language Switch, ...</p>
 * <p>Represents the document type {@link #NAME CMPlaceholder}.</p>
 */
public interface CMPlaceholder extends CMTeasable {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMPlaceholder'.
   */
  String NAME = "CMPlaceholder";

  /**
   * Name of the document property 'id'.
   */
  String ID = "id";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMLinkable} object
   */
  @Override
  CMPlaceholder getMaster();

  @Override
  Map<Locale, ? extends CMPlaceholder> getVariantsByLocale();

  @Override
  Collection<? extends CMPlaceholder> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMPlaceholder>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMPlaceholder>> getAspects();

  /**
   * Returns the value of the document property {@link #ID}.
   *
   * @return the value of the document property {@link #ID}
   */
  String getId();
}
