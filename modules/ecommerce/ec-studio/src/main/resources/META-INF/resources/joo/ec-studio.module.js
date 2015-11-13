joo.loadStyleSheet("joo/resources/css/ec-plugin.css");
joo.loadModule("${project.groupId}", "${project.artifactId}");
//noinspection JSUnusedGlobalSymbols
coremediaEditorPlugins.push({
  name:"ECommerce Extensions",
  mainClass:"com.coremedia.ecommerce.studio.ECommerceStudioPlugin"
});


