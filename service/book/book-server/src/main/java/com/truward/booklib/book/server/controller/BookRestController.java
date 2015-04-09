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
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


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
          NAMED_VALUE_MAPPER, id));
    }
    recorder.record("getGenres");

    // fetch languages
    for (final long id : request.getLanguageIdsList()) {
      builder.addLanguages(jdbcOperations.queryForObject("SELECT id, code AS name FROM lang_code WHERE id=?",
          NAMED_VALUE_MAPPER, id));
    }
    recorder.record("getLanguages");

    return builder.build();
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.BookMetaList queryBooks(@RequestBody BookModel.BookPageQuery query) {
    final StringBuilder queryBuilder = new StringBuilder(200);
    final List<Object> args = new ArrayList<>(20);

    queryBuilder.append("SELECT bm.id, bm.title, bm.f_size, bm.add_date, bm.lang_id, bm.origin_id FROM book_meta AS bm");

    if (query.hasGenreId()) {
      queryBuilder.append(" genre_id=?");
      args.add(query.getGenreId());
    }

    if (query.hasPersonId())

    queryBuilder.append(" LIMIT ?");
    args.add(query.getLimit());

    final List<BookModel.BookMeta.Builder> bookBuilders = jdbcOperations.query(queryBuilder.toString(),
        BOOK_BUILDER_MAPPER, args.toArray(new Object[args.size()]));

    for (final BookModel.BookMeta.Builder builder : bookBuilders) {
      //builder.addPersonRelations();
    }

    return BookModel.BookMetaList.newBuilder()
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.NamedValueList getGenres() {
    return getNamedValueList("SELECT id, code AS name FROM genre ORDER BY code");
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.NamedValueList getLanguages() {
    return getNamedValueList("SELECT id, code AS name FROM lang_code ORDER BY code");
  }

  @Override
  public BookModel.NamedValueList queryPersons(@RequestBody BookModel.PersonListRequest request) {
    return null;
  }

  @Override
  public BookModel.PersonNameHints getPersonHints(@RequestParam String namePart) {
    return null;
  }

  //
  // Private
  //

  @Nonnull
  private BookModel.NamedValueList getNamedValueList(@Nonnull String sql) {
    return BookModel.NamedValueList.newBuilder()
        .addAllValues(jdbcOperations.query(sql, NAMED_VALUE_MAPPER))
        .build();
  }

  //
  // Mapper Definitions
  //

  private static final class NamedValueMapper implements RowMapper<BookModel.NamedValue> {

    @Override
    public BookModel.NamedValue mapRow(ResultSet rs, int rowNum) throws SQLException {
      return BookModel.NamedValue.newBuilder().setId(rs.getLong("id")).setName(rs.getString("name")).build();
    }
  }

  private static final class BookMetaBuilderRowMapper implements RowMapper<BookModel.BookMeta.Builder> {

    @Override
    public BookModel.BookMeta.Builder mapRow(ResultSet rs, int rowNum) throws SQLException {
      return null;
    }
  }

  //
  // Mapper Instances
  //

  private static final NamedValueMapper NAMED_VALUE_MAPPER = new NamedValueMapper();
  private static final BookMetaBuilderRowMapper BOOK_BUILDER_MAPPER = new BookMetaBuilderRowMapper();
}
