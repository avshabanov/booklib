package com.alexshabanov.booklib.service

import com.alexshabanov.booklib.model.BookMeta
import com.alexshabanov.booklib.model.Book
import java.util.ArrayList
import java.util.LinkedHashSet
import com.alexshabanov.booklib.model.NamedValue

class BookService(val bookDao: BookDao, val namedValueDao: NamedValueDao) {
  fun getRandomBooks() = getBooksByMeta(bookDao.getRandomBooks())

  fun getBooksByMeta(bookMetaList: List<BookMeta>): List<Book> {
    val authorsFn = namedValuesFn(bookMetaList, { namedValueDao.getAuthorsByIds(it) }, { bookDao.getBookAuthors(it) })
    val genresFn = namedValuesFn(bookMetaList, { namedValueDao.getGenresByIds(it) }, { bookDao.getBookGenres(it) })

    return bookMetaList.map {
      val bookId = it.id
      if (bookId == null) {
        throw IllegalStateException("bookId can't be null. bookMetaList=${bookMetaList}")
      }
      Book(meta = it, authors = authorsFn(bookId), genres = genresFn(bookId))
    }
  }

  private fun namedValuesFn(bookMetaList: List<BookMeta>,
                               getNamedValuesByIdsFn: (List<Long>) -> List<NamedValue>,
                               getBookNamedValuesFn: (Long) -> List<Long>): (bookId: Long) -> List<NamedValue> {
    val bookIdToNamedValueIdsMap = linkedMapOf(*bookMetaList.map {
      val id = it.id
      if (id == null) {
        throw IllegalStateException("id can't be null. meta=${it}")
      }
      Pair(id, getBookNamedValuesFn(id))
    }.copyToArray())
    val namedValueIds = LinkedHashSet<Long>()
    bookIdToNamedValueIdsMap.values().forEach { namedValueIds.addAll(it) }

    // get named values in one turn - this is the purpose of this function
    val namedValues = getNamedValuesByIdsFn(namedValueIds.toArrayList())

    return {(bookId) ->
      bookIdToNamedValueIdsMap.get(bookId).map {
        namedValues.get(namedValueIds.indexOf(it))
      }
    }
  }
}
