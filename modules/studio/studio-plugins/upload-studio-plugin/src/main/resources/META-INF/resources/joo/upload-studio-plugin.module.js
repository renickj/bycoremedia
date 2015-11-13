joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/upload-ui.css');
coremediaEditorPlugins.push({
  name:"Upload",
  mainClass:"com.coremedia.blueprint.studio.UploadStudioPlugin"
});
