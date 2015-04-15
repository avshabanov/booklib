// dev-only post-service

var u = require('./dev-service-util.js');
var rsvp = require('rsvp');

//
// Helpers
//

function getFetchedPageToFavsHandler(data) {
  return function transformFetchedPageToFavs(resolve, reject) {
    return resolve({favorites: {persons: [], books: data.books}});
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
      if (this.readyState === this.DONE) {
        if (this.status === 200 || this.status === 201 || this.status === 204) {
          resolve(this.response);
        } else {
          reject(this);
        }
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
}

AjaxLibService.prototype.getStorefrontPage = function prodGetStorefrontPage() {
  // [1] fetch favorites (persons and authors)
  var promise = makeAjaxRequest("GET", "/rest/ajax/p13n/favorites");

  // [2] fetch book and person data
  promise = promise.then(function (response) {
    return makeAjaxRequest("POST", "/rest/ajax/books/page/fetch", {
      "pageIds": {
        "bookIds": response["favorites"]["bookIds"],
        "personIds": response["favorites"]["personIds"]
      },
      "fetchBookDependencies": true
    });
  }, onAjaxError);

  // [3] transform fetched data
  promise = promise.then(function (d) {
    return new rsvp.Promise(getFetchedPageToFavsHandler(d));
  }, onAjaxError);

  return promise; // end of processing
}

AjaxLibService.prototype.getBooks = function prodGetBooks(booksId) {
  var result = [];
  return u.newResolvableDelayedPromise(result, u.DELAY);
}

module.exports.LibService = AjaxLibService;
