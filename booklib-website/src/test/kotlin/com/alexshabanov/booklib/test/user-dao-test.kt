package com.alexshabanov.booklib.test

import com.alexshabanov.booklib.model.FavoriteKind
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.ContextConfiguration
import org.junit.Test
import javax.annotation.Resource
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import com.alexshabanov.booklib.service.dao.UserAccountDao
import org.springframework.transaction.annotation.Transactional
import java.io.Serializable
import kotlin.test.assertTrue
import kotlin.test.assertFalse

RunWith(SpringJUnit4ClassRunner::class)
ContextConfiguration(locations = arrayOf("/spring/DaoTest.xml"))
Transactional(value = "userTxManager") class UserDaoTest: Serializable {
  Resource(name = "dao.user.userAccountDao") private var userDao: UserAccountDao = mock(javaClass())

  Test fun shouldNotHaveFavorites() {
    assertFalse(userDao.isFavorite(1L, FavoriteKind.BOOK, 10L))
  }
}