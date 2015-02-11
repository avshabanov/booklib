package com.alexshabanov.booklib.service.dao

import org.springframework.jdbc.core.JdbcOperations
import com.alexshabanov.booklib.util.queryForLong
import com.alexshabanov.booklib.model.UserProfileData
import com.truward.time.jdbc.UtcTimeSqlUtil
import com.alexshabanov.booklib.model.UserAccount
import org.springframework.jdbc.core.RowMapper
import com.alexshabanov.booklib.util.queryForInt
import org.springframework.security.core.authority.SimpleGrantedAuthority
import com.alexshabanov.booklib.model.FavoriteEntry
import com.alexshabanov.booklib.model.InvitationToken
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Propagation


trait UserAccountDao {

  fun getIntConstantByName(constantName: String): Int

  /**
   * Method, that fetches profile information, needed for Spring Security
   *
   * @param name User login name or email
   * @return User Account
   * @throws EmptyResultDataAccessException on error
   */
  fun getUserAccountByName(name: String): UserAccount

  fun isUserExist(nickname: String, email: String): Boolean

  fun getProfileById(id: Long): UserProfileData

  fun saveProfile(profile: UserProfileData): Long

  fun deleteProfile(id: Long)

  fun hasAccounts(): Boolean

  //
  // Invitation Token API
  //

  fun redeemToken(code: String): Int

  fun createToken(code: String, note: String)

  fun getTokens(): List<InvitationToken>

  fun deleteToken(code: String)

  //
  // Favorites
  //

  fun isFavorite(userId: Long, kind: Int, entityId: Long): Boolean

  fun setFavorite(userId: Long, kind: Int, entityId: Long)

  fun resetFavorite(userId: Long, kind: Int, entityId: Long)

  fun getFavorites(userId: Long): List<FavoriteEntry>
}

//
// User Account Dao Impl
//

val AUTHOR_ENTITY_KIND_CONST_NAME = "c_entity_kind_author"
val BOOK_ENTITY_KIND_CONST_NAME = "c_entity_kind_book"

private val ROLE_NAME_SQL = "SELECT r.role_name FROM role AS r INNER JOIN user_role AS ur ON r.id=ur.role_id WHERE ur.user_id=?"

private val INVITATION_TOKEN_ROW_MAPPER = RowMapper() {(rs: java.sql.ResultSet, i: Int) ->
  InvitationToken(code = rs.getString("code"), note = rs.getString("note"))
}

private val FAVORITE_ENTRY_ROW_MAPPER = RowMapper() {(rs: java.sql.ResultSet, i: Int) ->
  FavoriteEntry(kind = rs.getInt("entity_kind"), entityId = rs.getLong("entity_id"))
}

Transactional(value = "userTxManager", propagation = Propagation.MANDATORY)
class UserAccountDaoImpl(val db: JdbcOperations):
    UserAccountDao {

  override fun getIntConstantByName(constantName: String) = queryForInt(db, "SELECT " + constantName)

  override fun getUserAccountByName(name: String): UserAccount {
    // fetch profile
    val account = db.queryForObject(
        "SELECT id, nickname, password_hash FROM user_profile WHERE nickname=? OR email=?",
        RowMapper {(rs: java.sql.ResultSet, i: Int) ->
          UserAccount(id = rs.getLong("id"), nickname = rs.getString("nickname"),
              passwordHash = rs.getString("password_hash"))
        }, name, name)

    // fetch roles
    account.authorityList = db.query(
        ROLE_NAME_SQL,
        RowMapper {(rs: java.sql.ResultSet, i: Int) ->
          SimpleGrantedAuthority(rs.getString("role_name"))
        }, account.id)

    return account
  }

  override fun getProfileById(id: Long): UserProfileData {
    val authorityList = db.queryForList(ROLE_NAME_SQL, javaClass<String>(), id)
    return db.queryForObject(
        "SELECT id, nickname, email, password_hash, created FROM user_profile WHERE id=?",
        RowMapper() {(rs: java.sql.ResultSet, i: Int) ->
          UserProfileData(id = rs.getInt("id").toLong(), nickname = rs.getString("nickname"),
              authorityList = authorityList,
              passwordHash = rs.getString("password_hash"),
              email = rs.getString("email"),
              created = UtcTimeSqlUtil.getUtcTime(rs, "created"))
        }, id)
  }

  override fun saveProfile(profile: UserProfileData): Long {
    // save user itself
    val id = profile.id ?: queryForLong(db, "SELECT seq_user_profile.nextval")
    db.update("INSERT INTO user_profile (id, nickname, email, password_hash, created) VALUES (?, ?, ?, ?, ?)", id,
        profile.nickname, profile.email, profile.passwordHash, profile.created.asCalendar())

    if (profile.id != null) {
      // we need to update roles only for update scenarios
      db.update("DELETE FROM user_role WHERE user_id=?", id)
    }

    // insert user roles
    for (role in profile.authorityList) {
      db.update("INSERT INTO user_role (user_id, role_id) VALUES (?, (SELECT r.id FROM role AS r WHERE r.role_name=?))",
          id, role)
    }

    return id;
  }

  override fun deleteProfile(id: Long) {
    db.update("DELETE FROM user_role WHERE user_id=?", id)
    db.update("DELETE FROM favorite WHERE user_id=?", id)

    db.update("DELETE FROM user_profile WHERE id=?", id)
  }

  override fun isUserExist(nickname: String, email: String): Boolean {
    return queryForInt(db, "SELECT count(id) FROM user_profile WHERE nickname=? OR email=?", nickname, email) > 0
  }

  override fun hasAccounts(): Boolean {
    return queryForInt(db, "SELECT count(0) FROM user_profile") > 0
  }

  override fun redeemToken(code: String): Int {
    val redeemCount = queryForInt(db, "SELECT redeem_count FROM invitation_token WHERE code=? FOR UPDATE", code)
    if (redeemCount > 0) {
      db.update("UPDATE invitation_token SET redeem_count=? WHERE code=?", redeemCount - 1, code)
    }
    return redeemCount
  }

  override fun createToken(code: String, note: String) {
    db.update("INSERT INTO invitation_token (code, note, redeem_count) VALUES (?, ?, 1)", code, note)
  }

  override fun getTokens() = db.query("SELECT code, note FROM invitation_token", INVITATION_TOKEN_ROW_MAPPER)

  override fun deleteToken(code: String) {
    db.update("DELETE FROM invitation_token WHERE code=?", code)
  }

  override fun isFavorite(userId: Long, kind: Int, entityId: Long) = queryForInt(db,
      "SELECT COUNT(0) FROM favorite WHERE user_id=? AND entity_kind=? AND entityId=?", userId, kind, entityId) > 0

  override fun setFavorite(userId: Long, kind: Int, entityId: Long) {
    db.update("INSERT INTO favorite (user_id, entity_kind, entity_id) VALUES (?, ?, ?)", userId, kind, entityId)
  }

  override fun resetFavorite(userId: Long, kind: Int, entityId: Long) {
    db.update("DELETE FROM favorite WHERE user_id=? AND entity_kind=? AND entity_id=?", userId, kind, entityId)
  }

  override fun getFavorites(userId: Long) = db.query("SELECT entity_id, entity_kind FROM favorite WHERE user_id=?",
      FAVORITE_ENTRY_ROW_MAPPER, userId)
}
