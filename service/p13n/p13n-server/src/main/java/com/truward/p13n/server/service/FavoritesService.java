package com.truward.p13n.server.service;

import com.truward.p13n.model.P13n;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public final class FavoritesService {

  public interface Contract {
    void setFavorites(@Nonnull List<P13n.SetFavoritesRequest.Entry> entries);
  }

  public static final class Impl implements Contract {
    private final JdbcOperations db;

    public Impl(@Nonnull JdbcOperations db) {
      Assert.notNull(db, "db");
      this.db = db;
    }

    @Override
    public void setFavorites(@Nonnull List<P13n.SetFavoritesRequest.Entry> entries) {
      // TODO: implement
    }
  }

  //
  // Private
  //

  private FavoritesService() {}
}
