package com.alexshabanov.booklib.service

import com.alexshabanov.booklib.model.BookMeta
import java.util.ArrayList
import java.util.LinkedHashSet
import com.alexshabanov.booklib.model.NamedValue

//
// Presentational model
//

data class Book(val meta: BookMeta, val authors: List<NamedValue>, val genres: List<NamedValue>)

data class NamedValuePage(val namedValue: NamedValue, val books: List<Book>, val startBookId: Long?)

private fun asNamedValuePage(namedValue: NamedValue, books: List<Book>, limit: Int) = NamedValuePage(
    namedValue = namedValue,
    books = books,
    startBookId = (if (books.size() < limit) null else books.last().meta.id))

//
// Service Implementation
//

class BookService(val bookDao: com.alexshabanov.booklib.service.dao.BookDao, val namedValueDao: com.alexshabanov.booklib.service.dao.NamedValueDao) {
  fun getRandomBooks() = getBooksByMeta(bookDao.getRandomBooks())

  fun getBooksByMeta(bookMetaList: List<BookMeta>): List<Book> {
    val bookIds: List<Long> = bookMetaList.map { it.id ?: -1 }
    val bookAuthorsMap = namedValueDao.getAuthorsOfBooks(bookIds)
    val bookGenresMap = namedValueDao.getGenresOfBooks(bookIds)

    return bookMetaList.map { Book(meta = it,
        authors = bookAuthorsMap.get(it.id) ?: listOf(),
        genres = bookGenresMap.get(it.id) ?: listOf())
    }
  }

  fun getAuthorPageModel(authorId: Long, startBookId: Long?, limit: Int = com.alexshabanov.booklib.service.dao.DEFAULT_LIMIT) = asNamedValuePage(
      namedValueDao.getAuthorById(authorId),
      getBooksByMeta(bookDao.getBooksOfAuthor(authorId, startBookId, limit)), limit)

  fun getGenrePageModel(genreId: Long, startBookId: Long?, limit: Int = com.alexshabanov.booklib.service.dao.DEFAULT_LIMIT) = asNamedValuePage(
      namedValueDao.getGenreById(genreId),
      getBooksByMeta(bookDao.getBooksOfGenre(genreId, startBookId, limit)), limit)

  fun getLanguagePageModel(languageId: Long, startBookId: Long?, limit: Int = com.alexshabanov.booklib.service.dao.DEFAULT_LIMIT) = asNamedValuePage(
      namedValueDao.getLanguageById(languageId),
      getBooksByMeta(bookDao.getBooksByLanguage(languageId, startBookId, limit)), limit)

  fun getGenres() = namedValueDao.getGenres()

  fun getLanguages() = namedValueDao.getLanguages()

  fun getAuthorNameHint(namePrefix: String? = null) = namedValueDao.getAuthorNameHint(namePrefix)

  fun getAuthorsByNamePrefix(namePrefix: String, startWithName: String? = null, limit: Int = com.alexshabanov.booklib.service.dao.DEFAULT_LIMIT) =
      namedValueDao.getAuthorsByNamePrefix(namePrefix, startWithName, limit)

  fun getBookById(bookId: Long): Book = getBooksByMeta(listOf(bookDao.getBookById(bookId))).get(0)
}
