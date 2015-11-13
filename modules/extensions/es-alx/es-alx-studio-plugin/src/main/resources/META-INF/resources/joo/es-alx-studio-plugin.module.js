joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/${project.artifactId}.css');
joo.loadStyleSheet("joo/resources/css/morris.css");

coremediaEditorPlugins.push({
  mainClass : 'com.coremedia.blueprint.studio.esanalytics.EsAnalyticsStudioPlugin',
  name : 'EsAnalytics'
});


joo.loadScript('joo/resources/js/raphael.js');
// morris needs jquery. but jquery seems to be loaded already by other cm modules (image-cropping?)
joo.loadScript('joo/resources/js/morris.js');
joo.loadScript('joo/resources/js/morris.esalx-plugin.js');


