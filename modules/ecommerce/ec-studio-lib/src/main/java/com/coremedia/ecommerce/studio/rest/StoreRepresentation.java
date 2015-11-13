package com.coremedia.ecommerce.studio.rest;

import com.coremedia.ecommerce.studio.rest.model.Catalog;
import com.coremedia.ecommerce.studio.rest.model.ChildRepresentation;
import com.coremedia.ecommerce.studio.rest.model.Contracts;
import com.coremedia.ecommerce.studio.rest.model.Marketing;
import com.coremedia.ecommerce.studio.rest.model.Segments;
import com.coremedia.ecommerce.studio.rest.model.Workspaces;
import com.coremedia.livecontext.ecommerce.common.CommerceObject;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Store representation for JSON.
 */
public class StoreRepresentation extends AbstractCatalogRepresentation {

  private StoreContext context;

  private String vendorVersion;
  private String vendorUrl;
  private String vendorName;

  private boolean marketingEnabled = false;

  public void setContext(StoreContext context) {
    this.context = context;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getName() {
    return context.getStoreName();
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public String getStoreId() {
    return context.getStoreId();
  }

  public List<CommerceObject> getTopLevel() {
    List<CommerceObject> topLevel = new ArrayList<>();
    if(isMarketingEnabled()) {
      topLevel.add(getMarketing());
    }

    topLevel.add(getCatalog());
    return topLevel;
  }

  public Catalog getCatalog() {
    return new Catalog(context);
  }

  public Marketing getMarketing() {
    return new Marketing(context);
  }

  public Segments getSegments() {
    return new Segments(context);
  }

  public Contracts getContracts() {
    return new Contracts(context);
  }

  public Workspaces getWorkspaces() {
    return new Workspaces(context);
  }

  public Map<String, ChildRepresentation> getChildrenByName() {
    Map<String, ChildRepresentation> result = new LinkedHashMap<>();

    if(isMarketingEnabled()) {
      Marketing marketing = getMarketing();
      ChildRepresentation child = new ChildRepresentation();
      child.setDisplayName("store-marketing");
      child.setChild(marketing);
      result.put("store-marketing", child);
    }

    Catalog catalog = getCatalog();
    ChildRepresentation child = new ChildRepresentation();
    child.setChild(catalog);
    child.setDisplayName("store-catalog");
    result.put("store-catalog", child);
    return result;
  }

  public String getVendorUrl() {
    return vendorUrl;
  }

  public void setVendorUrl(String vendorUrl) {
    this.vendorUrl = vendorUrl;
  }

  public String getVendorVersion() {
    return vendorVersion;
  }

  public String getVendorName() {
    return vendorName;
  }

  public void setVendorName(String vendorName) {
    this.vendorName = vendorName;
  }

  public void setVendorVersion(String vendorVersion) {
    this.vendorVersion = vendorVersion;
  }

  public boolean isMarketingEnabled() {
    return marketingEnabled;
  }

  public void setMarketingEnabled(boolean marketingEnabled) {
    this.marketingEnabled = marketingEnabled;
  }

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  public Map<String, String> getWcsTimeZone() {
    return (Map<String, String>) context.get("wcsTimeZone");
  }
}
