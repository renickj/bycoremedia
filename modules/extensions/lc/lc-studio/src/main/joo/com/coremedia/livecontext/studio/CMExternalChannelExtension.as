package com.coremedia.livecontext.studio {
import com.coremedia.blueprint.base.components.quickcreate.QuickCreate;
import com.coremedia.blueprint.base.components.quickcreate.QuickCreate_properties;
import com.coremedia.blueprint.base.components.quickcreate.processing.ProcessingData;
import com.coremedia.blueprint.studio.config.components.navigationLinkFieldWrapper;
import com.coremedia.blueprint.studio.dialog.editors.NavigationLinkFieldWrapper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Component;

/**
 * Extension, the enhances the QuickCreateDialog for CMChannel with the parent navigation editor
 */
public class CMExternalChannelExtension {

  //content and custom properties for quick create dialog
  public static const PARENT_PROPERTY:String = "parentChannel";
  public static const CHILDREN_PROPERTY:String = "children";

  public static const CONTENT_TYPE_EXTERNAL_PAGE:String = "CMExternalChannel";
  public static const CONTENT_TYPE_PAGE:String = "CMChannel";

  public static function register():void {
    /**
     * Apply custom properties for CMChannel
     */
    QuickCreate.addQuickCreateDialogProperty(CONTENT_TYPE_EXTERNAL_PAGE, PARENT_PROPERTY, createComponent);

    QuickCreate.addSuccessHandler(CONTENT_TYPE_EXTERNAL_PAGE, process);
  }

  /**
   * Creates the UI Component for the Quick Creation Dialog
   * @param data the Data Process Object
   * @param properties The properties of the bound object
   * @return the UI Component
   */
  private static function createComponent(data:ProcessingData, properties:Object):Component {
    var c:Content = null;
    if (properties.bindTo) {
      c = properties.bindTo.getValue();
    }
    if (c && c.getType().getName() === CONTENT_TYPE_EXTERNAL_PAGE) {
      data.set(PARENT_PROPERTY, c);
      ValueExpressionFactory.create(ContentPropertyNames.PATH, c).loadValue(function (path:String):void {
        data.set(ProcessingData.FOLDER_PROPERTY, path);
      });
    }
    properties.label = QuickCreate_properties.INSTANCE.parent_label;
    properties.doctype = CONTENT_TYPE_PAGE;
    return new NavigationLinkFieldWrapper(navigationLinkFieldWrapper(properties));
  }

  /**
   * Adds a hook for processing the creation of CMChannel
   * @param content the content to created
   * @param data the processing data with varios informations
   * @param callback the function to call after processing
   */
  private static function process(content:Content, data:ProcessingData, callback:Function):void {

    //parent property is read from a link list, so resolve value from array
    var parentContent:Content = data.get(PARENT_PROPERTY);
    content.getProperties().set('title',content.getName());

    if(parentContent) {
      linkToList(parentContent, content, CHILDREN_PROPERTY, data, function():void {
        callback.call(null);
      });
    }
    else {
      callback.call(null);
    }
  }

  private static function linkToList(parentContent:Content, content:Content, property:String, data:ProcessingData, callback:Function):void {
    if(parentContent) {
      parentContent.load(function():void {
        var children:Array = parentContent.getProperties().get(property);
        if(!children) {
          children = [];
        }
        if(children.indexOf(content) === -1) { //maybe the dialog is linking too.
          children = children.concat(content);
          parentContent.getProperties().set(property, children);
          data.addAdditionalContent(parentContent);
        }
        callback.call(null);
      });
    }
    else {
      callback.call(null);
    }
  }

}
}