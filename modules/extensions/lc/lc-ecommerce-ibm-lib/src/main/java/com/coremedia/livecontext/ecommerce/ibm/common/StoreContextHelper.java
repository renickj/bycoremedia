package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.ibm.login.WcCredentials;
import com.coremedia.livecontext.ecommerce.ibm.login.WcPreviewToken;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CATALOG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.COMMERCE_SYSTEM_IS_UNAVAILABLE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CONFIG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CONTRACT_IDS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.NO_WS_MARKER;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.PREVIEW_DATE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.REPLACEMENTS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.STORE_NAME;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.USER_SEGMENTS;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.WORKSPACE_ID;

/**
 * Helper class to build an "IBM WCS conform" store context.
 * You do not have to know the exact keys if you use the helper method.
 * Use this class as static import.
 */
public class StoreContextHelper {

  public static final String WCS_VERSION = "wcsVersion";
  public static final float WCS_VERSION_7_6 = 7.6f;
  public static final float WCS_VERSION_7_7 = 7.7f;
  public static final float WCS_VERSION_7_8 = 7.8f;
  public static final float WCS_VERSION_DEFAULT = WCS_VERSION_7_7;

  public static final String CREDENTIALS = "credentials";
  public static final String PREVIEW_TOKEN = "previewToken";

  private StoreContextHelper() {
  }

  /**
   * Set the given store context in the current request (thread).
   * Read the current context with #getCurrentContext().
   * @param context the current context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException
   */
  public static void setCurrentContext(@Nonnull StoreContext context) throws InvalidContextException {
    validateContext(context);
    Commerce.getCurrentConnection().setStoreContext(context);
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

  public static StoreContext getCurrentContextFor(Locale locale) {
    StoreContext currentStoreContext = getCurrentContext();
    // locale can be null if the default locale is not set for commerce beans
    // in such a case we return the current context (a warning should be logged from caller)
    if (locale != null) {
      StoreContext result = StoreContextHelper.createContext(
              currentStoreContext.getConfigId(),
              currentStoreContext.getStoreId(),
              currentStoreContext.getStoreName(),
              currentStoreContext.getCatalogId(),
              locale.toString(),
              currentStoreContext.getCurrency().toString()
      );
      setWorkspaceId(result, currentStoreContext.getWorkspaceId());
      return result;
    }
    return currentStoreContext;
  }

  public static StoreContext cloneContext(@Nonnull StoreContext context) throws InvalidContextException {
    return StoreContextBuilder.create().withValues(context).build();
  }

  /**
   * Adds the given values to a store context.
   * All potential values are possible. You can use a "null" value to omit single values.
   * @param storeId the store id or null
   * @param storeName the store name or null
   * @param catalogId the catalog id or null
   * @param locale the locale id or null
   * @param currency the currency id or null
   * @return the new built store context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if locale or currency has wrong format
   */
  public static StoreContext createContext(String configId, String storeId, String storeName, String catalogId, String locale, String currency)
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
    if (catalogId != null) {
      if (StringUtils.isBlank(catalogId)) {
        throw new InvalidContextException("catalogId has wrong format: \"" + catalogId + "\"");
      }
      context.put(CATALOG_ID, catalogId);
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

  /**
   * Gets the store name from the given store context.
   * @param context the store context
   * @return the store name
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  public static String getStoreName(StoreContext context) throws InvalidContextException {
    Object value = context.get(STORE_NAME);
    if (!(value instanceof String)) {
      throw new InvalidContextException("missing " + STORE_NAME + " (" + formatContext(context) + ")");
    }
    return (String) value;
  }

  /**
   * Gets the store name from the given store context - in the form used for seo related handler, i.e. in low-case.
   * @param context the store context
   * @return the store name in low-case
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  public static String getStoreNameInLowerCase(StoreContext context) throws InvalidContextException {
    return getStoreName(context).toLowerCase();
  }

  /**
   * Gets the optional spring config id name from the given store context.
   * The config id can be used to read other store config credentials from a spring configuration.
   * @param context the store context
   * @return the config id or null if not present
   */
  public static String getConfigId(StoreContext context) throws InvalidContextException {
    return (String) context.get(CONFIG_ID);
  }

  /**
   * Gets the locale from the given store context.
   * @param context the store context
   * @return the locale
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  public static Locale getLocale(StoreContext context) throws InvalidContextException {
    Object value = context.get(LOCALE);
    if (!(value instanceof Locale)) {
      throw new InvalidContextException("missing " + LOCALE + " (" + formatContext(context) + ")");
    }
    return (Locale) value;
  }

  /**
   * Sets locale to storeContext
   */
  public static void setLocale(@Nullable StoreContext context, String locale) {
    if (context != null && locale != null) {
      try {
        context.put(LOCALE, LocaleUtils.toLocale(locale));
      } catch (IllegalArgumentException e) {
        throw new InvalidContextException(e);
      }
    }
  }

  /**
   * Gets the currency from the given store context.
   * @param context the store context
   * @return the currency
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the store id is invalid (missing or wrong type)
   */
  public static Currency getCurrency(StoreContext context) throws InvalidContextException {
    Object value = context.get(CURRENCY);
    if (!(value instanceof Currency)) {
      throw new InvalidContextException("missing " + CURRENCY + " (" + formatContext(context) + ")");
    }
    return (Currency) value;
  }

  /**
   * Gets the workspace id from the given store context.
   * @param context the store context
   * @return the workspace id or null if no workspace was set
   */
  public static String getWorkspaceId(StoreContext context) {
    return (String) context.get(WORKSPACE_ID);
  }

  /**
   * Sets the workspaceId of the given context
   * @param context the given context
   * @param workspaceId the given workspaceId
   */
  public static void setWorkspaceId(@Nullable StoreContext context, String workspaceId) {
    if (context != null) {
      if (workspaceId != null) {
        if (StringUtils.isBlank(workspaceId)) {
          throw new InvalidContextException("workspaceId has wrong format: \"" + workspaceId + "\"");
        }
        context.put(WORKSPACE_ID, workspaceId);
      } else {
        context.put(WORKSPACE_ID, NO_WS_MARKER);
      }
    }
  }

  /**
   * Gets the contract ids from the given store context.
   * @param context the store context
   * @return the contract ids or null if no contract was set
   */
  public static String[] getContractIds(StoreContext context) {
    return (String[]) context.get(CONTRACT_IDS);
  }

  /**
   * Sets the contract ids of the given context
   * @param context the given context
   * @param contractIds the given contract ids
   */
  public static void setContractIds(@Nullable StoreContext context, String[] contractIds) {
    if (context != null && contractIds != null && contractIds.length > 0) {
      context.put(CONTRACT_IDS, contractIds);
    }
  }

  /**
   * Gets the wcs version from the given store context or the if not configured the default value.
   * @param context the store context
   * @return the version as float
   */
  public static float getWcsVersion(StoreContext context) {
    Float value = (Float) context.get(WCS_VERSION);
    return value != null ? value : WCS_VERSION_DEFAULT;
  }

  /**
   * Set the version to the given store context.
   * @param context the store context
   * @param wcsVersion the version as String
   */
  public static void setWcsVersion(@Nullable StoreContext context, String wcsVersion) {
    if (context != null) {
      context.put(WCS_VERSION, Float.parseFloat(wcsVersion));
    }
  }

  /**
   * Gets the catalog id from the given store context.
   * @param context the store context
   * @return the catalog id or null if no workspace was set
   */
  public static String getCatalogId(StoreContext context) {
    return (String) context.get(CATALOG_ID);
  }

  /**
   * Gets the preview date in "YYYY/MM/dd HH:MM:SS" format from the given store context.
   * @param context the store context
   * @return the preview date or null if no preview date was set
   */
  public static String getPreviewDate(StoreContext context) {
    return (String) context.get(PREVIEW_DATE);
  }

  /**
   * Gets the list of comma separated user segments from the given store context.
   * @param context the store context
   * @return the workspace id or null if no workspace was set
   */
  public static String getUserSegments(StoreContext context) {
    return (String) context.get(USER_SEGMENTS);
  }

  public static boolean isCommerceSystemUnavailable(@Nullable StoreContext context) {
    return context != null && Boolean.getBoolean(context.get(COMMERCE_SYSTEM_IS_UNAVAILABLE)+"");
  }

  public static void setCommerceSystemIsUnavailable(@Nullable StoreContext context, boolean isUnavailable) {
    if (context != null) {
      context.put(COMMERCE_SYSTEM_IS_UNAVAILABLE, isUnavailable);
    }
  }

  public static void setCredentials(@Nullable StoreContext context, WcCredentials credentials) {
    if (context != null) {
      context.put(CREDENTIALS, credentials);
    }
  }

  public static void setPreviewToken(@Nullable StoreContext context, WcPreviewToken previewToken) {
    if (context != null) {
      context.put(PREVIEW_TOKEN, previewToken);
    }
  }

  /**
   * Set the replacement map into the given store context.
   * @param context the store context
   * @param replacements the replacement map
   */
  public static void setReplacements(@Nullable StoreContext context, Map<String,String> replacements) {
    if (context != null) {
      context.put(REPLACEMENTS, replacements);
    }
  }

  /**
   * Set the configId into the given store context.
   * @param context the store context
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

  public static void setWcsTimeZone(@Nullable StoreContext context, Map<String, String> timeZone) {
    if (context != null) {
      context.put("wcsTimeZone", timeZone);
    }
  }

  /**
   * Gets true if the dynamic pricing is enabled that leads to separate personalized price calls.
   * Default: false (if not configured, it will be assumed it is not enabled)
   * @param context the store context
   * @return true if enabled
   */
  public static boolean isDynamicPricingEnabled(StoreContext context) {
    Boolean value = (Boolean) context.get(AbstractStoreContextProvider.CONFIG_KEY_DYNAMIC_PRICING_ENABLED);
    return value != null ? value : false;
  }

  /**
   * Set the the value that dynamic pricing is enabled.
   * @param context the store context
   * @param enabled the boolean value
   */
  public static void setDynamicPricingEnabled(@Nullable StoreContext context, boolean enabled) {
    if (context != null) {
      context.put(AbstractStoreContextProvider.CONFIG_KEY_DYNAMIC_PRICING_ENABLED, enabled);
    }
  }

  /**
   * Convenience method to validate the whole context.
   * Checks if all known context values exist.
   * @param context the store context
   * @throws com.coremedia.livecontext.ecommerce.common.InvalidContextException if the context is invalid (missing or wrong typed values)
   */
  public static void validateContext(StoreContext context) throws InvalidContextException {
    if (context == null){
      throw new InvalidContextException("context is null");
    }
    getStoreId(context);
    getStoreName(context);
    getLocale(context);
    getCurrency(context);
  }

  private static String formatContext(StoreContext context) {
    return CONFIG_ID + ": " + context.get(CONFIG_ID) + ", " +
            STORE_ID + ": " + context.get(STORE_ID) + ", " +
            STORE_NAME + ": " + context.get(STORE_NAME) + ", " +
            CATALOG_ID + ": " + context.get(CATALOG_ID) + ", " +
            LOCALE + ": " + context.get(LOCALE) + ", " +
            CURRENCY + ": " + context.get(CURRENCY) + ", " +
            WORKSPACE_ID + ": " + context.get(WORKSPACE_ID);
  }

}
