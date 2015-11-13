package com.coremedia.blueprint.studio.taxonomy.rendering {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;

/**
 * Renders the search result displayed in the drop down of full text search of link list
 * and in the taxonomy administration.
 */
public class SearchComboRenderer extends TaxonomyRenderer {
  private const TAXONOMY_NODE_BASE_CLS:String = 'taxonomy-node-linklist-base';

  public function SearchComboRenderer(nodes:Array) {
    super(nodes);
  }

  override protected function doRenderInternal(nodes:Array, callback:Function):void {
    var html:String = "";

    for (var i:int = 1; i < nodes.length; i++) {
      var node:TaxonomyNode = new TaxonomyNode(nodes[i]);
      var nodeCls:String = TAXONOMY_NODE_BASE_CLS;
      var isLeaf:Boolean = i === (nodes.length -1);
      if (!isLeaf) {
        // Render parent node
        nodeCls += " " + PARENT_NODE_CLS;
      }

      html += "<div class='" + nodeCls + "'>" + node.getName() + "</div>";

      if (!isLeaf) {
        // Render path arrow
        html += "<div class='" + LinkListRenderer.ARROW_CLS + "'></div>";
      } else {
        html += "<div style='clear: left;'></div>"
      }

    }

    setHtml(html);
  }
}
}