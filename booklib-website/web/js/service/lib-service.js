// dev-only post-service

var domainUtil = require('../util/domain-util.js');
var ajax = require('../util/ajax-util.js');
var rsvp = require('rsvp');
var StubLibService = require('./stub-lib-service.js').StubLibService;

//
// Helpers
//

function getAdaptedId(entityId) {
  var entityType = typeof(entityId);
  if (entityType === "number") {
    return entityId;
  }

  if (entityType === "string") {
    return parseInt(entityId); // TODO: remove this, make sure dispatcher makes all the appropriate conversions
  }

  throw new Error("Unknown type of entityId=" + entityId);
}

function getAdaptedIds(entityIds) {
  var ids = [];
  for (var i = 0; i < entityIds.length; ++i) {
    ids.push(getAdaptedId(entityIds[i])); // TODO: remove this, make sure dispatcher makes all the appropriate conversions
  }
  return ids;
}

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
    addDate: new Date(book["addDate"]).toGMTString(),
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

AjaxLibService.prototype.queryBooks = function (request) {
  var searchRequest = {
    "sortType": request.sortType || "TITLE",
    "limit": request.limit || 8
  };

  if (request.offsetToken != null) {
    searchRequest["offsetToken"] = request.offsetToken;
  }

  if (request.personId != null) {
    searchRequest["personId"] = getAdaptedId(request.personId);
  }

  return ajax.request("POST", "/rest/ajax/books/query", searchRequest);
}

AjaxLibService.prototype.getBooks = function (request) {
  var bookIds = getAdaptedIds(request.bookIds || []);
  var personIds = getAdaptedIds(request.personIds || []);

  // [1] fetch book page
  var promise = ajax.request("POST", "/rest/ajax/books/page/fetch", {
    "pageIds": {
      "bookIds": bookIds,
      "personIds": personIds
    },
    "fetchBookDependencies": true
  });

  // [2] transform the data
  promise = promise.then(function (response) {
    return new rsvp.Promise(function (resolve, reject) {
      var books = [];
      var responseBooks = response["books"];

      if (responseBooks.length != bookIds.length) {
        // TODO: reusable error reporting function
        console.error("source data is malformed: response", response);
      }

      for (var j = 0; j < responseBooks.length; ++j) {
        var bookMeta = responseBooks[j];
        //console.log("bookMeta:", bookMeta);
        var bookData = getBookData(response, bookMeta);
        //console.log("bookData:", bookData);
        books.push(bookData);
      }

      return resolve({
        books: books,
        persons: response.persons,
        genres: response.genres
      });
    });
  }, ajax.onError);

  return promise;
}

//
// exports
//

if (window.location.href.startsWith("file")) {
  module.exports.LibService = StubLibService; // use stub service
} else {
  module.exports.LibService = AjaxLibService; // use real service
}
