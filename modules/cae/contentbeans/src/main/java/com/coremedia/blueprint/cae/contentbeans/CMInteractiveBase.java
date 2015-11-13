package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMInteractive;
import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generated base class for immutable beans of document type CMInteractive.
 * Should not be changed.
 */
public abstract class CMInteractiveBase extends CMVisualImpl implements CMInteractive {

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link com.coremedia.blueprint.common.contentbeans.CMInteractive} objects
   */
  @Override
  public CMInteractive getMaster() {
    return (CMInteractive) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMInteractive> getVariantsByLocale() {
    return getVariantsByLocale(CMInteractive.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMInteractive> getLocalizations() {
    return (Collection<? extends CMInteractive>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMInteractive>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMInteractive>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMInteractive>> getAspects() {
    return (List<? extends Aspect<? extends CMInteractive>>) super.getAspects();
  }
}
