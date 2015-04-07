package com.truward.booklib.book.server.controller;

import com.truward.booklib.book.model.BookModel;
import com.truward.booklib.book.model.BookRestService;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/todo")
@Transactional
public final class BookRestController implements BookRestService {
  private final JdbcOperations jdbcOperations;

  public BookRestController(JdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  @Override
  public BookModel.BookPageIds savePage(@RequestBody BookModel.BookPageData request) {
    return null;
  }

  @Override
  public BookModel.BookPageIds delete(@RequestBody BookModel.BookPageIds request) {
    return null;
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.BookPageData getPage(@RequestBody BookModel.BookPageIds request) {
    return null;
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.BookPageData queryBookPage(@RequestBody BookModel.BookPageQuery query) {
    return null;
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.GenreList getGenres() {
    return BookModel.GenreList.newBuilder()
        .addAllGenres(jdbcOperations.query("SELECT id, code AS name FROM genre ORDER BY code", new GenreRowMapper()))
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.LanguageList getLanguages() {
    return null;
  }

  //
  // Private
  //

  private static final class GenreRowMapper implements RowMapper<BookModel.Genre> {

    @Override
    public BookModel.Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
      return BookModel.Genre.newBuilder()
          .setValue(BookModel.NamedValue.newBuilder()
              .setId(rs.getLong("id"))
              .setName(rs.getString("code"))
              .build())
          .build();
    }
  }
}
