package com.coremedia.cms.studio.queryeditor.conditions {

import com.coremedia.cms.studio.queryeditor.config.modificationDateConditionEditor;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.beanFactory;

import ext.Ext;

public class ModificationDateConditionEditorBase extends ConditionEditorBase {

  private const EXPRESSION_PREFIX:String = "freshness:";
  protected const TIME_SLOTS:String = "timeSlots";
  protected const SELECTED_TIME_SLOT:String = "selectedTimeSlot";

  private var timeSlots:Array;
  private var timeSlotsByName:Object;
  private var propertyName:String;
  private var contentType:String;
  private var bindTo:ValueExpression;

  public override function translateAssistantToExpertFormat(assistantFormattedExpression:*):String{
    if(assistantFormattedExpression){
      return assistantFormattedExpression; //since they are the same
    } else {
      return assembleCondition(propertyName, []);
    }
  }

  public function ModificationDateConditionEditorBase(config:modificationDateConditionEditor) {
    timeSlots = config.timeSlots;
    timeSlotsByName = mapNameToTitle(timeSlots);
    super(config);
    propertyName = config.propertyName;
    contentType = config.contentType;
    bindTo = config.bindTo;
  }

  override protected function afterRender():void {
    super.afterRender();
    renderTimeSlots();
    listenToModelChanges();
    super.applyBaseStruct(bindTo, contentType, propertyName);
  }

  /**
   * Populate the dropdown menu with time slots.
   */
  private function renderTimeSlots():void {
    var slots:Array = [];

    Ext.each(timeSlots, function(date:*){
      slots.push(beanFactory.createLocalBean({'name': date.name}));
    }, null);
    getLocalModel().set(TIME_SLOTS, slots);
  }

  private function listenToModelChanges():void {

    //listen to local changes
    getLocalModel().addPropertyChangeListener(SELECTED_TIME_SLOT, updateRemoteFromLocal);

    //listen to changes from remote
    getRemoteValueExpression().addChangeListener(updateLocalFromRemote);
    getRemoteValueExpression().loadValue(updateLocal);
  }

  private function updateLocalFromRemote(remoteValueExpression:ValueExpression):void {
    if (remoteValueExpression.isLoaded()) {
      updateLocal(remoteValueExpression.getValue());
    }
  }

  private function updateLocal(serializedValue:String) {
    var newExpression:String = deserialize(serializedValue);
    if (!newExpression && shouldSetToDefault()) {
      newExpression = timeSlots[0].expression;
    }
    var oldExpression:String = getExpression(getLocalModel().get(SELECTED_TIME_SLOT));
    if(newExpression && newExpression != oldExpression){
      var name :String = getName(newExpression);
      getLocalModel().set(SELECTED_TIME_SLOT, name);
    }
  }

  private function updateRemoteFromLocal(e:PropertyChangeEvent):void {
    var timeSlotName = e.newValue,
        newExpression = getExpression(timeSlotName),
        oldExpression = deserialize(getRemoteValueExpression().getValue());
    if(newExpression && oldExpression != newExpression){
      getRemoteValueExpression().setValue(serialize(newExpression));
    }
  }

  /**
   * Transforms the partial expression to the full condition expression.
   *
   * @param expression partial expression (as given in the condition editor configuration)
   * @return full condition expression
   */
  private function serialize(expression:String):String {
    if(expression){
      return EXPRESSION_PREFIX + '[' + expression + ']';
    } else {
      return undefined;
    }
  }

  /**
   * Transforms full condition expression to partial expression.
   *
   * @param fullExpression full condition expression
   * @return partial expression (as given in the condition editor configuration)
   */
  private function deserialize(fullExpression:String):String {
    var startIndex:Number,
        endIndex:Number,
        expression:String;
    if(fullExpression){
      startIndex = fullExpression.indexOf('[') + 1;
      endIndex = fullExpression.indexOf(']');
      expression = fullExpression.substring(startIndex, endIndex);
    }
    return expression;
  }

  /**
   * Returns the name for the time slot expression.
   *
   * @param expression time slot expression.
   * @return time slot name
   */
  private function getName(expression:String):String {
    var i:Number,
        timeSlot:Object;
    for(i=0; timeSlots.length; i++) {
      timeSlot = timeSlots[i];
      if(timeSlot.expression == expression){
        return timeSlot.name;
      }
    }
    return undefined;
  }


  /**
   * Returns the text displayed in the time slot dropdown menu.
   *
   * @param name time slot name
   * @return time slot text
   */
  protected function getText(name:String):String {
    return timeSlotsByName[name].text;
  }

  /**
   * Returns the time slot expression used in the condition.
   *
   * @param name time slot name
   * @return time slot value
   */
  protected function getExpression(name:String):String {
    return name && timeSlotsByName[name] && timeSlotsByName[name].expression;
  }

  /**
   * Optimizes access to time slot properties.
   *
   * @param timeSlots array of time slots configuration
   * @return map of time slots by their name
   */
  private function mapNameToTitle(timeSlots:Array):Object {
    var map:Object = {},
        timeSlot:Object;
    for(var i=0; i<timeSlots.length; i++){
      timeSlot = timeSlots[i];
      map[timeSlot.name] = timeSlot;
    }
    return map;
  }


  public override function destroy():void {
    getLocalModel().removePropertyChangeListener(SELECTED_TIME_SLOT, updateRemoteFromLocal);
    getRemoteValueExpression().removeChangeListener(updateLocal);
    super.destroy();
  }
}
}
