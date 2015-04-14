
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

### How to invoke REST API

```
curl -u testonly:test -H 'Accept: application/json' -H "Content-Type: application/json" -X POST -d '{"pageIds": {"bookIds": [1,2,3,4,5,18]}, "fetchBookDependencies": true}' http://127.0.0.1:8080/rest/ajax/books/page/fetch -s | python -mjson.tool
```
