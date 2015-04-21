package com.truward.booklib.extid.server.service;

import com.truward.booklib.extid.model.ExtId;
import com.truward.booklib.extid.model.ExtidRestService;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Alexander Shabanov
 */
@Transactional
public final class ExtidService implements ExtidRestService {
  private final JdbcOperations jdbc;

  public ExtidService(@Nonnull JdbcOperations jdbc) {
    Assert.notNull(jdbc, "jdbc operations parameter can't be null");
    this.jdbc = jdbc;
  }

  @Override
  public ExtId.GetTypesResponse getTypes() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ExtId.IdPage queryByInternalIds(@RequestBody ExtId.QueryByInternalIds request) {
    final StringBuilder sql = new StringBuilder(250);
    final List<Object> args = new ArrayList<>();

    sql.append("SELECT int_id, int_type_id, ext_group_id, ext_id FROM ext_id\nWHERE");

    // int ids
    sql.append(" int_id IN (");
    for (int i = 0; i < request.getIntIdsCount(); ++i) {
      sql.append(i > 0 ? ", ?" : "?");
      args.add(request.getIntIds(i));
    }
    sql.append(')');

    // types
    sql.append(" AND int_type_id IN (");
    for (int i = 0; i < request.getTypeIdsCount(); ++i) {
      sql.append(i > 0 ? ", ?" : "?");
      args.add(request.getTypeIds(i));
    }
    sql.append(')');

    // groups
    if (!request.getIncludeAllGroupIds()) {
      // take into an account group IDs
      sql.append(" AND ext_group_id IN (");
      for (int i = 0; i < request.getGroupIdsCount(); ++i) {
        sql.append(i > 0 ? ", ?" : "?");
        args.add(request.getGroupIds(i));
      }
      sql.append(')');
    }

    sql.append("\nORDER BY int_id"); // order by internal IDs to have pre-determined order

    final List<ExtId.Id> ids = jdbc.query(sql.toString(), EXT_ID_MAPPER, args.toArray(new Object[args.size()]));

    // fetch group IDs
    final Set<Integer> groupIds = new HashSet<>();
    for (final ExtId.Id id : ids) {
      groupIds.add(id.getGroupId());
    }

    final ExtId.IdPage.Builder builder = ExtId.IdPage.newBuilder().addAllIds(ids);

    // Then fetch real groups
    sql.setLength(0);
    args.clear();

    sql.append("SELECT id, name FROM ext_group WHERE id IN (");
    for (final Integer id : groupIds) {
      sql.append(sql.charAt(sql.length() - 1) == '?' ? ", ?" : "?");
      args.add(id);
    }
    sql.append(") ORDER BY id");
    builder.addAllGroups(jdbc.query(sql.toString(), GROUP_ROW_MAPPER, args.toArray(new Object[args.size()])));

    return builder.build();
  }

  @Override
  public void saveIds(@RequestBody ExtId.SaveIdRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteByIntId(@RequestBody ExtId.DeleteByIntIdRequest request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void deleteByType(@RequestBody ExtId.DeleteByTypeRequest request) {
    throw new UnsupportedOperationException();
  }

  //
  // Private
  //

  private static final class GroupRowMapper implements RowMapper<ExtId.Group> {

    @Override
    public ExtId.Group mapRow(ResultSet rs, int i) throws SQLException {
      return ExtId.Group.newBuilder().setId(rs.getInt("id")).setCode(rs.getString("name")).build();
    }
  }

  private static final class ExtIdRowMapper implements RowMapper<ExtId.Id> {

    @Override
    public ExtId.Id mapRow(ResultSet rs, int i) throws SQLException {
      return ExtId.Id.newBuilder()
          .setIntId(rs.getLong("int_id"))
          .setTypeId(rs.getInt("int_type_id"))
          .setExtId(rs.getString("ext_id"))
          .setGroupId(rs.getInt("ext_group_id"))
          .build();
    }
  }

  private static final ExtIdRowMapper EXT_ID_MAPPER = new ExtIdRowMapper();
  private static final GroupRowMapper GROUP_ROW_MAPPER = new GroupRowMapper();

  private void save() {
//    // External ID Types
//    for (final BookModel.NamedValue val : request.getExternalBookTypesList()) {
//      final long extTypeId;
//      if (val.hasId()) {
//        extTypeId = val.getId();
//        jdbcOperations.update("UPDATE external_id_type SET code=? WHERE id=?", val.getName(), extTypeId);
//      } else {
//        extTypeId = jdbcOperations.queryForObject("SELECT seq_ext_id_type.nextval", Long.class);
//        jdbcOperations.update("INSERT INTO external_id_type (id, code) VALUES (?, ?)", extTypeId, val.getName());
//      }
//      builder.addExternalBookTypeIds(extTypeId);
//    }


//
//    for (final BookModel.ExternalBookId externalId : val.getExternalIdsList()) {
//      jdbcOperations.update("INSERT INTO book_external_id (book_id, external_id, external_id_type) VALUES (?, ?, ?)",
//          bookId, externalId.getId(), externalId.getTypeId());
//    }
//
//    // External IDs
//    for (final long id : request.getExternalBookTypeIdsList()) {
//      jdbcOperations.update("DELETE FROM book_external_id WHERE external_id_type=?", id);
//      jdbcOperations.update("DELETE FROM external_id_type WHERE id=?", id);
//    }
//
//    for (final BookModel.ExternalBookId externalBookId : book.getExternalIdsList()) {
//      externalIdTypeIds.add(externalBookId.getTypeId());
//    }
//
//    // fetch external id types
//    for (final long id : externalIdTypeIds) {
//      builder.addExternalBookTypes(jdbcOperations.queryForObject(
//          "SELECT id, code AS name FROM external_id_type WHERE id=?", NAMED_VALUE_MAPPER, id));
//    }
//
//    // add external ids
//    bookBuilder.addAllExternalIds(jdbcOperations.query(
//        "SELECT external_id_type, external_id FROM book_external_id WHERE book_id=?", EXT_ID_MAPPER, id));
//
//
//    final Set<Long> externalIdTypeIds = new HashSet<>(pageIds.getExternalBookTypeIdsList());
  }
}
