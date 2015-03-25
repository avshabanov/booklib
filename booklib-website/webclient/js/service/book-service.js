
/* Service for fetching book data */

var BookService = (function () {
  //var debugLog = console.debug;
  var debugLog = function () {};

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
      langId: 2, originId: 4},

    {id: 2, title: 'Hermit and Sixfinger', fileSize: 169981, addDate: '2010-01-16',
      genres: [selectById(GENRES, 3)],
      authors: [selectById(AUTHORS, 7)],
      langId: 2, originId: 4},

    {id: 3, title: 'The Dark Tower: The Gunslinger', fileSize: 412035, addDate: '2008-06-10',
      genres: [selectById(GENRES, 2), selectById(GENRES, 6)],
      authors: [selectById(AUTHORS, 3)],
      langId: 1, originId: 2, series: {seriesId: 2, seriesPos: 1}},

    {id: 4, title: 'Hard to Be a God', fileSize: 198245, addDate: '2008-05-14',
      genres: [selectById(GENRES, 1), selectById(GENRES, 4)],
      authors: [selectById(AUTHORS, 5), selectById(AUTHORS, 6)],
      langId: 2, originId: 4, series: {seriesId: 1, seriesPos: 4}},

    {}
  ];

  function resolveAfterDelay(timeout, deferred, data) {
    setTimeout(function () {
      debugLog("resolved data: " + JSON.stringify(data));
      deferred.resolve(data);
    }, timeout);
    return deferred;
  }

  // actual object
  // dev, local only version, returns jquery promises (imitates AJAX)
  return {
    getFavoriteBooks: function () {
      debugLog("dev.BookService.getFavoriteBooks started");
      return resolveAfterDelay(200, new $.Deferred(), [BOOKS[1], BOOKS[2], BOOKS[3]]);
    }
  };
})();
