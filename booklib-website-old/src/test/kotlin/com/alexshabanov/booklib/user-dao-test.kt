package com.alexshabanov.booklib

import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.junit.Test
import javax.annotation.Resource
import org.mockito.Mockito.mock
import com.alexshabanov.booklib.model.UserProfileData
import kotlin.test.assertEquals
import com.alexshabanov.booklib.service.dao.UserAccountDao
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertTrue
import kotlin.test.assertFalse

RunWith(javaClass<SpringJUnit4ClassRunner>())
ContextConfiguration(locations = array("/spring/DaoTest-context.xml"))
Transactional(value = "userTxManager") class UserDaoTest {
  Resource(name = "dao.user.userAccountDao") var userDao: UserAccountDao = mock(javaClass())

  Test fun shouldRegisterUser() {
    // Given:
    val profile = UserProfileData(nickname = "test", email = "test@localhost", passwordHash = "hash")

    // When:
    val id = userDao.saveProfile(profile)
    val account1 = userDao.getUserAccountByName(profile.nickname)
    val account2 = userDao.getUserAccountByName(profile.email)

    // Then:
    assertTrue(userDao.hasAccounts())

    val newProfile = userDao.getProfileById(id)
    val expectedProfile = profile.copy(id = id)
    assertEquals(expectedProfile, newProfile)

    assertEquals(profile.nickname, account1.nickname)
    assertEquals(account1, account2)

    assertTrue(userDao.isUserExist(profile.nickname, profile.email))
    assertTrue(userDao.isUserExist(profile.nickname, profile.email + "nonexistent"))
    assertTrue(userDao.isUserExist(profile.nickname + "nonexistent", profile.email))
  }

  Test fun shouldNotFindNonExistentUser() {
    assertFalse(userDao.isUserExist("test", "test@localhost"))
  }

  Test fun shouldDeleteUser() {
    // Given:
    val profile = UserProfileData(nickname = "test", email = "test@localhost", passwordHash = "hash")

    // When:
    val id = userDao.saveProfile(profile)
    userDao.deleteProfile(id)

    // Then:
    assertFalse(userDao.isUserExist(profile.nickname, profile.email))
    assertFalse(userDao.isUserExist(profile.nickname, profile.email + "nonexistent"))
    assertFalse(userDao.isUserExist(profile.nickname + "nonexistent", profile.email))
  }

  Test fun shouldDeleteNonExistentUser() {
    userDao.deleteProfile(1234567890L);
  }

  Test fun shouldNotHaveAccounts() {
    assertFalse(userDao.hasAccounts())
  }
}