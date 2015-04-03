// dev-only post-service


var u = require('./dev-service-util.js');
var Deferred = $.Deferred;

function LibService() {
}

LibService.prototype.getPosts = function getPosts() {
  u.debugLog("LibService.getPosts started");

  var result = [
    {id: 1, title: "test", content: "Lorem ipsum..."}
  ];

  return u.withDefaults(u.resolveAfterDelay(u.DELAY, new Deferred(), result));
}

LibService.prototype.getPost = function getPost(postId) {
  u.debugLog("LibService.getPost started");

  var result = {id: 1, title: "test", content: "Lorem ipsum..."};

  return u.withDefaults(u.resolveAfterDelay(u.DELAY, Deferred(), result));
}

LibService.prototype.getComments = function getComments(entityId, entityType) {
  u.debugLog("LibService.getComments started");

  var results = [];
  if (entityType !== 'POST' && entityId != 1) {
    results = [];
  } else {
    results = [
      {id: 100, content: "First", authorId: 800},
      {id: 101, content: "Second", authorId: 800},
      {id: 102, content: "Third", authorId: 800},
      {id: 103, content: "Fifth", authorId: 823},
      {id: 104, content: "One more...", authorId: 810}
    ];
  }

  return u.withDefaults(u.resolveAfterDelay(u.DELAY, new Deferred(), result));
}

module.exports.LibService = LibService;