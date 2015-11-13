package com.coremedia.blueprint.optimizely;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.util.ExtensionsAspectUtil;
import org.springframework.beans.factory.annotation.Required;

public class OptimizelyPageAspectImpl implements OptimizelyPageAspect {
  private Page aggregator;
  private SettingsService settingsService;

  @Override
  public boolean isEnabled() {
    return settingsService.settingWithDefault("optimizely.enabled", Boolean.class, false, aggregator.getNavigation()) &&
            ExtensionsAspectUtil.isFeatureConfigured(getOptimizelyId());
  }

  @Override
  public String getOptimizelyId() {
    return settingsService.settingWithDefault("optimizely.id" + ExtensionsAspectUtil.EXTERNAL_ACCOUNT, String.class, "", aggregator.getNavigation());
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
