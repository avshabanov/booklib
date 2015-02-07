package com.alexshabanov.booklib.service.dao

import org.springframework.jdbc.core.JdbcOperations
import com.alexshabanov.booklib.util.queryForLong
import com.alexshabanov.booklib.model.UserProfileData
import com.truward.time.jdbc.UtcTimeSqlUtil
import com.alexshabanov.booklib.model.UserAccount
import org.springframework.jdbc.core.RowMapper
import com.alexshabanov.booklib.util.queryForInt
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Propagation
import org.springframework.security.core.authority.SimpleGrantedAuthority


trait UserAccountDao {

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
}

//
// User Account Dao Impl
//

val ROLE_NAME_SQL = "SELECT r.role_name FROM role AS r INNER JOIN user_role AS ur ON r.id=ur.role_id WHERE ur.user_id=?"

Transactional(value = "userTxManager", propagation = Propagation.MANDATORY)
class UserAccountDaoImpl(val db: JdbcOperations):
    UserAccountDao {

  override fun getUserAccountByName(name: String): UserAccount {
    // fetch profile
    val account = db.queryForObject(
        "SELECT id, nickname, password_hash FROM user_profile WHERE nickname=? OR email=?",
        org.springframework.jdbc.core.RowMapper {(rs: java.sql.ResultSet, i: Int) ->
          UserAccount(id = rs.getLong("id"), nickname = rs.getString("nickname"),
              passwordHash = rs.getString("password_hash"))
        }, name, name)

    // fetch roles
    account.authorityList = db.query(
        ROLE_NAME_SQL,
        org.springframework.jdbc.core.RowMapper {(rs: java.sql.ResultSet, i: Int) ->
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
}
