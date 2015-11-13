package com.coremedia.ecommerce.studio.rest;

import com.coremedia.cap.content.Content;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product representation for JSON.
 */
public class ProductRepresentation extends CommerceBeanRepresentation {

  private String name;
  private String shortDescription;
  private String longDescription;
  private String thumbnailUrl;
  private Category category;
  private Store store;
  private BigDecimal offerPrice;
  private BigDecimal listPrice;
  private String currency;
  private List<ProductVariant> variants;
  private List<Content> visuals;
  private List<Content> pictures;
  private List<Content> downloads;
  private List<ProductAttribute> describingAttributes;

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getName() {
    return name;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getShortDescription() {
    return shortDescription;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getLongDescription() {
    return longDescription;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getThumbnailUrl(){
    return thumbnailUrl;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Category getCategory() {
    return category;
  }

  public BigDecimal getOfferPrice() {
    return offerPrice;
  }

  public BigDecimal getListPrice() {
    return listPrice;
  }

  public String getCurrency() {
    return currency;
  }

  public List<ProductVariant> getVariants() {
    return variants;
  }

  public List<Content> getVisuals() {
    return visuals;
  }

  public List<Content> getPictures() {
    return pictures;
  }

  public List<Content> getDownloads() {
    return downloads;
  }

  public List<ProductAttribute> getDescribingAttributes() {
    return describingAttributes;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public void setLongDescription(String longDescription) {
    this.longDescription = longDescription;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public void setOfferPrice(BigDecimal offerPrice) {
    this.offerPrice = offerPrice;
  }

  public void setListPrice(BigDecimal listPrice) {
    this.listPrice = listPrice;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public void setVariants(List<ProductVariant> variants) {
    this.variants = variants;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public void setVisuals(List<Content> visuals) {
    this.visuals = visuals;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public void setPictures(List<Content> pictures) {
    this.pictures = pictures;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public void setDownloads(List<Content> downloads) {
    this.downloads = downloads;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public void setDescribingAttributes(List<ProductAttribute> describingAttributes) {
    this.describingAttributes = describingAttributes;
  }
}
