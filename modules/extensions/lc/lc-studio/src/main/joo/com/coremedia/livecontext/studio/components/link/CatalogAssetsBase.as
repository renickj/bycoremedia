package com.coremedia.livecontext.studio.components.link {

import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.dragdrop.DragInfo;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.livecontext.studio.config.catalogAssets;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.util.PropertyChangeEventUtil;
import com.coremedia.ui.util.ArrayUtils;

import ext.Ext;

import ext.IEventObject;
import ext.config.droptarget;
import ext.dd.DragSource;

import ext.dd.DropTarget;

import ext.grid.GridPanel;

public class CatalogAssetsBase extends GridPanel {

  private var bindTo:ValueExpression;
  private var assetContentTypes:Array;
  private var selectedItems:Array;
  private var dropTarget:DropTarget;

  /**
   * @param config the config object
   */
  public function CatalogAssetsBase(config:catalogAssets) {
    super(config);

    bindTo = config.bindTo;
    assetContentTypes = config.assetContentTypes;

    addListener("render", onRender);
  }

  [ProvideToExtChildren]
  public function getSelectedItems():Array {
    return selectedItems;
  }

  public function setSelectedItems(value:Array):void {
    var oldValue:* = selectedItems;
    selectedItems = value;
    PropertyChangeEventUtil.fireEvent(this, catalogAssets.SELECTED_ITEMS_VARIABLE_NAME, oldValue, value);
  }

  internal function getSelectedValuesExpression():ValueExpression {
    return ValueExpressionFactory.create(catalogAssets.SELECTED_ITEMS_VARIABLE_NAME, this);
  }

  internal static function transformToArray(data:*):Array {
    return data === undefined ? undefined : ArrayUtils.asArray(data);
  }

  private function onRender():void {
    //noinspection JSUnusedGlobalSymbols
    dropTarget = new DropTarget(this.getEl(),
            new droptarget({
              ddGroup: 'ContentLinkDD',
              gridDropTarget: this,
              notifyDrop: notifyDrop,
              notifyOver: notifyOver,
              notifyEnter: notifyOver,
              notifyOut: Ext.emptyFn
            }));
    dropTarget.addToGroup('ContentDD');
  }

  private function notifyDrop(d:DragSource, e:IEventObject, data:Object):Boolean {
    if (notifyOver(d, e, data) !== dropTarget.dropAllowed) {
      return false;
    }
    var dragInfo:DragInfo = DragInfo.makeDragInfo(d, data, this);
    if (!dragInfo) {
      return false;
    }

    var contents:Array = dragInfo.getContents();
    for each (var content:Content in contents) {
      if (!PropertyEditorUtil.isReadOnly(content)) {
        CatalogHelper.getInstance().createOrUpdateProductListStructs(ValueExpressionFactory.createFromValue(content),
                bindTo.getValue());
      }
    }

    return true;
  }

  private function notifyOver(d:DragSource, e:IEventObject, data:Object):String {
    var dragInfo:DragInfo = DragInfo.makeDragInfo(d, data, this);
    if (!dragInfo) {
      return dropTarget.dropNotAllowed;
    }

    var contents:Array = dragInfo.getContents();
    for each (var content:Content in contents) {
      if (!(content is Content)) {
        return dropTarget.dropNotAllowed;
      }
      //check the content type. Todo: consider subtype
      if (assetContentTypes.indexOf(content.getType().getName()) === -1) {
        return dropTarget.dropNotAllowed;
      }
      if (PropertyEditorUtil.isReadOnly(content)) {
        return dropTarget.dropNotAllowed;
      }
    }

    return dropTarget.dropAllowed;
  }

  }
}
