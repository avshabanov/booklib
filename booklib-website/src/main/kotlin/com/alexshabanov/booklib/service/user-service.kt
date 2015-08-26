package com.alexshabanov.booklib.service

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.dao.EmptyResultDataAccessException
import javax.annotation.PostConstruct
import com.truward.time.UtcTime
import com.alexshabanov.booklib.service.dao.UserAccountDao
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Isolation
import com.alexshabanov.booklib.model.FavoriteEntry
import com.alexshabanov.booklib.model.FavoriteKind

//
// Interface
//


interface UserProfileService {

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
class UserProfileServiceImpl(val userDao: UserAccountDao): UserProfileService {

  override fun isFavorite(userId: Long, kind: FavoriteKind, entityId: Long) =
      userDao.isFavorite(userId, kind, entityId)

  override fun setFavorite(userId: Long, kind: FavoriteKind, entityId: Long) =
      userDao.setFavorite(userId, kind, entityId)

  override fun resetFavorite(userId: Long, kind: FavoriteKind, entityId: Long) =
      userDao.resetFavorite(userId, kind, entityId)

  override fun getFavorites(userId: Long) = userDao.getFavorites(userId)
}
