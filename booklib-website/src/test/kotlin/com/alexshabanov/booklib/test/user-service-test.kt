package com.alexshabanov.booklib.test

import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import javax.annotation.Resource
import org.mockito.Mockito.mock
import com.alexshabanov.booklib.service.UserProfileService
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.fail
import com.alexshabanov.booklib.model.FavoriteEntry
import com.alexshabanov.booklib.model.FavoriteKind
import java.io.Serializable

RunWith(SpringJUnit4ClassRunner::class)
ContextConfiguration(locations = arrayOf("/spring/UserServiceTest.xml"))
Transactional(value = "userTxManager") class UserServiceTest: Serializable {
  // HERE: mocks to silence compiler
  Resource(name = "svc.sec.userProfileService") var userService: UserProfileService = mock(javaClass())

  //
  // Favorites test
  //

  Test fun shouldCreateGetAndUpdateFavorite() {
    val userId = 12L

    assertTrue(userService.getFavorites(userId).isEmpty(), "favs should be empty")

    val kind = FavoriteKind.AUTHOR
    userService.setFavorite(userId, kind, 1000L)
    assertEquals(listOf(FavoriteEntry(kind, 1000L)), userService.getFavorites(userId))

    userService.resetFavorite(userId, kind, 1000L)
    assertTrue(userService.getFavorites(userId).isEmpty(), "favs should be empty after resetting fav")
  }
}
