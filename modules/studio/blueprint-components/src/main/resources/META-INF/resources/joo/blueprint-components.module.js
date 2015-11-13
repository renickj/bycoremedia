joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/blueprint-ui.css');
joo.loadStyleSheet('joo/resources/css/newcontent-ui.css');
joo.loadStyleSheet('joo/resources/css/slider-icons.css');
joo.loadStyleSheet('joo/resources/css/blueprint-ui-extensions.css');
coremediaEditorPlugins.push({
  name:"Blueprint",
  mainClass:"com.coremedia.blueprint.studio.BlueprintStudioPlugin"
});

