package com.coremedia.blueprint.studio.taxonomy.rendering {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.ui.util.EventUtil;

import ext.ComponentMgr;
import ext.Ext;

import js.Collection;

import js.Element;
import js.Event;

/**
 * Renderer is used for the leaf list in the selection dialog.
 * Since there are no path information shown, we only have to render
 * the leaf itself. We re-use the link list renderer again, since the leaf
 * layout matches the one of the regular taxonomy link lists.
 */
public class SelectionListRenderer extends LinkListRenderer {
  private var componentId:String;
  private var selected:Boolean;

  public function SelectionListRenderer(nodes:Array, componentId:String, selected:Boolean) {
    super(nodes, componentId);
    this.componentId = componentId;
    this.selected = selected;
  }

  override protected function doRenderInternal(nodes:Array, callback:Function):void {
    var node:TaxonomyNode = nodes[0];
    var wrapperId:String = Ext.id();
    var displayName:String = getLeafName(node);
    var nodeCls:String = getBaseNodeCls();
    if(selected) {
      nodeCls+=LEAF_NODE_CLS;
    }
    var nodeInnerHtml:String = "<div class='tag-name'>" + displayName + "</div>";
    if(!node.isLeaf()) {
      nodeInnerHtml = '<div class="tag-name"><a class="tag-link" href="#">' + displayName + '</div></a>';
      EventUtil.invokeLater(function ():void {
        function nodeClicked(event:Event):void {
          ComponentMgr.get(componentId)['selectedNodeClicked'](event);
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
    }
    var nodeHtml:String = "<div class='" + nodeCls + "' id='" + wrapperId + "'>";
    nodeHtml += nodeInnerHtml;
    nodeHtml += createPlusMinusButton(node);
    nodeHtml += "</div>";
    setHtml(nodeHtml);

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

  override protected function getLeafActionIconCls(node:TaxonomyNode):String {
    if(!selected) {
      return BUTTON_PLUS_BLACK_CLS;
    }
    return super.getLeafActionIconCls(node);
  }

  override protected function getLeafActionIconTooltip():String {
    if(!selected) {
      return TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_keyword_add_text;
    }
    return super.getLeafActionIconTooltip();
  }
}
}