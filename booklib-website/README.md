
## Development Howtos

### How to run grunt

```
./node_modules/grunt-cli/bin/grunt
```

For web devs - you can use watchify:

```
./node_modules/grunt-cli/bin/grunt watch
```

### How to point website to dev resources

Add the following to the program JVM arguments (example):

```
-Dbooklib.override.staticPath=/path/to/booklib/booklib-website/target/web/
```
