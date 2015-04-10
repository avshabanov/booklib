package com.truward.booklib.book.server.controller;

import com.truward.booklib.book.model.BookModel;
import com.truward.booklib.book.model.BookRestService;
import com.truward.booklib.book.server.util.IdConcealingUtil;
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
@RequestMapping("/rest/book")
@Transactional
public final class BookRestController implements BookRestService {
  private static final int MAX_LIMIT = 64;

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final JdbcOperations jdbcOperations;

  public BookRestController(JdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;
  }

  @Override
  public BookModel.BookPageIds savePage(@RequestBody BookModel.BookPageData request) {
    final BookModel.BookPageIds.Builder builder = BookModel.BookPageIds.newBuilder().setFetchBookDependencies(false);

    // Genres
    for (final BookModel.NamedValue val : request.getGenresList()) {
      final long genreId;
      if (val.hasId()) {
        genreId = val.getId();
        jdbcOperations.update("UPDATE genre SET code=? WHERE id=?", val.getName(), genreId);
      } else {
        genreId = jdbcOperations.queryForObject("SELECT seq_genre.nextval", Long.class);
        jdbcOperations.update("INSERT INTO genre (id, code) VALUES (?, ?)", genreId, val.getName());
      }
      builder.addGenreIds(genreId);
    }

    return builder.build();
  }

  @Override
  public BookModel.BookPageIds delete(@RequestBody BookModel.BookPageIds request) {
    // Genres
    for (final long id : request.getGenreIdsList()) {
      jdbcOperations.update("DELETE FROM book_genre WHERE genre_id=?", id);
      jdbcOperations.update("DELETE FROM genre WHERE id=?", id);
    }

    // Persons
    for (final long id : request.getPersonIdsList()) {
      jdbcOperations.update("DELETE FROM book_person WHERE person_id=?", id);
      jdbcOperations.update("DELETE FROM person WHERE id=?", id);
    }

    // Books
    for (final long id : request.getBookIdsList()) {
      jdbcOperations.update("DELETE FROM book_series WHERE book_id=?", id);
      jdbcOperations.update("DELETE FROM book_meta WHERE id=?", id);
    }

    // Languages
    for (final long id : request.getLanguageIdsList()) {
      jdbcOperations.update("DELETE FROM lang_code WHERE id=?", id);
    }

    // Origins
    for (final long id : request.getOriginIdsList()) {
      jdbcOperations.update("DELETE FROM book_origin WHERE id=?", id);
    }

    return request;
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
    final BookModel.BookSublist.Builder builder = BookModel.BookSublist.newBuilder();
    if (query.getLimit() == 0) {
      // edge condition: zero elements requested
      if (query.hasOffsetToken()) {
        builder.setOffsetToken(query.getOffsetToken());
      }
      return builder.build();
    }

    switch (query.getSortType()) {
      case ID:
        queryBooksOrderedById(builder, query);
        break;

      case TITLE:
        queryBooksOrderedByTitle(builder, query);
        break;

      default:
        throw new UnsupportedOperationException("Unknown sortType=" + query.getSortType());
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
  @Transactional(readOnly = true)
  public BookModel.NamedValueList queryPersons(@RequestBody BookModel.PersonListRequest request) {
    checkLimitBounds(request.getLimit());
    final BookModel.NamedValueList.Builder builder = BookModel.NamedValueList.newBuilder();
    if (request.getLimit() == 0) {
      // short cirquit for limit=0
      if (request.hasOffsetToken()) {
        builder.setOffsetToken(request.getOffsetToken());
      }
      return builder.build();
    }

    final StringBuilder sql = new StringBuilder(100);
    final List<Object> args = new ArrayList<>();

    sql.append("SELECT id, f_name AS name FROM person\nWHERE 1=1");

    if (request.hasOffsetToken()) {
      sql.append(" AND f_name>?");
      args.add(request.getOffsetToken());
    }

    if (request.hasStartName()) {
      sql.append(" AND f_name LIKE ?");
      args.add(request.getStartName() + '%');
    }

    sql.append("\nORDER BY f_name LIMIT ?");
    args.add(request.getLimit());

    final List<BookModel.NamedValue> values = jdbcOperations.query(sql.toString(), NAMED_VALUE_MAPPER,
        args.toArray(new Object[args.size()]));
    builder.addAllValues(values);

    if (values.size() == request.getLimit()) {
      // offset token is available if and only if we were able to get a full page
      builder.setOffsetToken(values.get(values.size() - 1).getName());
    }

    return builder.build();
  }

  @Override
  public BookModel.PersonNameHints getPersonHints(@RequestParam(value = "prefix", required = false) String prefix) {
    final String prefixParam;
    final int charCount;
    if (prefix != null) {
      prefixParam = prefix + '%';
      charCount = prefix.length() + 1;
    } else {
      prefixParam = null;
      charCount = 1;
    }

    final List<String> parts = jdbcOperations.queryForList(
        "SELECT DISTINCT SUBSTR(f_name, 0, ?) AS name_part FROM person\n" +
        "WHERE (? IS NULL OR f_name LIKE ?)\n" +
        "ORDER BY name_part", String.class, charCount, prefixParam, prefixParam);

    return BookModel.PersonNameHints.newBuilder().addAllNameParts(parts).build();
  }

  //
  // Private
  //

  private void addCommonBookQueryParameters(@Nonnull StringBuilder queryBuilder,
                                            @Nonnull List<Object> args,
                                            @Nonnull BookModel.BookPageQuery query) {
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
  }

  private void queryBooksOrderedById(@Nonnull BookModel.BookSublist.Builder builder,
                                     @Nonnull BookModel.BookPageQuery query) {
    // generate and execute a query
    final StringBuilder queryBuilder = new StringBuilder(200);
    final List<Object> args = new ArrayList<>(20);

    addCommonBookQueryParameters(queryBuilder, args, query);

    if (query.hasOffsetToken()) {
      queryBuilder.append(" AND bm.id>?");
      args.add(IdConcealingUtil.parseHexLong(query.getOffsetToken(), "offsetToken"));
    }

    queryBuilder.append(" ORDER BY bm.id");

    // [5] 'LIMIT' clause
    queryBuilder.append(" LIMIT ?");
    args.add(query.getLimit());

    // Query for ids
    final List<Long> ids = jdbcOperations.queryForList(queryBuilder.toString(),
        args.toArray(new Object[args.size()]), Long.class);

    builder.addAllBookIds(ids);
    builder.setOffsetToken(IdConcealingUtil.toString(ids.get(ids.size() - 1)));
  }

  private void queryBooksOrderedByTitle(@Nonnull BookModel.BookSublist.Builder builder,
                                        @Nonnull BookModel.BookPageQuery query) {
    // generate and execute a query
    final StringBuilder queryBuilder = new StringBuilder(200);
    final List<Object> args = new ArrayList<>(20);

    addCommonBookQueryParameters(queryBuilder, args, query);

    queryBuilder.append(" ORDER BY bm.title");

    // [5] 'OFFSET' clause
    // TODO: replace with something like offset behavior for ID sort type
    int offset = 0;
    if (query.hasOffsetToken()) {
      offset = IdConcealingUtil.parseHexInt(query.getOffsetToken(), "offsetToken");
      queryBuilder.append(" OFFSET ?");
      args.add(offset);
    }

    // [6] 'LIMIT' clause
    queryBuilder.append(" LIMIT ?");
    args.add(query.getLimit());

    // Query for ids
    final List<Long> ids = jdbcOperations.queryForList(queryBuilder.toString(),
        args.toArray(new Object[args.size()]), Long.class);

    builder.addAllBookIds(ids);

    // now get an offset token (and we know that limit is greater than zero)
    if (ids.size() == query.getLimit()) {
      final int newOffset = offset + query.getLimit();
      builder.setOffsetToken(IdConcealingUtil.toString(newOffset));
    }
  }

  private static void checkLimitBounds(int limit) {
    if (limit < 0) {
      throw new IllegalArgumentException("limit can't be less than zero");
    }
    if (limit > MAX_LIMIT) {
      throw new IllegalArgumentException("limit exceeds allowed upper bound which is " + MAX_LIMIT);
    }
  }

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
  // Helper models
  //

  private static final class IdWithIndex {
    final long id;
    final int index;

    public IdWithIndex(long id, int index) {
      this.id = id;
      this.index = index;
    }
  }

  //
  // Mapper Definitions
  //

  private static final class IdWithIndexRowMapper implements RowMapper<IdWithIndex> {

    @Override
    public IdWithIndex mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new IdWithIndex(rs.getLong("id"), rs.getInt("index"));
    }
  }

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
  private static final IdWithIndexRowMapper ID_WITH_INDEX_MAPPER = new IdWithIndexRowMapper();

  //
  // Helpers
  //

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
