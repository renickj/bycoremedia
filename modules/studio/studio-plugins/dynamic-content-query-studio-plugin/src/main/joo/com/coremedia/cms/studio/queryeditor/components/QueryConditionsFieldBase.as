package com.coremedia.cms.studio.queryeditor.components {

import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.cms.studio.queryeditor.QueryEditor_properties;
import com.coremedia.cms.studio.queryeditor.conditions.ConditionEditorBase;
import com.coremedia.cms.studio.queryeditor.config.conditionEditorBase;
import com.coremedia.cms.studio.queryeditor.config.queryConditionsFieldBase;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.logging.Logger;

import ext.Button;
import ext.Component;
import ext.ComponentMgr;
import ext.Container;
import ext.Ext;
import ext.config.label;

/**
 * Contains and manages all applied conditions (to be more exact - condition editors).
 */
public class QueryConditionsFieldBase extends Container {

  private var dcqe:ContentQueryEditor;
  private var appliedConditionsContainer:Container;
  private var fqValueExpression:ValueExpression;
  private var monitorVE:ValueExpression;
  private var commonModel:Bean;

  public function QueryConditionsFieldBase(config:queryConditionsFieldBase = null) {
    super(queryConditionsFieldBase(Ext.apply({}, config)));
  }

  public function getConditionEditors():Array {
    return getAppliedConditionsContainer().findBy(function(cmp:Component, container:Container):Boolean {
      return cmp is ConditionEditorBase;
    });
  }

  public function getConditionEditor(xtype:String):ConditionEditorBase {
    //there can be only one of specific type applied
    return getAppliedConditionsContainer().findByType(xtype)[0];
  }

  protected override function afterRender():void {
    super.afterRender();
    listenToModelChanges();
    setUpDeleteAllLink();
  }

  private function listenToModelChanges():void {
    var appliedConditionsKey:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS;

    //listen to local changes
    getCommonModel().addPropertyChangeListener(appliedConditionsKey, onAppliedChange);

    fqValueExpression = getContentQueryEditor().getFilterQuery();
    monitorVE = ValueExpressionFactory.createFromFunction(function ():Boolean {
      var queryVE:ValueExpression = getContentQueryEditor().getQuery();
      queryVE.loadValue(Ext.emptyFn);
      fqValueExpression.loadValue(Ext.emptyFn);
      return queryVE.getValue() && fqValueExpression.getValue();
    });

    monitorVE.loadValue(function(queryStruct:Struct){
      //listen to remote changes on the query struct VE because filter query substruct doesn't support change listeners
      monitorVE.addChangeListener(updateLocalFromRemote);
      //but use filter query substruct to update local data
      updateLocalFromRemote();
    });


  }

  private function onAppliedChange(e:PropertyChangeEvent):void {
    var appliedConditions:Array = e.newValue;
    updateUIFromLocal(appliedConditions);
    updateRemoteFromLocal(appliedConditions);
  }

  private function setUpDeleteAllLink():void {
    var deleteAllLink:Button = this.findBy(function(comp:Component):Boolean {
      return comp.initialConfig.itemId === "deleteAll";
    })[0] as Button;
    deleteAllLink.setHandler(removeAllAppliedConditions);
  }

  /**
   * Takes care of the condition removed by the user. Conditions created by the user will save their expressions
   * themselves.
   *
   * @param appliedConditions
   */
  private function updateRemoteFromLocal(appliedConditions:Array):void {
    fqValueExpression.loadValue(function(fq:Struct):void {
      // check if there is a condition present in the remote bean but not applied locally
      // and remove it if there is
      var allConditions:Array = getContentQueryEditor().getConditionEditorConfigs(),
          appliedConditions:Array = getCommonModel().get(ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS),
          isApplied:Boolean,
          condition:conditionEditorBase,
          remoteExpression:String,
          i:Number;

      for(i=0; i<allConditions.length; i++){
        condition = allConditions[i];
        remoteExpression = fq.get(condition.propertyName);
        isApplied = appliedConditions.some(function(appliedCondition:conditionEditorBase){
          return appliedCondition.xtype == condition.xtype;
        });
        if(remoteExpression != undefined && !isApplied){
          if (fq.getType().hasProperty(condition.propertyName)) {
            fq.getType().removeProperty(condition.propertyName);
          } else {
            //shouldn't ever happen
            Logger.error('Trying to delete non-existing filter query substruct property: ' + condition.propertyName);
          }
        }
      }
    });
  }

  /**
   * Updates UI with changes to the applied conditions.
   *
   * @param appliedConditions
   */
  private function updateUIFromLocal(appliedConditions:Array):void {
    var appliedConditionEditors:Array = getConditionEditors();

    // go through the applied conditions list and check if there is an editor for each
    appliedConditions.forEach(function(appliedCondition:conditionEditorBase){
      var applied:Boolean = appliedConditionEditors.some(function(appliedConditionEditor:ConditionEditorBase){
         return appliedCondition.xtype == appliedConditionEditor.xtype
      });
      if(!applied){
        createConditionEditor(appliedCondition);
      }

    }, null);

    // go through the editors and check if each of them is in the applied conditions list
    appliedConditionEditors.forEach(function(appliedConditionEditor:ConditionEditorBase){
      var stillApplied:Boolean = appliedConditions.some(function(appliedCondition:conditionEditorBase){
        return appliedConditionEditor.xtype == appliedCondition.xtype
      });
      if(!stillApplied){
        deleteConditionEditor(appliedConditionEditor);
      }
    }, null);
  }

  /**
   * Updates UI with changes from the any of the filter query substruct properties. Mainly takes care of properties that
   * are present in the UI but not in the remote bean and vice versa. Updates for the existing properties with their
   * condition editors present in the UI are handled by the condition editors themselves.
   */
  private function updateLocalFromRemote():void {
//    fqValueExpression = getContentQueryEditor().getFilterQuery();

    var fq:Struct;
    fq = fqValueExpression.getValue();
    if(fq){
      // check conditions applied locally and also present in the remote bean, leave those in, skip the rest
      var appliedConditionsKey:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS;
      var appliedConditions:Array = getCommonModel().get(appliedConditionsKey);
      var newAppliedConditions:Array = [];
      appliedConditions.forEach(function(appliedCondition:conditionEditorBase){
        var remoteExpression:String = fq.get(appliedCondition.propertyName);
        if(remoteExpression != undefined){
          newAppliedConditions.push(appliedCondition);
        }
      });

      // check if there are any conditions present in the remote bean but not applied locally
      // and add them if there are
      var allConditions:Array = getContentQueryEditor().getConditionEditorConfigs();
      allConditions.forEach(function(condition:conditionEditorBase){
        var remoteExpression:String = fq.get(condition.propertyName),
            isApplied:Boolean = newAppliedConditions.some(function(appliedCondition:conditionEditorBase){
               return condition.xtype == appliedCondition.xtype;
            });

        if(remoteExpression != undefined && !isApplied){
          newAppliedConditions.push(condition);
        }
      });

      getCommonModel().set(appliedConditionsKey, newAppliedConditions);
    }
  }

  /**
   * Adds the condition to the applied conditions list in the local model.
   *
   * @param conditionEditorXtype condition editor xtype
   */
  public function applyCondition(conditionEditorXtype:String):void {
    var configClone:conditionEditorBase =
            conditionEditorBase(Ext.apply({}, getContentQueryEditor().getConditionEditorConfigByXtype(conditionEditorXtype)));
    configClone.setToDefault = true; //so that the condition editor knows whether it should set the default expression
    addAppliedCondition(configClone);

    /**
     * This code has been added to ensure that after a condition is dropped
     * the fq struct is created. The modification condition createS the Struct
     * hierarchy properly, but if the context and taxonomy condition are added first
     * and the fq struct not set yet an error raises.
     */
    if(!fqValueExpression.getValue()) {
      var contentStruct:Struct = fqValueExpression.getParent().getValue();
      contentStruct.getType().addStructProperty(ContentQueryEditorBase.SETTINGS_STRUCT_NAME);
    }
  }

  /**
   * Removes the condition from the applied conditions list in the local model.
   *
   * @param conditionEditorXtype condition editor xtype
   */
  public function removeCondition(conditionEditorXtype:String):void {
    removeAppliedCondition(getContentQueryEditor().getConditionEditorConfigByXtype(conditionEditorXtype));
  }

  //TODO: we need easier handling of the bean properties of type array (some like the wrapper used in DocumentTypesFilterBase)
  private function addAppliedCondition(condition:conditionEditorBase):void {
    var conditions:Array,
        key:String;
    key = ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS;
    conditions = getCommonModel().get(key).slice(); //we need a copy to trigger the change handler
    conditions.push(condition);
    getCommonModel().set(key, conditions);
  }

  private function removeAppliedCondition(condition:conditionEditorBase):void {
    var conditions:Array,
        key:String,
        index:Number, length:Number;
    key = ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS;
    conditions = getCommonModel().get(key).slice(); //we need a copy to trigger the change handler

    for (index = 0, length = conditions.length; index < length; index += 1) {
      if (conditions[index].xtype === condition.xtype) { break; }
    }
    conditions.splice(index, 1);
    getCommonModel().set(key, conditions);
  }

  private function removeAllAppliedConditions():void {
    MessageBoxUtil.showConfirmation(QueryEditor_properties.INSTANCE.DCQE_delete_condition_title,
            QueryEditor_properties.INSTANCE.DCQE_delete_condition_msg,
            QueryEditor_properties.INSTANCE.DCQE_delete_condition_buttonText,
            function(buttonId:String):void {
      if (buttonId === "ok") {
        var key:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS;
        getCommonModel().set(key, new Array);
      }
    });
  }

  /**
   * Creates a new condition editor and adds it with a label at the end of the list.
   * @param conditionEditorConfig condition editor config object.
   */
  public function createConditionEditor(conditionEditorConfig:conditionEditorBase):void {
        // A drop target has to be at the end of the list at all times, so the
        // condition editor is added just before it.
    var insertPosition:Number = getAppliedConditionsContainer().items.length - 1,
        newLabelConfig:label = new label({
          cls: "x-form-item-label",
          text: QueryEditor_properties.INSTANCE.DCQE_condition_target_title_AND
        }),
        newLabel:Component = ComponentMgr.create(newLabelConfig),
        newConditionEditor:Component = ComponentMgr.create(conditionEditorConfig);

    // The label will pushed towards the drop target when an editor is inserted in the same place.
    getAppliedConditionsContainer().insert(insertPosition, newLabel);
    getAppliedConditionsContainer().insert(insertPosition, newConditionEditor);
    this.doLayout();

  }

  /**
   * Removes the given condition editor, the label that was added with it and
   * the property this condition points to in the remote bean.
   * @param conditionEditor The condition editor marked for deletion.
   */
  public function deleteConditionEditor(conditionEditor:Component):void {
    getAppliedConditionsContainer().getComponent( // .. that is next in line from condition editor
            getAppliedConditionsContainer().items.indexOf(conditionEditor) + 1
    ).destroy(); // destroy that label
    conditionEditor.destroy();
  }

  private function getAppliedConditionsContainer():Container {
    if (!appliedConditionsContainer) {
      appliedConditionsContainer = this.getComponent("appliedConditions") as Container;
    }
    return appliedConditionsContainer;
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
    var appliedConditionsKey:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLIED_CONDITIONS;
    commonModel && commonModel.removePropertyChangeListener(appliedConditionsKey, onAppliedChange);
    monitorVE && monitorVE.removeChangeListener(updateLocalFromRemote);
    super.destroy();
  }
}
}
