// dev-only post-service

var u = require('./dev-service-util.js');

//
// Test Data
//

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

var BOOKS = [
  {id: 1, title: 'Far Rainbow', fileSize: 255365, addDate: '2007-10-23',
    genres: [u.selectById(GENRES, 4), u.selectById(GENRES, 1)],
    authors: [u.selectById(AUTHORS, 5), u.selectById(AUTHORS, 6)],
    lang: u.selectById(LANG_CODES, 2), origin: u.selectById(ORIGINS, 4), isFavorite: true},

  {id: 2, title: 'Hermit and Sixfinger', fileSize: 169981, addDate: '2010-01-16',
    genres: [u.selectById(GENRES, 3)],
    authors: [u.selectById(AUTHORS, 7)],
    lang: u.selectById(LANG_CODES, 2), origin: u.selectById(ORIGINS, 4), isFavorite: true},

  {id: 3, title: 'The Dark Tower: The Gunslinger', fileSize: 412035, addDate: '2008-06-10',
    genres: [u.selectById(GENRES, 2), u.selectById(GENRES, 6)],
    authors: [u.selectById(AUTHORS, 3)],
    lang: u.selectById(LANG_CODES, 1), origin: u.selectById(ORIGINS, 2), series: {seriesId: 2, seriesPos: 1}},

  {id: 4, title: 'Hard to Be a God', fileSize: 198245, addDate: '2008-05-14',
    genres: [u.selectById(GENRES, 1), u.selectById(GENRES, 4)],
    authors: [u.selectById(AUTHORS, 5), u.selectById(AUTHORS, 6)],
    lang: u.selectById(LANG_CODES, 2), origin: u.selectById(ORIGINS, 2), series: {seriesId: 1, seriesPos: 4}, isFavorite: true},

  {}
];

//
// Helpers
//

function listSelectById(col, listIds) {
  var i;
  var r = [];
  for (i = 0; i < listIds.length; ++i) {
    r.push(u.selectById(col, listIds[i]));
  }
  return r;
}

//
// Service Definition
//

function LibService() {
}

LibService.prototype.getPosts = function getPosts() {
  u.debugLog("LibService.getPosts started");

  var result = [
    {id: 1, title: "test", content: "Lorem ipsum..."}
  ];

  return u.newResolvableDelayedPromise(result, u.DELAY);
}

LibService.prototype.getFavorites = function getFavorites() {
  var result = {favorites: {books: [1, 3], authors: [5, 6, 3]}};

  result.favorites.books = listSelectById(BOOKS, result.favorites.books);
  result.favorites.authors = listSelectById(AUTHORS, result.favorites.authors);

  return u.newResolvableDelayedPromise(result, u.DELAY);
}

LibService.prototype.getBooks = function getBooks(booksId) {
  var result = [];
  return u.newResolvableDelayedPromise(result, u.DELAY);
}

module.exports.LibService = LibService;
