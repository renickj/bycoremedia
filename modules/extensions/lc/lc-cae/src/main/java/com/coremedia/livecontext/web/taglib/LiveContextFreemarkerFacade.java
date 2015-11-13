package com.coremedia.livecontext.web.taglib;

import com.coremedia.livecontext.context.ProductInSite;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.fragment.FragmentContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.navigation.LiveContextNavigationFactory;
import com.coremedia.objectserver.view.freemarker.FreemarkerUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.Currency;
import java.util.Locale;

/**
 * A Facade for LiveContext utility functions used by FreeMarker templates.
 */
public class LiveContextFreemarkerFacade {
  private LiveContextNavigationFactory liveContextNavigationFactory;
  private String secureScheme;

  public String formatPrice(Object amount, Currency currency, Locale locale) {
    return FormatFunctions.formatPrice(amount, currency, locale);
  }

  public ProductInSite createProductInSite(Product product) {
    return liveContextNavigationFactory.createProductInSite(product, getStoreContextProvider().getCurrentContext().getSiteId());
  }

  public FragmentContext fragmentContext() {
    return FragmentContextProvider.getFragmentContext(FreemarkerUtils.getCurrentRequest());
  }

  public String getSecureScheme() {
    return secureScheme;
  }

  public void setSecureScheme(String secureScheme) {
    this.secureScheme = secureScheme;
  }

  @Required
  public void setLiveContextNavigationFactory(@Nonnull LiveContextNavigationFactory liveContextNavigationFactory) {
    this.liveContextNavigationFactory = liveContextNavigationFactory;
  }

  public StoreContextProvider getStoreContextProvider() {
    return Commerce.getCurrentConnection().getStoreContextProvider();
  }
}
