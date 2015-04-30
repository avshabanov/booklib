package com.truward.extid.server.service;

import com.truward.extid.model.ExtId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class GroupsService {

  public interface Contract {

    @Nonnull
    List<ExtId.Group> getGroups();

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

  private GroupsService() {}

  private static final class GroupRowMapper implements RowMapper<ExtId.Group> {

    @Override
    public ExtId.Group mapRow(ResultSet rs, int i) throws SQLException {
      return ExtId.Group.newBuilder().setId(rs.getInt("id")).setCode(rs.getString("name")).build();
    }
  }

  private static final GroupRowMapper GROUP_ROW_MAPPER = new GroupRowMapper();
}
