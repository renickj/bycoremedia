package com.coremedia.blueprint.studio.taxonomy.rendering {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.ui.util.EventUtil;

import ext.ComponentMgr;
import ext.Ext;

import js.Collection;
import js.Element;
import js.Event;

/**
 * Renderer is used for the leaf list in the selection dialog when there is only single selection allowed.
 * Since there are no path information shown, we only have to render
 * the leaf itself. We re-use the link list renderer again, since the leaf
 * layout matches the one of the regular taxonomy link lists.
 */
public class SingleSelectionListRenderer extends SelectionListRenderer {
  private var componentId:String;
  private var selected:Boolean;
  private var selectionExists:Boolean;

  public function SingleSelectionListRenderer(nodes:Array, componentId:String, selected:Boolean, selectionExists:Boolean) {
    super(nodes, componentId, selected);
    this.componentId = componentId;
    this.selected = selected;
    this.selectionExists = selectionExists;
  }

  override protected function doRenderInternal(nodes:Array, callback:Function):void {
    var node:TaxonomyNode = nodes[0];
    var wrapperId:String = Ext.id();
    var displayName:String = getLeafName(node);
    var nodeCls:String = getBaseNodeCls();
    var nameCls:String = 'tag-name';
    if(selectionExists && !selected) { //node that is not selectable
      nameCls = ''; //remove the space that was reserved for the plus/minus icon
    }
    var nodeInnerHtml:String;
    if(!node.isLeaf()) {
      nodeInnerHtml = '<div class="' + nameCls + '""><a class="tag-link" href="#">' + displayName + '</a></div>';
      EventUtil.invokeLater(function ():void {
        function nodeClicked(event:Event):void {
          ComponentMgr.get(componentId)['selectedNodeClicked']();
          event.preventDefault();
          event.stopPropagation();
        }
        var wrapperElement:Element = window.document.getElementById(wrapperId);
        if (wrapperElement) {
          var childAnchors:Collection = wrapperElement.getElementsByTagName("a");
          var nodeAnchor:Element = childAnchors[0];
          nodeAnchor.addEventListener("click", nodeClicked, false);
        }
      });
    } else {
      nodeInnerHtml = '<div class="' + nameCls + '">' + displayName + '</div>';
    }
    var nodeHtml:String = '<div class="' + nodeCls + '" id="' + wrapperId + '">';
    nodeHtml += nodeInnerHtml;
    if(selected || !selectionExists) {
      nodeHtml += createPlusMinusButton(node);
      EventUtil.invokeLater(function ():void {
        function plusMinusClicked(event:Event):void {
          ComponentMgr.get(componentId)['plusMinusClicked'](node.getRef());
          event.preventDefault();
          event.stopPropagation();
        }
        var wrapperElement:Element = window.document.getElementById(wrapperId);
        if (wrapperElement) {
          var childAnchors:Collection = wrapperElement.getElementsByTagName("a");
          var plusMinusAnchor:Element = childAnchors[childAnchors.length - 1];
          plusMinusAnchor.addEventListener("click", plusMinusClicked, false);
        }
      });
    }
    nodeHtml += "</div>";
    setHtml(nodeHtml);
  }


  override protected function getLeafActionIconCls(node:TaxonomyNode):String {
    if(selected) {
      return BUTTON_MINUS_BLACK_CLS;
    }
    return BUTTON_PLUS_BLACK_CLS;
  }
}
}