package com.coremedia.catalog.studio.preferences {
import com.coremedia.blueprint.studio.config.catalog.catalogPreferences;

import ext.Component;
import ext.Container;
import ext.Plugin;
import ext.TabPanel;

public class CatalogPreferenceWindowPlugin implements Plugin {

  public function CatalogPreferenceWindowPlugin() {
    super();
  }

  public function init(component:Component):void {
    var prefWindow:Container = component as Container;
    var tabPanel:TabPanel = prefWindow.getComponent(0) as TabPanel;

      var prevPanel:CatalogPreferences = new CatalogPreferences(catalogPreferences({}));
      tabPanel.add(prevPanel);
      tabPanel.doLayout();
  }
}
}

