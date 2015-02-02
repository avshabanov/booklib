package com.alexshabanov.booklib.model

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.GrantedAuthority
import com.truward.time.UtcTime

/** User account details for spring framework */
data class UserAccount(val id: Long,
                       val userName: String,
                       val passwordHash: String,
                       var authorityList: Collection<SimpleGrantedAuthority> = listOf()): UserDetails {
  override fun getAuthorities(): Collection<GrantedAuthority> = authorityList

  override fun getPassword(): String = passwordHash

  override fun getUsername(): String? = userName

  override fun isAccountNonExpired(): Boolean = true

  override fun isAccountNonLocked(): Boolean = true

  override fun isCredentialsNonExpired(): Boolean = true

  override fun isEnabled(): Boolean = true

  override fun toString(): String = "User{id=${id}, username=${userName}, roles=${authorityList}}"
}

/** User profile information. */
data class UserProfileData(val id: Long? = null,
                           val userName: String,
                           val passwordHash: String,
                           val authorityList: List<String> = listOf("ROLE_USER"),
                           val created: UtcTime = UtcTime.now())
