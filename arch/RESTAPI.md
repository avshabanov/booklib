
# AJAX API


Sample:

``
PUT /favorites/book/1?isFavorite=true

>> 204 NO CONTENT
``

## POST /favorites/query

Request Sample:

```js
{items: [{id: 1, type: 1}, {id: 2, type 3}]}
```

Response Sample (only favorites included):

```js
{favorites: {books: [1, 2, 3], authors: [4, 5, 6]}}
```

# Page Interactions

## Storefront

* p13n.GetFavorites
* book.GetPage
* p13n.GetPersonalizedInfo
** GetRatings
** GetOverviews
** GetCommentsCount
** GetComments

# Sample Flow

## Storefront

```js

// p13n.GetFavorites request:
{userId: 15000, types: [1, 2]}
// response:
{elements: [{type: 1, ids: [100, 200, 300]}, {type: 2, ids: [10000, 12000]}]}

// book.GetPage
{pageIds: {bookIds: [10000, 12000], personIds: [100, 200, 300]}, fetchBookDependencies: true}
// response:
{
    books: [
        {id: 1, title: 'Alice in the Wonderland'}
        //...
    ],
    personIds: [
        {id: 100, name: 'Alice'}
        //...
    ]
}

// p13n.GetPage
{
    userId: 15000,
    elements: [{type: 1, ids: [100, 200, 300]}, {type: 2, ids: [10000, 12000]}]
}
// response:
{
    elements: [
        {
            type: 1,
            
            items: [
                {
                    id: 100,
                    rating: 495,
                    favorite: true,
                    comments: {
                        total: 12
                    },
                    overviews: {
                        total: 14
                    }
                }
            ]
        },
        {
            type: 2,
            items: [/* ... */]
        }
    ]
}

```

