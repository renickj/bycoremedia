package com.coremedia.livecontext.ecommerce.toko.catalog;

import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.NotFoundException;
import com.coremedia.livecontext.ecommerce.toko.common.AbstractTokoCommerceBean;
import com.coremedia.livecontext.ecommerce.toko.common.CommerceIdHelper;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.codehaus.jackson.JsonNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryImpl extends AbstractTokoCommerceBean implements Category {
  private static final String EMPTY_RICHTEXT_STR = "<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><p/></div>";
  protected static final Markup EMPTY_RICHTEXT = MarkupFactory.fromString(EMPTY_RICHTEXT_STR);

  private Category parent;

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
    return EMPTY_RICHTEXT;
  }

  @Override
  public String getThumbnailUrl() {
    //todo toko
    return null;
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
  public String getDisplayName() {
    return getExternalId();
  }

  @Override
  public String getReference() {
    return CommerceIdHelper.formatCategoryId(getExternalId());
  }

  @Override
  public Locale getLocale() {
    return Locale.US;
  }

  @Nonnull
  @Override
  public List<Category> getChildren() throws CommerceException {
    return getCatalogService().findSubCategories(this);
  }

  @Nonnull
  @Override
  public List<Product> getProducts() throws CommerceException {
    return getCatalogService().findProductsByCategory(this);
  }

  @Nullable
  @Override
  public Category getParent() throws CommerceException {
    if (parent == null) {
      JsonNode delegate = catalogMock.getParentCategory(getExternalId());
      if (delegate != null) {
        String categoryId = getIdFromJsonNode(delegate);
        if (categoryId != null) {
          parent = (Category) getCommerceBeanFactory().createBeanFor(CommerceIdHelper.formatCategoryId(categoryId), getContext());
        }
      }
    }
    return parent;
  }

  @Nonnull
  @Override
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
  public void load() throws CommerceException {
    JsonNode delegate = catalogMock.getCategoryById(CommerceIdHelper.parseExternalIdFromId(getId()));
    if (delegate == null) {
      throw new NotFoundException("Commerce object not found with id: " + getId());
    }
    else {
      setDelegate(delegate);
    }
  }
}
