joo.loadScript('osm/OpenLayers.js');
joo.loadScript('osm/OpenStreetMap.js');
joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/osm-ui.css');
coremediaEditorPlugins.push({
  name:"Open Streetmap",
  mainClass:"com.coremedia.blueprint.studio.osm.OSMStudioPlugin"
});
