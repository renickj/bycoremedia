joo.loadModule('${project.groupId}', '${project.artifactId}');
joo.loadStyleSheet('joo/resources/css/blueprint-forms.css');
coremediaEditorPlugins.push({
  name: "Blueprint Forms",
  mainClass: "com.coremedia.blueprint.studio.BlueprintFormsStudioPlugin"
});
