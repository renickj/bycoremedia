joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/topicpages-ui.css');
coremediaEditorPlugins.push({
  name:"Topic Pages Editor",
  mainClass:"com.coremedia.blueprint.studio.topicpages.TopicPagesStudioPlugin"
});
