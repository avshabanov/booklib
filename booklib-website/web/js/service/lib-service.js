// dev-only post-service

var u = require('./dev-service-util.js');
var rsvp = require('rsvp');

//
// Test Data
//

var PERSONS = [
  {id: 1, name: 'Jack London', kind: 'AUTHOR'},
  {id: 2, name: 'Edgar Poe', kind: 'AUTHOR'},
  {id: 3, name: 'Stephen King', kind: 'AUTHOR'},
  {id: 4, name: 'Joe Hill', kind: 'AUTHOR'},
  {id: 5, name: 'Arkady Strugatsky', kind: 'AUTHOR'},
  {id: 6, name: 'Boris Strugatsky', kind: 'AUTHOR'},
  {id: 7, name: 'Victor Pelevin', kind: 'AUTHOR'},
  {id: 8, name: 'Jason Ciaramella', kind: 'AUTHOR'}
];

var GENRES = [
  {id: 1, name: 'sci_fi'},
  {id: 2, name: 'fantasy'},
  {id: 3, name: 'essay'},
  {id: 4, name: 'novel'},
  {id: 5, name: 'comics'},
  {id: 6, name: 'western'},
  {id: 7, name: 'horror'}
];

var LANG_CODES = [
  {id: 1, name: 'en'},
  {id: 2, name: 'ru'}
];

var ORIGINS = [
  {id: 1, name: 'EnglishClassicBooks'},
  {id: 2, name: 'EnglishModernBooks'},
  {id: 3, name: 'EnglishMisc'},
  {id: 4, name: 'RussianBooks'}
];

var SERIES = [
  {id: 1, name: 'Noon: 22nd Century'},
  {id: 2, name: 'The Dark Tower'}
]

var BOOKS = [
  {id: 1, title: 'Far Rainbow', fileSize: 255365, addDate: '2007-10-23',
    genres: [u.selectById(GENRES, 4), u.selectById(GENRES, 1)],
    persons: [u.selectById(PERSONS, 5), u.selectById(PERSONS, 6)],
    lang: u.selectById(LANG_CODES, 2), origin: u.selectById(ORIGINS, 4), isFavorite: true},

  {id: 2, title: 'Hermit and Sixfinger', fileSize: 169981, addDate: '2010-01-16',
    genres: [u.selectById(GENRES, 3)],
    persons: [u.selectById(PERSONS, 7)],
    lang: u.selectById(LANG_CODES, 2), origin: u.selectById(ORIGINS, 4), isFavorite: true},

  {id: 3, title: 'The Dark Tower: The Gunslinger', fileSize: 412035, addDate: '2008-06-10',
    genres: [u.selectById(GENRES, 2), u.selectById(GENRES, 6)],
    persons: [u.selectById(PERSONS, 3)],
    lang: u.selectById(LANG_CODES, 1), origin: u.selectById(ORIGINS, 2), series: {seriesId: 2, seriesPos: 1}},

  {id: 4, title: 'Hard to Be a God', fileSize: 198245, addDate: '2008-05-14',
    genres: [u.selectById(GENRES, 1), u.selectById(GENRES, 4)],
    persons: [u.selectById(PERSONS, 5), u.selectById(PERSONS, 6)],
    lang: u.selectById(LANG_CODES, 2), origin: u.selectById(ORIGINS, 2), series: {seriesId: 1, seriesPos: 4}, isFavorite: true},

  {}
];

//
// Helpers
//

function listSelectById(col, listIds) {
  var i;
  var r = [];
  for (i = 0; i < listIds.length; ++i) {
    r.push(u.selectById(col, listIds[i]));
  }
  return r;
}

//
// Prod Lib Service Definition
//

function getFetchedPageToFavsHandler(data) {
  console.log("[2]...");
  return function transformFetchedPageToFavs(resolve, reject) {
    console.log("[3]...");
    //var result = {favorites: {books: [1, 3, 4], persons: [5, 6, 3]}};
    console.log("[3] data.books = " + JSON.stringify(data.books));
    return resolve({favorites: {persons: [], books: data.books}});
  }
}

function ProdLibService() {
}

ProdLibService.prototype.getStorefrontPage = function prodGetStorefrontPage() {
  var url = "/rest/ajax/books/page/fetch";
  var promise = new rsvp.Promise(function(resolve, reject) {
    function xmlHttpRequestHandler() {
      if (this.readyState === this.DONE) {
        if (this.status === 200) {
          resolve(this.response);
        } else {
          reject(this);
        }
      }
    };

    var client = new XMLHttpRequest();
    client.open("POST", url);
    client.onreadystatechange = xmlHttpRequestHandler;
    client.responseType = "json";
    client.setRequestHeader("Accept", "application/json");
    client.setRequestHeader("Content-Type", "application/json");
    client.send(JSON.stringify({
      "pageIds": {"bookIds": [1,3,4]},
      "fetchBookDependencies": true}));
  });

  return promise.then(function (d) {
    console.log("[1]...");
    return new rsvp.Promise(getFetchedPageToFavsHandler(d));
  }, function (e) {
    console.error("got error response: " + e);
  });

//  // get favorites...
//  var result = {favorites: {books: [1, 3, 4], persons: [5, 6, 3]}};
//
//  result.favorites.books = listSelectById(BOOKS, result.favorites.books);
//  result.favorites.persons = listSelectById(PERSONS, result.favorites.persons);
//
//  return u.newResolvableDelayedPromise(result, u.DELAY);
}

ProdLibService.prototype.getBooks = function prodGetBooks(booksId) {
  var result = [];
  return u.newResolvableDelayedPromise(result, u.DELAY);
}

//
// Development Lib Service Definition
//

function DevLibService() {
}

DevLibService.prototype.getStorefrontPage = function devGetStorefrontPage() {
  // get favorites...
  var result = {favorites: {books: [1, 3], persons: [5, 6, 3]}};

  result.favorites.books = listSelectById(BOOKS, result.favorites.books);
  result.favorites.persons = listSelectById(PERSONS, result.favorites.persons);

  return u.newResolvableDelayedPromise(result, u.DELAY);
}

DevLibService.prototype.getBooks = function devGetBooks(booksId) {
  var result = [];
  return u.newResolvableDelayedPromise(result, u.DELAY);
}




module.exports.LibService = ProdLibService;
