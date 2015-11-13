package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dynamic resources which don't contain their actual content in the shape of
 * properties but generate it on the fly by some business logic.
 * <p>Usecases:  Search, Login, UAPI Queries, Webflows ...</p>
 * <p>Represents the document type "CMAction".</p>
 */
public interface CMAction extends CMPlaceholder {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMAction'
   */
  String NAME = "CMAction";

  /**
   * Name of the document property 'type'.
   */
  String TYPE = "type";

  /**
   * Returns the value of the document property {@link #TYPE}.
   *
   * @return a {@link String} string
   */
  String getType();

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMAction} object
   */
  @Override
  CMAction getMaster();

  @Override
  Map<Locale, ? extends CMAction> getVariantsByLocale();

  @Override
  Collection<? extends CMAction> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMAction>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMAction>> getAspects();


  /**
   * @return Checks whether this action represents a webflow action.
   */
  boolean isWebFlow();





}
