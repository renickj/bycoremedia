joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/struct-ui.css');
coremediaEditorPlugins.push({
  name:"Struct Editor",
  mainClass:"com.coremedia.blueprint.studio.struct.StructEditorStudioPlugin"
});
