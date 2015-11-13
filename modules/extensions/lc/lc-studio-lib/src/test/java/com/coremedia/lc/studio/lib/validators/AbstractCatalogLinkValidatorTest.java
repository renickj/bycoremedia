package com.coremedia.lc.studio.lib.validators;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.undoc.common.CapConnection;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.mockito.Mock;

import java.util.Currency;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CATALOG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CONFIG_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.CURRENCY;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.LOCALE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.STORE_ID;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilder.STORE_NAME;
import static org.mockito.Mockito.when;

public class AbstractCatalogLinkValidatorTest {

  protected CommerceConnection commerceConnection;

  @Mock
  protected CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  protected Content content;

  @Mock
  protected CapConnection capConnection;

  @Mock
  protected StoreContextProvider storeContextProvider;

  protected void init() {
    commerceConnection = MockCommerceEnvBuilder.create().setupEnv();
    when(content.isInProduction()).thenReturn(true);
  }



  protected StoreContext createContext(String configId, String storeId, String storeName, String catalogId, String locale, String currency)
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
}
