package com.coremedia.cms.studio.queryeditor.components {

import com.coremedia.cms.studio.queryeditor.QueryEditor_properties;
import com.coremedia.cms.studio.queryeditor.config.applicableConditions;
import com.coremedia.cms.studio.queryeditor.config.conditionDroppable;
import com.coremedia.cms.studio.queryeditor.config.conditionEditorBase;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;

import ext.ComponentMgr;
import ext.Container;
import ext.Ext;
import ext.Panel;
import ext.config.panel;

public class ApplicableConditionsBase extends Container {

  private var checkedOutValueExpression:ValueExpression;
  // map of conditionEditorBase config objects by group
  private var groupedConditions:Object;
  private var dcqe:ContentQueryEditor;
  private var conditionDroppables:Array;
  private var commonModel:Bean;
  private var groupsCt:Container;

  public function ApplicableConditionsBase(config:applicableConditions = null) {
    super(applicableConditions(Ext.apply({}, config)));

    checkedOutValueExpression = config.bindTo.extendBy('checkedOutByOther');
    groupedConditions = groupConditions(config.conditions);
  }

  override protected function afterRender():void {
    super.afterRender();
    renderGroupedConditions();
    listenToModelChanges();
  }

  /**
   * Listen to the changes of the applicable conditions and the applied conditions.
   */
  private function listenToModelChanges():void {
    var applicableConditionsKey:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLICABLE_CONDITIONS,
        appliedConditionsKey:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS;

    getCommonModel().addPropertyChangeListener(applicableConditionsKey, onApplicableChange);
    updateWithApplicable(getCommonModel().get(applicableConditionsKey)); //initial state

    getCommonModel().addPropertyChangeListener(appliedConditionsKey, onAppliedChange);
    updateUIWithApplied(getCommonModel().get(appliedConditionsKey)); //initial state

    //disable conditions if checked out by other
    checkedOutValueExpression.addChangeListener(enableOrDisable);
    enableOrDisable(checkedOutValueExpression);
  }

  private function onApplicableChange(e:PropertyChangeEvent){
    updateWithApplicable(e.newValue as Array);
  }

  private function onAppliedChange(e:PropertyChangeEvent){
    updateUIWithApplied(e.newValue as Array);
  }

  private function updateWithApplicable(applicableConditions:Array):void {
    updateUIWithApplicable(applicableConditions);
    updateAppliedWithApplicable(applicableConditions);
  }

  /**
   * If some of the applied conditions were removed from the applicable conditions, they need to also be removed
   * from the applied conditions.
   *
   * @param applicableConditions array of condition editor config objects
   */
  private function updateAppliedWithApplicable(applicableConditions:Array):void {
    var appliedConditions:Array = getCommonModel().get(ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS),
        newAppliedConditions:Array = [];

    appliedConditions.forEach(function(appliedCondition:conditionEditorBase){
      var stillApplicable:Boolean = applicableConditions.some(function(applicableCondition:conditionEditorBase){
        return appliedCondition.xtype == applicableCondition.xtype;
      });
      if(stillApplicable){
        newAppliedConditions.push(appliedCondition);
      }
    });

    getCommonModel().set(ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS, newAppliedConditions);
  }

  /**
   * Show applicable conditions and hide conditions that are not applicable.
   *
   * @param applicableConditions array of conditionDroppable configuration objects
   */
  private function updateUIWithApplicable(applicableConditions:Array):void {
   var allConditionDroppables:Array = findByType(ConditionDroppable);

    applicableConditions = applicableConditions || []; //in case applicable conditions are still unknown

    // show or hide conditions
    allConditionDroppables.forEach(function(component:ConditionDroppable){
     var isApplicable:Boolean = applicableConditions.some(function(applicable:conditionEditorBase){
       return component.getConditionEditorXtype() == applicable.xtype;
     });
     component[isApplicable ? 'show' : 'hide']();
   });

    // disable collapsible groups that have no applicable conditions
    // NOTE: more extensible solution would be to have the "isEmpty" flags for each of the groups in the local model
    // and a bindDisable plugin for every groupPanel to react on the changes of the local model.
    getGroupsCt().items.each(function(groupPanel:Panel){
      var isEmpty:Boolean = groupPanel.items.filter('hidden', 'false').length == 0;
      groupPanel.setDisabled(isEmpty);
    });
  }

  /**
   * Enable conditions that are not applied and disable conditions that are applied.
   *
   * @param appliedConditions array of conditionDroppable configuration objects
   */
  private function updateUIWithApplied(appliedConditions:Array):void {
    Ext.each(getConditionDroppables(), function(conditionDroppable:ConditionDroppable){
      var applied:Boolean = appliedConditions.some(function(appliedCondition:conditionEditorBase){
            return conditionDroppable.getConditionEditorXtype() == appliedCondition.xtype;
          });
      if(applied){
        conditionDroppable.hide();
      } else {
        conditionDroppable.show();
        checkedOutValueExpression.loadValue(function(checkedOutByOther:Boolean){
          if(!checkedOutByOther) {
            conditionDroppable.show();
          }
        });
      }
    }, null);

  }

  private function enableOrDisable(value:ValueExpression):void {
    var checkedOutByOther:Boolean = value.getValue(),
        conditions:Array = findByType(ConditionDroppable);

    if(checkedOutByOther != undefined){
      if(checkedOutByOther){
        conditions.forEach(function(condition:ConditionDroppable){
          condition.hide();
        });
      } else {
        conditions.forEach(function(condition:ConditionDroppable){
          var appliedConditions:Array = getCommonModel().get(ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS);
          var isApplied:Boolean = appliedConditions.some(function(appliedCondition:conditionEditorBase){
                return condition.getConditionEditorXtype() == appliedCondition.xtype;
              });
          if(!isApplied){
            condition.show();
          }
        });
      }
    }
  }

  private function renderGroupedConditions():void {
    var conditionsGroup:Array;
    var condition:conditionEditorBase;
    var groupPanel:Panel;
    var group:*;

    for(group in groupedConditions) {
      conditionsGroup = groupedConditions[group];
      groupPanel = ComponentMgr.create(
              new panel({
                title:getGroupTitle(group),
                cls:"applicable-condition-group",
                bodyCssClass:"applicable-condition-group-body"
              })
      ) as Panel;
      for(var i:int=0; i<conditionsGroup.length; i++) {
        condition = conditionsGroup[i];
        groupPanel.add(
          new conditionDroppable({
            text: ContentQueryEditorBase.getConditionTitle(condition.propertyName),
            conditionEditorXtype:condition.xtype
          })
        );
      }
      getGroupsCt().add(groupPanel);
    }

    this.doLayout();
  }

  private function getGroupsCt():Container {
    if(!groupsCt){
      groupsCt = find('itemId', 'groupsCt')[0];
    }
    return groupsCt;
  }

  private function getConditionDroppables():Array {
    if(!conditionDroppables){
      conditionDroppables = findByType(ConditionDroppable);
    }
    return conditionDroppables;
  }

  private function groupConditions(conditions:Array):Object {
    var condition:conditionEditorBase;
    var group:String;
    var groupedConditions = {};

    for(var i:int=0; i<conditions.length; i++) {
      condition = conditions[i];
      group = condition.group;
      if(!groupedConditions[group]) {
        groupedConditions[group] = [];
      }
      groupedConditions[group].push(condition);
    }

    return groupedConditions;
  }

  private function getGroupTitle(groupName:String):String {
    return QueryEditor_properties.INSTANCE['DCQE_title_condition_group_' + groupName] || groupName;
  }

  private function getContentQueryEditor():ContentQueryEditor {
    if (!dcqe) {
      dcqe = this.findParentByType(ContentQueryEditor) as ContentQueryEditor;
    }
    return dcqe;
  }

  private function getCommonModel():Bean {
    if(!commonModel){
      commonModel = getContentQueryEditor() && getContentQueryEditor().getCommonModel();
    }
    return commonModel;
  }

  public override function destroy():void {
    var applicableConditionsKey:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLICABLE_CONDITIONS,
        appliedConditionsKey:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS;

    //if the component wasn't even rendered, commonModel will be undefined
    if(commonModel){
      commonModel.removePropertyChangeListener(applicableConditionsKey, onApplicableChange);
      commonModel.removePropertyChangeListener(appliedConditionsKey, onAppliedChange);
    }
    checkedOutValueExpression.removeChangeListener(enableOrDisable);

    super.destroy();
  }
}
}
