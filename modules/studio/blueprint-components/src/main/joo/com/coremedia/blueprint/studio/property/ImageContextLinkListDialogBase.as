package com.coremedia.blueprint.studio.property {
import com.coremedia.blueprint.studio.config.components.imageContextLinkListDialog;
import com.coremedia.cap.content.Content;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.store.BeanRecord;
import com.coremedia.ui.util.EventUtil;

import ext.Ext;
import ext.Window;
import ext.form.Label;
import ext.form.TextField;

/**
 * Base class of the image context edit dialog. The dialog apply the
 * value of the text field to the struct value expression that is given
 * by the cfg param contextValueExpression.
 */
public class ImageContextLinkListDialogBase extends Window {
  private var record:BeanRecord;
  private var contextValueExpression:ValueExpression;
  private var textValueExpression:ValueExpression;
  private var callback:Function;

  public function ImageContextLinkListDialogBase(config:imageContextLinkListDialog) {
    super(config);
    this.callback = config.callback;
    this.contextValueExpression = config.contextValueExpression;
    this.record = config.record;
    addListener('afterlayout', initDialog);
  }

  /**
   * Only used to apply the focus.
   */
  private function initDialog():void {
    removeListener('afterlayout', initDialog);
    var text:TextField = find('itemId', 'labelTextfield')[0];
    text.focus(true);
  }

  /**
   * Creates the value expression the current text is stored in.
   * @return
   */
  protected function getTextValueExpression():ValueExpression {
    if(!textValueExpression) {
      textValueExpression = ValueExpressionFactory.create('contextValue', beanFactory.createLocalBean());
    }
    return textValueExpression;
  }

  protected function resetField():void {
    textValueExpression.setValue("");
  }

  override protected function afterRender():void {
    super.afterRender();
    var c:Content = record.getBean() as Content;
    var ppeThumbnail:ValueExpression = ValueExpressionFactory.create('properties.data.uri', c);
    var thumbUri:String = ppeThumbnail.getValue();
    var html:String = '<div class="context-thumb-wrapper"><img src="' + thumbUri + '/rm/box;width=360;height=250"/></div>';

    var label:Label = find('itemId', 'imageLabel')[0];
    label.setText(html, false);

    getTextValueExpression().setValue(contextValueExpression.getValue());
  }

  /**
   * Applies the value from the text value expression to the context (struct) value expression.
   */
  protected function applyValue():void {
    var value:String = textValueExpression.getValue();
    callback.call(null, contextValueExpression, record, value);
    destroy();
  }
}
}