package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * CMExternalLink enhances an external URL with the Teasable features.
 *
 * <p>Represents document type {@link #NAME CMExternalLink}.</p>
 */
public interface CMExternalLink extends CMTeasable {
  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMExternalLink'.
   */
  String NAME = "CMExternalLink";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMExternalLink} object
   */
  @Override
  CMExternalLink getMaster();

  @Override
  Map<Locale, ? extends CMExternalLink> getVariantsByLocale();

  @Override
  Collection<? extends CMExternalLink> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMExternalLink>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMExternalLink>> getAspects();

  /**
   * Name of the document property 'url'.
   */
  String URL = "url";

  /**
   * Returns the value of the document property {@link #URL}.
   *
   * @return the value of the document property {@link #URL}
   */
  String getUrl();
}
