package com.coremedia.blueprint.studio.taxonomy.chooser {
import com.coremedia.blueprint.studio.config.taxonomy.taxonomySelector;
import com.coremedia.blueprint.studio.config.taxonomy.textLinkButton;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeFactory;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.rendering.LinkListRenderer;
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.components.TextLink;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Button;
import ext.Container;
import ext.config.button;
import ext.config.label;
import ext.form.Label;

import js.Event;

/**
 * The taxonomy selector panel that steps through the hierarchy of a taxonomy type.
 */
public class TaxonomySelectorBase extends Container {
  private var ALPHABET:Array;

  private var activeLetters:ValueExpression;
  private var selectedLetter:ValueExpression;
  private var selectedNodeId:ValueExpression;
  private var selectedNodeList:ValueExpression;

  private var taxonomyId:String;
  private var activePathList:TaxonomyNodeList;

  private var buttonCache:Array;

  public function TaxonomySelectorBase(config:taxonomySelector) {
    ALPHABET = [];
    ALPHABET[0] = 'A';
    ALPHABET[1] = 'B';
    ALPHABET[2] = 'C';
    ALPHABET[3] = 'D';
    ALPHABET[4] = 'E';
    ALPHABET[5] = 'F';
    ALPHABET[6] = 'G';
    ALPHABET[7] = 'H';
    ALPHABET[8] = 'I';
    ALPHABET[9] = 'J';
    ALPHABET[10] = 'K';
    ALPHABET[11] = 'L';
    ALPHABET[12] = 'M';
    ALPHABET[13] = 'N';
    ALPHABET[14] = 'O';
    ALPHABET[15] = 'P';
    ALPHABET[16] = 'Q';
    ALPHABET[17] = 'R';
    ALPHABET[18] = 'S';
    ALPHABET[19] = 'T';
    ALPHABET[20] = 'U';
    ALPHABET[21] = 'V';
    ALPHABET[22] = 'W';
    ALPHABET[23] = 'X';
    ALPHABET[24] = 'Y';
    ALPHABET[25] = 'Z';
    super(config);

    taxonomyId = config.taxonomyId;
  }

  /**
   * Adds missing components to the container like the button list.
   */
  override protected function initComponent():void {
    super.initComponent();
    buttonCache = [];

    var alphabetPanel:Container = this.find('itemId', 'alphabetPanel')[0];
    for (var i:int = 0; i < ALPHABET.length; i++) {
      var letter:String = ALPHABET[i];
      var alphaButton:LetterButton = new LetterButton(button({text:letter, disabled:true, flex:1, handler:buttonClicked}));
      alphabetPanel.add(alphaButton);
      buttonCache.push(alphaButton);
    }
  }

  /**
   * Updates the status of the letter buttons, depending on the active selection.
   */
  private function updateLetters():void {
    var activeLettrs:Array = activeLetters.getValue();

    for (var i:int = 0; i < buttonCache.length; i++) {
      var alphaButton:LetterButton = buttonCache[i];
      alphaButton.setDisabled(true);
      var letter:String = alphaButton.getText().toLowerCase();
      for (var j:int = 0; j < activeLettrs.length; j++) {
        var activeLetter:String = activeLettrs[j];
        if (activeLetter === letter) {
          alphaButton.setDisabled(false);
          break;
        }
      }
    }
  }

  /**
   * Creates the value expression that contains an array with the active letters.
   * @return
   */
  protected function getActiveLettersExpression():ValueExpression {
    if (!activeLetters) {
      activeLetters = ValueExpressionFactory.create("letters", beanFactory.createLocalBean());
      activeLetters.addChangeListener(updateLetters);
    }
    return activeLetters;
  }

  /**
   * Creates the value expression that contains the taxonomy node that should be added to the selection link list.
   * @return
   */
  protected function getSelectedNodeIdValueExpression():ValueExpression {
    if (!selectedNodeId) {
      selectedNodeId = ValueExpressionFactory.create("nodeRef", beanFactory.createLocalBean());
      selectedNodeId.addChangeListener(updateLevel);
    }
    return selectedNodeId;
  }

  /**
   * Creates the value expression that contains the taxonomy node list
   * @return
   */
  protected function getSelectedNodeListValueExpression():ValueExpression {
    if (!selectedNodeList) {
      selectedNodeList = ValueExpressionFactory.create("nodeList", beanFactory.createLocalBean());
    }
    return selectedNodeList;
  }

  /**
   * Creates the value expression that contains the active selected button, if user clicked one.
   * @return
   */
  protected function getSelectedLetterExpression():ValueExpression {
    if (!selectedLetter) {
      selectedLetter = ValueExpressionFactory.create("letter", beanFactory.createLocalBean());
    }
    return selectedLetter;
  }

  /**
   * Fire when the user double clicked a node, so that the next sub-level/children are shown.
   */
  private function updateLevel():void {
    var ref:String = getSelectedNodeIdValueExpression().getValue();
    if (ref) {
      var content:Content = StudioUtil.getActiveContent();
      var siteId:String = editorContext.getSitesService().getSiteIdFor(content);
      if (ref === taxonomyId) {
        //update the list with the root children
        TaxonomyNodeFactory.loadTaxonomyRoot(siteId, taxonomyId, function (parent:TaxonomyNode):void {
          parent.loadChildren(function (list:TaxonomyNodeList):void {
            getSelectedNodeListValueExpression().setValue(list);
          });
        });
        //we do not have to build the path for the root taxonomy, since this only exists of the taxonomy id value.
        updatePathPanel(null);
      }
      else {
        //update the list with a regular child
        var currentList:TaxonomyNodeList = selectedNodeList.getValue();
        var newSelection:TaxonomyNode = currentList.getNode(ref);
        if (!newSelection) {
          /**
           * the update was triggered by a path item, so the selection node list (contains children of a path node),
           * so the active ref won't be found in the list.
           * Instead we look up the ref in the path list, this an item of this list was selected.
           */
          newSelection = activePathList.getNode(ref);
        }
        if (!newSelection.isLeaf()) {//do not show children of leafs, which are empty of course).
          newSelection.loadChildren(function (list:TaxonomyNodeList):void {
            getSelectedNodeListValueExpression().setValue(list);
          });
        }
        //update the path of the current selection, this will build the path above the list
        TaxonomyNodeFactory.loadPath(taxonomyId, ref, siteId,
                function (list:TaxonomyNodeList):void {
                  updatePathPanel(list);
                });
      }
    }
  }

  /**
   * Displays the current path selection.
   * @param list
   */
  private function updatePathPanel(list:TaxonomyNodeList):void {
    activePathList = list;
    var pathPanel:Container = getPathPanel();
    pathPanel.removeAll(true);

    //Add root
    var root:TextLinkButton = new TextLinkButton(textLinkButton({text:taxonomyId, itemId:taxonomyId, cls:'x-btn-arrow-right', handler:doSetLevel}));
    pathPanel.add(root);
    if (list && list.getNodes().length > 0) {
      pathPanel.add(getSeparator());
    }

    //add each level
    if (list) {
      var nodes:Array = list.getNodes();
      for (var i:int = 1; i < nodes.length; i++) {
        var node:TaxonomyNode = nodes[i];

        var pathItem:TextLinkButton = new TextLinkButton(textLinkButton({node:node, itemId:node.getRef(),
          cls:'x-btn-arrow-right', handler:doSetLevel, tooltip:'Show Level'}));
        pathPanel.add(pathItem);

        //add separator
        if (i < nodes.length - 1) {
          pathPanel.add(getSeparator());
        }
      }
    }

    //refresh the layout
    pathPanel.doLayout(false, true);
  }

  /**
   * The path selector item handler.
   */
  private function doSetLevel(button:Button, e:Event):void {
    var nodeRef:String = button.getItemId();
    getSelectedNodeIdValueExpression().setValue(nodeRef);
  }

  /**
   * Creates the separate used between the path items.
   * @return
   */
  private function getSeparator():Label {
    var separator:Label = new Label(label({}));
    separator.setText('<div class="' + LinkListRenderer.ARROW_CLS + ' navigator"></div>', false);
    return separator;
  }

  /**
   * Returns the path panel that contains the path and level selectors.
   * @return
   */
  private function getPathPanel():Container {
    return find('itemId', 'levelSelector')[0];
  }


  /**
   * Applies the active button to the selected leter expression, so that the link list is updated.
   * @param b The button component.
   * @param e The js event.
   */
  private function buttonClicked(b:TextLink, e:Event):void {
    var letter:String = b.getText();
    getSelectedLetterExpression().setValue(letter.toLowerCase());
  }
}
}
