package com.coremedia.blueprint.studio.topicpages {

import com.coremedia.blueprint.studio.topicpages.config.topicPagesStudioPlugin;
import com.coremedia.cms.editor.configuration.StudioPlugin;

/**
 * The topicpages plugin, handles different util initializations.
 */
public class TopicPagesStudioPluginBase extends StudioPlugin {

  public function TopicPagesStudioPluginBase(config:topicPagesStudioPlugin) {
    super(config);
  }

}
}