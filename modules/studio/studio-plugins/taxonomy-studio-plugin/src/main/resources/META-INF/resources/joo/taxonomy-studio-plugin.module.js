joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/taxonomy-studio-plugin.css');
coremediaEditorPlugins.push({
  name:"Taxonomy",
  mainClass:"com.coremedia.blueprint.studio.TaxonomyStudioPlugin"
});
