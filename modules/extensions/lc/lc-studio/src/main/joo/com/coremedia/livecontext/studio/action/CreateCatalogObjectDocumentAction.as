package com.coremedia.livecontext.studio.action {
import com.coremedia.blueprint.base.components.config.quickCreateDialog;
import com.coremedia.blueprint.base.components.quickcreate.processing.ProcessingData;
import com.coremedia.cms.editor.sdk.actions.ActionConfigUtil;
import com.coremedia.ecommerce.studio.model.CatalogObject;
import com.coremedia.livecontext.studio.LivecontextStudioPlugin_properties;
import com.coremedia.livecontext.studio.config.createCatalogObjectDocumentAction;

import ext.ComponentMgr;
import ext.Window;

/**
 * This action is intended to be used from within EXML, only.
 *
 */
public class CreateCatalogObjectDocumentAction extends LiveContextCatalogObjectAction {

  public static const EXTERNAL_ID_PROPERTY:String = 'externalId';

  private var contentType:String;
  private var catalogObjectType:Class;
  private var inheritEditors:Boolean;

  /**
   * @param config the configuration object
   */
  public function CreateCatalogObjectDocumentAction(config:createCatalogObjectDocumentAction) {
    super(createCatalogObjectDocumentAction(ActionConfigUtil.extendConfiguration(LivecontextStudioPlugin_properties.INSTANCE, config, config.actionName, {handler: handler})));
    contentType = config.contentType;
    catalogObjectType = config.catalogObjectType;
    inheritEditors = config.inheritEditors;
  }

  override protected function isDisabledFor(catalogObjects:Array):Boolean {
    //the action should be enabled only if there is only one catalog object and it is a correct configured type
    if (catalogObjects.length !== 1) {
      return true;
    }
    var catalogObject:CatalogObject = catalogObjects[0];
    if (!(isCorrectType(catalogObject))) {
      return true;
    }

    return super.isDisabledFor(catalogObjects);
  }

  override protected function isHiddenFor(catalogObjects:Array):Boolean {
    return super.isHiddenFor(catalogObjects) || isDisabledFor(catalogObjects);
  }

  private function isCorrectType(catalogObject:CatalogObject):Boolean {
    return catalogObject is catalogObjectType;
  }

  private function handler():void {
    var catalogObject:CatalogObject = getCatalogObjects()[0];
    if (isCorrectType(catalogObject)) {
      //create the dialog
      var dialogConfig:quickCreateDialog = new quickCreateDialog();
      dialogConfig.contentType = contentType;
      dialogConfig.model = new ProcessingData();
      dialogConfig.model.set(EXTERNAL_ID_PROPERTY, catalogObject.getId());
      dialogConfig.model.set(ProcessingData.NAME_PROPERTY, catalogObject.getName());
      dialogConfig.inheritEditors = inheritEditors;

      var dialog:Window = ComponentMgr.create(dialogConfig, 'window') as Window;
      dialog.show();
    }
  }
}
}
