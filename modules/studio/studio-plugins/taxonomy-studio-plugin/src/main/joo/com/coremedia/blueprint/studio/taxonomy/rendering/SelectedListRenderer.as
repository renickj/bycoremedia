package com.coremedia.blueprint.studio.taxonomy.rendering {

import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;

import ext.BoxComponent;
import ext.ComponentMgr;

/**
 * The renderer used for the taxonomy link lists in the selection dialog (upper list).
 */
public class SelectedListRenderer extends LinkListRenderer {
  private var componentId:String;
  private var scrolling:Boolean;

  public function SelectedListRenderer(nodes:Array, componentId:String, scrolling:Boolean) {
    super(nodes, componentId);
    this.componentId = componentId;
    this.scrolling = scrolling;
  }


  override protected function getMarginRightOffset():Number {
    if(scrolling)
      return 20; //we to increase the offset because of the scrolling
    return super.getMarginRightOffset();
  }

  override protected function getLeafActionIconCls(node:TaxonomyNode):String {
    return BUTTON_MINUS_WHITE_CLS;
  }

  /**
   * Calculates the available with for component the path should be rendered into.
   * @return The available pixels.
   */
  override protected function getComponentWidth():Number {
    // Calculate component size
    var comp:BoxComponent = ComponentMgr.get(componentId) as BoxComponent;
    var compWidth:Number = 0;
    if (comp) {
      compWidth = comp.getWidth() - 20; //margins increased here, because of scrolling
    }
    return compWidth;
  }
}
}