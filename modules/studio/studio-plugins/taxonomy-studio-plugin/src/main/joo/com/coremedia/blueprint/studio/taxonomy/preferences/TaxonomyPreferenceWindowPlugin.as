package com.coremedia.blueprint.studio.taxonomy.preferences {

import com.coremedia.blueprint.studio.config.taxonomy.taxonomyPreferences;

import ext.Component;
import ext.Container;
import ext.Plugin;
import ext.TabPanel;

public class TaxonomyPreferenceWindowPlugin implements Plugin {

  public function TaxonomyPreferenceWindowPlugin() {
    super();
  }

  public function init(component:Component):void {
    var prefWindow:Container = component as Container;
    var tabPanel:TabPanel = prefWindow.getComponent(0) as TabPanel;

    var prevPanel:TaxonomyPreferences = new TaxonomyPreferences(taxonomyPreferences({}));
    tabPanel.add(prevPanel);
    tabPanel.doLayout();
  }
}
}

