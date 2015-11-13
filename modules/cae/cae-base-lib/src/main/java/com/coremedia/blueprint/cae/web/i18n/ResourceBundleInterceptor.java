package com.coremedia.blueprint.cae.web.i18n;

import com.coremedia.blueprint.base.util.ContentCacheKey;
import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.blueprint.cae.constants.RequestAttributeConstants;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.common.CapStructHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.objectserver.web.HandlerHelper;
import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.google.common.collect.Lists.transform;

/**
 * A handler interceptor that makes a {@link Page page's} resource bundle and locale available to the template engine so
 * that <code>&lt;fmt:message&gt;</code>, <code>&lt;spring:message&gt;</code> make use of the bundle.
 */
public class ResourceBundleInterceptor extends HandlerInterceptorAdapter {

  private Cache cache;

  // --- configure --------------------------------------------------

  @Required
  public void setCache(Cache cache) {
    this.cache = cache;
  }


  // --- features ---------------------------------------------------

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    Page page = getPage(modelAndView, request);
    if (page != null) {
      // todo questionable that page.content can influence the localization of the whole page - this should be a sole property of the navigation context?
      registerResourceBundleForPage(page, request, response);
    } else {
      if (modelAndView != null) {
        Navigation navigation = NavigationLinkSupport.getNavigation(modelAndView.getModelMap());
        if (navigation != null) {
          registerResourceBundle(resourceBundle(navigation), navigation.getLocale(), request, response);
        }
      }
    }
  }

  /**
   * Register the resource bundle configured in the {@link Page Page's } settings for the given request / response
   */
  public void registerResourceBundleForPage(Page page, HttpServletRequest request, HttpServletResponse response) {
    Locale locale = page.getLocale();
    ResourceBundle bundle = resourceBundle(page.getNavigation());
    registerResourceBundle(bundle, locale, request, response);
  }

  // --- internal ---------------------------------------------------

  private static void registerResourceBundle(ResourceBundle bundle, Locale locale, HttpServletRequest request, HttpServletResponse response) {


    // --- 1.) registering locale to be used by <spring:message> etc.
    // This has been copied from org.springframework.web.servlet.i18n.LocaleChangeInterceptor
    // Note that this works only for some LocaleResolver implementations.
    LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
    if (localeResolver == null) {
      // Is this request not sent by a dispatcher servlet?
      throw new IllegalStateException("No LocaleResolver found");
    }
    localeResolver.setLocale(request, response, locale);

    // --- 2.) registering bundle to be used by <spring:message> etc.
    RequestMessageSource.setMessageSource(new FixedResourceBundleMessageSource(bundle), request);

    // --- 3.) registering bundle/locale to be used by <fmt:message>
    LocalizationContext localizationContext = new LocalizationContext(bundle, locale);
    Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, localizationContext);
  }

  /**
   * Returns the page that is either the self object of the modelAndView
   * or the page from the request attribute.
   *
   * @param modelAndView the {@link org.springframework.web.servlet.ModelAndView}
   * @param request      the {@link javax.servlet.http.HttpServletRequest}
   * @return the page object or <code>null</code> if no page could be found.
   */
  private static Page getPage(ModelAndView modelAndView, HttpServletRequest request) {
    // try to get Page via "self" from ModelAndView
    Object self = modelAndView != null ? HandlerHelper.getRootModel(modelAndView) : null;
    // if self is in in the model or not a page (e.g. direct request to a CMLinkable) still try to get the page
    // from a well-known request attribute
    if( self instanceof Page) {
      return (Page) self;
    }
    else {

      Page page = null;
      if( modelAndView != null ) {
        page = RequestAttributeConstants.getPage(modelAndView);
      }
      if( page == null ) {
        // note: the page should be set via RequestAttributeConstants#setPage(ModelAndView) and NOT via
        // RequestAttributeConstants#setPage(HttpServletRequest)
        page = RequestAttributeConstants.getPage(request);
      }
      return page;
    }
  }


  // --- tmp --------------------------------------------------------

  // TODO: separate ResourceBundles from Settings
  // This is a backward compatible hack to preserve the old behaviour for now while
  // getting rid of Page.getSettingsStruct().
  // Supports only linked settings at the root channel.
  // Less powerful than the old implementation, sufficient for our sample content.
  // Dirty: ResourceBundles and Settings are still mixed up in the linkedSettings property.
  // To be replaced by a real I18N concept as soon as available.
  // When you revisit this, also look at the usage in WebflowHandlerBase, which does not
  // look exactly clean.

  private ResourceBundle resourceBundle(Navigation navigation) {
    CMNavigation rootNavigation = navigation.getRootNavigation();
    return cache.get(new ResourceBundleCacheKey(rootNavigation.getContent()));
  }

  private static class ResourceBundleCacheKey extends ContentCacheKey<ResourceBundle> {
    private static final String CMLINKABLE_LOCALSETTINGS = "localSettings";
    private static final String CMLINKABLE_LINKEDSETTINGS = "linkedSettings";

    private ResourceBundleCacheKey(Content content) {
      super(content);
    }

    @Override
    public ResourceBundle evaluate(Cache cache) throws Exception {
      Content content = getContent();
      Struct struct = content.getStruct(CMLINKABLE_LOCALSETTINGS);
      if(content.getType().getDescriptor(CMLINKABLE_LINKEDSETTINGS) != null) {
        List<Struct> structList = transform(content.getLinks(CMLINKABLE_LINKEDSETTINGS), new ContentToStruct());
        struct = StructUtil.mergeStructList(struct, structList);
      }
      if (struct==null) {
        struct = content.getRepository().getConnection().getStructService().emptyStruct();
      }
      return CapStructHelper.asResourceBundle(struct);
    }
  }

  private static class ContentToStruct implements Function<Content, Struct> {
    private static final String CMSETTINGS_SETTINGS = "settings";

    @Override
    public Struct apply(Content input) {
      if(input != null) {
        final Struct struct = input.getStruct(CMSETTINGS_SETTINGS);
        if(struct != null) {
          return struct;
        }
      }
      return null;
    }
  }

  private static class EmptyResourceBundle extends ResourceBundle {
    @Override
    protected Object handleGetObject(String key) {
      return null;
    }

    @Override
    public Enumeration<String> getKeys() {
      return Collections.emptyEnumeration();
    }
  }

}
