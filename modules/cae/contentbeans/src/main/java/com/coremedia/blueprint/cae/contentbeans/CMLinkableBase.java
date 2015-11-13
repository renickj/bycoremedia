package com.coremedia.blueprint.cae.contentbeans;


import com.coremedia.blueprint.base.links.UrlPathFormattingHelper;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMLocTaxonomy;
import com.coremedia.blueprint.common.contentbeans.CMLocalized;
import com.coremedia.blueprint.common.contentbeans.CMSettings;
import com.coremedia.blueprint.common.contentbeans.CMTaxonomy;
import com.coremedia.blueprint.common.contentbeans.CMViewtype;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.base.navigation.context.ContextStrategy;
import com.coremedia.blueprint.common.services.validation.ValidationService;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generated base class for immutable beans of document type CMLinkable.
 * Should not be changed.
 */
public abstract class CMLinkableBase extends CMLocalizedImpl implements CMLinkable {

  private ContextStrategy<CMLinkable, CMContext> contextStrategy;

  private ValidationService<Linkable> validationService;

  private SettingsService settingsService;
  private UrlPathFormattingHelper urlPathFormattingHelper;


  // This should be protected, since it is not meant to be a feature of
  // a contentbean, but only for internal usage in subclasses.
  // public only for compatibility reasons.
  public ContextStrategy<CMLinkable, CMContext> getContextStrategy() {
    return contextStrategy;
  }

  @Required
  public void setContextStrategy(ContextStrategy<CMLinkable, CMContext> contextStrategy) {
    if(contextStrategy == null) {
      throw new IllegalArgumentException("supplied 'contextStrategy' must not be null");
    }
    this.contextStrategy = contextStrategy;
  }

  @SuppressWarnings("unchecked")
  public ValidationService<Linkable> getValidationService() {
    return validationService;
  }

  @Required
  public void setValidationService(ValidationService<Linkable> validationService) {
    if(validationService == null) {
      throw new IllegalArgumentException("supplied 'validationService' must not be null");
    }
    this.validationService = validationService;
  }

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMLinkable} objects
   */
  @Override
  public CMLinkable getMaster() {
    return (CMLinkable) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMLinkable> getVariantsByLocale() {
    return getVariantsByLocale(CMLinkable.class);
  }

  @Override
  protected <T extends CMLocalized> Map<Locale, T> getVariantsByLocale(Class<T> type) {
    Map<Locale, T> variantsByLocale = super.getVariantsByLocale(type);
    return ImmutableMap.copyOf(Maps.filterValues(variantsByLocale, new Predicate<T>() {
      @Override
      public boolean apply(T variant) {
        return variant instanceof Linkable && validationService.validate((Linkable) variant);
      }
    }));
  }

  public Map<Locale, ? extends CMLinkable> getVariantsByLocaleUnfiltered() {
    return super.getVariantsByLocale(CMLinkable.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMLinkable> getLocalizations() {
    return (Collection<? extends CMLinkable>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMLinkable>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMLinkable>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMLinkable>> getAspects() {
    return (List<? extends Aspect<? extends CMLinkable>>) super.getAspects();
  }

  /**
   * Returns the first value of the document property {@link #VIEWTYPE}.
   *
   * @return a {@link CMViewtype}
   */
  @Override
  public CMViewtype getViewtype() {
    Content viewtype = getContent().getLink(VIEWTYPE);
    return createBeanFor(viewtype, CMViewtype.class);
  }

  /**
   * Returns the value of the document property {@link #KEYWORDS}.
   *
   * @return the value of the document property {@link #KEYWORDS}
   */
  @Override
  public String getKeywords() {
    return getContent().getString(KEYWORDS);
  }

  /**
   * Returns the value of the document property {@link #SEGMENT}.
   *
   * @return the value of the document property {@link #SEGMENT}
   */
  @Override
  public String getSegment() {
    return getContent().getString(SEGMENT);
  }

  /**
   * Returns the value of the document property  {@link #TITLE}
   *
   * @return the value of the document property  {@link #TITLE}
   */
  @Override
  public String getTitle() {
    return getContent().getString(CMLinkable.TITLE);
  }

  /**
   * Returns the value of the document property {@link #HTML_TITLE}
   *
   * @return the value of the document property {@link #HTML_TITLE}
   */
  @Override
  public String getHtmlTitle() {
    String title = getContent().getString(CMLinkable.HTML_TITLE);
    if(StringUtils.isEmpty(title)) {
      title = getContent().getString(CMLinkable.TITLE);
    }
    return title;
  }

  /**
   * Returns the value of the document property {@link #HTML_DESCRIPTION}
   *
   * @return the value of the document property {@link #HTML_DESCRIPTION}
   */
  @Override
  public String getHtmlDescription() {
    return getContent().getString(CMLinkable.HTML_DESCRIPTION);
  }

  /**
   * Returns the value of the document property {@link #LOCAL_SETTINGS}.
   *
   * @return the value of the document property {@link #LOCAL_SETTINGS}
   */
  @Override
  public Struct getLocalSettings() {
    Struct struct = getContent().getStruct(LOCAL_SETTINGS);
    return struct != null ? struct : getContent().getRepository().getConnection().getStructService().emptyStruct();
  }

  /**
   * Returns the value of the document property {@link #LINKED_SETTINGS}.
   *
   * @return a list of {@link CMSettings} objects
   */
  @Override
  public List<CMSettings> getLinkedSettings() {
    List<Content> contents = getContent().getLinks(LINKED_SETTINGS);
    return createBeansFor(contents, CMSettings.class);
  }

  @Override
  public Calendar getValidFrom() {
    return getContent().getDate(CMLinkable.VALID_FROM);
  }

  @Override
  public Calendar getExternallyDisplayedDate() {
    Calendar displayedDate = getContent().getDate(CMLinkable.EXTERNALLY_DISPLAYED_DATE);
    Calendar modificationDate = getContent().getModificationDate();

    if (displayedDate == null) {
      displayedDate = modificationDate;
    }
    return displayedDate;
  }

  @Override
  public Calendar getValidTo() {
    return getContent().getDate(CMLinkable.VALID_TO);
  }

  @Override
  public List<CMTaxonomy> getSubjectTaxonomy() {
    List<Content> contents = getContent().getLinks(SUBJECT_TAXONOMY);
    return createBeansFor(contents, CMTaxonomy.class);
  }

  @Override
  public List<CMLocTaxonomy> getLocationTaxonomy() {
    List<Content> contents = getContent().getLinks(LOCATION_TAXONOMY);
    return createBeansFor(contents, CMLocTaxonomy.class);
  }

  protected SettingsService getSettingsService() {
    return settingsService;
  }

  protected UrlPathFormattingHelper getUrlPathFormattingHelper() {
    return urlPathFormattingHelper;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @Required
  public void setUrlPathFormattingHelper(UrlPathFormattingHelper urlPathFormattingHelper) {
    this.urlPathFormattingHelper = urlPathFormattingHelper;
  }
}
  
