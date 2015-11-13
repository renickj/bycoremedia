package com.coremedia.blueprint.studio.template {
import com.coremedia.blueprint.base.components.util.ContentCreationUtil;
import com.coremedia.blueprint.studio.dialog.editors.NavigationLinkField_properties;
import com.coremedia.blueprint.studio.template.config.createFromTemplateDialog;
import com.coremedia.blueprint.studio.template.model.ProcessingData;
import com.coremedia.blueprint.studio.util.StudioConfigurationUtil;
import com.coremedia.blueprint.studio.util.StudioUtil;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.user.User;
import com.coremedia.cms.editor.sdk.components.folderprompt.FolderCreationResult;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.cms.editor.sdk.util.MessageBoxUtil;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;

import ext.QuickTips;
import ext.Window;
import ext.config.quicktip;
import ext.util.StringUtil;

/**
 * The base class of the create from template dialog creates.
 */
public class CreateFromTemplateDialogBase extends Window {
  private var disabledExpression:ValueExpression;
  private var model:ProcessingData;

  private var errorMessages:Object = {};

  private var baseFolderEditorial:ValueExpression;
  private var baseFolderNavigation:ValueExpression;

  public static const TEMPLATE_CHOOSER_FIELD_ID:String = "templateChooserField";
  public static const PAGE_FOLDER_COMBO_ID:String = 'folderCombo';
  public static const PARENT_PAGE_FIELD_ID:String = 'parentPageFieldId';
  public static const EDITORIAL_FOLDER_COMBO_ID:String = 'editorialFolderCombo';
  public static const NAME_FIELD_ID:String = 'nameField';

  public function CreateFromTemplateDialogBase(config:createFromTemplateDialog) {
    super(config);

    errorMessages[NAME_FIELD_ID] = CreateFromTemplateStudioPlugin_properties.INSTANCE.name_not_valid_value;
    errorMessages[TEMPLATE_CHOOSER_FIELD_ID] = CreateFromTemplateStudioPlugin_properties.INSTANCE.template_chooser_not_empty_value;
    errorMessages[PAGE_FOLDER_COMBO_ID] = CreateFromTemplateStudioPlugin_properties.INSTANCE.page_folder_combo_validation_message;
  }

  /**
   * Init dialog
   */
  override protected function afterRender():void {
    super.afterRender();

    getBaseFolderNavigation().loadValue(function(path:String):void {
      getModel().set(ProcessingData.FOLDER_PROPERTY, path);
      getBaseFolderNavigation().addChangeListener(navigationFolderListener)
    });

    getBaseFolderEditorial().loadValue(function(path:String):void {
      getModel().set(CreateFromTemplateStudioPluginSettings_properties.INSTANCE.editorial_folder_property, path);
      getBaseFolderEditorial().addChangeListener(editorialFolderListener)
    });

    var pageFolderChangeExpression:ValueExpression = ValueExpressionFactory.create(ProcessingData.FOLDER_PROPERTY, getModel());
    pageFolderChangeExpression.addChangeListener(validateForm);
  }

  private function navigationFolderListener():void {
    getBaseFolderNavigation().loadValue(function(path:String):void {
      getModel().set(ProcessingData.FOLDER_PROPERTY, path);
    });
  }

  private function editorialFolderListener():void {
    getBaseFolderEditorial().loadValue(function(path:String):void {
      getModel().set(CreateFromTemplateStudioPluginSettings_properties.INSTANCE.editorial_folder_property, path);
    });
  }

  override protected function onDestroy():void {
    super.onDestroy();
    model.removeValueChangeListener(validateForm);
    getBaseFolderNavigation().removeChangeListener(navigationFolderListener);
    getBaseFolderEditorial().removeChangeListener(editorialFolderListener);
  }

  /**
   * Creates the model that is used for this dialog.
   * @return
   */
  protected function getModel():ProcessingData {
    if (!model) {
      model = new ProcessingData();
      model.addValueChangeListener(validateForm);
    }
    return model;
  }

  private function validateForm():void {
    getDisabledExpression().setValue(false);
    validate(find('itemId', NAME_FIELD_ID)[0]);
    validate(find('itemId', EDITORIAL_FOLDER_COMBO_ID)[0]);
    validateAsync(find('itemId', PAGE_FOLDER_COMBO_ID)[0], folderValidator);
    validate(find('itemId', TEMPLATE_CHOOSER_FIELD_ID)[0]);
  }

  private function validate(editor:*):void {
    if (editor) {
      var validatorFunction:Function = editor.initialConfig['validate'];
      if (validatorFunction) {
        var result:Boolean = validatorFunction.call(null);
        applyValidationResult(editor, result);
      }
    }
  }

  private function validateAsync(editor:*, validator:Function):void {
    if (editor) {
      if (validator) {
        validator.call(null, function(result:Boolean):void {
          applyValidationResult(editor, result);
        });
      }
    }
  }

  private function applyValidationResult(editor:*, result:Boolean):void {
    var errorMsg:String = errorMessages[editor.itemId];
    if (!errorMsg) {
      errorMsg = CreateFromTemplateStudioPlugin_properties.INSTANCE.template_create_missing_value;
    }
    if (!result) {
      getDisabledExpression().setValue(true);
      editor.addClass("issue-error");
      QuickTips.register(quicktip({
        target: editor.getId(),
        id: 'quick-create-error-tt',
        text: errorMsg,
        trackMouse: false,
        autoHide: true,
        dismissDelay: 3000
      }));
    }
    else {
      editor.removeClass("issue-error");
      QuickTips.unregister(editor.el);
      QuickTips.getQuickTip().hide();
    }
  }

  /**
   * Invokes the post processing and closes the dialog
   */
  protected function handleSubmit():void {
    var data:ProcessingData = getModel();
    var path:String = data.get(ProcessingData.FOLDER_PROPERTY);
    var parent:Content = data.get(CreateFromTemplateStudioPluginSettings_properties.INSTANCE.parent_property);

    if (!parent) {
      MessageBoxUtil.showConfirmation(CreateFromTemplateStudioPlugin_properties.INSTANCE.text,
              CreateFromTemplateStudioPlugin_properties.INSTANCE.no_parent_page_selected_warning,
              CreateFromTemplateStudioPlugin_properties.INSTANCE.no_parent_page_selected_warning_buttonText,
              function (buttonId:String):void {
                if (buttonId === "ok") {
                  doCreation(path);
                }
              });
    } else {
      parent.invalidate(function():void {
        if (parent.isCheckedOutByOther()) {
          parent.getEditor().load(function(user:User):void {
            var msg:String = StringUtil.format(NavigationLinkField_properties.INSTANCE.layout_error_msg, user.getName());
            MessageBoxUtil.showError(NavigationLinkField_properties.INSTANCE.layout_error, msg);
          })
        } else {
          doCreation(path);
        }
      });
    }
  }


  /**
   * Performs the creation of the content
   * @param path the navigation path
   */
  private function doCreation(path:String):void {
    //first ensure that all folders exist
    var data:ProcessingData = getModel();

    var editorialFolderName:String = data.get(CreateFromTemplateStudioPluginSettings_properties.INSTANCE.editorial_folder_property);
    ContentCreationUtil.createRequiredSubfolders(path, function (result:FolderCreationResult):void {
      if (result.success) {
        var navigationFolder:Content = result.baseFolder;
        ContentCreationUtil.createRequiredSubfolders(editorialFolderName, function (editorialResult:FolderCreationResult):void {
          if (editorialResult.success) {
            destroy();

            //apply the folder instance to the processing data
            data.set(ProcessingData.FOLDER_PROPERTY, navigationFolder);
            CreateFromTemplateProcessor.process(data, function ():void {
              trace('INFO', 'Finished create from template');
              var content:Content = data.getContent();
              var initializer:Function = editorContext.lookupContentInitializer(content.getType());
              if (initializer) {
                initializer(content);
              }

              var parent:Content = data.get(CreateFromTemplateStudioPluginSettings_properties.INSTANCE.parent_property);
              if (parent) {
                parent.invalidate(function ():void {
                  StudioUtil.openInTab(parent);
                  openNewPageInTab(data);
                });
              } else {
                openNewPageInTab(data);
              }
            });
          } else {
            MessageBoxUtil.showError(CreateFromTemplateStudioPlugin_properties.INSTANCE.text,
                    CreateFromTemplateStudioPlugin_properties.INSTANCE.editor_folder_could_not_create_message);
            editorialResult.remoteError.setHandled(true);
          }
        });
      } else {
        MessageBoxUtil.showError(CreateFromTemplateStudioPlugin_properties.INSTANCE.text,
                CreateFromTemplateStudioPlugin_properties.INSTANCE.page_folder_could_not_create_message);
        result.remoteError.setHandled(true);
      }
    });
  }

  private function openNewPageInTab(data:ProcessingData):void {
    var newPage:Content = data.getContent();
    newPage.invalidate(function ():void {
      StudioUtil.openInTab(newPage);
    });
  }

  /**
   * Calculates if the mandatory input is given.
   * @return
   */
  protected function getDisabledExpression():ValueExpression {
    if (!disabledExpression) {
      disabledExpression = ValueExpressionFactory.create('disabled', beanFactory.createLocalBean());
      disabledExpression.setValue(true);
    }
    return disabledExpression;
  }

  protected function nameValidator():Boolean {
    var repository:ContentRepository = session.getConnection().getContentRepository();
    return getModel().get(ProcessingData.NAME_PROPERTY) && repository.isValidName(getModel().get(ProcessingData.NAME_PROPERTY));
  }

  protected function folderValidator(callback:Function):void {
    var ve:ValueExpression = ValueExpressionFactory.create(ProcessingData.FOLDER_PROPERTY, getModel());
    if (ve && ve.getValue() && (ve.getValue() as String).length > 0) {
      var folder:String = ve.getValue();
      session.getConnection().getContentRepository().getChild(folder, function(c:Content):void {
        if (c) {
          callback(false);
        } else {
          callback(true);
        }
      });
    } else {
      callback(true);
    }
  }

  protected function templateChooserNonEmptyValidator():Boolean {
    var ve:ValueExpression = ValueExpressionFactory.create(
            CreateFromTemplateStudioPluginSettings_properties.INSTANCE.template_property, getModel());
    return ve && ve.getValue() && (ve.getValue() as Array).length > 0;
  }

  protected static function editorialFolderValidator(value:String):Boolean {
    return !!(value && value.length > 0);
  }


  protected function getBaseFolderEditorial():ValueExpression {
    if (!baseFolderEditorial) {
      baseFolderEditorial = ValueExpressionFactory.createFromFunction(baseFolderEditorialCalculation);
    }
    return baseFolderEditorial;
  }

  protected function getBaseFolderNavigation():ValueExpression {
    if (!baseFolderNavigation) {
      baseFolderNavigation = ValueExpressionFactory.createFromFunction(baseFolderNavigationCalculation);
    }
    return baseFolderNavigation;
  }

  private function baseFolderNavigationCalculation():String {
    return baseFolderCalculation("paths.navigation", getNavigationFolderFallback);
  }

  private function baseFolderEditorialCalculation():String {
    return baseFolderCalculation("paths.editorial", getEditorialFolderFallback);
  }

  private function baseFolderCalculation(configuration:String, fallback:Function):String {
    var retPath:String = baseFolderCalculationRaw(configuration, fallback);
    if (retPath === undefined) {
      return undefined;
    }

    var diffSelectedParentPageAndNavigationPath:String = getDiffNavigationFolderParentFolder();
    if (diffSelectedParentPageAndNavigationPath === undefined) {
      return undefined;
    }

    if (diffSelectedParentPageAndNavigationPath) {
      retPath += "/" + diffSelectedParentPageAndNavigationPath;
    }

    var name:String = getModel().get(ProcessingData.NAME_PROPERTY);
    if (name && name.length > 0) {
      retPath += "/" + name;
    }
    return retPath;
  }

  private static function baseFolderCalculationRaw(configuration:String, fallback:Function):String {
    var folder:Content = StudioConfigurationUtil.getConfiguration("Content Creation", configuration);

    if (folder === undefined) {
      return undefined;
    }

    if (folder === null) {
      return fallback();
    } else {
      return folder.getPath();
    }
  }

  private function getDiffNavigationFolderParentFolder():String {
    var folderNavigation:String = baseFolderCalculationRaw("paths.navigation", getNavigationFolderFallback);
    if (folderNavigation === undefined) {
      return undefined;
    }
    var parent:Content = getModel().get(CreateFromTemplateStudioPluginSettings_properties.INSTANCE.parent_property);
    if (!parent) {
      return null;
    }

    var parentFolder:Content = parent.getParent();

    if (parentFolder === undefined) {
      return undefined;
    }

    var parentFolderPath:String = parentFolder.getPath();
    if (parentFolderPath === undefined) {
      return undefined;
    }

    if (parentFolderPath.substr(0, folderNavigation.length) === folderNavigation) {
      return parentFolderPath.substr(folderNavigation.length + 1);
    } else {
      return null;
    }
  }

  protected static function getNavigationFolderFallback():String {
    return getFolderFallback(CreateFromTemplateStudioPluginSettings_properties.INSTANCE.doctype);
  }

  protected static function getEditorialFolderFallback():String {
    return getFolderFallback("CMArticle");
  }

  protected static function getFolderFallback(docType:String):String {
    var siteId:String = editorContext.getSitesService().getPreferredSiteId();
    var site:Site = editorContext.getSitesService().getSite(siteId);
    var docTypeDefault:String = CreateFromTemplateStudioPluginSettings_properties.INSTANCE[docType + '_home_folder'];
    var path:String = site.getSiteRootFolder().getPath();
    if (path === undefined) {
      return undefined;
    }
    if(docTypeDefault) {
      if(docTypeDefault.indexOf('/') === 0) {
        return docTypeDefault;
      }
      if(site) {
        return path + '/' + docTypeDefault;
      }
    }
    if(site) {
      return path;
    }
    return null;
  }


}
}
