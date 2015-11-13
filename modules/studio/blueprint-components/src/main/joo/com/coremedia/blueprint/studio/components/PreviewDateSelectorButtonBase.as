package com.coremedia.blueprint.studio.components {

import com.coremedia.blueprint.studio.config.components.previewDateSelectorButton;
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.ui.actions.OpenDialogAction;
import com.coremedia.ui.components.IconButtonMedium;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.Ext;

use namespace beanFactory;

/**
 * Base class of the button that opens the preview date selection dialog.
 */
public class PreviewDateSelectorButtonBase extends IconButtonMedium {
  private var dummyExpression:ValueExpression;

  public function PreviewDateSelectorButtonBase(config:previewDateSelectorButton = null) {
    super(config);
  }

  // Used by test
  //noinspection JSUnusedGlobalSymbols
  internal function getPreviewDateSelectorDialog():PreviewDateSelectorDialog {
    return PreviewDateSelectorDialog(OpenDialogAction(baseAction).getDialog());
  }

  internal function getDummyExpression():ValueExpression {
    if (!dummyExpression) {
      var contentWrapperBean:Bean = beanFactory.createLocalBean({properties:beanFactory.createLocalBean()});
      dummyExpression = ValueExpressionFactory.create('', contentWrapperBean);
    }
    return dummyExpression;
  }

  internal function getDateValueExpression():ValueExpression {
    var dateValueExpression:ValueExpression = getDummyExpression().extendBy('properties.previewDate');
    dateValueExpression.setValue(null);
    return dateValueExpression;
  }

  internal static function getDisabledValueExpression(previewPanel:PreviewPanel):ValueExpression {
    return ValueExpressionFactory.createTransformingValueExpression(previewPanel.bindTo, Ext.isEmpty);
  }
}
}
