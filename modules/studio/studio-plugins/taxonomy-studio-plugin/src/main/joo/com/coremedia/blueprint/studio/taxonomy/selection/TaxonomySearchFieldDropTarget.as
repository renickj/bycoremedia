package com.coremedia.blueprint.studio.taxonomy.selection {

import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.dragdrop.DragInfo;
import com.coremedia.ui.data.ValueExpression;

import ext.Component;
import ext.IEventObject;
import ext.config.droptarget;
import ext.dd.DragSource;
import ext.dd.DropTarget;

/**
 * A drop zone for property editors of link list properties
 */
public class TaxonomySearchFieldDropTarget extends DropTarget {

  public static const DROP_TARGET_HOVER_CLASS:String = "drop-target-hover";

  private var component:Component;
  private var propertyValueExpression:ValueExpression;
  private var bindTo:ValueExpression;
  private var propertyName:String;
  private var forceReadOnlyValueExpression:ValueExpression;

  public function TaxonomySearchFieldDropTarget(component:Component, bindTo:ValueExpression, propertyName:String, forceReadOnlyValueExpression:ValueExpression) {
    super(component.getEl(), droptarget({
      ddGroup: "ContentLinkDD"
    }));
    this.addToGroup("ContentDD");
    this.component = component;
    this.bindTo = bindTo;
    this.propertyName = propertyName;
    propertyValueExpression = bindTo.extendBy("properties." + propertyName);
    this.forceReadOnlyValueExpression = forceReadOnlyValueExpression;

    // Lock drop target until the bean property is fully loaded.
    this.lock();
    propertyValueExpression.loadValue(function() {
      unlock();
    });
  }

  /*
   * Drag is not allowed if more than one element is dragged, the
   * target bean is not writable or document types do not match.
   */
  private function allowDrag(dragInfo:DragInfo):* {
    if (!dragInfo) {
      return false;
    }
    if (dragInfo.getContents().length !== 1) {
      return false;
    }
    if (!isWritable()) {
      return false;
    }
    if (dragInfo.hasInvalidIds(bindTo, propertyName)) {
      // no. the user should have received appropriate feedback, so just don't do it.
      return;
    }
    return true;
  }

  override public function notifyEnter(source:DragSource, e:IEventObject, data:Object):String {
    return notifyOver(source, e, data);
  }

  override public function notifyOver(source:DragSource, e:IEventObject, data:Object):String {
    var dragInfo:DragInfo = DragInfo.makeDragInfo(source, data, component);
    if (allowDrag(dragInfo)) {
      return dropAllowed;
    } else {
      return dropNotAllowed;
    }
  }

  override public function notifyOut(source:DragSource, e:IEventObject, data:Object):void {

  }

  override public function notifyDrop(source:DragSource, e:IEventObject, data:Object):Boolean {
    // to avoid deleted items reappear after a successive drop
    propertyValueExpression = bindTo.extendBy("properties." + propertyName);

    var dragInfo:DragInfo = DragInfo.makeDragInfo(source, data, component);
    if (!isWritable()) {
      return false;
    }

    if (!allowDrag(dragInfo)) {
      return false;
    }

    var content:Content = dragInfo.getContents()[0] as Content;
    propertyValueExpression.setValue(propertyValueExpression.getValue().concat(content));
    return true;
  }

  private function isWritable():Boolean {
    var targetValue:* = bindTo.getValue();
    if (!(targetValue is Content)) {
      return false;
    }
    if (forceReadOnlyValueExpression.getValue()) {
      return false;
    }
    return !(targetValue as Content).isCheckedOutByOther();
  }

  private function droppingRowChanged(valueExpression:ValueExpression):void {
    var row:* = valueExpression.getValue();
    if (row === -1) {
      showDragFeedback();
    } else {
      clearDragFeedback();
    }
  }

  private function showDragFeedback():void {
    component.addClass(DROP_TARGET_HOVER_CLASS);
  }

  private function clearDragFeedback():void {
    component.removeClass(DROP_TARGET_HOVER_CLASS);
  }
}
}

