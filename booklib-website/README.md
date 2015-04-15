
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

### How to configure logging

This is the sample configuration for logging used for dev purposes:

```
-Dapp.logback.logBaseName=/tmp/devlog-booklib -Dapp.logback.rootLogId=ROLLING_FILE
```

This configuration tells slf4j to use ROLLING_FILE appender and log everything to the files starting
with ``/tmp/devlog-booklib`` path.
