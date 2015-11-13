package com.coremedia.cms.studio.queryeditor.components {

import com.coremedia.cms.studio.queryeditor.QueryEditor_properties;
import com.coremedia.cms.studio.queryeditor.conditions.ConditionEditorBase;
import com.coremedia.cms.studio.queryeditor.config.conditionEditorBase;
import com.coremedia.cms.studio.queryeditor.config.expertView;
import com.coremedia.cms.studio.queryeditor.config.queryChunkField;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.ComponentMgr;
import ext.Container;
import ext.Ext;

public class ExpertViewBase extends Container {

  protected const FILTER_QUERY:String = "filterQuery";
  protected const SORTING_QUERY:String = "sortingQuery";

  private var localModel:Bean;
  private var dcqe:ContentQueryEditor;
  private var expertPropertyNameSuffix:String;
  private var documentTypesPropertyName:String;
  private var bindTo:ValueExpression;
  private var sortingPropertyName:String;
  private var allFilterQueryPropertiesNames:Array;

  private var documentTypesRemote:ValueExpression;
  private var conditionRemotes:Array;
  private var sortingRemote:ValueExpression;

  private const DOC_TYPES_EXPERT_EXPRESSION:String = "documentTypesExpertExpression";
  private const CONDITION_EXPERT_EXPRESSIONS:String = "conditionExpertExpressions";
  private const SORTING_EXPERT_EXPRESSION:String = "sortingExpertExpression";

  private const ATTACHED_CHANGE_LISTENER:String = "____attachedChangeListenerSecretProperty";

  public function ExpertViewBase(config:expertView = null) {
    super(expertView(Ext.apply({}, config)));
    documentTypesPropertyName = config.documentTypesPropertyName;
    sortingPropertyName = config.sortingPropertyName;
    expertPropertyNameSuffix = config.expertPropertyNameSuffix;
    bindTo = config.bindTo;
    allFilterQueryPropertiesNames = config.allFilterQueryPropertiesNames
  }

  protected override function afterRender():void{
    super.afterRender();
    renderTextFieldsForAllExpressions();
  }

  private function renderTextFieldsForAllExpressions():void {
    renderDocumentTypesTextField();
    renderConditionTextFields();
    renderSortingTextField();
    doLayout();
  }

  private function renderDocumentTypesTextField():void {
    var documentTypesDefaultLocal:ValueExpression,
        remoteChangeListener:Function = function (documentTypesRemote:ValueExpression):void {
          documentTypesDefaultLocal.setValue(
            DocumentTypesFilterBase.translateAssistantToExpertFormat(documentTypesRemote.getValue(), documentTypesPropertyName)
          );
        };

    documentTypesRemote = getContentQueryEditor().getDocumentTypesRemote();
    documentTypesDefaultLocal = ValueExpressionFactory.create(DOC_TYPES_EXPERT_EXPRESSION, getLocalModel());

    documentTypesRemote.addChangeListener(remoteChangeListener);
    remoteChangeListener(documentTypesRemote);

    documentTypesRemote[ATTACHED_CHANGE_LISTENER] = remoteChangeListener;

    add(ComponentMgr.create(new queryChunkField({
      bindTo: bindTo,
      defaultExpertExpression: documentTypesDefaultLocal,
      remoteExpertExpression: getExpertRemote(documentTypesRemote, documentTypesPropertyName),
      labelText: QueryEditor_properties.INSTANCE.DCQE_label_selection_of_document_types
    })));
  }

  private function renderConditionTextFields():void {
    var conditionRemote:ValueExpression,
        conditionDefaultLocal:ValueExpression,
        getRemoteChangeListener:Function = function(conditionDefaultLocal:ValueExpression, translateFunc:Function):Function {
          //need to save the reference to conditionDefaultLocal in the closure
          return function(conditionRemote:ValueExpression):void {
            conditionDefaultLocal.setValue(translateFunc(conditionRemote.getValue()));
          };
        },
        remoteChangeListener:Function;

    conditionRemotes = [];

    allFilterQueryPropertiesNames.forEach(function(conditionPropertyName:String){
      var conditionConfig:conditionEditorBase =
              getContentQueryEditor().getConditionEditorConfigByPropertyName(conditionPropertyName),
          conditionEditor:ConditionEditorBase = ConditionEditorBase(ComponentMgr.create(conditionConfig));

      conditionRemote = getContentQueryEditor().getFilterQuerySubProperty(conditionPropertyName);
      conditionDefaultLocal = ValueExpressionFactory.create(
        [CONDITION_EXPERT_EXPRESSIONS, conditionPropertyName].join('.'),
        getLocalModel()
      );
      
      remoteChangeListener =
              getRemoteChangeListener(conditionDefaultLocal, conditionEditor.translateAssistantToExpertFormat);
      conditionRemote.addChangeListener(remoteChangeListener);
      remoteChangeListener(conditionRemote);
      conditionRemote[ATTACHED_CHANGE_LISTENER] = remoteChangeListener;
      
      conditionRemotes.push(conditionRemote);

      add(ComponentMgr.create(new queryChunkField({
        bindTo: bindTo,
        defaultExpertExpression: conditionDefaultLocal,
        remoteExpertExpression: getExpertRemote(conditionRemote, conditionPropertyName),
        labelText: ContentQueryEditorBase.getConditionTitle(conditionPropertyName)
      })));
    });
  }

  private function renderSortingTextField():void {
    var sortingDefaultLocal:ValueExpression,
        remoteChangeListener:Function = function(sortingRemote:ValueExpression):void {
          sortingDefaultLocal.setValue(
            SortingFieldBase.translateAssistantToExpertFormat(sortingRemote.getValue())
          );
        };

    sortingRemote = getContentQueryEditor().getSortingRemote();
    sortingDefaultLocal = ValueExpressionFactory.create(SORTING_EXPERT_EXPRESSION, getLocalModel());

    sortingRemote.addChangeListener(remoteChangeListener);
    remoteChangeListener(sortingRemote);

    sortingRemote[ATTACHED_CHANGE_LISTENER] = remoteChangeListener;

    add(ComponentMgr.create(new queryChunkField({
      bindTo: bindTo,
      defaultExpertExpression: sortingDefaultLocal,
      remoteExpertExpression: getExpertRemote(sortingRemote, sortingPropertyName),
      labelText: QueryEditor_properties.INSTANCE.DCQE_label_sorting_search_query
    })));

  }

  private function getExpertRemote(remote:ValueExpression, propertyName:String):ValueExpression {
    var parentBean:Bean = Bean(remote.getParent().getValue());
    return ValueExpressionFactory.create(propertyName + expertPropertyNameSuffix, parentBean);
  }

  private function getContentQueryEditor():ContentQueryEditor {
    if (!dcqe) {
      dcqe = this.findParentByType(ContentQueryEditor) as ContentQueryEditor;
    }
    return dcqe;
  }

  /*
   * Initializes template properties object used to create local bean.
   */
  private function createProperties():Object {
    var props = {};
    props[FILTER_QUERY] = undefined;
    props[SORTING_QUERY] = undefined;
    return props;
  }

  protected function getLocalModel():Bean {
    if (!localModel){
      localModel = beanFactory.createLocalBean(createProperties());
      // separate bean is used for condition expressions so that the property names defined outside of the module
      // can't interfere with our own property names in the local model
      localModel.set(CONDITION_EXPERT_EXPRESSIONS, beanFactory.createLocalBean({}));
    }
    return localModel;
  }

  public override function destroy():void{
    documentTypesRemote && documentTypesRemote.removeChangeListener(documentTypesRemote[ATTACHED_CHANGE_LISTENER]);
    conditionRemotes && conditionRemotes.length && conditionRemotes.forEach(function(valueExpression:ValueExpression){
      valueExpression.removeChangeListener(valueExpression[ATTACHED_CHANGE_LISTENER]);
    });
    sortingRemote && sortingRemote.removeChangeListener(sortingRemote[ATTACHED_CHANGE_LISTENER]);
    super.destroy();
  }
}
}
