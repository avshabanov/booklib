
function debugLog() {}

//
// Exports
//

module.exports.DELAY = 200;

module.exports.selectById = function selectById(col, id) {
  var i;

  for (i = 0; i < col.length; ++i) {
    if (col[i].id == id) {
      return col[i];
    }
  }

  throw new Error('No element in collection=' + col + ' with id=' + id);
}

module.exports.debugLog = debugLog;

module.exports.withDefaults = function withDefaults(promise) {
  promise.fail(function (e) {
    console.error("Service call failed. Error=" + e);
  });
  return promise;
}

module.exports.resolveAfterDelay = function resolveAfterDelay(timeout, deferred, data) {
  setTimeout(function () {
    debugLog("resolved data: " + JSON.stringify(data));
    deferred.resolve(data);
  }, timeout);
  return deferred;
}
