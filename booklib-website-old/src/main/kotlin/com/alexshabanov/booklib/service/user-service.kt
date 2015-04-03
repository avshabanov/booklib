package com.alexshabanov.booklib.service

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.dao.EmptyResultDataAccessException
import javax.annotation.PostConstruct
import com.alexshabanov.booklib.model.UserProfileData
import com.truward.time.UtcTime
import com.alexshabanov.booklib.service.dao.UserAccountDao
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Isolation
import com.alexshabanov.booklib.model.FavoriteEntry
import com.alexshabanov.booklib.model.InvitationToken
import com.alexshabanov.booklib.model.FavoriteKind

//
// Exceptions
//

class UserAlreadyExistsException: RuntimeException() {}

class InvalidInvitationTokenException(msg: String): RuntimeException(msg) {}

//
// Interface
//

val ROLE_USER = "ROLE_USER"
val ROLE_ADMIN = "ROLE_ADMIN"

data class RegistrationData(val nickname: String,
                            val email: String,
                            val password: String,
                            val desiredRoles: List<String> = listOf(ROLE_USER),
                            val invitationCode: String? = null)



trait UserProfileService : UserDetailsService {
  fun registerUser(reg: RegistrationData): Long

  fun hasAccounts(): Boolean

  //
  // Invitation Token API
  //

  fun createToken(code: String, note: String)

  fun getTokens(): List<InvitationToken>

  fun deleteToken(code: String)

  //
  // Favorites
  //

  fun isFavorite(userId: Long, kind: FavoriteKind, entityId: Long): Boolean

  fun setFavorite(userId: Long, kind: FavoriteKind, entityId: Long)

  fun resetFavorite(userId: Long, kind: FavoriteKind, entityId: Long)

  fun getFavorites(userId: Long): List<FavoriteEntry>
}

//
// Implementation
//

Transactional(value = "userTxManager", isolation = Isolation.READ_COMMITTED)
class UserProfileServiceImpl(val userDao: UserAccountDao, val passwordEncoder: PasswordEncoder): UserProfileService {

  // overridden method from UserDetailsService - used in login procedure
  override fun loadUserByUsername(username: String): UserDetails {
    try {
      return userDao.getUserAccountByName(username)
    } catch (e: EmptyResultDataAccessException) {
      throw UsernameNotFoundException("No user with username=${username}", e)
    }
  }

  override fun registerUser(reg: RegistrationData): Long {
    if (userDao.isUserExist(reg.nickname, reg.email)) {
      throw UserAlreadyExistsException()
    }

    if (reg.invitationCode != null) {
      if (reg.invitationCode.isEmpty()) {
        throw InvalidInvitationTokenException("Invitation token is empty")
      }

      // TODO: more invitation token checks...
      try {
        val redeemCount = userDao.redeemToken(reg.invitationCode)
        if (redeemCount == 0) {
          throw InvalidInvitationTokenException("Token already redeemed")
        }
      } catch (ignored: EmptyResultDataAccessException) {
        throw InvalidInvitationTokenException("No such token has been registered")
      }
    }

    // ok, so the user doesn't exist, add it
    val passwordHash = passwordEncoder.encode(reg.password)

    return userDao.saveProfile(UserProfileData(nickname = reg.nickname, email = reg.email, passwordHash = passwordHash,
        authorityList = reg.desiredRoles, created = UtcTime.now()))
  }

  override fun hasAccounts() = userDao.hasAccounts()

  override fun createToken(code: String, note: String) = userDao.createToken(code, note)

  override fun getTokens() = userDao.getTokens()

  override fun deleteToken(code: String) = userDao.deleteToken(code)

  override fun isFavorite(userId: Long, kind: FavoriteKind, entityId: Long) =
      userDao.isFavorite(userId, kind, entityId)

  override fun setFavorite(userId: Long, kind: FavoriteKind, entityId: Long) =
      userDao.setFavorite(userId, kind, entityId)

  override fun resetFavorite(userId: Long, kind: FavoriteKind, entityId: Long) =
      userDao.resetFavorite(userId, kind, entityId)

  override fun getFavorites(userId: Long) = userDao.getFavorites(userId)
}

class DefaultUserInitializer(val userService: UserProfileService) {

  PostConstruct fun registerTestUsersIfNoOneAlreadyRegistered() {
    if (userService.hasAccounts()) {
      return
    }

    // if we don't have any users at all - create admin and test user
    userService.registerUser(RegistrationData(nickname = "admin", email = "admin@localhost", password = "admin",
        desiredRoles = listOf(ROLE_USER, ROLE_ADMIN)))
    userService.registerUser(RegistrationData(nickname = "test", email = "test@localhost", password = "test"))
  }
}

