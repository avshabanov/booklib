package com.alexshabanov.booklib.service

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.dao.EmptyResultDataAccessException
import javax.annotation.PostConstruct
import com.alexshabanov.booklib.model.UserProfileData
import org.springframework.security.authentication.dao.SaltSource
import org.springframework.security.crypto.password.PasswordEncoder

class UserService(val userDao: UserAccountDao, val saltSource: SaltSource, val passwordEncoder: PasswordEncoder): UserDetailsService {

  PostConstruct fun registerTestUserIfNotExist() {
    val username = "test"
    val password = "test"

    try {
      userDao.getUserAccountByName(username)
      return
    } catch (ignored: EmptyResultDataAccessException) {
    }

    // ok, so the user doesn't exist
    // TODO: use salt source to encode password
    userDao.saveProfile(UserProfileData(userName = username, passwordHash = password))
  }

  // overridden method from UserDetailsService - used in login procedure
  override fun loadUserByUsername(username: String): UserDetails {
    try {
      return userDao.getUserAccountByName(username)
    } catch (e: EmptyResultDataAccessException) {
      throw UsernameNotFoundException("No user with username=${username}", e)
    }
  }
}