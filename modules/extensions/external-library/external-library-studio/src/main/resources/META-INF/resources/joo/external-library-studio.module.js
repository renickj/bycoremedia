joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/external-library-ui.css');
coremediaEditorPlugins.push({
  name:"External Library",
  mainClass:"com.coremedia.blueprint.studio.ExternalLibraryStudioPlugin"
});
