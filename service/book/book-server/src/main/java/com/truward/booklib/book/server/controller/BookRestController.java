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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    throw new UnsupportedOperationException();
  }

  @Override
  public BookModel.BookPageIds delete(@RequestBody BookModel.BookPageIds request) {
    throw new UnsupportedOperationException();
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.BookPageData getPage(@RequestBody BookModel.BookPageIds request) {
    final BookModel.BookPageData.Builder builder = BookModel.BookPageData.newBuilder();

    final SnapshotTimeRecorder recorder = new SnapshotTimeRecorder("BookRestController.getPage", log);

    // prepare IDs
    final Set<Long> genreIds = new HashSet<>(request.getGenreIdsList());
    final Set<Long> languageIds = new HashSet<>(request.getLanguageIdsList());
    final Set<Long> originIds = new HashSet<>(request.getOriginIdsList());
    final Set<Long> personIds = new HashSet<>(request.getPersonIdsList());

    // fetch basic book fields
    final Set<Long> idSet = new HashSet<>(request.getBookIdsList());
    final List<BookModel.BookMeta> books = new ArrayList<>(idSet.size());
    recorder.start();
    for (final long id : idSet) {
      final BookModel.BookMeta.Builder bookBuilder = jdbcOperations.queryForObject(
          "SELECT id, title, f_size, add_date, lang_id, origin_id FROM book_meta WHERE id=?", BOOK_BUILDER_MAPPER, id);

      // add genres
      final List<Long> bookGenreIds = jdbcOperations.queryForList("SELECT genre_id FROM book_genre WHERE book_id=?",
          Long.class, id);
      bookBuilder.addAllGenreIds(bookGenreIds);

      // add person relations
      bookBuilder.addAllPersonRelations(jdbcOperations.query("SELECT person_id, role FROM book_person WHERE book_id=?",
          PERSON_REL_MAPPER, id));

      // construct book and add to list
      final BookModel.BookMeta book = bookBuilder.build();
      books.add(book);

      // add to discoverable info
      if (request.getFetchBookDependencies()) {
        genreIds.addAll(bookGenreIds);
        languageIds.add(book.getLangId());
        originIds.add(book.getOriginId());
        for (final BookModel.PersonRelation rel : book.getPersonRelationsList()) {
          personIds.add(rel.getId());
        }
      }
    }
    builder.addAllBooks(books);
    recorder.record("getBooks");

    // fetch genres
    for (final long id : genreIds) {
      builder.addGenres(jdbcOperations.queryForObject("SELECT id, code AS name FROM genre WHERE id=?",
          NAMED_VALUE_MAPPER, id));
    }
    recorder.record("getGenres");

    // fetch languages
    for (final long id : languageIds) {
      builder.addLanguages(jdbcOperations.queryForObject("SELECT id, code AS name FROM lang_code WHERE id=?",
          NAMED_VALUE_MAPPER, id));
    }
    recorder.record("getLanguages");

    // fetch origins
    for (final long id : originIds) {
      builder.addOrigins(jdbcOperations.queryForObject("SELECT id, code AS name FROM book_origin WHERE id=?",
          NAMED_VALUE_MAPPER, id));
    }
    recorder.record("getOrigins");

    // fetch persons
    for (final long id : personIds) {
      builder.addOrigins(jdbcOperations.queryForObject("SELECT id, f_name AS name FROM person WHERE id=?",
          NAMED_VALUE_MAPPER, id));
    }
    recorder.record("getPersons");

    return builder.build();
  }

  @Override
  @Transactional(readOnly = true)
  public BookModel.BookSublist queryBooks(@RequestBody BookModel.BookPageQuery query) {
    if (query.getLimit() == 0) {
      // edge condition: zero elements requested
      final BookModel.BookSublist.Builder builder = BookModel.BookSublist.newBuilder();
      if (query.hasOffsetToken()) {
        builder.setOffsetToken(query.getOffsetToken());
      }
      return builder.build();
    }

    // generate and execute a query
    final StringBuilder queryBuilder = new StringBuilder(200);
    final List<Object> args = new ArrayList<>(20);

    queryBuilder.append("SELECT bm.id FROM book_meta AS bm\n");

    // [1] Joined tables
    if (query.hasGenreId()) {
      queryBuilder.append("INNER JOIN book_genre AS bg\n");
    }

    if (query.hasPersonId()) {
      queryBuilder.append("INNER JOIN book_person AS bp\n");
    }

    // [2] 'WHERE' clause
    queryBuilder.append("WHERE 1=1"); // 1=1 is to avoid complicated 'AND'/'WHERE' logic

    if (query.hasGenreId()) {
      queryBuilder.append(" AND bg.genre_id=?");
      args.add(query.getGenreId());
    }

    if (query.hasPersonId()) {
      queryBuilder.append(" AND bp.person_id=?");
      args.add(query.getPersonId());
    }

    if (query.hasOriginId()) {
      queryBuilder.append(" AND bm.origin_id=?");
      args.add(query.getOriginId());
    }

    if (query.hasOffsetToken() && query.getSortType() == BookModel.BookPageQuery.SortType.ID) {
      // parse startId
      final Long startId;
      try {
        startId = Long.parseLong(query.getOffsetToken(), 16);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid offsetToken value", e);
      }

      queryBuilder.append(" AND bm.id>?");
      args.add(startId);
    }

    // [4] 'ORDER BY' clause
    switch (query.getSortType()) {
      case ID:
        queryBuilder.append(" ORDER BY bm.id");
        break;
      case TITLE:
        queryBuilder.append(" ORDER BY bm.title");
        break;
      default:
        throw new UnsupportedOperationException("Unsupported sortType=" + query.getSortType());
    }

    // [5] 'OFFSET' clause
    // TODO: replace with something like offset behavior for ID sort type
    if (query.hasOffsetToken() && query.getSortType() == BookModel.BookPageQuery.SortType.TITLE) {
      final Integer offset;
      try {
        offset = Integer.parseInt(query.getOffsetToken(), 16);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Invalid offsetToken value", e);
      }
      queryBuilder.append(" OFFSET ?");
      args.add(offset);
    }

    // [6] 'LIMIT' clause
    queryBuilder.append(" LIMIT ?");
    args.add(query.getLimit());

    // Query for ids
    final List<Long> ids = jdbcOperations.queryForList(queryBuilder.toString(),
        args.toArray(new Object[args.size()]), Long.class);

    final BookModel.BookSublist.Builder builder = BookModel.BookSublist.newBuilder();
    builder.addAllBookIds(ids);

    // now get an offset token (and we know that limit is greater than zero)
    if (ids.size() == query.getLimit()) {
      switch (query.getSortType()) {
        case ID:
          builder.setOffsetToken(Long.toString(ids.get(ids.size() - 1), 16));
          break;

        case TITLE:
          // newOffsetToken = toInt(prevOffsetToken) + limit
          builder.setOffsetToken(Integer.toString(Integer.parseInt(query.getOffsetToken(), 16) + query.getLimit(), 16));
          break;
      }
    }

    return builder.build();
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
    throw new UnsupportedOperationException();
  }

  @Override
  public BookModel.PersonNameHints getPersonHints(@RequestParam String namePart) {
    throw new UnsupportedOperationException();
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

  @Nonnull
  private static BookModel.PersonRelation.Type getPersonRelationTypeFromCode(int code) {
    switch (code) {
      case 1:
        return BookModel.PersonRelation.Type.AUTHOR;
      case 2:
        return BookModel.PersonRelation.Type.ILLUSTRATOR;
      default:
        return BookModel.PersonRelation.Type.UNKNOWN;
    }
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
      final BookModel.BookMeta.Builder builder = BookModel.BookMeta.newBuilder()
          .setId(rs.getLong("id")).setFileSize(rs.getInt("f_size")).setLangId(rs.getLong("lang_id"))
          .setOriginId(rs.getLong("origin_id")).setTitle(rs.getString("title"));

      final Timestamp addDate = rs.getTimestamp("add_date");
      if (addDate != null) {
        builder.setAddDate(addDate.getTime());
      }

      return builder;
    }
  }

  private static final class PersonRelationRowMapper implements RowMapper<BookModel.PersonRelation> {

    @Override
    public BookModel.PersonRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
      return BookModel.PersonRelation.newBuilder()
          .setId(rs.getLong("person_id"))
          .setRelation(getPersonRelationTypeFromCode(rs.getInt("role")))
          .build();
    }
  }

  //
  // Mapper Instances
  //

  private static final NamedValueMapper NAMED_VALUE_MAPPER = new NamedValueMapper();
  private static final BookMetaBuilderRowMapper BOOK_BUILDER_MAPPER = new BookMetaBuilderRowMapper();
  private static final PersonRelationRowMapper PERSON_REL_MAPPER = new PersonRelationRowMapper();

  // TODO: move to brikar
  private static final class SnapshotTimeRecorder {
    long startTime;
    long endTime;
    final String metricRootName;
    final Logger log;

    public SnapshotTimeRecorder(@Nonnull String metricRootName, @Nonnull Logger log) {
      this.metricRootName = metricRootName;
      this.log = log;
    }

    void start() {
      startTime = System.currentTimeMillis();
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
}
