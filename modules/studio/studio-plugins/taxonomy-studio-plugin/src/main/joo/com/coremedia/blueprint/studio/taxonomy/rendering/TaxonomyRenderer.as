package com.coremedia.blueprint.studio.taxonomy.rendering {

import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;

/**
 * The common renderer implementation. Subclasses will overwrite the doRenderInternal method.
 */
public class TaxonomyRenderer {
  public static const BUTTON_PLUS_BLACK_CLS:String = "btn-plus-black";
  public static const BUTTON_MINUS_BLACK_CLS:String = "btn-minus-black";

  public static const BUTTON_PLUS_WHITE_CLS:String = "btn-plus-white";
  public static const BUTTON_MINUS_WHITE_CLS:String = "btn-minus-white";

  public static const PARENT_NODE_CLS:String = "grey";
  public static const LEAF_NODE_CLS:String = " leaf";

  private var nodes:Array;
  private var html:String; //applied if the callback handler is or can not used

  public function TaxonomyRenderer(nodes:Array) {
    this.nodes = nodes;
  }

  public function getHtml():String {
    return html;
  }

  public function setHtml(value:String):void {
    html = value;
  }

  /**
   * Triggers the rendering for the concrete instance of the renderer.
   * @param callback The callback function the generated HTML will be passed to.
   */
  public function doRender(callback:Function = null):void {
    doRenderInternal(nodes, callback);
  }

  /**
   * Returns the nodes of this renderer.
   * @return
   */
  protected function getNodes():Array {
    return nodes;
  }

  /**
   * Returns the taxonomy leaf the renderer is build for.
   * @return A taxonomy node.
   */
  protected function getLeaf():TaxonomyNode {
    var leaf:* = nodes[nodes.length-1];
    if(leaf as TaxonomyNode) {
      return leaf;
    }
    return new TaxonomyNode(leaf);
  }

  /**
   * The method must be overwritten by subclasses, error is thrown otherwise.
   * @param callback The callback method the HTML is passed to.
   */
  protected function doRenderInternal(nodes:Array, callback:Function):void {
    throw new Error("Subclass must overwrite rendering method 'doRenderInternal'");
  }
}
}