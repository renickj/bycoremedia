joo.loadStyleSheet("joo/resources/css/livecontext-plugin.css");
joo.loadModule("${project.groupId}", "${project.artifactId}");
//noinspection JSUnusedGlobalSymbols
coremediaEditorPlugins.push({
  name:"Livecontext Extensions",
  mainClass:"com.coremedia.livecontext.studio.LivecontextStudioPlugin"
});


