package com.coremedia.blueprint.studio.template {
import com.coremedia.blueprint.studio.template.config.templateBeanListChooser;
import com.coremedia.blueprint.studio.util.LocalizationUtil;
import com.coremedia.blueprint.studio.util.UserUtil;
import com.coremedia.cap.common.session;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cms.editor.sdk.components.BeanListChooser;
import com.coremedia.cms.editor.sdk.util.PathFormatter;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.Blob;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.logging.Logger;

import ext.Ext;
import ext.XTemplate;

/**
 * Base class of the template chooser:
 * Loads all template folders and their descriptions.
 */
public class TemplateBeanListChooserBase extends BeanListChooser {

  private static const CONTENT_ITEM_SELECTOR_EXPRESSION:String = "div.bean-list-chooser-item-wrap";
  private static const SHORT_NAME_MAX_LENGTH:int = 15;

  private var configPaths:String;

  private static var xTemplate:XTemplate = new XTemplate('<tpl for=".">',
          '<div class="bean-list-chooser-item-wrap {mergedItemClass}">',
          '<div class="bean-list-chooser-item-border">',
          '<div class="bean-list-chooser-item" ext:qtip="{description}">',
          '<div class="large">',
          '<img width="100%" height="100%" src="{iconUri}"/>',
          '</div>',
          '</div>',
          '<p ext:qtip="{description}">{descriptionShort}</p>',
          '</div>',
          '</div>',
          '</tpl>');

  public function TemplateBeanListChooserBase(config:templateBeanListChooser) {
    configPaths = config.configPaths;

    super(config);
  }

  /**
   * This function is designed for multiple executions to evaluate all templates. As this is a FunctionValueExpression,
   * the inner function will always be reinvoked, if a collected dependency will be invalided.
   * @return a ValueExpressoin representing the founded templates to choose from
   */
  internal function getTemplates():ValueExpression {
    return ValueExpressionFactory.createFromFunction(function ():Array { // the inner function for
                                                                         // the FunctionValueExpression

      // build up an array with all paths including the user home directory
      var paths:Array = configPaths.split(",");
      paths = paths.concat(UserUtil.getHome().getPath() + '/' +
              CreateFromTemplateStudioPluginSettings_properties.INSTANCE.template_folder_fragment +
              "/" + CreateFromTemplateStudioPluginSettings_properties.INSTANCE.doctype );

      var contentListResult:Array = [];
      var contentRepo:ContentRepository = session.getConnection().getContentRepository();

      // Method returns undefined until this flag is true at the end of the function.
      var ready:Boolean = true;

      // iterate over each path
      paths.forEach(function (path:String):void {
        path = PathFormatter.formatSitePath(path);
        if(!path) { //maybe null if the active site is not set
          return;
        }

        var currentNode:Content = contentRepo.getRoot();
        var pathSegments:Array = path.split("/");
        pathSegments = pathSegments.splice(1, pathSegments.length);
        var validPath:Boolean = true;

        // because this method has to be completely synchronous, we have to iterate over each path fragment instead of
        // use contentRepo.getChildren(...), because this call is asynchronous.
        pathSegments.forEach(function (segment:String):void {
          if (currentNode && currentNode.getChildrenByName()) { // currentNode is loaded
            if (segment !== "") {
              if (!currentNode.getChildrenByName()[segment]) {
                validPath = false;
              } else {
                currentNode = currentNode.getChildrenByName()[segment]
              }
            }
          } else { // currentNode is not loaded
            currentNode = undefined;
          }
        });

        // If the given path exists...
        if (validPath) {
          if (currentNode && currentNode.getChildrenByName()) { // currentNode is loaded
            var children:Array = currentNode.getChildren();
            //this is the type of channel to be found in a template folder so that the folder is used as template.
            var templateChannelDocType:ContentType = session.getConnection().getContentRepository().getContentType(CreateFromTemplateStudioPluginSettings_properties.INSTANCE.doctype);

            // Iterator over each children of the folder
            children.forEach(function (templateFolder:Content):void {
              if (templateFolder.isFolder()) {
                if (templateFolder && templateFolder.getChildrenByName()) { // template folder is loaded

                  // Check, if there is a content (CMSymbol) with the predefined descriptor name
                  var c:Content = templateFolder.getChildrenByName()
                                 [CreateFromTemplateStudioPluginSettings_properties.INSTANCE.template_descriptor_name];
                  if (c) {
                    c.load(); // load content asychronously, but ignore callback
                    if (c.getProperties()) { // but get a dependency on properties to force reevaluation of this
                                             // ValueExpression
                      // verify that there is a template page next to the descriptor, otherwise ignore this descriptor
                      var pageFound:Boolean = false;
                      var templateFolderChildren:Array = templateFolder.getChildDocuments();
                      templateFolderChildren.forEach(function(folderChild:Content) {
                        folderChild.load();
                        if (folderChild.getProperties()) {
                          if (folderChild.getType().isSubtypeOf(templateChannelDocType)) {
                            pageFound = true;
                          }
                        }
                      });
                      // Check, if there exists a localized variant of this Content (CMSymbol)
                      if (pageFound) {
                        var locContent:Content = LocalizationUtil.getLocalizedContentSync(c);
                        if (locContent !== undefined) {
                          // If content is not already part of the result list
                          if (contentListResult.indexOf(locContent) === -1) {
                            // add it
                            contentListResult.push(locContent);
                          }
                        } else { // Descriptor is not loaded yet
                          ready = false;
                        }
                      }
                    } else { // Content is not loaded yet
                      ready = false;
                    }
                  }
                } else { // Template Folder is not loaded yet
                  ready = false;
                }
              }
            });
          } else { // path is not loaded yet
            ready = false;
          }
        } else {
          Logger.info(TemplateBeanListChooser +": No valid documents are found in configured path '" + path + "'.");
        }
      });
      if (ready) {
        // All contents should be loaded by now, adding a comparator does not cause the FVE to fire again.
        return contentListResult.sort(comparator);
      } else {
        // Make sure that the contentList remains undefined until it is complete.
        return undefined;
      }
    });
  }

  /////////////////////////////////////////////////////////////////////////////////////
  // Functions for evaluate values for dataview store converters
  /////////////////////////////////////////////////////////////////////////////////////
  internal function getXTemplateForRendering():XTemplate {
    return xTemplate;
  }

  public function computeIconURL(name:String, content:Content):String {
    if (content && content.getProperties()) {
      var imageBlob:Blob = content.getProperties().get('icon');
      if (imageBlob) {
        var size:String = CreateFromTemplateStudioPluginSettings_properties.INSTANCE.template_icon_size;
        return imageBlob.getUri() + '/rm/box;w=' + size + ';h=' + size;
      }
    }
    return Ext.BLANK_IMAGE_URL;
  }

  public function getDescription(name:String, content:Content):String {
    if (content && content.getProperties()) {

      var description:String = content.getProperties().get('description');
      if (!description || description.length === 0) {
        description = name;
      }
      return description;
    }
    return "";
  }

  public function getDescriptionCurtailed(name:String, content:Content):String {
    return curtail(getDescription(name, content));
  }

  internal static function curtail(s:String):String {
    if (!s) return '';
    if (s.length > SHORT_NAME_MAX_LENGTH) {
      return s.substr(0, SHORT_NAME_MAX_LENGTH - 3) + '...';
    }
    return s;
  }

  /**
   * Comparator to sort the result list of templates, when this list is completely built
   * @param val1 one Bean
   * @param val2 another Bean
   * @return the compare result
   */
  internal function comparator(val1:Bean, val2:Bean):Number {
    return getDescription(val1.get('name'), val1 as Content).localeCompare(getDescription(val2.get('name'), val2 as Content));
  }

  /**
   * Returns the itemSelector value matching the content template.
   *
   * @return default itemSelector value
   */
  public static function getContentItemSelector():String {
    return CONTENT_ITEM_SELECTOR_EXPRESSION;
  }

}
}