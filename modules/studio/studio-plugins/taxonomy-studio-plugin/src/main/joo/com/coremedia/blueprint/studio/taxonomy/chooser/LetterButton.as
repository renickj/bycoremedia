package com.coremedia.blueprint.studio.taxonomy.chooser {

import ext.Button;
import ext.Ext;
import ext.Template;
import ext.config.button;

/**
 * A Button that displays its text only as overflowText. This Button also displays two icons as inline placed elements.
 */
public class LetterButton extends Button {

  private static const TEXT_BUTTON_TEMPLATE:Template = new Template([
    '<div class="{2}">',
    '<em class="wrapper-btn"><b><a id="{4}" href="#">{4}</a></b></em>',
    '</div>']).compile();

  public function LetterButton(config:button) {
    super(button(Ext.apply({
      template: TEXT_BUTTON_TEMPLATE,
      cls: 'letter-button',
      buttonSelector: 'a'
    }, config)));
    this['xtype'] = this['xtype'] || button['xtype']; // set to default xtype when created through Action!
    this["overflowText"] = this["text"];
  }

}
}