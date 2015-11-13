package com.coremedia.blueprint.studio.util {

import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.config.workArea;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.logging.Logger;

import ext.Panel;

/**
 * Common utility method for the studio.
 */
public class StudioUtil {
  private static var workAreaTabChangeExpression:ValueExpression;
  private static var documentTabChangeExpression:ValueExpression;

  /**
   * Returns the value expression work area tab change events are fired to.
   * @return
   */
  public static function getWorkAreaTabChangeExpression():ValueExpression {
    if (!workAreaTabChangeExpression) {
      workAreaTabChangeExpression = ValueExpressionFactory.create('workAreaTab', beanFactory.createLocalBean());
    }
    return workAreaTabChangeExpression;
  }

  /**
   * Returns the value expression document tab change events are fired to.
   * @return
   */
  public static function getDocumentTabChangeExpression():ValueExpression {
    if (!documentTabChangeExpression) {
      documentTabChangeExpression = ValueExpressionFactory.create('documentTab', beanFactory.createLocalBean());
    }
    return documentTabChangeExpression;
  }

  /**
   * Returns the preference value if the preferences exist and is readable, null otherwise.
   * @param name The preference to read.
   * @return The preference value.
   */
  public static function getPreference(name:String):String {
    if (!editorContext.getPreferences() || !editorContext.getPreferences().getState().readable) {
      Logger.info('User preferences not found or not readable.');
      return null;
    }
    return editorContext.getPreferences().get(name);
  }

  /**
   * Uses the reload frame method of the premular
   * to reload the preview.
   */
  public static function reloadPreview():void {
    window.setTimeout(function ():void {
      var premular:Panel = editorContext.getWorkArea().getActiveTab();
      if (premular) {
        var previewPanel:PreviewPanel = premular.find('itemId', 'previewPanel')[0] as PreviewPanel;
        if (previewPanel) {
          previewPanel.reloadFrame();
        }
      }
    }, 300);
  }

  /**
   * Checks if the given content is in the list of excluded previewable documents.
   * @param content The content to check.
   * @return True, if the given document does not support a preview.
   */
  public static function isExcludedDocumentTypeWithoutPreview(content:Content):Boolean {
    var exclusions:Array = editorContext.getDocumentTypesWithoutPreview();
    for (var i:int = 0; i < exclusions.length; i++) {
      var type:String = exclusions[i];
      if (type === content.getType().getName()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Opens the given content in a new tab.
   * @param content The content to open in a new tab.
   */
  public static function openInTab(content:Content):void {
    editorContext.getContentTabManager().openDocument(content);
  }

  /**
   * Returns the content of the active selected content.
   * @return The active content object.
   */
  public static function getActiveContent():Content {
    return workArea.ACTIVE_CONTENT_VALUE_EXPRESSION.getValue();
  }

}
}