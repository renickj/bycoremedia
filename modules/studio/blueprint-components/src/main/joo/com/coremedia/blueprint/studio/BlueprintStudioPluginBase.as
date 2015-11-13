package com.coremedia.blueprint.studio {

import com.coremedia.blueprint.studio.config.components.blueprintStudioPlugin;
import com.coremedia.blueprint.studio.plugins.SiteAwareVisibilityPluginBase;
import com.coremedia.blueprint.studio.property.ImageLinkListRenderer;
import com.coremedia.blueprint.studio.util.ContentInitializer;
import com.coremedia.blueprint.studio.util.UserUtil;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentProperties;
import com.coremedia.cms.editor.configuration.StudioPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;
import com.coremedia.cms.editor.sdk.plugins.TabExpandPlugin;

/**
 * The blueprint plugin, handles different util initializations.
 */
public class BlueprintStudioPluginBase extends StudioPlugin {

  public function BlueprintStudioPluginBase(config:blueprintStudioPlugin) {
    super(config);
  }

  /**
   * Retrieve the master document from which the given document
   * was derived as a translation. Return null if the document is
   * not the translation of another document.
   *
   * @param content the content to analyse
   * @return the master document
   */
  public function resolveMasterDocument(content:Content):Content {
    var contentProperties:ContentProperties = content.getProperties();
    if (contentProperties) {
      var readOnlyContents:Array = contentProperties.get('master') as Array;
      if (readOnlyContents) {
        return readOnlyContents[0] as Content;
      }
    }
    return null;
  }

  override public function init(editorContext:IEditorContext):void {
    //load all sites and cache them
    //caches the available users and groups
    UserUtil.init();

    //Enable advanced tabs
    TabExpandPlugin.ADVANCED_TABS_ENABLED = true;

    ContentInitializer.applyInitializers();

    super.init(editorContext);

    SiteAwareVisibilityPluginBase.preLoadConfiguration();

    editorContext.registerThumbnailUriRenderer("CMSelectionRules", ImageLinkListRenderer.renderCMSelectionRules);
    editorContext.registerThumbnailUriRenderer("CMCollection", ImageLinkListRenderer.renderCMCollections);
    editorContext.registerThumbnailUriRenderer("CMTeasable", ImageLinkListRenderer.renderCMTeasable);
    editorContext.registerThumbnailUriRenderer("CMPicture", ImageLinkListRenderer.renderPicture);
    editorContext.registerThumbnailUriRenderer("CMImage", ImageLinkListRenderer.renderPicture);
    editorContext.registerThumbnailUriRenderer("CMSymbol", ImageLinkListRenderer.renderSymbol);
  }
}
}