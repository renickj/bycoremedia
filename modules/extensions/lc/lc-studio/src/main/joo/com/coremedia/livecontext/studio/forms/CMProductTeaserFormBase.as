package com.coremedia.livecontext.studio.forms {
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.premular.DocumentTabPanel;
import com.coremedia.livecontext.studio.config.cmProductTeaserForm;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

use namespace editorContext;

public class CMProductTeaserFormBase extends DocumentTabPanel {

  public static const INHERIT_SETTING:String = "inherit";
  public static const DISABLE_OVERLAY:String = "disableOverlay";

  private var viewSettingsExpression:ValueExpression;

  public function CMProductTeaserFormBase(config:cmProductTeaserForm) {
    super(config);
  }

  protected function getLabelValueExpression(value:String):ValueExpression {
    return ValueExpressionFactory.create('label', beanFactory.createLocalBean({label:value}));
  }

  public function getViewSettingsExpression(bindTo:ValueExpression):ValueExpression {
    if(!viewSettingsExpression) {
      viewSettingsExpression = bindTo.extendBy('properties.localSettings.viewSettings');
    }
    return viewSettingsExpression;
  }
}
}