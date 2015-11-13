package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>Nearly everything except very technical entities is localizable, so this
 * type is nearly top level.</p>
 * <p>Derived doctypes <b>must</b> override the master linklist and restrict it to exactly their own type.</p>
 * <p>Represents the document type {@link #NAME CMLocalized}.</p>
 */
public interface CMLocalized extends CMObject {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMLocalized'.
   */
  String NAME = "CMLocalized";

  @Override
  Map<String, ? extends Aspect<? extends CMLocalized>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMLocalized>> getAspects();

  /**
   * Returns the variants of this {@link CMLocalized} indexed by their {@link java.util.Locale}
   *
   * @return the variants of this {@link CMLocalized} indexed by their {@link java.util.Locale}
   */
  Map<Locale, ? extends CMLocalized> getVariantsByLocale();

  /**
   * Returns the {@link java.util.Locale} specific variants of this {@link CMLocalized}
   *
   * @return the {@link java.util.Locale} specific variants of this {@link CMLocalized}
   */
  Collection<? extends CMLocalized> getLocalizations();

  /**
   * Name of the document property 'master'.
   */
  String MASTER = "master";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMLocalized} object
   */
  CMLocalized getMaster();

  /**
   * Name of the document property 'masterVersion'.
   */
  String MASTER_VERSION = "masterVersion";

  /**
   * Returns the value of the document property {@link #MASTER_VERSION}.
   *
   * @return the value of the document property {@link #MASTER_VERSION}
   */
  int getMasterVersion();

  /**
   * Name of the document property 'locale'.
   */
  String LOCALE = "locale";

  /**
   * Returns the Locale of this document.
   *
   * @return the Locale of this document.
   */
  Locale getLocale();

  /**
   * Returns the language of this document which is either the empty string or a lowercase ISO 639 code.
   *
   * @return the language of this document
   */
  String getLang();

  /**
   * Returns the country/region of this document which will either be the empty string or an
   * uppercase ISO 3166 2-letter code.
   *
   * @return the country/region of this document
   */
  String getCountry();

}
