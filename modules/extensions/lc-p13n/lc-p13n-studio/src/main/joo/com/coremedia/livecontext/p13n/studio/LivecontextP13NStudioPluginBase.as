package com.coremedia.livecontext.p13n.studio {
import com.coremedia.blueprint.personalization.editorplugin.plugin.AddSiteSpecificPathPlugin;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.cms.editor.sdk.sites.SitesService;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.ecommerce.studio.model.CatalogObjectPropertyNames;
import com.coremedia.ecommerce.studio.model.Store;
import com.coremedia.livecontext.p13n.studio.config.livecontextP13NStudioPlugin;
import com.coremedia.ui.data.ValueExpression;

use namespace editorContext;

public class LivecontextP13NStudioPluginBase extends StudioPlugin {
  public function LivecontextP13NStudioPluginBase(config:livecontextP13NStudioPlugin) {
    super(config);
  }

  override public function init(editorContext:IEditorContext):void {
    super.init(editorContext);
    //add site formatter for the catalog object entity
    AddSiteSpecificPathPlugin.addSitePathFormatter(formatSitePathFromCatalogObject);
  }

  private function formatSitePathFromCatalogObject(path:String, entityExpression:ValueExpression, callback:Function):void {
    entityExpression.loadValue(function(entity:Object):void {
      if (entity is CatalogObject) {
        entityExpression.extendBy(CatalogObjectPropertyNames.STORE).loadValue(function(store:Store):void {
          //value should be the store
          var sitesService:SitesService = editorContext.getSitesService();
          var site:Site = sitesService.getSite(store.getSiteId());
          var selectedSitePath:String = site.getSiteRootFolder().getPath() + '/' + path;
          callback.call(null, selectedSitePath);
        });
      }
    });
  }

}
}