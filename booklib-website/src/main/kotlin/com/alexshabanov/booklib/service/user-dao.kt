package com.alexshabanov.booklib.service

import com.alexshabanov.booklib.model.UserAccount
import com.alexshabanov.booklib.model.UserProfileData
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.jdbc.core.JdbcOperations
import com.truward.time.UtcTime
import com.truward.time.jdbc.UtcTimeSqlUtil


trait UserAccountDao {

  /**
   * Method, that fetches profile information, needed for Spring Security
   *
   * @param name User login name or email
   * @return User Account
   * @throws EmptyResultDataAccessException on error
   */
  fun getUserAccountByName(name: String): UserAccount

  fun getProfileById(id: Long): UserProfileData

  fun saveProfile(profile: UserProfileData): Long
}

//
// User Account Dao Impl
//

val ROLE_NAME_SQL = "SELECT r.role_name FROM role AS r INNER JOIN user_role AS ur ON r.id=ur.role_id WHERE ur.user_id=?"

class UserAccountDaoImpl(val db: JdbcOperations): UserAccountDao {

  override fun getUserAccountByName(name: String): UserAccount {
    // fetch profile
    val account = db.queryForObject(
        "SELECT id, login, password_hash FROM user_account WHERE login_name=? OR email=?",
        RowMapper {(rs: ResultSet, i: Int) ->
          UserAccount(id = rs.getLong("id"), userName = rs.getString("login"),
              passwordHash = rs.getString("password_hash"))
        }, name)

    // fetch roles
    account.authorityList = db.query(
        ROLE_NAME_SQL,
        RowMapper {(rs: ResultSet, i: Int) ->
          SimpleGrantedAuthority(rs.getString("role_name"))
        }, account.id)

    return account
  }

  override fun getProfileById(id: Long): UserProfileData {
    val authorityList = db.queryForList(ROLE_NAME_SQL, javaClass<String>(), id)
    return db.queryForObject(
        "SELECT id, login, password_hash, created FROM user_account WHERE id=?",
        RowMapper() {(rs: ResultSet, i: Int) ->
          UserProfileData(id = rs.getInt("id").toLong(), userName = rs.getString("login_name"),
              authorityList = authorityList,
              passwordHash = rs.getString("password_hash"),
              created = UtcTimeSqlUtil.getUtcTime(rs, "created"))
        }, id)
  }

  override fun saveProfile(profile: UserProfileData): Long {
    throw UnsupportedOperationException()
  }
}
