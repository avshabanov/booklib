module.exports = function(grunt) {
  function prepareSkeleton() {
    grunt.file.mkdir('target/web/js');
    grunt.file.mkdir('target/web/tmp/js');
  }

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),

    watch: {
      scripts: {
        files: ['web/js/**/*.*', 'web/css/**/*.css', 'web/html/index.html'],
        tasks: ['copy', 'react', 'browserify']
      }
    },

    copy: {
      main: {
        files: [
          {
            src: 'web/html/index.html',
            dest: 'target/web/index.html'
          },
          {
            cwd: 'web/css',
            src: '**',
            dest: 'target/web/css',
            expand: true
          },
          {
            cwd: 'web/js',                // source js dir
            src: ['**', '!**/*.jsx'],     // copy all files and subfolders to the temporary dor, except for jsx
            dest: 'target/web/tmp/js',    // destination folder, used by browserify
            expand: true                  // required when using cwd
          }
        ]
      }
    },

    react: {
      dynamic_mappings: {
        files: [
          {
            expand: true,
            cwd: 'web/js/view',
            src: ['**/*.jsx'],
            dest: 'target/web/tmp/js/view',
            ext: '.js'
          }
        ]
      }
    },

    browserify: {
      dist: {
        files: {
          'target/web/js/app.js': ['target/web/tmp/js/main.js'],
        }
      }
    },

    uglify: {
      options: {
        banner: '/*! Generated app.js <%= grunt.template.today("yyyy-mm-dd") %> */\n'
      },
      build: {
        src: 'target/web/js/app.js',
        dest: 'target/web/js/app.min.js'
      }
    }
  });

  // Load plugins
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-browserify');
  grunt.loadNpmTasks('grunt-react');
  grunt.loadNpmTasks('grunt-contrib-uglify');

  prepareSkeleton();

  // Default task that generates development build
  grunt.registerTask('default', [
    'copy', 'react', 'browserify'
  ]);

  // Helper task for moving app.min.js to app.js
  grunt.registerTask('uglify-fix-app', 'Rename app.min.js to app.js', function () {
    grunt.file.copy('target/web/js/app.min.js', 'target/web/js/app.js');
    grunt.file.delete('target/web/js/app.min.js');
  });

  // Release task that generates production build
  grunt.registerTask('dist', [
    'copy', 'react', 'browserify', 'uglify', 'uglify-fix-app'
  ]);

  grunt.registerTask('clean', 'Recursively cleans build folder', function () {
    grunt.file.delete('target');
  });
};
