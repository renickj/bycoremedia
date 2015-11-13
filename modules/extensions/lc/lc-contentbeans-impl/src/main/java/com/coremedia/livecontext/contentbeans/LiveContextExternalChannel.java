package com.coremedia.livecontext.contentbeans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper.getCurrentCommerceIdProvider;

/**
 * A LiveContextNavigation which is backed by a CMExternalChannel content
 * in the CMS repository.
 */
public class LiveContextExternalChannel extends CMExternalChannelBase implements LiveContextNavigation {
  private LiveContextNavigationFactory liveContextNavigationFactory;
  private Site site;
  private static final Logger LOG = LoggerFactory.getLogger(LiveContextExternalChannel.class);

  @Override
  public Category getCategory() {
    if (isCatalogPage()) {
      StoreContext storeContext = getStoreContextProvider().findContextByContent(this.getContent());
      Category category = getCatalogService().withStoreContext(storeContext).findCategoryById(
              getCurrentCommerceIdProvider().formatCategoryId(getExternalId()));
      if (category == null) {
        LOG.debug("Content #" + getContent().getId()
                + ": No category found for externalId: " + getExternalId() + " - maybe the category only exists in a Workspace?");
        throw new NotFoundException("Content #" + getContent().getId() + ": No category found for externalId: " + getExternalId());
      }
      return category;
    }
    return null;
  }

  @Nonnull
  @Override
  public Site getSite() {
    if(site == null) {
      site = getSitesService().getContentSiteAspect(getContent()).getSite();
      if (site == null) {
        throw new IllegalStateException("A " + LiveContextExternalChannel.class.getName() + " must belong to a site " +
                "but content[" + getContent().getId() + "] does not. ");
      }
    }
    return site;
  }
  
  @Nonnull
  public String getExternalId() {
    String externalId = getContent().getString(EXTERNAL_ID);
    return externalId==null ? "" : externalId.trim();
  }

  @Override
  protected List<Linkable> getExternalChildren(Site site) {
    List<Linkable> externalChildren = new ArrayList<>();
    if (isCatalogPage()) {
      List<Category> subCategories = findSubCategories();
      for (Category subCategory : subCategories) {
        externalChildren.add(liveContextNavigationFactory.createNavigation(subCategory, site));
      }
    }
    return externalChildren;
  }

  private List<Category> findSubCategories() {
    Category category = getCatalogService().findCategoryById(getCurrentCommerceIdProvider().formatCategoryId(getExternalId()));
    if (category != null) {
      return getCatalogService().findSubCategories(category);
    }
    return Collections.emptyList();
  }

  @Required
  public void setLiveContextNavigationFactory(LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  private StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }
}
