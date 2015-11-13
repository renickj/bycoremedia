package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.cae.contentbeans.CMChannelImpl;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class CMExternalChannelBase extends CMChannelImpl implements CMExternalChannel {

  // --- Standard Blueprint typing overrides ------------------------

  @Override
  public String getExternalUriPath() {
    return getSettingsService().setting(CMExternalChannel.EXTERNAL_URI_PATH, String.class, this);
  }

  @Override
  public boolean isCatalogPage() {
    Object value = getLocalSettings().get(CMExternalChannel.IS_CATALOG_PAGE);
    return value instanceof Boolean ? (Boolean) value : true;
  }

  @Override
  public CMExternalChannel getMaster() {
    return (CMExternalChannel) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMExternalChannel> getVariantsByLocale() {
    return getVariantsByLocale(CMExternalChannel.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMExternalChannel> getLocalizations() {
    return (Collection<? extends CMExternalChannel>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMExternalChannel>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMExternalChannel>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMExternalChannel>> getAspects() {
    return (List<? extends Aspect<? extends CMExternalChannel>>) super.getAspects();
  }


  // --- Features ---------------------------------------------------

  @Override
  public List<? extends Linkable> getChildren() {
    List<? extends Linkable> internalChildren = super.getChildren();
    List<Linkable> externalChildren = getExternalChildren(getSitesService().getContentSiteAspect(getContent()).getSite());
    return merge(internalChildren, externalChildren);
  }

  protected abstract List<Linkable> getExternalChildren(Site siteId);

  private List<? extends Linkable> merge(List<? extends Linkable> internalChildren, List<Linkable> externalChildren) {
    // if some children are explicitely linked, take these children only and do NOT add any external children
    if (internalChildren != null && !internalChildren.isEmpty()) {
      return internalChildren;
    }
    return externalChildren != null ? externalChildren : Collections.<Navigation>emptyList();
  }

  protected static CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }
}