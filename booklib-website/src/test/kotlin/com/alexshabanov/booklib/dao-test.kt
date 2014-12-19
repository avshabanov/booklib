package com.alexshabanov.booklib

import org.junit.Test
import org.springframework.test.context.ContextConfiguration
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.jdbc.core.JdbcOperations
import javax.annotation.Resource
import org.springframework.jdbc.core.RowMapper
import java.math.BigDecimal
import java.sql.ResultSet
import java.util.Date
import org.junit.Ignore
import org.mockito.Mockito.mock
import com.alexshabanov.booklib.service.BookDao
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import com.alexshabanov.booklib.service.DEFAULT_LIMIT
import com.alexshabanov.booklib.service.NamedValueDao
import com.alexshabanov.booklib.model.NamedValue
import com.alexshabanov.booklib.service.BookService


RunWith(javaClass<SpringJUnit4ClassRunner>())
ContextConfiguration(locations = array("/spring/DaoTest-context.xml"))
class DaoTest {
  Resource(name = "dao.bookDao") var bookDao: BookDao = mock(javaClass<BookDao>())
  Resource(name = "dao.namedValueDao") var namedValueDao: NamedValueDao = mock(javaClass<NamedValueDao>())

  Test fun shouldGetBookByTitle() {
    val results1 = bookDao.getBooksByTitleTerm(titleSqlMask = "%the%", limit = 5)
    assertFalse(results1.isEmpty(), "Results should not be empty")

    val results2 = bookDao.getBooksByTitleTerm(titleSqlMask = "Far Rainbow", limit = 5)
    assertEquals(1, results2.size(), "Results should not be empty")
  }

  Test fun shouldGetRandomBooks() {
    assertEquals(DEFAULT_LIMIT, bookDao.getRandomBooks().size(), "Should get more than one random books")
  }

  Test fun shouldGetAuthors() {
    assertEquals(listOf(NamedValue(id = 1, name = "Jack London")), namedValueDao.getAuthorsByIds(listOf(1)))
  }

  Test fun shouldGetGenres() {
    assertEquals(listOf(NamedValue(id = 1, name = "sci_fi")), namedValueDao.getGenresByIds(listOf(1)))
  }

  Test fun shouldGetAuthorsWithBooks() {
    val bookService = BookService(bookDao = bookDao, namedValueDao = namedValueDao)
    val books = bookService.getBooksByMeta(bookDao.getBooksByTitleTerm(titleSqlMask = "Far Rainbow", limit = 5))

    assertEquals(1, books.size())
    val book = books.get(0)

    assertEquals(setOf(NamedValue(5, "Arkady Strugatsky"), NamedValue(6, "Boris Strugatsky")), book.authors.toSet())
    assertEquals(setOf(NamedValue(1, "sci_fi"), NamedValue(4, "novel")), book.genres.toSet())
  }
}