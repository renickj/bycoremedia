package com.coremedia.livecontext.ecommerce.toko.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Currency;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CONFIG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.STORE_NAME;

/**
 * Todo toko
 */
public class StoreContextHelper {

  private StoreContextHelper() {
  }

  /**
   * Gets the current store context within the current request (thread).
   * Set the current context with #setCurrentContext();
   * @return the StoreContext
   */
  public static StoreContext getCurrentContext() {
    CommerceConnection currentConnection = Commerce.getCurrentConnection();
    return currentConnection != null ? currentConnection.getStoreContext() : null;
  }

  /**
   * Gets the store id from the given store context.
   * @param context the store context
   * @return the store id
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  public static String getStoreId(StoreContext context) throws InvalidContextException {
    Object value = context.get(STORE_ID);
    if (!(value instanceof String)) {
      throw new InvalidContextException("missing " + STORE_ID + " (" + formatContext(context) + ")");
    }
    return (String) value;
  }

  public static String getStoreName(StoreContext context) {
    Object value = context.get(STORE_NAME);
    if (!(value instanceof String)) {
      throw new InvalidContextException("missing " + STORE_NAME + " (" + formatContext(context) + ")");
    }
    return (String) value;
  }

  private static String formatContext(StoreContext context) {
    return CONFIG_ID + ": " + context.get(CONFIG_ID) + ", " +
            STORE_ID + ": " + context.get(STORE_ID) + ", " +
            STORE_NAME + ": " + context.get(STORE_NAME) + ", " +
            LOCALE + ": " + context.get(LOCALE) + ", " +
            CURRENCY + ": " + context.get(CURRENCY);
  }

  /**
   * Adds the given values to a store context.
   * All potential values are possible. You can use a "null" value to omit single values.
   *
   * @param storeId   the store id or null
   * @param storeName the store name or null
   * @param locale    the locale id or null
   * @param currency  the currency id or null
   * @return the new built store context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if locale or currency has wrong format
   */
  public static StoreContext createContext(String configId, String storeId, String storeName, String locale, String currency)
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

  /**
   * Set the configId into the given store context.
   *
   * @param context  the store context
   * @param configId the store configuration identifier
   */
  public static void setConfigId(@Nullable StoreContext context, String configId) {
    if (context != null) {
      context.put(CONFIG_ID, configId);
    }
  }

  public static void setSiteId(@Nullable StoreContext context, String siteId) {
    if (context != null) {
      context.put(StoreContextBuilder.SITE, siteId);
    }
  }

}
