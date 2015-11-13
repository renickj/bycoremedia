package com.coremedia.cms.studio.queryeditor.conditions {

import com.coremedia.cap.content.Content;
import com.coremedia.cms.studio.queryeditor.config.contextConditionEditor;
import com.coremedia.ui.data.ValueExpression;

public class ContextConditionEditorBase extends ConditionEditorBase {

  private var propertyName:String;
  private var contentType:String;
  private var bindTo:ValueExpression;

  public function ContextConditionEditorBase(config:contextConditionEditor) {
    super(config);
    contentType = config.contentType;
    propertyName = config.propertyName;
    bindTo = config.bindTo;
  }

  protected function resolvePropertyName(propertyName:String):String {
    return "localSettings.fq." + propertyName;
  }

  public override function translateAssistantToExpertFormat(assistantFormattedExpression:*):String {
    var contexts:Array = assistantFormattedExpression;
    var contextIds:Array = [];
    if (contexts && contexts.length) {
      contexts.forEach(function (context:Content) {
        contextIds.push(context.getId());
      });
    }
    return assembleCondition(propertyName, contextIds, "OR");
  }


  /**
   * Ensures that the substruct the condition editor
   * writes into is created.
   */
  override protected function afterRender():void {
    super.afterRender();
    super.applyBaseStruct(bindTo, contentType, propertyName);
  }
}
}
