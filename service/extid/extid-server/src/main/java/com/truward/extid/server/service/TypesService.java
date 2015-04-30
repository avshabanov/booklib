package com.truward.extid.server.service;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.truward.extid.model.ExtId;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

/**
 * @author Alexander Shabanov
 */
public final class TypesService {

  public interface Contract {
    @Nonnull
    List<ExtId.Type> getTypes();

    void saveType(@Nonnull ExtId.Type type);
  }

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
  }

  //
  // Private
  //

  private TypesService() {}

  private static final class TypeRowMapper implements RowMapper<ExtId.Type> {

    @Override
    public ExtId.Type mapRow(ResultSet rs, int i) throws SQLException {
      return ExtId.Type.newBuilder().setId(rs.getInt("id")).setName(rs.getString("name")).build();
    }
  }

  private static final TypeRowMapper TYPE_ROW_MAPPER = new TypeRowMapper();
}
