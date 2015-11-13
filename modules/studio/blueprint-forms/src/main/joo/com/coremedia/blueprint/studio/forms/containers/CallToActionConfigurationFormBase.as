package com.coremedia.blueprint.studio.forms.containers {
import com.coremedia.blueprint.studio.config.callToActionConfigurationForm;
import com.coremedia.cms.editor.sdk.premular.CollapsibleFormPanel;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

public class CallToActionConfigurationFormBase extends CollapsibleFormPanel {
  internal var model:Bean;

  internal var modelValueExpression:ValueExpression;

  public function CallToActionConfigurationFormBase(config:callToActionConfigurationForm = null) {
    addEvents("CTAConfigurationChanged");
    super(config);

    getModel().addPropertyChangeListener("callToActionDisabled", callToActionDisabledListener);
    getModel().addPropertyChangeListener("callToActionCustomText", callToActionCustomTextListener);
    getModel().get("properties").addPropertyChangeListener("CTAType", ctaTypeListener);
    getModel().get("properties").addPropertyChangeListener("CTAText", ctaTextListener);
  }

  private function callToActionDisabledListener(event:PropertyChangeEvent):void {
    updateOuterType();
    fireEvent("CTAConfigurationChanged");
  }

  private function callToActionCustomTextListener(event:PropertyChangeEvent):void {
    updateOuterText();
    fireEvent("CTAConfigurationChanged");
  }

  private function ctaTypeListener(event:PropertyChangeEvent):void {
    updateInnerType();
  }

  private function ctaTextListener(event:PropertyChangeEvent):void {
    updateInnerText();
  }

  override protected function onDestroy():void {
    getModel().removePropertyChangeListener("callToActionDisabled", callToActionDisabledListener);
    getModel().removePropertyChangeListener("callToActionCustomText", callToActionCustomTextListener);
    getModel().get("properties").removePropertyChangeListener("CTAType", ctaTypeListener);
    getModel().get("properties").removePropertyChangeListener("CTAText", ctaTextListener);
    super.onDestroy();
  }

  internal function updateInnerText():void {
    var text:String = getModel().get("properties").get("CTAText");
    var ctatype:String = getModel().get("properties").get("CTAType");
    getModel().set('callToActionCustomText', text);
    if (!text || text.length === 0) {
      if (ctatype === 'customCTA') {
        getModel().get("properties").set("CTAType", 'defaultCTA');
      }
    } else {
      if (ctatype !== 'customCTA') {
        getModel().get("properties").set("CTAType", 'customCTA');
      }
    }
  }

  internal function updateInnerType():void {
    var ctatype:String = getModel().get("properties").get("CTAType");

    if (ctatype === 'noCTA') {
      getModel().set('callToActionDisabled', true);
      getModel().get("properties").set("CTAText", "");
    } else if (ctatype === 'defaultCTA') {
      getModel().set('callToActionDisabled', false);
      getModel().get("properties").set("CTAText", "");
    } else {
      getModel().set('callToActionDisabled', false);
    }
  }

  internal function updateOuterText():void {
    var text:String = getModel().get('callToActionCustomText');

    getModel().get('properties').set('CTAText',text);
  }

  internal function updateOuterType():void {
    var disableCTA:Boolean = getModel().get('callToActionDisabled');
    var text:String = getModel().get('callToActionCustomText');
    if (!disableCTA) {
      if (text && text.length > 0) {
        getModel().get('properties').set('CTAType','customCTA');
      }
    } else {
      getModel().get('properties').set('CTAType','noCTA');
    }
  }

  internal function getModel():Bean {
    if (!model) {
      model = beanFactory.createLocalBean();
      var innerModel:Bean = beanFactory.createLocalBean();
      innerModel.set("CTAType", "defaultCTA");
      innerModel.set("CTAText", null);
      model.set("properties", innerModel);
    }
    return model;
  }

  internal function getModelExpression():ValueExpression {
    if (!modelValueExpression) {
      modelValueExpression = ValueExpressionFactory.createFromValue(getModel());
    }
    return modelValueExpression;
  }

  public function setCallToActionDisabled(useCTA: Boolean):void {
    getModel().set("callToActionDisabled", useCTA);
  }

  public function getCallToActionDisabled():Boolean {
    return getModel().get("callToActionDisabled");
  }

  public function setCallToActionCustomText(callToActionCustomText: String):void {
    getModel().set("callToActionCustomText", callToActionCustomText);
  }

  public function getCallToActionCustomText():Boolean {
    return getModel().get("callToActionCustomText");
  }
}
}
