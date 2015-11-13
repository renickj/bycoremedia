package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpotService;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.coremedia.rest.cap.common.represent.SuggestionRepresentation;
import com.coremedia.rest.cap.common.represent.SuggestionResultRepresentation;
import com.coremedia.rest.cap.content.SearchParameterNames;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Catalog configuration helpter as a RESTful resource.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("livecontext")
public class CatalogServiceResource {
  private static final String DEFAULT_SUGGESTIONS_LIMIT = "10";
  private static final String DEFAULT_SEARCH_LIMIT = "-1";

  private static final String SEARCH_PARAM_CATEGORY = "category";
  private static final String SEARCH_PARAM_SITE_ID = "siteId";
  private static final String SEARCH_PARAM_QUERY = "query";
  private static final String SEARCH_PARAM_LIMIT = "limit";
  private static final String SEARCH_PARAM_ORDER_BY = "orderBy";
  private static final String SEARCH_PARAM_WORKSPACE_ID = "workspaceId";

  private static final String SEARCH_PARAM_SEARCH_TYPE = "searchType";
  public static final String SEARCH_TYPE_PRODUCT_VARIANT = "ProductVariant";
  private static final String SEARCH_TYPE_MARKETING_SPOTS = "MarketingSpot";

  private CommerceConnectionInitializer commerceConnectionInitializer;

  public CatalogService getCatalogService() {
    return Commerce.getCurrentConnection().getCatalogService();
  }

  public MarketingSpotService getMarketingSpotService() {
    return Commerce.getCurrentConnection().getMarketingSpotService();
  }

  @GET
  @Path("search/{siteId:[^/]+}")
  public CatalogSearchResultRepresentation search(@PathParam(SEARCH_PARAM_SITE_ID) String siteId,
                                                  @QueryParam(SEARCH_PARAM_QUERY) String query,
                                                  @QueryParam(SEARCH_PARAM_LIMIT) @DefaultValue(DEFAULT_SEARCH_LIMIT) int limit,
                                                  @QueryParam(SEARCH_PARAM_ORDER_BY) String orderBy,
                                                  @QueryParam(SEARCH_PARAM_SEARCH_TYPE) String searchType,
                                                  @QueryParam(SEARCH_PARAM_CATEGORY) String category,
                                                  @QueryParam(SEARCH_PARAM_WORKSPACE_ID) String workspaceId) {

    StoreContext newStoreContextForSite = getStoreContext(siteId, workspaceId);
    if (newStoreContextForSite == null) {
      return null;
    }

    newStoreContextForSite.setWorkspaceId(workspaceId);

    Map<String, String> searchParams = new HashMap<>();
    if (category != null && !category.isEmpty()) {
      searchParams.put(CatalogService.SEARCH_PARAM_CATEGORYID, category);
    }
    if (limit > 0) {
      searchParams.put(CatalogService.SEARCH_PARAM_PAGESIZE, String.valueOf(limit));
    }

    SearchResult<? extends CommerceBean> searchResult;
    if (searchType != null && searchType.equals(SEARCH_TYPE_PRODUCT_VARIANT)) {
      searchResult = getCatalogService().withStoreContext(newStoreContextForSite).searchProductVariants(query, searchParams);
    } else if (searchType != null && searchType.equals(SEARCH_TYPE_MARKETING_SPOTS)) {
      if (getMarketingSpotService() == null) {
        searchResult = new SearchResult<>();
        searchResult.setSearchResult(Collections.EMPTY_LIST);
        searchResult.setTotalCount(0);
      } else {
        searchResult = getMarketingSpotService().searchMarketingSpots(query, searchParams); //TODO switch store context check
      }
    } else {// default: Product
      searchResult = getCatalogService().withStoreContext(newStoreContextForSite).searchProducts(query, searchParams);
    }

    return new CatalogSearchResultRepresentation(searchResult.getSearchResult(), searchResult.getTotalCount());
  }

  @GET
  @Path("suggestions")
  public SuggestionResultRepresentation searchSuggestions(@QueryParam(SearchParameterNames.QUERY) final String query,
                                                          @QueryParam(SearchParameterNames.LIMIT) @DefaultValue(DEFAULT_SUGGESTIONS_LIMIT) final int limit,
                                                          @QueryParam(SEARCH_PARAM_SEARCH_TYPE) final String searchType,
                                                          @QueryParam(SEARCH_PARAM_SITE_ID) String siteId,
                                                          @QueryParam(SEARCH_PARAM_CATEGORY) String category,
                                                          @QueryParam(SEARCH_PARAM_WORKSPACE_ID) String workspaceId) {
    //TODO not supported yet
    return new SuggestionResultRepresentation(new ArrayList<SuggestionRepresentation>());
  }

  protected StoreContext getStoreContext(String siteId, String workspaceId) {
    CommerceConnection connection = getConnection(siteId);
    if (connection != null) {
      StoreContext storeContext = connection.getStoreContext();
      if (storeContext != null) {
        storeContext.setWorkspaceId(workspaceId);
        return storeContext;
      }
    }
    return null;
  }

  protected CommerceConnection getConnection(String siteId) {
    commerceConnectionInitializer.init(siteId);
    return Commerce.getCurrentConnection();
  }

  @Required
  public void setCommerceConnectionInitializer(CommerceConnectionInitializer commerceConnectionInitializer) {
    this.commerceConnectionInitializer = commerceConnectionInitializer;
  }
}
