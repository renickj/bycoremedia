package com.coremedia.catalog.studio.preferences {
import com.coremedia.blueprint.studio.config.taxonomy.taxonomyPreferences;
import com.coremedia.cms.editor.sdk.preferences.PreferencePanel;
import com.coremedia.cms.editor.sdk.util.PreferencesUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Panel;

public class CatalogPreferencesBase extends Panel implements PreferencePanel {
  public static var PREFERENCE_SHOW_CATALOG_KEY:String = "showCatalog";

  protected static var SETTING_SHOW_IN_TREE:String = "showInTree";
  protected static var SETTING_SHOW_AS_CONTENT:String = "showAsContent";

  internal var showCatalogValueExpression:ValueExpression;


  public function CatalogPreferencesBase(config:taxonomyPreferences) {
    super(config);
  }

  protected function getShowCatalogValueExpression():ValueExpression {
    if(!showCatalogValueExpression) {
      var value:Boolean = PreferencesUtil.getPreferencesProperty(PREFERENCE_SHOW_CATALOG_KEY);
      if(value === undefined || value) {
        value = SETTING_SHOW_IN_TREE;
      }
      else {
        value = SETTING_SHOW_AS_CONTENT;
      }
      showCatalogValueExpression = ValueExpressionFactory.createFromValue(value);
    }
    return showCatalogValueExpression;
  }

  public function updatePreferences():void {
    var value:String = getShowCatalogValueExpression().getValue();
    PreferencesUtil.updatePreferencesJSONProperty(value === SETTING_SHOW_IN_TREE, PREFERENCE_SHOW_CATALOG_KEY);
  }
}
}
