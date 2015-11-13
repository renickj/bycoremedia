package com.coremedia.cms.studio.queryeditor.conditions {

import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.studio.queryeditor.config.contextConditionEditor;
import com.coremedia.ui.data.ValueExpression;

public class LinkingTaxonomyConditionEditorBase extends ConditionEditorBase {
  private var propertyName:String;
  private var bindTo:ValueExpression;

  public function LinkingTaxonomyConditionEditorBase(config:contextConditionEditor) {
    super(config);
    propertyName = config.propertyName;
    bindTo = config.bindTo;
  }

  public override function translateAssistantToExpertFormat(assistantFormattedExpression:*):String {
    return "";
  }

  /**
   * Ensures that the substruct the condition editor
   * writes into is created.
   */
  override protected function afterRender():void {
    super.afterRender();
    var c:Content = bindTo.getValue();
    var struct:Struct = c.getProperties().get('localSettings');
    struct.getType().addStructProperty('fq');
    var fq:Struct = struct.get('fq');
    fq.getType().addBooleanProperty(propertyName, true);
    fq.set(propertyName,true);
  }
}
}
