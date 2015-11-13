package com.coremedia.livecontext.ecommerce.ibm.catalog;

import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractIbmCommerceBean;
import com.coremedia.livecontext.ecommerce.ibm.common.DataMapHelper;
import com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper;
import com.coremedia.xml.Markup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Locale;


import static org.apache.commons.lang3.StringUtils.isBlank;

public class CategoryImpl extends AbstractIbmCommerceBean implements Category {

  private static final Logger LOG = LoggerFactory.getLogger(CategoryImpl.class);

  private Map<String, Object> delegate;
  private WcCatalogWrapperService catalogWrapperService;

  protected Map<String, Object> getDelegate() {
    if (delegate == null) {
      delegate = (Map<String, Object>) getCommerceCache().get(
        new CategoryCacheKey(getId(), getContext(), getCatalogWrapperService(), getCommerceCache()));
      if (delegate == null) {
        throw new NotFoundException(getId() + " (category not found in catalog)");
      }
    }
    return delegate;
  }

  @Override
  public void load() throws CommerceException {
    getDelegate();
  }

  @Override
  public void setDelegate(Object delegate) {
    this.delegate = (Map<String, Object>) delegate;
  }

  public WcCatalogWrapperService getCatalogWrapperService() {
    return catalogWrapperService;
  }

  @Required
  public void setCatalogWrapperService(WcCatalogWrapperService catalogWrapperService) {
    this.catalogWrapperService = catalogWrapperService;
  }

  @Override
  public String getReference() {
    return CommerceIdHelper.formatCategoryId(getExternalId());
  }

  @Override
  public String getExternalId() {
    return DataMapHelper.getValueForKey(getDelegate(), "identifier", String.class);
  }

  @Override
  public String getExternalTechId() {
    return DataMapHelper.getValueForKey(getDelegate(), "uniqueID", String.class);
  }

  @Override
  public String getName() {
    return DataMapHelper.getValueForKey(getDelegate(), "name", String.class);
  }

  @Override
  public Markup getShortDescription() {
    String shortDescription = DataMapHelper.getValueForKey(getDelegate(), "shortDescription", String.class);
    return toRichtext(shortDescription);
  }

  @Override
  public Markup getLongDescription() {
    String longDescription = DataMapHelper.getValueForKey(getDelegate(), "longDescription", String.class);
    return toRichtext(longDescription);
  }

  @Override
  public String getThumbnailUrl() {
    return getAssetUrlProvider().getImageUrl(DataMapHelper.getValueForKey(getDelegate(), "thumbnail", String.class), true);
  }

  @Override
  @Nonnull
  public List<Category> getChildren() throws CommerceException {
    return getCatalogService().findSubCategories(this);
  }

  @Override
  @Nonnull
  public List<Product> getProducts() throws CommerceException {
    return getCatalogService().findProductsByCategory(this);
  }

  @Override
  @Nullable
  public Category getParent() throws CommerceException {
    String parentCatalogGroupID = DataMapHelper.getValueForKey(getDelegate(), "parentCatalogGroupID[0]", String.class);
    if (parentCatalogGroupID != null && !parentCatalogGroupID.isEmpty() && !parentCatalogGroupID.equals("-1")){
      return (Category) getCommerceBeanFactory().createBeanFor(
        CommerceIdHelper.formatCategoryTechId(parentCatalogGroupID), getContext());
    }
    return null;
  }

  @Override
  @Nonnull
  public List<Category> getBreadcrumb() throws CommerceException {
    List<Category> result = new ArrayList<>();
    Category parent = getParent();
    if (parent != null) {
      result.addAll(parent.getBreadcrumb());
    }
    result.add(this);
    return result;
  }

  @Override
  @Nonnull
  public String getSeoSegment() {

   String localizedSeoSegment = DataMapHelper.getValueForKey(getDelegate(), "seo_token_ntk", String.class);
    if (isBlank(localizedSeoSegment)) {
      if (getDefaultLocale() == null) {
       LOG.warn("Default locale does not set for commerce beans.");
     }
      if (!getLocale().equals(getDefaultLocale())) {
       LOG.info("Category {} does not have a seo segment for locale {}. Return the seo segment of the category for the default locale {}.",
               getName(), getLocale(), getDefaultLocale());
        StoreContext newStoreContext = StoreContextHelper.getCurrentContextFor(getDefaultLocale());
         Category master = getCatalogService().withStoreContext(newStoreContext).findCategoryById(CommerceIdHelper.formatCategoryId(getExternalId()));
          if (master!=null && !equals(master)) {
            localizedSeoSegment = master.getSeoSegment();
        }
      }
    }

    if (isBlank(localizedSeoSegment)) {
      throw new IllegalStateException("Either category " + getName() + " (" + getExternalId() + ") or its master has to have a seo segment.");
    }

    return localizedSeoSegment;
  }

  @Override
  public String getMetaDescription() {
    return DataMapHelper.getValueForKey(getDelegate(), "metaDescription", String.class);
  }

  @Override
  public String getMetaKeywords() {
    return DataMapHelper.getValueForKey(getDelegate(), "metaKeyword", String.class);
  }

  @Override
  public String getTitle() {
    return DataMapHelper.getValueForKey(getDelegate(), "title", String.class);
  }

  @Override
  public String getDisplayName() {
    return getExternalId();
  }
}
