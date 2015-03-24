module.exports = function(grunt) {

  var tdir = 'target/webclient';

  var sources = [
    'node_modules/react/dist/react.js',
    'build/tmp/app-react-widgets.js',
    'js/router.js'
  ];

  function prepareSkeleton() {
    // Skeleton preparation task - see also http://gruntjs.com/api/grunt.file
    grunt.file.mkdir('target');
    grunt.file.mkdir(tdir); //< temporary dir for intermediate files
    grunt.file.mkdir(tdir + '/js');
    grunt.file.copy('html/_index.html', tdir + '/index.html');
  }

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),

    react: {
      combined_file_output: {
        files: {
          tdir + '/js/app-react-widgets.js': [
            'js/react/hello.jsx',
            'js/react/footer.jsx'
          ]
        }
      }
    },

    // (development only)
    concat: {
      options: {
        // define a string to put between each file in the concatenated output
        separator: ''
      },
      dist: {
        src: sources,
        dest: 'build/js/app.js'
      }
    },

    // (production version)
    uglify: {
      options: {
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n'
      },
      build: {
        src: 'build/js/app.js',
        dest: 'build/js/app.min.js'
      }
    }
  });

  // Load plugins
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-react');

  prepareSkeleton();

  grunt.registerTask('dist-finalize', 'Helper subtask for dist task that finalizes files structure', function () {
    grunt.file.copy('build/js/app.min.js', 'build/js/app.js');
    grunt.file.delete('build/js/app.min.js');
    grunt.file.delete('build/tmp');
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
    grunt.file.delete('build');
  });
};
