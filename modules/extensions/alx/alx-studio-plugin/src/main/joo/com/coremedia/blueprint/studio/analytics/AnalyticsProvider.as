package com.coremedia.blueprint.studio.analytics {

import com.coremedia.blueprint.studio.config.analytics.analyticsProvider;
import com.coremedia.cms.editor.sdk.EditorPlugin;
import com.coremedia.cms.editor.sdk.IEditorContext;

public class AnalyticsProvider implements EditorPlugin {

  private var config:analyticsProvider;

  /**
   * Register Analytics Providers as [providerName, localizedProviderName] two-element array;
   */
  public static const ANALYTICS_PROVIDERS:Array = [];

  public function AnalyticsProvider(config:analyticsProvider) {
    this.config = config;
  }

  public function init(editorContext:IEditorContext):void {
    ANALYTICS_PROVIDERS.push([config.providerName,config.localizedProviderName]);
  }

}
}