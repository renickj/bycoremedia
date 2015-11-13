package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.cae.contentbeans.CMDynamicListImpl;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class CMMarketingSpotImpl extends CMDynamicListImpl implements CMMarketingSpot {

  public static final String EXTERNAL_ID = "externalId";

  private LiveContextNavigationFactory liveContextNavigationFactory;

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link com.coremedia.livecontext.contentbeans.CMMarketingSpot} objects
   */
  @Override
  public CMMarketingSpot getMaster() {
    return (CMMarketingSpot) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMMarketingSpot> getVariantsByLocale() {
    return getVariantsByLocale(CMMarketingSpot.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMMarketingSpot> getLocalizations() {
    return (Collection<? extends CMMarketingSpot>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMMarketingSpot>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMMarketingSpot>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMMarketingSpot>> getAspects() {
    return (List<? extends Aspect<? extends CMMarketingSpot>>) super.getAspects();
  }

  @Override
  public String getExternalId() {
    return getContent().getString(EXTERNAL_ID);
  }

  @Override
  public List<CommerceObject> getItems() {
    List<CommerceObject> result = new ArrayList<>();
    MarketingSpot spot = getMarketingSpot();
    if (spot != null) {
      Site site = requireNonNull(getSitesService().getContentSiteAspect(getContent()).getSite(), "Site must not be null");
      List<CommerceObject> entities = spot.getEntities();
      for (CommerceObject o : entities) {
        // consider the the max length of items
        if (getMaxLength() <= 0 || result.size() < getMaxLength()) {
          if (o instanceof Product) {
            result.add(liveContextNavigationFactory.createProductInSite((Product) o, site));
          } else if (o instanceof Category) {
            result.add(liveContextNavigationFactory.createCategoryInSite((Category) o, site));
          } else if (o != null) {
            result.add(o);
          }
        }
      }
    }
    return result;
  }

  /**
   * @return the value of the document property "teaserTitle".
   * If it is empty then fallback to the document property "title".
   * If it is still empty then fallback to the name of the marketing spot.
   */
  @Override
  public String getTeaserTitle() {
    String teaserTitle = super.getTeaserTitle();
    if (isBlank(teaserTitle)) {
      MarketingSpot marketingSpot = getMarketingSpot();
      if (marketingSpot != null && marketingSpot.getName() != null) {
        teaserTitle = marketingSpot.getName();
      }
    }
    return teaserTitle;
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }

  @Required
  public void setLiveContextNavigationFactory(LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  private MarketingSpot getMarketingSpot() {
    MarketingSpot marketingSpot = null;
    StoreContext storeContext = getStoreContextProvider().findContextByContent(getContent());
    if (storeContext != null && getExternalId() != null && !getExternalId().trim().isEmpty()) {
      MarketingSpotService marketingSpotService = Commerce.getCurrentConnection().getMarketingSpotService();
      if (marketingSpotService != null) {
        marketingSpot = marketingSpotService.withStoreContext(storeContext).findMarketingSpotById(getExternalId());
      }
    }

    return marketingSpot;
  }

}
