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
    distDir: 'target/resources/themes/corporate',
    bootstrapDir: 'node_modules/bootstrap-sass/assets',
    bootstrapSwipe: 'node_modules/bootstrap-carousel-swipe',
    jqueryDir: 'node_modules/jquery/dist',
    autoprefixerBrowsers: [
      "last 2 versions",
      "Firefox >= 24",
      "Explorer >= 9",
      "Safari >= 8" 
    ],

    // --- Task configuration ---
    clean: {
      options: {
        force: true
      },
      build: ['<%=  distDir %>']
    },
    sass: {
      options: {
        sourceMap: true
      },
      build: {
        files: {
          '<%=  distDir %>/css/bootstrap.css': 'src/sass/bootstrap.scss',
          '<%=  distDir %>/css/corporate.css': 'src/sass/corporate.scss',
          '<%=  distDir %>/css/preview.css': 'src/sass/preview.scss'
        }
      }
    },
    watch: {
      css: {
        files: 'src/sass/**/*.scss',
        tasks: ['sass', 'autoprefixer'],
        options: {
          livereload: true // default port: 35729
        }
      },
      js: {
        files: 'src/js/*.js',
        tasks: ['copy:javascripts'],
        options: {
          livereload: true
        }
      },
      ftl: {
        files: 'src/main/resources/**/*.ftl',
        options: {
          livereload: true
        }
      },
      images: {
        files: 'src/images/**/*.*',
        tasks: ['copy:images'],
        options: {
          livereload: true
        }
      }
    },
    copy: {
      fonts: {
        files: [
          {
            expand: true,
            src: ['src/fonts/*'],
            dest: '<%=  distDir %>/fonts/',
            filter: 'isFile',
            flatten: true
          },
          {
            expand: true,
            src: ['<%=  bootstrapDir %>/fonts/**'],
            dest: '<%=  distDir %>/fonts/bootstrap/',
            filter: 'isFile',
            flatten: true
          }
        ]
      },
      javascripts: {
        expand: true,
        flatten: true,
        filter: 'isFile',
        src: [
          'src/js/*',
          '<%=  jqueryDir %>/jquery.js',
          '<%=  bootstrapDir %>/javascripts/bootstrap.js',
          '<%=  bootstrapSwipe %>/carousel-swipe.js'
        ],
        dest: '<%=  distDir %>/js/'
      },
      images: {
        expand: true,
        flatten: true,
        filter: 'isFile',
        src: ['src/images/**'],
        dest: '<%=  distDir %>/images/'
      }
    },
    autoprefixer: {
      options: {
        browsers: '<%= autoprefixerBrowsers %>',
        map: true
      },
      build: {
        src: '<%= distDir %>/css/corporate.css'
      }
    },
    compress: {
      templates: {
        options: {
          archive: '<%=  distDir %>/templates/corporate-templates.jar',
          mode: 'zip'
        },
        expand: true,
        dot: true,
        cwd: 'src/main/resources/',
        src: ['**']
      }
    },
    styledocco: {
      build: {
        options: {
          cmd: "./node_modules/.bin/coremedia-styledocco",
          name: 'Coremedia Corporation'
        },
        files: {
          'docs/styleguide': '<%= distDir %>/css/corporate.css'
        }
      }
    }
  });

  // --- Tasks ---

  // Full distribution task without templates.
  grunt.registerTask('build', ['clean', 'copy', 'sass', 'autoprefixer']);

  // Full distribution task with templates.
  grunt.registerTask('buildWithTemplates', ['build', 'compress:templates']);

  // Default task = distribution.
  grunt.registerTask('default', ['build']);
};
