package com.coremedia.blueprint.studio.taxonomy.action {

import com.coremedia.blueprint.studio.Blueprint_properties;
import com.coremedia.blueprint.studio.TaxonomyStudioPluginBase;
import com.coremedia.blueprint.studio.config.taxonomy.openTaxonomyEditorAction;
import com.coremedia.blueprint.studio.config.taxonomy.taxonomyEditor;
import com.coremedia.blueprint.studio.taxonomy.administration.TaxonomyEditor;
import com.coremedia.blueprint.studio.taxonomy.administration.TaxonomyEditorBase;
import com.coremedia.cms.editor.sdk.desktop.WorkArea;
import com.coremedia.cms.editor.sdk.desktop.WorkAreaTabType;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.ui.util.EventUtil;

import ext.Action;
import ext.Button;
import ext.Component;
import ext.Ext;

/** Opens the TaxonomyEditor **/
public class OpenTaxonomyEditorAction extends Action {
  private var taxonomyId:String;

  internal native function get items():Array;

  public function OpenTaxonomyEditorAction(config:openTaxonomyEditorAction) {
    config.handler = showTaxonomyAdministrationWithLatestSelection;
    super(config);
    taxonomyId = config.taxonomyId;
  }

  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    TaxonomyStudioPluginBase.isAdministrationEnabled(function(enabled:Boolean):void {
      setDisabled(!enabled);
      updateTooltip(enabled);
    });

    /**
     * Add site selection listener and destroy the editor if the site has
     * changed and the taxonomy manager is still open.
     */
    editorContext.getSitesService().getPreferredSiteIdExpression().addChangeListener(preferredSiteChangedHandler);
  }

  private function preferredSiteChangedHandler():void {
    TaxonomyStudioPluginBase.isAdministrationEnabled(function(enabled:Boolean):void {
      setDisabled(!enabled); //also update the action on site change
      updateTooltip(enabled);
      if(!enabled) {
        var editor:TaxonomyEditor = TaxonomyEditorBase.getInstance();
        if(editor) {
          editor.destroy();
        }
      }
    });
  }

  private static function updateTooltip(enabled:Boolean):void {
    var msg:String = Blueprint_properties.INSTANCE.Button_disabled_insufficient_privileges;
    if(enabled) {
      msg = '';
    }
    var button:Button = (Ext.getCmp('btn-taxonomy-editor') as Button);
    if(button) {
      button.setTooltip(msg);
    }
  }

  public static function showTaxonomyAdministrationWithLatestSelection():void {
    openTaxonomyAdministration();

    EventUtil.invokeLater(function ():void {
      TaxonomyEditorBase.getInstance().showNodeSelectedNode();
    });
  }

  /**
   * Static call to open the taxonomy admin console.
   */
  private static function openTaxonomyAdministration():void {
    var workArea:WorkArea = editorContext.getWorkArea() as WorkArea;
    var taxonomyAdminTab:TaxonomyEditor = TaxonomyEditorBase.getInstance();

    if (!taxonomyAdminTab) {
      var workAreaTabType:WorkAreaTabType = workArea.getTabTypeById(taxonomyEditor.xtype);
      taxonomyAdminTab = workAreaTabType.createTab(null) as TaxonomyEditor;
      workArea.addTab(workAreaTabType, taxonomyAdminTab);
    }

    workArea.setActiveTab(taxonomyAdminTab);
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      editorContext.getSitesService().getPreferredSiteIdExpression().removeChangeListener(preferredSiteChangedHandler);
    }
  }
}
}
