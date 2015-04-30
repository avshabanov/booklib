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
public final class IdPairsService {
  public interface Contract {
    @Nonnull
    List<ExtId.IdPair> queryIdPairs(@Nonnull ExtId.QueryIdPairs request);

    void saveIdPairs(@Nonnull List<ExtId.IdPair> ids);

    void deleteIdPairsByIntIds(@Nonnull List<ExtId.IntId> idsList);

    void deleteIdPairsByTypeIds(@Nonnull List<Integer> idsList);

    void deleteIdPairsByGroupIds(@Nonnull List<Integer> idsList);
  }

  @Transactional
  public static final class Impl implements Contract {
    private final JdbcOperations jdbc;

    public Impl(@Nonnull JdbcOperations jdbc) {
      Assert.notNull(jdbc, "jdbc operations parameter can't be null");
      this.jdbc = jdbc;
    }

    @Override
    @Nonnull
    public List<ExtId.IdPair> queryIdPairs(@Nonnull ExtId.QueryIdPairs request) {
      if (request.getIntIdsCount() == 0 || (!request.getIncludeAllGroupIds() && request.getGroupIdsCount() == 0)) {
        return Collections.emptyList();
      }

      final StringBuilder sql = new StringBuilder(200);
      final List<Object> args = new ArrayList<>();

      sql.append("SELECT int_id, int_type_id, ext_group_id, ext_id FROM ext_id\nWHERE");

      // int ids
      sql.append(" (int_id, int_type_id) IN (");
      for (int i = 0; i < request.getIntIdsCount(); ++i) {
        sql.append(i > 0 ? ", (?, ?)" : "(?, ?)");
        final ExtId.IntId intId = request.getIntIds(i);
        args.add(intId.getId());
        args.add(intId.getTypeId());
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

      return jdbc.query(sql.toString(), ID_PAIR_MAPPER, args.toArray(new Object[args.size()]));
    }

    @Override
    public void saveIdPairs(@Nonnull final List<ExtId.IdPair> ids) {
      if (ids.isEmpty()) {
        return;
      }

      jdbc.batchUpdate("INSERT INTO ext_id (int_id, int_type_id, ext_group_id, ext_id) VALUES (?, ?, ?, ?)", new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          final ExtId.IdPair id = ids.get(i);
          final ExtId.IntId intId = id.getIntId();
          ps.setLong(1, intId.getId());
          ps.setInt(2, intId.getTypeId());
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
    public void deleteIdPairsByIntIds(@Nonnull final List<ExtId.IntId> idsList) {
      if (idsList.isEmpty()) {
        return;
      }

      jdbc.batchUpdate("DELETE FROM ext_id WHERE int_id=? AND int_type_id=?", new BatchPreparedStatementSetter() {
        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
          final ExtId.IntId intId = idsList.get(i);
          ps.setLong(1, intId.getId());
          ps.setLong(2, intId.getTypeId());
        }

        @Override
        public int getBatchSize() {
          return idsList.size();
        }
      });
    }

    @Override
    public void deleteIdPairsByTypeIds(@Nonnull final List<Integer> idsList) {
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

    @Override
    public void deleteIdPairsByGroupIds(@Nonnull final List<Integer> idsList) {
      if (idsList.isEmpty()) {
        return;
      }

      jdbc.batchUpdate("DELETE FROM ext_id WHERE ext_group_id=?", new BatchPreparedStatementSetter() {
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
  }

  //
  // Private
  //

  private static final class IdPairRowMapper implements RowMapper<ExtId.IdPair> {

    @Override
    public ExtId.IdPair mapRow(ResultSet rs, int i) throws SQLException {
      return ExtId.IdPair.newBuilder()
          .setIntId(ExtId.IntId.newBuilder().setId(rs.getLong("int_id")).setTypeId(rs.getInt("int_type_id")).build())
          .setExtId(rs.getString("ext_id"))
          .setGroupId(rs.getInt("ext_group_id"))
          .build();
    }
  }

  private static final IdPairRowMapper ID_PAIR_MAPPER = new IdPairRowMapper();
}
