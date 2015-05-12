
var rsvp = require('rsvp');

/**
 * Creates a new HTTP request for data fetched by using async AJAX interface.
 *
 * @arg method String, that identifies HTTP request method, e.g. 'GET', 'PUT', 'POST', 'DELETE'
 * @arg url URL to the AJAX resource, e.g. '/rest/ajax/foo/bar/baz'
 * @arg requestBody An object, that represents a request, can be null
 * @return A new rsvp.Promise instance
 */
function request(method, url, requestBody) {
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

function onError(xmlHttpRequest) {
  // TODO: better error handling
  window.lastXhr = xmlHttpRequest; // TODO: hooks, make sure this is used for debug purposes only: record last XHR
  console.error("AJAX error, status:", xmlHttpRequest.status, xmlHttpRequest.statusText,
    "responseURL:", xmlHttpRequest.responseURL);
}

//
// Exports
//

module.exports.request = request;
module.exports.onError = onError;
