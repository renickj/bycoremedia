package com.coremedia.blueprint.studio.taxonomy.administration {

import com.coremedia.blueprint.studio.TaxonomyStudioPluginSettings_properties;
import com.coremedia.blueprint.studio.config.taxonomy.taxonomyExplorerColumn;
import com.coremedia.blueprint.studio.config.taxonomy.taxonomyExplorerPanel;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeFactory;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNodeList;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyStudioPlugin_properties;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.blueprint.studio.taxonomy.selection.TaxonomySearchField;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.config.collapsibleFormPanel;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.premular.FocusForwarder;
import com.coremedia.cms.editor.sdk.premular.PropertyFieldRegistry;
import com.coremedia.cms.editor.sdk.premular.StandAloneDocumentView;
import com.coremedia.cms.editor.sdk.premular.fields.StringPropertyField;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.util.EventUtil;
import com.coremedia.ui.util.IdUtil;

import ext.Button;
import ext.Container;
import ext.EventObject;
import ext.Ext;
import ext.MessageBox;
import ext.Panel;
import ext.form.Field;
import ext.form.TextField;
import ext.util.StringUtil;

/**
 * Handles the adding and removing of the level columns.
 */
public class TaxonomyExplorerPanelBase extends Panel {
  private var contextInfo:FocusForwarder;

  private var selectedValueExpression:ValueExpression;
  private var displayedTaxonomyContentExpression:ValueExpression;
  private var displayedTaxonomyNodeExpression:ValueExpression;
  private var siteSelectionExpression:ValueExpression;
  private var searchResultExpression:ValueExpression;
  private var contextInfoValueExpression:ValueExpression;

  private var columnsContainer:Container;
  private var clipboardValueExpression:ValueExpression;
  private var propertyFieldRegistry:PropertyFieldRegistry;

  private static const DOCTYPE_TAXONOMY_REFERRERS:String = 'CMLinkable';

  private static const DOCTYPE_TAXONOMY_REFERRERS_PROPERTY_SUBJECT:String = 'subjectTaxonomy';
  private static const DOCTYPE_TAXONOMY_REFERRERS_PROPERTY_LOCATION:String = 'locationTaxonomy';

  public function TaxonomyExplorerPanelBase(config:taxonomyExplorerPanel) {
    this.contextInfo = new FocusForwarder(Ext.emptyFn, Ext.emptyFn);
    super(config);
    searchResultExpression = config.searchResultExpression;
    siteSelectionExpression = config.siteSelectionExpression;

    searchResultExpression.addChangeListener(searchSelectionChanged);
    siteSelectionExpression.addChangeListener(siteSelectionChanged);
  }

  public function getContextInfo():Bean {
    return this.contextInfo;
  }

  protected function getContextInfoValueExpression():ValueExpression {
    if (!contextInfoValueExpression) {
      contextInfoValueExpression = ValueExpressionFactory.create("contextInfo", this);
    }
    return contextInfoValueExpression;
  }

  public function getPropertyFieldRegistry():PropertyFieldRegistry {
    if (!propertyFieldRegistry) {
      propertyFieldRegistry = new PropertyFieldRegistry(this);
    }
    return propertyFieldRegistry;
  }

  public function getSelectedValueExpression():ValueExpression {
    if (!selectedValueExpression) {
      var selectedValuesBean:Bean = beanFactory.createLocalBean();
      selectedValueExpression = ValueExpressionFactory.create("value", selectedValuesBean);
      selectedValueExpression.addChangeListener(selectedNodeChanged);
    }
    return selectedValueExpression;
  }

  protected function getClipboardValueExpression():ValueExpression {
    if (!clipboardValueExpression) {
      var selectedValuesBean:Bean = beanFactory.createLocalBean();
      clipboardValueExpression = ValueExpressionFactory.create("copy", selectedValuesBean);
    }
    return clipboardValueExpression;
  }

  public function getDisplayedTaxonomyContentExpression():ValueExpression {
    if (!displayedTaxonomyContentExpression) {
      var bean:Bean = beanFactory.createLocalBean();
      displayedTaxonomyContentExpression = ValueExpressionFactory.create("content", bean);
    }
    return displayedTaxonomyContentExpression;
  }

  public function getDisplayedTaxonomyNodeExpression():ValueExpression {
    if (!displayedTaxonomyNodeExpression) {
      displayedTaxonomyNodeExpression = ValueExpressionFactory.create("node", beanFactory.createLocalBean());
    }
    return displayedTaxonomyNodeExpression;
  }

  /**
   * Handler implementation of the 'Add child node' button.
   */
  protected function createChildNode(b:Button, e:EventObject):void {
    setBusy(true);
    var parent:TaxonomyNode = getSelectedValueExpression().getValue() as TaxonomyNode;
    parent.createChild(function (newChild:TaxonomyNode):void {
      parent.invalidate(function ():void {
        refreshNode(parent);
        updateColumns(parent);
        //god, i love ext
        EventUtil.invokeLater(function ():void {
          EventUtil.invokeLater(function ():void {
            getSelectedValueExpression().setValue(newChild);
            selectNode(newChild, true);

            // Preset location latitude/longitude for location taxonomy nodes
            setInitialLocation(newChild, parent);
          });
        });
      });
    });
  }

  /**
   * Handler implementation of the delete button.
   */
  protected function deleteNode(b:Button, e:EventObject):void {
    var node:TaxonomyNode = getSelectedValueExpression().getValue();

    var uriPath:String = node.getRef();
    var taxononmyId:int = IdUtil.parseContentBean(uriPath);
    if (taxononmyId === IdUtil.MISSING_CONTENT_ID) {
      return;
    }

    var nodeRef:Content = session.getConnection().getContentRepository().getContent(uriPath);
    ValueExpressionFactory.createFromFunction(function():Array {
      var subjectTaxonomyReferrers:Array = nodeRef.getReferrersWithNamedDescriptor(DOCTYPE_TAXONOMY_REFERRERS, DOCTYPE_TAXONOMY_REFERRERS_PROPERTY_SUBJECT);
      if(undefined === subjectTaxonomyReferrers) {
        return undefined;
      }
      if(Ext.isEmpty(subjectTaxonomyReferrers)) {
        return nodeRef.getReferrersWithNamedDescriptor(DOCTYPE_TAXONOMY_REFERRERS, DOCTYPE_TAXONOMY_REFERRERS_PROPERTY_LOCATION);
      }
      return subjectTaxonomyReferrers;
    }).loadValue(function(referrers:Array):void {
      doDeletion(node, referrers.length > 0);
    });
  }

  private function doDeletion(node:TaxonomyNode, referrered:Boolean):void {
    var message:String = TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyEditor_deletion_text;
    var icon:String = MessageBox.INFO;
    if (referrered) {
      message = TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyEditor_deletion_text_referrer_warning;
      icon = MessageBox.ERROR;
    }

    MessageBox.show({
      title: TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyEditor_deletion_title,
      msg: StringUtil.format(message, node.getName() || TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyExplorerColumn_undefined),
      icon: icon,
      minWidth: 300,
      buttons: MessageBox.OKCANCEL,
      fn: function (btn:*):void {
        if (btn === 'ok') {
          setBusy(true);
          node.deleteNode(function (parent:TaxonomyNode):void {
            var parentContent:Content = session.getConnection().getContentRepository().getContent(node.getRef()).getParent();
            if (parentContent) {
              parentContent.invalidate();
            }
            //checks if return value is defined, otherwise the node could not be deleted.
            if (parent) {
              parent.invalidate(function ():void { //reload the inner content too!!
                var parentCC:TaxonomyExplorerColumn = getColumnContainer(parent);
                if (parentCC) {
                  parentCC.updateNode(parent);
                  updateColumns(parent);
                }
                getSelectedValueExpression().setValue(parent);
              });
            }
            else {
              setBusy(false);
              var msg:String = TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyEditor_deletion_failed_text;
              msg = StringUtil.format(msg, node.getName());
              MessageBox.alert(TaxonomyStudioPlugin_properties.INSTANCE.TaxonomyEditor_deletion_failed_title, msg);
            }

            setBusy(false);
          })
        }
      }
    });
  }

  /**
   * Reloads this panel, applies for site selection changes
   * and reload taxonomies action.
   */
  protected function reload():void {
    setBusy(true);
    selectedValueExpression.setValue(null);
    getRootColumnPanel().reload();
    setBusy(false);
  }


  /**
   * Sets initial latitude/longitude value from parent if the node type is 'CMLocTaxonomy'.
   */
  private function setInitialLocation(taxonomyNode:TaxonomyNode, parentNode:TaxonomyNode):void {
    var parentNodeContent:Content = beanFactory.getRemoteBean(parentNode.getRef()) as Content;
    var newNodeContent:Content = beanFactory.getRemoteBean(taxonomyNode.getRef()) as Content;

    newNodeContent.load(function ():void {
      if (newNodeContent.getType().isSubtypeOf(TaxonomyStudioPluginSettings_properties.INSTANCE.taxonomy_location_doctype)) {
        var property:String = TaxonomyStudioPluginSettings_properties.INSTANCE.taxonomy_location_latLong_property_name;
        var parentLocation:ValueExpression = ValueExpressionFactory.create(property, parentNodeContent);
        var newContentLocation:ValueExpression = ValueExpressionFactory.create(property, newNodeContent);

        if (parentNodeContent.isFolder()) {
          newContentLocation.setValue(TaxonomyStudioPluginSettings_properties.INSTANCE.taxonomy_location_default_value);
        }
        else {
          parentLocation.loadValue(function (location:String):void {
            newContentLocation.loadValue(function ():void {
              newContentLocation.setValue(location);
            });
          });
        }
      }
    });
  }

  /**
   * Fired when the user has selected a search result. All column panels are removed then,
   * the new column tree is rebuild and the leaf selected.
   */
  private function searchSelectionChanged():void {
    var list:TaxonomyNodeList = searchResultExpression.getValue();
    if (list) {
      selectNodePath(list);
    }
  }

  /**
   * Shows the columns, selection and leaf formular for the given node list.
   * @param list
   */
  private function selectNodePath(list:TaxonomyNodeList):void {
    setBusy(true);
    var columnsContainer:Container = getColumnsContainer();
    columnsContainer.removeAll(true);

    var nodes:Array = list.getNodes();
    for (var i:int = 0; i < nodes.length; i++) {
      var node:TaxonomyNode = nodes[i];
      if (i === 0) {
        getRootColumnPanel().selectNode(node, true);
      }
      else {
        addColumn(nodes[i - 1]);
        if (i === nodes.length - 1) {
          selectedValueExpression.setValue(node);
        }
      }
    }
    for (var j:int = 1; j < nodes.length; j++) {
      var selectNode:TaxonomyNode = nodes[j];
      var column:TaxonomyExplorerColumn = getColumnContainer(selectNode);
      if (column) {
        column.selectNode(selectNode);
      }
    }
  }

  /**
   * Returns the panel that contains the root taxonomy nodes.
   * @return The taxonomy grid panel.
   */
  private function getRootColumnPanel():TaxonomyExplorerColumn {
    var rootContainer:TaxonomyExplorerColumn = find("itemId", "taxonomyRootsColumn")[0] as TaxonomyExplorerColumn;
    return rootContainer;
  }

  /**
   * Notifies a change in the active selection. If the value expression is empty,
   * the view is set to the root nodes.
   */
  private function selectedNodeChanged():void {
    commitTaxonomyNodeForm();
    var newNode:TaxonomyNode = selectedValueExpression.getValue() as TaxonomyNode;
    changeSelectedNode(newNode);
  }

  /**
   * Updates the ui depending on the selected node.
   * @param newNode The selected node.
   */
  private function changeSelectedNode(newNode:TaxonomyNode):void {
    TaxonomyUtil.setLatestSelection(newNode);
    updateActions(newNode);
    updateTaxonomyNodeForm(newNode);
    updateColumns(newNode);
    if (!newNode) {
      getRootColumnPanel().selectNode(null);
    }
  }


  /**
   * Enabling/Disabling of the toolbar buttons.
   * @param newNode The selected node.
   */
  private function updateActions(newNode:TaxonomyNode):void {
    var addButton:Button = getTopToolbar().find('itemId', 'add')[0] as Button;
    addButton.setDisabled(!newNode || !newNode.isExtendable());//disable add button if node is not extendable

    var deleteButton:Button = getTopToolbar().find('itemId', 'delete')[0] as Button;
    deleteButton.setDisabled(!newNode || newNode.isRoot() || !newNode.isLeaf());
  }

  /**
   * Updates after the selected node has been changed. The document form dispatcher is updated afterwards
   * with the selected content or hidden, if no content is selected.
   * @param node The node to display the Document Form Dispatcher for.
   */
  public function updateTaxonomyNodeForm(node:TaxonomyNode):void {
    getDisplayedTaxonomyNodeExpression().setValue(node);
    var dfd:Container = find('itemId', 'documentFormDispatcher')[0];

    if (node && !node.isRoot()) {
      var content:Content = session.getConnection().getContentRepository().getContent(node.getRef());
      if (content) {//null after content deletion
        editorContext.getApplicationContext().set('taxonomy_node_level', node.getLevel());
        content.invalidate(function ():void {
          ValueExpressionFactory.create(TaxonomyNode.CONTENT_PROPERTY_DISPLAY_NAME, content).addChangeListener(selectedTaxonomyNameChanged);
          getDisplayedTaxonomyContentExpression().setValue(content);
          dfd.show();

          ensureExpandState(dfd, content, attachBlurListener);
        });
      }
      else {
        dfd.hide();
      }
    } else {
      //hide the document dispatcher panel!
      dfd.hide();
    }
  }

  private function ensureExpandState(dfd:Container, content:Content, callback:Function):void {
    EventUtil.invokeLater(function ():void {
      var formContainer = dfd.find('itemId', content.getType().getName())[0];
      var nameField:TextField = formContainer.find('name', TaxonomyStudioPluginSettings_properties.INSTANCE.taxonomy_display_property)[0] as TextField;

      var parent:Panel = nameField.findParentByType(collapsibleFormPanel.xtype) as Panel;

      EventUtil.invokeLater(function ():void {
        if (parent) {
          parent.expand(false);
          callback(nameField);
        }
        else {
          callback(nameField);
        }
      });
    });
  }

  private function attachBlurListener(nameField:TextField):void {
    if (nameField && nameField.getValue() === TaxonomyStudioPluginSettings_properties.INSTANCE.taxonomy_default_name) {
      nameField.focus(true);
    }
    nameField.addListener('blur', onNameFieldBlur);
  }


  /**
   * Blur event listener handler that checks if a renaming is required
   * and fires a commit command then.
   * @param nameField
   */
  private function onNameFieldBlur(nameField:Field):void {
    nameField.removeListener('blur', onNameFieldBlur);
    //maybe the focus was lost because an action was triggered, so we delay the disable action
    setBusy(true);
    EventUtil.invokeLater(function():void {
      setBusy(true);
      var node:TaxonomyNode = getSelectedValueExpression().getValue();
      var content:Content = session.getConnection().getContentRepository().getContent(node.getRef());
      content.invalidate(function ():void {
        //mmh, not the best check, but the node name is already escaped
        if (TaxonomyUtil.escapeHTML(content.getName()) !== node.getName() || !content.isPublished()) {
          node.commitNode(function ():void {
            content.invalidate(function ():void {
              refreshNode(node);
              setBusy(false);
            });
          });
        }
        else {
          setBusy(false);
        }
      });
    });
  }

  /**
   * Fired for the name change of the current selected node, updates
   * the corresponding column afterwards.
   */
  private function selectedTaxonomyNameChanged(contentDisplayNameVE:ValueExpression):void {
    var newName:String = contentDisplayNameVE.getValue();
    //A regular reload is fired once the selected node changes. So we only have too
    //update the node name without reloading the complete node.
    var node:TaxonomyNode = getSelectedValueExpression().getValue();
    if (node && node.getName() !== newName) {
      node.setName(newName);
      var column:TaxonomyExplorerColumn = getColumnContainer(node);
      column.updateNode(node);
    }
  }

  /**
   * Commits the changes on a node and refreshes the UI afterwards.
   */
  private function commitTaxonomyNodeForm():void {
    var node:TaxonomyNode = getDisplayedTaxonomyNodeExpression().getValue() as TaxonomyNode;
    var content:Content = getDisplayedTaxonomyContentExpression().getValue() as Content;

    if (node && !node.isRoot() && content.isCheckedOutByCurrentSession()) {
      node.commitNode(function ():void {
        content.invalidate(function ():void {
          refreshNode(node);
        });
      });
    }
  }

  /**
   * Transforms the disabled status of the taxonomy paste button.
   * @param value
   * @return
   */
  protected function transformPaste(value:Boolean):Boolean {
    var selection:TaxonomyNode = selectedValueExpression.getValue();
    if (selection.isRoot()) {    //disable for roots
      return true;
    }
    if (!clipboardValueExpression.getValue()) { // disable if there is no value in the clipboard
      return true;
    }
    if (clipboardValueExpression.getValue().getRef() === selection.getRef()) { //disable if same node is selected
      return true;
    }
    return true;
  }

  /**
   * Executes the column update (adding/removing) depending on the type of the node.
   * @param node The selected node.
   */
  public function updateColumns(node:TaxonomyNode, callback:Function = undefined):void {
    setBusy(true);
    var columnsContainer:Container = getColumnsContainer();
    if (node) {
      var level:int = node.getLevel();
      if (columnsContainer.items.length > level) {
        var columnsToBeRemoved:Array = columnsContainer.items.getRange(level);
        columnsToBeRemoved.forEach(function (column:TaxonomyExplorerColumn):void {
          columnsContainer.remove(column, true);
        });
      }
      if (node.isExtendable() && !node.isLeaf()) {
        addColumn(node);
      }
    } else {
      columnsContainer.removeAll(true);
    }

    columnsContainer.doLayout(false, true);
    setBusy(false);
    scrollRight();
  }

  private function scrollRight():void {
    EventUtil.invokeLater(function ():void {
      var columns:Container = getColumnsContainer();
      if (columns.el.dom.childNodes[0]) {
        columns.el.dom.childNodes[0].scrollLeft = 10000;
      }
    });
  }

  /**
   * Adds a new column for the given node.
   * @param node The node the column should be created for.
   */
  private function addColumn(node:TaxonomyNode):void {
    var columnsContainer:Container = getColumnsContainer();
    var column:TaxonomyExplorerColumn = new TaxonomyExplorerColumn(taxonomyExplorerColumn({
      parentNode: node,
      itemId: "taxonomyColumn-" + node.getLevel(),
      clipboardValueExpression: getClipboardValueExpression(),
      selectedNodeExpression: selectedValueExpression,
      siteSelectionExpression: siteSelectionExpression
    }));
    columnsContainer.add(column);
//    EventUtil.invokeLater(function ():void {
//      if(columnsContainer.el) {
//        var columnsContainerDom:* = columnsContainer.el.dom;
//        columnsContainerDom.scrollTop = 100000;
//      }
//    });
  }

  /**
   * Moves the source node to the target node and updates the UI.
   * @param sourceNode
   * @param targetNode
   */
  public function moveNode(sourceNode:TaxonomyNode, targetNode:TaxonomyNode):void {
    setBusy(true);
    clipboardValueExpression.setValue(null);
    sourceNode.moveTo(targetNode.getRef(), function (updatedNode:TaxonomyNode):void {
      targetNode.invalidate(function ():void {
        getColumnContainer(targetNode).updateNode(targetNode);
        TaxonomyNodeFactory.loadPath(updatedNode.getTaxonomyId(), updatedNode.getRef(), updatedNode.getSite(),
                function (nodeList:TaxonomyNodeList):void {
                  TaxonomyEditorBase.getInstance().selectNode(nodeList);
                });
      });
    });
  }

  /**
   * Returns the container that contains the dynamic explorer columns.
   * @return
   */
  public function getColumnsContainer():Container {
    if (!columnsContainer) {
      columnsContainer = find("itemId", "columnsContainer")[0] as Container;
    }
    return columnsContainer;
  }

  /**
   * Updates the ext record of the given node, so that the reformatting is triggered.
   * @param node The node to reformat.
   */
  private function refreshNode(node:TaxonomyNode):void {
    //refresh given node entry (maybe after an update) of a visible column list.
    var nodesColumn:TaxonomyExplorerColumn = getColumnContainer(node);
    if (nodesColumn) {
      nodesColumn.updateNode(node);
    }
  }

  /**
   * Returns true if the node with the given id is marked for copying.
   * Another style will be applied then to mark this node in the column.
   * @param id
   * @return
   */
  public function isMarkedForCopying(id:String):Boolean {
    var selection:TaxonomyNode = getClipboardValueExpression().getValue();
    return selection && selection.getRef() === id;
  }

  /**
   * Selects the ext record of the given node.
   * @param node The node to select.
   * @param force if the selection should be forced or not
   */
  private function selectNode(node:TaxonomyNode, force:Boolean = false):void {
    //refresh given node entry (maybe after an update) of a visible column list.
    var nodesColumn:TaxonomyExplorerColumn = getColumnContainer(node);
    if (nodesColumn) {
      nodesColumn.selectNode(node, force);
    }
  }

  /**
   * Returns the column container in which the given taxonomy node is located.
   * @param node The taxonomy node to retrieve the column from.
   * @return The column grid panel that contains the node.
   */
  public function getColumnContainer(node:TaxonomyNode):TaxonomyExplorerColumn {
    var columnsContainer:Container = find("itemId", "columnsContainer")[0] as Container;
    var position:int = node.getLevel() - 1;
    return getColumnContainerForLevel(position);
  }

  /**
   * Returns the columns container for the given level.
   * @param position The level, starting from -1 which is the root column.
   * @return
   */
  public function getColumnContainerForLevel(position:int):TaxonomyExplorerColumn {
    if (position === -1) {
      return Ext.getCmp('taxonomyRootsColumn') as TaxonomyExplorerColumn;
    }
    var columnContainer:TaxonomyExplorerColumn = columnsContainer.items.itemAt(position) as TaxonomyExplorerColumn;
    return columnContainer
  }

  /**
   * Sets the column panels to disabled while REST operations are in place.
   * @param b If true, the panel is disabled.
   */
  public function setBusy(b:Boolean):void {
    setDisabled(b);
    var view:StandAloneDocumentView = getDocumentForm();
    if (view) {
      //we have to set TextFields to readonly, disabling won't work here since the user can still input if the field is focused.
      var spf:Array = view.find('itemId', 'documentFormDispatcher')[0].findByType('com.coremedia.cms.editor.sdk.config.stringPropertyField');
      for (var i:int = 0; i < spf.length; i++) {
        var spField:StringPropertyField = spf[i] as StringPropertyField;
        var text:TextField = spField.find('itemId', 'stringPropertyField')[0];
        text.setReadOnly(b);
      }
    }
  }

  /**
   * Fired if the site selection combo has been changed.
   */
  private function siteSelectionChanged():void {
    var container:TaxonomySearchField = Ext.getCmp('taxonomySearchField') as TaxonomySearchField;
    container.setValue("");
    getRootColumnPanel().initColumn();
  }

  private function getDocumentForm():StandAloneDocumentView {
    return Ext.getCmp('taxSplitPanelRight') as StandAloneDocumentView;
  }
}
}
