package com.coremedia.blueprint.studio.util {
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentProperties;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.actions.Actions_properties;
import com.coremedia.cms.editor.sdk.editorContext;
import com.coremedia.cms.editor.sdk.preview.PreviewPanel;
import com.coremedia.cms.editor.sdk.sites.Site;
import com.coremedia.cms.editor.sdk.sites.SitesService;
import com.coremedia.ui.data.FlushResult;

import joo.ResourceBundleAwareClassLoader;

/**
 * Initializer settings for the blueprint project.
 */
public class ContentInitializer {

  /**
   * The registration of the initializers for the corresponding document types.
   */
  public static function applyInitializers():void {
    editorContext.registerContentInitializer("CMArticle", initArticle);
    editorContext.registerContentInitializer("CMAudio", initAudio);
    editorContext.registerContentInitializer("CMChannel", initChannel);
    editorContext.registerContentInitializer("CMCollection", initCollection);
    editorContext.registerContentInitializer("CMDownload", initTeasable);
    editorContext.registerContentInitializer("CMImageMap", initCMImageMap);
    editorContext.registerContentInitializer("CMLinkable", initCMLinkable);
    editorContext.registerContentInitializer("CMLocalized", initCMLocalized);
    editorContext.registerContentInitializer("CMTaxonomy", initTaxonomy);
    editorContext.registerContentInitializer("CMLocTaxonomy", initTaxonomy);
    editorContext.registerContentInitializer("CMMedia", initTeasable);
    editorContext.registerContentInitializer("CMPicture", initPicture);
    editorContext.registerContentInitializer("CMQueryList", initQueryList);
    editorContext.registerContentInitializer("CMTeasable", initTeaser);
    editorContext.registerContentInitializer("CMViewtype", initViewType);
    editorContext.registerContentInitializer("CMVideo", initVideo);
  }

  private static function initViewType(content:Content):void {
    initCMLocalized(content);
    initializePropertyWithName(content, 'layout');
  }

  private static function initQueryList(content:Content):void {
    var localSettings:Struct = content.getProperties().get('localSettings');
    localSettings.getType().addIntegerProperty('limit', 10);
    initCMLinkable(content);
    initCMLocalized(content);
  }

  private static function initTeaser(content:Content):void {
    initializePropertyWithName(content, 'teaserTitle');
    initCMLinkable(content);
  }

  private static function initPicture(content:Content):void {
    initializePropertyWithName(content, 'title');
    initializePropertyWithName(content, 'alt');
    initCMLinkable(content);
  }

  private static function initAudio(content:Content):void {
    initializePropertyWithName(content, 'title');
    initCMLinkable(content);
  }

  private static function initVideo(content:Content):void {
    initializePropertyWithName(content, 'title');
    initCMLinkable(content);
  }

  private static function initTeasable(content:Content):void {
    initializePropertyWithName(content, 'title');
    initCMLinkable(content);
  }
  
  private static function initCollection(content:Content):void {
    setProperty(content, 'teaserTitle', content.getName());
    initCMLinkable(content);
  }  

  private static function initTaxonomy(content:Content):void {
    initTeasable(content);
    initializePropertyWithName(content, 'value');
  }

  private static function initArticle(content:Content):void {
    if (content.getProperties().get("title").length < 1){
       initializePropertyWithName(content, 'title');
    }
    initCMLinkable(content);
  }

  public static function initChannel(content:Content):void {
    initializePropertyWithName(content, 'title');
    initializePropertyWithName(content, 'segment');
    initCMLinkable(content);
  }

  public static function initCMLocalized(content:Content):void {
    var sitesService:SitesService = editorContext.getSitesService();
    var site:Site = sitesService.getSiteFor(content) || sitesService.getPreferredSite();
    var locale:String;
    if (site) {
      locale = site.getLocale().getLanguageTag();
    } else {
      locale = ResourceBundleAwareClassLoader.INSTANCE.getLocale();
    }
    setProperty(content, 'locale', locale);
  }

  public static function initCMLinkable(content:Content):void {
    initCMLocalized(content);
  }

  private static function initCMImageMap(content:Content):void {
    initCMLinkable(content);
    var localSettings:Struct = content.getProperties().get('localSettings');
    localSettings.getType().addStructProperty("overlay");
    var overlay:* = localSettings.get("overlay");
    overlay.set("displayTitle", true);
    overlay.set("displayDefaultPrice", true);
  }

  private static function initializePropertyWithName(content:Content, property:String):void{
    //Only initialize if the name of the content is not "New content item"
    if(content.getName() != Actions_properties.INSTANCE.Action_newContent_newDocumentName_text) {
      setProperty(content, property, content.getName());
    }
  }

  private static function setProperty(content:Content, property:String, value:Object):void {
    var properties:ContentProperties = content.getProperties();
    properties.set(property, value);
    content.flush(
            function (result:FlushResult):void {
              var previewPanel:PreviewPanel = editorContext.getWorkArea().getActiveTab().get('previewPanel') as PreviewPanel;
              if (StudioUtil.getActiveContent() && StudioUtil.getActiveContent().getId() === content.getId() && previewPanel) {
                previewPanel.reloadFrame();
              }
            });
  }
}
}
