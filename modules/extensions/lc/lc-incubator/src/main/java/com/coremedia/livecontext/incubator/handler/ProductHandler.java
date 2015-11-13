package com.coremedia.livecontext.incubator.handler;

import com.coremedia.blueprint.cae.handlers.HandlerBase;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.model.Product;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.common.CatalogService;
import com.coremedia.livecontext.ecommerce.common.UserContext;
import com.coremedia.livecontext.ecommerce.common.UserContextProvider;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Set;

@Link
@RequestMapping
public class ProductHandler extends HandlerBase {


  /**
   * Zero or more alphanumeric characters, underscore or dashes.
   *
   * @deprecated Included for compatibility with previous releases.
   * This pattern is no longer used for segment names in Blueprint, because it is too restrictive for many languages.
   */
  protected static final String PATTERN_WORD = "\\w+";

  private static final String URI_PATTERN = "/product";
  private static final String LOCALE = "locale";
  private static final String SEOURL = "seoUrl";

  private static final String SEO_URI_PATTERN =
          "/product" + "/{" + LOCALE + ":" + PATTERN_WORD + "}" +
                      "/{" + SEOURL + ":" + "[\\w[äöü]\\-]+" + "}";

  private CatalogService catalogService;
  private StoreContextProvider storeContextProvider;
  private UserContextProvider userContextProvider;
  private SitesService sitesService;
  private SettingsService settingsService;

  @Required
  public void setCatalogService(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @Required
  public void setStoreContextProvider(StoreContextProvider storeContextProvider) {
    this.storeContextProvider = storeContextProvider;
  }

  @Required
  public void setUserContextProvider(UserContextProvider userContextProvider) {
    this.userContextProvider = userContextProvider;
  }

  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }

  @RequestMapping(value = URI_PATTERN)
  public ModelAndView handleRequest(@RequestParam(value = "code", required = false) String code,
                                    @RequestParam(value = "seoUrl", required = false) String seoUrl,
                                    @RequestParam(value = "locale", required = false) String locale,
                                    @RequestParam(value = "currency", required = false) String currency,
                                    @RequestParam(value = "user", required = false) String user,
                                    @RequestParam(value = "workspace", required = false) String workspace,
                                    HttpServletRequest request, HttpServletResponse response) {

    Product product = null;

    //Todo: find the store context by blueprint context
    Site currentSite = findPerfectChefSite();
    StoreContext storeContext = storeContextProvider.findContextBySite(currentSite);

    if (locale != null) {
      storeContext.put("locale", Locale.forLanguageTag(locale));
    }
    if (currency != null) {
      storeContext.put("currency", currency);
    }
    storeContext.setWorkspaceId(workspace);
    storeContextProvider.setCurrentContext(storeContext);

    //Todo: user context is recreated with each session.
    // In a real-live scenario the user context would be stored and reused via httpSessions.
    if (user != null) {
      UserContext userContext = userContextProvider.createContext(user);
      userContextProvider.setCurrentContext(userContext);
    }

    if (StringUtils.isNotEmpty(seoUrl)) {
      product = catalogService.findProductBySeoSegment(seoUrl, storeContext);
    } else if (StringUtils.isNotEmpty(code)) {
      product = catalogService.findProductByExternalId(code, storeContext);
    }

    ModelAndView model = HandlerHelper.createModel(product);
    // providing the site is only required for vanilla pages to retrieve livecontext settings
    model.addObject("displayAvailability", settingsService.setting("livecontext.displayAvailability", Boolean.class, findPerfectChefSite().getSiteRootDocument()));
    return model;
  }

  @RequestMapping(value = SEO_URI_PATTERN)
  public ModelAndView handleSeoURI(@PathVariable(LOCALE) String locale,
                                   @PathVariable(SEOURL) String seoUrl,
                                   @RequestParam(value = "user", required = false) String user,
                                   @RequestParam(value = "workspace", required = false) String workspace,
                                   HttpServletRequest requestServlet, HttpServletResponse responseServlet) {
    return handleRequest(null, seoUrl, locale, null, user, workspace, requestServlet, responseServlet);
  }

  @Link(type = Product.class, parameter = "vanilla")
  public String buildUrl(Product product) {
    return "/product/" + product.getLocale().getLanguage() + "/" + product.getSeoSegment();
  }

  private Site findPerfectChefSite() {
    Set<Site> sites = sitesService.getSites();
    for (Site site : sites) {
      if(site.getName().equals("PerfectChef") || site.getName().equals("en")) {
        return site;
      }
    }
    return null;
  }
}
