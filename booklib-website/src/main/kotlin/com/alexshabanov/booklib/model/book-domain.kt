package com.alexshabanov.booklib.model

import java.util.Date
import com.truward.time.UtcTime

/** Book meta information model */
data class BookMeta(val id: Long?, val title: String, val fileSize: Int, val addDate: UtcTime,
                    val lang: NamedValue, val origin: String)

data class NamedValue(val id: Long?, val name: String)

enum class FavoriteStatus {
  NONE
  FAVORITE
  UNDECIDED
}

fun toFavoriteStatus(isFavorite: Boolean) = if (isFavorite) FavoriteStatus.FAVORITE else FavoriteStatus.NONE
