package com.alexshabanov.booklib.model

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.GrantedAuthority
import com.truward.time.UtcTime

/** Personalized information. */

enum class FavoriteKind {
  AUTHOR,
  BOOK
}

data class FavoriteEntry(val kind: FavoriteKind, val entityId: Long)
