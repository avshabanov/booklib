var domainUtil = require('../util/domain-util.js');
var devUtil = require('../util/dev-util.js');

var BOOKS = [
  {
    id: 123,
    title: 'Sample Book',
    addDate: 123000000,
    lang: {id: 1000, name: "en"},
    persons: [{id: 3000, name: "Alex"}],
    genres: [{id: 4000, name: "sf"}]
  }
];

var PERSONS = [
  {id: 3000, name: "Alex"}
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

StubLibService.prototype.getBooks = function (booksId) {
  var result = [];
  return devUtil.newResolvableDelayedPromise(result);
}

StubLibService.prototype.getBookPage = function (bookId) {
  var result = {
    book: domainUtil.selectById(BOOKS, bookId)
  };

  return devUtil.newResolvableDelayedPromise(result);
}

//
// Exports
//

module.exports.StubLibService = StubLibService;
