package com.coremedia.livecontext.ecommerce.toko.common;

import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.cap.common.NoSuchPropertyDescriptorException;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class StoreContextProviderImpl extends AbstractStoreContextProvider {

  private final static String CONFIG_KEY_STORE_CONFIG = "livecontext.store.config";
  private final static String CONFIG_KEY_STORE_ID = "store.id";
  private final static String CONFIG_KEY_STORE_NAME = "store.name";
  private final static String CONFIG_KEY_CURRENCY = "currency";
  private final static String CONFIG_KEY_CONFIG_ID = "config.id";


  @Override
  protected StoreContext internalCreateContext(@Nonnull Site site) {
    StoreContext result = null;
    // only create catalog context if settings were found for current site
    Struct repositoryStoreConfig = getSettingsService().setting(CONFIG_KEY_STORE_CONFIG, Struct.class, site.getSiteRootDocument());
    if (repositoryStoreConfig != null) {
      try {
        String configId = StructUtil.getString(repositoryStoreConfig, CONFIG_KEY_CONFIG_ID);

        Map<String, Object> targetConfig = new HashMap<>();
        readStoreConfigFromSpring(configId, targetConfig);
        updateStoreConfigFromRepository(repositoryStoreConfig, targetConfig);

        result = StoreContextHelper.createContext(
                (String) targetConfig.get(CONFIG_KEY_CONFIG_ID),
                (String) targetConfig.get(CONFIG_KEY_STORE_ID),
                (String) targetConfig.get(CONFIG_KEY_STORE_NAME),
                site.getLocale().toString(),
                (String) targetConfig.get(CONFIG_KEY_CURRENCY)
        );

        StoreContextHelper.setConfigId(result, configId);
        StoreContextHelper.setSiteId(result, site.getId());

      } catch (NoSuchPropertyDescriptorException e) {
        throw new InvalidContextException("Missing properties in store configuration. ", e);
      }
    }
    return result;
  }

}
