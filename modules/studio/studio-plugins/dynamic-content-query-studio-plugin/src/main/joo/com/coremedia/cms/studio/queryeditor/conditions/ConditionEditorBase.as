package com.coremedia.cms.studio.queryeditor.conditions {

import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.common.CapType;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.studio.queryeditor.components.ContentQueryEditor;
import com.coremedia.cms.studio.queryeditor.components.ContentQueryEditorBase;
import com.coremedia.cms.studio.queryeditor.config.conditionEditorBase;
import com.coremedia.cms.studio.queryeditor.config.conditionEditorHeader;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.beanFactory;

import ext.ComponentMgr;
import ext.Container;
import ext.Ext;

public class ConditionEditorBase extends Container {

  private const SERIALIZED_EXPRESSION:String = "expertExpression";

  private var localModel:Bean;
  private var propertyName:String;
  private var valueExpression:ValueExpression;
  private var dcqe:ContentQueryEditor;
  private var setToDefault:Boolean;
  private var expertExpression:ValueExpression;

  public function ConditionEditorBase(config:conditionEditorBase = null) {
    config.cls = "applied-condition-editor";
    super(conditionEditorBase(Ext.apply({}, config)));

    setToDefault = config.setToDefault || false;

    propertyName = config.propertyName;
    addHeader(ContentQueryEditorBase.getConditionTitle(config.propertyName), config.bindTo, config.forceReadOnlyValueExpression);
  }

  /* *** Methods that should be overwritten or used by the condition editor implementation class *** */

  /**
   * If there is a difference between the serialization format for the assistant view and the expert view, this method
   * needs to be overridden with proper implementation.
   *
   * @param assistantFormattedExpression condition expression for the assistant view, different condition editors store
   *        their information displayed in the assistant view in different data types
   * @return serialized condition expression for the expert view
   */
  public function translateAssistantToExpertFormat(assistantFormattedExpression:*):String{
    //can't use String cast because it will translate 0 to null
    return Ext.isEmpty(assistantFormattedExpression) ? "" : assistantFormattedExpression + "";
  }

  /**
   * Assembles (filter) query expression.
   *
   * @param propertyName property name
   * @param values values
   * @param operator operator
   */
  public static function assembleCondition(propertyName:String, values:Array, operator:String=''):String {
    return [propertyName,
              ":",
              values.length > 1 ? '(' : '',
              values.join(' ' + operator + ' '),
              values.length > 1 ? ')' : ''].join('');
  }

  /**
   * It's important that this method is called from the extended afterRender method!
   */
  protected override function afterRender():void{
    // once the default value is written to the remote bean, set the flag to false
    getRemoteValueExpression().addChangeListener(updateSetToDefaultFlag);
  }

  /**
   * Local model getter. Use the local model to store values and listen to the value changes.
   * @return local model bean
   */
  protected function getLocalModel():Bean {
    if (!localModel) {
      localModel = beanFactory.createLocalBean(createLocalModelInitObject());
    }
    return localModel;
  }

  /**
   * Override this method if you want to initialize the local bean with some values.
   */
  protected function createLocalModelInitObject():Object {
    return {};
  }

  /**
   * Read the existing condition expression from it and update it when the condition expression changes.
   * @return source value expression for the condition expression (most probably stored in the remote bean)
   */
  protected function getRemoteValueExpression():ValueExpression {
    if(!valueExpression){
      valueExpression = getContentQueryEditor().getFilterQuerySubProperty(propertyName);
      valueExpression.addChangeListener(conditionChanged);
    }
    return valueExpression;
  }

  /**
   * Event listener when one of the condition statuses has been changed.
   * We trigger the preview reload manually then.
   */
  private function conditionChanged():void {
    StudioUtil.reloadPreview();
  }

  /**
   * Use this method to distinguish the situation where the condition was just added and you need to set the default
   * value, from the situation where some other user removed the condition from the query. In both of these situations
   * you will have the condition editor rendered and the remote value undefined.
   *
   * NOTE: ConditionEditorBase.afterRender method needs to be called in the subclass for this method to work properly
   *
   * @return true if the condition should be set to default, false otherwise
   */
  protected function shouldSetToDefault():Boolean {
    return setToDefault;
  }


  /* *** Private methods *** */

  private function updateSetToDefaultFlag(valueExpression:ValueExpression):void {
    if(setToDefault && valueExpression.getValue()){
      setToDefault = false;
      getRemoteValueExpression().removeChangeListener(updateSetToDefaultFlag);
    }
  }

  /**
   * Adds header with a title and a delete button to the condition editor.
   *
   * @param title header title
   */
  private function addHeader(title:String, bindTo:ValueExpression, forceReadOnlyValueExpression:ValueExpression):void {
    var headerParams:Object  = {
        headerText: title.replace('<br/>',' '), //mpf
        bindTo: bindTo,
        forceReadOnlyValueExpression: forceReadOnlyValueExpression
      },
      headerConfig:conditionEditorHeader = new conditionEditorHeader(headerParams),
      header:ConditionEditorHeader = ComponentMgr.create(headerConfig) as ConditionEditorHeader;

    insert(0, header);
  }

  /**
   * Helper getter.
   * @return ContentQueryEditor
   */
  private function getContentQueryEditor():ContentQueryEditor {
    if (!dcqe) {
      dcqe = this.findParentByType(ContentQueryEditor) as ContentQueryEditor;
    }
    return dcqe;
  }

  public override function destroy():void{
    getRemoteValueExpression().removeChangeListener(updateSetToDefaultFlag);
    expertExpression = null;
    super.destroy();
  }

  /**
   * Initializes the struct the condition is working on.
   * @param bindTo
   * @param contentType
   * @param structPropertyName
   */
  protected function applyBaseStruct(bindTo:ValueExpression, contentType:String, structPropertyName:String):void {
    var c:Content = bindTo.getValue();
    var struct:Struct = c.getProperties().get('localSettings');
    struct.getType().addStructProperty('fq');
    var fq:Struct = struct.get('fq');

    var capType:CapType = session.getConnection().getContentRepository().getContentType(contentType);
    fq.getType().addLinkListProperty(structPropertyName, capType, []);
  }
}
}
