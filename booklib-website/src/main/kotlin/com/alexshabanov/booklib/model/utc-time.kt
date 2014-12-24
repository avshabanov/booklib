package com.alexshabanov.booklib.model

import java.util.TimeZone
import java.util.Calendar
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.Date


val UTC_TIMEZONE = TimeZone.getTimeZone("UTC")
private val UTC_CALENDAR = Calendar.getInstance(UTC_TIMEZONE)

data class UtcTime(val time: Long) {
  override fun toString(): String {
    val dateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    dateTime.setCalendar(UTC_CALENDAR)
    return dateTime.format(Date(time))
  }
}

fun currentUtcTime() = UtcTime(System.currentTimeMillis())

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
  val dateTime = SimpleDateFormat("yyyy-MM-dd")
  dateTime.setCalendar(UTC_CALENDAR)
  return UtcTime(time = dateTime.parse(isoDate).getTime())
}
