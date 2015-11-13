joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/external-preview-ui.css');
coremediaEditorPlugins.push({
  name:"External Preview",
  mainClass:"com.coremedia.blueprint.studio.externalpreview.ExternalPreviewStudioPlugin"
});
