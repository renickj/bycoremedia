package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.cae.contentbeans.CMTeasableImpl;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.cae.aspect.Aspect;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import com.coremedia.xml.Markup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.xml.MarkupUtil.isEmptyRichtext;


public class CMProductTeaserImpl extends CMTeasableImpl implements CMProductTeaser {
  private static final Logger LOG = LoggerFactory.getLogger(CMProductTeaserImpl.class);

  private static final String SHOP_NOW_POLICY = "shopNow";
  private static final String SHOP_NOW_POLICY_ENABLED = "enabled";
  private static final String SHOP_NOW_POLICY_DISABLED = "disabled";

  private LiveContextNavigationFactory liveContextNavigationFactory;

  /**
   * Returns the value of the document property {@link #MASTER}.
   *
   * @return a list of {@link CMProductTeaser} objects
   */
  @Override
  public CMProductTeaser getMaster() {
    return (CMProductTeaser) super.getMaster();
  }

  @Override
  public Map<Locale, ? extends CMProductTeaser> getVariantsByLocale() {
    return getVariantsByLocale(CMProductTeaser.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<? extends CMProductTeaser> getLocalizations() {
    return (Collection<? extends CMProductTeaser>) super.getLocalizations();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Map<String, ? extends Aspect<? extends CMProductTeaser>> getAspectByName() {
    return (Map<String, ? extends Aspect<? extends CMProductTeaser>>) super.getAspectByName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends Aspect<? extends CMProductTeaser>> getAspects() {
    return (List<? extends Aspect<? extends CMProductTeaser>>) super.getAspects();
  }

  @Override
  public Product getProduct() {
    StoreContext storeContext = getStoreContextProvider().findContextByContent(this.getContent());
    return getCatalogService().withStoreContext(storeContext).findProductById(getExternalId());
  }

  @Override
  public boolean isShopNowEnabled(CMContext context) {
    String shopNowPolicy = getShopNowPolicy(getContent());
    if (SHOP_NOW_POLICY_DISABLED.equals(shopNowPolicy)) {
      return false;
    }
    if (SHOP_NOW_POLICY_ENABLED.equals(shopNowPolicy)) {
      return true;
    }

    shopNowPolicy = getShopNowPolicy(context.getContent());
    return !SHOP_NOW_POLICY_DISABLED.equals(shopNowPolicy);
  }

  private String getShopNowPolicy(Content bean) {
    return getSettingsService().setting(SHOP_NOW_POLICY, String.class, bean);
  }

  @Override
  public String getTeaserTitle() {
    String tt = getContent().getString(TEASER_TITLE);
    //fetch the product name for the teaser title in case of empty teaser title
    if (tt == null || tt.trim().length() == 0) {
      try {
        Product product = getProduct();
        if (product != null) {
          tt = product.getName();
        }
      } catch (CommerceException ce) {
        LOG.debug("Could not load product with id: " + getExternalId(), ce);
      }
    }

    //if the teaser title is still empty then use the super class behavior
    if (tt == null || tt.trim().length() == 0) {
      tt = super.getTeaserTitle();
    }
    return tt;
  }

  @Override
  public Markup getTeaserText() {
    Markup tt = getMarkup(TEASER_TEXT);
    //fetch the product short description for the teaser text in case of empty teaser text
    if (isEmptyRichtext(tt, true)) {
      try {
        Product product = getProduct();
        if (product != null) {
          tt = product.getShortDescription();
        }
      } catch (CommerceException ce) {
        LOG.debug("Could not load product with id: " + getExternalId(), ce);
      }
    }

    //if the teaser text is still empty then use the super class behavior
    if (isEmptyRichtext(tt, true)) {
      tt = super.getTeaserText();
    }

    return tt;
  }

  /**
   * Returns the underlying Product in this content's site.
   * <p>
   * You cannot build links for a Product, since a Product can occur in multiple
   * sites and is thus not unique for link building.
   * Use a {@link ProductInSite} for link building.
   *
   * @return a ProductInSite or null if product or site cannot be determined.
   */
  @Override
  public ProductInSite getProductInSite() {
    ProductInSite result = null;
    Site site = getSitesService().getContentSiteAspect(getContent()).getSite();
    Product product = getProduct();
    if (product != null && site != null) {
      result = liveContextNavigationFactory.createProductInSite(getProduct(), site.getId());
    }
    return result;
  }

  @Required
  public void setLiveContextNavigationFactory(LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  @Override
  public String getExternalId() {
    String externalId = getContent().getString(EXTERNAL_ID);
    return externalId==null ? "" : externalId.trim();
  }

  protected static CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }

  protected static StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }
}
