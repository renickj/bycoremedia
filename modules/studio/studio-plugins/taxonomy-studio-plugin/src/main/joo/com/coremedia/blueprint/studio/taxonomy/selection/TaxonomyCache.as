package com.coremedia.blueprint.studio.taxonomy.selection {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeFactory;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.util.EventUtil;

public class TaxonomyCache {
  private var taxId:String;
  private var activeContent:Content;
  private var cachedSuggestions:TaxonomyNodeList;
  private var pvExpression:ValueExpression;

  public function TaxonomyCache(content:Content, propertyValueExpression:ValueExpression, taxonomyId:String) {
    pvExpression = propertyValueExpression;
    activeContent = content;
    taxId = taxonomyId;
  }

  /**
   * Invalidates the cached result and re-requests the suggestion
   * list for the given content.
   * @param callback The callback handler that processes the suggestions list.
   */
  public function invalidate(callback:Function):void {
    EventUtil.invokeLater(function ():void { //must be invoked later, otherwise the leaf is not rendered, don't know why
      TaxonomyNodeFactory.loadSuggestions(taxId, activeContent, function (nodeList:TaxonomyNodeList):void {
        cachedSuggestions = nodeList;
        callback(getActiveSuggestions());
      });
    });
  }

  /**
   * Returns a subset of the suggestions.
   * @return
   */
  private function getActiveSuggestions():TaxonomyNodeList {
    if (cachedSuggestions) {
      var json:Array = cachedSuggestions.toJson();
      var nodes:Array = [];
      for (var i:int = 0; i < json.length; i++) {
        var node:TaxonomyNode = new TaxonomyNode(json[i]);
        if (!isInTaxonomyList(node)) {
          nodes.push(node);
        }
      }
      var taxList:TaxonomyNodeList = new TaxonomyNodeList(json);
      taxList.setNodes(nodes);
      return taxList;
    }
    else {
      return null;
    }
  }

  /**
   * Returns true if the given node is already added a keyword
   * for the active content.
   * @param node
   * @return
   */
  public function isInTaxonomyList(node:TaxonomyNode):Boolean {
    var items:Array = pvExpression.getValue();
    if (items) {
      for (var i:int = 0; i < items.length; i++) {
        var child:Content = items[i];
        var childId:String = TaxonomyUtil.parseRestId(child);
        if (childId === node.getRef()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns the active suggestions list.
   * @param callback
   */
  public function loadSuggestions(callback:Function):void {
    EventUtil.invokeLater(function ():void {//must be invoked later, otherwise the leaf is not rendered, don't know why
      callback(getActiveSuggestions());
    })
  }

  /**
   * Returns the weight of the
   * @param id
   * @return
   */
  public function getWeight(id:String):String {
    if(cachedSuggestions) {
      id = TaxonomyUtil.getRestIdFromCapId(id);
      return cachedSuggestions.getNode(id).getWeight();
    }
    return null;
  }
}
}