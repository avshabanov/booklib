/* Defines utility functions for development only */

var rsvp = require('rsvp');

var DELAY = 800;

function newResolvableDelayedPromise(data, delay) {
  delay = delay | DELAY;
  return new rsvp.Promise(function (resolve, reject) {
    setTimeout(function () {
      resolve(data);
    }, delay);
  });
}

//
// Exports
//

module.exports.DELAY = DELAY;

module.exports.newResolvableDelayedPromise = newResolvableDelayedPromise;
