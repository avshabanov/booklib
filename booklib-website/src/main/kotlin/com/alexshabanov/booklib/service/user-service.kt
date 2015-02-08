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

//
// Exceptions
//

class UserAlreadyExistsException: RuntimeException() {}

class InvalidInvitationTokenException: RuntimeException() {}

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

    // ok, so the user doesn't exist, add it
    val passwordHash = passwordEncoder.encode(reg.password)
    if (reg.invitationCode != null) {
      if (reg.invitationCode.isEmpty()) {
        throw InvalidInvitationTokenException()
      }

      // TODO: more invitation token checks...
    }

    return userDao.saveProfile(UserProfileData(nickname = reg.nickname, email = reg.email, passwordHash = passwordHash,
        authorityList = reg.desiredRoles, created = UtcTime.now()))
  }

  override fun hasAccounts() = userDao.hasAccounts()
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

