package com.coremedia.blueprint.studio.forms.containers {
import com.coremedia.blueprint.studio.config.externallyVisibleDateForm;
import com.coremedia.cms.editor.sdk.premular.CollapsibleFormPanel;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.Calendar;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

public class ExternallyVisibleDateFormBase extends CollapsibleFormPanel {

  internal var model:Bean;

  internal var modelValueExpression:ValueExpression;

  public function ExternallyVisibleDateFormBase(config:externallyVisibleDateForm = null) {
    addEvents("externallyDisplayedDateChanged");
    super(config);

    getModel().addPropertyChangeListener("externallyDisplayDate", externallyDisplayDateChangeListener);
    getModel().get("properties").addPropertyChangeListener("innerExternallyDisplayedDate", innerExternallyDisplayedDateListener);
    getModel().addPropertyChangeListener("innerUseCustomExternalDisplayedDate", innerUseCustomExternalDisplayedDateListener);
  }

  private function externallyDisplayDateChangeListener(event:PropertyChangeEvent):void {
    getModel().get("properties").set("innerExternallyDisplayedDate", event.newValue);
    fireEvent("externallyDisplayedDateChanged");
  }

  private function innerExternallyDisplayedDateListener(event:PropertyChangeEvent):void {
    getModel().set("externallyDisplayDate", event.newValue);
    if (event.newValue === null) {
      getModel().set("innerUseCustomExternalDisplayedDate", false);
    } else {
      getModel().set("innerUseCustomExternalDisplayedDate", true);
    }
  }

  private function innerUseCustomExternalDisplayedDateListener(event:PropertyChangeEvent):void {
    if (event.newValue === false) {
      getModel().set("archivedDisplayedDate", getModel().get("externallyDisplayDate"));
      getModel().set("externallyDisplayDate", null);
    }
    if (event.newValue === true && getModel().get("archivedDisplayedDate") ) {
      getModel().set("externallyDisplayDate", getModel().get("archivedDisplayedDate"));
    }
  }


  override protected function onDestroy():void {
    getModel().removePropertyChangeListener("externallyDisplayDate", externallyDisplayDateChangeListener);
    getModel().get("properties").removePropertyChangeListener("innerExternallyDisplayedDate", innerExternallyDisplayedDateListener);
    getModel().removePropertyChangeListener("innerUseCustomExternalDisplayedDate", innerUseCustomExternalDisplayedDateListener);

    super.onDestroy();
  }

  internal function getModel():Bean {
    if (!model) {
      model = beanFactory.createLocalBean();
      var innerModel:Bean = beanFactory.createLocalBean();
      innerModel.set("innerExternallyDisplayedDate", null);
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

  internal static function toValue(value:String):Boolean {
    return value === 'ownDate';
  }

  public function setExternallyDisplayedDate(displayedDate: Calendar):void {
    getModel().set("externallyDisplayDate", displayedDate);
  }

  public function getExternallyDisplayedDate():Calendar {
    return getModel().get("externallyDisplayDate");
  }

}
}
