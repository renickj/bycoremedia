package com.coremedia.blueprint.studio.taxonomy.rendering {

import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.ui.util.EventUtil;

import ext.BoxComponent;
import ext.ComponentMgr;

import js.Element;
import js.Event;

/**
 * The renderer used for the regular taxonomy link lists (property editor).
 */
public class LinkListRenderer extends TaxonomyRenderer {
  public static const ARROW_CLS:String = "taxonomy-path-arrow";

  private static const STRIPPED_OVERLAY_WIDTH:Number = 15;
  private static const ARROW_WIDTH:Number = 10;

  private static const STRIPPED_NODE_CLS:String = "stripped";
  private static const TAXONOMY_NODE_BASE_CLS:String = "taxonomy-node-linklist-base";
  private var componentId:String;
  private var wrapperId:String;
  private var averageNodeWidth:Number; //only set if required!!!!

  public function LinkListRenderer(nodes:Array, componentId:String) {
    super(nodes);
    this.componentId = componentId;
    this.wrapperId = componentId + "-wrapper-" + nodes[nodes.length - 1].ref;
  }

  override protected function doRenderInternal(nodes:Array, callback:Function):void {
    var html:String = '<div style="width:1900px;"><span id="' + wrapperId + '">';

    // Generate html for all nodes
    var nodeCls:String = getBaseNodeCls() + " " + PARENT_NODE_CLS;
    for (var i:int = 1; i < nodes.length; i++) {
      var node:TaxonomyNode = new TaxonomyNode(nodes[i]);
      if (i === nodes.length - 1) {
        html += renderLeaf(node); //render leaf
      } else {
        if (isPathRenderingEnabled()) {
          html += renderNode(node, nodeCls);
          html += renderPathArrow();
        }
      }
    }

    html += '</span></div>';
    try {
      callback.call(null, html);
    } catch (e) {
      // Do nothing and hope for the best.
    }

    postProcessFormatting(nodeCls);
  }

  /**
   * This is executed once the rendering is done.
   * We can calculate now all the actual width's, so no more guessing
   * what the width of a node might be.
   */
  private function postProcessFormatting(nodeCls:String):void {
    EventUtil.invokeLater(function ():void {
      var actualTotalWidth:Number = 0;
      var spanElement:* = window.document.getElementById(wrapperId);
      //might not be there if not match was found
      if(!spanElement) {
        return;
      }
      
      var nodeElements:Array = spanElement.childNodes;
      var leafElement:* = nodeElements[nodeElements.length - 1];


      //calculation is done without the child!
      for (var i:int = 0; i < nodeElements.length; i++) {
        actualTotalWidth += nodeElements[i].offsetWidth;
      }

      var componentWidth:Number = getComponentWidth();
      var minWidth:Number = calculateMinimumWidth(nodeElements);

      if (actualTotalWidth <= componentWidth) {
        //the path rendering fits into the container, so everything is fine
      }
      else if (actualTotalWidth > componentWidth) {
        //ok, so we can apply the regular stripping: strip children, let the leaf on full length
        var availableParentWidth:Number = componentWidth - leafElement.offsetWidth;
        averageNodeWidth = availableParentWidth / (getNodes().length - 1);
        averageNodeWidth = (averageNodeWidth - STRIPPED_OVERLAY_WIDTH - ARROW_WIDTH);
        if (averageNodeWidth < STRIPPED_OVERLAY_WIDTH) {
          averageNodeWidth = STRIPPED_OVERLAY_WIDTH;
        }
        for (var j:int = 0; j < nodeElements.length - 1; j++) {
          var node:* = nodeElements[j];
          if (node.offsetWidth > (averageNodeWidth + STRIPPED_OVERLAY_WIDTH) && !isArrowElement(node)) {
            node.setAttribute('style', 'width:' + averageNodeWidth + 'px;');
            node.setAttribute('class', TAXONOMY_NODE_BASE_CLS + ' ' + PARENT_NODE_CLS + ' ' + STRIPPED_NODE_CLS);
            node.setAttribute('stripped', averageNodeWidth);
          }
          else {
            //not stripped, given node is short enough
          }
        }
      }

      for (var k:int = 0; k < nodeElements.length - 1; k++) {
        var nod:* = nodeElements[k];
        if (!isArrowElement(nod)) {
          addNodeMouseEventHandlers(nod, nodeCls);
        }
      }
      addLeafMouseEventHandlers(nodeElements[nodeElements.length - 1]);
    });
  }

  private function addNodeMouseEventHandlers(node:Element, nodeCls:String):void {

    function handleNodeMouseOver(event:js.Event):void {
      node.removeAttribute("style");
      node.setAttribute("class", nodeCls);
    }

    function handleNodeMouseOut(event:js.Event):void {
      var strippedWidth:* = node.getAttribute("stripped");
      if (strippedWidth) {
        node.setAttribute("style", "width: " + strippedWidth + "px;");
        node.setAttribute("class", TAXONOMY_NODE_BASE_CLS + ' ' + PARENT_NODE_CLS + ' ' + STRIPPED_NODE_CLS);
      }
    }

    node.addEventListener('mouseover', handleNodeMouseOver, false);
    node.addEventListener('mouseout', handleNodeMouseOut, false);
  }

  protected function addLeafMouseEventHandlers(node:Element):void {
    var plusMinusAnchor:Element = node.getElementsByTagName("a")[0];

    node.addEventListener('mouseover', leafMouseOver, false);
    node.addEventListener('mouseout', leafMouseOut, false);
    plusMinusAnchor.addEventListener('click', plusMinusClicked, false);
  }

  function plusMinusClicked(event:Event):void {
    var plusMinusAnchor:Element = event.target;
    ComponentMgr.get(componentId)['plusMinusClicked'](plusMinusAnchor.getAttribute("data-taxonomy-node-ref"));
    event.preventDefault();
    event.stopPropagation();
  }


  /**
   * Called when the taxonomy leaf is not fully visible. Previous elements are hidden then.
   */
  function leafMouseOver():void {
    var componentWidth:Number = BoxComponent(ComponentMgr.get(componentId)).getWidth();
    var spanElement:Element = window.document.getElementById(wrapperId);
    var nodeElements:Array = spanElement.childNodes;
    var actualTotalWidth:Number = 0;
    //calculate current with...
    for (var i:uint = 0; i < nodeElements.length; i++) {
      actualTotalWidth += nodeElements[i].offsetWidth;
    }
    actualTotalWidth = actualTotalWidth + 10; //add margin left and right
    if (actualTotalWidth > componentWidth) {
      var margin:Number = actualTotalWidth - componentWidth + getMarginRightOffset();
      if (spanElement.parentNode) {
        spanElement.parentNode.style.marginLeft = '-' + margin + 'px';
      }
    }
  }

  /**
   * Called when the leaf of a link list taxonomy has been moved
   * to the left, so that it is fully visible. On the mouse out event, the previous
   * elements are shown again.
   */
  function leafMouseOut():void {
    var spanElement:Element = window.document.getElementById(wrapperId);
    if (spanElement.parentNode) {
      spanElement.parentNode.style.marginLeft = '0px';
    }
  }


  /**
   * Calculates the amount of pixels that are necessary to render
   * a full stripped path.
   * @param element
   * @return
   */
  private function calculateMinimumWidth(nodes:Array):Number {
    var width:Number = 0;
    for (var j:int = 0; j < nodes.length - 1; j++) {
      if (isArrowElement(nodes[j])) {
        width += ARROW_WIDTH;
      }
      else {
        width += STRIPPED_OVERLAY_WIDTH;
      }
    }
    //plus the full leaf width
    var leafElement:* = nodes[nodes.length - 1];
    width += leafElement.offsetWidth;
    return width;
  }

  /**
   * Checks if the given element is an arrow node.
   * @param element
   * @return
   */
  private function isArrowElement(element:*):Boolean {
    return element.getAttribute('class') === ARROW_CLS;
  }

  /**
   * Returns the actual width of all parent nodes together.
   * @return The pixel width of all parent nodes.
   */
  private function getActualParentNodesWidth():Number {
    var spanElement:* = window.document.getElementById(wrapperId);
    var nodeElements:Array = spanElement.childNodes;
    var actualTotalWidth:Number = 0;
    for (var j:int = 0; j < nodeElements.length - 1; j++) {
      if (!isArrowElement(nodeElements[j])) {
        actualTotalWidth += nodeElements[j].offsetWidth;
      }
    }
    return actualTotalWidth;
  }

  /**
   * Renders parent nodes.
   * @param node The node to render.
   * @param defaultCls The default CSS class applied to the div of the node element.
   * @return The HTML for parent nodes.
   */
  protected function renderNode(node:TaxonomyNode, defaultCls:String):String {
    var html:String = "";
    html += "<div class='" + defaultCls + "'>";
    html += " <div class='tag-name'>" + node.getDisplayName() + "</div>";
    html += " <div class='stripped-tag-overlay'></div>";
    html += "</div>";
    return html;
  }

  /**
   * Returns the HTML that is used for rendering the arrow between nodes.
   * @return
   */
  protected function renderPathArrow():String {
    return "<div class='" + ARROW_CLS + "'></div>";
  }

  /**
   * Renders the taxonomy node leaf for link lists.
   * @param node The node to render.
   * @return
   */
  protected function renderLeaf(node:TaxonomyNode):String {
    var nodeCls:String = getBaseNodeCls();
    if (getLeafCls()) {
      nodeCls = nodeCls + " " + getLeafCls();
    }
    var nodeInnerHtml:String = "<div class='tag-name'>" + getLeafName(node) + "</div>";
    var nodeHtml:String = '<div class="' + nodeCls + '">';
    nodeHtml += nodeInnerHtml;
    nodeHtml += createPlusMinusButton(node);
    nodeHtml += "</div>";
    return nodeHtml;
  }

  /**
   * Returns the additional class name used for leafs in addition to the base class.
   * @return The name of the leaf css class.
   */
  protected function getLeafCls():String {
    return LEAF_NODE_CLS;
  }

  /**
   * Returns the margin amount of pixels from the right border
   * of the link list.
   * @return
   */
  protected function getMarginRightOffset():Number {
    return 25;
  }

  /**
   * Allows to overwrite the actual name rendering of the leaf, e.g. to add
   * additional information like the weight of a suggestion used in subclasses.
   * @param node The node to render the name for.
   * @return
   */
  protected function getLeafName(node:TaxonomyNode):String {
    return node.getDisplayName();
  }

  /**
   * Allows to overwrite the actual name rendering of the parent, e.g.
   * to add additional information of the node.
   * @param node The node to render the name for.
   * @return
   */
  protected function getParentName(node:TaxonomyNode):String {
    return node.getDisplayName();
  }

  /**
   * Renders the node including the '+' link into each row, using ids.
   */
  protected function createPlusMinusButton(node:TaxonomyNode):String {
    var iconCls:String = getLeafActionIconCls(node);
    var tooltipText:String = getLeafActionIconTooltip();
    var btnHtml:String = "<div class='plus-minus-button'>";
    btnHtml += '<a ' +
    'class="' + iconCls + '" ' +
    'href="#" ' +
    'title="' + tooltipText + '"' +
    'data-taxonomy-node-ref="' + node.getRef() + '">' +
    '&nbsp;' +
    '</a>';
    btnHtml += '</div>';

    return btnHtml;
  }

  /**
   * Returns the CSS class for the link action image shown on the leaf.
   * We deal with property editor link lists here, so only removing is possible, but
   * e.g. suggestions will overwrite this method.
   * @return The name of the CSS class for the leaf link icon.
   */
  protected function getLeafActionIconCls(node:TaxonomyNode):String {
    return BUTTON_MINUS_WHITE_CLS;
  }

  /**
   * Returns the tooltip string for the leaf action icon.
   * @return
   */
  protected function getLeafActionIconTooltip():String {
    return TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyLinkList_keyword_remove_text;
  }

  /**
   * Returns the name of the base class used for rendering a node.
   * The method may be overwritten so support a different link list rendering.
   * @return The name of a CSS class.
   */
  protected function getBaseNodeCls():String {
    return TAXONOMY_NODE_BASE_CLS;
  }

  /**
   * Calculates the available with for component the path should be rendered into.
   * @return The available pixels.
   */
  protected function getComponentWidth():Number {
    // Calculate component size
    var comp:BoxComponent = ComponentMgr.get(componentId) as BoxComponent;
    var compWidth:Number = 0;
    if (comp) {
      compWidth = comp.getWidth() - 25; //margins
    }
    return compWidth;
  }

  /**
   * Can be overwritten to omit the path rendering and render the leaf only instead.
   * @return
   */
  protected function isPathRenderingEnabled():Boolean {
    return true;
  }
}
}
