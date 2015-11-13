package com.coremedia.ecommerce.studio.dragdrop {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.data.ValueExpression;

import ext.Component;
import ext.IEventObject;
import ext.config.droptarget;
import ext.dd.DragSource;
import ext.dd.DropTarget;

/**
 * A drop zone for string properties that
 * support a single link to a catalog object per property.
 */
public class CatalogLinkDropTarget extends DropTarget {

  public static const SINGLE_LIST_HOVER_CLASS:String = "single-list-hover";

  private var component:Component;
  private var valueExpression:ValueExpression;
  private var readOnlyValueExpression:ValueExpression;
  private var catalogObjectType:String;
  private var multiple:Boolean;
  private var createStructFunction:Function;

  public function CatalogLinkDropTarget(component:Component,
                                        valueExpression:ValueExpression,
                                        catalogObjectType:String,
                                        forceReadOnlyValueExpression:ValueExpression,
                                        multiple:Boolean = false,
                                        createStructFunction:Function = null) {
    super(component.getEl(), droptarget({
      ddGroup: "ContentDD"
    }));
    this.addToGroup("ContentDD");
    this.component = component;
    this.component['CatalogLinkDropTarget'] = this;
    this.valueExpression = valueExpression;
    this.catalogObjectType = catalogObjectType;
    this.readOnlyValueExpression = forceReadOnlyValueExpression;
    this.multiple = multiple;
    this.createStructFunction = createStructFunction;
  }


  private function allowDrag(dragInfo:CatalogDragInfo):* {
    if (!dragInfo) {
      return false;
    }
    if (!isWritable()) {
      return false;
    }

    if (!multiple) {
      if (dragInfo.getCatalogObjects().length !== 1) {
        return false;
      }
    }

    var catalogObject:CatalogObject = dragInfo.getCatalogObjects()[0];
    if (!CatalogHelper.getInstance().isSubType(catalogObject, catalogObjectType)) {
      return false;
    }

    //prevent dropping catalog objects from stores of another site
    if(this.component['bindTo']) {
      var componentOwnerContent:Content = this.component['bindTo'].getValue();
      var siteId:String = editorContext.getSitesService().getSiteIdFor(componentOwnerContent);
      if(siteId !== catalogObject.getStore().getSiteId()) {
        return false;
      }
    }


    return !component.disabled;
  }

  private function isWritable():Boolean {
    return !(readOnlyValueExpression.getValue() === true);
  }

  override public function notifyOver(source:DragSource, e:IEventObject, data:Object):String {
    return handleOverDrag(source, e, data);
  }
  
  override public function notifyEnter(source:DragSource, e:IEventObject, data:Object):String {
   return handleOverDrag(source, e, data);
  }

  private function handleOverDrag(source:DragSource, e:IEventObject, data:Object):String {
    var dragInfo:CatalogDragInfo = CatalogDragInfo.makeDragInfo(source, data, component);
    if (allowDrag(dragInfo)) {
      component.addClass(SINGLE_LIST_HOVER_CLASS);
      return dropAllowed;
    } else {
      return dropNotAllowed;
    }
  }

  override public function notifyOut(source:DragSource, e:IEventObject, data:Object):void {
    component.removeClass(SINGLE_LIST_HOVER_CLASS);
  }

  override public function notifyDrop(source:DragSource, e:IEventObject, data:Object):Boolean {
    var dragInfo:CatalogDragInfo = CatalogDragInfo.makeDragInfo(source, data, component);
    if (!allowDrag(dragInfo)) {
      return false;
    }

    if (createStructFunction) {
      createStructFunction.apply();
    }

    if (valueExpression.isLoaded()) {
      setValue(dragInfo);
    } else {
      valueExpression.loadValue(function():void {
        setValue(dragInfo);
      });
    }
    component.removeClass(SINGLE_LIST_HOVER_CLASS);
    return true;
  }

  private function setValue(dragInfo:CatalogDragInfo):void {
    if (multiple) {
      var oldIds:Array = valueExpression.getValue();
      var newIds:Array = oldIds ? oldIds : [];
      //append the ids of the dragged catalog objects
      var catalogObjects:Array = dragInfo.getCatalogObjects();
      for (var i:int = 0; i < catalogObjects.length; i++) {
        //avoid redundant entries
        if (newIds.indexOf(catalogObjects[i].getId()) < 0) {
          newIds = newIds.concat(catalogObjects[i].getId());
        }
      }
      valueExpression.setValue(newIds);
    } else {
      // Set the string to the id of the dragged catalog object
      var catalogObject:CatalogObject = dragInfo.getCatalogObjects()[0];
      valueExpression.setValue(catalogObject.getId());
    }
  }
}
}
