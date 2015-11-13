package com.coremedia.blueprint.studio.upload {
import com.coremedia.blueprint.studio.config.upload.openUploadDialogAction;
import com.coremedia.blueprint.studio.config.upload.uploadDialog;
import com.coremedia.blueprint.studio.upload.dialog.UploadDialog;
import com.coremedia.blueprint.studio.util.StudioConfigurationUtil;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cms.editor.sdk.actions.ContentAction;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.ui.data.ValueExpressionFactory;

public class OpenUploadDialogAction extends ContentAction {

  private var uploadSettings:UploadSettings;

  /**
   * @param config
   */
  public function OpenUploadDialogAction(config:openUploadDialogAction) {
    super(config);
    if (!config['handler']) {
      setHandler(openDropBox, this);
    }
  }

  override protected function calculateDisabled():Boolean {
    if (!UploadHelper.isHTML5()) {
      return true;
    }
    var contents:Array = getContents();
    if (contents === undefined) {
      return undefined;
    }
    /*always enable uploadAction in create content menu from favorites toolbar*/
    if (contents.length === 0) {
      return false;
    }
    if (contents.length !== 1) {
      return true;
    }
    var theFolder:Content = contents[0] as Content;
    if (!theFolder || theFolder.isDocument()) {
      return true;
    }
    var mimeTypeMappings:Object = getUploadSettings().ensureLoaded().getMimeTypeMappings();
    if (mimeTypeMappings === undefined) {
      return undefined;
    }

    var repository:ContentRepository = theFolder.getRepository();
    for (var mimeType:String in mimeTypeMappings) {
      if (mimeTypeMappings.hasOwnProperty(mimeType)) {
        var contentType:ContentType = repository.getContentType(mimeTypeMappings[mimeType]);
        if (repository.getAccessControl().mayCreate(theFolder, contentType)) {
          return false;
        }
      }
    }
    var defaultContentType:ContentType = repository.getContentType(getUploadSettings().getDefaultContentType());
    return !repository.getAccessControl().mayCreate(theFolder, defaultContentType);
  }

  private function openDropBox():void {
    var contents:Array = getContents();
    if(contents.length > 0) {
      var content:Content = contents[0];
      if (content.isDocument()) {
        content = content.getParent();
      }
      openUploadDialog(content);
    }
    else {
      openUploadDialog(null);
    }
  }

  private function getUploadSettings():UploadSettings {
    if (!uploadSettings) {
      uploadSettings = new UploadSettings();
    }
    return uploadSettings;
  }

  private function openUploadDialog(content:Content):void {
    getUploadSettings().load(function():void {
      if (!content) {
        var preferredSite:Site = editorContext.getSitesService().getPreferredSite();
        if (preferredSite) {
          ValueExpressionFactory.createFromFunction(getEditorialPathFromStudioConfiguration).loadValue(function(folder:Content):void {
            if (folder) {
              createDialog(folder);
            } else {
              var root:Content = preferredSite.getSiteRootFolder();
              root.getChild(getUploadSettings().getDefaultUploadPath(), function (folder:Content):void {
                if (folder) {
                  createDialog(folder);
                } else {
                  createDialog(null);
                }
              });
            }
          });
        } else {
          createDialog(session.getConnection().getContentRepository().getRoot());
        }
      } else {
        createDialog(content);
      }
    });

  }

  private function getEditorialPathFromStudioConfiguration():Content {
    var folder:Content = StudioConfigurationUtil.getConfiguration("Content Creation", "paths.editorial");
    return folder;
  }

  private function createDialog(content:Content):void {
    ValueExpressionFactory.create(ContentPropertyNames.PATH, content).loadValue(function ():void {
      var dialog:UploadDialog = new UploadDialog(uploadDialog({
        content: content,
        settings: getUploadSettings()
      }));
      dialog.show();
    });
  }
}
}