package com.coremedia.blueprint.cae.web.taglib;

import com.coremedia.blueprint.base.settings.SettingsService;

/**
 * Static SettingsService utilities used in JSP Taglibs.
 * For Freemarker use {@link BlueprintFreemarkerFacade} instead.
 */
public final class SettingsFunction {

  // static class
  private SettingsFunction() {
  }

  public static Object setting(SettingsService settingsService, Object self, String key) {
    return setting(settingsService, self, key, null);
  }

  public static Object setting(SettingsService settingsService, Object self, String key, Object defaultValue) {
    return settingsService.settingWithDefault(key, Object.class, defaultValue, self);
  }
}
