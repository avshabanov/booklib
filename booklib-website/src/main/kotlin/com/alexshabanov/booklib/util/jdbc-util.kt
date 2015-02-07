package com.alexshabanov.booklib.util

import org.springframework.jdbc.core.JdbcOperations
import org.springframework.dao.TypeMismatchDataAccessException

/**
 * Utility functions for bypassing 'deprecated' functions in JdbcOperations
 *
 * @author Alexander Shabanov
 */

fun queryForNumber(db: JdbcOperations, sql: String, args: Array<out Any>): Number {
  val n = if (args.isEmpty()) db.queryForObject(sql, javaClass<Number>())
              else db.queryForObject(sql, javaClass<Number>(), *args)
  if (n == null) {
    throw TypeMismatchDataAccessException("Returned number is null")
  }
  return n
}

fun queryForInt(db: JdbcOperations, sql: String, vararg args: Any): Int = queryForNumber(db, sql, args).toInt()

fun queryForLong(db: JdbcOperations, sql: String, vararg args: Any): Long = queryForNumber(db, sql, args).toLong()
