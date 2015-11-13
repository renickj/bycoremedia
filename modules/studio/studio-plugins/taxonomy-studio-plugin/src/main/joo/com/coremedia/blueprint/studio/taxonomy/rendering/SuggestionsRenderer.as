package com.coremedia.blueprint.studio.taxonomy.rendering {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;

/**
 * The renderer used for rendering the suggestions.
 * The rendering is almost the same like for regular link lists, so we
 * extend the link list renderer and modify the leaf rendering.
 */
public class SuggestionsRenderer extends LinkListRenderer {
  private var weight:String;

  public function SuggestionsRenderer(nodes:Array, componentId, weight:String) {
    super(nodes, componentId);
    this.weight = weight;
  }

  override protected function doRenderInternal(nodes:Array, callback:Function):void {
    super.doRenderInternal(nodes, callback);
  }

  override protected function getLeafActionIconCls(node:TaxonomyNode):String {
    if(node.isLeaf()) {
      return BUTTON_PLUS_BLACK_CLS;
    }
    return BUTTON_PLUS_BLACK_CLS;
  }

  override protected function getLeafActionIconTooltip():String {
    return TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_keyword_add_text;
  }


  /**
   * We do not return an additional class for leafs here.
   * This means we do not color leafs in blue, but leave them grey instead.
   * @return undefined for no additional CSS class.
   */
  override protected function getLeafCls():String {
    return undefined;
  }

  /**
   * Overwrites the name rendering to add the weight
   * information calculated by the suggestions plugin.
   * @param node The node to render the name for.
   * @return
   */
  override protected function getLeafName(node:TaxonomyNode):String {
    var name:String = super.getLeafName(node);
    if(weight) {
      name+= ' (' + weight + ')';
    }
    return name;
  }
}
}