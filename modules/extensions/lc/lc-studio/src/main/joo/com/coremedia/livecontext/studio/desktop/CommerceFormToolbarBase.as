package com.coremedia.livecontext.studio.desktop {
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.livecontext.studio.config.commerceFormToolbar;
import com.coremedia.ecommerce.studio.helper.CatalogHelper;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ui.components.IconLabel;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Toolbar;

public class CommerceFormToolbarBase extends Toolbar {

  private var localeNameValueExpression:ValueExpression;


  /**
   * Create a new instance.
   */
  public function CommerceFormToolbarBase(config:commerceFormToolbar) {
    super(config);
  }

  public native function get bindTo():ValueExpression;

  protected function getCatalogObject():CatalogObject {
    return bindTo.getValue() as CatalogObject;
  }


  internal function getLocaleValueExpression():ValueExpression {
    if (!localeNameValueExpression) {
      localeNameValueExpression = ValueExpressionFactory.createFromFunction(function ():Object {
        var catalogObject:CatalogObject = getCatalogObject();
        if (!catalogObject.getStore()) return undefined;
        if (!catalogObject.getStore().getSiteId()) return undefined;
        var site:Site = editorContext.getSitesService().getSite(catalogObject.getStore().getSiteId());
        if (site === undefined) return undefined;
        if (site && site.getLocale()) {
          var displayName:String = site.getLocale().getDisplayName();
          return {text: displayName, help: displayName, visible: true};
        } else {
          return {text: '', help: '', visible: false};
        }
      });
    }
    return localeNameValueExpression;
  }

  public static function changeLabel(component:IconLabel, valueExpression:ValueExpression):void {
    var model:Object = valueExpression.getValue();

    if (model) {
      var text:String = model.text;

      component.setVisible(model.visible);
      component.setText(text);
      component.setTooltip(model.help);
    }
  }

  public static function changeType(label:IconLabel, valueExpression:ValueExpression):void {
    var catalogObject:CatalogObject = valueExpression.getValue();
    if (!catalogObject) {
      return;
    }
    var catalogHelper:CatalogHelper = CatalogHelper.getInstance();
    var iconStyleClass:String = catalogHelper.getTypeCls(catalogObject);
    var text:String = catalogHelper.getTypeLabel(catalogObject);
    label.setIconClass("content-type-transparent content-type-xs " + iconStyleClass);
    label.setText(text);
  }
}
}
