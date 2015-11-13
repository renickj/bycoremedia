package com.coremedia.livecontext.ecommerce.toko.catalog;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.livecontext.ecommerce.pricing.PriceService;
import com.coremedia.livecontext.ecommerce.toko.common.AbstractTokoCommerceBean;
import com.coremedia.livecontext.ecommerce.toko.common.CommerceIdHelper;
import com.coremedia.xml.Markup;
import org.codehaus.jackson.JsonNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Todo toko
 */
public class ProductImpl extends AbstractTokoCommerceBean implements Product {

  protected static final String DEFAULT_IMAGE_URL_KEY = "defaultImageUrl";
  protected static final String THUMBNAIL_IMAGE_URL_KEY = "thumbnailImageUrl";

  private PriceService priceService;

  private Category category;

  public void setPriceService(PriceService priceService) {
    this.priceService = priceService;
  }

  @Override
  public String getExternalId() {
    return getTextValue(ID_KEY);
  }

  @Override
  public String getExternalTechId() {
    return getExternalId();
  }

  @Override
  public String getName() {
    return getTextValue(NAME_KEY);
  }

  @Override
  public Markup getShortDescription() {
    return toRichtext(getTextValue(SHORT_DESCRIPTION_KEY));
  }

  @Override
  public Markup getLongDescription() {
    return getShortDescription();
  }

  @Override
  public String getThumbnailUrl() {
    return getTextValue(THUMBNAIL_IMAGE_URL_KEY); // todo toko: relative vs. absolute URLs
  }

  @Override
  public String getDefaultImageUrl() {
    return getTextValue(DEFAULT_IMAGE_URL_KEY); // todo toko: relative vs. absolute URLs
  }

  @Nonnull
  @Override
  public String getSeoSegment() {
    return getExternalId();
  }

  @Override
  public String getMetaDescription() {
    return getName();
  }

  @Override
  public String getMetaKeywords() {
    return getName();
  }

  @Override
  public String getTitle() {
    return getTextValue(TITLE_KEY);
  }

  @Override
  public String getReference() {
    return CommerceIdHelper.formatCategoryId(getExternalId());
  }

  @Override
  public Locale getLocale() {
    return Locale.US;
  }

  @Override
  public Category getCategory() {
    return doGetCategory();
  }

  @Override
  public List<Category> getCategories() {
    Category category = doGetCategory();
    return category==null ? Collections.<Category>emptyList() : Collections.singletonList(category);
  }

  @Override
  public Currency getCurrency() {
    return Currency.getInstance("USD");
  }

  @Override
  public BigDecimal getListPrice() {
    return priceService.findListPriceForProduct(getId());
  }

  @Override
  public BigDecimal getOfferPrice() {
    return priceService.findOfferPriceForProduct(getId());
  }

  @Override
  public String getDefaultImageAlt() {
    return getName();
  }

  @Nonnull
  @Override
  public List<String> getVariantAxisNames() {
    //todo toko
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants() {
    //todo toko
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants(@Nullable List<VariantFilter> filters) {
    //todo toko
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants(@Nullable VariantFilter filter) {
    //todo toko
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public Map<ProductVariant, AvailabilityInfo> getAvailabilityMap() {
    //todo toko
    return Collections.emptyMap();
  }

  @Override
  public float getTotalStockCount() {
    return 10;
  }

  @Override
  public boolean isAvailable() {
    return true;
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDefiningAttributes() {
    //todo toko
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDescribingAttributes() {
    //todo toko
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public List<Object> getAttributeValues(@Nonnull String attributeId) {
    //todo toko
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable List<VariantFilter> filters) {
    //todo toko
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable VariantFilter filter) {
    //todo toko
    return Collections.emptyList();
  }

  @Override
  public boolean isVariant() {
    return false;
  }

  @Override
  public void load() throws CommerceException {
    JsonNode delegate = catalogMock.getProductById(CommerceIdHelper.parseExternalIdFromId(getId()));
    if (delegate == null) {
      throw new NotFoundException("Commerce object not found with id: " + getId());
    }
    else {
      setDelegate(delegate);
    }
  }

  @Override
  public CatalogPicture getCatalogPicture() {
    AssetService assetService = getAssetService();
    if(null != assetService) {
      return assetService.getCatalogPicture(getDefaultImageUrl());
    }
    return new CatalogPicture("#", null);
  }

  @Override
  public Content getPicture() {
    List<Content> pictures = getPictures();
    return pictures != null && !pictures.isEmpty() ? pictures.get(0) : null;
  }

  @Override
  public List<Content> getPictures(){
    AssetService assetService = getAssetService();
    if(null != assetService) {
      return assetService.findPictures(getExternalId());
    }
    return Collections.emptyList();
  }

  @Override
  public List<Content> getDownloads() {
    return Collections.emptyList();
  }

  @Override
  public List<Content> getVisuals() {
    return Collections.emptyList();
  }


  // --- internal ---------------------------------------------------

  private Category doGetCategory() {
    if (category == null) {
      JsonNode delegate = catalogMock.getCategoryByProductId(getExternalId());
      if (delegate != null) {
        String categoryId = getIdFromJsonNode(delegate);
        if (categoryId != null) {
          category = (Category) getCommerceBeanFactory().createBeanFor(CommerceIdHelper.formatCategoryId(categoryId), getContext());
        }
      }
    }
    return category;
  }
}
