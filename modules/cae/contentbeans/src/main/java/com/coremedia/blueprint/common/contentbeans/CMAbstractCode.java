package com.coremedia.blueprint.common.contentbeans;


import com.coremedia.cae.aspect.Aspect;
import com.coremedia.xml.Markup;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Aka ClientCode. E.g. for CSS or JS.
 * We represent script code as CoreMedia Richtext because of
 * internal link support.
 * <p>Represents the document type {@link #NAME CMAbstractCode}.</p>
 */
public interface CMAbstractCode extends CMLocalized {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMAbstractCode'.
   */
  String NAME = "CMAbstractCode";


  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMAbstractCode} object
   */
  @Override
  CMAbstractCode getMaster();

  @Override
  Map<Locale, ? extends CMAbstractCode> getVariantsByLocale();

  @Override
  Collection<? extends CMAbstractCode> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMAbstractCode>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMAbstractCode>> getAspects();

  /**
   * Name of the document property 'description'.
   */
  String DESCRIPTION = "description";

  /**
   * Returns the value of the document property {@link #DESCRIPTION}.
   *
   * @return the value of the document property {@link #DESCRIPTION}
   */
  String getDescription();


  /**
   * Name of the document property 'code'.
   */
  String CODE = "code";

  /**
   * Returns the value of the document property {@link #CODE}.
   *
   * @return the value of the document property {@link #CODE}
   */
  Markup getCode();


  /**
   * Name of the document property 'include'.
   */
  String INCLUDE = "include";

  /**
   * Returns the value of the document property {@link #INCLUDE}.
   *
   * @return a list of {@link CMAbstractCode} objects
   */
  List<? extends CMAbstractCode> getInclude();

  /**
   * @return the content type of the code, e.g. text/css
   */
  String getContentType();

  /**
   * Name of the document property 'ieExpression'.
   */
  String IE_EXPRESSION = "ieExpression";

  /**
   * Returns the value of the document property {@link #IE_EXPRESSION}.
   *
   * @return the value of the document property {@link #IE_EXPRESSION}
   */
  String getIeExpression();

  /**
   * Name of the document property 'ieRevealed'.
   */
  String IE_REVEALED = "ieRevealed";

  /**
   * Returns the value of the document property {@link #IE_REVEALED}.
   *
   * @return the value of the document property {@link #IE_REVEALED}
   */
  boolean isIeRevealed();

  /**
   * Name of the document property 'dataUrl'.
   */
  String DATA_URL = "dataUrl";

  /**
   * Returns the value of the document property {@link #DATA_URL}.
   *
   * @return the value of the document property {@link #DATA_URL}
   */
  String getDataUrl();
}
