package com.coremedia.cms.studio.queryeditor.components {

import com.coremedia.cms.studio.queryeditor.config.queryChunkField;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Ext;
import ext.form.TextField;

public class QueryChunkFieldBase extends TextField {

  protected const EXPERT_EXPRESSION:String = 'expertExpression';
  //all three expressions are the serialized query chunk expressions
  private var localExpression:ValueExpression; // is displayed in the expert view UI
  private var defaultLocalExpression:ValueExpression; // is serialized from the assistant view
  private var remoteExpression:ValueExpression; // is stored in the remote bean

  private var localModel:Bean;
  //it is possible that the defaultLocalExpression value changes between execution of #resolveLocal and
  //#updateRemoteFromLocal methods, so it needs to be cached in the local field
  private var defaultLocalExpressionValue:String;

  public function QueryChunkFieldBase(config:queryChunkField = null) {
    super(queryChunkField(Ext.apply({}, config)));
    localExpression = ValueExpressionFactory.create(EXPERT_EXPRESSION, getLocalModel());
    remoteExpression = config.remoteExpertExpression;
    defaultLocalExpression =  config.defaultExpertExpression;
  }

  protected override function afterRender():void{
    super.afterRender();
    listenToModelChanges();
    resolveLocal();
  }

  private function listenToModelChanges():void {
    defaultLocalExpression.addChangeListener(resolveLocal);
    remoteExpression.addChangeListener(updateLocalFromRemote);
    localExpression.addChangeListener(updateRemoteFromLocal);
  }

  private function updateLocalFromRemote(valueExpression:ValueExpression):void {
    var remoteExpression:String = valueExpression.getValue();
    if(remoteExpression != localExpression.getValue()){
      resolveLocal();
    }
  }

  private function updateRemoteFromLocal(valueExpression:ValueExpression):void {
    var editedExpression:String = valueExpression.getValue();
    //it needs to be not null, different from what's already in the remote bean and also
    //different from expression defined in the assistant view
    if(!Ext.isEmpty(editedExpression)
            && editedExpression != remoteExpression.getValue()
            && editedExpression != defaultLocalExpressionValue){
      remoteExpression.setValue(editedExpression);
    }
  }

  private function resolveLocal(){
    var remoteExpressionValue:String;

    if(!defaultLocalExpression.isLoaded()){
      defaultLocalExpression.getValue();
    }
    if(!remoteExpression.isLoaded()){
      remoteExpression.getValue();
    }
    if(defaultLocalExpression.isLoaded() && remoteExpression.isLoaded()){
      remoteExpressionValue = remoteExpression.getValue();
      defaultLocalExpressionValue = defaultLocalExpression.getValue();
      if(!Ext.isEmpty(remoteExpressionValue)){
        localExpression.setValue(remoteExpressionValue);
      } else {
        localExpression.setValue(defaultLocalExpressionValue || "");
      }
    }
  }

  /*
   * Initializes template properties object used to create local bean.
   */
  private function createProperties():Object {
    var props = {};
    props[EXPERT_EXPRESSION] = "";
    return props;
  }

  protected function getLocalModel():Bean {
    if (!localModel){
      localModel = beanFactory.createLocalBean(createProperties());
    }
    return localModel;
  }

  public override function destroy():void {
    defaultLocalExpression.removeChangeListener(resolveLocal);
    defaultLocalExpression = null;
    remoteExpression.removeChangeListener(updateLocalFromRemote);
    remoteExpression = null;
    localExpression.removeChangeListener(updateRemoteFromLocal);
    super.destroy();
  }
}
}
