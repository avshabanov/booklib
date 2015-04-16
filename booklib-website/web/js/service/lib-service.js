// dev-only post-service

var u = require('./dev-service-util.js');
var rsvp = require('rsvp');

//
// Helpers
//

function getBookData(page, book) {
  var i;

  var genres = [];
  var genreIds = book["genreIds"];
  for (i = 0; i < genreIds.length; ++i) {
    genres.push(u.selectById(page["genres"], genreIds[i]));
  }

  var persons = [];
  var personRelations = book["personRelations"];
  for (i = 0; i < personRelations.length; ++i) {
    var rel = personRelations[i];
    persons.push({
      id: rel["id"],
      name: u.selectById(page["persons"], rel["id"]).name,
      relation: rel["relation"]
    });
  }

  return {
    id: book["id"],
    title: book["title"],
    addDate: book["addDate"],
    lang: u.selectById(page["languages"], book["langId"]),
    origin: u.selectById(page["origins"], book["originId"]),
    persons: persons,
    genres: genres

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
    var persons = [];
    for (i = 0; i < personIds.length; ++i) {
      persons.push(u.selectById(data["persons"], personIds[i]));
    }

    return resolve({
      favorites: {
        persons: persons,
        books: books
      }
    });
  }
}

/**
 * Creates a new HTTP request for data fetched by using async AJAX interface.
 * TODO: make common
 *
 * @arg method String, that identifies HTTP request method, e.g. 'GET', 'PUT', 'POST', 'DELETE'
 * @arg url URL to the AJAX resource, e.g. '/rest/ajax/foo/bar/baz'
 * @arg requestBody An object, that represents a request, can be null
 * @return A new rsvp.Promise instance
 */
function makeAjaxRequest(method, url, requestBody) {
  return new rsvp.Promise(function(resolve, reject) {
    function xmlHttpRequestHandler() {
      if (this.readyState !== this.DONE) {
        return;
      }

      if (this.status === 200 || this.status === 201 || this.status === 204) {
        resolve(this.response);
      } else {
        reject(this);
      }
    };

    var client = new XMLHttpRequest();
    client.open(method, url);
    client.onreadystatechange = xmlHttpRequestHandler;
    client.responseType = "json";
    client.setRequestHeader("Accept", "application/json");

    if (requestBody != null) {
      client.setRequestHeader("Content-Type", "application/json");
      client.send(JSON.stringify(requestBody));
    } else {
      client.send();
    }
  });
}

// TODO: make common
function onAjaxError(e) {
  console.error("AJAX error: " + e);
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

AjaxLibService.prototype.getStorefrontPage = function ajaxGetStorefrontPage() {
  // inter-promise processing context
  var context = {
    personIds: null
  };

  // [1] fetch favorites (persons and authors)
  var promise = makeAjaxRequest("GET", "/rest/ajax/p13n/favorites");

  // [2] fetch book and person data
  promise = promise.then(function (response) {
    var favs = response["favorites"];
    context.personIds = favs["personIds"]; // save personIds (for later use in transformation)
    return makeAjaxRequest("POST", "/rest/ajax/books/page/fetch", {
      "pageIds": {
        "bookIds": favs["bookIds"],
        "personIds": favs["personIds"]
      },
      "fetchBookDependencies": true
    });
  }, onAjaxError);

  // [3] transform fetched data
  promise = promise.then(function (d) {
    return new rsvp.Promise(getFetchedPageToFavsHandler(d, context.personIds));
  }, onAjaxError);

  return promise; // end of processing
}

AjaxLibService.prototype.getBooks = function ajaxGetBooks(booksId) {
  var result = [];
  return u.newResolvableDelayedPromise(result, u.DELAY);
}

//
// StubLibService
//

function StubLibService() {
}

StubLibService.prototype.getStorefrontPage = function ajaxGetStorefrontPage() {
  var result = {
    favorites: {
      books: [
        {
          id: 123,
          title: 'Sample Book',
          addDate: 123000000,
          lang: {id: 1000, name: "en"},
          origin: {id: 2000, name: "sampleOrigin"},
          persons: [{id: 3000, name: "Alex"}],
          genres: [{id: 4000, name: "sf"}]
        }
      ],
      persons: [
        {id: 3000, name: "Alex"}
      ]
    }
  };
  return u.newResolvableDelayedPromise(result, u.DELAY);
}

StubLibService.prototype.getBooks = function (booksId) {
  var result = [];
  return u.newResolvableDelayedPromise(result, u.DELAY);
}

//
// exports
//

if (window.location.href.startsWith("file")) {
  module.exports.LibService = StubLibService; // use stub service
} else {
  module.exports.LibService = AjaxLibService; // use real service
}
