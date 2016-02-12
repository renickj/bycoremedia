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
    distDir: 'target/resources/themes/estore',
    //bootstrapDir: 'node_modules/bootstrap-sass/assets',
    //bootstrapSwipe: 'node_modules/bootstrap-carousel-swipe',
    //jqueryDir: 'node_modules/jquery/dist',
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
          '<%=  distDir %>/css/estore.css': 'src/sass/estore.scss'
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
        tasks: ['copy:templates'],
        options: {
          livereload: true
        }
      },
      images: {
        files: 'src/images/**/*.*',
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
          }
        ]
      },
      javascripts: {
        expand: true,
        flatten: true,
        filter: 'isFile',
        src: [
          'src/js/*'
        ],
        dest: '<%=  distDir %>/js/'
      },
      images: {
        expand: true,
        flatten: true,
        filter: 'isFile',
        src: ['src/images/**'],
        dest: '<%=  distDir %>/images/'
      },
      templates: {
        src: 'target/estore-theme-1.0-SNAPSHOT.jar',
        dest: '<%=  distDir %>/templates/estore-templates.jar'
      }
    },
    autoprefixer: {
      options: {
        browsers: '<%= autoprefixerBrowsers %>',
        map: true
      },
      build: {
        src: '<%= distDir %>/css/estore.css'
      }
    }
    /*styledocco: {
      build: {
        options: {
          cmd: "./node_modules/.bin/coremedia-styledocco",
          name: 'Coremedia Corporation'
        },
        files: {
          'docs/styleguide': '<%= distDir %>/css/corporate.css'
        }
      }
    }*/
  });

  // --- Tasks ---

  // Full distribution task.
  grunt.registerTask('build', ['clean', 'copy', 'sass', 'autoprefixer']);

  // Default task = distribution.
  grunt.registerTask('default', ['build']);
};
