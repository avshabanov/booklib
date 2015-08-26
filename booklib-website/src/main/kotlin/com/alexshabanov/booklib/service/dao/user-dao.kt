package com.alexshabanov.booklib.service.dao

import org.springframework.jdbc.core.JdbcOperations
import com.alexshabanov.booklib.util.queryForLong
import com.truward.time.jdbc.UtcTimeSqlUtil
import org.springframework.jdbc.core.RowMapper
import com.alexshabanov.booklib.util.queryForInt
import org.springframework.security.core.authority.SimpleGrantedAuthority
import com.alexshabanov.booklib.model.FavoriteEntry
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Propagation
import com.alexshabanov.booklib.model.FavoriteKind
import java.sql.ResultSet


interface UserAccountDao {

  //
  // Favorites
  //

  fun isFavorite(userId: Long, kind: FavoriteKind, entityId: Long): Boolean

  fun setFavorite(userId: Long, kind: FavoriteKind, entityId: Long)

  fun resetFavorite(userId: Long, kind: FavoriteKind, entityId: Long)

  fun getFavorites(userId: Long): List<FavoriteEntry>
}

//
// User Account Dao Impl
//

private val FAV_KIND_AUTHOR_CODE = 200
private val FAV_KIND_BOOK_CODE = 201

private fun toFavoriteKind(v: Int) = when (v) {
  FAV_KIND_AUTHOR_CODE -> FavoriteKind.AUTHOR
  FAV_KIND_BOOK_CODE -> FavoriteKind.BOOK
  else -> throw IllegalStateException("Unknown favorite kind code=${v}")
}

private fun toInt(v: FavoriteKind) = when (v) {
  FavoriteKind.AUTHOR -> FAV_KIND_AUTHOR_CODE
  FavoriteKind.BOOK -> FAV_KIND_BOOK_CODE
  else -> throw IllegalStateException("Unknown favorite kind value=${v}")
}

private val FAVORITE_ENTRY_ROW_MAPPER = RowMapper() { rs: ResultSet, i: Int ->
  FavoriteEntry(kind = toFavoriteKind(rs.getInt("entity_kind")), entityId = rs.getLong("entity_id"))
}

Transactional(value = "userTxManager", propagation = Propagation.MANDATORY)
class UserAccountDaoImpl(val db: JdbcOperations):
    UserAccountDao {

  override fun isFavorite(userId: Long, kind: FavoriteKind, entityId: Long) = queryForInt(db,
      "SELECT COUNT(0) FROM favorite WHERE user_id=? AND entity_kind=? AND entity_id=?",
      userId, toInt(kind), entityId) > 0

  override fun setFavorite(userId: Long, kind: FavoriteKind, entityId: Long) {
    db.update("INSERT INTO favorite (user_id, entity_kind, entity_id) VALUES (?, ?, ?)", userId, toInt(kind), entityId)
  }

  override fun resetFavorite(userId: Long, kind: FavoriteKind, entityId: Long) {
    db.update("DELETE FROM favorite WHERE user_id=? AND entity_kind=? AND entity_id=?", userId, toInt(kind), entityId)
  }

  override fun getFavorites(userId: Long) = db.query("SELECT entity_id, entity_kind FROM favorite WHERE user_id=?",
      FAVORITE_ENTRY_ROW_MAPPER, userId)
}
