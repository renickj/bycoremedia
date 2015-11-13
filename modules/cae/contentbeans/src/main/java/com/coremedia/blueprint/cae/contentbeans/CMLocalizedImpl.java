package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMLocalized;
import com.coremedia.cap.content.Content;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Generated extension class for immutable beans of document type "CMLocalized".
 */
public abstract class CMLocalizedImpl extends CMLocalizedBase {
  @Override
  public Locale getLocale() {
    return getSitesService().getContentSiteAspect(getContent()).getLocale();
  }

  @Override
  public String getLang() {
    Locale locale = getLocale();
    return locale != null ? locale.getLanguage() : null;
  }

  @Override
  public String getCountry() {
    Locale locale = getLocale();
    return locale != null ? locale.getCountry() : null;
  }

  public CMLocalized getVariant(Locale locale) {
    return getVariantsByLocale().get(locale);
  }

  @Override
  public Map<Locale, ? extends CMLocalized> getVariantsByLocale() {
    return getVariantsByLocale(CMLocalized.class);
  }

  protected <T extends CMLocalized> Map<Locale, T> getVariantsByLocale(Class<T> type) {
    Set<Content> variants = getSitesService().getContentSiteAspect(getContent()).getVariants();
    Map<Locale, T> variantsByLocale = new TreeMap<>(new LocaleComparator());
    for (Content variant : variants) {
      T variantBean = createBeanFor(variant, type);
      if (variantBean != null) {
        Locale locale = variantBean.getLocale();
        if (locale != null) {
          variantsByLocale.put(locale, variantBean);
        }
      }
    }
    return variantsByLocale;
  }

  @Override
  public Collection<? extends CMLocalized> getLocalizations() {
    return getVariantsByLocale().values();
  }

  private static class LocaleComparator implements Comparator<Locale>, Serializable {

    private static final long serialVersionUID = 5667734265087336585L;

    @Override
    public int compare(Locale l1, Locale l2) {
      return l1.toLanguageTag().compareTo(l2.toLanguageTag());
    }
  }
}
