joo.loadStyleSheet("joo/resources/css/es-plugin.css");
joo.loadModule("${project.groupId}", "${project.artifactId}");
//noinspection JSUnusedGlobalSymbols
coremediaEditorPlugins.push({
  name:"Elastic Social Extensions",
  mainClass:"com.coremedia.blueprint.elastic.social.studio.ElasticSocialStudioPlugin"
});


