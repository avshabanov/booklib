var app = app || {};

/* Service for fetching book data */

(function () {
  //var debugLog = console.debug;
  var debugLog = function () {};

  var DELAY = 200;

  function nv(id, name) { // produces name-value pair
    return {'id': id, 'name': name}
  };

  var AUTHORS = [
    nv(1, 'Jack London'),
    nv(2, 'Edgar Poe'),
    nv(3, 'Stephen King'),
    nv(4, 'Joe Hill'),
    nv(5, 'Arkady Strugatsky'),
    nv(6, 'Boris Strugatsky'),
    nv(7, 'Victor Pelevin'),
    nv(8, 'Jason Ciaramella')
  ];

  var GENRES = [
    nv(1, 'sci_fi'),
    nv(2, 'fantasy'),
    nv(3, 'essay'),
    nv(4, 'novel'),
    nv(5, 'comics'),
    nv(6, 'western'),
    nv(7, 'horror')
  ];

  var LANG_CODES = [
    nv(1, 'en'),
    nv(2, 'ru')
  ];

  var ORIGINS = [
    nv(1, 'EnglishClassicBooks'),
    nv(2, 'EnglishModernBooks'),
    nv(3, 'EnglishMisc'),
    nv(4, 'RussianBooks')
  ];

  var SERIES = [
    nv(1, 'Noon: 22nd Century'),
    nv(2, 'The Dark Tower')
  ]

  function selectById(col, id) {
    var i;
    for (i = 0; i < col.length; ++i) {
      if (col[i].id == id) {
        return col[i];
      }
    }
    throw new Error('No element in collection=' + col + ' with id=' + id);
  }

  var BOOKS = [
    {id: 1, title: 'Far Rainbow', fileSize: 255365, addDate: '2007-10-23',
      genres: [selectById(GENRES, 4), selectById(GENRES, 1)],
      authors: [selectById(AUTHORS, 5), selectById(AUTHORS, 6)],
      lang: selectById(LANG_CODES, 2), origin: selectById(ORIGINS, 4), isFavorite: true},

    {id: 2, title: 'Hermit and Sixfinger', fileSize: 169981, addDate: '2010-01-16',
      genres: [selectById(GENRES, 3)],
      authors: [selectById(AUTHORS, 7)],
      lang: selectById(LANG_CODES, 2), origin: selectById(ORIGINS, 4), isFavorite: true},

    {id: 3, title: 'The Dark Tower: The Gunslinger', fileSize: 412035, addDate: '2008-06-10',
      genres: [selectById(GENRES, 2), selectById(GENRES, 6)],
      authors: [selectById(AUTHORS, 3)],
      lang: selectById(LANG_CODES, 1), origin: selectById(ORIGINS, 2), series: {seriesId: 2, seriesPos: 1}},

    {id: 4, title: 'Hard to Be a God', fileSize: 198245, addDate: '2008-05-14',
      genres: [selectById(GENRES, 1), selectById(GENRES, 4)],
      authors: [selectById(AUTHORS, 5), selectById(AUTHORS, 6)],
      lang: selectById(LANG_CODES, 2), origin: selectById(ORIGINS, 2), series: {seriesId: 1, seriesPos: 4}, isFavorite: true},

    {}
  ];

  function resolveAfterDelay(timeout, deferred, data) {
    setTimeout(function () {
      debugLog("resolved data: " + JSON.stringify(data));
      deferred.resolve(data);
    }, timeout);
    return deferred;
  }

  function withDefaults(promise) {
    promise.fail(function (e) {
      console.error("Service call failed. Error=" + e);
    });
    return promise;
  }

  // actual object
  // dev, local only version, returns jquery promises (imitates AJAX)
  app.BookService = {
    getFavoriteBooks: function () {
      debugLog("dev.BookService.getFavoriteBooks started");
      var result = [];
      var i;
      for (i = 0; i < BOOKS.length; ++i) {
        if (BOOKS[i].isFavorite === true) {
          result.push(BOOKS[i]);
        }
      }
      return withDefaults(resolveAfterDelay(DELAY, $.Deferred(), result));
    },

    getBookById: function (id) {
      debugLog("dev.BookService.getBookById started");
      var result = selectById(BOOKS, id);
      return withDefaults(resolveAfterDelay(DELAY, $.Deferred(), result));
    },

    setFavorite: function (id, type, isFavorite) {
      debugLog("dev.BookService.setFavorite started");
      var deferred = $.Deferred();
      setTimeout(function () {
        var col;
        if (type === "BOOK") {
          col = BOOKS;
        } else if (type === "AUTHOR") {
          col = AUTHORS;
        } else {
          deferred.reject(new Error("Unable to set favorite status for entity with type=" + type));
          return;
        }

        try {
          var entity = selectById(col, id);
          entity.isFavorite = isFavorite;
          deferred.resolve(isFavorite)
          return;
        } catch (e) {
          deferred.reject(e);
        }
      }, DELAY);
      return withDefaults(deferred);
    }
  };
})();
