package com.coremedia.blueprint.studio.taxonomy.queryeditor {

import com.coremedia.blueprint.studio.config.taxonomy.taxonomyConditionEditor;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.studio.queryeditor.conditions.ConditionEditorBase;
import com.coremedia.ui.data.ValueExpression;

public class TaxonomyConditionEditorBase extends ConditionEditorBase {

  private var propertyName:String;
  private var contentType:String;
  private var bindTo:ValueExpression;
  private var structPropertyName:String;

  public function TaxonomyConditionEditorBase(config:taxonomyConditionEditor) {
    super(config);
    contentType = config.contentType;
    propertyName = config.propertyName;
    bindTo = config.bindTo;

    structPropertyName = propertyName.substring(propertyName.lastIndexOf(".")+1, propertyName.length);
  }

  public override function translateAssistantToExpertFormat(assistantFormattedExpression:*):String {
    var taxonomies:Array = assistantFormattedExpression,
            taxonomyIds:Array = [];
    if (taxonomies && taxonomies.length) {
      taxonomies.forEach(function (taxonomy:Content):void {
        taxonomyIds.push(taxonomy.getId());
      });
    }
    return assembleCondition(propertyName, taxonomyIds, "OR");
  }


  /**
   * Ensures that the substruct the condition editor
   * writes into is created.
   */
  override protected function afterRender():void {
    super.afterRender();
    super.applyBaseStruct(bindTo, contentType, structPropertyName);
  }
}
}
