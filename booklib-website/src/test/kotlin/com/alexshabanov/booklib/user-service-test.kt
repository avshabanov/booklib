package com.alexshabanov.booklib

import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import javax.annotation.Resource
import org.mockito.Mockito.mock
import com.alexshabanov.booklib.service.UserProfileService
import org.junit.Test
import com.alexshabanov.booklib.service.DefaultUserInitializer
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import com.alexshabanov.booklib.service.InvalidInvitationTokenException
import com.alexshabanov.booklib.service.RegistrationData
import kotlin.test.fail
import com.alexshabanov.booklib.model.InvitationToken
import com.alexshabanov.booklib.model.FavoriteEntry

RunWith(javaClass<SpringJUnit4ClassRunner>())
ContextConfiguration(locations = array("/spring/UserServiceTest-context.xml"))
Transactional(value = "userTxManager") class UserServiceTest {
  // HERE: mocks to silence compiler
  Resource(name = "svc.sec.userProfileService") var userService: UserProfileService = mock(javaClass())

  val regData = RegistrationData(nickname = "test", email = "test@localhost", password = "test", invitationCode = "1")

  //
  // User registration and invitation tokens
  //

  Test fun shouldRegisterUsers() {
    DefaultUserInitializer(userService).registerTestUsersIfNoOneAlreadyRegistered()
    assertTrue(userService.hasAccounts())

    val admin = userService.loadUserByUsername("admin")
    assertEquals("admin", admin.getUsername())
  }

  Test(expected = javaClass<InvalidInvitationTokenException>()) fun shouldNotRegisterUserWithWrongInvitationToken() {
    userService.registerUser(regData)
  }

  Test fun shouldRegisterUserWithInvitationToken() {
    // Given:
    val token = "token"

    // When:
    userService.createToken(token, "note")
    userService.registerUser(regData.copy(invitationCode = token))

    // Then:
    val user = userService.loadUserByUsername(regData.nickname)
    assertEquals(regData.nickname, user.getUsername())
  }

  Test fun shouldFailToUseInvitationTokenTwice() {
    // Given:
    val token = "token"

    // When:
    userService.createToken(token, "note")
    userService.registerUser(regData.copy(invitationCode = token))

    // Then:
    try {
      userService.registerUser(regData.copy(nickname = "n2", email = "n2@n", invitationCode = token))
      fail("should not be able to use the same token twice")
    } catch (e: InvalidInvitationTokenException) {
      // ok
    }
  }

  Test fun shouldCreateGetAndDeleteInvitationToken() {
    assertTrue(userService.getTokens().isEmpty())

    val token = "token"
    val note = "note"
    userService.createToken(token, note)
    assertEquals(listOf(InvitationToken(code = token, note = note)), userService.getTokens())

    userService.deleteToken(token)
    assertTrue(userService.getTokens().isEmpty())
  }

  //
  // Favorites test
  //

  Test fun shouldCreateGetAndUpdateFavorite() {
    val token = "token"
    userService.createToken(code = token, note = "note")
    val userId = userService.registerUser(regData.copy(invitationCode = token))

    assertTrue(userService.getFavorites(userId).isEmpty(), "favs should be empty")

    userService.setFavorite(userId, 1, 1000L)
    assertEquals(listOf(FavoriteEntry(1, 1000L)), userService.getFavorites(userId))

    userService.resetFavorite(userId, 1, 1000L)
    assertTrue(userService.getFavorites(userId).isEmpty(), "favs should be empty after resetting fav")
  }
}
