package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMVideo;
import com.coremedia.cae.aspect.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Generated base class for immutable beans of document type CMVideo.
 * Should not be changed.
 */
public abstract class CMVideoBase extends CMVisualImpl implements CMVideo {

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link com.coremedia.blueprint.common.contentbeans.CMVideo} objects
   */
  @Override
  public CMVideo getMaster() {
    return (CMVideo) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMVideo> getVariantsByLocale() {
    return getVariantsByLocale(CMVideo.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMVideo> getLocalizations() {
    return (Collection<? extends CMVideo>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMVideo>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMVideo>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMVideo>> getAspects() {
    return (List<? extends Aspect<? extends CMVideo>>) super.getAspects();
  }
}
