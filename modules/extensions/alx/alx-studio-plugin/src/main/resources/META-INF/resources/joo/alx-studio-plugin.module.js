joo.loadModule("${project.groupId}", "${project.artifactId}");
joo.loadStyleSheet('joo/resources/css/${project.artifactId}.css');

coremediaEditorPlugins.push({
  mainClass : 'com.coremedia.blueprint.studio.analytics.AnalyticsStudioPlugin',
  name : 'Analytics'
});
