
# AJAX API

## GET /books/{id}

Response:

```js
{
  id: <long>
  title: <String>
  ...

  genres: [{1, 'classic'}],
  persons: {authors: [[2, 'Oscar Wilde']]},
  language: {3, 'en'}
}
```

## GET /comments/{id}

## GET /authors/{id}

##  GET /authors/query

Query Parameters:

* ``String nameStart``
* ``long lastId``
* ``int limit``

## GET /genres

## GET /genres/{id}

## GET /languages

## GET /languages/{id}

## PUT /favorites/{type:book,author}/{id}?isFavorite=true/false

Sample:

``
PUT /favorites/book/1?isFavorite=true

>> 204 NO CONTENT
``

## POST /favorites/query

Request Sample:

```js
{items: {books: [1, 2, 3], authors: [1]}}
```

Response Sample (only favorites included):

```js
{favorites: {books: [1, 2, 3], authors: [4, 5, 6]}}
```
