package com.coremedia.ecommerce.studio.rest;

import com.coremedia.cap.content.Content;
import com.coremedia.ecommerce.studio.rest.model.Store;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product Variant representation for JSON.
 */
public class ProductVariantRepresentation extends CommerceBeanRepresentation {

  private String name;
  private String shortDescription;
  private String longDescription;
  private String externalId;
  private String externalTechId;
  private String thumbnailUrl;
  private Product parent;
  private Category category;
  private String previewUrl;
  private Store store;
  private BigDecimal offerPrice;
  private BigDecimal listPrice;
  private String currency;
  private List<Content> visuals;
  private List<Content> pictures;
  private List<Content> downloads;
  private List<ProductAttribute> definingAttributes;
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
  public String getExternalId() {
    return externalId;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getExternalTechId(){
    return externalTechId;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getThumbnailUrl(){
    return thumbnailUrl;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getPreviewUrl() {
    return previewUrl;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Category getCategory() {
    return category;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Product getParent() {
    return parent;
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

  public List<Content> getVisuals() {
    return visuals;
  }

  public List<Content> getPictures() {
    return pictures;
  }

  public List<Content> getDownloads() {
    return downloads;
  }

  public List<ProductAttribute> getDefiningAttributes() {
    return definingAttributes;
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

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public void setExternalTechId(String externalTechId) {
    this.externalTechId = externalTechId;
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public void setPreviewUrl(String url) {
    this.previewUrl = url;
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

  public void setParent(Product parent) {
    this.parent = parent;
  }

  public void setDefiningAttributes(List<ProductAttribute> definingAttributes) {
    this.definingAttributes = definingAttributes;
  }

  public void setDescribingAttributes(List<ProductAttribute> describingAttributes) {
    this.describingAttributes = describingAttributes;
  }
}
