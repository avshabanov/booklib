package com.truward.booklib.book.server.controller;

import com.truward.booklib.book.model.BookModel;
import com.truward.booklib.book.model.BookRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * @author Alexander Shabanov
 */
@Controller
@RequestMapping("/rest/todo")
@Transactional
public final class BookRestController implements BookRestService {
  private final Logger log = LoggerFactory.getLogger(getClass());
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

  // TODO: move to brikar
  private static final class SnapshotTimeRecorder {
    long startTime = System.currentTimeMillis();
    long endTime;
    final String metricRootName;
    final Logger log;

    public SnapshotTimeRecorder(@Nonnull String metricRootName, @Nonnull Logger log) {
      this.metricRootName = metricRootName;
      this.log = log;
    }

    void record(@Nonnull String metricChildName) {
      if (!log.isInfoEnabled()) {
        return;
      }

      endTime = System.currentTimeMillis();
      log.info('@' + metricRootName + '.' + metricChildName + ' ' + "timeDelta=" + (endTime - startTime));
      startTime = endTime;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.BookPageData getPage(@RequestBody BookModel.BookPageIds request) {
    final BookModel.BookPageData.Builder builder = BookModel.BookPageData.newBuilder();

    final SnapshotTimeRecorder recorder = new SnapshotTimeRecorder("BookRestController.getPage", log);

    // fetch genres
    for (final long id : request.getGenreIdsList()) {
      builder.addGenres(jdbcOperations.queryForObject("SELECT id, code AS name FROM genre WHERE id=?",
          GENRE_MAPPER, id));
    }
    recorder.record("getGenres");

    // fetch languages
    for (final long id : request.getLanguageIdsList()) {
      builder.addLanguages(jdbcOperations.queryForObject("SELECT id, code AS name FROM lang_code WHERE id=?",
          LANG_MAPPER, id));
    }
    recorder.record("getLanguages");

    return builder.build();
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
        .addAllGenres(jdbcOperations.query("SELECT id, code AS name FROM genre ORDER BY code", GENRE_MAPPER))
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.LanguageList getLanguages() {
    return BookModel.LanguageList.newBuilder()
        .addAllLanguages(jdbcOperations.query("SELECT id, code AS name FROM lang_code ORDER BY code", LANG_MAPPER))
        .build();
  }

  //
  // Private
  //

  private static BookModel.NamedValue getNamedValue(ResultSet rs) throws SQLException {
    return BookModel.NamedValue.newBuilder().setId(rs.getLong("id")).setName(rs.getString("name")).build();
  }

  private static final class GenreRowMapper implements RowMapper<BookModel.Genre> {

    @Override
    public BookModel.Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
      return BookModel.Genre.newBuilder().setValue(getNamedValue(rs)).build();
    }
  }

  private static final class LanguageRowMapper implements RowMapper<BookModel.Language> {

    @Override
    public BookModel.Language mapRow(ResultSet rs, int rowNum) throws SQLException {
      return BookModel.Language.newBuilder().setValue(getNamedValue(rs)).build();
    }
  }

  private static final GenreRowMapper GENRE_MAPPER = new GenreRowMapper();

  private static final LanguageRowMapper LANG_MAPPER = new LanguageRowMapper();
}
