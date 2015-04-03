module.exports = function(grunt) {

  function tdir(relPath) { return 'target/webclient/' + relPath; }

  function sdir(relPath) { return 'webclient/' + relPath; }

  var sources = [
    'node_modules/react/dist/react.js',
    'node_modules/director/lib/director.js',
    sdir('js/service/book-service.js'),
    tdir('js/app-react-widgets.js'),
    sdir('js/app.js')
  ];

  function prepareSkeleton() {
    // Skeleton preparation task - see also http://gruntjs.com/api/grunt.file
    grunt.file.mkdir(tdir('js'));
    grunt.file.mkdir(tdir('css'));
    grunt.file.copy(sdir('html/_index.html'), tdir('index.html'));
    grunt.file.copy(sdir('css/global.css'), tdir('css/global.css'));
  }

  // prepare combined reactjs files separately (because of tdir(...) in the object key)
  var combinedReactFiles = {};
  combinedReactFiles[tdir('js/app-react-widgets.js')] = [
    // widgets
    sdir('js/react/widget/fav-star.jsx'),
    sdir('js/react/widget/book-item.jsx'),
    sdir('js/react/widget/book-list.jsx'),

    // pages
    sdir('js/react/page/main.jsx'),
    sdir('js/react/page/about.jsx')
  ];

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),

    react: {
      combined_file_output: {
        files: combinedReactFiles
      }
    },

    // (development only)
    concat: {
      options: {
        banner: '/*! Generated app.js */\n',
        // define a string to put between each file in the concatenated output
        separator: ''
      },
      dist: {
        src: sources,
        dest: tdir('js/app.js')
      }
    },

    // (production version)
    uglify: {
      options: {
        banner: '/*! Generated app.js <%= grunt.template.today("yyyy-mm-dd") %> */\n'
      },
      build: {
        src: tdir('js/app.js'),
        dest: tdir('js/app.min.js')
      }
    }
  });

  // Load plugins
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-react');

  prepareSkeleton();

  grunt.registerTask('dist-finalize', 'Helper subtask for dist task that finalizes files structure', function () {
    //grunt.file.copy('build/js/app.min.js', 'build/js/app.js');
    //grunt.file.delete('build/js/app.min.js');
    //grunt.file.delete('build/tmp');
  });

  // Default task that generates development build
  grunt.registerTask('default', [
    'react', 'concat'
  ]);

  // Release task that generates production build
  grunt.registerTask('dist', [
    'react', 'concat', 'uglify', 'dist-finalize'
  ]);

  grunt.registerTask('clean', 'Recursively cleans build folder', function () {
    grunt.file.delete('target');
  });
};
