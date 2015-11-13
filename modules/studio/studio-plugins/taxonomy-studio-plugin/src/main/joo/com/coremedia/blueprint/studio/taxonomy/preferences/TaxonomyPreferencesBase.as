package com.coremedia.blueprint.studio.taxonomy.preferences {
import com.coremedia.blueprint.studio.config.taxonomy.taxonomyPreferences;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.preferences.PreferencePanel;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Panel;
import ext.config.arraystore;
import ext.data.ArrayStore;
import ext.data.Store;

public class TaxonomyPreferencesBase extends Panel implements PreferencePanel {
  public static var PREFERENCE_SEMANTIC_SETTINGS_KEY:String = "semanticSettings";

  public static var TAXONOMY_SEMANTIC_CALAIS_KEY:String = "semantic";
  public static var TAXONOMY_NAME_MATCHING_KEY:String = "nameMatching";

  public static var DEFAULT_SUGGESTION_KEY:String = TAXONOMY_NAME_MATCHING_KEY;

  internal var previewOptionValueExpression:ValueExpression;


  public function TaxonomyPreferencesBase(config:taxonomyPreferences) {
    super(config);
  }

  public function getStore():Store {
    var arrayStore:Store = new ArrayStore(arraystore({
      data:getTaxonomyOptions(),
      fields:['name', 'value']
    }));
    return arrayStore;
  }

  protected function getSuggestionTypesValueExpression():ValueExpression {
    if (!previewOptionValueExpression) {
      previewOptionValueExpression = ValueExpressionFactory.create('taxonomyOption', editorContext.getBeanFactory().createLocalBean());
      var valueString:String = StudioUtil.getPreference(PREFERENCE_SEMANTIC_SETTINGS_KEY);
      if (!valueString) {
        valueString = DEFAULT_SUGGESTION_KEY;
      }
      previewOptionValueExpression.setValue(valueString);
    }
    return previewOptionValueExpression;
  }


  public function getTaxonomyOptions():Array {
    var result:Array = [
      [TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyPreferences_value_semantic_opencalais_text, TAXONOMY_SEMANTIC_CALAIS_KEY],
      [TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyPreferences_value_nameMatching_text, TAXONOMY_NAME_MATCHING_KEY]
    ];
    return result;
  }

  private function persistOptionSelection(ve:ValueExpression):void {
    var previewOption:String = ve.getValue();
    editorContext.getPreferences().set(PREFERENCE_SEMANTIC_SETTINGS_KEY, previewOption);
    editorContext.getApplicationContext().set(PREFERENCE_SEMANTIC_SETTINGS_KEY, previewOption);
  }

  public function updatePreferences():void {
    persistOptionSelection(previewOptionValueExpression);
  }
}
}
