package com.coremedia.blueprint.common.contentbeans;


import com.coremedia.blueprint.common.datevalidation.ValidityPeriod;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.struct.Struct;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CMLinkable is the base type for each document which can be referenced
 * as standalone content or navigation unit.
 *
 * <p>Represents the document type {@link #NAME CMLinkable}.</p>
 */
public interface CMLinkable extends Linkable, CMLocalized, BelowRootNavigation, ValidityPeriod {

  /**
   * {@link com.coremedia.cap.content.ContentType#getName() Name of the ContentType} 'CMLinkable'.
   */
  String NAME = "CMLinkable";

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a {@link CMLinkable} object
   */
  @Override
  CMLinkable getMaster();

  @Override
  Map<Locale, ? extends CMLinkable> getVariantsByLocale();

  @Override
  Collection<? extends CMLinkable> getLocalizations();

  @Override
  Map<String, ? extends Aspect<? extends CMLinkable>> getAspectByName();

  @Override
  List<? extends Aspect<? extends CMLinkable>> getAspects();

  /**
   * Name of the document property '  viewtype'.
   */
  String VIEWTYPE = "viewtype";

  /**
   * Returns the first value of the document property {@link #VIEWTYPE}.
   *
   * @return a list of {@link CMViewtype} objects
   */
  CMViewtype getViewtype();

  @Deprecated  // Obsolete with SettingsService
  String LOCAL_SETTINGS = "localSettings";

  /**
   * Name of the document property 'keywords'.
   */
  String KEYWORDS = "keywords";

  /**
   * Returns the value of the document property {@link #KEYWORDS}.
   *
   * @return the value of the document property {@link #KEYWORDS}
   */
  @Override
  String getKeywords();

  /**
   * Name of the document property 'htmlDescription'.
   */
  String HTML_DESCRIPTION = "htmlDescription";


  /**
   * Returns the value of the document property {@link #HTML_DESCRIPTION}.
   *
   * @return the value of the document property {@link #HTML_DESCRIPTION}
   */
  String getHtmlDescription();

  /**
   * Name of the document property 'htmlTitle'.
   */
  String HTML_TITLE = "htmlTitle";

  /**
   * Returns the value of the document property {@link #HTML_TITLE}.
   *
   * @return the value of the document property {@link #HTML_TITLE}
   */
  String getHtmlTitle();

  /**
   * Name of the document property 'segment'.
   */
  String SEGMENT = "segment";

  /**
   * Name of the document property 'title'.
   */
  String TITLE = "title";

  /**
   * Returns the title to be used in the head meta data.
   *
   * @return the value of the document property {@link #TITLE}
   */
  @Override
  String getTitle();

  /**
   * Returns the contexts of this CMLinkable.
   *
   * @return a list of {@link CMContext} objects
   */
  List<CMContext> getContexts();


  /**
   * Return local settings as a Struct.
   *
   * @return local settings. May return null if no settings are found.
   *
   * @deprecated use SettingsService
   */
  @Deprecated
  Struct getLocalSettings();

  /**
   * Name of the document property 'linkedSettings'.
   */
  String LINKED_SETTINGS = "linkedSettings";

  /**
   * Returns all {@link CMSettings} linked settings.
   * <p/>
   *
   * @return a {@link java.util.List} of {@link CMSettings} objects
   */
  List<CMSettings> getLinkedSettings();

  /**
   * Name of the document property 'validFrom'.
   */
  String VALID_FROM = "validFrom";

  /**
   * Returns the value of the document property {@link #VALID_FROM}.
   *
   * @return the value of the document property {@link #VALID_FROM}
   */
  @Override
  Calendar getValidFrom();

  /**
   * Name of the document property 'validTo'.
   */
  String VALID_TO = "validTo";

  /**
   * Returns the value of the document property {@link #VALID_TO}.
   *
   * @return the value of the document property {@link #VALID_TO}
   */
  @Override
  Calendar getValidTo();

  /**
   * Name of the document property 'externallyDisplayedDate'.
   */
  String EXTERNALLY_DISPLAYED_DATE = "extDisplayedDate";

  /**
   * Returns the value of the document property {@link #EXTERNALLY_DISPLAYED_DATE}.
   *
   * @return the value of the document property {@link #EXTERNALLY_DISPLAYED_DATE}
   */
  Calendar getExternallyDisplayedDate();

  /**
   * Name of the document property 'subjectTaxonomy'.
   */
  String SUBJECT_TAXONOMY = "subjectTaxonomy";

  /**
   * Returns the value of the document property {@link #SUBJECT_TAXONOMY}.
   *
   * @return a list of {@link CMTaxonomy} objects
   */
  List<CMTaxonomy> getSubjectTaxonomy();

  /**
   * Name of the document property 'locationTaxonomy'.
   */
  String LOCATION_TAXONOMY = "locationTaxonomy";

  /**
   * Returns the value of the document property {@link #LOCATION_TAXONOMY}.
   *
   * @return a list of {@link CMLocTaxonomy} objects
   */
  List<CMLocTaxonomy> getLocationTaxonomy();

  /*
   * Lookup the given setting and return the values bound to it as a map.
   * ##todo move to customer extension.
   * @param settingName name the setting name
   * @return the map or null
   */
  Map<String,Object> getSettingMap(String settingName);
}
