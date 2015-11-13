package com.coremedia.cms.studio.queryeditor.components {

import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.premular.fields.BooleanPropertyField;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.cms.studio.queryeditor.QueryEditor_properties;
import com.coremedia.cms.studio.queryeditor.conditions.ConditionEditorBase;
import com.coremedia.cms.studio.queryeditor.config.documentTypesFilter;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.beanFactory;

import ext.ComponentMgr;
import ext.Ext;
import ext.Panel;
import ext.config.checkbox;
import ext.form.Checkbox;
import ext.form.CheckboxGroup;
import ext.util.MixedCollection;

public class DocumentTypesFilterBase extends Panel {

  private var documentTypes:Array;
  private var propertyName:String;
  private var readOnlyValueExpression:ValueExpression;
  private var propertyValueExpression:ValueExpression;
  private var allCheckbox:Checkbox;
  private var typeCheckboxesParent:CheckboxGroup;
  private var dcqe:ContentQueryEditor;
  private var selectedLocal:*;
  private var localModel:Bean;
  private var triggeredByAllCheckbox:Boolean;
  private var triggeredByIndividualCheckbox:Boolean;

  private const SELECTED_TYPES:String = SelectedLocal.SELECTED_TYPES;

  /*
   * @param assistantFormattedExpression serialized document types expression for the assistant view
   * @return serialized document types expression for the expert view
   */
  public static function translateAssistantToExpertFormat(assistantFormattedExpression:String, propertyName:String):String{
    //documenttype:(CMArticle OR CMPicture)
    return ConditionEditorBase.assembleCondition(propertyName, deserialize(assistantFormattedExpression), 'OR');
  }

  public function DocumentTypesFilterBase(config:documentTypesFilter = null) {
    super(documentTypesFilter(Ext.apply({}, config)));
    selectedLocal = initSelectedLocal();
    documentTypes = config.documentTypes;
    readOnlyValueExpression = PropertyEditorUtil.createReadOnlyValueExpression(config.bindTo,
      config.forceReadOnlyValueExpression);
    propertyName = config.propertyName;
  }

  override protected function afterRender():void {
    super.afterRender();

    renderTypes();

    propertyValueExpression = getContentQueryEditor().getDocumentTypesRemote();
    propertyValueExpression.addChangeListener(selectedFromRemoteChanged);
    selectedFromRemoteChanged(propertyValueExpression); // trigger loading stored values

    mon(getAllCheckbox(), 'check', onAllCheckboxChange);
    Ext.each(this.getTypeCheckboxes().items, function(checkboxx:Checkbox){
      mon(checkboxx, 'check', function(checkboxx:Checkbox){
        var numberOfChecked:Number = (getTypeCheckboxes() as MixedCollection).filterBy(function(checkboxx:Checkbox){
          return checkboxx.checked;
        }).length;

        updateModel(checkboxx);

        //check the "all checkbox" if every type checkbox is checked, uncheck otherwise
        if(!triggeredByAllCheckbox){
          triggeredByIndividualCheckbox = true;
          getAllCheckbox().setValue(numberOfChecked == getTypeCheckboxes().length);
          triggeredByIndividualCheckbox = false;
        }
      });
    }, null);

    //disable checkboxes if checked out by other
    //TODO: code is similar in multiple components - there should be a plugin which listens to the checkedOutByOther
    // and calls custom function when the value changes.
    readOnlyValueExpression.addChangeListener(enableOrDisable);
    enableOrDisable(readOnlyValueExpression);

    //select all by default
    if(getLocalModel().get(SELECTED_TYPES).length === 0) {
      getLocalModel().set(SELECTED_TYPES, documentTypes);
    }
  }

  /**
   * @return collection-like object for managing selected types in the local bean
   */
  private function initSelectedLocal():* {
    getLocalModel().set(SELECTED_TYPES, []);
    getLocalModel().addPropertyChangeListener(SELECTED_TYPES, onSelectedTypesChange);
    return new SelectedLocal(getLocalModel());
  }

  private function onSelectedTypesChange(e:PropertyChangeEvent){
    getContentQueryEditor().updateApplicableConditions(e.newValue);
  }

  private function renderTypes():void {
    var options:Array = [],
        typeCheckboxes:* = getTypeCheckboxes(),
        //typCheckboxes are either instance of MixedCollection or Array
        addMethod = typeCheckboxes instanceof MixedCollection ? typeCheckboxes.add : typeCheckboxes.push;

    Ext.each(documentTypes, function(documentType:String){
      addMethod.call(typeCheckboxes, ComponentMgr.create(
        new checkbox({
          boxLabel:typeNameToString(documentType),
          name: documentType,
          checked: true,
          ctCls:BooleanPropertyField.BOOLEAN_PROPERTY_FIELD_CSS})
      ));
    }, null);
    this.doLayout();
  }

  /**
   * Updates local model when a checkbox changes value and syncs the new value to the server.
   *
   * @param checkboxx checkbox component that was changed
   */
  private function updateModel(checkboxx:Checkbox):void {
    var fq:Struct,
        selectedLocalArray:Array;

    // check if change from the UI is already present in the local model:
    // (this happens when checkbox change listeners are triggered upon update from remote)
    if(checkboxx.checked != selectedLocal.contains(checkboxx.getName())) {
      selectedLocal[checkboxx.checked ? 'add' : 'remove'](checkboxx.getName());
      selectedLocalArray = selectedLocal.asArray();
      if(selectedLocalArray.length){
        propertyValueExpression.setValue(serialize(selectedLocalArray));
      } else {
        fq = propertyValueExpression.getParent().getValue() as Struct;
        if (fq && fq.getType().hasProperty(propertyName)) {
          fq.getType().removeProperty(propertyName);
        }
      }
    }
  }

  /**
   * Updates checkboxes when the selected document types come from the system.
   *
   * @param selectedFromRemote array of selected document types
   */
  private function updateUI(selectedFromRemote:Array):void{
    if(getTypeCheckboxes().items) {
      selectedLocal.set(selectedFromRemote);
      Ext.each(getTypeCheckboxes().items, function(checkboxx:Checkbox){
        var selected:Boolean = selectedLocal.contains(checkboxx.getName());
        checkboxx.setValue(selected);
      }, null);
      if(selectedLocal.asArray().length == getTypeCheckboxes().length){
        getAllCheckbox().setValue(true);
      }
    } else {
      //document type checkboxes weren't rendered yet, do nothing until they are rendered
    }
  }

  /**
   * Translate to the text representation stored in the query remote bean.
   */
  private static function serialize(selectedDocumentTypes:Array):String {
    return selectedDocumentTypes.join(",");
  }

  /**
   * Translate from assistant view format.
   */
  private static function deserialize(selectedDocumentTypes:String):Array {
    var split:Array = selectedDocumentTypes ? selectedDocumentTypes.split(",") : [];
    return split.length > 1 || split[0] != '' ? split : [];
  }

  private function selectedFromRemoteChanged(source:ValueExpression):void {
    var selectedFromRemote = source.getValue();
    // if the remote and local version are the same, there is no need to update UI
    if(selectedFromRemote != undefined && selectedFromRemote != serialize(selectedLocal.asArray())) {
      updateUI(deserialize(selectedFromRemote));
    }
  }

  private function onAllCheckboxChange(allCheckbox:Checkbox):void {
    if(!triggeredByIndividualCheckbox){
      triggeredByAllCheckbox = true;
      Ext.each(this.getTypeCheckboxes().items, function(checkboxx:Checkbox){
        checkboxx.setValue(allCheckbox.checked);
      }, this);
      triggeredByAllCheckbox = false;
    }
  }

  private function enableOrDisable(valueExpression:ValueExpression):void {
    var readOnly:Boolean = valueExpression.getValue();
    var enableOrDisable:String;

    if(readOnly != undefined){
      enableOrDisable = readOnly ? 'disable' : 'enable';
      getAllCheckbox()[enableOrDisable]();
      getTypeCheckboxes().each(function(checkboxx:Checkbox){
        checkboxx[enableOrDisable]();
      });
    }
  }


  private function getAllCheckbox():Checkbox{
    if(!allCheckbox){
      allCheckbox = this.find('itemId', 'all')[0];
    }
    return allCheckbox as Checkbox;
  }

  private function getTypeCheckboxesParent():CheckboxGroup{
    if(!typeCheckboxesParent){
      typeCheckboxesParent = this.find('itemId', 'types')[0];
    }
    return typeCheckboxesParent as CheckboxGroup;
  }

  private function getTypeCheckboxes():Object{
    return this.getTypeCheckboxesParent().items;
  }

  private function getContentQueryEditor():ContentQueryEditor {
    if (!dcqe) {
      dcqe = this.findParentByType(ContentQueryEditor) as ContentQueryEditor;
    }
    return dcqe;
  }

  private function typeNameToString(name:String){
    return QueryEditor_properties.INSTANCE['DCQE_label_document_types_' + name] || name;
  }

  protected function getLocalModel():Bean {
    if (!localModel) {
      localModel = beanFactory.createLocalBean({});
    }
    return localModel;
  }

  public override function destroy():void {
    localModel.removePropertyChangeListener(SELECTED_TYPES, onSelectedTypesChange);
    propertyValueExpression && propertyValueExpression.removeChangeListener(selectedFromRemoteChanged);
    readOnlyValueExpression.removeChangeListener(enableOrDisable);
    super.destroy();
  }
}
}

import com.coremedia.ui.data.Bean;

/**
 * collection-like object for managing selected types in the local bean
 */
class SelectedLocal {
  public static const SELECTED_TYPES:String = 'selectedTypes';

  private var localModel:Bean;

  public function SelectedLocal(localModel:Bean) {
    this.localModel = localModel;
  }

  public function getLocalModel():Bean {
    return localModel;
  }

  public function set(selectedTypes:Array):void {
    getLocalModel().set(SELECTED_TYPES, selectedTypes);
  }

  public function add(type:String):void {
    var st:Array = this.asArray(true); //we need a copy to trigger the change listener
    st.push(type);
    getLocalModel().set(SELECTED_TYPES, st);
  }

  public function remove(type:String):void {
    var st:Array = this.asArray(true); //we need a copy to trigger the change listener
    st.splice(st.indexOf(type),1);
    getLocalModel().set(SELECTED_TYPES, st);
  }

  public function contains(type:String):Boolean {
    var st:Array = this.asArray();
    return st.indexOf(type) != -1;
  }

  public function asArray(copy:Boolean = false):Array {
    var st:Array = getLocalModel().get(SELECTED_TYPES) as Array;
    return copy ? st.slice(0) : st;
  }
}

