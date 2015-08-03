package com.alexshabanov.booklib.test

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
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import com.alexshabanov.booklib.model.NamedValue
import com.alexshabanov.booklib.service.BookService
import com.alexshabanov.booklib.model.BookMeta
import java.util.Calendar
import java.text.SimpleDateFormat
import com.truward.time.UtcTime
import com.alexshabanov.booklib.service.dao.NamedValueDao
import com.alexshabanov.booklib.service.dao.BookDao
import com.alexshabanov.booklib.service.dao.DEFAULT_LIMIT
import com.alexshabanov.booklib.service.UserProfileService
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

private fun parseUtcTime(str: String): UtcTime {
  val format = SimpleDateFormat("yyyy-MM-dd");
  format.setCalendar(UtcTime.newUtcCalendar())
  return UtcTime.valueOf(format.parse(str).getTime())
}

RunWith(SpringJUnit4ClassRunner::class)
ContextConfiguration(locations = arrayOf("/spring/DaoTest-context.xml"))
Transactional(value = "userTxManager") class BookDaoTest: Serializable {
  Resource(name = "dao.book.bookDao") var bookDao: BookDao = mock(javaClass()) // HERE: mocks to silence compiler
  Resource(name = "dao.book.namedValueDao") var namedValueDao: NamedValueDao = mock(javaClass())

  Test fun shouldGetBookById() {
    assertEquals(BookMeta(id = 1, title = "Far Rainbow", fileSize = 255365, addDate = parseUtcTime("2007-10-23"),
        lang = NamedValue(2, "ru"), origin = "RussianBooks"), bookDao.getBookById(1))
  }

  Test fun shouldGetBookByTitle() {
    val results1 = bookDao.getBooksByTitleTerm(titleSqlMask = "%the%", limit = 5)
    assertFalse(results1.isEmpty(), "Results should not be empty")

    val results2 = bookDao.getBooksByTitleTerm(titleSqlMask = "Far Rainbow", limit = 5)
    assertEquals(listOf(bookDao.getBookById(1)), results2)
  }

  Test fun shouldGetRandomBooks() {
    assertEquals(DEFAULT_LIMIT, bookDao.getRandomBooks().size(), "Should get more than one random books")
  }

  Test fun shouldGetAuthors() {
    assertEquals(mapOf(Pair(2L, listOf(NamedValue(id = 7, name = "Victor Pelevin")))), namedValueDao.getAuthorsOfBooks(listOf(2)))
  }

  Test fun shouldGetAuthorById() {
    assertEquals(NamedValue(7L, "Victor Pelevin"), namedValueDao.getAuthorById(7))
  }

  Test fun shouldGetGenreById() {
    assertEquals(NamedValue(3L, "essay"), namedValueDao.getGenreById(3))
  }

  Test fun shouldGetGenres() {
    assertEquals(mapOf(Pair(2L, listOf(NamedValue(id = 3, name = "essay")))), namedValueDao.getGenresOfBooks(listOf(2)))
  }

  Test fun shouldGetFirstBooksOfAuthor() {
    val books = bookDao.getBooksOfAuthor(authorId = 1, limit = 2)
    assertEquals(listOf(bookDao.getBookById(17), bookDao.getBookById(18)), books)
  }

  Test fun shouldGetNextBooksOfAuthor() {
    val books = bookDao.getBooksOfAuthor(authorId = 1, nextBookId = 18, limit = 2)
    assertEquals(listOf(bookDao.getBookById(19)), books)
  }

  Test fun shouldGetFirstBooksOfGenre() {
    val books = bookDao.getBooksOfGenre(genreId = 4, limit = 2)
    assertEquals(listOf(bookDao.getBookById(1), bookDao.getBookById(4)), books)
  }

  Test fun shouldGetAuthorsWithBooks() {
    var userService: UserProfileService = mock(javaClass())
    val bookService = BookService(bookDao = bookDao, namedValueDao = namedValueDao, userService = userService)
    val books = bookService.getBooksByMeta(1L, bookDao.getBooksByTitleTerm(titleSqlMask = "Far Rainbow", limit = 5))

    assertEquals(1, books.size())
    val book = books.get(0)

    assertEquals(setOf(NamedValue(5, "Arkady Strugatsky"), NamedValue(6, "Boris Strugatsky")), book.authors.toSet())
    assertEquals(setOf(NamedValue(1, "sci_fi"), NamedValue(4, "novel")), book.genres.toSet())
  }

  Test fun shouldGetAuthorNamesHint() {
    assertEquals(listOf("A", "B", "E", "J", "S", "V"), namedValueDao.getAuthorNameHint())
  }

  Test fun shouldGetAuthorNamesHintByPrefix() {
    assertEquals(listOf("Ja", "Jo"), namedValueDao.getAuthorNameHint("J"))
  }

  Test fun shouldGetAuthorsByNamePrefix() {
    assertEquals(listOf(NamedValue(1, "Jack London"), NamedValue(8, "Jason Ciaramella"), NamedValue(4, "Joe Hill")),
        namedValueDao.getAuthorsByNamePrefix("J"))

    assertEquals(listOf(NamedValue(8, "Jason Ciaramella"), NamedValue(4, "Joe Hill")),
        namedValueDao.getAuthorsByNamePrefix("J", "Jack London"))

    assertEquals(listOf(NamedValue(8, "Jason Ciaramella")), namedValueDao.getAuthorsByNamePrefix("J", "Jack London", 1))
  }

  Test fun shouldGetLanguageById() {
    assertEquals(NamedValue(1, "en"), namedValueDao.getLanguageById(1))
  }

  Test fun shouldGetLanguages() {
    assertEquals(listOf(NamedValue(1, "en"), NamedValue(2, "ru")), namedValueDao.getLanguages())
  }

  Test fun shouldGetFirstRuBooksByLanguage() {
    val books = bookDao.getBooksByLanguage(languageId = 2, limit = 2)
    assertEquals(listOf(bookDao.getBookById(1), bookDao.getBookById(2)), books)
  }

  Test fun shouldGetFirstEnBooksByLanguage() {
    val books = bookDao.getBooksByLanguage(languageId = 1, limit = 2)
    assertEquals(listOf(bookDao.getBookById(3), bookDao.getBookById(5)), books)
  }

  Test fun shouldGetNextEnBooksByLanguage() {
    val books = bookDao.getBooksByLanguage(languageId = 1, nextBookId = 5, limit = 2)
    assertEquals(listOf(bookDao.getBookById(6), bookDao.getBookById(7)), books)
  }
}

