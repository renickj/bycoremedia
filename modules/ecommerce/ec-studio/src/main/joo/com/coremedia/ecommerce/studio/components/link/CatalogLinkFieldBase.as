package com.coremedia.ecommerce.studio.components.link {
import com.coremedia.ecommerce.studio.config.catalogLink;
import com.coremedia.ecommerce.studio.config.catalogLinkField;

import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.util.PropertyChangeEventUtil;

import ext.Ext;

/**
 * The application logic for the catalog product link displayed in the catalog link property field
 */
public class CatalogLinkFieldBase extends CatalogLink {

  private static const PROPERTIES:String = 'properties';
  private static const LOCAL_SETTINGS_STRUCT_NAME:String = 'localSettings';
  private static const COMMERCE_STRUCT_NAME:String = 'commerce';
  private static const INHERIT_PROPERTY_NAME:String = 'inherit';

  private var content:Content;
  private var openLinkSources:Function;
  private var propertyExpression:ValueExpression;
  private var readOnlyExpression:ValueExpression;

  /**
   * @param config the config object
   */
  public function CatalogLinkFieldBase(config:catalogLinkField) {
    openLinkSources = config.openLinkSources;
    super(config);
    if (config.bindTo) {
      config.bindTo.addChangeListener(function (source:ValueExpression):void {
        setContent(source.getValue());
      });
      setContent(config.bindTo.getValue());
    }
  }

  override protected function afterRender():void {
    super.afterRender();
    mon(getGridEl(), 'click', function ():void {
      if (getStore().data.length == 0) {
        openLinkSources();
      }
    });
  }

  [ProvideToExtChildren]
  public function getContent():Content {
    return content;
  }

  public function setContent(value:Content):void {
    var oldValue:Content = content;
    content = value;
    PropertyChangeEventUtil.fireEvent(this, catalogLink.CONTENT_VARIABLE_NAME, oldValue, value);
  }


  internal function getReadOnlyExpression(config:catalogLink):ValueExpression {
    if (!readOnlyExpression) {
      readOnlyExpression = ValueExpressionFactory.createFromFunction(getReadOnlyFunction(catalogLinkField(config)));
    }
    return readOnlyExpression;
  }

  public static function getReadOnlyFunction(config:catalogLinkField):Function {
    return function():Boolean {
      //is the content or read-only or are we forced to set read-only?
      var contentOrForceReadOnlyExpression:ValueExpression = PropertyEditorUtil.createReadOnlyValueExpression(config.bindTo, config.forceReadOnlyValueExpression);
      if (contentOrForceReadOnlyExpression.getValue()) {
        return true;
      }
      if (config.bindTo) {
        var inheritExpression:ValueExpression = config.bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME, COMMERCE_STRUCT_NAME, INHERIT_PROPERTY_NAME);
        return !!inheritExpression.getValue();
      }
      return false;
    }
  }

  override protected function getPropertyExpression(config:catalogLink):ValueExpression {
    if (!propertyExpression) {
      if (config.model) {
        propertyExpression = ValueExpressionFactory.create(config.propertyName, config.model);
      } else {
        propertyExpression = super.getPropertyExpression(config);
      }
    }
    return propertyExpression;
  }
}
}
