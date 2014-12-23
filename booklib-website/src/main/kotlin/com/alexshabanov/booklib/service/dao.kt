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
import java.util.HashMap
import java.util.ArrayList
import com.alexshabanov.booklib.model.asUtcTime

//
// Interface
//

val DEFAULT_LIMIT = 8

trait BookDao {

  fun getBookById(bookId: Long): BookMeta

  fun getBooksByTitleTerm(titleSqlMask: String, limit: Int = DEFAULT_LIMIT): List<BookMeta>

  fun getBookAuthors(bookId: Long): List<Long>

  fun getBookGenres(bookId: Long): List<Long>

  fun getRandomBooks(limit: Int = DEFAULT_LIMIT): List<BookMeta>

  fun getBooksOfAuthor(authorId: Long, nextBookId: Long? = null, limit: Int = DEFAULT_LIMIT): List<BookMeta>

  fun getBooksOfGenre(genreId: Long, nextBookId: Long? = null, limit: Int = DEFAULT_LIMIT): List<BookMeta>
}

trait NamedValueDao {

  fun getAuthorsOfBooks(bookIds: List<Long>): Map<Long, List<NamedValue>>

  fun getGenresOfBooks(bookIds: List<Long>): Map<Long, List<NamedValue>>

  fun getAuthorById(id: Long): NamedValue

  fun getGenreById(id: Long): NamedValue

  fun getGenres(): List<NamedValue>

  fun getAuthorNameHint(namePrefix: String? = null): List<String>

  fun getAuthorsByNamePrefix(namePrefix: String, startWithName: String? = null, limit: Int = DEFAULT_LIMIT): List<NamedValue>
}

//
// Impl
//

private val BOOK_META_ROW_MAPPER = RowMapper() {(rs: ResultSet, i: Int) ->
  BookMeta(id = rs.getLong("id"), title = rs.getString("title"), fileSize = rs.getInt("f_size"),
      lang = rs.getString("lang_name"), origin = rs.getString("origin_name"), addDate = asUtcTime(rs, "add_date"))
}

private val BOOK_QUERY_SQL_HEAD =
    "SELECT bm.id, bm.title, bm.f_size, bm.add_date, bo.code AS origin_name, lc.code AS lang_name FROM book_meta AS bm\n" +
    "INNER JOIN book_origin AS bo ON bm.origin_id=bo.id\n" +
    "INNER JOIN lang_code AS lc ON bm.lang_id=lc.id\n"

class BookDaoImpl(val db: JdbcOperations): BookDao {

  override fun getBookById(bookId: Long) = db.queryForObject(
        BOOK_QUERY_SQL_HEAD + "WHERE bm.id=?",
        BOOK_META_ROW_MAPPER,
        bookId)

  override fun getBooksByTitleTerm(titleSqlMask: String, limit: Int) = db.query(
        BOOK_QUERY_SQL_HEAD + "WHERE bm.title LIKE ? LIMIT ?",
        BOOK_META_ROW_MAPPER,
        titleSqlMask,
        limit)

  override fun getRandomBooks(limit: Int) = db.query(
        //SELECT id FROM book_meta ORDER BY RAND() LIMIT ?; <-- performs too badly even on average tables
        BOOK_QUERY_SQL_HEAD +
            "WHERE bm.id >= (RAND() * (SELECT MAX(id) FROM book_meta) - ?)\n" +
            "ORDER BY bm.id\n" +
            "LIMIT ?",
        BOOK_META_ROW_MAPPER,
        limit,
        limit)

  override fun getBookAuthors(bookId: Long) = db.queryForList(
      "SELECT author_id FROM book_author WHERE book_id=?", bookId.javaClass, bookId)

  override fun getBookGenres(bookId: Long) = db.queryForList(
      "SELECT genre_id FROM book_genre WHERE book_id=?", bookId.javaClass, bookId)

  override fun getBooksOfAuthor(authorId: Long, nextBookId: Long?, limit: Int) = db.query(
      BOOK_QUERY_SQL_HEAD +
          "INNER JOIN book_author AS ba ON bm.id=ba.book_id\n" +
          "WHERE ba.author_id=? AND ((? IS NULL) OR (bm.id > ?))\n" +
          "ORDER BY bm.id\n" +
          "LIMIT ?",
      BOOK_META_ROW_MAPPER, authorId, nextBookId, nextBookId, limit)

  override fun getBooksOfGenre(genreId: Long, nextBookId: Long?, limit: Int) = db.query(
      BOOK_QUERY_SQL_HEAD +
          "INNER JOIN book_genre AS bg ON bm.id=bg.book_id\n" +
          "WHERE bg.genre_id=? AND ((? IS NULL) OR (bm.id > ?))\n" +
          "ORDER BY bm.id\n" +
          "LIMIT ?",
      BOOK_META_ROW_MAPPER, genreId, nextBookId, nextBookId, limit)
}

private val NAMED_VALUE_ROW_MAPPER = RowMapper() {(rs: ResultSet, i: Int) ->
  NamedValue(id = rs.getInt("id").toLong(), name = rs.getString("name"))
}

private val BOOK_ID_WITH_NAMED_VALUE_ROW_MAPPER = RowMapper() {(rs: ResultSet, i: Int) ->
  Pair(rs.getLong("book_id"), NamedValue(id = rs.getInt("id").toLong(), name = rs.getString("name")))
}

class NamedValueDaoImpl(val db: NamedParameterJdbcOperations): NamedValueDao {
  override fun getAuthorsOfBooks(bookIds: List<Long>) = getBookToNamedValuesMap(bookIds,
      "SELECT ba.book_id, a.id, a.f_name AS name FROM author AS a\n" +
      "INNER JOIN book_author AS ba ON a.id=ba.author_id WHERE ba.book_id IN (:bookIds)")

  override fun getGenresOfBooks(bookIds: List<Long>) = getBookToNamedValuesMap(bookIds,
      "SELECT bg.book_id, g.id, g.code AS name FROM genre AS g\n" +
          "INNER JOIN book_genre AS bg ON g.id=bg.genre_id WHERE bg.book_id IN (:bookIds)")

  override fun getAuthorById(id: Long) = db.queryForObject(
      "SELECT id, f_name AS name FROM author WHERE id=:id", mapOf(Pair("id", id)), NAMED_VALUE_ROW_MAPPER)

  override fun getGenreById(id: Long) = db.queryForObject(
      "SELECT id, code AS name FROM genre WHERE id=:id", mapOf(Pair("id", id)), NAMED_VALUE_ROW_MAPPER)

  override fun getGenres() = db.query("SELECT id, code AS name FROM genre ORDER BY code", NAMED_VALUE_ROW_MAPPER)

  override fun getAuthorNameHint(namePrefix: String?) = db.queryForList(
      "SELECT DISTINCT SUBSTR(f_name, 0, :char_count) AS name_part FROM author\n" +
          "WHERE (:name_prefix IS NULL OR f_name LIKE :name_prefix)\n" +
          "ORDER BY name_part",
      mapOf(Pair("name_prefix", if (namePrefix != null) namePrefix + "%" else null),
          Pair("char_count", if (namePrefix != null) namePrefix.length() + 1 else 1)),
      javaClass<String>())

  override fun getAuthorsByNamePrefix(namePrefix: String, startWithName: String?, limit: Int) = db.query(
      "SELECT id, f_name AS name FROM author\n" +
          "WHERE f_name LIKE :name_prefix AND (:start_name IS NULL OR f_name > :start_name) LIMIT :limit",
      mapOf(Pair("name_prefix", namePrefix + "%"), Pair("start_name", startWithName), Pair("limit", limit)),
      NAMED_VALUE_ROW_MAPPER)

  //
  // Private
  //

  private fun getBookToNamedValuesMap(bookIds: List<Long>, sqlQuery: String): Map<Long, List<NamedValue>> {
    val pairs = db.query(sqlQuery,
        mapOf(Pair("bookIds", bookIds)), BOOK_ID_WITH_NAMED_VALUE_ROW_MAPPER)

    val result = HashMap<Long, MutableList<NamedValue>>(pairs.size())

    for (pair in pairs) {
      var list = result.get(pair.first)
      if (list == null) {
        list = ArrayList<NamedValue>()
        result.put(pair.first, list)
      }

      list.add(pair.second)
    }

    return result
  }
}
