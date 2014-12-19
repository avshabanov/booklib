package com.alexshabanov.booklib.service

import org.springframework.jdbc.core.JdbcOperations
import com.alexshabanov.booklib.model.BookMeta
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.util.Random
import com.alexshabanov.booklib.model.NamedValue
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.JdbcTemplate

//
// Interface
//

val DEFAULT_LIMIT = 8

trait BookDao {
  fun getBooksByTitleTerm(titleSqlMask: String, limit: Int = DEFAULT_LIMIT): List<BookMeta>

  fun getBookAuthors(bookId: Long): List<Long>

  fun getBookGenres(bookId: Long): List<Long>

  fun getRandomBooks(limit: Int = DEFAULT_LIMIT): List<BookMeta>
}

trait NamedValueDao {
  fun getAuthorsByIds(authorIds: List<Long>): List<NamedValue>

  fun getGenresByIds(genresIds: List<Long>): List<NamedValue>
}

//
// Impl
//

private val BOOK_META_ROW_MAPPER = RowMapper() {(rs: ResultSet, i: Int) ->
  BookMeta(id = rs.getLong("id"), title = rs.getString("title"), fileSize = rs.getInt("f_size"),
      lang = rs.getString("lang_name"), origin = rs.getString("origin_name"), addDate = rs.getDate("add_date"))
}

class BookDaoImpl(val db: JdbcOperations): BookDao {

  override fun getBooksByTitleTerm(titleSqlMask: String, limit: Int) = db.query(
        "SELECT bm.id, bm.title, bm.f_size, bm.add_date, bo.code AS origin_name, lc.code AS lang_name FROM book_meta AS bm\n" +
            "INNER JOIN book_origin AS bo ON bm.origin_id=bo.id\n" +
            "INNER JOIN lang_code AS lc ON bm.lang_id=lc.id\n" +
            "WHERE bm.title LIKE ? LIMIT ?",
        BOOK_META_ROW_MAPPER,
        titleSqlMask,
        limit)

  override fun getRandomBooks(limit: Int) = db.query(
        //SELECT id FROM book_meta ORDER BY RAND() LIMIT ?; <-- performs too badly even on average tables
        "SELECT bm.id, bm.title, bm.f_size, bm.add_date, bo.code AS origin_name, lc.code AS lang_name FROM book_meta AS bm\n" +
            "INNER JOIN book_origin AS bo ON bm.origin_id=bo.id\n" +
            "INNER JOIN lang_code AS lc ON bm.lang_id=lc.id\n" +
            "LIMIT ?",
        BOOK_META_ROW_MAPPER,
        limit)

  override fun getBookAuthors(bookId: Long) = db.queryForList(
      "SELECT author_id FROM book_author WHERE book_id=?", bookId.javaClass, bookId)

  override fun getBookGenres(bookId: Long) = db.queryForList(
      "SELECT genre_id FROM book_genre WHERE book_id=?", bookId.javaClass, bookId)
}

private val NAMED_VALUE_ROW_MAPPER = RowMapper() {(rs: ResultSet, i: Int) ->
  NamedValue(id = rs.getInt("id").toLong(), name = rs.getString("name"))
}

class NamedValueDaoImpl(val db: NamedParameterJdbcOperations): NamedValueDao {
  override fun getAuthorsByIds(authorIds: List<Long>) = db.query(
      "SELECT id, f_name AS name FROM author WHERE id IN (:authorIds)",
      mapOf(Pair("authorIds", authorIds)), NAMED_VALUE_ROW_MAPPER)

  override fun getGenresByIds(genresIds: List<Long>) = db.query(
      "SELECT id, code AS name FROM genre WHERE id IN (:genresIds)",
      mapOf(Pair("genresIds", genresIds)), NAMED_VALUE_ROW_MAPPER)
}