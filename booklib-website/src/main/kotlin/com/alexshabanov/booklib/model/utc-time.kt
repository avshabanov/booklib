package com.alexshabanov.booklib.model

import java.util.TimeZone
import java.util.Calendar
import java.sql.ResultSet
import java.text.SimpleDateFormat

data class UtcTime(val time: Long)

val UTC_TIMEZONE = TimeZone.getTimeZone("UTC")
private val UTC_CALENDAR = Calendar.getInstance(UTC_TIMEZONE)

fun asNullableUtcTime(rs: ResultSet, columnName: String): UtcTime? {
  val utcTimestamp = rs.getTimestamp(columnName, UTC_CALENDAR)
  if (utcTimestamp == null) {
    return null
  }
  return UtcTime(time = utcTimestamp.getTime())
}

fun asUtcTime(rs: ResultSet, columnName: String, defaultTime: UtcTime? = null): UtcTime {
  val result = asNullableUtcTime(rs, columnName)
  if (result != null) {
    return result;
  }

  if (defaultTime == null) {
    throw IllegalStateException("Unable to retrieve time from resultSet for columnName=${columnName}")
  }

  return defaultTime
}

fun parseUtcDate(isoDate: String): UtcTime {
  val cal = Calendar.getInstance(UTC_TIMEZONE)
  val dateTime = SimpleDateFormat("yyyy-MM-dd")
  dateTime.setCalendar(cal)
  return UtcTime(time = dateTime.parse(isoDate).getTime())
}
