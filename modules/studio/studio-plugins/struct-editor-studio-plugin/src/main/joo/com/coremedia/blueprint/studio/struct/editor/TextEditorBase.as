package com.coremedia.blueprint.studio.struct.editor {
import com.coremedia.blueprint.studio.struct.config.textEditor;

import ext.Container;
import ext.form.TextField;

public class TextEditorBase extends Container {
  private var propertyName:String;

  public function TextEditorBase(config:textEditor) {
    super(config);
    this.propertyName = config.propertyName;
  }


  override protected function afterRender():void {
    super.afterRender();
    if (propertyName === ElementModel.NAME_PROPERTY) {
      var text:TextField = find('itemId', 'texteditor-textfield')[0];
      text.focus(true, 500);
    }
  }
}
}