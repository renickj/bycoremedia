package com.coremedia.livecontext.incubator.handler;

import com.coremedia.blueprint.cae.handlers.HandlerBase;
import com.coremedia.livecontext.ecommerce.model.Category;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.common.CatalogService;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

@Link
@RequestMapping
public class CategoryHandler extends HandlerBase {
  private static final Logger LOG = LoggerFactory.getLogger(CategoryHandler.class);

  private static final String LOCALE = "locale";
  private static final String CATEGORY_ID = "categoryId";
  private static final String SEO_SEGMENT = "seoSegment";
  private static final String DEPTH_REQUEST_PARAM = "depth";
  private static final String PATTERN_WORD = "\\w+";
  private static final String PATTERN_SEGMENTS = ".+?";

  private static final String CATEGORY_URI_PATTERN =
          "/category" + "/{" + LOCALE + ":" + PATTERN_WORD + "}" +
                  "/{" + CATEGORY_ID + ":" + PATTERN_SEGMENTS + "}";

  private static final String CATEGORY_SEO_URI_PATTERN =
          "/category/seo" + "/{" + LOCALE + ":" + PATTERN_WORD + "}" +
                  "/{" + SEO_SEGMENT + ":" + "[\\w[äöü]\\-]+" + "}";

  private static final String SUB_CATEGORIES_URI_PATTERN =
          "/category/children" + "/{" + LOCALE + ":" + PATTERN_WORD + "}" +
                  "/{" + CATEGORY_ID + ":" + PATTERN_WORD + "}";

  private static final String TOP_CATEGORIES_URI_PATTERN =
          "/category/top" + "/{" + LOCALE + ":" + PATTERN_WORD + "}";

  private CatalogService catalogService;
  private StoreContextProvider storeContextProvider;

  @Required
  public void setCatalogService(CatalogService catalogService) {
    this.catalogService = catalogService;
  }

  @Required
  public void setStoreContextProvider(StoreContextProvider storeContextProvider) {
    this.storeContextProvider = storeContextProvider;
  }

  @RequestMapping(value = CATEGORY_URI_PATTERN)
  public ModelAndView handleCategory(@PathVariable(LOCALE) String locale,
                                   @PathVariable(CATEGORY_ID) String categoryId,
                                   HttpServletRequest requestServlet, HttpServletResponse responseServlet) {
    if (StringUtils.isNotEmpty(categoryId)) {
      //Todo: find the store context by blueprint context
      StoreContext storeContext = storeContextProvider.findContextBySiteName("PerfectChef");
      if (storeContext == null) {
        storeContext = storeContextProvider.findContextBySiteName("en");
      }
      storeContext.put("locale", Locale.forLanguageTag(locale));
      Category category = catalogService.findCategoryByExternalId(decodeQuietly(categoryId), storeContext);
      if (category == null) {
        return HandlerHelper.notFound();
      }
      return HandlerHelper.createModel(category);
    }
    return HandlerHelper.badRequest();
  }

  @RequestMapping(value = CATEGORY_SEO_URI_PATTERN)
  public ModelAndView handleCategorySeo(@PathVariable(LOCALE) String locale,
                                     @PathVariable(SEO_SEGMENT) String categorySeoSegment,
                                     HttpServletRequest requestServlet, HttpServletResponse responseServlet) throws Exception {
    if (StringUtils.isNotEmpty(categorySeoSegment)) {
      //Todo: find the store context by blueprint context
      StoreContext storeContext = storeContextProvider.findContextBySiteName("PerfectChef");
      if (storeContext == null) {
        storeContext = storeContextProvider.findContextBySiteName("en");
      }
      storeContext.put("locale", Locale.forLanguageTag(locale));
      Category category = catalogService.findCategoryBySeoSegment(decodeQuietly(categorySeoSegment), storeContext);
      if (category == null) {
        return HandlerHelper.notFound();
      }
      return HandlerHelper.createModel(category);
    }
    return HandlerHelper.badRequest();
  }

  @RequestMapping(value = TOP_CATEGORIES_URI_PATTERN)
  public ModelAndView handleTopCategories(@PathVariable(LOCALE) String locale,
                                          @RequestParam(value = DEPTH_REQUEST_PARAM, required = false, defaultValue = "1") Integer depth,
                                   HttpServletRequest requestServlet, HttpServletResponse responseServlet) {

    //Todo: find the store context by blueprint context
    StoreContext storeContext = storeContextProvider.findContextBySiteName("PerfectChef");
    if (storeContext == null) {
      storeContext = storeContextProvider.findContextBySiteName("en");
    }
    storeContext.put("locale", Locale.forLanguageTag(locale));
    List<Category> categories = catalogService.findTopCategories(storeContext);
    if (categories == null) {
      return HandlerHelper.notFound();
    }
    ModelAndView modelAndView = HandlerHelper.createModelWithView(categories, "categories");
    modelAndView.addObject("depth", depth);
    return modelAndView;
  }

  @RequestMapping(value = SUB_CATEGORIES_URI_PATTERN)
  public ModelAndView handleSubCategories(@PathVariable(LOCALE) String locale,
                                   @PathVariable(CATEGORY_ID) String parentCategoryId,
                                   @RequestParam(value = DEPTH_REQUEST_PARAM, required = false, defaultValue = "1") Integer depth,
                                   HttpServletRequest requestServlet, HttpServletResponse responseServlet) {
    if (StringUtils.isNotEmpty(parentCategoryId)) {
      //Todo: find the store context by blueprint context
      StoreContext storeContext = storeContextProvider.findContextBySiteName("PerfectChef");
      if (storeContext == null) {
        storeContext = storeContextProvider.findContextBySiteName("en");
      }
      storeContextProvider.setCurrentContext(storeContext);
      Category parentCategory = catalogService.findCategoryByExternalId(parentCategoryId, storeContext);
      List<Category> categories = catalogService.findSubCategories(parentCategory, storeContext);
      if (categories == null) {
        return HandlerHelper.notFound();
      }
      ModelAndView modelAndView = HandlerHelper.createModelWithView(categories, "categories");
      modelAndView.addObject("depth", depth);
      return modelAndView;
    }
    return HandlerHelper.badRequest();
  }

  @Link(type = Category.class, parameter = "vanilla")
  public String buildUrl(Category category) {
    if (category.getSeoSegment() != null) {
      return "/category" + "/seo/" + category.getLocale() + "/" + encodeQuietly(category.getSeoSegment());
    } else {
      return "/category/" + category.getLocale() + "/" + encodeQuietly(category.getExternalId());
    }
  }

  // --- internal ---------------------------------------------------

  private String encodeQuietly(String externalId) {
    try {
      return URLEncoder.encode(externalId, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOG.error("Should not happen.", e);
      throw new Error("JVM does not support UTF-8");
    }
  }

  private String decodeQuietly(String categoryId) {
    try {
      return URLDecoder.decode(categoryId, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOG.error("Should not happen.", e);
      throw new Error("JVM does not support UTF-8");
    }
  }

}
