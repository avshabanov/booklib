package com.truward.booklib.extid.server.service;

import com.truward.booklib.extid.model.ExtId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Alexander Shabanov
 */
public final class ExtIdService {
  private ExtIdService() {}

  public interface Contract {
    @Nonnull List<ExtId.Type> getTypes();

    void saveType(@Nonnull ExtId.Type type);

    @Nonnull List<ExtId.Id> queryByInternalIds(@Nonnull ExtId.QueryByInternalIds request);

    void saveIds(@Nonnull List<ExtId.Id> ids);

    void deleteByIntIds(@Nonnull List<Long> idsList);

    void deleteByTypeIds(@Nonnull List<Integer> idsList);

    @Nonnull List<ExtId.Group> getGroups();

    @Nonnull List<ExtId.Group> queryGroups(@Nonnull List<Integer> idsList);

    @Nonnull List<Integer> saveGroups(@Nonnull List<ExtId.Group> groups);

    void deleteGroups(@Nonnull List<Integer> groupIds);
  }

  @Transactional
  public static final class Impl implements Contract {
    private final JdbcOperations jdbc;

    public Impl(@Nonnull JdbcOperations jdbc) {
      Assert.notNull(jdbc, "jdbc operations parameter can't be null");
      this.jdbc = jdbc;
    }

    @Nonnull
    @Override
    public List<ExtId.Type> getTypes() {
      return jdbc.query("SELECT id, name FROM int_type ORDER BY id", TYPE_ROW_MAPPER);
    }

    @Override
    public void saveType(@Nonnull ExtId.Type type) {
      if (type.hasId()) {
        jdbc.update("UPDATE int_type SET name=? WHERE id=?", type.getName(), type.getId());
      } else {
        jdbc.update("INSERT INTO int_type (id, name) VALUES ((SELECT seq_type.nextval), ?)", type.getName());
      }
    }

    @Override
    @Nonnull
    public List<ExtId.Id> queryByInternalIds(@Nonnull ExtId.QueryByInternalIds request) {
      if (request.getIntIdsCount() == 0 || request.getTypeIdsCount() == 0 ||
          (!request.getIncludeAllGroupIds() && request.getGroupIdsCount() == 0)) {
        return Collections.emptyList();
      }

      final StringBuilder sql = new StringBuilder(200);
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

      return jdbc.query(sql.toString(), EXT_ID_MAPPER, args.toArray(new Object[args.size()]));
    }

    @Override
    public void saveIds(@Nonnull final List<ExtId.Id> ids) {
      if (ids.isEmpty()) {
        return;
      }

      jdbc.batchUpdate("INSERT INTO ext_id (int_id, int_type_id, ext_group_id, ext_id) VALUES (?, ?, ?, ?)", new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          final ExtId.Id id = ids.get(i);
          ps.setLong(1, id.getIntId());
          ps.setInt(2, id.getTypeId());
          ps.setInt(3, id.getGroupId());
          ps.setString(4, id.getExtId());
        }

        @Override
        public int getBatchSize() {
          return ids.size();
        }
      });
    }

    @Override
    public void deleteByIntIds(@Nonnull final List<Long> idsList) {
      if (idsList.isEmpty()) {
        return;
      }

      jdbc.batchUpdate("DELETE FROM ext_id WHERE int_id=?", new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          ps.setLong(1, idsList.get(i));
        }

        @Override
        public int getBatchSize() {
          return idsList.size();
        }
      });
    }

    @Override
    public void deleteByTypeIds(@Nonnull final List<Integer> idsList) {
      if (idsList.isEmpty()) {
        return;
      }

      jdbc.batchUpdate("DELETE FROM ext_id WHERE int_type_id=?", new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          ps.setInt(1, idsList.get(i));
        }

        @Override
        public int getBatchSize() {
          return idsList.size();
        }
      });
    }

    @Nonnull
    @Override
    public List<ExtId.Group> getGroups() {
      return jdbc.query("SELECT id, name FROM ext_group ORDER BY id", GROUP_ROW_MAPPER);
    }

    @Nonnull
    @Override
    public List<ExtId.Group> queryGroups(@Nonnull List<Integer> idsList) {
      if (idsList.isEmpty()) {
        return Collections.emptyList();
      }

      final StringBuilder sql = new StringBuilder(200);
      final List<Object> args = new ArrayList<>();

      sql.append("SELECT id, name FROM ext_group WHERE id IN (");
      for (final Integer id : idsList) {
        sql.append(sql.charAt(sql.length() - 1) == '?' ? ", ?" : "?");
        args.add(id);
      }
      sql.append(") ORDER BY id");

      return jdbc.query(sql.toString(), GROUP_ROW_MAPPER, args.toArray(new Object[args.size()]));
    }

    @Nonnull
    @Override
    public List<Integer> saveGroups(@Nonnull List<ExtId.Group> groups) {
      if (groups.isEmpty()) {
        return Collections.emptyList();
      }

      // populate list of ids for groups to be inserted/updated
      final List<ExtId.Group> inserted = new ArrayList<>();
      final List<ExtId.Group> updated = new ArrayList<>();
      final List<Integer> returnIds = new ArrayList<>(groups.size());
      for (final ExtId.Group group : groups) {
        if (group.hasId()) {
          updated.add(group);
          returnIds.add(group.getId());
        } else {
          inserted.add(group);
          returnIds.add(null);
        }
      }

      // insert groups
      if (!inserted.isEmpty()) {
        final List<Integer> newIds = jdbc.queryForList("SELECT seq_group.nextval FROM system_range(1, ?)",
            Integer.class, inserted.size());

        // update returnIds
        for (int i = 0, next = 0; i < returnIds.size(); ++i) {
          if (returnIds.get(i) == null) {
            returnIds.set(i, newIds.get(next));
            ++next;
          }
        }

        jdbc.batchUpdate("INSERT INTO ext_group (id, name) VALUES (?, ?)", new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setInt(1, newIds.get(i));
            ps.setString(2, inserted.get(i).getCode());
          }

          @Override
          public int getBatchSize() {
            return inserted.size();
          }
        });
      }

      // update groups
      if (!updated.isEmpty()) {
        jdbc.batchUpdate("UPDATE ext_group SET name=? WHERE id=?", new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            final ExtId.Group group = updated.get(i);
            ps.setString(1, group.getCode());
            ps.setInt(1, group.getId());
          }

          @Override
          public int getBatchSize() {
            return updated.size();
          }
        });
      }

      return returnIds; // return IDs of entities
    }

    @Override
    public void deleteGroups(@Nonnull final List<Integer> groupIds) {
      if (groupIds.isEmpty()) {
        return;
      }

      jdbc.batchUpdate("DELETE FROM ext_group WHERE id=?", new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          ps.setLong(1, groupIds.get(i));
        }

        @Override
        public int getBatchSize() {
          return groupIds.size();
        }
      });
    }
  }

  //
  // Private
  //

  private static final class TypeRowMapper implements RowMapper<ExtId.Type> {

    @Override
    public ExtId.Type mapRow(ResultSet rs, int i) throws SQLException {
      return ExtId.Type.newBuilder().setId(rs.getInt("id")).setName(rs.getString("name")).build();
    }
  }

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

  private static final TypeRowMapper TYPE_ROW_MAPPER = new TypeRowMapper();
  private static final GroupRowMapper GROUP_ROW_MAPPER = new GroupRowMapper();
  private static final ExtIdRowMapper EXT_ID_MAPPER = new ExtIdRowMapper();
}
