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

RunWith(javaClass<SpringJUnit4ClassRunner>())
ContextConfiguration(locations = array("/spring/UserServiceTest-context.xml"))
Transactional(value = "userTxManager") class UserServiceTest {
  // HERE: mocks to silence compiler
  Resource(name = "svc.sec.userProfileService") var userService: UserProfileService = mock(javaClass())

  Test fun shouldRegisterUsers() {
    DefaultUserInitializer(userService).registerTestUsersIfNoOneAlreadyRegistered()
    assertTrue(userService.hasAccounts())

    val admin = userService.loadUserByUsername("admin")
    assertEquals("admin", admin.getUsername())
  }
}
