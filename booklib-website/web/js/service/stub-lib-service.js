var domainUtil = require('../util/domain-util.js');
var devUtil = require('../util/dev-util.js');

var PERSONS = [
  {id: 3000, name: "Alex"}
];

var GENRES = [
  {id: 4000, name: "sf"}
];

var BOOKS = [
  {
    id: 123,
    title: 'Sample Book',
    addDate: 123000000,
    lang: {id: 1000, name: "en"},
    persons: PERSONS,
    genres: GENRES
  }
];

var FAV_BOOKS = BOOKS;
var FAV_PERSONS = PERSONS;

function StubLibService() {
}

StubLibService.prototype.getStorefrontPage = function ajaxGetStorefrontPage() {
  var result = {
    favorites: {
      books: FAV_BOOKS,
      persons: FAV_PERSONS
    }
  };
  return devUtil.newResolvableDelayedPromise(result);
}

StubLibService.prototype.getBooks = function (request) {
  var result = {books: [], persons: [], genres: []};
  var bookIds = request.bookIds || [];
  var personIds = request.personIds || [];
  if (bookIds.length !== 0 || personIds.length !== 0) {
    result = {books: BOOKS, persons: PERSONS, genres: GENRES};
  }
  return devUtil.newResolvableDelayedPromise(result);
}

StubLibService.prototype.queryBooks = function (request) {
  var result = {bookIds: [], offsetToken: null};
  if (request.personId == 3000 && request.offsetToken == null) {
    result.bookIds = [123];
  }
  return devUtil.newResolvableDelayedPromise(result);
}

//
// Exports
//

module.exports.StubLibService = StubLibService;
