package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CONFIG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.STORE_NAME;

public class StoreContextProviderMock implements StoreContextProvider {

  @Override
  @Nullable
  public StoreContext findContextBySiteName(@Nonnull String siteName) {
    if (siteName.equals("Helios")){
      return createContext();
    }
    else {
      throw new InvalidContextException("Could not find context for " + siteName);
    }
  }

  @Nullable
  @Override
  public StoreContext findContextBySiteId(@Nonnull String siteId) throws InvalidContextException {
    if (siteId.equals("Helios")){
      return createContext();
    }
    else {
      throw new InvalidContextException("Could not find context for " + siteId);
    }
  }

  @Override
  @Nullable
  public StoreContext findContextBySite(Site site) throws InvalidContextException {
    if ("Helios".equals(site.getName())) {
      return createContext();
    } else {
      throw new InvalidContextException("Could not find context for " + site.getName());
    }
  }

  @Override
  @Nullable
  public StoreContext findContextByContent(@Nullable Content content) {
    return createContext();
  }

  @Nullable
  @Override
  public StoreContext createContext(@Nonnull Site site) throws InvalidContextException {
    return createContext();
  }

  private StoreContext createContext(){
    return createContext("myConfigId", "10001", "aurora", "10001", "en_US", "USD");
  }

  @Override
  public void setCurrentContext(StoreContext context) throws InvalidContextException {
    setCurrentContext(context);
  }

  @Override
  public StoreContext getCurrentContext() {
    return null;
  }


  private StoreContext createContext(String configId, String storeId, String storeName, String catalogId, String locale, String currency)
          throws InvalidContextException {

    StoreContext context = StoreContextBuilder.create().build();
    if (configId != null) {
      if (StringUtils.isBlank(configId)) {
        throw new InvalidContextException("configId has wrong format: \"" + storeId + "\"");
      }
      context.put(CONFIG_ID, configId);
    }
    if (storeId != null) {
      if (StringUtils.isBlank(storeId)) {
        throw new InvalidContextException("storeId has wrong format: \"" + storeId + "\"");
      }
      context.put(STORE_ID, storeId);
    }
    if (storeName != null) {
      if (StringUtils.isBlank(storeName)) {
        throw new InvalidContextException("storeName has wrong format: \"" + storeId + "\"");
      }
      context.put(STORE_NAME, storeName);
    }
    if (locale != null) {
      try {
        context.put(LOCALE, LocaleUtils.toLocale(locale));
      } catch (IllegalArgumentException e) {
        throw new InvalidContextException(e);
      }
    }
    if (currency != null) {
      try {
        context.put(CURRENCY, Currency.getInstance(currency));
      } catch (IllegalArgumentException e) {
        throw new InvalidContextException(e);
      }
    }

    return context;
  }
}
