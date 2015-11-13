package com.coremedia.livecontext.incubator.handler;

import com.coremedia.blueprint.cae.handlers.HandlerBase;
import com.coremedia.livecontext.ecommerce.model.Product;
import com.coremedia.livecontext.ecommerce.model.SearchResult;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.common.CatalogService;
import com.coremedia.objectserver.web.HandlerHelper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Locale;

@RequestMapping
public class ProductSearchHandler extends HandlerBase {

  private CatalogService catalogService;
  private StoreContextProvider storeContextProvider;


  @RequestMapping(value = "/productsearch", method= RequestMethod.POST)
  public ModelAndView handleRequest(@RequestParam("locale") String locale,
                                    @RequestParam("searchType") String searchType,
                                    @RequestParam("searchTerm") String searchTerm,
                                    HttpServletRequest request) {
    //Todo: find the store context by blueprint context
    StoreContext storeContext = storeContextProvider.findContextBySiteName("en");
    if (locale != null) {
      storeContext.put("locale", Locale.forLanguageTag(locale));
    }
    storeContextProvider.setCurrentContext(storeContext);

    SearchResult<? extends Product> searchResult;
    if (searchType.equals("ProductVariants")){
      searchResult = catalogService.searchProductVariants(searchTerm, new Hashtable<String, String>(), storeContext);
    } else {
      searchResult = catalogService.searchProducts(searchTerm, new Hashtable<String, String>(), storeContext);
    }

    ModelAndView modelAndView = HandlerHelper.createModelWithView(searchResult.getSearchResult(), "productlist");
    modelAndView.addObject("locale", locale);
    return modelAndView;
  }

  @RequestMapping(value = "/productsearch", method=RequestMethod.GET)
  public ModelAndView handleRequest() {
    // redirect to initial search page
    ModelAndView modelAndView = HandlerHelper.createModelWithView(Collections.emptyList(), "productlist");
    return modelAndView;
  }

  @Required
  public void setCatalogService(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @Required
  public void setStoreContextProvider(StoreContextProvider storeContextProvider) {
    this.storeContextProvider = storeContextProvider;
  }
}
