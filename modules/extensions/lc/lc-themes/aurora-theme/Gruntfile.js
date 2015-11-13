module.exports = function (grunt) {
  'use strict';

  // Force use of Unix newlines
  grunt.util.linefeed = '\n';

  // These plugins provide necessary tasks.
  require('load-grunt-tasks')(grunt, {scope: 'devDependencies'});
  require('time-grunt')(grunt);

  // --- Project configuration ---
  grunt.initConfig({

    // --- Properties ---
    pkg: grunt.file.readJSON('package.json'),
    distDir: 'target/resources/themes/aurora',

    // --- Task configuration ---
    clean: {
      options: {
        force: true
      },
      build: ['<%=  distDir %>']
    },
    copy: {
      main: {
        files: [{
          expand: true,
          cwd: 'src/',
          src: ['css/**', 'fonts/**', 'img/**', 'js/**', 'vendor/**'],
          dest: '<%=  distDir %>/'
        }]
      }
    }
  });

  // --- Tasks ---

  // Full distribution task without templates.
  grunt.registerTask('build', ['clean', 'copy:main']);

  // This theme has no templates.
  grunt.registerTask('buildWithTemplates', ['build']);

  // Default task = distribution.
  grunt.registerTask('default', ['build']);
};
