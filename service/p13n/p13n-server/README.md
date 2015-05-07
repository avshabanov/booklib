## Quick Start

In p13n-server run the following:

```
mvn exec:java -Dexec.args="--port 9097"
```

Then after start you can query REST API:

```
# Query favorites of types 10 and 11 for user 120
curl -u testonly:test -H 'Accept: application/json' -H "Content-Type: application/json" -X POST -d '{"userId": 120, "types": [10, 11]}' http://127.0.0.1:9097/rest/p13n/favorites/query -s | python -mjson.tool
```

The result may look as follows:

```js
{
    "entries": []
}
```
