var domainUtil = require('../util/domain-util.js');
var devUtil = require('../util/dev-util.js');


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
  return devUtil.newResolvableDelayedPromise(result);
}

StubLibService.prototype.getBooks = function (booksId) {
  var result = [];
  return devUtil.newResolvableDelayedPromise(result);
}

//
// Exports
//

module.exports.StubLibService = StubLibService;
