package com.coremedia.blueprint.studio.taxonomy.chooser {

import com.coremedia.blueprint.studio.config.taxonomy.textLinkButton;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;

import ext.Button;
import ext.Ext;
import ext.Template;
import ext.config.button;

/**
 * A Button that displays its text only as overflowText. This Button also displays two icons as inline placed elements.
 */
public class TextLinkButton extends Button {

  private static const NODE_TEMPLATE:Template = new Template([
    '<span class="{2}">{5}<a style="text-decoration:none !important;" id="{4}" href="#"></a></span>']).compile();

  private var node:TaxonomyNode;
  private var addable:Boolean;
  private var weight:String;

  public function TextLinkButton(config:textLinkButton) {
    weight = config.weight;
    node = config.node;
    addable = config.addable;
    var template:Template = NODE_TEMPLATE;
    super(button(Ext.apply({
      template: template,
      iconCls: iconCls,
      buttonSelector: 'a'
    }, config)));
    this['xtype'] = this['xtype'] || button['xtype']; // set to default xtype when created through Action!
    this["overflowText"] = this["text"];

    if(node && !node.isLeaf()) {
      addListener('afterrender', addCursor);
    }
  }

  /**
   * Cursor change on mouse over if the node is addable.
   */
  private function addCursor():void {
    removeListener('afterrender', addCursor);
    getEl().setStyle('cursor', 'pointer');
  }

  /**
   * Getter is used by the handler which identifies the
   * button by the item id, matching the node id.
   * @return
   */
  public function getTaxonomyNode():TaxonomyNode {
    return node;
  }


  /**
   * Adds the button class to the list of template arguments.
   * @return
   */
  override public function getTemplateArgs():Array {
    var args:Array = super.getTemplateArgs();
    if(node) {
      var name:String = node.getName();
      if(weight) {
        name = name + " (" + weight + ")";
      }
      args.push(name);
    }
    return args;
  }
}
}