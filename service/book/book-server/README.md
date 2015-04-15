
## How to create a database

Sample:

Create database:

```
java -cp ~/.m2/repository/com/h2database/h2/1.4.183/h2-1.4.183.jar org.h2.tools.RunScript -url jdbc:h2:/tmp/bookdb -user sa -script service/book/book-server/src/main/resources/bookService/sql/book-schema.sql
```

Fill it with test data:

```
java -cp ~/.m2/repository/com/h2database/h2/1.4.183/h2-1.4.183.jar org.h2.tools.RunScript -url jdbc:h2:/tmp/bookdb -user sa -script service/book/book-server/src/main/resources/bookService/sql/book-fixture.sql
```

Then you can connect to it using h2 shell (remove rlwrap if you don't want to use readline):

```
rlwrap java -cp ~/.m2/repository/com/h2database/h2/1.4.183/h2-1.4.183.jar org.h2.tools.Shell -url jdbc:h2:/tmp/bookdb -user sa
```

## Sample invocation

```
curl -u testonly:test -H 'Accept: application/json' -H "Content-Type: application/json" -X POST -d '{"sortType": "TITLE", "limit": 8}' http://127.0.0.1:8080/rest/book/books/query -s | python -mjson.tool
```

Results in:

```js
{
    "bookIds": [
        18,
        22,
        16,
        1,
        13,
        8,
        4,
        2
    ],
    "offsetToken": "2,Hermit and Sixfinger"
}
```

Query full book information by using book IDs:

```
curl -u testonly:test -H 'Accept: application/json' -H "Content-Type: application/json" -X POST -d '{"pageIds": {"bookIds": [1,2,3,4,5,18]}, "fetchBookDependencies": true}' http://127.0.0.1:8080/rest/book/books/page/fetch -s | python -mjson.tool
```

Sample output:

```js
{
    "books": [
        {
            "addDate": 1193097600000,
            "externalIds": [],
            "fileSize": 255365,
            "genreIds": [
                1,
                4
            ],
            "id": 1,
            "langId": 2,
            "originId": 4,
            "personRelations": [
                {
                    "id": 5,
                    "relation": "AUTHOR"
                },
                {
                    "id": 6,
                    "relation": "AUTHOR"
                }
            ],
            "seriesPos": [
                {
                    "id": 1,
                    "pos": 3
                }
            ],
            "title": "Far Rainbow"
        },
        {
            "addDate": 1263600000000,
            "externalIds": [],
            "fileSize": 169981,
            "genreIds": [
                3
            ],
            "id": 2,
            "langId": 2,
            "originId": 4,
            "personRelations": [
                {
                    "id": 7,
                    "relation": "AUTHOR"
                }
            ],
            "seriesPos": [],
            "title": "Hermit and Sixfinger"
        },
        {
            "addDate": 1407628800000,
            "externalIds": [],
            "fileSize": 0,
            "genreIds": [
                4
            ],
            "id": 18,
            "langId": 1,
            "originId": 2,
            "personRelations": [
                {
                    "id": 1,
                    "relation": "AUTHOR"
                }
            ],
            "seriesPos": [],
            "title": "A Daughter of the Snows"
        },
        {
            "addDate": 1213056000000,
            "externalIds": [
                {
                    "id": "301575949364",
                    "typeId": 20
                },
                {
                    "id": "B000OCXILW",
                    "typeId": 21
                },
                {
                    "id": "B0012GV992",
                    "typeId": 22
                },
                {
                    "id": "OxM59ts4iloC",
                    "typeId": 24
                }
            ],
            "fileSize": 412035,
            "genreIds": [
                2,
                6
            ],
            "id": 3,
            "langId": 1,
            "originId": 2,
            "personRelations": [
                {
                    "id": 3,
                    "relation": "AUTHOR"
                }
            ],
            "seriesPos": [
                {
                    "id": 2,
                    "pos": 1
                }
            ],
            "title": "The Dark Tower: The Gunslinger"
        },
        {
            "addDate": 1210723200000,
            "externalIds": [],
            "fileSize": 198245,
            "genreIds": [
                1,
                4
            ],
            "id": 4,
            "langId": 2,
            "originId": 4,
            "personRelations": [
                {
                    "id": 5,
                    "relation": "AUTHOR"
                },
                {
                    "id": 6,
                    "relation": "AUTHOR"
                }
            ],
            "seriesPos": [
                {
                    "id": 1,
                    "pos": 4
                }
            ],
            "title": "Hard to Be a God"
        },
        {
            "addDate": 1379548800000,
            "externalIds": [],
            "fileSize": 412035,
            "genreIds": [
                1,
                2,
                6,
                7
            ],
            "id": 5,
            "langId": 1,
            "originId": 2,
            "personRelations": [
                {
                    "id": 3,
                    "relation": "AUTHOR"
                }
            ],
            "seriesPos": [
                {
                    "id": 2,
                    "pos": 6
                }
            ],
            "title": "The Dark Tower: The Wind Through the Keyhole"
        }
    ],
    "externalBookTypes": [
        {
            "id": 20,
            "name": "eBay"
        },
        {
            "id": 21,
            "name": "AmazonKindleStore"
        },
        {
            "id": 22,
            "name": "AmazonPaperback"
        },
        {
            "id": 24,
            "name": "GoogleBooks"
        }
    ],
    "genres": [
        {
            "id": 1,
            "name": "sci_fi"
        },
        {
            "id": 2,
            "name": "fantasy"
        },
        {
            "id": 3,
            "name": "essay"
        },
        {
            "id": 4,
            "name": "novel"
        },
        {
            "id": 6,
            "name": "western"
        },
        {
            "id": 7,
            "name": "horror"
        }
    ],
    "languages": [
        {
            "id": 1,
            "name": "en"
        },
        {
            "id": 2,
            "name": "ru"
        }
    ],
    "origins": [
        {
            "id": 2,
            "name": "EnglishModernBooks"
        },
        {
            "id": 4,
            "name": "RussianBooks"
        }
    ],
    "persons": [
        {
            "id": 1,
            "name": "Jack London"
        },
        {
            "id": 3,
            "name": "Stephen King"
        },
        {
            "id": 5,
            "name": "Arkady Strugatsky"
        },
        {
            "id": 6,
            "name": "Boris Strugatsky"
        },
        {
            "id": 7,
            "name": "Victor Pelevin"
        }
    ],
    "series": [
        {
            "id": 1,
            "name": "Noon: 22nd Century"
        },
        {
            "id": 2,
            "name": "The Dark Tower"
        }
    ]
}
```
