joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/optimizely-ui.css');
coremediaEditorPlugins.push({
  name:"Optimizely Plugin",
  mainClass:"com.coremedia.blueprint.studio.OptimizelyStudioPlugin"
});
