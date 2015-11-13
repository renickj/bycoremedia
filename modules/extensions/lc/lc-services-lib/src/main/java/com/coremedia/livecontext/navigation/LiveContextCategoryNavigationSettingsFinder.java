package com.coremedia.livecontext.navigation;


import com.coremedia.blueprint.base.settings.SettingsFinder;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.livecontext.context.LiveContextNavigation;
import org.springframework.beans.factory.annotation.Required;

public class LiveContextCategoryNavigationSettingsFinder implements SettingsFinder {
  private LiveContextNavigationTreeRelation treeRelation;

  // --- configure --------------------------------------------------

  @Required
  public void setTreeRelation(LiveContextNavigationTreeRelation treeRelation) {
    this.treeRelation = treeRelation;
  }


  // --- SettingsFinder ---------------------------------------------

  @Override
  public Object setting(Object bean, String name, SettingsService settingsService) {
    // responsible?
    if (!(bean instanceof LiveContextCategoryNavigation)) {
      return null;
    }

    // get setting of parent navigation, since a LiveContextCategoryNavigation
    // has no settings of its own.
    LiveContextNavigation lcn = (LiveContextNavigation) bean;
    return settingsService.setting(name, Object.class, treeRelation.getParentOf(lcn));
  }
}
