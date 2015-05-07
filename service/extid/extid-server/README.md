

## Quick Start

In extid-server run the following:

```
mvn exec:java -Dexec.args="--port 9093"
```

## Sample invocation

Query IDs:

```
curl -u testonly:test -H 'Accept: application/json' -H "Content-Type: application/json" -X POST -d '{"intIds": [{"id": 3,"typeId": 10}], "includeAllGroupIds": true}' http://127.0.0.1:8080/rest/extid/id-pairs/query -s | python -mjson.tool
```

Results in:

```js
{
    "idPairs": [
        {
            "extId": "301575949364",
            "groupId": 20,
            "intId": {
                "id": 3,
                "typeId": 10
            }
        },
        {
            "extId": "B000OCXILW",
            "groupId": 21,
            "intId": {
                "id": 3,
                "typeId": 10
            }
        },
        {
            "extId": "B0012GV992",
            "groupId": 22,
            "intId": {
                "id": 3,
                "typeId": 10
            }
        },
        {
            "extId": "OxM59ts4iloC",
            "groupId": 24,
            "intId": {
                "id": 3,
                "typeId": 10
            }
        }
    ]
}
```

Get All Types:

```
curl -u testonly:test -H 'Accept: application/json' -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/rest/extid/types -s | python -mjson.tool
```

Results in:

```js
{
    "types": [
        {
            "id": 10,
            "name": "BOOK"
        }
    ]
}
```
