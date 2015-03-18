package com.alexshabanov.booklib.service

import com.alexshabanov.booklib.model.BookMeta
import com.alexshabanov.booklib.model.NamedValue
import com.alexshabanov.booklib.service.dao.BookDao
import com.alexshabanov.booklib.service.dao.NamedValueDao
import com.alexshabanov.booklib.service.dao.DEFAULT_LIMIT
import com.alexshabanov.booklib.model.FavoriteKind
import com.alexshabanov.booklib.model.FavoriteStatus
import com.alexshabanov.booklib.model.toFavoriteStatus

//
// Presentational model
//

data class Book(val meta: BookMeta, val favorite: Boolean,
                val authors: List<NamedValue>, val genres: List<NamedValue>)

data class NamedValuePage(val namedValue: NamedValue,
                          val books: List<Book>,
                          val startBookId: Long?,
                          private val favStatus: FavoriteStatus) {
  var favorite: Boolean
    get() = favStatus == FavoriteStatus.FAVORITE
    private set(b: Boolean) = throw IllegalStateException()

  var hasFavorite: Boolean
    get() = favStatus != FavoriteStatus.UNDECIDED
    private set(b: Boolean) = throw IllegalStateException()
}

private fun asNamedValuePage(namedValue: NamedValue, books: List<Book>, limit: Int,
                             favStatus: FavoriteStatus = FavoriteStatus.UNDECIDED) = NamedValuePage(
    namedValue = namedValue,
    books = books,
    favStatus = favStatus,
    startBookId = (if (books.size() < limit) null else books.last().meta.id))

//
// Service Implementation
//

class BookService(val bookDao: BookDao, val namedValueDao: NamedValueDao, val userService: UserProfileService) {
  fun getRandomBooks(userId: Long) = getBooksByMeta(userId, bookDao.getRandomBooks())

  fun getBooksByMeta(userId: Long, bookMetaList: List<BookMeta>): List<Book> {
    val bookIds: List<Long> = bookMetaList.map { it.id ?: -1 }
    val bookAuthorsMap = namedValueDao.getAuthorsOfBooks(bookIds)
    val bookGenresMap = namedValueDao.getGenresOfBooks(bookIds)

    return bookMetaList.map { Book(meta = it,
        favorite = userService.isFavorite(userId, FavoriteKind.BOOK, it.id ?: -1),
        authors = bookAuthorsMap.get(it.id) ?: listOf(),
        genres = bookGenresMap.get(it.id) ?: listOf())
    }
  }

  fun getAuthorPageModel(userId: Long, authorId: Long, startBookId: Long?, limit: Int = DEFAULT_LIMIT) = asNamedValuePage(
      namedValue = namedValueDao.getAuthorById(authorId),
      favStatus = toFavoriteStatus(userService.isFavorite(userId, FavoriteKind.AUTHOR, authorId)),
      books = getBooksByMeta(userId, bookDao.getBooksOfAuthor(authorId, startBookId, limit)),
      limit = limit)

  fun getGenrePageModel(userId: Long, genreId: Long, startBookId: Long?, limit: Int = DEFAULT_LIMIT) = asNamedValuePage(
      namedValueDao.getGenreById(genreId),
      getBooksByMeta(userId, bookDao.getBooksOfGenre(genreId, startBookId, limit)), limit)

  fun getLanguagePageModel(userId: Long, languageId: Long, startBookId: Long?, limit: Int = DEFAULT_LIMIT) = asNamedValuePage(
      namedValueDao.getLanguageById(languageId),
      getBooksByMeta(userId, bookDao.getBooksByLanguage(languageId, startBookId, limit)), limit)

  fun getGenres() = namedValueDao.getGenres()

  fun getLanguages() = namedValueDao.getLanguages()

  fun getAuthorNameHint(namePrefix: String? = null) = namedValueDao.getAuthorNameHint(namePrefix)

  fun getAuthorsByNamePrefix(namePrefix: String, startWithName: String? = null, limit: Int = DEFAULT_LIMIT) =
      namedValueDao.getAuthorsByNamePrefix(namePrefix, startWithName, limit)

  fun getAuthorById(id: Long) = namedValueDao.getAuthorById(id)

  fun getBookById(userId: Long, bookId: Long): Book = getBooksByMeta(userId, listOf(bookDao.getBookById(bookId))).get(0)
}
