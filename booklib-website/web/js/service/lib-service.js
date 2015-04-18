// dev-only post-service

var domainUtil = require('../util/domain-util.js');
var devUtil = require('../util/dev-util.js');
var ajax = require('../util/ajax-util.js');
var rsvp = require('rsvp');
var StubLibService = require('./stub-lib-service.js').StubLibService;

//
// Helpers
//

function getBookData(page, book) {
  var i;

  var persons = [];
  var personRelations = book["personRelations"];
  for (i = 0; i < personRelations.length; ++i) {
    var rel = personRelations[i];
    persons.push({
      id: rel["id"],
      name: domainUtil.selectById(page["persons"], rel["id"]).name,
      relation: rel["relation"]
    });
  }

  return {
    id: book["id"],
    title: book["title"],
    addDate: book["addDate"],
    lang: domainUtil.selectById(page["languages"], book["langId"]),
    origin: domainUtil.selectById(page["origins"], book["originId"]),
    persons: persons,
    genres: domainUtil.selectByIds(page["genres"], book["genreIds"])

    // TODO: externalIds
    // TODO: series
  };
}

function getFetchedPageToFavsHandler(data, personIds) {
  return function transformFetchedPageToFavs(resolve, reject) {
    var i;

    // extract book info
    var books = [];
    var bookMetaList = data["books"];
    for (i = 0; i < bookMetaList.length; ++i) {
      books.push(getBookData(data, bookMetaList[i]));
    }

    // extract persons
    var persons = domainUtil.selectByIds(data["persons"], personIds);

    return resolve({
      favorites: {
        persons: persons,
        books: books
      }
    });
  }
}

//
// Service
//

function AjaxLibService() {
  this._cache = {
    books: {},
    persons: {},
    languages: {},
    origins: {},
    genres: {},
    series: {}
  };
}

AjaxLibService.prototype.getStorefrontPage = function () {
  // inter-promise processing context
  var context = {
    personIds: null
  };

  // [1] fetch favorites (persons and authors)
  var promise = ajax.request("GET", "/rest/ajax/p13n/favorites");

  // [2] fetch book and person data
  promise = promise.then(function (response) {
    var favs = response["favorites"];
    context.personIds = favs["personIds"]; // save personIds (for later use in transformation)
    return ajax.request("POST", "/rest/ajax/books/page/fetch", {
      "pageIds": {
        "bookIds": favs["bookIds"],
        "personIds": favs["personIds"]
      },
      "fetchBookDependencies": true
    });
  }, ajax.onError);

  // [3] transform fetched data
  promise = promise.then(function (d) {
    return new rsvp.Promise(getFetchedPageToFavsHandler(d, context.personIds));
  });

  return promise; // end of processing
}

AjaxLibService.prototype.getBooks = function (booksId) {
  var result = [];
    return devUtil.newResolvableDelayedPromise(result);
}

//
// StubLibService
//


//
// exports
//

if (window.location.href.startsWith("file")) {
  module.exports.LibService = StubLibService; // use stub service
} else {
  module.exports.LibService = AjaxLibService; // use real service
}
