package com.alexshabanov.booklib.service

import com.alexshabanov.booklib.model.BookMeta
import com.alexshabanov.booklib.model.Book
import java.util.ArrayList
import java.util.LinkedHashSet
import com.alexshabanov.booklib.model.NamedValue

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
}
