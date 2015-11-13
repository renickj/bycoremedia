package com.coremedia.blueprint.studio.struct.editor {

import com.coremedia.blueprint.studio.struct.XMLUtil;
import com.coremedia.blueprint.studio.struct.config.structPasteDialog;
import com.coremedia.ui.data.ValueExpression;

import ext.Window;
import ext.form.TextArea;

/**
 * Shows a list of selected images and creates
 * a collection document out of it.
 */
public class StructPasteDialogBase extends Window {

  private var structPasteExpression:ValueExpression;

  /**
   *
   * @param config the config object
   */
  public function StructPasteDialogBase(config:structPasteDialog) {
    super(config);
    this.structPasteExpression = config.structPasteExpression;
    addListener('show', initTextArea);
  }

  /**
   * Init the state.
   */
  private function initTextArea():void {
    removeListener('show', initTextArea);
//    getTextArea().focus(true,500);
  }

  /**
   * Just execute the action for creating a gallery.
   */
  public function handleOk():void {
    var field:TextArea = getTextArea();
    var xml:String = field.getValue();
    var document:* = XMLUtil.parseXML(xml);
    if (document) {
      structPasteExpression.setValue(xml);
    }
    close();
  }

  /**
   * Returns the text area that contains the current or pasted XML.
   * @return
   */
  private function getTextArea():TextArea {
    return find('itemId', 'xmlTextField')[0] as TextArea;
  }
}
}