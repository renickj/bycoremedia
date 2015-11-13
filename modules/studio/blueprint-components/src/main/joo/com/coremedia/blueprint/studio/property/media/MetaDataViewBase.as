package com.coremedia.blueprint.studio.property.media {
import com.coremedia.blueprint.studio.config.components.metaDataView;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Container;

public class MetaDataViewBase extends Container {

  private var metaDataExpression:ValueExpression;

  public function MetaDataViewBase(config:metaDataView) {
    super(config);
  }

  public function getMetaDataExpression(metaDataSection:MetaDataSection):ValueExpression {
    if (!metaDataExpression) {
      metaDataExpression = ValueExpressionFactory.createFromValue(metaDataSection.getData());
    }
    return metaDataExpression;
  }
}
}