package com.coremedia.blueprint.elastic.social.cae.flows;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Locale;

import static org.apache.commons.lang3.LocaleUtils.toLocale;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LocalizedLocale implements Serializable {
  private static final long serialVersionUID = 42L;
  private String displayLanguage;
  private Locale locale;

  public LocalizedLocale() {
  }

  public LocalizedLocale(String localizedLocale) {
    if (isNotBlank(localizedLocale)) {
      String[] localeItems = StringUtils.split(localizedLocale, '_');
      if (localeItems.length > 0) {
        displayLanguage = localeItems[0];
        StringBuilder builder = new StringBuilder();
        if (localeItems.length > 1) {
          builder.append(localeItems[1]);
        }
        if (localeItems.length > 2) {
          builder.append("_").append(localeItems[2]);
        }
        if (localeItems.length > 3) {
          builder.append("_").append(localeItems[3]);
        }
        locale = toLocale(builder.toString());
      }
    }
  }

  public LocalizedLocale(Locale locale, String displayLanguage) {
    this.locale = locale;
    this.displayLanguage = displayLanguage;
  }

  public String getDisplayLanguage() {
    return displayLanguage;
  }

  public void setDisplayLanguage(String displayLanguage) {
    this.displayLanguage = displayLanguage;
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (StringUtils.isNotBlank(displayLanguage)) {
      builder.append(displayLanguage);
    }
    if (this.locale != null) {
      if (StringUtils.isNotBlank(locale.getLanguage())) {
        builder.append("_").append(locale.getLanguage());
      }
      if (StringUtils.isNotBlank(locale.getCountry())) {
        builder.append("_").append(locale.getCountry());
      }
      if (StringUtils.isNotBlank(locale.getVariant())) {
        builder.append("_").append(locale.getVariant());
      }
    }
    return builder.toString();
  }

  public Locale getLocale() {
    return locale;
  }
}
