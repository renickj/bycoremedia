package com.coremedia.blueprint.nuggad;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.util.ExtensionsAspectUtil;
import org.springframework.beans.factory.annotation.Required;

public class NuggAdPageAspectImpl implements NuggAdPageAspect {
  private Page aggregator;
  private SettingsService settingsService;

  @Override
  public boolean isEnabled() {
    return settingsService.settingWithDefault("nuggad.enabled", Boolean.class, false, aggregator.getNavigation())
            && ExtensionsAspectUtil.isFeatureConfigured(getNuggn()) && ExtensionsAspectUtil.isFeatureConfigured(getNuggsid());
  }

  @Override
  public String getNuggn() {
    return settingsService.settingWithDefault("nuggad.nuggn" + ExtensionsAspectUtil.EXTERNAL_ACCOUNT, String.class, "", aggregator.getNavigation());
  }

  @Override
  public String getNuggsid() {
    return settingsService.settingWithDefault("nuggad.nuggsid" + ExtensionsAspectUtil.EXTERNAL_ACCOUNT, String.class, "", aggregator.getNavigation());
  }

  @Override
  public void setAspectAggregator(Object aggregator) {
    this.aggregator = (Page) aggregator;
  }

  @Override
  public Page getAggregator() {
    return aggregator;
  }

  @Required
  public void setSettingsService(SettingsService settingsService) {
    this.settingsService = settingsService;
  }
}
