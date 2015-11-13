package com.coremedia.cms.studio.queryeditor.components {

import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.studio.queryeditor.QueryEditor_properties;
import com.coremedia.cms.studio.queryeditor.config.conditionEditorBase;
import com.coremedia.cms.studio.queryeditor.config.contentQueryEditor;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Container;
import ext.Ext;
import ext.layout.CardLayout;

public class ContentQueryEditorBase extends Container {
  public static const SETTINGS_STRUCT_NAME = "fq";

  /**
   * Enumeration of all common model properties.
   */
  public static const MODEL_PROPERTIES:Object = {
    APPLICABLE_CONDITIONS: "applicableConditions",
    APPLIED_CONDITIONS: "appliedConditions"
  };

  protected const CONDITION_VIEW:String = "conditionView";
  protected const IS_EXPERT_EDITED:String = "isExpertEdited";

  //suffix for the query property names containing expert edited expressions

  protected const EXPERT_SUFFIX:String = "_expert";
  private var commonModel:Bean;
  private var conditions:Array;
  private var conditionsPerDocumentType:Object;
  private var bindTo:ValueExpression;
  private var queryPropertyName:String;
  private var documentTypesPropertyName:String;

  private var sortingPropertyName:String;
  private var allFilterQueryProperties:Array;

  private var query:Struct;
  private var isExpertEdited:ValueExpression;
  private var viewValueExpression:ValueExpression;

  public function ContentQueryEditorBase(config:contentQueryEditor = null) {
    super(contentQueryEditor(config));

    conditions = config.conditions;
    conditionsPerDocumentType = getConditionsPerDocumentType(conditions);
    bindTo = config.bindTo;
    queryPropertyName = config.queryPropertyName;
    documentTypesPropertyName = config.documentTypesPropertyName;
    sortingPropertyName = config.sortingPropertyName;
  }

  protected override function afterRender():void{
    super.afterRender();

    isExpertEdited = ValueExpressionFactory.create(IS_EXPERT_EDITED, getCommonModel());
    isExpertEdited.addChangeListener(resolveActiveView);
    getQuery().loadValue(function(queryStruct:Struct):void{
      query = queryStruct;
      query.addValueChangeListener(resolveIsExpertEdited);
      resolveIsExpertEdited();
    });
  }

  /**
   * Common model used by the ContentQueryEditor and all ContentQueryEditor subcomponents.
   */
  public function getCommonModel():Bean {
    if (!commonModel) {
      commonModel = beanFactory.createLocalBean(initCommonModelTemplate());
    }
    return commonModel;
  }

  /**
   * Gets the condition title based on its propertyName.
   * @param key A condition's propertyName.
   * @return Condition title.
   */
  public static function getConditionTitle(key:String):String {
    return QueryEditor_properties.INSTANCE['DCQE_label_condition_' + key.replace('\.','_')] || key;
  }

  /**
   * Update applicable conditions according to the selected document types.
   *
   * @param selectedDocumentTypes selected document types
   */
  public function updateApplicableConditions(selectedDocumentTypes:Array):void {
    var key:String = ContentQueryEditorBase.MODEL_PROPERTIES.APPLICABLE_CONDITIONS,
        applicableConditions:Array = calculateApplicableConditions(selectedDocumentTypes);

    getCommonModel().set(key, applicableConditions);
    StudioUtil.reloadPreview();
  }

  public function getConditionEditorConfigByXtype(xtype:String):conditionEditorBase {
    var i:int = 0,
        conditionEditorConfig:conditionEditorBase;
    //TODO: convert to map for optimization
    for(i=0; i < conditions.length; i++){
      conditionEditorConfig = conditions[i];
      if (conditionEditorConfig.xtype == xtype){
        return conditionEditorConfig;
      }
    }
    return null;
  }

  public function getConditionEditorConfigByPropertyName(conditionPropertyName:String):conditionEditorBase {
    return getConditionEditorConfigs().filter(function(config:conditionEditorBase):Boolean{
      return config.propertyName == conditionPropertyName;
    })[0];
  }

  public function getConditionEditorConfigs():Array {
   return conditions;
  }

  /**
   * If the method is called from EXML, the constructor was not yet called, local fields will be undefined, thus config
   * object containing needed properties should be passed as argument.
   *
   * @param config
   * @return
   */
  public function getQuery(config:contentQueryEditor=null):ValueExpression {
    var bve:ValueExpression = config ? config.bindTo : bindTo,
        qpn:String = config ? config.queryPropertyName : queryPropertyName;
    return bve.extendBy('properties', qpn);
  }

  public function getQuerySubProperty(subpropertyName:String, config:contentQueryEditor=null):ValueExpression {
    return getQuery(config).extendBy(subpropertyName);
  }

  public function getFilterQuery(config:contentQueryEditor=null):ValueExpression {
    return getQuery(config).extendBy(SETTINGS_STRUCT_NAME);
  }

  public function getFilterQuerySubProperty(subpropertyName:String, config:contentQueryEditor=null):ValueExpression {
    return getFilterQuery(config).extendBy(subpropertyName);
  }

  public function getDocumentTypesRemote(config:contentQueryEditor=null):ValueExpression{
    return getFilterQuerySubProperty(documentTypesPropertyName, config);
  }

  public function getSortingRemote(config:contentQueryEditor=null):ValueExpression{
    return getQuerySubProperty(sortingPropertyName, config);
  }

  protected static function getSwitchLinkText(view:String):String {
    return view == "assistant" ?
      QueryEditor_properties.INSTANCE.DCQE_label_switch_to_expert_view
      :
      QueryEditor_properties.INSTANCE.DCQE_label_switch_to_assistant_view;
  }

  protected function switchView():void {
    var cardPanel:Container = find('itemId', 'switch')[0] as Container;
    if(((cardPanel.getLayout() as CardLayout).activeItem as Container).getItemId() == 'assistant') {
      (cardPanel.getLayout() as CardLayout).setActiveItem('expert');
      viewValueExpression.setValue('expert');
    }
    else {
      (cardPanel.getLayout() as CardLayout).setActiveItem('assistant');
      viewValueExpression.setValue('assistant');
    }
  }

  protected function getConditionViewValueExpression():ValueExpression {
    if(!viewValueExpression) {
      viewValueExpression = ValueExpressionFactory.create(CONDITION_VIEW, beanFactory.createLocalBean({conditionView:'assistant'}));
    }
    return viewValueExpression;
  }

  /**
   *  Reverts all changes done in the expert view.
   */
  protected function revertExpertChanges():void {
    var queryStruct:Struct = query,
        fqStruct:Struct = queryStruct.get(SETTINGS_STRUCT_NAME);

    //erase all expert expressions from the query
    queryStruct.getType().removeProperty(sortingPropertyName + EXPERT_SUFFIX);
    fqStruct && fqStruct.getType().removeProperty(documentTypesPropertyName + EXPERT_SUFFIX);
    fqStruct && getAllFilterQueryProperties().forEach(function(fqPropertyName:String):void{
      fqStruct.getType().removeProperty(fqPropertyName + EXPERT_SUFFIX);
    });
  }

  protected function resolveRevertExpertChangesDisabled(isExpertEdited:Boolean):*{
    return !isExpertEdited || bindTo.extendBy('checkedOutByOther').getValue();
  }

  /**
   * Resolves if the query was edited in the expert mode.
   * @param query query struct
   */
  private function resolveIsExpertEdited():void {
    var allFqPropertyNames:Array = getAllFilterQueryProperties(conditions);
    isExpertEdited.setValue(checkIfExpertEdited(query, allFqPropertyNames, sortingPropertyName));
  }

  /**
   * If the query was edited only in the assistant mode, display the assistant mode. If it was edited in the expert mode,
   * display the expert mode.
   */
  private function resolveActiveView(isExpertEdited:ValueExpression):void {
    var isExpertEditedValue:Boolean = isExpertEdited.getValue();
    if(!Ext.isEmpty(isExpertEditedValue)){
      getCommonModel().set(CONDITION_VIEW, isExpertEditedValue ? "expert" : "assistant");
    }
  }

  /**
   * @param query query struct
   * @param allFqPropertyNames array of filter query property names
   * @param sortingPropertyName sorting property name
   * @return true if any of the query chunks are edited in the expert mode, false otherwise
   */
  private function checkIfExpertEdited(query:Struct, allFqPropertyNames:Array, sortingPropertyName:String):Boolean {
    var fqStruct:Struct = query.get(SETTINGS_STRUCT_NAME);

    return !!(query.get(sortingPropertyName + EXPERT_SUFFIX)
              || (fqStruct && fqStruct.get(documentTypesPropertyName + EXPERT_SUFFIX))
              || (fqStruct && allFqPropertyNames.some(function(fqPropertyName:String):Boolean{
                   return !!fqStruct.get(fqPropertyName + EXPERT_SUFFIX);
                 }))
            );
  }

  /**
   * Extract all available filter query properties for which the conditions can be applied.
   *
   * @param conditions array of config objects, it needs to be passed if the method is called before the component is
   *        rendered
   * @return array of property names
   */
  protected function getAllFilterQueryProperties(conditions:Array=null):Array {
    if(!allFilterQueryProperties) {
      conditions = conditions || this.conditions;
      if(conditions){
        allFilterQueryProperties = [];
        conditions.forEach(function(condition:conditionEditorBase):void{
          allFilterQueryProperties.push(condition.propertyName);
        });
      }
    }
    return allFilterQueryProperties;
  }

  /**
   * Extract all available document types from the conditionEditors' configuration.
   *
   * @param conditions conditionEditors' configuration
   * @return all available document types
   */
  protected static function getAllDocumentTypes(conditions:Array):Array {
    var allDocumentTypes:Array = [];
    var condition:conditionEditorBase;
    var documentType:String;

    for(var i=0; i < conditions.length; i++) {
      condition = conditions[i];
      for(var j=0; j<condition.documentTypes.length; j++) {
        documentType = condition.documentTypes[j];
        if(allDocumentTypes.indexOf(documentType) == -1){
          allDocumentTypes.push(documentType);
        }
      }
    }
    return allDocumentTypes;
  }

  private function calculateApplicableConditions(selectedDocumentTypes:Array):Array {
    var selectedType:Object,
        condition:conditionEditorBase,
        conditionsForType:Array,
        conditionsUnion:Array = [],
        conditionsIntersection:Array =  [],
        i,
        j;

    // get the union of all applicable conditions to any of the selected doctypes
    for(i=0; i<selectedDocumentTypes.length; i++){
      selectedType = selectedDocumentTypes[i];
      conditionsForType = conditionsPerDocumentType[selectedType];
      for(j=0; j<conditionsForType.length; j++) {
        condition = conditionsForType[j];
        if(conditionsUnion.indexOf(condition) == -1) {
          conditionsUnion.push(condition);
        }
      }
    }

    // remove the conditions not applicable to all selected doctypes
    conditionsIntersection = conditionsUnion.slice(0);
    for(i=0; i<conditionsUnion.length; i++) {
      condition = conditionsUnion[i];
      for(j=0; j<selectedDocumentTypes.length; j++) {
        selectedType = selectedDocumentTypes[j];
        conditionsForType = conditionsPerDocumentType[selectedType];
        if(conditionsForType.indexOf(condition) == -1) {
          conditionsIntersection.splice(conditionsIntersection.indexOf(condition),1);
        }
      }
    }

    return conditionsIntersection;
  }

  private static function getConditionsPerDocumentType(conditions:Array):Object {
    var condPDT:Object = {};
    var condition:conditionEditorBase;
    var conditionDocumentTypes:Array;
    var documentType:String;

    for(var i=0; i<conditions.length; i++) {
      condition = conditions[i];
      conditionDocumentTypes = condition.documentTypes;
      for(var j=0; j<conditionDocumentTypes.length; j++){
        documentType = conditionDocumentTypes[j];
        if(!condPDT[documentType]) {
          condPDT[documentType] = [];
        }
        if(condPDT[documentType].indexOf(condition) == -1) {
          condPDT[documentType].push(condition);
        }
      }
    }
    return condPDT;
  }

  /*
   * Initializes template properties object used to create local bean common for all ContentQueryEditor components.
   */
  private static function initCommonModelTemplate():Object {
    var props:Object = {};
    props[MODEL_PROPERTIES.APPLIED_CONDITIONS] = [];
    props[MODEL_PROPERTIES.DOC_TYPES_EXPERT_EXPRESSION] = "";
    props[MODEL_PROPERTIES.SORTING_EXPERT_EXPRESSION] = "";
    return props;
  }

  public override function destroy():void{
    query && query.removeValueChangeListener(resolveIsExpertEdited);
    super.destroy();
  }
}
}
