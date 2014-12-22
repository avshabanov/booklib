package com.alexshabanov.booklib.service

import com.alexshabanov.booklib.model.BookMeta
import java.util.ArrayList
import java.util.LinkedHashSet
import com.alexshabanov.booklib.model.NamedValue

//
// Presentational model
//

data class Book(val meta: BookMeta, val authors: List<NamedValue>, val genres: List<NamedValue>)

data class NamedValuePage(val namedValue: NamedValue, val books: List<Book>, val nextBookId: Long?)

//
// Service Implementation
//

class BookService(val bookDao: BookDao, val namedValueDao: NamedValueDao) {
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

  fun getAuthorPageModel(authorId: Long, nextBookId: Long?): NamedValuePage {
    val author = namedValueDao.getAuthorById(authorId)
    val limit = DEFAULT_LIMIT
    val books = getBooksByMeta(bookDao.getBooksOfAuthor(authorId, nextBookId, limit))
    val newNextBookId = (if (books.size() < limit) null else books.last().meta.id)

    return NamedValuePage(namedValue = author, books = books, nextBookId = newNextBookId)
  }

  fun getGenrePageModel(genreId: Long, nextBookId: Long?): NamedValuePage {
    val author = namedValueDao.getGenreById(genreId)
    val limit = DEFAULT_LIMIT
    val books = getBooksByMeta(bookDao.getBooksOfGenre(genreId, nextBookId, limit))
    val newNextBookId = (if (books.size() < limit) null else books.last().meta.id)

    return NamedValuePage(namedValue = author, books = books, nextBookId = newNextBookId)
  }
}
