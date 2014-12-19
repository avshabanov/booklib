package com.alexshabanov.booklib.model

import java.util.TimeZone
import java.util.Calendar
import java.sql.ResultSet

data class UtcTime(val time: Long)

val UTC_TIMEZONE = TimeZone.getTimeZone("UTC")
private val UTC_CALENDAR = Calendar.getInstance(UTC_TIMEZONE)

fun asUtcTime(rs: ResultSet, columnName: String): UtcTime? {
  val utcTimestamp = rs.getTimestamp(columnName, UTC_CALENDAR)
  if (utcTimestamp == null) {
    return null
  }
  return UtcTime(time = utcTimestamp.getTime())
}
