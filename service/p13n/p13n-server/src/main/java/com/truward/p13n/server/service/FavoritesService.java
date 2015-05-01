package com.truward.p13n.server.service;

import com.truward.p13n.model.P13n;
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
public final class FavoritesService {

  public interface Contract {
    void setFavorites(long userId, @Nonnull List<P13n.SetFavoritesRequest.Entry> entries);

    @Nonnull List<P13n.ExtId> getFavorites(long userId, @Nonnull List<P13n.ExtId> ids);

    @Nonnull List<P13n.ExtId> getAllFavorites(long userId, @Nonnull List<Integer> typeIds);
  }

  @Transactional
  public static final class Impl implements Contract {
    private final JdbcOperations db;

    public Impl(@Nonnull JdbcOperations db) {
      Assert.notNull(db, "db");
      this.db = db;
    }

    @Override
    public void setFavorites(final long userId, @Nonnull List<P13n.SetFavoritesRequest.Entry> entries) {
      final List<P13n.ExtId> setFavIds = new ArrayList<>();
      final List<P13n.ExtId> resetFavIds = new ArrayList<>();

      for (final P13n.SetFavoritesRequest.Entry e : entries) {
        if (e.getFavorite()) {
          setFavIds.add(e.getExtId());
        } else {
          resetFavIds.add(e.getExtId());
        }
      }

      if (!setFavIds.isEmpty()) {
        db.batchUpdate("MERGE INTO favorites (ext_id, ext_type_id, user_id) VALUES (?, ?, ?)",
            new BatchPreparedStatementSetter() {
              @Override
              public void setValues(PreparedStatement ps, int i) throws SQLException {
                final P13n.ExtId id = setFavIds.get(i);
                ps.setLong(1, id.getId());
                ps.setInt(2, id.getType());
                ps.setLong(3, userId);
              }

              @Override
              public int getBatchSize() {
                return setFavIds.size();
              }
            });
      }

      if (!resetFavIds.isEmpty()) {
        db.batchUpdate("DELETE FROM favorites WHERE ext_id=? AND ext_type_id=? AND user_id=?",
            new BatchPreparedStatementSetter() {
              @Override
              public void setValues(PreparedStatement ps, int i) throws SQLException {
                final P13n.ExtId id = resetFavIds.get(i);
                ps.setLong(1, id.getId());
                ps.setInt(2, id.getType());
                ps.setLong(3, userId);
              }

              @Override
              public int getBatchSize() {
                return resetFavIds.size();
              }
            });
      }
    }

    @Nonnull
    @Override
    public List<P13n.ExtId> getFavorites(long userId, @Nonnull List<P13n.ExtId> ids) {
      if (ids.isEmpty()) {
        return Collections.emptyList();
      }

      final StringBuilder sql = new StringBuilder(100 + 8 * ids.size());
      final List<Object> args = new ArrayList<>(ids.size() * 2 + 1);
      sql.append("SELECT ext_id, ext_type_id FROM favorites WHERE user_id=? AND (ext_id, ext_type_id) IN (");
      args.add(userId);
      for (int i = 0; i < ids.size(); ++i) {
        if (i > 0) {
          sql.append(", ");
        }
        sql.append("(?, ?)");
        final P13n.ExtId extId = ids.get(i);
        args.add(extId.getId());
        args.add(extId.getType());
      }
      sql.append(')');

      return db.query(sql.toString(), EXT_ID_MAPPER, args.toArray(new Object[args.size()]));
    }

    @Nonnull
    @Override
    public List<P13n.ExtId> getAllFavorites(long userId, @Nonnull List<Integer> typeIds) {
      if (typeIds.isEmpty()) {
        return Collections.emptyList();
      }

      final StringBuilder sql = new StringBuilder(100);
      final List<Object> args = new ArrayList<>(1 + typeIds.size());
      sql.append("SELECT ext_id, ext_type_id FROM favorites WHERE user_id=? AND (ext_type_id) IN (");
      args.add(userId);
      for (int i = 0; i < typeIds.size(); ++i) {
        if (i > 0) {
          sql.append(", ");
        }
        sql.append("?");
        args.add(typeIds.get(i));
      }
      sql.append(')');

      return db.query(sql.toString(), EXT_ID_MAPPER, args.toArray(new Object[args.size()]));
    }
  }

  //
  // Private
  //

  private FavoritesService() {}

  private static final class ExtIdMapper implements RowMapper<P13n.ExtId> {
    @Override
    public P13n.ExtId mapRow(ResultSet rs, int rowNum) throws SQLException {
      return P13n.ExtId.newBuilder().setType(rs.getInt("ext_type_id")).setId(rs.getLong("ext_id")).build();
    }
  }

  private static final ExtIdMapper EXT_ID_MAPPER = new ExtIdMapper();
}
